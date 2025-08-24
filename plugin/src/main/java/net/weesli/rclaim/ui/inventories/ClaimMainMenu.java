package net.weesli.rclaim.ui.inventories;

import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.model.Claim;
import net.weesli.rclaim.config.ConfigLoader;
import net.weesli.rclaim.config.adapter.model.Menu;
import net.weesli.rclaim.config.adapter.model.MenuItem;
import net.weesli.rclaim.ui.ClaimInventory;
import net.weesli.rclaim.model.ClaimImpl;
import net.weesli.rclaim.ui.inventories.tag.ClaimTagMainMenu;
import net.weesli.rozslib.inventory.ClickableItemStack;
import net.weesli.rozslib   .inventory.types.SimpleInventory;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class ClaimMainMenu extends ClaimInventory {

    private final Menu menu = ConfigLoader.getMenuConfig().getMainMenu();

    @Override
    public void openInventory(Player player, Claim claim) {
        SimpleInventory builder = new SimpleInventory(menu.getTitle(),menu.getSize());
        builder.setLayout("").fill(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), true);
        for (Map.Entry<String, MenuItem> item : menu.getItems().entrySet()){
            switch (item.getKey()){
                case "claims" -> builder.setItem(new ClickableItemStack(getItemStack(item.getValue()),item.getValue().getIndex()),
                        event -> RClaim.getInstance().getUiManager().openInventory(player, claim, ClaimsMenu.class));
                case "upgrade-claim" -> builder.setItem(new ClickableItemStack(getItemStack(item.getValue()),item.getValue().getIndex()),
                        event -> RClaim.getInstance().getUiManager().openInventory(player, claim, ClaimResizeInventory.class));
                case "members" -> builder.setItem(new ClickableItemStack(getItemStack(item.getValue()), item.getValue().getIndex()),
                        event -> RClaim.getInstance().getUiManager().openInventory(player, claim, ClaimUsersMenu.class));
                case "options" -> builder.setItem(new ClickableItemStack(getItemStack(item.getValue()), item.getValue().getIndex()),
                        event -> RClaim.getInstance().getUiManager().openInventory(player, claim, ClaimSettingsMenu.class));
                case "effects" -> {
                    if(ConfigLoader.getConfig().getEffects().isEnabled()){
                        builder.setItem(new ClickableItemStack(getItemStack(item.getValue()),item.getValue().getIndex()),
                                event -> RClaim.getInstance().getUiManager().openInventory(player, claim, ClaimEffectMenu.class));
                    }
                }
                case "block" -> builder.setItem(new ClickableItemStack(getItemStack(item.getValue()),item.getValue().getIndex()),
                        event -> RClaim.getInstance().getUiManager().openInventory(player, claim, ClaimBlockMenu.class));
                case "tags" -> builder.setItem(new ClickableItemStack(getItemStack(item.getValue()),item.getValue().getIndex()),
                        event -> RClaim.getInstance().getUiManager().openInventory(player, claim, ClaimTagMainMenu.class));
            }
        }
        builder.openInventory(player);
    }
}
