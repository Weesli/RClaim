package net.weesli.rClaim.ui.inventories;

import net.weesli.rClaim.RClaim;
import net.weesli.rClaim.ui.ClaimInventory;
import net.weesli.rClaim.modal.Claim;
import net.weesli.rozsLib.color.ColorBuilder;
import net.weesli.rozsLib.inventory.lasest.InventoryBuilder;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class ClaimMainMenu implements ClaimInventory {

    @Override
    public void openInventory(Player player, Claim claim, FileConfiguration config) {
        InventoryBuilder builder = new InventoryBuilder().title(ColorBuilder.convertColors( config.getString("main-menu.title"))).size(config.getInt("main-menu.size"));
        builder.setItem(config.getInt("main-menu.children.claims.slot"),
                getItemStack("main-menu.children.claims",config),
                event -> RClaim.getInstance().getUiManager().openInventory(player, claim, RClaim.getInstance().getUiManager().getClaimsMenu()));
        builder.setItem(config.getInt("main-menu.children.upgrade-claim.slot"),
                getItemStack("main-menu.children.upgrade-claim", config),
                event -> RClaim.getInstance().getUiManager().openInventory(player, claim, RClaim.getInstance().getUiManager().getResizeInventory()));
        builder.setItem(config.getInt("main-menu.children.members.slot"),
                getItemStack("main-menu.children.members", config),
                event -> RClaim.getInstance().getUiManager().openInventory(player, claim, RClaim.getInstance().getUiManager().getUsersMenu()));
        builder.setItem(config.getInt("main-menu.children.options.slot"),
                getItemStack("main-menu.children.options", config),
                event -> RClaim.getInstance().getUiManager().openInventory(player, claim, RClaim.getInstance().getUiManager().getSettingsMenu()));
        builder.setItem(config.getInt("main-menu.children.effects.slot"), getItemStack("main-menu.children.effects", config),
                event -> RClaim.getInstance().getUiManager().openInventory(player,claim,RClaim.getInstance().getUiManager().getEffectMenu()));
        builder.setItem(config.getInt("main-menu.children.block.slot"), getItemStack("main-menu.children.block", config),
                event -> RClaim.getInstance().getUiManager().openInventory(player,claim,RClaim.getInstance().getUiManager().getBlockMenu()));
        builder.openInventory(player);
    }
}
