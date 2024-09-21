package net.weesli.rClaim.utils;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.weesli.rClaim.RClaim;
import net.weesli.rozsLib.bossbar.BossBarBuilder;
import net.weesli.rozsLib.color.ColorBuilder;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Player;

import java.util.Map;

public class FormatManager {

    public static void sendMessage(Player player, Map<String, String> values){
        String format = RClaim.getInstance().getConfig().getString("options.enter-message.format");
        String message = RClaim.getInstance().getConfig().getString("options.enter-message.text");
        switch (format){
            case "bossbar":
                sendBossBar(player,values, message);
                break;
            case "actionbar":
                sendActionBar(player, values, message);
                break;
            case "title":
                sendTitle(player, values, message);
                break;
            default:
        }
    }


    public static void sendBossBar(Player player, Map<String, String> values,String message){
        BossBarBuilder builder = new BossBarBuilder(RClaim.getInstance());
        builder.addViewer(player);
        builder.setText(ColorBuilder.convertColors(message.replaceAll("%player%", values.get("player"))));
        builder.setColor(BarColor.GREEN);
        builder.setTime(3);
        builder.build();
    }

    public static void sendActionBar(Player player, Map<String, String> values, String message){
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ColorBuilder.convertColors(message.replace("%player%", values.get("player")))));
    }

    public static void sendTitle(Player player, Map<String, String> values, String message) {
        String[] split = message.split("<>");
        String title = split[0].replace("%player%", values.get("player"));
        String subtitle = split.length > 1 ? split[1].replace("%player%", values.get("player")) : "";
        player.sendTitle(ColorBuilder.convertColors(title), ColorBuilder.convertColors(subtitle), 20, 40, 20);
    }


}
