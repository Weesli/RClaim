package net.weesli.rclaim.ui.inventories;

import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.model.Claim;
import net.weesli.rclaim.config.ConfigLoader;
import net.weesli.rclaim.config.adapter.model.Menu;
import net.weesli.rclaim.api.enums.VerifyAction;
import net.weesli.rclaim.input.TextInputManager;
import net.weesli.rclaim.ui.ClaimInventory;
import net.weesli.rozslib.inventory.ClickableItemStack;
import net.weesli.rozslib.inventory.types.PageableInventory;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class ClaimUsersMenu extends ClaimInventory {

    private final Menu menu = ConfigLoader.getMenuConfig().getMembersMenu();

    @Override
    public void openInventory(Player player, Claim claim){
        PageableInventory inventory = new PageableInventory(menu.getTitle(), 54,
                new ClickableItemStack(getItemStack(ConfigLoader.getConfig().getPublicMenu().getPreviousItem()), 0),
                new ClickableItemStack(getItemStack(ConfigLoader.getConfig().getPublicMenu().getNextItem()),0), 45,53,menu.getItems().get("add-member").getIndex());
        inventory.setLayout("""
                *********
                *       *
                *       *
                *       *
                *       *
                 *** ***
                """).fill(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), false);
        for (UUID member : claim.getMembers()){
            ItemStack itemStack = getItemStack(menu.getItems().get("item-settings"));
            ItemMeta meta = itemStack.getItemMeta();
            meta.setDisplayName(meta.getDisplayName().replaceAll("<name>", Bukkit.getOfflinePlayer(member).getName()));
            itemStack.setItemMeta(meta);
            inventory.addItem(itemStack,event-> {
                if (event.isShiftClick()){
                    VerifyMenu verifyMenu = new VerifyMenu();
                    verifyMenu.setup(VerifyAction.UNTRUST_PLAYER,member.toString());
                    verifyMenu.openInventory(player, claim);
                    return;
                }
                ClaimPermissionMenu permissionMenu = new ClaimPermissionMenu();
                permissionMenu.setup(member);
                permissionMenu.openInventory(player, claim);
            });
        }
        inventory.addStaticItem(new ClickableItemStack(getItemStack(menu.getItems().get("add-member")),menu.getItems().get("add-member").getIndex()),event ->{
            player.closeInventory();
            player.sendMessage(RClaim.getInstance().getMessage("ENTER_A_PLAYER_NAME"));
            RClaim.getInstance().getTextInputManager().runAction(player,
                    TextInputManager.TextInputAction.ADD_PLAYER_TO_CLAIM, claim);
        });
        inventory.openDefaultInventory(player);
    }
}
