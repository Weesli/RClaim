package net.weesli.rclaim.input;

import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.model.Claim;
import net.weesli.rclaim.api.model.ClaimTag;
import net.weesli.rclaim.model.ClaimTagImpl;
import net.weesli.rclaim.ui.inventories.ClaimUsersMenu;
import net.weesli.rclaim.ui.inventories.tag.ClaimTagMainMenu;
import net.weesli.rclaim.ui.inventories.tag.ClaimTagUsersMenu;
import net.weesli.rclaim.util.BaseUtil;
import net.weesli.rclaim.util.PlayerUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static net.weesli.rclaim.config.lang.LangConfig.sendMessageToPlayer;

public class TextInputManager {
    public enum TextInputAction {
        ADD_PLAYER_TO_CLAIM,
        ADD_TAG_TO_CLAIM,
        ADD_PLAYER_TO_TAG
    }

    private final Map<Player, TextPlayer> actions;

    public TextInputManager(Plugin plugin) {
        this.actions =new HashMap<>();
        plugin.getServer().getPluginManager().registerEvents(new TextInputListener(), plugin);
    }

    public void runAction(Player player, TextInputAction action, Object o) {

        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                actions.remove(player);
                cancel();
            }
        }.runTaskLater(RClaim.getInstance(), 120L);
        TextPlayer textPlayer = new TextPlayer(task,action, o);
        actions.put(player, textPlayer);
    }

    public void processInput(Player player, String msg) {
        TextInputAction action = actions.get(player).action();
        switch (action) {
            case ADD_PLAYER_TO_CLAIM:
                addPlayerToClaim(player, msg);
                break;
            case ADD_TAG_TO_CLAIM:
                addTagToClaim(player, msg);
                break;
            case ADD_PLAYER_TO_TAG:
                addPlayerToTag(player, msg);
                break;
        }
    }

    private void addPlayerToClaim(Player player, String msg) {
        TextPlayer textPlayer = actions.get(player);
        if (msg.isEmpty() || player.getName().equals(msg)) {
            sendMessageToPlayer("ENTER_A_PLAYER_NAME", player);
            return;
        }
        Claim claim = (Claim) textPlayer.o();
        if (claim == null || !claim.getOwner().equals(player.getUniqueId())) {
            sendMessageToPlayer("YOU_DONT_IN_CLAIM", player);
            return;
        }
        OfflinePlayer targetPlayer = PlayerUtil.getPlayer(msg);
        if (targetPlayer == null) {
            sendMessageToPlayer("TARGET_NOT_FOUND", player);
            return;
        }
        claim.trustPlayer(player, targetPlayer.getUniqueId());
        RClaim.getInstance().getUiManager().openInventory(
                player,
                claim,
                ClaimUsersMenu.class
        );
    }

    private void addTagToClaim(Player player, String msg) {
        TextPlayer textPlayer = actions.get(player);
        if (msg.isEmpty()) {
            sendMessageToPlayer("ENTER_A_TAG_NAME", player);
            return;
        }
        Claim claim = (Claim) textPlayer.o();
        if (claim == null || !claim.getOwner().equals(player.getUniqueId())) {
            sendMessageToPlayer("YOU_DONT_IN_CLAIM", player);
            return;
        }
        ClaimTag claimTag = claim.getClaimTags().stream().filter(t -> t.getDisplayName().equalsIgnoreCase(msg)).findFirst().orElse(null);
        if (claimTag != null){
            sendMessageToPlayer("ALREADY_CREATED_TAG", player);
            return;
        }
        claimTag = new ClaimTagImpl(
                claim.getID(),
                BaseUtil.generateId(),
                msg,
                new ArrayList<>(),
                new ArrayList<>()
        );
        RClaim.getInstance().getTagManager().addTag(claim, claimTag);
        sendMessageToPlayer("TAG_CREATED", player);
        RClaim.getInstance().getUiManager().openInventory(
                player,
                claim,
                ClaimTagMainMenu.class
        );
    }

    private void addPlayerToTag(Player player, String msg) {
        TextPlayer textPlayer = actions.get(player);
        if (msg.isEmpty() && player.getName().equals(msg)) {
            sendMessageToPlayer("ENTER_A_PLAYER_NAME", player);
            return;
        }
        ClaimTag claimTag = (ClaimTag) textPlayer.o();
        if (claimTag == null){
            return;
        }
        if (PlayerUtil.getPlayer(msg) == null) {
            sendMessageToPlayer("TARGET_NOT_FOUND", player);
            return;
        }
        if (claimTag.getUsers().contains(PlayerUtil.getPlayer(msg).getUniqueId())) {
            sendMessageToPlayer("ALREADY_TRUSTED_PLAYER", player);
            return;
        }
        claimTag.getUsers().add(PlayerUtil.getPlayer(msg).getUniqueId());
        sendMessageToPlayer("ADDED_USER_TO_TAG", player);
        RClaim.getInstance().getUiManager().openTagInventory(
                player,
                claimTag,
                ClaimTagUsersMenu.class
        );
    }

    public boolean isInputActive(Player player) {
        return actions.containsKey(player);
    }

    public TextPlayer getTextPlayer(Player player) {
        return actions.get(player);
    }

    public void remove(Player player) {
        actions.remove(player);
    }
}
