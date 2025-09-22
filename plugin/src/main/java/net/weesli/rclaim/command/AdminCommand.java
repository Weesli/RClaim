package net.weesli.rclaim.command;

import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.Default;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import dev.triumphteam.cmd.core.annotation.Suggestion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.model.Claim;
import net.weesli.rclaim.config.ConfigLoader;
import net.weesli.rclaim.util.ClaimBlockUtil;
import net.weesli.rozslib.color.ColorBuilder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static net.weesli.rclaim.config.lang.LangConfig.sendMessageToConsole;
import static net.weesli.rclaim.util.ChatUtil.createTagResolver;

@Command("adminclaim")
public class AdminCommand extends BaseCommand {


    @Default
    public void execute(CommandSender commandSender){
        commandSender.sendMessage(ColorBuilder.convertColors("&bRunning RClaims by Weesli"));
    }

    @SubCommand("clearclaim")
    public void clearclaim(CommandSender commandSender, @Suggestion("name") String target){
        if (!commandSender.isOp())return;
        if (target == null || target.isEmpty()){
            sendMessageToConsole("ENTER_A_PLAYER_NAME");
            return;
        }
        OfflinePlayer player = Bukkit.getOfflinePlayer(target);
        if (player == null){
            sendMessageToConsole("TARGET_NOT_FOUND");
            return;
        }
        List<Claim> Claims = RClaim.getInstance().getCacheManager().getClaims().getAllClaims(player.getUniqueId());
        Claims.forEach(claim -> {
            RClaim.getInstance().getClaimManager().removeClaim(claim);
        });
        sendMessageToConsole("DELETED_CLAIMS", createTagResolver("player", player.getName()));
    }

    @SubCommand("reload")
    public void reload(CommandSender commandSender){
        if (!commandSender.isOp())return;
        ConfigLoader.reload();
        commandSender.sendMessage(ColorBuilder.convertColors("&aAll files reloaded!"));
    }

    @SubCommand("claimblock")
    public void claimblock(CommandSender commandSender, Player target, @Suggestion("amount") int amount){
        if (!commandSender.isOp())return;
        ClaimBlockUtil.giveBlock(target, amount);
    }


    @SubCommand("info")
    public void info(CommandSender commandSender){
        if (!commandSender.isOp())return;
        commandSender.sendMessage(ColorBuilder.convertColors("&a--------------------------------"));
        commandSender.sendMessage(ColorBuilder.convertColors("&aRClaims by Weesli"));
        commandSender.sendMessage(ColorBuilder.convertColors("&a--------------------------------"));
        commandSender.sendMessage(ColorBuilder.convertColors("&aVersion: " + RClaim.getInstance().getDescription().getVersion()));
        commandSender.sendMessage(ColorBuilder.convertColors("&aAuthor: Weesli"));
        commandSender.sendMessage(ColorBuilder.convertColors("&a--------------------------------"));
        commandSender.sendMessage(ColorBuilder.convertColors("&aEconomy: &f"+ RClaim.getInstance().getEconomyManager().getEconomyIntegration().getEconomyType().name()));
        commandSender.sendMessage(ColorBuilder.convertColors("&aStorage: &f"+ RClaim.getInstance().getStorage().getStorageType().name()));
        commandSender.sendMessage(ColorBuilder.convertColors("&aHologram: &f"+ ((RClaim.getInstance().getHologramManager().getHologramIntegration() == null) ? "Empty" : RClaim.getInstance().getHologramManager().getHologramIntegration().Type().name())));
        commandSender.sendMessage(ColorBuilder.convertColors("&aSpawner: &f"+ ((RClaim.getInstance().getSpawnerManager().getIntegration() == null) ? "Empty" : RClaim.getInstance().getSpawnerManager().getIntegration().getName())));
        commandSender.sendMessage(ColorBuilder.convertColors("&aMinion: &f"+ ((RClaim.getInstance().getMinionsManager().getIntegration() == null) ? "Empty" : RClaim.getInstance().getMinionsManager().getIntegration().getName())));
        commandSender.sendMessage(ColorBuilder.convertColors("&aCombat System: &f"+ ((RClaim.getInstance().getCombatManager() == null) ? "Empty" : RClaim.getInstance().getCombatManager().getCombatIntegration().getName())));
        commandSender.sendMessage(ColorBuilder.convertColors("&a--------------------------------"));
    }
}
