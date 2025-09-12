package net.weesli.rclaim.ui.inventories;

import me.clip.placeholderapi.PlaceholderAPI;
import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.model.Claim;
import net.weesli.rclaim.config.ConfigLoader;
import net.weesli.rclaim.config.lang.MenuConfig;
import net.weesli.rclaim.ui.ClaimInventory;
import net.weesli.rclaim.ui.inventories.tag.ClaimTagMainMenu;
import net.weesli.rozslib.inventory.ClickableItemStack;
import net.weesli.rozslib   .inventory.types.SimpleInventory;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class ClaimMainMenu extends ClaimInventory {

    private final MenuConfig.Menu menu = ConfigLoader.getMenuConfig().getMainMenu();

    @Override
    public void openInventory(Player player, Claim claim) {
        SimpleInventory inventory = new SimpleInventory(PlaceholderAPI.setPlaceholders(player,menu.getTitle()),menu.getSize());
        inventory.setLayout(menu.getFillerSlots()
                .stream()
                .mapToInt(Integer::intValue)
                .toArray()).fill(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), menu.isAutoFill());
        for (Map.Entry<String, MenuConfig.MenuItem> item : menu.getItems().entrySet()){
            switch (item.getKey()){
                case "claims" -> inventory.setItem(new ClickableItemStack(getItemStack(item.getValue(),player),item.getValue().getIndex()),
                        event -> RClaim.getInstance().getUiManager().openInventory(player, claim, ClaimsMenu.class));
                case "upgrade-claim" -> inventory.setItem(new ClickableItemStack(getItemStack(item.getValue(),player),item.getValue().getIndex()),
                        event -> RClaim.getInstance().getUiManager().openInventory(player, claim, ClaimResizeInventory.class));
                case "members" -> inventory.setItem(new ClickableItemStack(getItemStack(item.getValue(),player), item.getValue().getIndex()),
                        event -> RClaim.getInstance().getUiManager().openInventory(player, claim, ClaimUsersMenu.class));
                case "options" -> inventory.setItem(new ClickableItemStack(getItemStack(item.getValue(),player), item.getValue().getIndex()),
                        event -> RClaim.getInstance().getUiManager().openInventory(player, claim, ClaimSettingsMenu.class));
                case "effects" -> {
                    if(ConfigLoader.getConfig().getEffects().isEnabled()){
                        inventory.setItem(new ClickableItemStack(getItemStack(item.getValue(),player),item.getValue().getIndex()),
                                event -> RClaim.getInstance().getUiManager().openInventory(player, claim, ClaimEffectMenu.class));
                    }
                }
                case "block" -> inventory.setItem(new ClickableItemStack(getItemStack(item.getValue(),player),item.getValue().getIndex()),
                        event -> RClaim.getInstance().getUiManager().openInventory(player, claim, ClaimBlockMenu.class));
                case "tags" -> inventory.setItem(new ClickableItemStack(getItemStack(item.getValue(),player),item.getValue().getIndex()),
                        event -> RClaim.getInstance().getUiManager().openInventory(player, claim, ClaimTagMainMenu.class));
            }
        }
        inventory.openInventory(player);
    }
}
