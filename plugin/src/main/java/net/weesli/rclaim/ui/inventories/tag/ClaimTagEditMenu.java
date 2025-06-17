package net.weesli.rclaim.ui.inventories.tag;

import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.model.ClaimTag;
import net.weesli.rclaim.config.ConfigLoader;
import net.weesli.rclaim.config.adapter.model.Menu;
import net.weesli.rclaim.config.adapter.model.MenuItem;
import net.weesli.rclaim.model.ClaimTagImpl;
import net.weesli.rclaim.ui.TagInventory;
import net.weesli.rozslib.inventory.ClickableItemStack;
import net.weesli.rozslib.inventory.types.SimpleInventory;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class ClaimTagEditMenu extends TagInventory {

    private final Menu menu = ConfigLoader.getMenuConfig().getTagEditMenu();

    @Override
    public void openInventory(Player player, ClaimTag tag) {
        SimpleInventory builder = new SimpleInventory(menu.getTitle(),menu.getSize());
        builder.setLayout("").fill(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), true);
        for (Map.Entry<String, MenuItem> item : menu.getItems().entrySet()){
            ClickableItemStack itemStack = new ClickableItemStack(getItemStack(item.getValue()), item.getValue().getIndex());
            itemStack.setSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
            builder.setItem(itemStack, e -> {
                if (item.getKey().equalsIgnoreCase("users")){
                    RClaim.getInstance().getUiManager().openTagInventory(player,tag, ClaimTagUsersMenu.class);
                }else if (item.getKey().equalsIgnoreCase("permissions")){
                    RClaim.getInstance().getUiManager().openTagInventory(player, tag, ClaimTagPermissionMenu.class);
                }
            });
        }

        builder.openInventory(player);
    }

}
