package net.weesli.rclaim.ui.inventories.tag;

import me.clip.placeholderapi.PlaceholderAPI;
import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.model.ClaimTag;
import net.weesli.rclaim.config.ConfigLoader;
import net.weesli.rclaim.config.lang.MenuConfig;
import net.weesli.rclaim.input.TextInputManager;
import net.weesli.rclaim.ui.TagInventory;
import net.weesli.rozslib.inventory.ClickableItemStack;
import net.weesli.rozslib.inventory.types.PageableInventory;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.UUID;
import static net.weesli.rclaim.config.lang.LangConfig.sendMessageToPlayer;
public class ClaimTagUsersMenu extends TagInventory {

    private final MenuConfig.PageableMenu menu =  ConfigLoader.getMenuConfig().getTagUsersMenu();

    @Override
    public void openInventory(Player player, ClaimTag tag) {
        PageableInventory inventory = new PageableInventory(PlaceholderAPI.setPlaceholders(player,menu.getTitle()), menu.getSize(),
                menu.getPreviousItem().asClickableItemStack(player),
                menu.getNextItem().asClickableItemStack(player),
                menu.getItems().get("add-user").getIndex()
        );
        inventory.setLayout(menu.getFillerSlots()
                .stream()
                .mapToInt(Integer::intValue)
                .toArray()).fill(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), menu.isAutoFill());
        inventory.addStaticItem(new ClickableItemStack(getItemStack(menu.getItems().get("add-user"),player),menu.getItems().get("add-user").getIndex()),
                event -> {
                    Bukkit.getScheduler().runTask(RClaim.getInstance(), () -> player.closeInventory());
                    sendMessageToPlayer("ENTER_A_PLAYER_NAME", player);
                    RClaim.getInstance().getTextInputManager().runAction(
                            player,
                            TextInputManager.TextInputAction.ADD_PLAYER_TO_TAG,
                            tag
                    );
                });
        for (UUID uuid : tag.getUsers()){
            ItemStack itemStack = getItemStack(menu.getItems().get("item-settings"),player);
            ItemMeta meta = itemStack.getItemMeta();
            meta.setDisplayName(meta.getDisplayName().replaceAll("<name>", Bukkit.getOfflinePlayer(uuid).getName()));
            itemStack.setItemMeta(meta);
            ClickableItemStack clickableItemStack  = new ClickableItemStack(itemStack, 0);
            clickableItemStack.setSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
            inventory.setItem(clickableItemStack,event -> {
                tag.removeUser(uuid);
                RClaim.getInstance().getUiManager().openTagInventory(player,tag, ClaimTagEditMenu.class);
            });
        }

        inventory.openDefaultInventory(player);
    }
}