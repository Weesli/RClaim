package net.weesli.rclaim.util;

import de.rapha149.signgui.SignGUI;
import de.rapha149.signgui.SignGUIAction;
import de.rapha149.signgui.SignGUIBuilder;
import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.manager.TagManager;
import net.weesli.rclaim.api.model.Claim;
import net.weesli.rclaim.api.model.ClaimTag;
import net.weesli.rclaim.config.ConfigLoader;
import net.weesli.rclaim.model.ClaimTagImpl;
import net.weesli.rclaim.ui.inventories.ClaimUsersMenu;
import net.weesli.rclaim.ui.inventories.tag.ClaimTagMainMenu;
import net.weesli.rclaim.ui.inventories.tag.ClaimTagUsersMenu;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class SignViewUtil {

    public enum SignType {
        ADD_CLAIM_USER,
        ADD_TAG_USER,
        ADD_TAG
    }

    public static void viewSign(Player player, SignType type, Object object){
        List<String> lines = new ArrayList<>(List.of(""));
        switch (type) {
            case ADD_CLAIM_USER, ADD_TAG_USER -> {
                lines.addAll(ConfigLoader.getLangConfig().getEnterPlayerNameSign());
            }
            case ADD_TAG -> {
                lines.addAll(ConfigLoader.getLangConfig().getEnterClaimTagName());
            }
        }
        buildSign(player, lines, type, object);
    }

    private static void buildSign(Player player, List<String> lines, SignType type, Object object) {
        SignGUIBuilder builder = SignGUI.builder();
        builder.setLines(lines.toArray(new String[0]));
        builder.setType(Material.DARK_OAK_SIGN);
        builder.setColor(DyeColor.WHITE);
        builder.setHandler((sender, sign) -> {
            String name = sign.getLine(0);
            if (name.isEmpty()) {
                return Collections.emptyList();
            }
            return triggerSignAction(sender, name, type, object);
        });
        builder.build().open(player);
    }

    private static List<SignGUIAction> triggerSignAction(Player player, String name, SignType type, Object object) {
        switch (type) {
            case ADD_CLAIM_USER -> {
                Claim claim = (Claim) object;
                if (PlayerUtil.getPlayer(name) == null) {
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 5, 1);
                    player.sendMessage(RClaim.getInstance().getMessage("TARGET_NOT_FOUND"));
                    return Collections.singletonList(
                            SignGUIAction.run(()->{
                                RClaim.getInstance().getUiManager().openInventory(player, claim, ClaimUsersMenu.class);
                            })
                    );
                } else if (name.equals(player.getName())) {
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 5, 1);
                    player.sendMessage(RClaim.getInstance().getMessage("TARGET_NOT_FOUND"));
                    return Collections.singletonList(
                            SignGUIAction.run(()->{
                                RClaim.getInstance().getUiManager().openInventory(player, claim, ClaimUsersMenu.class);
                            })
                    );
                } else if (claim.isMember(PlayerUtil.getPlayer(name).getUniqueId())) {
                    player.sendMessage(RClaim.getInstance().getMessage("ALREADY_TRUSTED_PLAYER"));
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 5, 1);
                    return Collections.singletonList(
                            SignGUIAction.run(()->{
                                RClaim.getInstance().getUiManager().openInventory(player, claim, ClaimUsersMenu.class);
                            })
                    );
                } else {
                    claim.addMember(PlayerUtil.getPlayer(name).getUniqueId());
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 5, 1);
                    return Collections.singletonList(
                            SignGUIAction.run(()->{
                                RClaim.getInstance().getUiManager().openInventory(player, claim, ClaimUsersMenu.class);
                            })
                    );
                }
            }
            case ADD_TAG_USER -> {
                ClaimTag tag = (ClaimTag) object;
                Claim claim = RClaim.getInstance().getClaimManager().getClaim(tag.getClaimId());
                TagManager tagManager = RClaim.getInstance().getTagManager();
                if(PlayerUtil.getPlayer(name) == null){
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 5, 1);
                    player.sendMessage(RClaim.getInstance().getMessage("TARGET_NOT_FOUND"));
                    return Collections.singletonList(
                            SignGUIAction.run(()->{
                                RClaim.getInstance().getUiManager().openTagInventory(player, tag, ClaimTagUsersMenu.class);
                            })
                    );
                } else if (name.equals(player.getName()) || !claim.isMember(PlayerUtil.getPlayer(name).getUniqueId())) {
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 5, 1);
                    player.sendMessage(RClaim.getInstance().getMessage("TARGET_NOT_FOUND"));
                    return Collections.singletonList(
                            SignGUIAction.run(()->{
                                RClaim.getInstance().getUiManager().openTagInventory(player, tag, ClaimTagUsersMenu.class);
                            })
                    );
                } else if (tagManager.isPlayerInTag(PlayerUtil.getPlayer(name).getUniqueId(), claim.getID()) != null) {
                    player.sendMessage(RClaim.getInstance().getMessage("ALREADY_ADDED_USER"));
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 5, 1);
                    return Collections.singletonList(
                            SignGUIAction.run(()->{
                                RClaim.getInstance().getUiManager().openTagInventory(player, tag, ClaimTagUsersMenu.class);
                    }));
                } else {
                    tag.addUser(PlayerUtil.getPlayer(name).getUniqueId());
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 5, 1);
                    return Collections.singletonList(
                            SignGUIAction.run(()->{
                                RClaim.getInstance().getUiManager().openTagInventory(player, tag, ClaimTagUsersMenu.class);
                    }));
                }
            }
            case ADD_TAG -> {
                Claim claim = (Claim) object;
                if (name.isEmpty()) {
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 5, 1);
                } else {
                    RClaim.getInstance().getTagManager().addTag(claim,new ClaimTagImpl(
                            claim.getID(),
                            BaseUtil.generateId(),
                            name,
                            new ArrayList<>(),
                            new ArrayList<>()
                    ));
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 5, 1);
                }
                return Collections.singletonList(SignGUIAction.runSync(RClaim.getInstance(),()->RClaim.getInstance().getUiManager().openInventory(player, claim, ClaimTagMainMenu.class)));
            }
        }
        return Collections.emptyList();
    }
}