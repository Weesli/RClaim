package net.weesli.rClaim.ui;

import net.weesli.rClaim.modal.Claim;
import net.weesli.rozsLib.color.ColorBuilder;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.stream.Collectors;

public interface ClaimInventory {

    void openInventory(Player player, Claim claim, FileConfiguration config);

    default ItemStack getItemStack(String path, FileConfiguration config){
        ItemStack itemStack = new ItemStack(Material.getMaterial(config.getString(path + ".material")));
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ColorBuilder.convertColors(config.getString(path + ".title")));
        List<String> lore = config.getStringList(path + ".lore");
        itemMeta.setLore(lore.stream().map(ColorBuilder::convertColors).collect(Collectors.toList()));
        if (config.getInt(path + ".custom-model-data") != 0){
            itemMeta.setCustomModelData(config.getInt(path + ".custom-model-data"));
        }
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
