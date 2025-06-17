package net.weesli.rclaim.command;


import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import net.weesli.rclaim.RClaim;
import org.bukkit.command.CommandSender;

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
    }


    private void registerCommands() {
        commandManager.registerCommand(new AdminCommand());
        commandManager.registerCommand(new PlayerCommand());
        commandManager.registerCommand(new UnClaimCommand());
    }

}

