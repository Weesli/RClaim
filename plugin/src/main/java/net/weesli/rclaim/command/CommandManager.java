package net.weesli.rclaim.command;


import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.model.Claim;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandManager {

    private BukkitCommandManager<CommandSender> commandManager;

    public CommandManager() {
        commandManager = BukkitCommandManager.create(RClaim.getInstance());
        registerArgs();
        registerCommands();
    }

    private void registerArgs() {
        commandManager.registerSuggestion(SuggestionKey.of("name"), (key,value) ->  List.of("<name>"));
        commandManager.registerSuggestion(SuggestionKey.of("amount"), (key,value) -> List.of("<amount>"));
        commandManager.registerSuggestion(SuggestionKey.of("player_claims"), (key,value) -> {
            Player player = (Player) key;
            List<Claim> claims = RClaim.getInstance().getCacheManager().getClaims().getAllClaims(player.getUniqueId());
            return claims.stream().map(claim -> {
                if (claim.getDisplayName() == null){
                    return claim.getID();
                }
                return claim.getDisplayName();
            }).toList();
        });
    }


    private void registerCommands() {
        commandManager.registerCommand(new AdminCommand());
        commandManager.registerCommand(new PlayerCommand());
        commandManager.registerCommand(new UnClaimCommand());
    }

}

