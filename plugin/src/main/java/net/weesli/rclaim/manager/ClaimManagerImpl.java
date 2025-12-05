package net.weesli.rclaim.manager;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.events.ClaimCreateEvent;
import net.weesli.rclaim.api.events.ClaimDeleteEvent;
import net.weesli.rclaim.api.events.ClaimSubClaimCreateEvent;
import net.weesli.rclaim.api.manager.ClaimManager;
import net.weesli.rclaim.api.model.Claim;
import net.weesli.rclaim.api.model.SubClaim;
import net.weesli.rclaim.config.ConfigLoader;
import net.weesli.rclaim.api.enums.ExplodeCause;
import net.weesli.rclaim.model.ClaimImpl;
import net.weesli.rclaim.model.SubClaimImpl;
import net.weesli.rclaim.util.BaseUtil;
import net.weesli.rclaim.util.NameSpaceUtil;
import net.weesli.rclaim.util.PermissionUtil;
import net.weesli.rozsconfig.annotations.ConfigKey;
import net.weesli.rozslib.color.ColorBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.weesli.rclaim.config.lang.LangConfig.sendMessageToPlayer;
import static net.weesli.rclaim.util.BaseUtil.generateId;

public class ClaimManagerImpl implements ClaimManager {

    public Claim getClaim(Location location){
        return getClaim(location.getChunk().getPersistentDataContainer().get(NameSpaceUtil.getKey(), PersistentDataType.STRING));
    }

    public Claim getClaim(Chunk chunk){
        return getClaim(chunk.getPersistentDataContainer().get(NameSpaceUtil.getKey(), PersistentDataType.STRING));
    }

    public Claim getClaim(String id){
        return RClaim.getInstance().getCacheManager().getClaims().get(id);
    }

    public boolean createSubClaim(Player player, Claim claim, Location location){
        Chunk chunk = location.getChunk();
        SubClaim subClaim = new SubClaimImpl(claim.getID(), chunk.getX(), chunk.getZ());
        ClaimSubClaimCreateEvent createEvent = new ClaimSubClaimCreateEvent(claim,location,player);
        RClaim.getInstance().getServer().getPluginManager().callEvent(createEvent);
        if(createEvent.isCancelled()){
            return false;
        }
        chunk.getPersistentDataContainer().set(
                NameSpaceUtil.getKey(),
                PersistentDataType.STRING,
                claim.getID()
        );
        claim.addSubClaim(subClaim);
        return true;
    }

    public void createClaim(Chunk chunk, Player owner) {
        boolean hasLimit = PermissionUtil.checkPlayerClaimLimit(owner); // if player has limit for create claim
        if(!hasLimit) return;
        boolean isBetweenAnyClaim = BaseUtil.isBetweenAnyClaim(chunk); // if chunk is between any claim
        if (!isBetweenAnyClaim) {
            sendMessageToPlayer("VERY_CLOSE_TO_ANOTHER_CLAIM", owner);
            return;
        }
        String id = generateId();
        Claim claim = new ClaimImpl(id, owner.getUniqueId(), new ArrayList<>() , new ArrayList<>(), chunk.getX() * 16,chunk.getZ() * 16, chunk.getWorld());
        ClaimCreateEvent event = new ClaimCreateEvent(owner, claim);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            RClaim.getInstance().getCacheManager().getClaims().addClaim(claim);
            chunk.getPersistentDataContainer().set(
                    NameSpaceUtil.getKey(),
                    PersistentDataType.STRING,
                    claim.getID()
            );
            Block block = owner.getWorld().getBlockAt(claim.getCenter().add(0,1,0));

            block.setType(Material.BEDROCK);

            /*Arrays.stream(ConfigLoader.getConfig().getClaimPermissions().getClass().getFields())
                    .toList().forEach(value -> {
                        try {
                            if (ConfigLoader.getConfig().getClaimSettings().getDefaultClaimStatus().getClass().getField(value.getName()) != null) {
                                claim.addClaimStatus(value.getName());
                            }
                        } catch (NoSuchFieldException e) {
                            throw new RuntimeException(e);
                        }
                    });
             */
            List<String> defaultStatues = Arrays.stream(ConfigLoader.getConfig().getClaimSettings().getDefaultClaimStatus().getClass().getFields()).map(field -> {
                boolean value;
                try {
                    value = field.getBoolean(ConfigLoader.getConfig().getClaimSettings().getDefaultClaimStatus());
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                if (value){
                    return field.getName();
                }
                ConfigKey key = field.getAnnotation(ConfigKey.class);
                if (key !=null){
                    return key.value();
                }
                return field.getName();
            }).toList();
            for (String status : defaultStatues) {
                claim.addClaimStatus(status);
            }
            sendMessageToPlayer("SUCCESS_CLAIM_CREATED", owner);
        }

    }

