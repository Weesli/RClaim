package net.weesli.rclaim.ui.inventories.tag;

import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.model.ClaimTag;
import net.weesli.rclaim.config.ConfigLoader;
import net.weesli.rclaim.config.adapter.model.Menu;
import net.weesli.rclaim.api.enums.ClaimPermission;
import net.weesli.rclaim.ui.TagInventory;
import net.weesli.rclaim.util.BaseUtil;
import net.weesli.rozslib.inventory.ClickableItemStack;
import net.weesli.rozslib.inventory.types.SimpleInventory;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ClaimTagPermissionMenu extends TagInventory {

    private final int[] item_cluster = {10,12,14,16,28,30,32,34,46,48,50,52};

    private final Menu menu = ConfigLoader.getMenuConfig().getTagPermissionsMenu();


    @Override
    public void openInventory(Player player, ClaimTag tag) {
        SimpleInventory builder = new SimpleInventory(menu.getTitle(),menu.getSize());
        builder.setLayout("").fill(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), true);
        for (int i = 0; i < item_cluster.length; i++){
            ClaimPermission permission = ClaimPermission.values()[i];
            showPermissionItem(player,builder, permission, tag, item_cluster[i]);
        }
        builder.openInventory(player);
    }



    private void showPermissionItem(Player player, SimpleInventory builder, ClaimPermission permission, ClaimTag tag, int slot){
        ItemStack itemStack = getItemStack(menu.getItems().get("item-settings"));
        if (tag.hasPermission(permission)){
            itemStack.setType(Material.LIME_DYE);
        }else {
            itemStack.setType(Material.GRAY_DYE);
        }
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(meta.getDisplayName().replaceAll("<permission>", ConfigLoader.getConfig().getClaimPermissions().get(permission.name(),
                String.class)));
        meta.setLore(meta.getLore().stream().map(line -> line.replaceAll("%status%", BaseUtil.getStatus(tag.hasPermission(permission)))).toList());
        itemStack.setItemMeta(meta);
        builder.setItem(new ClickableItemStack(itemStack,slot), event -> {
            if (tag.hasPermission(permission)){
                tag.removePermission(permission);
            }else {
                tag.addPermission(permission);
            }
            RClaim.getInstance().getUiManager().openTagInventory(player, tag, ClaimTagPermissionMenu.class);
        });
    }
}
