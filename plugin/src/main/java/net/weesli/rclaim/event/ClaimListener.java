package net.weesli.rclaim.event;

import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.events.ClaimDeleteEvent;
import net.weesli.rclaim.api.events.ClaimEnterEvent;
import net.weesli.rclaim.api.events.ClaimLeaveEvent;
import net.weesli.rclaim.api.events.ClaimStatusChangeEvent;
import net.weesli.rclaim.api.model.Claim;
import net.weesli.rclaim.api.model.ClaimEffect;
import net.weesli.rclaim.config.ConfigLoader;
import net.weesli.rclaim.ui.inventories.ClaimMainMenu;
import net.weesli.rclaim.api.enums.ClaimStatus;
import net.weesli.rclaim.util.FormatUtil;
import net.weesli.rozslib.events.BlockLeftClickEvent;
import net.weesli.rozslib.events.BlockRightClickEvent;
import net.weesli.rozslib.events.PlayerDamageByPlayerEvent;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.WeatherType;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ClaimListener implements Listener {

    private final List<Player> claimBlockEditors = new ArrayList<>();

    @EventHandler
    public void onSpawn(EntitySpawnEvent e) {
        Entity entity = e.getEntity();
        // Find if the entity is inside a claim
        Claim claim = RClaim.getInstance().getClaimManager().getClaim(e.getLocation());
        if (claim == null) {
            return;
        }
        // Cancel spawning if the claim does not allow monsters or animals
        if (entity instanceof Monster) {
            if (!claim.checkStatus(ClaimStatus.SPAWN_MONSTER)) {
                e.setCancelled(true);
            }
        } else {
            if (!claim.checkStatus(ClaimStatus.SPAWN_ANIMAL)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onTnt(EntityExplodeEvent e) {
        // Prevent TNT explosions in claims where explosions are not allowed
        for (Block block : e.blockList()) {
            Claim claim = RClaim.getInstance().getClaimManager().getClaim(block.getLocation());
            if (claim == null) {
                return;
            }
            if (!claim.checkStatus(ClaimStatus.EXPLOSION)) {
                e.setCancelled(true);
                break;
            }
        }
    }

    @EventHandler
    public void onPvP(PlayerDamageByPlayerEvent e) {
        // Check if PvP is allowed in both the victim's and the attacker's claims
        Claim victim_claim = RClaim.getInstance().getClaimManager().getClaim(e.getPlayer().getLocation());
        Claim attacker_claim = RClaim.getInstance().getClaimManager().getClaim(e.getDamager().getLocation());
        if (attacker_claim == null || victim_claim == null) {
            return;
        }
        // Cancel the event and notify the player if PvP is not allowed
        if (!attacker_claim.checkStatus(ClaimStatus.PVP)) {
            e.setCancelled(true);
            e.getDamager().sendMessage(RClaim.getInstance().getMessage("STATUS_PVP"));
            return;
        }
        if (!victim_claim.checkStatus(ClaimStatus.PVP)) {
            e.setCancelled(true);
            e.getDamager().sendMessage(RClaim.getInstance().getMessage("STATUS_PVP"));
        }
    }

    @EventHandler
    public void onBreakBedrock(BlockBreakEvent e) {
        // Prevent breaking bedrock blocks that mark the center of a claim
        Claim claim = RClaim.getInstance().getClaimManager().getClaim(e.getBlock().getLocation());
        if (claim == null) {
            return;
        }
        if (!e.getBlock().getType().equals(claim.getBlock())) {
            return;
        }
        boolean isBlock = e.getBlock().getLocation().equals(claim.getBlockLocation());
        if (isBlock) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onClickBedrock(BlockRightClickEvent e) {
        // Handle clicking on the center bedrock block to open claim UI
        Player player = e.getPlayer();
        Block block = e.getClickedBlock();
        Claim claim = RClaim.getInstance().getClaimManager().getClaim(e.getClickedBlock().getLocation());
        if (claim == null) {
            return;
        }
        if (!block.getType().equals(claim.getBlock())) {
            return;
        }
        boolean isBlock = block.getLocation().equals(claim.getBlockLocation());
        if (!isBlock) {
            return;
        }
        if (!claim.isOwner(player.getUniqueId())) {
            e.setCancelled(true);
            return;
        }
        RClaim.getInstance().getUiManager().openInventory(player, claim, ClaimMainMenu.class);
    }

    @EventHandler
    public void onDeleted(ClaimDeleteEvent e) {
        // Handle claim deletion, removing holograms and effects
        if (ConfigLoader.getConfig().getHologram().isEnabled()) {
            if (RClaim.getInstance().getHologramManager().getHologramIntegration().hasHologram(e.getClaim().getID())) {
                RClaim.getInstance().getHologramManager().getHologramIntegration().deleteHologram(e.getClaim().getID());
            }
        }
        Block block = e.getClaim().getBlockLocation().getBlock();
        if (block.getType().equals(e.getClaim().getBlock())) {
            block.setType(Material.AIR);
        }
        for (Player player : e.getClaim().getCenter().getWorld().getPlayers()) {
            for (ClaimEffect effect : e.getClaim().getEffects()) {
                if (player.hasPotionEffect(effect.getEffect().getType())) {
                    player.removePotionEffect(effect.getEffect().getType());
                }
            }
        }

        // effects clear
        Chunk chunk = Bukkit.getWorld(e.getClaim().getWorldName()).getChunkAt(e.getClaim().getX(), e.getClaim().getZ());
        for (Entity entity : chunk.getEntities()){
            if (entity instanceof Player player) {
                for (ClaimEffect effect : e.getClaim().getEffects()) {
                    if (player.hasPotionEffect(effect.getEffect().getType())) {
                        player.removePotionEffect(effect.getEffect().getType());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEnter(ClaimEnterEvent e) {
        // Send a message to the player when they enter a claim
        HashMap<String, String> map = new HashMap<>();
        map.put("player", Bukkit.getOfflinePlayer(e.getClaim().getOwner()).getName());
        FormatUtil.sendMessage(e.getPlayer(), map);
    }

    @EventHandler
    public void onSpread(BlockSpreadEvent e) {
        // Prevent block spreading (e.g., fire, grass) in claims if not allowed
        Block block = e.getBlock();
        Claim claim = RClaim.getInstance().getClaimManager().getClaim(e.getBlock().getLocation());
        if (claim == null) {
            return;
        }

        if (!claim.checkStatus(ClaimStatus.SPREAD)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onClaimStatusChangeEvent(ClaimStatusChangeEvent e) {
        Chunk chunk = Bukkit.getWorld(e.getClaim().getWorldName()).getChunkAt(e.getClaim().getX(),e.getClaim().getZ());
        // Handle changes in claim status, such as weather or time settings
        if (!e.isChangeStatus()) {
            if (e.getStatus().equals(ClaimStatus.WEATHER)) {
                Arrays.stream(chunk.getEntities())
                        .filter(entity -> entity instanceof Player)
                        .forEach(founded_player -> ((Player) founded_player).resetPlayerWeather());
            }
            if (e.getStatus().equals(ClaimStatus.TIME)) {
                Arrays.stream(chunk.getEntities())
                        .filter(entity -> entity instanceof Player)
                        .forEach(founded_player -> ((Player) founded_player).resetPlayerTime());
            }
        } else {
            if (e.getStatus().equals(ClaimStatus.WEATHER)) {
                Arrays.stream(chunk.getEntities())
                        .filter(entity -> entity instanceof Player)
                        .forEach(founded_player -> ((Player) founded_player).setPlayerWeather(WeatherType.CLEAR));
            }
            if (e.getStatus().equals(ClaimStatus.TIME)) {
                Arrays.stream(chunk.getEntities())
                        .filter(entity -> entity instanceof Player)
                        .forEach(founded_player -> ((Player) founded_player).setPlayerTime(6000, false));
            }
        }
    }

    @EventHandler
    public void onClickClaimBlock(BlockLeftClickEvent e){ // move the claim block
        Player player = e.getPlayer();
        Block block = e.getClickedBlock();
        Claim claim = RClaim.getInstance().getClaimManager().getClaim(e.getClickedBlock().getLocation());
        if (claim == null) {
            return;
        }
        if (!claim.isOwner(player.getUniqueId())) {
            e.setCancelled(true);
            return;
        }
        if (claimBlockEditors.contains(player)) {
            claimBlockEditors.remove(player);
            RClaim.getInstance().getMessage("DISABLE_CLAIM_BLOCK_EDIT_MODE");
            claim.moveBlock(block.getLocation().add(0,1,0));
            RClaim.getInstance().getHologramManager().getHologramIntegration().deleteHologram(claim.getID()); // delete old hologram
            RClaim.getInstance().getHologramManager().getHologramIntegration().createHologram(claim.getID()); // create new hologram in new location
            return;
        }
        if (!e.getPlayer().isSneaking()) return; // if player is not press shift return
        if (!block.getType().equals(claim.getBlock())) {
            return;
        }
        boolean isBlock = block.getLocation().equals(claim.getBlockLocation());
        if (!isBlock) {
            return;
        }
        claimBlockEditors.add(player);
        RClaim.getInstance().getMessage("ENABLE_CLAIM_BLOCK_EDIT_MODE");
    }

    @EventHandler
    public void onLeaveClaim(ClaimLeaveEvent e){ // this event use for claim block editor
        Player player = e.getPlayer();
        if (claimBlockEditors.contains(player)) { // if player leaved claim disable edit mode
            claimBlockEditors.remove(player);
            RClaim.getInstance().getMessage("DISABLE_CLAIM_BLOCK_EDIT_MODE");
        }

    }

}
