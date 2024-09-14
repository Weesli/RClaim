package net.weesli.rClaim.ui.inventories;

import net.weesli.rClaim.RClaim;
import net.weesli.rClaim.enums.ClaimStatus;
import net.weesli.rClaim.ui.ClaimInventory;
import net.weesli.rClaim.modal.Claim;
import net.weesli.rClaim.utils.ClaimManager;
import net.weesli.rozsLib.color.ColorBuilder;
import net.weesli.rozsLib.inventory.ClickableItemStack;
import net.weesli.rozsLib.inventory.InventoryBuilder;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ClaimSettingsMenu implements ClaimInventory {
    @Override
    public void openInventory(Player player, Claim claim, FileConfiguration config) {
        InventoryBuilder builder = new InventoryBuilder(
                RClaim.getInstance(),
                ColorBuilder.convertColors(config.getString("options-menu.title")),
                config.getInt("options-menu.size")
        );
        addClickableItem(builder, claim, player, ClaimStatus.SPAWN_ANIMAL, "spawn-animal", config);
        addClickableItem(builder, claim, player, ClaimStatus.SPAWN_MONSTER, "spawn-monster", config);
        addClickableItem(builder, claim, player, ClaimStatus.PVP, "pvp", config);
        addClickableItem(builder, claim, player, ClaimStatus.EXPLOSION, "explosion", config);
        addClickableItem(builder, claim, player, ClaimStatus.SPREAD, "spread", config);

        player.openInventory(builder.build());
    }

    private void addClickableItem(InventoryBuilder builder, Claim claim, Player player, ClaimStatus status, String configPath, FileConfiguration config) {
        String itemPath = "options-menu.children." + configPath;
        ItemStack itemStack = RClaim.getInstance().getMenusFile().getItemStack(itemPath);

        ClickableItemStack clickableItem = new ClickableItemStack(RClaim.getInstance(), itemStack, builder.build())
                .setEvent(event -> {
                    if (claim.checkStatus(status)) {
                        claim.removeClaimStatus(status);
                    } else {
                        claim.addClaimStatus(status);
                    }
                    openInventory(player,claim,config);
                })
                .setCancelled(true);
        ItemMeta meta = clickableItem.getItemStack().getItemMeta();
        if (claim.checkStatus(status)) {
            meta.setLore(meta.getLore().stream().map(line-> line.replaceAll("%status%", ClaimManager.getStatus(true))).toList());
        }else {
            meta.setLore(meta.getLore().stream().map(line-> line.replaceAll("%status%", ClaimManager.getStatus(false))).toList());
        }
        clickableItem.getItemStack().setItemMeta(meta);
        builder.setItem(config.getInt(itemPath + ".slot"), clickableItem.getItemStack());
    }
}
