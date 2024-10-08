package net.weesli.rClaim.ui.inventories;

import net.weesli.rClaim.RClaim;
import net.weesli.rClaim.enums.ClaimPermission;
import net.weesli.rClaim.ui.ClaimInventory;
import net.weesli.rClaim.modal.Claim;
import net.weesli.rClaim.utils.ClaimManager;
import net.weesli.rozsLib.color.ColorBuilder;
import net.weesli.rozsLib.inventory.lasest.ClickableItemStack;
import net.weesli.rozsLib.inventory.lasest.InventoryBuilder;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.UUID;

public class ClaimPermissionMenu implements ClaimInventory {


    private UUID target;

    public void setup(UUID target) {
        this.target = target;
    }


    @Override
    public void openInventory(Player player, Claim claim, FileConfiguration config) {
        InventoryBuilder builder = new InventoryBuilder().title(ColorBuilder.convertColors(config.getString("permissions-menu.title"))).size(config.getInt("permissions-menu.size"));

        addClickableItemWithStatus(builder, player, claim, ClaimPermission.BLOCK_BREAK, "block-break", config);
        addClickableItemWithStatus(builder, player, claim, ClaimPermission.BLOCK_PLACE, "block-place", config);
        addClickableItemWithStatus(builder, player, claim, ClaimPermission.PICKUP_ITEM, "pickup-item", config);
        addClickableItemWithStatus(builder, player, claim, ClaimPermission.DROP_ITEM, "drop-item", config);
        addClickableItemWithStatus(builder, player, claim, ClaimPermission.CONTAINER_OPEN, "container-open", config);
        addClickableItemWithStatus(builder, player, claim, ClaimPermission.INTERACT_ENTITY, "interact-entity", config);
        addClickableItemWithStatus(builder, player, claim, ClaimPermission.ATTACK_ANIMAL, "attack-animal", config);
        addClickableItemWithStatus(builder, player, claim, ClaimPermission.ATTACK_MONSTER, "attack-monster", config);
        addClickableItemWithStatus(builder, player, claim, ClaimPermission.BREAK_CONTAINER, "break-container", config);
        addClickableItemWithStatus(builder, player, claim, ClaimPermission.USE_DOOR, "use-door", config);
        addClickableItemWithStatus(builder, player, claim, ClaimPermission.USE_PORTAL, "use-portal", config);
        addClickableItemWithStatus(builder, player, claim, ClaimPermission.USE_POTION, "use-potion", config);

        builder.openInventory(player);
    }

    private void addClickableItemWithStatus(InventoryBuilder builder, Player player, Claim claim,
                                            ClaimPermission permission, String configPath, FileConfiguration config) {
        String itemPath = "permissions-menu.children." + configPath;
        ItemStack itemStack = getItemStack(itemPath,config);
        String statusPlaceholder = claim.checkPermission(target, permission) ? ClaimManager.getStatus(true) : ClaimManager.getStatus(false);

        ItemMeta meta = itemStack.getItemMeta();
        List<String> lore = meta.getLore().stream()
                .map(line -> line.replace("%status%", statusPlaceholder))
                .toList();
        meta.setLore(lore);
        itemStack.setItemMeta(meta);

        ItemStack target_item = setupFlagItem(itemStack);


        builder.setItem(config.getInt(itemPath + ".slot"),target_item,event -> {
            InteractPlayerPermission(target, claim, permission);
            openInventory(player, claim, config);
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
