package net.weesli.rClaim.ui.inventories;

import net.weesli.rClaim.RClaim;
import net.weesli.rClaim.modal.Claim;
import net.weesli.rClaim.ui.ClaimInventory;
import net.weesli.rClaim.utils.ClaimManager;
import net.weesli.rozsLib.color.ColorBuilder;
import net.weesli.rozsLib.inventory.lasest.ClickableItemStack;
import net.weesli.rozsLib.inventory.lasest.InventoryBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ClaimBlockMenu implements ClaimInventory {

    private static final int ITEMS_PER_PAGE = 7;
    private static final int INVENTORY_SIZE = 27;
    private static final int[] block_slots = {10,11,12,13,14,15,16};
    private static final int[] glass_slots = {0,1,2,3,4,5,6,7,8,9,17,18,19,20,22,24,25,26};

    @Override
    public void openInventory(Player player, Claim claim, FileConfiguration config) {
        List<String> blockTypes = RClaim.getInstance().getConfig().getStringList("options.block-types");
        final int totalPages = (int) Math.ceil((double) blockTypes.size() / ITEMS_PER_PAGE);
        final int[] currentPage = {0};
        openPage(player, claim, config, blockTypes, currentPage[0], totalPages);
    }

    private void openPage(Player player, Claim claim, FileConfiguration config, List<String> blockTypes, int page, int totalPages) {
        InventoryBuilder builder = new InventoryBuilder().title(config.getString("block-menu.title")).size(INVENTORY_SIZE);

        int start = page * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, blockTypes.size());

        for (int i = start; i < end; i++) {
            String name = blockTypes.get(i);
            Material material = Material.getMaterial(name);
            if (material != null) {
                int slotIndex = i - start;
                if (slotIndex < block_slots.length) {
                    ClickableItemStack itemStack = new ClickableItemStack(new ItemStack(material), block_slots[slotIndex]);
                    itemStack.setSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
                    builder.setItem(itemStack , event -> {
                        ClaimManager.changeBlockMaterial(player, claim, material);
                        player.closeInventory();
                    });
                }
            }
        }

        for (int i : glass_slots){
            builder.setItem(i, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        }

        if (page > 0) {
            builder.setItem(21, createArrowItem(ColorBuilder.convertColors(config.getString("block-menu.previous-item-name")), Material.ARROW), event -> {
                openPage(player, claim, config, blockTypes, page - 1, totalPages);
            });
        }

        if (page < totalPages - 1) {
            builder.setItem(23, createArrowItem(ColorBuilder.convertColors(config.getString("block-menu.next-item-name")), Material.ARROW), event -> {
                openPage(player, claim, config, blockTypes, page + 1, totalPages);
            });
        }

        builder.openInventory(player);
    }


    private ItemStack createArrowItem(String name, Material material) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }

}
