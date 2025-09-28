package net.weesli.rclaim.event;

import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.permission.ClaimPermissionRegistry;
import net.weesli.rclaim.api.permission.ClaimPermissionService;
import org.bukkit.Material;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.LingeringPotionSplashEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.InventoryHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static net.weesli.rclaim.config.lang.LangConfig.sendMessageToPlayer;

public class ClaimPermissionListener {

    private final ClaimPermissionService permissionService;

    public ClaimPermissionListener() {
        this.permissionService = RClaim.getInstance().getPermissionService();
        permissionService.registerPermission(new BlockBreakPermission(permissionService));
        permissionService.registerPermission(new BlockPlacePermission(permissionService));
        permissionService.registerPermission(new ContainerOpenPermission(permissionService));
        permissionService.registerPermission(new DoorUsePermission(permissionService));
        permissionService.registerPermission(new InteractEntityPermission(permissionService));
        permissionService.registerPermission(new AttackMonsterPermission(permissionService));
        permissionService.registerPermission(new AttackAnimalPermission(permissionService));
        permissionService.registerPermission(new DropItemPermission(permissionService));
        permissionService.registerPermission(new PickupItemPermission(permissionService));
        permissionService.registerPermission(new PortalUsePermission(permissionService));
        permissionService.registerPermission(new PotionUsePermission(permissionService));
        permissionService.registerPermission(new BucketEmptyPermission(permissionService));
        permissionService.registerPermission(new BucketFillPermission(permissionService));
        permissionService.registerPermission(new PlayerDamagePermission(permissionService));
    }

    public static class BlockBreakPermission extends ClaimPermissionRegistry {
        protected BlockBreakPermission(ClaimPermissionService svc) { super(svc, "BLOCK_BREAK"); }
        @EventHandler(ignoreCancelled = true)
        public void on(BlockBreakEvent e) {
            if (e.getPlayer().hasPermission("rclaim.admin.bypass")) return;
            if (!internalCheck(e.getPlayer(), e.getBlock().getLocation())) {
                e.setCancelled(true);
                sendMessageToPlayer("PERMISSION_BLOCK_BREAK", e.getPlayer());
            }
        }
    }

    public static class BlockPlacePermission extends ClaimPermissionRegistry {
        protected BlockPlacePermission(ClaimPermissionService svc) { super(svc, "BLOCK_PLACE"); }
        @EventHandler(ignoreCancelled = true)
        public void on(BlockPlaceEvent e) {
            if (e.getPlayer().hasPermission("rclaim.admin.bypass")) return;
            if (!internalCheck(e.getPlayer(), e.getBlock().getLocation())) {
                e.setCancelled(true);
                sendMessageToPlayer("PERMISSION_BLOCK_PLACE", e.getPlayer());
            }
        }
    }

    public static class ContainerOpenPermission extends ClaimPermissionRegistry {
        protected ContainerOpenPermission(ClaimPermissionService svc) { super(svc, "CONTAINER_OPEN"); }
        @EventHandler(ignoreCancelled = true)
        public void on(PlayerInteractEvent e) {
            if (e.getClickedBlock() == null) return;
            if (e.getClickedBlock().getState() instanceof InventoryHolder || e.getClickedBlock().getType().name().contains("ENDER_CHEST")){
                if (e.getPlayer().hasPermission("rclaim.admin.bypass")) return;
                if (!internalCheck(e.getPlayer(), e.getClickedBlock().getLocation())) {
                    e.setCancelled(true);
                    sendMessageToPlayer("PERMISSION_CONTAINER_OPEN", e.getPlayer());
                }
            }
        }
    }

    public static class DoorUsePermission extends ClaimPermissionRegistry {
        protected DoorUsePermission(ClaimPermissionService svc) { super(svc, "USE_DOOR"); }
        @EventHandler(ignoreCancelled = true)
        public void on(PlayerInteractEvent e) {
            if (!e.getClickedBlock().getType().name().contains("DOOR") && !e.getClickedBlock().getType().name().contains("TRAPDOOR")) return;
            if (e.getPlayer().hasPermission("rclaim.admin.bypass")) return;
            if (e.getClickedBlock() == null) return;
            if (!internalCheck(e.getPlayer(), e.getClickedBlock().getLocation())) {
                e.setCancelled(true);
                sendMessageToPlayer("PERMISSION_DOOR_OPEN", e.getPlayer());
            }
        }
    }

