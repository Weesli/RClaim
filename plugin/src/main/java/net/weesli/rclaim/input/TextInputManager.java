package net.weesli.rclaim.input;

import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.events.ClaimTrustEvent;
import net.weesli.rclaim.api.model.Claim;
import net.weesli.rclaim.api.model.ClaimTag;
import net.weesli.rclaim.model.ClaimTagImpl;
import net.weesli.rclaim.util.BaseUtil;
import net.weesli.rclaim.util.PlayerUtil;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
        }.runTaskLater(RClaim.getInstance(), 60L);
        TextPlayer textPlayer = new TextPlayer(task, action, o);
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
        if (msg.isEmpty()) {
            player.sendMessage(RClaim.getInstance().getMessage("ENTER_A_PLAYER_NAME"));
            return;
        }
        Claim claim = (Claim) textPlayer.o();
        if (claim == null || !claim.getOwner().equals(player.getUniqueId())) {
            player.sendMessage(RClaim.getInstance().getMessage("YOU_DONT_IN_CLAIM"));
            return;
        }
        if (PlayerUtil.getPlayer(msg) == null) {
            player.sendMessage(RClaim.getInstance().getMessage("TARGET_NOT_FOUND"));
        }
        if (claim.getMembers().contains(PlayerUtil.getPlayer(msg).getUniqueId())) {
            player.sendMessage(RClaim.getInstance().getMessage("ALREADY_TRUSTED_PLAYER"));
            return;
        }
        ClaimTrustEvent event = new ClaimTrustEvent(player.getUniqueId(), PlayerUtil.getPlayer(msg).getUniqueId());
        RClaim.getInstance().getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        claim.addMember(PlayerUtil.getPlayer(msg).getUniqueId());
        player.sendMessage(RClaim.getInstance().getMessage("TRUSTED_PLAYER"));
    }

    private void addTagToClaim(Player player, String msg) {
        TextPlayer textPlayer = actions.get(player);
        if (msg.isEmpty()) {
            player.sendMessage(RClaim.getInstance().getMessage("ENTER_A_TAG_NAME"));
            return;
        }
        Claim claim = (Claim) textPlayer.o();
        if (claim == null || !claim.getOwner().equals(player.getUniqueId())) {
            player.sendMessage(RClaim.getInstance().getMessage("YOU_DONT_IN_CLAIM"));
            return;
        }
        ClaimTag claimTag = claim.getClaimTags().stream().filter(t -> t.getDisplayName().equalsIgnoreCase(msg)).findFirst().orElse(null);
        if (claimTag != null){
            player.sendMessage(RClaim.getInstance().getMessage("ALREADY_CREATED_TAG"));
            return;
        }
        RClaim.getInstance().getTagManager().addTag(claim,
                new ClaimTagImpl(
                        claim.getID(),
                        BaseUtil.generateId(),
                        msg,
                        new ArrayList<>(),
                        new ArrayList<>()
                ));
        player.sendMessage(RClaim.getInstance().getMessage("TAG_CREATED"));
    }

    private void addPlayerToTag(Player player, String msg) {
        TextPlayer textPlayer = actions.get(player);
        if (msg.isEmpty()) {
            player.sendMessage(RClaim.getInstance().getMessage("ENTER_A_PLAYER_NAME"));
            return;
        }
        ClaimTag claimTag = (ClaimTag) textPlayer.o();
        if (claimTag == null){
            return;
        }
        if (PlayerUtil.getPlayer(msg) == null) {
            player.sendMessage(RClaim.getInstance().getMessage("TARGET_NOT_FOUND"));
        }
        if (claimTag.getUsers().contains(PlayerUtil.getPlayer(msg).getUniqueId())) {
            player.sendMessage(RClaim.getInstance().getMessage("ALREADY_TRUSTED_PLAYER"));
            return;
        }
        claimTag.getUsers().add(PlayerUtil.getPlayer(msg).getUniqueId());
        player.sendMessage(RClaim.getInstance().getMessage("ADDED_USER_TO_TAG"));
    }

    public boolean isInputActive(Player player) {
        return actions.containsKey(player);
    }

    public TextPlayer getTextPlayer(Player player) {
        return actions.get(player);
    }
}
