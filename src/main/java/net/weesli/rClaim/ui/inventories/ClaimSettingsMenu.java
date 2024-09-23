package net.weesli.rClaim.ui.inventories;

import net.weesli.rClaim.RClaim;
import net.weesli.rClaim.api.events.ClaimStatusChangeEvent;
import net.weesli.rClaim.enums.ClaimStatus;
import net.weesli.rClaim.ui.ClaimInventory;
import net.weesli.rClaim.modal.Claim;
import net.weesli.rClaim.utils.ClaimManager;
import net.weesli.rozsLib.color.ColorBuilder;
import net.weesli.rozsLib.inventory.lasest.ClickableItemStack;
import net.weesli.rozsLib.inventory.lasest.InventoryBuilder;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ClaimSettingsMenu implements ClaimInventory {
    @Override
    public void openInventory(Player player, Claim claim, FileConfiguration config) {
        InventoryBuilder builder = new InventoryBuilder().title(ColorBuilder.convertColors(config.getString("options-menu.title"))).size(config.getInt("options-menu.size"));
        addClickableItem(builder, claim, player, ClaimStatus.SPAWN_ANIMAL, "spawn-animal", config);
        addClickableItem(builder, claim, player, ClaimStatus.SPAWN_MONSTER, "spawn-monster", config);
        addClickableItem(builder, claim, player, ClaimStatus.PVP, "pvp", config);
        addClickableItem(builder, claim, player, ClaimStatus.EXPLOSION, "explosion", config);
        addClickableItem(builder, claim, player, ClaimStatus.SPREAD, "spread", config);
        addClickableItem(builder,claim,player,ClaimStatus.TIME, "time", config);
        addClickableItem(builder,claim,player,ClaimStatus.WEATHER, "weather", config);
        builder.openInventory(player);
    }

    private void addClickableItem(InventoryBuilder builder, Claim claim, Player player, ClaimStatus status, String configPath, FileConfiguration config) {
        String itemPath = "options-menu.children." + configPath;
        ItemStack itemStack = getItemStack(itemPath,config);

        ClickableItemStack clickableItem = new ClickableItemStack(itemStack, config.getInt(itemPath + ".slot"));
        ItemMeta meta = clickableItem.getItemStack().getItemMeta();
        if (claim.checkStatus(status)) {
            meta.setLore(meta.getLore().stream().map(line-> line.replaceAll("%status%", ClaimManager.getStatus(true))).toList());
        }else {
            meta.setLore(meta.getLore().stream().map(line-> line.replaceAll("%status%", ClaimManager.getStatus(false))).toList());
        }
        clickableItem.getItemStack().setItemMeta(meta);
        builder.setItem(clickableItem,event -> {
            if (claim.checkStatus(status)) {
                claim.removeClaimStatus(status);
                ClaimStatusChangeEvent changeEvent = new ClaimStatusChangeEvent(player,claim,status,false);
                RClaim.getInstance().getServer().getPluginManager().callEvent(changeEvent);
            } else {
                claim.addClaimStatus(status);
                ClaimStatusChangeEvent changeEvent = new ClaimStatusChangeEvent(player,claim,status,true);
                RClaim.getInstance().getServer().getPluginManager().callEvent(changeEvent);

            }
            openInventory(player,claim,config);
        });
    }
}
