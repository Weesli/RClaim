package net.weesli.rClaim.events;

import net.weesli.rClaim.RClaim;
import net.weesli.rClaim.api.events.ClaimEnterEvent;
import net.weesli.rClaim.api.events.ClaimLeaveEvent;
import net.weesli.rClaim.enums.ClaimStatus;
import net.weesli.rClaim.modal.ClaimEffect;
import net.weesli.rClaim.modal.ClaimTag;
import net.weesli.rClaim.utils.ClaimManager;
import net.weesli.rClaim.modal.Claim;
import net.weesli.rClaim.enums.ClaimPermission;
import net.weesli.rClaim.utils.TagManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WeatherType;
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
import org.bukkit.potion.PotionEffect;
import org.bukkit.projectiles.ProjectileSource;

import java.util.List;
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
        ClaimTag tag = TagManager.isPlayerInTag(e.getPlayer(), target_claim.getID());
        if (tag != null){
            if (tag.getPermissions().contains(ClaimPermission.BLOCK_PLACE)){
                return;
            }
        }
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
        ClaimTag tag = TagManager.isPlayerInTag(e.getPlayer(), c.getID());
        if (e.getBlock().getState() instanceof InventoryHolder){
            if (tag != null){
                if (tag.getPermissions().contains(ClaimPermission.BREAK_CONTAINER)){
                    return;
                }
            }
            if (!c.checkPermission(e.getPlayer().getUniqueId(),ClaimPermission.BREAK_CONTAINER)){
                e.setCancelled(true);
                e.getPlayer().sendMessage(RClaim.getInstance().getMessage("PERMISSION_BREAK_CONTAINER"));
            }
        }
        if (tag != null){
            if (tag.getPermissions().contains(ClaimPermission.BLOCK_BREAK)){
                return;
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
            ClaimTag tag = TagManager.isPlayerInTag(player, c.getID());
            if (tag != null){
                if (tag.getPermissions().contains(ClaimPermission.CONTAINER_OPEN)){
                    return;
                }
            }
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
            ClaimTag tag = TagManager.isPlayerInTag(player, c.getID());
            if (tag != null){
                if (tag.getPermissions().contains(ClaimPermission.USE_DOOR)){
                    return;
                }
            }
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
        ClaimTag tag = TagManager.isPlayerInTag(player, c.getID());
        if (tag != null){
            if (tag.getPermissions().contains(ClaimPermission.INTERACT_ENTITY)){
                return;
            }
        }
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
            ClaimTag tag = TagManager.isPlayerInTag(player, c.getID());
            if (e.getEntity() instanceof Monster){
                if (tag != null){
                    if (tag.getPermissions().contains(ClaimPermission.ATTACK_MONSTER)){
                        return;
                    }
                }
                if (!c.checkPermission(player.getUniqueId(), ClaimPermission.ATTACK_MONSTER)){
                    e.setCancelled(true);
                    player.sendMessage(RClaim.getInstance().getMessage("PERMISSION_ATTACK_MONSTER"));
                }
            }else {
                if (tag != null){
                    if (tag.getPermissions().contains(ClaimPermission.ATTACK_ANIMAL)){
                        return;
                    }
                }
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
        ClaimTag tag = TagManager.isPlayerInTag(player,c.getID());
        if (tag != null){
            if (tag.getPermissions().contains(ClaimPermission.DROP_ITEM)){
                return;
            }
        }
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
        ClaimTag tag = TagManager.isPlayerInTag(player,c.getID());
        if (tag != null){
            if (tag.getPermissions().contains(ClaimPermission.PICKUP_ITEM)){
                return;
            }
        }
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
            ClaimTag tag = TagManager.isPlayerInTag(player,c.getID());
            if (tag != null){
                if (tag.getPermissions().contains(ClaimPermission.USE_PORTAL)){
                    return;
                }
            }
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
        ClaimTag tag = TagManager.isPlayerInTag(player, c.getID());
        if (tag != null){
            if (tag.getPermissions().contains(ClaimPermission.USE_POTION)){
                return;
            }
        }
        if (!c.checkPermission(player.getUniqueId(), ClaimPermission.USE_POTION)){
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
            ClaimTag tag = TagManager.isPlayerInTag(player, c.getID());
            if (tag != null){
                if (tag.getPermissions().contains(ClaimPermission.USE_POTION)){
                    return;
                }
            }
            if (!c.checkPermission(player.getUniqueId(), ClaimPermission.USE_POTION)){
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

    // this place is for claim effects and Weather types and time

    @EventHandler
    public void onClaimEnter(ClaimEnterEvent e){
        Player player = e.getPlayer();
        Claim claim = (e.getClaim().isCenter() ? e.getClaim() : ClaimManager.getClaim(e.getClaim().getCenterId()).get());
        // for claim effects
        if (claim.isOwner(player.getUniqueId()) || claim.isMember(player.getUniqueId())){
            List<ClaimEffect> effects = claim.getEffects();
            for (ClaimEffect effect : effects){
                if (effect.isEnabled()){
                    player.addPotionEffect(new PotionEffect(effect.getEffect().getType(), 999999, effect.getLevel() - 1));
                }
            }
        }
        // for weather types
        if (claim.getClaimStatuses().contains(ClaimStatus.WEATHER)){
            player.setPlayerWeather(WeatherType.CLEAR);
        }
        // for time
        if (claim.getClaimStatuses().contains(ClaimStatus.TIME)){
            player.setPlayerTime(6000,false);
        }

        // for combat system

        if (RClaim.getInstance().getCombatManager() != null){
            if (RClaim.getInstance().getCombatManager().getCombat().isPvP(player)){
                player.setVelocity(player.getLocation().getDirection().normalize().multiply(-2).setY(0.5));
                player.sendMessage(RClaim.getInstance().getMessage("COMBAT_SYSTEM_MESSAGE"));
            }
        }
    }

    @EventHandler
    public void onClaimLeave(ClaimLeaveEvent e){
        Player player = e.getPlayer();
        Claim claim = (e.getClaim().isCenter() ? e.getClaim() : ClaimManager.getClaim(e.getClaim().getCenterId()).get());
        // for claim effects
        if (claim.isOwner(player.getUniqueId()) || claim.isMember(player.getUniqueId())){
            List<ClaimEffect> effects = claim.getEffects();
            for (ClaimEffect effect : effects){
                if (player.hasPotionEffect(effect.getEffect().getType())){
                    player.removePotionEffect(effect.getEffect().getType());
                }
            }
        }
        // for weather types
        if (claim.getClaimStatuses().contains(ClaimStatus.WEATHER)){
            player.resetPlayerWeather();
        }
        // for time
        if (claim.getClaimStatuses().contains(ClaimStatus.TIME)){
            player.resetPlayerTime();
        }
    }
}
