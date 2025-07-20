package net.weesli.rclaim.util;

import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.model.Claim;
import net.weesli.rclaim.config.ConfigLoader;
import net.weesli.rclaim.api.enums.Effect;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.time.Duration;
import java.util.*;

public class BaseUtil {

    public static String generateId(){
        String alphabet = "0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            sb.append(alphabet.charAt(random.nextInt(alphabet.length())));
        }
        return sb.toString();
    }

    public static int getSec(int day){
        return day * 24 * 60 * 60;
    }

    public static String getTimeFormat(String claimId) {
        int time = RClaim.getInstance().getClaimManager().getClaim(claimId).getTimestamp();
        Duration duration = Duration.ofSeconds(time);
        long weeks = duration.toDays() / 7;
        long days = duration.toDays() % 7;
        long hours = duration.toHoursPart();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();

        return ConfigLoader.getConfig().getTimeFormat()
                .replace("%week%", String.valueOf(weeks))
                .replace("%day%", String.valueOf(days))
                .replace("%hour%", String.valueOf(hours))
                .replace("%minute%", String.valueOf(minutes))
                .replace("%second%", String.valueOf(seconds));
    }


    public static boolean isActiveWorld(String name){
        return ConfigLoader.getConfig().getActiveWorlds().stream().anyMatch(s -> s.equals(name));
    }

    public static String getStatus(boolean status){
        return status? ConfigLoader.getConfig().getStatus().getActive() : ConfigLoader.getConfig().getStatus().getNonActive();
    }

    public static int getCost(Effect effect, Claim claim){
        boolean isActive = claim.hasEffect(effect);
        int cost = 0;
        if (isActive){
            if (effect.equals(Effect.JUMP)) cost = ConfigLoader.getConfig().getEffects().getJump().getUpgradeCost();
            else if (effect.equals(Effect.HASTE)) cost = ConfigLoader.getConfig().getEffects().getHaste().getUpgradeCost();
            else if (effect.equals(Effect.SPEED)) cost = ConfigLoader.getConfig().getEffects().getSpeed().getUpgradeCost();
        } else {
            if (effect.equals(Effect.JUMP)) cost = ConfigLoader.getConfig().getEffects().getJump().getBuyCost();
            else if (effect.equals(Effect.HASTE)) cost = ConfigLoader.getConfig().getEffects().getHaste().getBuyCost();
            else if (effect.equals(Effect.SPEED)) cost = ConfigLoader.getConfig().getEffects().getSpeed().getBuyCost();
        }
        return cost;
    }

    public static boolean changeBlockMaterial(Player player, Claim claim, Material material){
        boolean hasPermission = PermissionUtil.hasPermissionClaimBlock(player, material);
        if (hasPermission){
            claim.setBlock(material);
            player.playEffect(claim.getBlockLocation(), org.bukkit.Effect.SMOKE, 25);
            return true;
        }
        return false;
    }

    public static String createProgressBar(int progress, int maxProgress, int barLength) {
        int filledLength = (int) (((double) progress / maxProgress) * barLength);
        int emptyLength = barLength - filledLength;
        return "|".repeat(Math.max(0, filledLength)) +
                " ".repeat(Math.max(0, emptyLength));
    }

    public static boolean isBetweenAnyClaim(Chunk chunk) {
        int minSize = ConfigLoader.getConfig().getMinBetweenClaim();
        int currentX = chunk.getX() * 16;
        int currentZ = chunk.getZ() * 16;
        Location location = new Location(chunk.getWorld(), currentX, 0, currentZ);
        for (Claim claim : RClaim.getInstance().getCacheManager().getClaims().getCache().values()) {
            if (!claim.getCenter().getWorld().getName().contains(chunk.getWorld().getName())){continue;}
            if (location.distance(claim.getCenter()) < minSize) return false;
        }
        return true;
    }
}
