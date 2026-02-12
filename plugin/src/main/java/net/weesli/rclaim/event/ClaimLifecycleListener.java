package net.weesli.rclaim.event;

import com.tcoded.folialib.FoliaLib;
import lombok.NonNull;
import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.events.ClaimEnterEvent;
import net.weesli.rclaim.api.events.ClaimLeaveEvent;
import net.weesli.rclaim.api.events.ClaimStatusChangeEvent;
import net.weesli.rclaim.api.model.Claim;
import net.weesli.rclaim.api.model.ClaimEffect;
import net.weesli.rclaim.config.ConfigLoader;
import net.weesli.rclaim.ui.inventories.ClaimMainMenu;
import net.weesli.rclaim.util.FormatUtil;
import net.weesli.rozslib.events.BlockLeftClickEvent;
import net.weesli.rozslib.events.BlockRightClickEvent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffect;

import javax.annotation.Nonnull;
import java.util.*;

import static net.weesli.rclaim.config.lang.LangConfig.sendMessageToPlayer;

public class ClaimLifecycleListener implements Listener {

    public ClaimLifecycleListener() {
        RClaim.getInstance().getServer().getPluginManager().registerEvents(this, RClaim.getInstance());
    }
    private final List<Player> claimBlockEditors = new ArrayList<>();
    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        Player player = e.getPlayer();
        Claim from = RClaim.getInstance().getClaimManager().getClaim(e.getFrom());
        Claim to = RClaim.getInstance().getClaimManager().getClaim(e.getTo());

