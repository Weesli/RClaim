package net.weesli.rClaim.hooks;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.weesli.rClaim.RClaim;
import net.weesli.rClaim.management.ClaimManager;
import net.weesli.rClaim.tasks.ClaimTask;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    Pattern nameMatcher = Pattern.compile("\\d+");

    /**
     * get Claim time format is %rclaims_<id>_time%
     */
    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if (params.contains("time")){
            Matcher matcher = nameMatcher.matcher(params);
            if (matcher.find()){
                String claimId = matcher.group();
                return ClaimManager.getTimeFormat(claimId);
            }
        }
        return "";
    }
}