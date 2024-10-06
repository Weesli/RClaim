package net.weesli.rClaim.command;

import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.Default;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import dev.triumphteam.cmd.core.annotation.Suggestion;
import net.weesli.rClaim.RClaim;
import net.weesli.rClaim.modal.Claim;
import net.weesli.rClaim.utils.ClaimManager;
import net.weesli.rozsLib.color.ColorBuilder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.List;

@Command("adminclaim")
public class AdminCommand extends BaseCommand {


    @Default
    public void execute(CommandSender commandSender){
        commandSender.sendMessage(ColorBuilder.convertColors("&bRunning RClaims by Weesli"));
    }

    @SubCommand("clearclaim")
    public void clearclaim(CommandSender commandSender, @Suggestion("name") String target){
        if (!commandSender.isOp()){return;}
        if (target == null || target.isEmpty()){
            commandSender.sendMessage(RClaim.getInstance().getMessage("ENTER_A_PLAYER_NAME"));
            return;
        }
        OfflinePlayer player = Bukkit.getOfflinePlayer(target);
        if (player == null){
            commandSender.sendMessage(RClaim.getInstance().getMessage("TARGET_NOT_FOUND"));
            return;
        }
        List<Claim> claims = ClaimManager.getPlayerData(player.getUniqueId()).getClaims();
        claims.forEach(claim -> {
            ClaimManager.removeClaim(claim);
        });
        commandSender.sendMessage(RClaim.getInstance().getMessage("DELETED_CLAIMS").replaceAll("%player%", player.getName()));
    }

    @SubCommand("reload")
    public void reload(CommandSender commandSender){
        if (!commandSender.isOp()){return;}
        RClaim.getInstance().reloadConfig();
        RClaim.getInstance().getMenusFile().reload();
        RClaim.getInstance().getUiManager().config = RClaim.getInstance().getMenusFile().load();
        RClaim.getInstance().getMessagesFile().reload();
        commandSender.sendMessage(ColorBuilder.convertColors("&aAll files reloaded!"));
    }

    @SubCommand("info")
    public void info(CommandSender commandSender){
        if (!commandSender.isOp()){return;}
        commandSender.sendMessage(ColorBuilder.convertColors("&a--------------------------------"));
        commandSender.sendMessage(ColorBuilder.convertColors("&aRClaims by Weesli"));
        commandSender.sendMessage(ColorBuilder.convertColors("&a--------------------------------"));
        commandSender.sendMessage(ColorBuilder.convertColors("&aVersion: " + RClaim.getInstance().getDescription().getVersion()));
        commandSender.sendMessage(ColorBuilder.convertColors("&aAuthor: Weesli"));
        commandSender.sendMessage(ColorBuilder.convertColors("&a--------------------------------"));
        commandSender.sendMessage(ColorBuilder.convertColors("&aEconomy: &f"+ RClaim.getInstance().getEconomy().getEconomyType().name()));
        commandSender.sendMessage(ColorBuilder.convertColors("&aStorage: &f"+ RClaim.getInstance().getStorage().getStorageType().name()));
        commandSender.sendMessage(ColorBuilder.convertColors("&aHologram: &f"+ ((RClaim.getInstance().getHologram() == null) ? "Empty" : RClaim.getInstance().getHologram().Type().name())));
        commandSender.sendMessage(ColorBuilder.convertColors("&aSpawner: &f"+ ((RClaim.getInstance().getSpawnerManager().getIntegration() == null) ? "Empty" : RClaim.getInstance().getSpawnerManager().getIntegration().getName())));
        commandSender.sendMessage(ColorBuilder.convertColors("&aMinion: &f"+ ((RClaim.getInstance().getMinionsManager().getIntegration() == null) ? "Empty" : RClaim.getInstance().getMinionsManager().getIntegration().getName())));
        commandSender.sendMessage(ColorBuilder.convertColors("&aCombat System: &f"+ ((RClaim.getInstance().getCombatManager() == null) ? "Empty" : RClaim.getInstance().getCombatManager().getCombat().getName())));
        commandSender.sendMessage(ColorBuilder.convertColors("&a--------------------------------"));
    }

}
