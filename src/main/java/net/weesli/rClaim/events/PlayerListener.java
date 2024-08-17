package net.weesli.rClaim.events;

import net.weesli.rClaim.RClaim;
import net.weesli.rClaim.management.ClaimManager;
import net.weesli.rClaim.utils.Claim;
import net.weesli.rClaim.utils.ClaimPermission;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.InventoryHolder;

import java.util.Optional;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlaceBlock(BlockPlaceEvent e){
        Optional<Claim> claim = ClaimManager.getClaims().stream().filter(c -> c.contains(e.getBlock().getLocation())).findFirst();
        claim.ifPresent(c -> {
            if (c.isOwner(e.getPlayer().getUniqueId())){return;}
            if (!c.checkPermission(e.getPlayer().getUniqueId(), ClaimPermission.BLOCK_PLACE)){
                e.setCancelled(true);
                e.getPlayer().sendMessage(RClaim.getInstance().getMessage("PERMISSION_BLOCK_PLACE"));
            }
        });
    }

    @EventHandler
    public void onBreakBlock(BlockBreakEvent e){
        Optional<Claim> claim = ClaimManager.getClaims().stream().filter(c -> c.contains(e.getBlock().getLocation())).findFirst();
        claim.ifPresent(c -> {
            if (c.isOwner(e.getPlayer().getUniqueId())){return;}
            if (!c.checkPermission(e.getPlayer().getUniqueId(), ClaimPermission.BLOCK_BREAK)){
                e.setCancelled(true);
                e.getPlayer().sendMessage(RClaim.getInstance().getMessage("PERMISSION_BLOCK_BREAK"));
            }
        });
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e){
        Player player = e.getPlayer();
        if (e.getClickedBlock() == null){return;}
        if (e.getClickedBlock().getState() instanceof InventoryHolder){
            Optional<Claim> claim = ClaimManager.getClaims().stream().filter(c -> c.contains(e.getClickedBlock().getLocation())).findFirst();
            claim.ifPresent(c -> {
                if (c.isOwner(player.getUniqueId())){return;}
                if (!c.checkPermission(player.getUniqueId(), ClaimPermission.CONTAINER_OPEN)){
                    e.setCancelled(true);
                    player.sendMessage(RClaim.getInstance().getMessage("PERMISSION_CONTAINER_OPEN"));
                }
            });
        }
    }

    @EventHandler
    public void onInteractEntity(PlayerInteractEntityEvent e){
        Player player = e.getPlayer();
        Optional<Claim> claim = ClaimManager.getClaims().stream().filter(c-> c.contains(e.getRightClicked().getLocation())).findFirst();
        claim.ifPresent(c -> {
            if (c.isOwner(player.getUniqueId())){return;}
            if (!c.checkPermission(player.getUniqueId(), ClaimPermission.INTERACT_ENTITY)){
                e.setCancelled(true);
                player.sendMessage(RClaim.getInstance().getMessage("PERMISSION_ENTITY_INTERACT"));
            }
        });
    }

    @EventHandler
    public void onAttackEntity(EntityDamageByEntityEvent e){
        if (e.getEntity().getType() == EntityType.PLAYER){return;}
        if (e.getDamager() instanceof Player){
            Player player = (Player) e.getDamager();
            Optional<Claim> claim = ClaimManager.getClaims().stream().filter(c -> c.contains(e.getEntity().getLocation())).findFirst();
            claim.ifPresent(c -> {
                if (c.isOwner(player.getUniqueId())){return;}
                if (e.getEntity() instanceof Monster){
                    if (!c.checkPermission(player.getUniqueId(), ClaimPermission.ATTACK_MONSTER)){
                        e.setCancelled(true);
                        player.sendMessage(RClaim.getInstance().getMessage("PERMISSION_ATTACK_MONSTER"));
                    }
                }else {
                    if (!c.checkPermission(player.getUniqueId(), ClaimPermission.ATTACK_ANIMAL)){
                        e.setCancelled(true);
                        player.sendMessage(RClaim.getInstance().getMessage("PERMISSION_ATTACK_ANIMAL"));
                    }
                }
            });
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e){
        Player player = e.getPlayer();
        Optional<Claim> claim = ClaimManager.getClaims().stream().filter(c -> c.contains(e.getItemDrop().getLocation())).findFirst();
        claim.ifPresent(c -> {
            if (c.isOwner(player.getUniqueId())){return;}
            if (!c.checkPermission(player.getUniqueId(), ClaimPermission.DROP_ITEM)){
                e.setCancelled(true);
                player.sendMessage(RClaim.getInstance().getMessage("PERMISSION_DROP_ITEM"));
            }
        });
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent e){
        Player player = e.getPlayer();
        Optional<Claim> claim = ClaimManager.getClaims().stream().filter(c -> c.contains(e.getItem().getLocation())).findFirst();
        claim.ifPresent(c -> {
            if (c.isOwner(player.getUniqueId())){return;}
            if (!c.checkPermission(player.getUniqueId(), ClaimPermission.PICKUP_ITEM)){
                e.setCancelled(true);
                player.sendMessage(RClaim.getInstance().getMessage("PERMISSION_PICKUP_ITEM"));
            }
        });
    }
}