    public void removeClaim(Claim claim) {
        Chunk chunk = Bukkit.getWorld(claim.getWorldName()).getChunkAt(claim.getX() >> 4, claim.getZ() >> 4); // get base claim chunk for remove
        if (!chunk.isLoaded()) { // if chunk is not loaded, start a task for load it and finish process
            /*Bukkit.getScheduler().runTask(RClaim.getInstance(), () ->{
                chunk.load();
                chunk.getPersistentDataContainer().remove(NameSpaceUtil.getKey());
                claim.getSubClaims().forEach(subClaim -> {
                    Chunk subClaimChunk = chunk.getWorld().getChunkAt(subClaim.getX(),subClaim.getZ());
                    subClaimChunk.getPersistentDataContainer().remove(NameSpaceUtil.getKey());
                });
                RClaim.getInstance().getCacheManager().getClaims().getCache().remove(claim.getID());
                RClaim.getInstance().getStorage().deleteClaim(claim.getID());
                chunk.unload();
            });*/
            RClaim.getInstance().getFoliaLib().getScheduler().runNextTick((wrapper) -> {
                chunk.load();
                chunk.getPersistentDataContainer().remove(NameSpaceUtil.getKey());
                claim.getSubClaims().forEach(subClaim -> {
                    Chunk subClaimChunk = chunk.getWorld().getChunkAt(subClaim.getX(),subClaim.getZ());
                    subClaimChunk.getPersistentDataContainer().remove(NameSpaceUtil.getKey());
                });
                RClaim.getInstance().getCacheManager().getClaims().getCache().remove(claim.getID());
                RClaim.getInstance().getStorage().deleteClaim(claim.getID());
                chunk.unload();
            });
        }else { // if chunk is loaded, direct finish process
            chunk.getPersistentDataContainer().remove(NameSpaceUtil.getKey());
            claim.getSubClaims().forEach(subClaim -> {
                Chunk subClaimChunk = chunk.getWorld().getChunkAt(subClaim.getX(),subClaim.getZ());
                subClaimChunk.getPersistentDataContainer().remove(NameSpaceUtil.getKey());
            });
            RClaim.getInstance().getCacheManager().getClaims().getCache().remove(claim.getID());
            RClaim.getInstance().getStorage().deleteClaim(claim.getID());
        }
        // delete hologram, block etc.
        if (ConfigLoader.getConfig().getHologram().isEnabled()) {
            if (RClaim.getInstance().getHologramManager().getHologramIntegration().hasHologram(claim.getID())) {
                RClaim.getInstance().getHologramManager().getHologramIntegration().deleteHologram(claim.getID());
            }
        }
        if (claim.isEnableBlock()){
            claim.getBlockLocation().getBlock().setType(Material.AIR);
        }

        // clear effects
        if (ConfigLoader.getConfig().getEffects().isEnabled()) {
            for (Player p : claim.getAllPlayers()) claim.clearEffects(p);
        }
    }

    public void explodeClaim(String ID, ExplodeCause cause){
        Claim claim = getClaim(ID);
        if (claim == null){
            return;
        }
        ClaimDeleteEvent event = new ClaimDeleteEvent(claim,cause);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()){
            removeClaim(claim);
            if (ConfigLoader.getConfig().getClaimTimeoutMessage().isEnabled()){
                ConfigLoader.getConfig().getClaimTimeoutMessage().getText()
                        .stream().map(line-> LegacyComponentSerializer.legacySection().serialize(ColorBuilder.convertColors(line))
                                .replaceAll("%player%", Bukkit.getPlayer(claim.getOwner()).getName())
                                .replaceAll("%x%", String.valueOf(claim.getX()))
                                .replaceAll("%z%", String.valueOf(claim.getZ()))
                                .replaceAll("%world%", claim.getWorldName())
                        ).forEach(Bukkit::broadcastMessage);
            }
        }
    }

    public boolean isSuitable(Chunk chunk){
        Claim claim = getClaim(chunk);
        return claim != null;
    }

    public boolean isSuitable(Location location){
        Claim claim = getClaim(location);
        return claim != null;
    }
}
