package net.weesli.rclaim.sync;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.events.ClaimCreateEvent;
import net.weesli.rclaim.api.events.ClaimDeleteEvent;
import net.weesli.rclaim.api.events.ClaimStatusChangeEvent;
import net.weesli.rclaim.api.events.ClaimTrustEvent;
import net.weesli.rclaim.api.events.ClaimUnTrustEvent;
import net.weesli.rclaim.api.model.Claim;
import net.weesli.rclaim.config.ConfigLoader;
import net.weesli.rozslib.color.ColorBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import java.util.UUID;

/**
 * Cross-server claim metadata sync over Redis pub/sub, backed by the shared MySQL claim
 * table (only meaningful when storageType is MySQL - a local SQLite/RozsDBLite file has
 * nothing for another server to read).
 *
 * IMPORTANT scope note: this syncs claim *metadata* (owner, members, statuses, timestamp)
 * so it's visible/manageable network-wide - it does not and cannot make one server enforce
 * protection on another server's world. A claim's "this chunk is protected" marker lives in
 * that chunk's PersistentDataContainer, which is part of that world's own save data; only
 * the server actually hosting that world/chunk can enforce it. That's a Bukkit/Folia data
 * model constraint, not something Redis or a shared database changes.
 *
 * The task description referenced following the same approach as another of this developer's
 * plugins ("RFriend")'s cross-server sync; that codebase was not available in this session to
 * read, so this is a standalone design rather than a port of that pattern.
 *
 * Also note: the pre-existing claim cache (CacheManagerImpl.ClaimCache) is backed by a plain
 * HashMap, not a concurrent map - see CLAUDE.md's own warning about claim-cache race
 * conditions. Fixing that broadly is out of scope here; this class only ever mutates the
 * cache via FoliaLib's schedulers (never from the raw Redis subscriber thread), which keeps
 * this feature from making that pre-existing gap any worse.
 *
 * UNVERIFIED: the redis.clients:jedis:5.1.0 coordinate in build.gradle.kts and the classic
 * JedisPool(JedisPoolConfig, host, port[, timeout, password]) constructors / Jedis.subscribe /
 * Jedis.publish calls used below could not be compiled or checked against a real Jedis jar in
 * this environment (network blocked, same caveat as the Pl3xMap and CoreProtect hooks). Jedis
 * 4.x/5.x moved a lot of its API toward UnifiedJedis/JedisPooled; confirm this exact surface
 * still exists in 5.1.0 before relying on this feature.
 */
public class RedisManager implements Listener {

    private final String serverId = UUID.randomUUID().toString();
    private JedisPool pool;
    private Thread subscriberThread;
    private volatile boolean running;

    public RedisManager() {
        var config = ConfigLoader.getConfig().getRedis();
        if (!config.isEnabled()) return;

        if (!"MySQL".equalsIgnoreCase(ConfigLoader.getConfig().getStorageType())) {
            Bukkit.getConsoleSender().sendMessage(ColorBuilder.convertColors(
                    "&c[RClaim] redis.enabled is true but storageType isn't MySQL - Redis sync needs a shared database, disabling it."));
            return;
        }

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        pool = (config.getPassword() == null || config.getPassword().isEmpty())
                ? new JedisPool(poolConfig, config.getHost(), config.getPort())
                : new JedisPool(poolConfig, config.getHost(), config.getPort(), 2000, config.getPassword());

        startSubscriber(config.getChannel());
        Bukkit.getPluginManager().registerEvents(this, RClaim.getInstance());
    }

    public boolean isActive() {
        return pool != null;
    }

    private void startSubscriber(String channel) {
        running = true;
        JedisPubSub pubSub = new JedisPubSub() {
            @Override
            public void onMessage(String ch, String message) {
                handleMessage(message);
            }
        };

        subscriberThread = new Thread(() -> {
            while (running) {
                try (Jedis jedis = pool.getResource()) {
                    jedis.subscribe(pubSub, channel);
                } catch (Exception e) {
                    if (!running) return;
                    Bukkit.getConsoleSender().sendMessage(ColorBuilder.convertColors(
                            "&c[RClaim] Redis subscription lost, retrying in 5s: " + e.getMessage()));
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException interrupted) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
        }, "RClaim-Redis-Subscriber");
        subscriberThread.setDaemon(true);
        subscriberThread.start();
    }

    private void handleMessage(String message) {
        try {
            JsonObject json = JsonParser.parseString(message).getAsJsonObject();
            if (serverId.equals(json.get("serverId").getAsString())) return; // our own publish, ignore

            RClaim.getInstance().getFoliaLib().getScheduler().runNextTick((task) -> {
                var cache = RClaim.getInstance().getCacheManager().getClaims();
                cache.getCache().clear();
                for (Claim claim : RClaim.getInstance().getStorage().getAllClaims()) {
                    cache.addClaim(claim);
                }
            });
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(ColorBuilder.convertColors(
                    "&c[RClaim] Failed to process Redis sync message: " + e.getMessage()));
        }
    }

    @EventHandler
    public void onCreate(ClaimCreateEvent e) {
        if (e.isCancelled()) return;
        scheduleSync(e.getClaim(), false);
    }

    @EventHandler
    public void onDelete(ClaimDeleteEvent e) {
        if (e.isCancelled()) return;
        scheduleSync(e.getClaim(), true);
    }

    @EventHandler
    public void onTrust(ClaimTrustEvent e) {
        if (e.isCancelled()) return;
        scheduleSync(e.getClaim(), false);
    }

    @EventHandler
    public void onUnTrust(ClaimUnTrustEvent e) {
        if (e.isCancelled()) return;
        scheduleSync(e.getClaim(), false);
    }

    @EventHandler
    public void onStatusChange(ClaimStatusChangeEvent e) {
        scheduleSync(e.getClaim(), false);
    }

    /**
     * Defers to the claim's own region one tick out (same reasoning as MapManagerImpl):
     * these events fire before the triggering manager method finishes applying its change,
     * so reading the claim's fields immediately would see stale data.
     */
    private void scheduleSync(Claim claim, boolean deleted) {
        if (pool == null) return;
        World world = Bukkit.getWorld(claim.getWorldName());
        if (world == null) return;

        RClaim.getInstance().getFoliaLib().getScheduler().runAtLocation(
                new Location(world, claim.getX(), 0, claim.getZ()),
                (task) -> syncAsync(claim, deleted)
        );
    }

    private void syncAsync(Claim claim, boolean deleted) {
        RClaim.getInstance().getFoliaLib().getScheduler().runAsync((task) -> {
            try {
                if (deleted) {
                    RClaim.getInstance().getStorage().deleteClaim(claim.getID());
                } else if (RClaim.getInstance().getStorage().hasClaim(claim.getID())) {
                    RClaim.getInstance().getStorage().updateClaim(claim);
                } else {
                    RClaim.getInstance().getStorage().insertClaim(claim);
                }

                JsonObject payload = new JsonObject();
                payload.addProperty("serverId", serverId);
                try (Jedis jedis = pool.getResource()) {
                    jedis.publish(ConfigLoader.getConfig().getRedis().getChannel(), payload.toString());
                }
            } catch (Exception e) {
                Bukkit.getConsoleSender().sendMessage(ColorBuilder.convertColors(
                        "&c[RClaim] Failed to publish Redis sync message: " + e.getMessage()));
            }
        });
    }

    public void shutdown() {
        running = false;
        if (subscriberThread != null) subscriberThread.interrupt();
        if (pool != null) pool.close();
    }
}
