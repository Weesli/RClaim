package net.weesli.rClaim.ui.inventories;

import de.rapha149.signgui.SignGUI;
import de.rapha149.signgui.SignGUIAction;
import net.weesli.rClaim.RClaim;
import net.weesli.rClaim.enums.VerifyAction;
import net.weesli.rClaim.ui.ClaimInventory;
import net.weesli.rClaim.modal.Claim;
import net.weesli.rClaim.utils.PlayerUtils;
import net.weesli.rozsLib.color.ColorBuilder;
import net.weesli.rozsLib.inventory.lasest.ClickableItemStack;
import net.weesli.rozsLib.inventory.lasest.InventoryBuilder;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

public class ClaimUsersMenu implements ClaimInventory {
    @Override
    public void openInventory(Player player, Claim claim, FileConfiguration config) {
        InventoryBuilder builder = new InventoryBuilder().title(ColorBuilder.convertColors(config.getString("members-menu.title"))).size(config.getInt("members-menu.size"));
        int i = 0;
        for (UUID member : claim.getMembers()){
            ClickableItemStack itemStack = new ClickableItemStack(getItemStack("members-menu.item-settings", config), i);
            itemStack.setSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
            ItemMeta meta = itemStack.getItemStack().getItemMeta();
            meta.setDisplayName(meta.getDisplayName().replaceAll("<name>", Bukkit.getOfflinePlayer(member).getName()));
            itemStack.getItemStack().setItemMeta(meta);
            builder.setItem(itemStack,event-> {
                if (event.isShiftClick()){
                    VerifyMenu verifyMenu = RClaim.getInstance().getUiManager().getVerifyMenu();
                    verifyMenu.setup(VerifyAction.UNTRUST_PLAYER,member.toString());
                    RClaim.getInstance().getUiManager().openInventory(player,claim,verifyMenu);
                    return;
                }
                ClaimPermissionMenu permissionMenu = RClaim.getInstance().getUiManager().getPermissionMenu();
                permissionMenu.setup(member);
                RClaim.getInstance().getUiManager().openInventory(player,claim,permissionMenu);
            }); i++;
        }
        builder.setItem(config.getInt("members-menu.add-member.slot"), getItemStack("members-menu.add-member", config),event -> callSign(player, claim));
        builder.openInventory(player);
    }

    private void callSign(Player player, Claim claim){
        SignGUI gui = SignGUI.builder()
                .setLines("", "----------","Enter player name", "----------")
                .setType(Material.DARK_OAK_SIGN)

                .setColor(DyeColor.WHITE)

                .setHandler((p, result) -> {
                    String name = result.getLine(0);

                    if (name.isEmpty()) {
                        return Collections.emptyList();
                    }else {
                        if (name.equals(player.getName())){
                            Bukkit.getScheduler().runTask(RClaim.getInstance(),()-> player.getWorld().playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 5, 1));
                            return new ArrayList<>(Collections.singleton(SignGUIAction.displayNewLines("", "----------", "Enter player name", "----------")));
                        }else if (PlayerUtils.getPlayer(name) == null){
                            Bukkit.getScheduler().runTask(RClaim.getInstance(),()-> player.getWorld().playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 5, 1));
                            return new ArrayList<>(Collections.singleton(SignGUIAction.displayNewLines("", "----------", "Player not found", "----------")));
                        }else if (claim.getMembers().contains(Bukkit.getOfflinePlayer(name).getUniqueId())){
                            Bukkit.getScheduler().runTask(RClaim.getInstance(),()-> player.getWorld().playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 5, 1));
                            return new ArrayList<>(Collections.singleton(SignGUIAction.displayNewLines("", "----------", "Enter player name", "----------")));
                        }else {
                            Bukkit.getScheduler().runTask(RClaim.getInstance(),()-> player.performCommand("claim trust " + name));
                            Bukkit.getScheduler().runTask(RClaim.getInstance(),()-> player.getWorld().playSound(player.getLocation(),Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 5, 1));
                            return Collections.emptyList();
                        }
                    }
                })
                .build();
        gui.open(player);
    }
}
