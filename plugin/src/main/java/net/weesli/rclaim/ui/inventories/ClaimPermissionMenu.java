package net.weesli.rclaim.ui.inventories;

import me.clip.placeholderapi.PlaceholderAPI;
import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.model.Claim;
import net.weesli.rclaim.api.permission.ClaimPermissionRegistry;
import net.weesli.rclaim.config.ConfigLoader;
import net.weesli.rclaim.config.lang.MenuConfig;
import net.weesli.rclaim.ui.ClaimInventory;
import net.weesli.rclaim.util.BaseUtil;
import net.weesli.rozslib.inventory.types.PageableInventory;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ClaimPermissionMenu extends ClaimInventory {
    private List<String> alreadyAdded = new java.util.ArrayList<>();
    private UUID target;

    public void setup(UUID target) {
        this.target = target;
    }

    private static final MenuConfig.PageableMenu menu = (MenuConfig.PageableMenu) ConfigLoader.getMenuConfig().getPermissionsMenu();

    @Override
    public void openInventory(Player player, Claim claim) {
        PageableInventory inventory = new PageableInventory(PlaceholderAPI.setPlaceholders(player,menu.getTitle()),menu.getSize(),
                menu.getPreviousItem().asClickableItemStack(player),
                menu.getNextItem().asClickableItemStack(player));
        inventory.setLayout(menu.getFillerSlots()
                .stream()
                .mapToInt(Integer::intValue)
                .toArray()).fill(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), menu.isAutoFill());
        for (Map.Entry<String, ClaimPermissionRegistry> registryEntry : RClaim.getInstance().getPermissionService().getRegistries().entrySet()) {
            addClickableItem(inventory,player,claim, registryEntry.getKey());
        }
        inventory.openInventory(player);
    }

    private void addClickableItem(PageableInventory builder, Player player, Claim claim, String key) {
        String anotherKey = key.replaceAll("_", "-").toLowerCase();
        if (alreadyAdded.contains(anotherKey) || alreadyAdded.contains(key)) return;
        alreadyAdded.add(anotherKey);
        alreadyAdded.add(key);
        MenuConfig.MenuItem item = menu.getItems().get(anotherKey) == null ? menu.getItems().get(key) : menu.getItems().get(anotherKey);
        if (item == null) return;
        ItemStack itemStack = getItemStack(item,player);
        String statusPlaceholder = claim.checkPermission(target, key) ? BaseUtil.getStatus(true) : BaseUtil.getStatus(false);

        ItemMeta meta = itemStack.getItemMeta();
        List<String> lore = meta.hasLore() ? meta.getLore().stream()
                .map(line -> line.replace("%status%", statusPlaceholder))
                .toList() : null;
        meta.setLore(lore);
        itemStack.setItemMeta(meta);

        ItemStack target_item = setupFlagItem(itemStack);

        String finalKey = key;
        builder.addItem(target_item, event -> {
            InteractPlayerPermission(target, claim, finalKey);
            alreadyAdded.clear();
            openInventory(player, claim);
        });
    }

    private ItemStack setupFlagItem(ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    private void InteractPlayerPermission(UUID target, Claim claim, String key) {
        if (claim.checkPermission(target, key)) {
            claim.removePermission(target, key);
        } else {
            claim.addPermission(target, key);
        }
    }


}
