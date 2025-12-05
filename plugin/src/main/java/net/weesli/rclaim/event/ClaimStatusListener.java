package net.weesli.rclaim.event;

import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.model.Claim;
import net.weesli.rclaim.api.status.ClaimStatusRegistry;
import net.weesli.rclaim.api.status.ClaimStatusService;
import net.weesli.rozslib.events.PlayerDamageByPlayerEvent;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;

import static net.weesli.rclaim.config.lang.LangConfig.sendMessageToPlayer;

public class ClaimStatusListener {

    private final ClaimStatusService statusService;


    public ClaimStatusListener() {
        this.statusService = RClaim.getInstance().getStatusService();
        statusService.registerStatus(new MonsterSpawnStatus(statusService));
        statusService.registerStatus(new AnimalSpawnStatus(statusService));
        statusService.registerStatus(new ExplosionStatus(statusService));
        statusService.registerStatus(new PvPStatus(statusService));
        statusService.registerStatus(new SpreadStatus(statusService));
        statusService.registerStatus(new WeatherStatus(statusService));
        statusService.registerStatus(new TimeStatus(statusService));
    }

    public static class MonsterSpawnStatus extends ClaimStatusRegistry {
        protected MonsterSpawnStatus(ClaimStatusService svc) { super("SPAWN_MONSTER"); }

        @EventHandler(ignoreCancelled = true)
        public void on(EntitySpawnEvent e) {
            if (!(e.getEntity() instanceof Monster)) {
                return;
            }
            Claim claim = RClaim.getInstance().getClaimManager().getClaim(e.getLocation());
            if (claim == null) return;
            if (!claim.checkStatus("SPAWN_MONSTER")) {
                e.setCancelled(true);
            }
        }
    }

    public static class AnimalSpawnStatus extends ClaimStatusRegistry {
        protected AnimalSpawnStatus(ClaimStatusService svc) { super("SPAWN_ANIMAL"); }

        @EventHandler(ignoreCancelled = true)
        public void on(EntitySpawnEvent e) {
            Entity spawned = e.getEntity();

            if (spawned instanceof Monster || spawned instanceof Player) {
                return;
            }
            if (!(spawned instanceof LivingEntity)) {
                return;
            }

            if (spawned instanceof org.bukkit.entity.Tameable && ((org.bukkit.entity.Tameable) spawned).isTamed()) {
                return;
            }

            Claim claim = RClaim.getInstance().getClaimManager().getClaim(e.getLocation());
            if (claim == null) return;

            if (!claim.checkStatus("SPAWN_ANIMAL")) {
                e.setCancelled(true);
            }
        }
    }

    public static class ExplosionStatus extends ClaimStatusRegistry {
        protected ExplosionStatus(ClaimStatusService svc) { super("EXPLOSION"); }
        @EventHandler(ignoreCancelled = true)
        public void on(EntityExplodeEvent e) {
            for (Block block : e.blockList()) {
                Claim claim = RClaim.getInstance().getClaimManager().getClaim(block.getLocation());
                if (claim != null && !claim.checkStatus("EXPLOSION")) {
                    e.setCancelled(true);
                    break;
                }
            }
        }
    }

    public static class PvPStatus extends ClaimStatusRegistry {
        protected PvPStatus(ClaimStatusService svc) { super("PVP"); }
        @EventHandler(ignoreCancelled = true)
        public void on(PlayerDamageByPlayerEvent e) {
            Claim victim = RClaim.getInstance().getClaimManager().getClaim(e.getPlayer().getLocation());
            Claim attacker = RClaim.getInstance().getClaimManager().getClaim(e.getDamager().getLocation());
            if (attacker == null || victim == null) return;
            if (!attacker.checkStatus("PVP") || !victim.checkStatus("PVP")) {
                e.setCancelled(true);
                sendMessageToPlayer("STATUS_PVP", e.getPlayer());
            }
        }
    }

    public static class SpreadStatus extends ClaimStatusRegistry {
        protected SpreadStatus(ClaimStatusService svc) { super("SPREAD"); }
        @EventHandler(ignoreCancelled = true)
        public void on(BlockSpreadEvent e) {
            Claim claim = RClaim.getInstance().getClaimManager().getClaim(e.getBlock().getLocation());
            if (claim != null && !claim.checkStatus("SPREAD")) e.setCancelled(true);
        }
    }

    public static class WeatherStatus extends ClaimStatusRegistry {
        protected WeatherStatus(ClaimStatusService svc) { super("WEATHER"); }
        // skip event for weather status
    }

    public static class TimeStatus extends ClaimStatusRegistry {
        protected TimeStatus(ClaimStatusService svc) { super("TIME"); }
        // skip event for time status
    }

}
