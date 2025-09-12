package net.weesli.rclaim.ui.inventories.tag;

import me.clip.placeholderapi.PlaceholderAPI;
import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.model.ClaimTag;
import net.weesli.rclaim.config.ConfigLoader;
import net.weesli.rclaim.config.lang.MenuConfig;
import net.weesli.rclaim.ui.TagInventory;
import net.weesli.rozslib.inventory.ClickableItemStack;
import net.weesli.rozslib.inventory.types.SimpleInventory;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Map;

public class ClaimTagEditMenu extends TagInventory {

    private final MenuConfig.Menu menu = ConfigLoader.getMenuConfig().getTagEditMenu();

    @Override
    public void openInventory(Player player, ClaimTag tag) {
        SimpleInventory inventory = new SimpleInventory(PlaceholderAPI.setPlaceholders(player,menu.getTitle()),menu.getSize());
        inventory.setLayout(menu.getFillerSlots()
                .stream()
                .mapToInt(Integer::intValue)
                .toArray()).fill(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), menu.isAutoFill());
        for (Map.Entry<String, MenuConfig.MenuItem> item : menu.getItems().entrySet()){
            ClickableItemStack itemStack = new ClickableItemStack(getItemStack(item.getValue(),player), item.getValue().getIndex());
            itemStack.setSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
            inventory.setItem(itemStack, e -> {
                if (item.getKey().equalsIgnoreCase("users")){
                    RClaim.getInstance().getUiManager().openTagInventory(player,tag, ClaimTagUsersMenu.class);
                }else if (item.getKey().equalsIgnoreCase("permissions")){
                    RClaim.getInstance().getUiManager().openTagInventory(player, tag, ClaimTagPermissionMenu.class);
                }
            });
        }

        inventory.openInventory(player);
    }

}
