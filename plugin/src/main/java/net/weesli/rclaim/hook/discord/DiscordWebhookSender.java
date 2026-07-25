package net.weesli.rclaim.hook.discord;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.config.ConfigLoader;
import net.weesli.rozslib.color.ColorBuilder;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public final class DiscordWebhookSender {

    private DiscordWebhookSender() {
    }

    public static void send(String description) {
        String url = ConfigLoader.getConfig().getDiscordWebhook().getUrl();
        if (url == null || url.isBlank()) return;

        JsonObject embed = new JsonObject();
        embed.addProperty("description", description);
        embed.addProperty("color", 0x5865F2);

        JsonArray embeds = new JsonArray();
        embeds.add(embed);

        JsonObject payload = new JsonObject();
        payload.add("embeds", embeds);
        String body = payload.toString();

        RClaim.getInstance().getFoliaLib().getScheduler().runAsync((task) -> {
            try (HttpClient client = HttpClient.newBuilder().build()) {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI(url))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(body))
                        .build();
                HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
                if (response.statusCode() >= 300) {
                    Bukkit.getConsoleSender().sendMessage(ColorBuilder.convertColors(
                            "&c[RClaim] Discord webhook request failed with status " + response.statusCode()));
                }
            } catch (URISyntaxException | IOException e) {
                Bukkit.getConsoleSender().sendMessage(ColorBuilder.convertColors(
                        "&c[RClaim] Failed to send Discord webhook notification: " + e.getMessage()));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }
}
