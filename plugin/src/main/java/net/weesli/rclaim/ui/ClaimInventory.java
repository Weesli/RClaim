package net.weesli.rclaim.ui;

import net.weesli.rclaim.api.model.Claim;
import net.weesli.rclaim.config.adapter.model.MenuItem;
import net.weesli.rclaim.model.ClaimImpl;
import net.weesli.rozslib.color.ColorBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class ClaimInventory {

    public abstract void openInventory(Player player, Claim claim);

    public ItemStack getItemStack(MenuItem menuItem){
        ItemStack itemStack = new ItemStack(Material.getMaterial(menuItem.getMaterial()));
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(ColorBuilder.convertColors(menuItem.getTitle()));
        meta.setLore(menuItem.getLore().stream().map(ColorBuilder::convertColors).toList());
        if (menuItem.getCustomModelData() != 0){
            meta.setCustomModelData(menuItem.getCustomModelData());
        }
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemStack.setItemMeta(meta);
        return itemStack;
    }
}
