package net.weesli.rClaim.events;

import net.weesli.rClaim.RClaim;
import net.weesli.rClaim.api.events.ClaimDeleteEvent;
import net.weesli.rClaim.api.events.ClaimEnterEvent;
import net.weesli.rClaim.management.ClaimManager;
import net.weesli.rClaim.ui.MenuManagement;
import net.weesli.rClaim.utils.Claim;
import net.weesli.rClaim.utils.ClaimStatus;
import net.weesli.rClaim.utils.FormatManager;
import net.weesli.rozsLib.events.BlockRightClickEvent;
import net.weesli.rozsLib.events.PlayerDamageByPlayerEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
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

import java.util.HashMap;
import java.util.Optional;

public class ClaimListener implements Listener {

    @EventHandler
    public void onSpawn(EntitySpawnEvent e){
        Entity entity = e.getEntity();
        Optional<Claim> claim = ClaimManager.getClaims().stream().filter(c -> c.contains(entity.getLocation())).findFirst();
        if (claim.isPresent()){
            // if entity in a claim and entity is a monster
            if (entity instanceof Monster){
                if (!claim.get().checkStatus(ClaimStatus.SPAWN_MONSTER)){
                    e.setCancelled(true);
                }
            // if entity is a animal
            }else {
                if (!claim.get().checkStatus(ClaimStatus.SPAWN_ANIMAL)){
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onTnt(EntityExplodeEvent e){
        Entity entity = e.getEntity();
        Optional<Claim> claim = ClaimManager.getClaims().stream().filter(c -> c.contains(entity.getLocation())).findFirst();
        if (claim.isPresent()){
            if (!claim.get().checkStatus(ClaimStatus.EXPLOSION)){
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPvP(PlayerDamageByPlayerEvent e){
        Optional<Claim> victim_claim = ClaimManager.getClaims().stream().filter(c -> c.contains(e.getPlayer().getLocation())).findFirst();
        Optional<Claim> attacker_claim = ClaimManager.getClaims().stream().filter(c -> c.contains(e.getDamager().getLocation())).findFirst();
        if(attacker_claim.isPresent()){
            if (!attacker_claim.get().checkStatus(ClaimStatus.PVP)){
                e.setCancelled(true);
                e.getDamager().sendMessage(RClaim.getInstance().getMessage("STATUS_PVP"));
                return;
            }
        }
        if(victim_claim.isPresent()){
            if (!victim_claim.get().checkStatus(ClaimStatus.PVP)){
                e.setCancelled(true);
                e.getDamager().sendMessage(RClaim.getInstance().getMessage("STATUS_PVP"));
            }
        }
    }

    @EventHandler
    public void onBreakBedrock(BlockBreakEvent e){
        Optional<Claim> claim = ClaimManager.getClaims().stream().filter(c -> c.contains(e.getBlock().getLocation())).findFirst();
        if (!e.getBlock().getType().equals(Material.BEDROCK)){return;}
        if (claim.isPresent()){
            boolean isBlock = e.getBlock().getLocation().equals(claim.get().getCenter());
            if (isBlock){
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onClickBedrock(BlockRightClickEvent e){
        Player player = e.getPlayer();
        Block block = e.getClickedBlock();
        if (!block.getType().equals(Material.BEDROCK)){return;}
        Optional<Claim> claim = ClaimManager.getClaims().stream().filter(c -> c.contains(block.getLocation())).findFirst();
        if (claim.isPresent()){
            boolean isBlock = block.getLocation().equals(claim.get().getCenter());
            if (!isBlock){
                return;
            }
            if (!claim.get().isOwner(player.getUniqueId())){
                e.setCancelled(true);
                return;
            }
            player.openInventory(MenuManagement.getMainMenu(player));
        }
    }


    @EventHandler
    public void onDeleted(ClaimDeleteEvent e){
        if (e.getClaim().isCenter()){
            if (RClaim.getInstance().getConfig().getBoolean("options.hologram.enabled")){
                RClaim.getInstance().getHologram().deleteHologram(e.getClaim().getID());
            }

            Block block = e.getClaim().getCenter().getBlock();
            if (block.getType().equals(Material.BEDROCK)){
                block.setType(Material.AIR);
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
        if (claim.isPresent()){
            if (!claim.get().checkStatus(ClaimStatus.SPREAD)){
                e.setCancelled(true);
            }
        }
    }



}
