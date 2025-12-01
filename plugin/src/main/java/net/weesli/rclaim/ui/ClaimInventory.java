package net.weesli.rclaim.ui;

import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.weesli.rclaim.api.model.Claim;
import net.weesli.rclaim.config.lang.MenuConfig;
import net.weesli.rozslib.inventory.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

public abstract class ClaimInventory {

    public abstract void openInventory(Player player, Claim claim);

    public ItemStack getItemStack(MenuConfig.MenuItem menuItem, @Nullable Player player, TagResolver... tags){
        Material material = Material.getMaterial(menuItem.getMaterial());
        return ItemBuilder.of(material != null ? material : Material.BEDROCK)
                .name(menuItem.getTitle(),player, tags)
                .lore(menuItem.getLore(),player, tags)
                .hideFlags()
                .customModelData(menuItem.getCustomModelData())
                .build();
    }
}
