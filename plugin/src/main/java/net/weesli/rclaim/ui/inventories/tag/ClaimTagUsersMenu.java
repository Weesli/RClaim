package net.weesli.rclaim.ui.inventories.tag;

import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.model.ClaimTag;
import net.weesli.rclaim.config.ConfigLoader;
import net.weesli.rclaim.config.adapter.model.Menu;
import net.weesli.rclaim.input.TextInputManager;
import net.weesli.rclaim.ui.TagInventory;
import net.weesli.rozslib.inventory.ClickableItemStack;
import net.weesli.rozslib.inventory.types.PageableInventory;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class ClaimTagUsersMenu extends TagInventory {

    private final Menu menu = ConfigLoader.getMenuConfig().getTagUsersMenu();

    @Override
    public void openInventory(Player player, ClaimTag tag) {
        PageableInventory builder = new PageableInventory(menu.getTitle(), 27,
                new ClickableItemStack(getItemStack(ConfigLoader.getConfig().getPublicMenu().getPreviousItem()),21),
                new ClickableItemStack(getItemStack(ConfigLoader.getConfig().getPublicMenu().getNextItem()),23),
                menu.getItems().get("add-user").getIndex(), 21,23
        );
        builder.addStaticItem(new ClickableItemStack(getItemStack(menu.getItems().get("add-user")),menu.getItems().get("add-user").getIndex()),
                event -> RClaim.getInstance().getTextInputManager().runAction(
                        player,
                        TextInputManager.TextInputAction.ADD_PLAYER_TO_TAG,
                        tag
                ));
        builder.setLayout("""
                *********
                *       *
                ***   ***
                """).fill(new ItemStack(Material.GRAY_STAINED_GLASS_PANE),false);
        for (UUID uuid : tag.getUsers()){
            ItemStack itemStack = getItemStack(menu.getItems().get("item-settings"));
            ItemMeta meta = itemStack.getItemMeta();
            meta.setDisplayName(meta.getDisplayName().replaceAll("<name>", Bukkit.getOfflinePlayer(uuid).getName()));
            itemStack.setItemMeta(meta);
            ClickableItemStack clickableItemStack  = new ClickableItemStack(itemStack, 0);
            clickableItemStack.setSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
            builder.setItem(clickableItemStack,event -> {
                tag.removeUser(uuid);
                RClaim.getInstance().getUiManager().openTagInventory(player,tag, ClaimTagEditMenu.class);
            });
        }

        builder.openDefaultInventory(player);
    }
}