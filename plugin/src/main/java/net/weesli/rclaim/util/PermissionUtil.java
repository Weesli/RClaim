package net.weesli.rclaim.util;

import net.weesli.rclaim.RClaim;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.Optional;

import static net.weesli.rclaim.config.lang.LangConfig.sendMessageToPlayer;

public class PermissionUtil {

    public static int getMemberLimit(Player player){
        int limit = 0;
        if (player.hasPermission("rclaim.memberlimit.*")) return -1;
        for (PermissionAttachmentInfo info : player.getEffectivePermissions()) {
            if (info.getPermission().startsWith("rclaim.memberlimit.")) {
                limit = Integer.parseInt(info.getPermission().split("\\.")[2]);
            }
        }
        return limit;
    }
    public static boolean hasPermissionClaimBlock(Player player, Material material){
        String name = material.name().toLowerCase();
        if (player.hasPermission("rclaim.claim.block.*")) return true;
        for (PermissionAttachmentInfo info : player.getEffectivePermissions()) {
            if (info.getPermission().startsWith("rclaim.claim.block.")) {
                if (info.getPermission().split("\\.")[2].equals(name)) return true;
            }
        }
        return false;
    }

    public static boolean checkPlayerClaimLimit(Player player){
        if (player.hasPermission("rclaim.claim.limit.*")) return true;
        Optional<PermissionAttachmentInfo> limitPermission = player.getEffectivePermissions().stream()
                .filter(p -> p.getPermission().startsWith("rclaim.claim.limit."))
                .findFirst();
        if (limitPermission.isPresent()) {
            try {
                String permission = limitPermission.get().getPermission();
                String[] parts = permission.split("\\.");
                int limitValue = Integer.parseInt(parts[parts.length - 1]);

                int currentClaims = RClaim.getInstance()
                        .getCacheManager()
                        .getClaims()
                        .getAllClaims(player.getUniqueId())
                        .size();

                if (currentClaims >= limitValue) {
                    sendMessageToPlayer("CLAIM_LIMIT", player);
                    return false;
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
}
