package net.weesli.rclaim.input;

import net.weesli.rclaim.RClaim;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class TextInputListener implements Listener {

    @EventHandler
    public void onInputText(AsyncPlayerChatEvent event){
        String msg = event.getMessage();
        Player player = event.getPlayer();
        if (RClaim.getInstance().getTextInputManager().isInputActive(player)) {
            TextPlayer textPlayer = RClaim.getInstance().getTextInputManager().getTextPlayer(player);
            if (textPlayer.isCancelled()) {
                return;
            }
            event.setCancelled(true);
            /*Bukkit.getScheduler().runTask(RClaim.getInstance(), () -> {
                RClaim.getInstance().getTextInputManager().processInput(player, msg);
                textPlayer.cancel();
            });*/
            RClaim.getInstance().getFoliaLib().getScheduler().runNextTick((wrapper) -> {
                RClaim.getInstance().getTextInputManager().processInput(player, msg);
                textPlayer.cancel();
            });
        }
    }
}