    public static class InteractEntityPermission extends ClaimPermissionRegistry {
        protected InteractEntityPermission(ClaimPermissionService svc) { super(svc, "INTERACT_ENTITY"); }
        @EventHandler(ignoreCancelled = true)
        public void on(PlayerInteractEntityEvent e) {
            if (e.getPlayer().hasPermission("rclaim.admin.bypass")) return;
            if (!internalCheck(e.getPlayer(), e.getRightClicked().getLocation())) {
                e.setCancelled(true);
                sendMessageToPlayer("PERMISSION_ENTITY_INTERACT", e.getPlayer());
            }
        }
    }

    public static class AttackMonsterPermission extends ClaimPermissionRegistry {
        protected AttackMonsterPermission(ClaimPermissionService svc) { super(svc, "ATTACK_MONSTER"); }
        @EventHandler(ignoreCancelled = true)
        public void on(EntityDamageByEntityEvent e) {
            if (!(e.getDamager() instanceof Player p)) return;
            if (!(e.getEntity() instanceof Monster)) return;
            if (p.hasPermission("rclaim.admin.bypass")) return;
            if (!internalCheck(p, e.getEntity().getLocation())) {
                e.setCancelled(true);
                sendMessageToPlayer("PERMISSION_ATTACK_MONSTER", p);
            }
        }
    }

    public static class AttackAnimalPermission extends ClaimPermissionRegistry {
        protected AttackAnimalPermission(ClaimPermissionService svc) { super(svc, "ATTACK_ANIMAL"); }
        @EventHandler(ignoreCancelled = true)
        public void on(EntityDamageByEntityEvent e) {
            if (!(e.getDamager() instanceof Player p)) return;
            if (e.getEntity() instanceof Monster) return;
            if (p.hasPermission("rclaim.admin.bypass")) return;
            if (!internalCheck(p, e.getEntity().getLocation())) {
                e.setCancelled(true);
                sendMessageToPlayer("PERMISSION_ATTACK_ANIMAL", p);
            }
        }
    }

    public static class DropItemPermission extends ClaimPermissionRegistry {
        protected DropItemPermission(ClaimPermissionService svc) { super(svc, "DROP_ITEM"); }
        @EventHandler(ignoreCancelled = true)
        public void on(PlayerDropItemEvent e) {
            if (e.getPlayer().hasPermission("rclaim.admin.bypass")) return;
            if (!internalCheck(e.getPlayer(), e.getPlayer().getLocation())) {
                e.setCancelled(true);
                sendMessageToPlayer("PERMISSION_DROP_ITEM", e.getPlayer());
            }
        }
    }

    public static class PickupItemPermission extends ClaimPermissionRegistry {
        private final Map<UUID, Long> cooldowns = new HashMap<>();
        protected PickupItemPermission(ClaimPermissionService svc) { super(svc, "PICKUP_ITEM"); }
        @EventHandler(ignoreCancelled = true)
        public void on(EntityPickupItemEvent e) {
            if (!(e.getEntity() instanceof Player p)) return;
            if (p.hasPermission("rclaim.admin.bypass")) return;

            if (!internalCheck(p, p.getLocation())) {
                e.setCancelled(true);

                long now = System.currentTimeMillis();
                long last = cooldowns.getOrDefault(p.getUniqueId(), 0L);

                if (now - last >= 5000) {
                    sendMessageToPlayer("PERMISSION_PICKUP_ITEM", p);
                    cooldowns.put(p.getUniqueId(), now);
                }
            }
        }
    }

