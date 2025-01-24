package net.weesli.rClaim.events;

import net.weesli.rClaim.RClaim;
import net.weesli.rClaim.api.events.ClaimDeleteEvent;
import net.weesli.rClaim.api.events.ClaimEnterEvent;
import net.weesli.rClaim.api.events.ClaimStatusChangeEvent;
import net.weesli.rClaim.modal.ClaimEffect;
import net.weesli.rClaim.utils.ClaimManager;
import net.weesli.rClaim.modal.Claim;
import net.weesli.rClaim.enums.ClaimStatus;
import net.weesli.rClaim.utils.ClaimUtils;
import net.weesli.rClaim.utils.FormatManager;
import net.weesli.rozsLib.events.BlockRightClickEvent;
import net.weesli.rozsLib.events.PlayerDamageByPlayerEvent;
import org.bukkit.Bukkit;
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

import java.util.Arrays;
import java.util.HashMap;

public class ClaimListener implements Listener {

    @EventHandler
    public void onSpawn(EntitySpawnEvent e) {
        Entity entity = e.getEntity();
        // Find if the entity is inside a claim
        Claim claim = ClaimUtils.getClaim(e.getLocation());
        if (claim == null) {
            return;
        }
        Claim target_claim = (claim.getCenterId().isEmpty() ? claim : ClaimUtils.getClaim(claim.getCenterId()));

        // Cancel spawning if the claim does not allow monsters or animals
        if (entity instanceof Monster) {
            if (!target_claim.checkStatus(ClaimStatus.SPAWN_MONSTER)) {
                e.setCancelled(true);
            }
        } else {
            if (!target_claim.checkStatus(ClaimStatus.SPAWN_ANIMAL)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onTnt(EntityExplodeEvent e) {
        // Prevent TNT explosions in claims where explosions are not allowed
        for (Block block : e.blockList()) {
            Claim claim = ClaimUtils.getClaim(block.getLocation());
            if (claim == null) {
                return;
            }
            Claim target_claim = (claim.getCenterId().isEmpty() ? claim : ClaimUtils.getClaim(claim.getCenterId()));
            if (!target_claim.checkStatus(ClaimStatus.EXPLOSION)) {
                e.setCancelled(true);
                break;
            }
        }
    }

    @EventHandler
    public void onPvP(PlayerDamageByPlayerEvent e) {
        // Check if PvP is allowed in both the victim's and the attacker's claims
        Claim victim_claim = ClaimUtils.getClaim(e.getPlayer().getLocation());
        Claim attacker_claim = ClaimUtils.getClaim(e.getDamager().getLocation());
        if (attacker_claim == null || victim_claim == null) {
            return;
        }
        Claim target_attacker_claim = (attacker_claim.getCenterId().isEmpty() ? attacker_claim : ClaimManager.getClaim(attacker_claim.getCenterId()).get());
        Claim target_victim_claim = (victim_claim.getCenterId().isEmpty() ? victim_claim : ClaimManager.getClaim(victim_claim.getCenterId()).get());

        // Cancel the event and notify the player if PvP is not allowed
        if (!target_attacker_claim.checkStatus(ClaimStatus.PVP)) {
            e.setCancelled(true);
            e.getDamager().sendMessage(RClaim.getInstance().getMessage("STATUS_PVP"));
            return;
        }
        if (!target_victim_claim.checkStatus(ClaimStatus.PVP)) {
            e.setCancelled(true);
            e.getDamager().sendMessage(RClaim.getInstance().getMessage("STATUS_PVP"));
        }
    }

    @EventHandler
    public void onBreakBedrock(BlockBreakEvent e) {
        // Prevent breaking bedrock blocks that mark the center of a claim
        Claim claim = ClaimUtils.getClaim(e.getBlock().getLocation());
        if (claim == null) {
            return;
        }
        Claim target_claim = (claim.getCenterId().isEmpty() ? claim : ClaimUtils.getClaim(claim.getCenterId()));
        if (!e.getBlock().getType().equals(target_claim.getBlock())) {
            return;
        }
        boolean isBlock = e.getBlock().getLocation().equals(target_claim.getCenter());
        if (isBlock) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onClickBedrock(BlockRightClickEvent e) {
        // Handle clicking on the center bedrock block to open claim UI
        Player player = e.getPlayer();
        Block block = e.getClickedBlock();
        Claim claim = ClaimUtils.getClaim(e.getClickedBlock().getLocation());
        if (claim == null) {
            return;
        }
        if (!claim.isCenter()) {
            return;
        }
        if (!block.getType().equals(claim.getBlock())) {
            return;
        }
        boolean isBlock = block.getLocation().equals(claim.getCenter());
        if (!isBlock) {
            return;
        }
        if (!claim.isOwner(player.getUniqueId())) {
            e.setCancelled(true);
            return;
        }
        RClaim.getInstance().getUiManager().openInventory(player, claim, RClaim.getInstance().getUiManager().getMainMenu());
    }

    @EventHandler
    public void onDeleted(ClaimDeleteEvent e) {
        // Handle claim deletion, removing holograms and effects
        if (e.getClaim().isCenter()) {
            if (RClaim.getInstance().getConfig().getBoolean("options.hologram.enabled")) {
                if (RClaim.getInstance().getHologram().hasHologram(e.getClaim().getID())) {
                    RClaim.getInstance().getHologram().deleteHologram(e.getClaim().getID());
                }
            }
            Block block = e.getClaim().getCenter().getBlock();
            if (block.getType().equals(e.getClaim().getBlock())) {
                block.setType(Material.AIR);
            }
        }
        for (Player player : e.getClaim().getCenter().getWorld().getPlayers()) {
            for (ClaimEffect effect : e.getClaim().getEffects()) {
                if (player.hasPotionEffect(effect.getEffect().getType())) {
                    player.removePotionEffect(effect.getEffect().getType());
                }
            }
        }

        // effects clear
        for (Entity entity : e.getClaim().getChunk().getEntities()){
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
        FormatManager.sendMessage(e.getPlayer(), map);
    }

    @EventHandler
    public void onSpread(BlockSpreadEvent e) {
        // Prevent block spreading (e.g., fire, grass) in claims if not allowed
        Block block = e.getBlock();
        Claim claim = ClaimUtils.getClaim(e.getBlock().getLocation());
        if (claim == null) {
            return;
        }
        Claim target_claim = (claim).getCenterId().isEmpty() ? claim : ClaimManager.getClaim(claim.getCenterId()).get();
        if (!target_claim.checkStatus(ClaimStatus.SPREAD)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onClaimStatusChangeEvent(ClaimStatusChangeEvent e) {
        // Handle changes in claim status, such as weather or time settings
        Player player = e.getPlayer();
        if (!e.isChangeStatus()) {
            if (e.getStatus().equals(ClaimStatus.WEATHER)) {
                Arrays.stream(e.getClaim().getChunk().getEntities())
                        .filter(entity -> entity instanceof Player)
                        .forEach(founded_player -> ((Player) founded_player).resetPlayerWeather());
            }
            if (e.getStatus().equals(ClaimStatus.TIME)) {
                Arrays.stream(e.getClaim().getChunk().getEntities())
                        .filter(entity -> entity instanceof Player)
                        .forEach(founded_player -> ((Player) founded_player).resetPlayerTime());
            }
        } else {
            if (e.getStatus().equals(ClaimStatus.WEATHER)) {
                Arrays.stream(e.getClaim().getChunk().getEntities())
                        .filter(entity -> entity instanceof Player)
                        .forEach(founded_player -> ((Player) founded_player).setPlayerWeather(WeatherType.CLEAR));
            }
            if (e.getStatus().equals(ClaimStatus.TIME)) {
                Arrays.stream(e.getClaim().getChunk().getEntities())
                        .filter(entity -> entity instanceof Player)
                        .forEach(founded_player -> ((Player) founded_player).setPlayerTime(6000, false));
            }
        }
    }
}
