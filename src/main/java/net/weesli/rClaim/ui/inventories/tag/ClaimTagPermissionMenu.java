package net.weesli.rClaim.ui.inventories.tag;

import net.weesli.rClaim.RClaim;
import net.weesli.rClaim.enums.ClaimPermission;
import net.weesli.rClaim.modal.ClaimTag;
import net.weesli.rClaim.utils.ClaimManager;
import net.weesli.rozsLib.inventory.lasest.InventoryBuilder;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ClaimTagPermissionMenu implements TagInventory{

    private final int[] item_cluster = {10,12,14,16,28,30,32,34,46,48,50,52};

    @Override
    public void openInventory(Player player, ClaimTag tag, FileConfiguration config) {
        InventoryBuilder builder = new InventoryBuilder().title(config.getString("tag-permissions-menu.title")).size(54);
        for (int i = 0; i < item_cluster.length; i++){
            ClaimPermission permission = ClaimPermission.values()[i];
            showPermissionItem(player,builder, permission, tag, config, item_cluster[i]);
        }
        builder.openInventory(player);
    }



    private void showPermissionItem(Player player, InventoryBuilder builder, ClaimPermission permission, ClaimTag tag, FileConfiguration config, int slot){
        ItemStack itemStack = getItemStack("tag-permissions-menu.item-settings",config);
        if (tag.hasPermission(permission)){
            itemStack.setType(Material.LIME_DYE);
        }else {
            itemStack.setType(Material.GRAY_DYE);
        }
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(meta.getDisplayName().replaceAll("<permission>", permission.getDisplayName()));
        meta.setLore(meta.getLore().stream().map(line -> line.replaceAll("%status%", ClaimManager.getStatus(tag.hasPermission(permission)))).toList());
        itemStack.setItemMeta(meta);
        builder.setItem(slot, itemStack, event -> {
            if (tag.hasPermission(permission)){
                tag.removePermission(permission);
            }else {
                tag.addPermission(permission);
            }
            RClaim.getInstance().getUiManager().openTagInventory(player, tag, "permission");
        });
    }
}
