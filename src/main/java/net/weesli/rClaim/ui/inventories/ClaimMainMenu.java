package net.weesli.rClaim.ui.inventories;

import net.weesli.rClaim.RClaim;
import net.weesli.rClaim.ui.ClaimInventory;
import net.weesli.rClaim.modal.Claim;
import net.weesli.rozsLib.color.ColorBuilder;
import net.weesli.rozsLib.inventory.ClickableItemStack;
import net.weesli.rozsLib.inventory.InventoryBuilder;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class ClaimMainMenu implements ClaimInventory {

    @Override
    public void openInventory(Player player, Claim claim, FileConfiguration config) {
        InventoryBuilder builder = new InventoryBuilder(RClaim.getInstance(), ColorBuilder.convertColors( config.getString("main-menu.title")), config.getInt("main-menu.size"));
        builder.setItem(config.getInt("main-menu.children.claims.slot"),new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack("main-menu.children.claims"), builder.build())
                .setCancelled(true)
                .setEvent(event -> RClaim.getInstance().getUiManager().openInventory(player,claim,RClaim.getInstance().getUiManager().getClaimsMenu())));
        builder.setItem(config.getInt("main-menu.children.upgrade-claim.slot"),new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack("main-menu.children.upgrade-claim"), builder.build())
                .setEvent(event -> RClaim.getInstance().getUiManager().openInventory(player,claim,RClaim.getInstance().getUiManager().getResizeInventory()))
                .setCancelled(true));
        builder.setItem(config.getInt("main-menu.children.members.slot"),new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack("main-menu.children.members"), builder.build())
                .setEvent(event -> RClaim.getInstance().getUiManager().openInventory(player,claim,RClaim.getInstance().getUiManager().getUsersMenu()))
                .setCancelled(true));
        builder.setItem(config.getInt("main-menu.children.options.slot"),new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack("main-menu.children.options"), builder.build())
                .setEvent(event-> RClaim.getInstance().getUiManager().openInventory(player,claim, RClaim.getInstance().getUiManager().getSettingsMenu()))
                .setCancelled(true));
        player.openInventory(builder.build());
    }
}
