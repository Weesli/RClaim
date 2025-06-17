package net.weesli.rclaim.util;

import net.weesli.rclaim.config.ConfigLoader;
import net.weesli.rozslib.color.ColorBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/*
 * since 2.2.0
 */
public class ClaimBlockUtil {

    public static void giveBlock(Player player, int amount){
        ItemStack itemStack = getItem();
        itemStack.setAmount(amount);
        player.getInventory().addItem(itemStack);
    }


    private static ItemStack getItem(){
        ItemStack itemStack = new ItemStack(Material.getMaterial(ConfigLoader.getConfig().getClaimBlock().getItem().getMaterial()));
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(ColorBuilder.convertColors(ConfigLoader.getConfig().getClaimBlock().getItem().getTitle()));
        meta.setLore(ConfigLoader.getConfig().getClaimBlock().getItem().getLore().stream().map(ColorBuilder::convertColors).toList());
        int modelData = ConfigLoader.getConfig().getClaimBlock().getItem().getCustomModelData();
        if (modelData != 0){
            meta.setCustomModelData(modelData);
        }
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static boolean isSimilar(ItemStack itemStack){
        ItemStack item = getItem();
        return itemStack.getType() == item.getType() && itemStack.hasItemMeta() && itemStack.getItemMeta().equals(item.getItemMeta());
    }

    public static boolean isEnabled(){
        return ConfigLoader.getConfig().getClaimBlock().isEnabled();
    }



}
