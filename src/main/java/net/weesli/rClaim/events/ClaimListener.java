package net.weesli.rClaim.events;

import net.weesli.rClaim.RClaim;
import net.weesli.rClaim.api.events.ClaimDeleteEvent;
import net.weesli.rClaim.api.events.ClaimEnterEvent;
import net.weesli.rClaim.api.events.ClaimStatusChangeEvent;
import net.weesli.rClaim.modal.ClaimEffect;
import net.weesli.rClaim.utils.ClaimManager;
import net.weesli.rClaim.modal.Claim;
import net.weesli.rClaim.enums.ClaimStatus;
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
import java.util.Optional;

public class ClaimListener implements Listener {

    @EventHandler
    public void onSpawn(EntitySpawnEvent e){
        Entity entity = e.getEntity();
        Optional<Claim> claim = ClaimManager.getClaims().stream().filter(c -> c.contains(entity.getLocation())).findFirst();
        if (claim.isEmpty()){
            return;
        }
        Claim target_claim = (claim.get().getCenterId().isEmpty() ? claim.get() : ClaimManager.getClaim(claim.get().getCenterId()).get());
        // if entity in a claim and entity is a monster
        if (entity instanceof Monster){
            if (!target_claim.checkStatus(ClaimStatus.SPAWN_MONSTER)){
                e.setCancelled(true);
            }
            // if entity is a animal
        }else {
            if (!target_claim.checkStatus(ClaimStatus.SPAWN_ANIMAL)){
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onTnt(EntityExplodeEvent e){
        Entity entity = e.getEntity();
        Optional<Claim> claim = ClaimManager.getClaims().stream().filter(c -> c.contains(entity.getLocation())).findFirst();
        if (claim.isEmpty()){
            return;
        }
        Claim target_claim = (claim.get().getCenterId().isEmpty() ? claim.get() : ClaimManager.getClaim(claim.get().getCenterId()).get());
        if (!target_claim.checkStatus(ClaimStatus.EXPLOSION)){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPvP(PlayerDamageByPlayerEvent e){
        Optional<Claim> victim_claim = ClaimManager.getClaims().stream().filter(c -> c.contains(e.getPlayer().getLocation())).findFirst();
        Optional<Claim> attacker_claim = ClaimManager.getClaims().stream().filter(c -> c.contains(e.getDamager().getLocation())).findFirst();
        if (attacker_claim.isEmpty() || victim_claim.isEmpty()){
            return;
        }
        Claim target_attacker_claim = (attacker_claim.get().getCenterId().isEmpty() ? attacker_claim.get() : ClaimManager.getClaim(attacker_claim.get().getCenterId()).get());
        Claim target_victim_claim = (victim_claim.get().getCenterId().isEmpty() ? victim_claim.get() : ClaimManager.getClaim(victim_claim.get().getCenterId()).get());
        if (!target_attacker_claim.checkStatus(ClaimStatus.PVP)){
            e.setCancelled(true);
            e.getDamager().sendMessage(RClaim.getInstance().getMessage("STATUS_PVP"));
            return;
        }
        if (!target_victim_claim.checkStatus(ClaimStatus.PVP)){
            e.setCancelled(true);
            e.getDamager().sendMessage(RClaim.getInstance().getMessage("STATUS_PVP"));
        }
    }

    @EventHandler
    public void onBreakBedrock(BlockBreakEvent e){
        Optional<Claim> claim = ClaimManager.getClaims().stream().filter(c -> c.contains(e.getBlock().getLocation())).findFirst();
        if (claim.isEmpty()){
            return;
        }
        Claim target_claim = (claim.get().getCenterId().isEmpty() ? claim.get() : ClaimManager.getClaim(claim.get().getCenterId()).get());
        if (!e.getBlock().getType().equals(target_claim.getBlock())){return;}
        boolean isBlock = e.getBlock().getLocation().equals(target_claim.getCenter());
        if (isBlock){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onClickBedrock(BlockRightClickEvent e){
        Player player = e.getPlayer();
        Block block = e.getClickedBlock();
        Optional<Claim> claim = ClaimManager.getClaims().stream().filter(c -> c.contains(block.getLocation())).findFirst();
        if (claim.isEmpty() || !claim.get().isCenter()){
            return;
        }
        if (!block.getType().equals(claim.get().getBlock())){return;}
        boolean isBlock = block.getLocation().equals(claim.get().getCenter());
        if (!isBlock){
            return;
        }
        if (!claim.get().isOwner(player.getUniqueId())){
            e.setCancelled(true);
            return;
        }
        RClaim.getInstance().getUiManager().openInventory(player,claim.get(),RClaim.getInstance().getUiManager().getMainMenu());
    }


    @EventHandler
    public void onDeleted(ClaimDeleteEvent e){
        if (e.getClaim().isCenter()){
            if (RClaim.getInstance().getConfig().getBoolean("options.hologram.enabled")){
                RClaim.getInstance().getHologram().deleteHologram(e.getClaim().getID());
            }
            Block block = e.getClaim().getCenter().getBlock();
            if (block.getType().equals(e.getClaim().getBlock())){
                block.setType(Material.AIR);
            }
        }
        for (Player player : e.getClaim().getCenter().getWorld().getPlayers()){
            for (ClaimEffect effect : e.getClaim().getEffects()){
                if (player.hasPotionEffect(effect.getEffect().getType())){
                    player.removePotionEffect(effect.getEffect().getType());
                }
            }
        }
    }

    @EventHandler
    public void onEnter(ClaimEnterEvent e){
        HashMap<String,String> map = new HashMap<>();
        map.put("player", Bukkit.getOfflinePlayer(e.getClaim().getOwner()).getName());
        FormatManager.sendMessage(e.getPlayer(), map);
    }


    @EventHandler
    public void onSpread(BlockSpreadEvent e){
        Block block = e.getBlock();
        Optional<Claim> claim = ClaimManager.getClaims().stream().filter(c -> c.contains(block.getLocation())).findFirst();
        if (claim.isEmpty()){
            return;
        }
        Claim target_claim = (claim.get().getCenterId().isEmpty() ? claim.get() : ClaimManager.getClaim(claim.get().getCenterId()).get());
        if (!target_claim.checkStatus(ClaimStatus.SPREAD)){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onClaimStatusChangeEvent(ClaimStatusChangeEvent e){
        Player player = e.getPlayer();
        if (!e.isChangeStatus()){
            if (e.getStatus().equals(ClaimStatus.WEATHER)){
                Arrays.stream(e.getClaim().getChunk().getEntities()).filter(entity -> entity instanceof Player).forEach(founded_player -> ((Player) founded_player).resetPlayerWeather());
            }
            if (e.getStatus().equals(ClaimStatus.TIME)){
                Arrays.stream(e.getClaim().getChunk().getEntities()).filter(entity -> entity instanceof Player).forEach(founded_player -> ((Player) founded_player).resetPlayerTime());
            }
        }else {
            if (e.getStatus().equals(ClaimStatus.WEATHER)){
                Arrays.stream(e.getClaim().getChunk().getEntities()).filter(entity -> entity instanceof Player).forEach(founded_player -> ((Player) founded_player).setPlayerWeather(WeatherType.CLEAR));
            }
            if (e.getStatus().equals(ClaimStatus.TIME)){
                Arrays.stream(e.getClaim().getChunk().getEntities()).filter(entity -> entity instanceof Player).forEach(founded_player -> ((Player) founded_player).setPlayerTime(6000,false));
            }
        }
    }

}
