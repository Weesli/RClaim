package net.weesli.rClaim.events;

import net.weesli.rClaim.RClaim;
import net.weesli.rClaim.api.events.ClaimEnterEvent;
import net.weesli.rClaim.api.events.ClaimLeaveEvent;
import net.weesli.rClaim.utils.ClaimManager;
import net.weesli.rClaim.modal.Claim;
import net.weesli.rClaim.enums.ClaimPermission;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.projectiles.ProjectileSource;

import java.util.Optional;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlaceBlock(BlockPlaceEvent e){
        if (e.getPlayer().hasPermission("rclaim.admin.bypass")){return;}
        Optional<Claim> claim = ClaimManager.getClaims().stream().filter(c -> c.contains(e.getBlock().getLocation())).findFirst();
        if (claim.isEmpty()){
            return;
        }
        Claim target_claim = (claim.get().getCenterId().isEmpty() ? claim.get() : ClaimManager.getClaim(claim.get().getCenterId()).get());
        if (target_claim.isOwner(e.getPlayer().getUniqueId())){return;}
        if (!target_claim.checkPermission(e.getPlayer().getUniqueId(), ClaimPermission.BLOCK_PLACE)){
            e.setCancelled(true);
            e.getPlayer().sendMessage(RClaim.getInstance().getMessage("PERMISSION_BLOCK_PLACE"));
        }
    }

    @EventHandler
    public void onBreakBlock(BlockBreakEvent e){
        if (e.getPlayer().hasPermission("rclaim.admin.bypass")){return;}
        Optional<Claim> claim = ClaimManager.getClaims().stream().filter(c -> c.contains(e.getBlock().getLocation())).findFirst();
        if (claim.isEmpty()){
            return;
        }
        Claim c = (claim.get().getCenterId().isEmpty() ? claim.get() : ClaimManager.getClaim(claim.get().getCenterId()).get());
        if (c.isOwner(e.getPlayer().getUniqueId())){return;}
        if (e.getBlock().getState() instanceof InventoryHolder){
            if (!c.checkPermission(e.getPlayer().getUniqueId(),ClaimPermission.BREAK_CONTAINER)){
                e.setCancelled(true);
                e.getPlayer().sendMessage(RClaim.getInstance().getMessage("PERMISSION_BREAK_CONTAINER"));
            }
        }
        if (!c.checkPermission(e.getPlayer().getUniqueId(), ClaimPermission.BLOCK_BREAK)){
            e.setCancelled(true);
            e.getPlayer().sendMessage(RClaim.getInstance().getMessage("PERMISSION_BLOCK_BREAK"));
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e){
        if (e.getPlayer().hasPermission("rclaim.admin.bypass")){return;}
        Player player = e.getPlayer();
        if (e.getClickedBlock() == null){return;}
        if (e.getClickedBlock().getState() instanceof InventoryHolder){
            Optional<Claim> claim = ClaimManager.getClaims().stream().filter(c -> c.contains(e.getClickedBlock().getLocation())).findFirst();
            if (claim.isEmpty()){
                return;
            }
            Claim c = (claim.get().getCenterId().isEmpty() ? claim.get() : ClaimManager.getClaim(claim.get().getCenterId()).get());
            if (c.isOwner(player.getUniqueId())){return;}
            if (!c.checkPermission(player.getUniqueId(), ClaimPermission.CONTAINER_OPEN)){
                e.setCancelled(true);
                player.sendMessage(RClaim.getInstance().getMessage("PERMISSION_CONTAINER_OPEN"));
            }
        }
        if (e.getClickedBlock().getType().name().contains("DOOR")){
            Optional<Claim> claim = ClaimManager.getClaims().stream().filter(c -> c.contains(e.getClickedBlock().getLocation())).findFirst();
            if (claim.isEmpty()){
                return;
            }
            Claim c = (claim.get().getCenterId().isEmpty() ? claim.get() : ClaimManager.getClaim(claim.get().getCenterId()).get());
            if (c.isOwner(player.getUniqueId())){return;}
            if (!c.checkPermission(player.getUniqueId(), ClaimPermission.USE_DOOR)){
                e.setCancelled(true);
                player.sendMessage(RClaim.getInstance().getMessage("PERMISSION_DOOR_OPEN"));
            }
        }
    }

    @EventHandler
    public void onInteractEntity(PlayerInteractEntityEvent e){
        if (e.getPlayer().hasPermission("rclaim.admin.bypass")){return;}
        Player player = e.getPlayer();
        Optional<Claim> claim = ClaimManager.getClaims().stream().filter(c-> c.contains(e.getRightClicked().getLocation())).findFirst();
        if (claim.isEmpty()){
            return;
        }
        Claim c = (claim.get().getCenterId().isEmpty() ? claim.get() : ClaimManager.getClaim(claim.get().getCenterId()).get());
        if (c.isOwner(player.getUniqueId())){return;}
        if (!c.checkPermission(player.getUniqueId(), ClaimPermission.INTERACT_ENTITY)){
            e.setCancelled(true);
            player.sendMessage(RClaim.getInstance().getMessage("PERMISSION_ENTITY_INTERACT"));
        }
    }

    @EventHandler
    public void onAttackEntity(EntityDamageByEntityEvent e){
        if (e.getEntity().getType() == EntityType.PLAYER){return;}
        if (e.getDamager() instanceof Player){
            Player player = (Player) e.getDamager();
            if (player.hasPermission("rclaim.admin.bypass")){return;}
            Optional<Claim> claim = ClaimManager.getClaims().stream().filter(c -> c.contains(e.getEntity().getLocation())).findFirst();
            if (claim.isEmpty()){
                return;
            }
            Claim c = (claim.get().getCenterId().isEmpty() ? claim.get() : ClaimManager.getClaim(claim.get().getCenterId()).get());
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
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e){
        if (e.getPlayer().hasPermission("rclaim.admin.bypass")){return;}
        Player player = e.getPlayer();
        Optional<Claim> claim = ClaimManager.getClaims().stream().filter(c -> c.contains(e.getItemDrop().getLocation())).findFirst();
        if (claim.isEmpty()){
            return;
        }
        Claim c = (claim.get().getCenterId().isEmpty() ? claim.get() : ClaimManager.getClaim(claim.get().getCenterId()).get());
        if (c.isOwner(player.getUniqueId())){return;}
        if (!c.checkPermission(player.getUniqueId(), ClaimPermission.DROP_ITEM)){
            e.setCancelled(true);
            player.sendMessage(RClaim.getInstance().getMessage("PERMISSION_DROP_ITEM"));
        }
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent e){
        if (e.getPlayer().hasPermission("rclaim.admin.bypass")){return;}
        Player player = e.getPlayer();
        Optional<Claim> claim = ClaimManager.getClaims().stream().filter(c -> c.contains(e.getItem().getLocation())).findFirst();
        if (claim.isEmpty()){
            return;
        }
        Claim c = (claim.get().getCenterId().isEmpty() ? claim.get() : ClaimManager.getClaim(claim.get().getCenterId()).get());
        if (c.isOwner(player.getUniqueId())){return;}
        if (!c.isMember(player.getUniqueId())){return;}
        if (!c.checkPermission(player.getUniqueId(), ClaimPermission.PICKUP_ITEM)){
            e.setCancelled(true);
            player.sendMessage(RClaim.getInstance().getMessage("PERMISSION_PICKUP_ITEM"));
        }
    }

    @EventHandler
    public void onPortal(PlayerTeleportEvent e){
        if (e.getCause().equals(PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) || e.getCause().equals(PlayerTeleportEvent.TeleportCause.END_PORTAL)){
            if (e.getPlayer().hasPermission("rclaim.admin.bypass")){return;}
            Player player = e.getPlayer();
            Optional<Claim> claim = ClaimManager.getClaims().stream().filter(c -> c.contains(player.getLocation())).findFirst();
            if (claim.isEmpty()){
                return;
            }
            Claim c = (claim.get().getCenterId().isEmpty() ? claim.get() : ClaimManager.getClaim(claim.get().getCenterId()).get());
            if (c.isOwner(player.getUniqueId())){return;}
            if (!c.checkPermission(player.getUniqueId(), ClaimPermission.USE_PORTAL)){
                e.setCancelled(true);
                player.sendMessage(RClaim.getInstance().getMessage("PERMISSION_ENTER_PORTAL"));
            }
        }
    }

    @EventHandler
    public void onPotion(PotionSplashEvent e){
        ProjectileSource source = e.getPotion().getShooter();
        if (!(source instanceof Player)) {
            return;
        }
        Player player = (Player) source;
        if (player.hasPermission("rclaim.admin.bypass")){return;}
        Optional<Claim> claim = ClaimManager.getClaims().stream().filter(c -> c.contains(player.getLocation())).findFirst();
        if (claim.isEmpty()){
            return;
        }
        Claim c = (claim.get().getCenterId().isEmpty() ? claim.get() : ClaimManager.getClaim(claim.get().getCenterId()).get());
        if (c.isOwner(player.getUniqueId())){return;}
        if (!c.checkPermission(player.getUniqueId(), ClaimPermission.USE_PORTAL)){
            e.setCancelled(true);
            player.sendMessage(RClaim.getInstance().getMessage("PERMISSION_USE_POTION"));
        }
    }

    @EventHandler
    public void Consume(PlayerItemConsumeEvent e){
        if (e.getItem().getType().equals(Material.POTION)){
            Player player = e.getPlayer();
            if (player.hasPermission("rclaim.admin.bypass")){return;}
            Optional<Claim> claim = ClaimManager.getClaims().stream().filter(c -> c.contains(player.getLocation())).findFirst();
            if (claim.isEmpty()){
                return;
            }
            Claim c = (claim.get().getCenterId().isEmpty() ? claim.get() : ClaimManager.getClaim(claim.get().getCenterId()).get());
            if (c.isOwner(player.getUniqueId())){return;}
            if (!c.checkPermission(player.getUniqueId(), ClaimPermission.USE_PORTAL)){
                e.setCancelled(true);
                player.sendMessage(RClaim.getInstance().getMessage("PERMISSION_USE_POTION"));
            }
        }
    }


    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (!RClaim.getInstance().getConfig().getBoolean("options.enter-message.enabled")){
            return;
        }
        Player player = e.getPlayer();
        Location from = e.getFrom();
        Location to = e.getTo();

        if (from.getChunk().equals(to.getChunk())) {
            return;
        }

        Optional<Claim> leavedChunk = ClaimManager.getClaims().stream()
                .filter(c -> c.getChunk().getX() == from.getChunk().getX() && c.getChunk().getZ() == from.getChunk().getZ())
                .findFirst();
        Optional<Claim> enteredChunk = ClaimManager.getClaims().stream()
                .filter(c -> c.getChunk().getX() == to.getChunk().getX() && c.getChunk().getZ() == to.getChunk().getZ())
                .findFirst();

        if (leavedChunk.isPresent() && enteredChunk.isPresent()) {
            if (leavedChunk.get().isOwner(enteredChunk.get().getOwner())) {
                return;
            }
        }
        if (!leavedChunk.equals(enteredChunk)) {
            if (leavedChunk.isPresent()) {
                ClaimLeaveEvent event = new ClaimLeaveEvent(leavedChunk.get(), player);
                Bukkit.getPluginManager().callEvent(event);
            }

            if (enteredChunk.isPresent()) {
                ClaimEnterEvent event = new ClaimEnterEvent(enteredChunk.get(), player);
                Bukkit.getPluginManager().callEvent(event);

            }
        }
    }


}
