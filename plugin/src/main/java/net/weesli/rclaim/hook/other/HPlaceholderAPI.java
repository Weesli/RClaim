package net.weesli.rclaim.hook.other;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.enums.ClaimStatus;
import net.weesli.rclaim.api.model.Claim;
import net.weesli.rclaim.config.ConfigLoader;
import net.weesli.rclaim.util.BaseUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.weesli.rclaim.util.BaseUtil.getSec;

public class HPlaceholderAPI extends PlaceholderExpansion {


    @Override
    public @NotNull String getIdentifier() {
        return "rclaim";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Weesli";
    }

    @Override
    public @NotNull String getVersion() {
        return RClaim.getInstance().getDescription().getVersion();
    }

    Pattern globalMatcher = Pattern.compile("\\d+"); // this patter is used to get claim id

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if (params.contains("time")){
            Matcher matcher = globalMatcher.matcher(params);
            if (matcher.find()){
                String claimId = matcher.group();
                return BaseUtil.getTimeFormat(claimId);
            }
        }
        if (params.contains("progressbar")){
            Matcher matcher = globalMatcher.matcher(params);
            if (matcher.find()){
                String claimId = matcher.group();
                Claim claim = RClaim.getInstance().getClaimManager().getClaim(claimId);
                return BaseUtil.createProgressBar(claim.getTimestamp(), getSec(ConfigLoader.getConfig().getClaimSettings().getClaimDuration()),75);
            }
        }
        if (params.contains("name") && !params.equals("name")){
            Matcher matcher = globalMatcher.matcher(params);
            if (matcher.find()){
                String claimId = matcher.group();
                Claim claim = RClaim.getInstance().getClaimManager().getClaim(claimId);
                return claim.getDisplayName();
            }
        }
        if (params.equals("count")){
            return String.valueOf(RClaim.getInstance().getCacheManager().getClaims().getCache().values().stream().filter(c -> c.isOwner(player.getUniqueId())).count());
        }
        if (params.equals("owner")){
            if (!RClaim.getInstance().getClaimManager().isSuitable(player.getLocation().getChunk())){
                return "";
            }
            Claim claim = RClaim.getInstance().getClaimManager().getClaim(player.getLocation().getChunk());
            if (claim != null){
                return Bukkit.getOfflinePlayer(claim.getOwner()).getName();
            }
        }
        if (params.equals("name")){
            if (!RClaim.getInstance().getClaimManager().isSuitable(player.getLocation().getChunk())){
                return "";
            }
            Claim claim = RClaim.getInstance().getClaimManager().getClaim(player.getLocation());
            if (claim != null){
                return claim.getDisplayName();
            }
        }
        if (params.equals("size")){
            if (!RClaim.getInstance().getClaimManager().isSuitable(player.getLocation().getChunk())){
                return "";
            }
            Claim claim = RClaim.getInstance().getClaimManager().getClaim(player.getLocation());
            if (claim != null){
                return String.valueOf(claim.getSize());
            }
        }
        if (params.equals("pvp")){
            if (!RClaim.getInstance().getClaimManager().isSuitable(player.getLocation().getChunk())){
                return "";
            }
            Claim claim = RClaim.getInstance().getClaimManager().getClaim(player.getLocation());
            if (claim != null){
                return claim.checkStatus(ClaimStatus.PVP) ? "true" : "false";
            }
        }
        return "";
    }
}
