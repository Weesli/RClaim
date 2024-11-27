package net.weesli.rClaim.utils;

import net.weesli.rClaim.RClaim;
import net.weesli.rozsLib.color.ColorBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/*
 * since 2.2.0
 */
public class ClaimBlockUtils {

    public static void giveBlock(Player player, int amount){
        ItemStack itemStack = getItem();
        itemStack.setAmount(amount);
        player.getInventory().addItem(itemStack);
    }


    private static ItemStack getItem(){
        String path = "options.claim-block.item";
        ItemStack itemStack = new ItemStack(Material.getMaterial(RClaim.getInstance().getConfig().getString(path + ".material")));
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(ColorBuilder.convertColors(RClaim.getInstance().getConfig().getString(path + ".title")));
        meta.setLore(RClaim.getInstance().getConfig().getStringList(path + ".lore").stream().map(ColorBuilder::convertColors).toList());
        int modelData = RClaim.getInstance().getConfig().getInt(path + ".custom-model-data");
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
        return RClaim.getInstance().getConfig().getBoolean("options.claim-block.enabled");
    }



}
