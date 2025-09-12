package net.weesli.rclaim.ui.inventories.tag;

import me.clip.placeholderapi.PlaceholderAPI;
import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.model.ClaimTag;
import net.weesli.rclaim.api.permission.ClaimPermissionRegistry;
import net.weesli.rclaim.config.ConfigLoader;
import net.weesli.rclaim.config.lang.MenuConfig;
import net.weesli.rclaim.ui.TagInventory;
import net.weesli.rclaim.util.BaseUtil;
import net.weesli.rozslib.inventory.types.PageableInventory;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ClaimTagPermissionMenu extends TagInventory {
    private MenuConfig.PageableMenu menu = ConfigLoader.getMenuConfig().getTagPermissionsMenu();


    @Override
    public void openInventory(Player player, ClaimTag tag) {
        PageableInventory inventory = new PageableInventory(PlaceholderAPI.setPlaceholders(player,menu.getTitle()), menu.getSize(),
                menu.getPreviousItem().asClickableItemStack(player),
                menu.getNextItem().asClickableItemStack(player));
        inventory.setLayout(menu.getFillerSlots()
                .stream()
                .mapToInt(Integer::intValue)
                .toArray()).fill(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), menu.isAutoFill());
        for (Map.Entry<String, ClaimPermissionRegistry> registryEntry : RClaim.getInstance().getPermissionService().getRegistries().entrySet()) {
            addClickableItem(player,inventory,tag, registryEntry.getKey());
        }
        inventory.openInventory(player);
    }



    private void addClickableItem(Player player, PageableInventory builder, ClaimTag tag,String key){
        menu =  ConfigLoader.getMenuConfig().getPermissionsMenu(); // this is an exception, only for the permission menu. Tag permission menu and classic permission menu use same items in config.
        String anotherKey = key.replaceAll("_", "-").toLowerCase();
        MenuConfig.MenuItem item = menu.getItems().get(anotherKey) == null ? menu.getItems().get(key) : menu.getItems().get(anotherKey);
        if (item == null) return;
        ItemStack itemStack = getItemStack(item,player);
        String statusPlaceholder = tag.hasPermission(key) ? BaseUtil.getStatus(true) : BaseUtil.getStatus(false);

        ItemMeta meta = itemStack.getItemMeta();
        List<String> lore = meta.getLore().stream()
                .map(line -> line.replace("%status%", statusPlaceholder))
                .toList();
        meta.setLore(lore);
        itemStack.setItemMeta(meta);

        ItemStack target_item = setupFlagItem(itemStack);

        String finalKey = key;
        builder.addItem(target_item, event -> {
            InteractPlayerPermission(tag, finalKey);
            openInventory(player, tag);
        });
    }

    private ItemStack setupFlagItem(ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    private void InteractPlayerPermission(ClaimTag tag, String key) {
        if (tag.hasPermission(key)) {
            tag.removePermission(key);
        } else {
            tag.addPermission(key);
        }
    }
}
