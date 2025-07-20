package net.weesli.rclaim.event;

import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.events.ClaimEnterEvent;
import net.weesli.rclaim.api.events.ClaimLeaveEvent;
import net.weesli.rclaim.api.model.Claim;
import net.weesli.rclaim.api.model.ClaimEffect;
import net.weesli.rclaim.api.model.ClaimTag;
import net.weesli.rclaim.api.enums.ClaimStatus;
import net.weesli.rclaim.hook.other.HWorldGuard;
import net.weesli.rclaim.util.ClaimBlockUtil;
import net.weesli.rclaim.util.BaseUtil;
import net.weesli.rclaim.api.enums.ClaimPermission;
import net.weesli.rozslib.events.PlayerDamageEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WeatherType;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlaceBlock(BlockPlaceEvent e){
        if (e.getPlayer().hasPermission("rclaim.admin.bypass")){return;}
        Claim claim = RClaim.getInstance().getClaimManager().getClaim(e.getBlock().getLocation().getChunk());
        if (claim == null){
            return;
        }
        if (claim.isOwner(e.getPlayer().getUniqueId())){return;}
        ClaimTag tag = RClaim.getInstance().getTagManager().isPlayerInTag(e.getPlayer(), claim.getID());
        if (tag != null){
            if (tag.getPermissions().contains(ClaimPermission.BLOCK_PLACE)){
                return;
            }
        }
        if (!claim.checkPermission(e.getPlayer().getUniqueId(), ClaimPermission.BLOCK_PLACE)){
            e.setCancelled(true);
            e.getPlayer().sendMessage(RClaim.getInstance().getMessage("PERMISSION_BLOCK_PLACE"));
        }
    }

    @EventHandler
    public void onBreakBlock(BlockBreakEvent e){
        if (e.getPlayer().hasPermission("rclaim.admin.bypass")){return;}
        Claim claim = RClaim.getInstance().getClaimManager().getClaim(e.getBlock().getLocation().getChunk());
        if (claim == null){return;}
        if (claim.isOwner(e.getPlayer().getUniqueId())){return;}
        ClaimTag tag = RClaim.getInstance().getTagManager().isPlayerInTag(e.getPlayer(), claim.getID());
        if (e.getBlock().getState() instanceof InventoryHolder || e.getBlock().getType().name().contains("ENDER_CHEST")){
            if (tag != null){
                if (tag.getPermissions().contains(ClaimPermission.BREAK_CONTAINER)){
                    return;
                }
            }
            if (!claim.checkPermission(e.getPlayer().getUniqueId(),ClaimPermission.BREAK_CONTAINER)){
                e.setCancelled(true);
                e.getPlayer().sendMessage(RClaim.getInstance().getMessage("PERMISSION_BREAK_CONTAINER"));
            }
        }
        if (tag != null){
            if (tag.getPermissions().contains(ClaimPermission.BLOCK_BREAK)){
                return;
            }
        }
        if (!claim.checkPermission(e.getPlayer().getUniqueId(), ClaimPermission.BLOCK_BREAK)){
            e.setCancelled(true);
            e.getPlayer().sendMessage(RClaim.getInstance().getMessage("PERMISSION_BLOCK_BREAK"));
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e){
        if (e.getPlayer().hasPermission("rclaim.admin.bypass")){return;}
        Player player = e.getPlayer();
        if (e.getClickedBlock() == null){return;}
        if (e.getClickedBlock().getState() instanceof InventoryHolder || e.getClickedBlock().getType().name().contains("ENDER_CHEST")){
            Claim claim = RClaim.getInstance().getClaimManager().getClaim(e.getPlayer().getLocation().getChunk());
            if (claim == null){
                return;
            }
            if (claim.isOwner(player.getUniqueId())){return;}
            ClaimTag tag = RClaim.getInstance().getTagManager().isPlayerInTag(player, claim.getID());
            if (tag != null){
                if (tag.getPermissions().contains(ClaimPermission.CONTAINER_OPEN)){
                    return;
                }
            }
            if (!claim.checkPermission(player.getUniqueId(), ClaimPermission.CONTAINER_OPEN)){
                e.setCancelled(true);
                player.sendMessage(RClaim.getInstance().getMessage("PERMISSION_CONTAINER_OPEN"));
            }
        }
        if (e.getClickedBlock().getType().name().contains("DOOR") ||
        e.getClickedBlock().getType().name().contains("GATE")){
            Claim claim = RClaim.getInstance().getClaimManager().getClaim(e.getPlayer().getLocation().getChunk());
            if (claim == null){
                return;
            }
            if (claim.isOwner(player.getUniqueId())){return;}
            ClaimTag tag = RClaim.getInstance().getTagManager().isPlayerInTag(player, claim.getID());
            if (tag != null){
                if (tag.getPermissions().contains(ClaimPermission.USE_DOOR)){
                    return;
                }
            }
            if (!claim.checkPermission(player.getUniqueId(), ClaimPermission.USE_DOOR)){
                e.setCancelled(true);
                player.sendMessage(RClaim.getInstance().getMessage("PERMISSION_DOOR_OPEN"));
            }
        }
    }

    @EventHandler
    public void onInteractEntity(PlayerInteractEntityEvent e){
        if (e.getPlayer().hasPermission("rclaim.admin.bypass")){return;}
        Player player = e.getPlayer();
        Claim claim = RClaim.getInstance().getClaimManager().getClaim(e.getPlayer().getLocation().getChunk());
        if (claim == null){
            return;
        }
        if (claim.isOwner(player.getUniqueId())){return;}
        ClaimTag tag = RClaim.getInstance().getTagManager().isPlayerInTag(player, claim.getID());
        if (tag != null){
            if (tag.getPermissions().contains(ClaimPermission.INTERACT_ENTITY)){
                return;
            }
        }
        if (!claim.checkPermission(player.getUniqueId(), ClaimPermission.INTERACT_ENTITY)){
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
            Claim claim = RClaim.getInstance().getClaimManager().getClaim(e.getEntity().getLocation().getChunk());
            if (claim == null){
                return;
            }
            if (claim.isOwner(player.getUniqueId())){return;}
            ClaimTag tag = RClaim.getInstance().getTagManager().isPlayerInTag(player, claim.getID());
            if (e.getEntity() instanceof Monster){
                if (tag != null){
                    if (tag.getPermissions().contains(ClaimPermission.ATTACK_MONSTER)){
                        return;
                    }
                }
                if (!claim.checkPermission(player.getUniqueId(), ClaimPermission.ATTACK_MONSTER)){
                    e.setCancelled(true);
                    player.sendMessage(RClaim.getInstance().getMessage("PERMISSION_ATTACK_MONSTER"));
                }
            }else {
                if (tag != null){
                    if (tag.getPermissions().contains(ClaimPermission.ATTACK_ANIMAL)){
                        return;
                    }
                }
                if (!claim.checkPermission(player.getUniqueId(), ClaimPermission.ATTACK_ANIMAL)){
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
        Claim claim = RClaim.getInstance().getClaimManager().getClaim(e.getPlayer().getLocation().getChunk());
        if (claim == null){
            return;
        }
        if (claim.isOwner(player.getUniqueId())){return;}
        ClaimTag tag = RClaim.getInstance().getTagManager().isPlayerInTag(player, claim.getID());
        if (tag != null){
            if (tag.getPermissions().contains(ClaimPermission.DROP_ITEM)){
                return;
            }
        }
        if (!claim.checkPermission(player.getUniqueId(), ClaimPermission.DROP_ITEM)){
            e.setCancelled(true);
            player.sendMessage(RClaim.getInstance().getMessage("PERMISSION_DROP_ITEM"));
        }
    }

    @EventHandler
    public void onBreak(PlayerInteractEntityEvent e){

    }

    private final Map<UUID, Long> pickupCooldowns = new HashMap<>();
    private final long COOLDOWN_MS = 5000;

    @EventHandler
    public void onPickup(EntityPickupItemEvent e){
        if (!(e.getEntity() instanceof Player player)){
            return;
        }
        if (player.hasPermission("rclaim.admin.bypass")){return;}
        Claim claim = RClaim.getInstance().getClaimManager().getClaim(player.getLocation().getChunk());
        if (claim == null){
            return;
        }
        if (claim.isOwner(player.getUniqueId())){return;}
        if (!claim.isMember(player.getUniqueId())){
            e.setCancelled(true);
            UUID uuid = player.getUniqueId();
            long now = System.currentTimeMillis();
            if (pickupCooldowns.containsKey(uuid)) {
                long last = pickupCooldowns.getOrDefault(uuid, 0L);
                if ((now - last) < COOLDOWN_MS) return;
            }
            pickupCooldowns.put(uuid, now);
            player.sendMessage(RClaim.getInstance().getMessage("PERMISSION_PICKUP_ITEM"));
        }
        ClaimTag tag = RClaim.getInstance().getTagManager().isPlayerInTag(player, claim.getID());
        if (tag != null){
            if (tag.getPermissions().contains(ClaimPermission.PICKUP_ITEM)){
                return;
            }
        }
        if (!claim.checkPermission(player.getUniqueId(), ClaimPermission.PICKUP_ITEM)){
            e.setCancelled(true);
            UUID uuid = player.getUniqueId();
            long now = System.currentTimeMillis();
            if (pickupCooldowns.containsKey(uuid)) {
                long last = pickupCooldowns.getOrDefault(uuid, 0L);
                if ((now - last) < COOLDOWN_MS) return;
            }
            player.sendMessage(RClaim.getInstance().getMessage("PERMISSION_PICKUP_ITEM"));
            pickupCooldowns.put(uuid, now);
        }
    }
    @EventHandler
    public void onPortal(PlayerTeleportEvent e){
        if (e.getCause().equals(PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) || e.getCause().equals(PlayerTeleportEvent.TeleportCause.END_PORTAL)){
            if (e.getPlayer().hasPermission("rclaim.admin.bypass")){return;}
            Player player = e.getPlayer();
            Claim claim = RClaim.getInstance().getClaimManager().getClaim(e.getPlayer().getLocation().getChunk());
            if (claim == null){
                return;
            }
            if (claim.isOwner(player.getUniqueId())){return;}
            ClaimTag tag = RClaim.getInstance().getTagManager().isPlayerInTag(player, claim.getID());
            if (tag != null){
                if (tag.getPermissions().contains(ClaimPermission.USE_PORTAL)){
                    return;
                }
            }
            if (!claim.checkPermission(player.getUniqueId(), ClaimPermission.USE_PORTAL)){
                e.setCancelled(true);
                player.sendMessage(RClaim.getInstance().getMessage("PERMISSION_ENTER_PORTAL"));
            }
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e){ // this event for if player teleport from claim, called ClaimLeaveEvent or ClaimEnterEvent
        // for claim leave
        Player player = e.getPlayer();
        Claim leavedClaim = RClaim.getInstance().getClaimManager().getClaim(e.getFrom());
        if (leavedClaim != null){
            ClaimLeaveEvent event = new ClaimLeaveEvent(leavedClaim, player);
            RClaim.getInstance().getServer().getPluginManager().callEvent(event);
        }
        // for claim enter
        Location to = e.getTo();
        if (to == null){
            return;
        }
        Claim enteredClaim = RClaim.getInstance().getClaimManager().getClaim(to);
        if (enteredClaim != null){
            ClaimEnterEvent event = new ClaimEnterEvent(enteredClaim, player);
            RClaim.getInstance().getServer().getPluginManager().callEvent(event);
        }
    }

    @EventHandler
    public void onJoinServer(PlayerJoinEvent e){
        Player player = e.getPlayer();
        Claim claim = RClaim.getInstance().getClaimManager().getClaim(player.getLocation().getChunk());
        if (claim == null){
            return;
        }
        ClaimEnterEvent event = new ClaimEnterEvent(claim, player);
        RClaim.getInstance().getServer().getPluginManager().callEvent(event);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e){
        Player player = e.getPlayer();
        Claim claim = RClaim.getInstance().getClaimManager().getClaim(player.getLocation().getChunk());
        if (claim == null){
            return;
        }
        Bukkit.getScheduler().runTaskLater(RClaim.getInstance(), () -> {
            ClaimEnterEvent event = new ClaimEnterEvent(claim, player);
            RClaim.getInstance().getServer().getPluginManager().callEvent(event);
            },20L);
    }

    @EventHandler
    public void onQuitServer(PlayerQuitEvent e){
        Player player = e.getPlayer();
        Claim claim = RClaim.getInstance().getClaimManager().getClaim(player.getLocation().getChunk());
        if (claim == null){
            return;
        }
        ClaimLeaveEvent event = new ClaimLeaveEvent(claim, player);
        RClaim.getInstance().getServer().getPluginManager().callEvent(event);
    }

    @EventHandler
    public void onPotion(PotionSplashEvent e){
        ProjectileSource source = e.getPotion().getShooter();
        if (!(source instanceof Player player)) {
            return;
        }
        if (player.hasPermission("rclaim.admin.bypass")){return;}
        Claim claim = RClaim.getInstance().getClaimManager().getClaim(player.getLocation().getChunk());
        if (claim == null){
            return;
        }
        if (claim.isOwner(player.getUniqueId())){return;}
        ClaimTag tag = RClaim.getInstance().getTagManager().isPlayerInTag(player, claim.getID());
        if (tag != null){
            if (tag.getPermissions().contains(ClaimPermission.USE_POTION)){
                return;
            }
        }
        if (!claim.checkPermission(player.getUniqueId(), ClaimPermission.USE_POTION)){
            e.setCancelled(true);
            // give again potion to player
            ItemStack itemStack = e.getEntity().getItem();
            player.getInventory().addItem(itemStack);
            player.sendMessage(RClaim.getInstance().getMessage("PERMISSION_USE_POTION"));
        }
    }

    @EventHandler
    public void onLingerEvent(LingeringPotionSplashEvent e){
        ProjectileSource source = e.getEntity().getShooter();
        if (!(source instanceof Player player)) {
            return;
        }
        if (player.hasPermission("rclaim.admin.bypass")){return;}
        Claim claim = RClaim.getInstance().getClaimManager().getClaim(player.getLocation().getChunk());
        if (claim == null){
            return;
        }
        if (claim.isOwner(player.getUniqueId())){return;}
        ClaimTag tag = RClaim.getInstance().getTagManager().isPlayerInTag(player, claim.getID());
        if (tag != null){
            if (tag.getPermissions().contains(ClaimPermission.USE_POTION)){
                return;
            }
        }
        if (!claim.checkPermission(player.getUniqueId(), ClaimPermission.USE_POTION)){
            e.setCancelled(true);
            // give again potion to player
            ItemStack itemStack = e.getEntity().getItem();
            player.getInventory().addItem(itemStack);
            player.sendMessage(RClaim.getInstance().getMessage("PERMISSION_USE_POTION"));
        }
    }

    @EventHandler
    public void Consume(PlayerItemConsumeEvent e){
        if (e.getItem().getType().equals(Material.POTION)){
            Player player = e.getPlayer();
            if (player.hasPermission("rclaim.admin.bypass")){return;}
            Claim claim = RClaim.getInstance().getClaimManager().getClaim(e.getPlayer().getLocation().getChunk());
            if (claim == null){
                return;
            }
            if (claim.isOwner(player.getUniqueId())){return;}
            ClaimTag tag = RClaim.getInstance().getTagManager().isPlayerInTag(player, claim.getID());
            if (tag != null){
                if (tag.getPermissions().contains(ClaimPermission.USE_POTION)){
                    return;
                }
            }
            if (!claim.checkPermission(player.getUniqueId(), ClaimPermission.USE_POTION)){
                e.setCancelled(true);
                player.sendMessage(RClaim.getInstance().getMessage("PERMISSION_USE_POTION"));
            }
        }
    }


    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        Location from = e.getFrom();
        Location to = e.getTo();

        if (from.getChunk().equals(to.getChunk())) {
            return;
        }

        Optional<Claim> leavedChunk = Optional.ofNullable(RClaim.getInstance().getClaimManager().getClaim(from));
        Optional<Claim> enteredChunk = Optional.ofNullable(RClaim.getInstance().getClaimManager().getClaim(to));

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
        Claim claim = e.getClaim();
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
            if (RClaim.getInstance().getCombatManager().getCombatIntegration().isPvP(player)){
                player.setVelocity(player.getLocation().getDirection().normalize().multiply(-2).setY(0.5));
                player.sendMessage(RClaim.getInstance().getMessage("COMBAT_SYSTEM_MESSAGE"));
            }
        }
    }

    @EventHandler
    public void onClaimLeave(ClaimLeaveEvent e){
        Player player = e.getPlayer();
        Claim claim = e.getClaim();
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

    // added 2.2.0 for claim block listener
    @EventHandler
    public void onPlaceClaimBlock(BlockPlaceEvent e){
        if (!ClaimBlockUtil.isEnabled()) return;
        Player player = e.getPlayer();
        ItemStack itemStack = e.getItemInHand();
        if (itemStack.getType().equals(Material.AIR)) return;
        boolean isSimilar = ClaimBlockUtil.isSimilar(itemStack);
        if (!isSimilar) return;
        e.setCancelled(true);
        // check area status
        if (!HWorldGuard.isAreaEnabled(player)){
            player.sendMessage(RClaim.getInstance().getMessage("AREA_DISABLED"));
            return;
        }
        if(!BaseUtil.isActiveWorld(player.getWorld().getName())){
            player.sendMessage(RClaim.getInstance().getMessage("NOT_IN_CLAIMABLE_WORLD"));
            return;
        }
        if (RClaim.getInstance().getClaimManager().isSuitable(player.getLocation().getChunk())){
            player.sendMessage(RClaim.getInstance().getMessage("IS_NOT_SUITABLE"));
            return;
        }
        // register a new claim in placed location
        RClaim.getInstance().getClaimManager().createClaim(e.getBlockPlaced().getChunk(), player);
        itemStack.setAmount(itemStack.getAmount()-1);
        e.getPlayer().getInventory().setItemInMainHand(itemStack);
        player.sendMessage(RClaim.getInstance().getMessage("SUCCESS_CLAIM_CREATED"));
    }

    // this area for liquid blocks place and fill event
    // if player is claim not owner or member, player don't fill or place any liquid
    @EventHandler
    public void onFluidPlaceEvent(PlayerBucketEmptyEvent e) {
        if(e.getPlayer().hasPermission("rclaim.admin.bypass")){return;}
        Location loc = e.getBlockClicked().getLocation();
        Claim claim = RClaim.getInstance().getClaimManager().getClaim(loc);
        if (claim == null) {
            return;
        }
        if (!claim.isOwner(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
        }
        if (claim.isOwner(e.getPlayer().getUniqueId())){return;}
        ClaimTag tag = RClaim.getInstance().getTagManager().isPlayerInTag(e.getPlayer(), claim.getID());
        if (tag != null){
            if (tag.getPermissions().contains(ClaimPermission.BLOCK_PLACE)){
                return;
            }
        }
        if (!claim.checkPermission(e.getPlayer().getUniqueId(), ClaimPermission.BLOCK_PLACE)){
            e.setCancelled(true);
            e.getPlayer().sendMessage(RClaim.getInstance().getMessage("PERMISSION_BLOCK_PLACE"));
        }
    }
    @EventHandler
    public void onFluidPlaceEvent(PlayerBucketFillEvent e) {
        if(e.getPlayer().hasPermission("rclaim.admin.bypass")){return;}
        if (!e.getBlockClicked().getType().equals(Material.LAVA) || e.getBlockClicked().getType().equals(Material.WATER)){ {
            return;
        }
        }
        Location loc = e.getBlockClicked().getLocation();
        Claim claim = RClaim.getInstance().getClaimManager().getClaim(loc);
        if (claim == null) {
            return;
        }
        if (!claim.isOwner(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
        }
        if (claim.isOwner(e.getPlayer().getUniqueId())){return;}
        ClaimTag tag = RClaim.getInstance().getTagManager().isPlayerInTag(e.getPlayer(), claim.getID());
        if (tag != null){
            if (tag.getPermissions().contains(ClaimPermission.BLOCK_BREAK)){
                return;
            }
        }
        if (!claim.checkPermission(e.getPlayer().getUniqueId(), ClaimPermission.BLOCK_BREAK)){
            e.setCancelled(true);
            e.getPlayer().sendMessage(RClaim.getInstance().getMessage("PERMISSION_BLOCK_BREAK"));
        }
    }

    @EventHandler
    public void onPlayerDamageByEntity(EntityDamageEvent e){
        if (!(e.getEntity() instanceof Player player)){
            return;
        }
        if (player.hasPermission("rclaim.admin.bypass")){return;}
        Claim claim = RClaim.getInstance().getClaimManager().getClaim(player.getLocation().getChunk());
        if (claim == null){
            return;
        }
        if (claim.isOwner(player.getUniqueId())){return;}
        ClaimTag tag = RClaim.getInstance().getTagManager().isPlayerInTag(player, claim.getID());
        if (tag != null){
            if (tag.getPermissions().contains(ClaimPermission.ATTACK_MONSTER)){
                return;
            }
        }
        if (!claim.checkPermission(player.getUniqueId(), ClaimPermission.ATTACK_MONSTER)){
            e.setCancelled(true);
        }
    }
}
