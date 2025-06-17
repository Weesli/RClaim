package net.weesli.rclaim.ui.inventories;

import net.weesli.rclaim.api.model.Claim;
import net.weesli.rclaim.config.ConfigLoader;
import net.weesli.rclaim.config.adapter.model.Menu;
import net.weesli.rclaim.config.adapter.model.MenuItem;
import net.weesli.rclaim.api.enums.ClaimPermission;
import net.weesli.rclaim.model.ClaimImpl;
import net.weesli.rclaim.ui.ClaimInventory;
import net.weesli.rclaim.util.BaseUtil;
import net.weesli.rozslib.inventory.ClickableItemStack;
import net.weesli.rozslib.inventory.types.SimpleInventory;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.UUID;

public class ClaimPermissionMenu extends ClaimInventory {


    private UUID target;

    public void setup(UUID target) {
        this.target = target;
    }

    private static final Menu menu = ConfigLoader.getMenuConfig().getPermissionsMenu();

    @Override
    public void openInventory(Player player, Claim claim) {
        SimpleInventory builder = new SimpleInventory(menu.getTitle(),menu.getSize());
        builder.setLayout("").fill(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), true);
        addClickableItemWithStatus(builder, player, claim, ClaimPermission.BLOCK_BREAK, "block-break");
        addClickableItemWithStatus(builder, player, claim, ClaimPermission.BLOCK_PLACE, "block-place");
        addClickableItemWithStatus(builder, player, claim, ClaimPermission.PICKUP_ITEM, "pickup-item");
        addClickableItemWithStatus(builder, player, claim, ClaimPermission.DROP_ITEM, "drop-item");
        addClickableItemWithStatus(builder, player, claim, ClaimPermission.CONTAINER_OPEN, "container-open");
        addClickableItemWithStatus(builder, player, claim, ClaimPermission.INTERACT_ENTITY, "interact-entity");
        addClickableItemWithStatus(builder, player, claim, ClaimPermission.ATTACK_ANIMAL, "attack-animal");
        addClickableItemWithStatus(builder, player, claim, ClaimPermission.ATTACK_MONSTER, "attack-monster");
        addClickableItemWithStatus(builder, player, claim, ClaimPermission.BREAK_CONTAINER, "break-container");
        addClickableItemWithStatus(builder, player, claim, ClaimPermission.USE_DOOR, "use-door");
        addClickableItemWithStatus(builder, player, claim, ClaimPermission.USE_PORTAL, "use-portal");
        addClickableItemWithStatus(builder, player, claim, ClaimPermission.USE_POTION, "use-potion");

        builder.openInventory(player);
    }

    private void addClickableItemWithStatus(SimpleInventory builder, Player player, Claim claim,
                                            ClaimPermission permission, String configPath) {
        MenuItem item = menu.getItems().entrySet().stream().filter(menuItemEntry -> menuItemEntry.getKey().equals(configPath)).findFirst().get().getValue();
        ItemStack itemStack = getItemStack(item);
        String statusPlaceholder = claim.checkPermission(target, permission) ? BaseUtil.getStatus(true) : BaseUtil.getStatus(false);

        ItemMeta meta = itemStack.getItemMeta();
        List<String> lore = meta.getLore().stream()
                .map(line -> line.replace("%status%", statusPlaceholder))
                .toList();
        meta.setLore(lore);
        itemStack.setItemMeta(meta);

        ItemStack target_item = setupFlagItem(itemStack);

        builder.setItem(new ClickableItemStack(target_item,item.getIndex()), event -> {
            InteractPlayerPermission(target, claim, permission);
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

    private void InteractPlayerPermission(UUID target, Claim claim, ClaimPermission permission) {
        if (claim.checkPermission(target, permission)) {
            claim.removePermission(target, permission);
        } else {
            claim.addPermission(target, permission);
        }
    }

}
