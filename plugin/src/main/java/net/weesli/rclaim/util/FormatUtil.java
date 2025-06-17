package net.weesli.rclaim.util;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.weesli.rclaim.config.ConfigLoader;
import net.weesli.rozslib.color.ColorBuilder;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * FormatManager handles the formatting and sending of messages to players
 * in various formats including BossBar, ActionBar, and Titles.
 */
public class FormatUtil {

    /**
     * Sends a formatted message to the player based on the specified format in the configuration.
     *
     * @param player The player to send the message to.
     * @param values A map of placeholder values to replace in the message.
     */
    public static void sendMessage(Player player, Map<String, String> values) {
        String format = ConfigLoader.getConfig().getEnterMessage().getFormat();
        String message = ConfigLoader.getConfig().getEnterMessage().getText();
        switch (format) {
            case "actionbar":
                sendActionBar(player, values, message);
                break;
            case "title":
                sendTitle(player, values, message);
                break;
            default:
        }
    }

    /**
     * Sends an ActionBar message to the player.
     *
     * @param player  The player to send the message to.
     * @param values  A map of placeholder values to replace in the message.
     * @param message The message to send.
     */
    public static void sendActionBar(Player player, Map<String, String> values, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ColorBuilder.convertColors(message.replace("%player%", values.get("player")))));
    }

    /**
     * Sends a Title message to the player.
     *
     * @param player  The player to send the message to.
     * @param values  A map of placeholder values to replace in the message.
     * @param message The message to send, with title and subtitle separated by "<>".
     */
    public static void sendTitle(Player player, Map<String, String> values, String message) {
        String[] split = message.split("<>");
        String title = split[0].replace("%player%", values.get("player"));
        String subtitle = split.length > 1 ? split[1].replace("%player%", values.get("player")) : "";
        player.sendTitle(ColorBuilder.convertColors(title), ColorBuilder.convertColors(subtitle), 20, 40, 20);
    }
}