    public static class PortalUsePermission extends ClaimPermissionRegistry {
        protected PortalUsePermission(ClaimPermissionService svc) { super(svc, "USE_PORTAL"); }
        @EventHandler(ignoreCancelled = true)
        public void on(org.bukkit.event.player.PlayerTeleportEvent e) {
            if (!(e.getCause().equals(org.bukkit.event.player.PlayerTeleportEvent.TeleportCause.NETHER_PORTAL)
                    || e.getCause().equals(org.bukkit.event.player.PlayerTeleportEvent.TeleportCause.END_PORTAL))) return;
            if (e.getPlayer().hasPermission("rclaim.admin.bypass")) return;
            if (!internalCheck(e.getPlayer(), e.getFrom())) {
                e.setCancelled(true);
                sendMessageToPlayer("PERMISSION_ENTER_PORTAL", e.getPlayer());
            }
        }
    }

    public static class PotionUsePermission extends ClaimPermissionRegistry {
        protected PotionUsePermission(ClaimPermissionService svc) { super(svc, "USE_POTION"); }
        @EventHandler(ignoreCancelled = true)
        public void on(PotionSplashEvent e) {
            if (!(e.getPotion().getShooter() instanceof Player p)) return;
            if (p.hasPermission("rclaim.admin.bypass")) return;
            if (!internalCheck(p, (e.getHitBlock() == null ? e.getHitEntity().getLocation() : e.getHitBlock().getLocation()))) {
                e.setCancelled(true);
                p.getInventory().addItem(e.getEntity().getItem());
                sendMessageToPlayer("PERMISSION_USE_POTION", p);
            }
        }
        @EventHandler(ignoreCancelled = true)
        public void on(LingeringPotionSplashEvent e) {
            if (!(e.getEntity().getShooter() instanceof Player p)) return;
            if (p.hasPermission("rclaim.admin.bypass")) return;
            if (!internalCheck(p, (e.getHitBlock() == null ? e.getHitEntity().getLocation() : e.getHitBlock().getLocation()))) {
                e.setCancelled(true);
                p.getInventory().addItem(e.getEntity().getItem());
                sendMessageToPlayer("PERMISSION_USE_POTION", p);
            }
        }
        @EventHandler(ignoreCancelled = true)
        public void on(PlayerItemConsumeEvent e) {
            if (!e.getItem().getType().equals(Material.POTION)) return;
            if (e.getPlayer().hasPermission("rclaim.admin.bypass")) return;
            if (!internalCheck(e.getPlayer(), e.getPlayer().getLocation())) {
                e.setCancelled(true);
                sendMessageToPlayer("PERMISSION_USE_POTION", e.getPlayer());
            }
        }
    }

    public static class BucketEmptyPermission extends ClaimPermissionRegistry {
        protected BucketEmptyPermission(ClaimPermissionService svc) { super(svc, "BUCKET_EMPTY"); }
        @EventHandler(ignoreCancelled = true)
        public void on(PlayerBucketEmptyEvent e) {
            if (e.getPlayer().hasPermission("rclaim.admin.bypass")) return;
            if (!internalCheck(e.getPlayer(), e.getBlockClicked().getLocation())) {
                e.setCancelled(true);
                sendMessageToPlayer("PERMISSION_BLOCK_PLACE", e.getPlayer());
            }
        }
    }

    public static class BucketFillPermission extends ClaimPermissionRegistry {
        protected BucketFillPermission(ClaimPermissionService svc) { super(svc, "BUCKET_FILL"); }
        @EventHandler(ignoreCancelled = true)
        public void on(PlayerBucketFillEvent e) {
            if (e.getPlayer().hasPermission("rclaim.admin.bypass")) return;
            if (!internalCheck(e.getPlayer(), e.getBlockClicked().getLocation())) {
                e.setCancelled(true);
                sendMessageToPlayer("PERMISSION_BLOCK_BREAK", e.getPlayer());
            }
        }
    }

    public static class PlayerDamagePermission extends ClaimPermissionRegistry {
        protected PlayerDamagePermission(ClaimPermissionService svc) { super(svc, "ATTACK_MONSTER"); }
        @EventHandler(ignoreCancelled = true)
        public void on(EntityDamageEvent e) {
            if (!(e.getEntity() instanceof Player p)) return;
            if (p.hasPermission("rclaim.admin.bypass")) return;
            if (!internalCheck(p, p.getLocation())) {
                e.setCancelled(true);
            }
        }
    }
}