        if (from != null && (!from.equals(to))) {
            Bukkit.getPluginManager().callEvent(new ClaimLeaveEvent(from, player));
        }
        if (to != null && (!to.equals(from))) {
            Bukkit.getPluginManager().callEvent(new ClaimEnterEvent(to, player));
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (e.getFrom().getChunk().equals(e.getTo().getChunk())) return;
        Player player = e.getPlayer();

        Optional<Claim> from = Optional.ofNullable(RClaim.getInstance().getClaimManager().getClaim(e.getFrom()));
        Optional<Claim> to = Optional.ofNullable(RClaim.getInstance().getClaimManager().getClaim(e.getTo()));

        if (from.isPresent() && to.isPresent() && from.get().isOwner(to.get().getOwner())) {
            return;
        }
        if (!from.equals(to)) {
            from.ifPresent(claim -> Bukkit.getPluginManager().callEvent(new ClaimLeaveEvent(claim, player)));
            to.ifPresent(claim -> Bukkit.getPluginManager().callEvent(new ClaimEnterEvent(claim, player)));
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        Claim claim = RClaim.getInstance().getClaimManager().getClaim(player.getLocation());
        if (claim != null) {
            Bukkit.getPluginManager().callEvent(new ClaimEnterEvent(claim, player));
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        RClaim.getInstance().getFoliaLib().getScheduler().runLater(() -> {
            Player player = e.getPlayer();
            Location loc = player.getRespawnLocation();
            if (loc != null) {
                Claim claim = RClaim.getInstance().getClaimManager().getClaim(loc);
                if (claim != null) {
                    Bukkit.getPluginManager().callEvent(new ClaimEnterEvent(claim, player));
                }
            }
        }, 20L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        Claim claim = RClaim.getInstance().getClaimManager().getClaim(player.getLocation());
        if (claim != null) {
            Bukkit.getPluginManager().callEvent(new ClaimLeaveEvent(claim, player));
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        Claim claim = RClaim.getInstance().getClaimManager().getClaim(player.getLocation());
        if (claim != null) {
            Bukkit.getPluginManager().callEvent(new ClaimLeaveEvent(claim, player));
        }
    }

    @EventHandler
    public void onClaimEnter(ClaimEnterEvent e) {
        Player player = e.getPlayer();
        Claim claim = e.getClaim();

        if (ConfigLoader.getConfig().getEnterMessage().isEnabled()){
            HashMap<String, String> map = new HashMap<>();
            map.put("player", Bukkit.getOfflinePlayer(e.getClaim().getOwner()).getName());
            FormatUtil.sendMessage(e.getPlayer(), map);
        }
        if (claim.isOwner(player.getUniqueId()) || claim.isMember(player.getUniqueId())) {
            List<ClaimEffect> effects = claim.getEffects();
            for (ClaimEffect effect : effects) {
                if (effect.isEnabled()) {
                    player.addPotionEffect(new PotionEffect(effect.getEffect().getType(), 999999, effect.getLevel() - 1));
                }
            }
        }

        if (claim.getClaimStatuses().contains("WEATHER")) {
            player.setPlayerWeather(WeatherType.CLEAR);
        }
        if (claim.getClaimStatuses().contains("TIME")) {
            player.setPlayerTime(6000, false);
        }

        if (RClaim.getInstance().getCombatManager() != null &&
                RClaim.getInstance().getCombatManager().getCombatIntegration().isPvP(player)) {
            player.setVelocity(player.getLocation().getDirection().normalize().multiply(-2).setY(0.5));
            sendMessageToPlayer("COMBAT_SYSTEM_MESSAGE", player);
        }
    }

    @EventHandler
    public void onClaimLeave(ClaimLeaveEvent e) {
        Player player = e.getPlayer();
        Claim claim = e.getClaim();

        if (claim.isOwner(player.getUniqueId()) || claim.isMember(player.getUniqueId())) {
            claim.clearEffects(player);
        }
        if (claim.getClaimStatuses().contains("WEATHER")) {
            player.resetPlayerWeather();
        }
        if (claim.getClaimStatuses().contains("TIME")) {
            player.resetPlayerTime();
        }
    }

    @EventHandler
    public void onBedrockBlockBreak(BlockBreakEvent e) {
        Material material = e.getBlock().getType();
        if (!material.equals(Material.BEDROCK)) return;
        Claim claim = RClaim.getInstance().getClaimManager().getClaim(e.getBlock().getLocation());
        if (claim == null) return;
        if (e.getPlayer().hasPermission("rclaim.admin.bypass")) return;
        if (claim.getBlockLocation().equals(e.getBlock().getLocation())) e.setCancelled(true);
    }

    @EventHandler
    public void onBedrockClick(BlockRightClickEvent e) {
        Player player = e.getPlayer();
        Block block = e.getClickedBlock();
        Claim claim = RClaim.getInstance().getClaimManager().getClaim(block.getLocation());
        if (claim == null) return;
        if (!block.getType().equals(claim.getBlock())) return;
        if (!block.getLocation().equals(claim.getBlockLocation())) return;
        if (!claim.isOwner(player.getUniqueId())) {
            e.setCancelled(true);
            return;
        }
        RClaim.getInstance().getUiManager().openInventory(player, claim, ClaimMainMenu.class);
    }

    @EventHandler
    public void onClaimStatusChange(ClaimStatusChangeEvent e){
        World world = Objects.requireNonNull(Bukkit.getWorld(e.getClaim().getWorldName()));
        RClaim.getInstance().getFoliaLib().getScheduler().runAtLocation(new Location(world, e.getClaim().getX(), 0, e.getClaim().getZ()), (wrapper) -> {
            Chunk chunk = world.getChunkAt(e.getClaim().getX() >> 4, e.getClaim().getZ() >> 4);
            if (!e.isChangeStatus()) {
                if ("WEATHER".equals(e.getStatus())) {
                    Arrays.stream(chunk.getEntities())
                            .filter(ent -> ent instanceof Player)
                            .forEach(ent -> ((Player) ent).resetPlayerWeather());
                }
                if ("TIME".equals(e.getStatus())) {
                    Arrays.stream(chunk.getEntities())
                            .filter(ent -> ent instanceof Player)
                            .forEach(ent -> ((Player) ent).resetPlayerTime());
                }
            } else {
                if ("WEATHER".equals(e.getStatus())) {
                    Arrays.stream(chunk.getEntities())
                            .filter(ent -> ent instanceof Player)
                            .forEach(ent -> ((Player) ent).setPlayerWeather(WeatherType.CLEAR));
                }
                if ("TIME".equals(e.getStatus())) {
                    Arrays.stream(chunk.getEntities())
                            .filter(ent -> ent instanceof Player)
                            .forEach(ent -> ((Player) ent).setPlayerTime(6000, false));
                }
            }
        });
    }

    @EventHandler
    public void onClaimBlockEditor(BlockLeftClickEvent e){
        Player player = e.getPlayer();
        Block block = e.getClickedBlock();
        Claim claim = RClaim.getInstance().getClaimManager().getClaim(block.getLocation());
        if (claim == null) return;
        if (!claim.isOwner(player.getUniqueId())) return;

        if (claimBlockEditors.contains(player)) {
            claimBlockEditors.remove(player);
            sendMessageToPlayer("DISABLE_CLAIM_BLOCK_EDIT_MODE", player);
            claim.moveBlock(block.getLocation().add(0, 1, 0));
            RClaim.getInstance().getHologramManager().getHologramIntegration().deleteHologram(claim.getID());
            RClaim.getInstance().getHologramManager().getHologramIntegration().createHologram(claim.getID());
            return;
        }

        if (!player.isSneaking()) return;
        if (!block.getType().equals(claim.getBlock())) return;
        if (!block.getLocation().equals(claim.getBlockLocation())) return;

        claimBlockEditors.add(player);
        sendMessageToPlayer("ENABLE_CLAIM_BLOCK_EDIT_MODE", player);
    }

    @EventHandler
    public void onClaimBlockEditorLeave(ClaimLeaveEvent e){
        Player player = e.getPlayer();
        if (claimBlockEditors.contains(player)) {
            claimBlockEditors.remove(player);
            sendMessageToPlayer("DISABLE_CLAIM_BLOCK_EDIT_MODE", player);
        }
    }

    @EventHandler
    public void FluidEvent(BlockFromToEvent e) {
        Claim placed = RClaim.getInstance().getClaimManager().getClaim(e.getBlock().getLocation());
        Claim target = RClaim.getInstance().getClaimManager().getClaim(e.getToBlock().getLocation());
        if (placed == null && target != null) {
            e.setCancelled(true);
        }
        if (placed != null && target != null && !placed.getOwner().equals(target.getOwner())) {
            e.setCancelled(true);
        }
    }
}
