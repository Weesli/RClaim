package net.weesli.rClaim.ui.inventories.tag;

import de.rapha149.signgui.SignGUI;
import de.rapha149.signgui.SignGUIAction;
import de.rapha149.signgui.SignGUIBuilder;
import net.weesli.rClaim.RClaim;
import net.weesli.rClaim.modal.Claim;
import net.weesli.rClaim.modal.ClaimTag;
import net.weesli.rClaim.ui.ClaimInventory;
import net.weesli.rClaim.utils.ClaimManager;
import net.weesli.rClaim.utils.TagManager;
import net.weesli.rozsLib.color.ColorBuilder;
import net.weesli.rozsLib.inventory.lasest.ClickableItemStack;
import net.weesli.rozsLib.inventory.lasest.InventoryBuilder;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClaimTagMainMenu implements ClaimInventory {

    private static final int ITEMS_PER_PAGE = 7;
    private static final int INVENTORY_SIZE = 27;
    private static final int[] block_slots = {10,11,12,13,14,15,16};
    private static final int[] glass_slots = {0,1,2,3,4,5,6,7,8,9,17,18,19,20,24,25,26};


    @Override
    public void openInventory(Player player, Claim claim, FileConfiguration config) {
        final int totalPages = (int) Math.ceil((double) TagManager.getTags(claim.getID()).size() / ITEMS_PER_PAGE);
        final int[] currentPage = {0};
        openPage(player, claim, config, TagManager.getTags(claim.getID()), currentPage[0], totalPages);
    }

    private void openPage(Player player, Claim claim, FileConfiguration config, List<ClaimTag> tags, int page, int totalPages) {
        InventoryBuilder inv = new InventoryBuilder().title(config.getString("tag-main-menu.title")).size(27);

        int start = page * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, tags.size());

        for (int i = start; i < end; i++) {
            ClaimTag tag = tags.get(i);
            int slotIndex = i - start;
            if (slotIndex < block_slots.length) {
                ClickableItemStack itemStack = new ClickableItemStack(getItemStack("tag-main-menu.item-settings",config), block_slots[slotIndex]);
                ItemMeta meta = itemStack.getItemStack().getItemMeta();
                meta.setDisplayName(meta.getDisplayName().replaceAll("<name>", tag.getDisplayName()));
                itemStack.getItemStack().setItemMeta(meta);
                itemStack.setSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
                inv.setItem(itemStack , event -> {
                    if (event.isShiftClick() && event.isRightClick()){
                        TagManager.removeTag(claim.getID(), tag);
                        RClaim.getInstance().getUiManager().openInventory(player,claim, RClaim.getInstance().getUiManager().getTagMainMenu());
                    } else if (event.isLeftClick() || event.isRightClick()) {
                        RClaim.getInstance().getUiManager().openTagInventory(player, tag, "edit");
                    }
                });
            }
        }

        for (int i : glass_slots){
            inv.setItem(i, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        }

        if (page > 0) {
            inv.setItem(21, createArrowItem(ColorBuilder.convertColors(config.getString("tag-main-menu.previous-item-name")), Material.ARROW), event -> {
                openPage(player, claim, config, tags, page - 1, totalPages);
            });
        }

        if (page < totalPages - 1) {
            inv.setItem(23, createArrowItem(ColorBuilder.convertColors(config.getString("tag-main-menu.next-item-name")), Material.ARROW), event -> {
                openPage(player, claim, config, tags, page + 1, totalPages);
            });
        }

        inv.setItem(config.getInt("tag-main-menu.children.add-tag.slot"), getItemStack("tag-main-menu.children.add-tag", config),
                event -> callSign(player,claim));

        inv.openInventory(player);
    }

    private ItemStack createArrowItem(String name, Material material) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }

    private void callSign(Player player, Claim claim){
        SignGUI gui = SignGUI.builder()
                .setLines("", "----------","^^^^^^^^", "----------")
                .setType(Material.DARK_OAK_SIGN)

                .setColor(DyeColor.WHITE)

                .setHandler((p, result) -> {
                    String name = result.getLine(0);
                    if (name.isEmpty()) {
                        return Collections.emptyList();
                    }else {
                        TagManager.addTag(claim,new ClaimTag(
                                claim.getID(),
                                ClaimManager.IDCreator(),
                                name,
                                new ArrayList<>(),
                                new ArrayList<>()
                        ));
                        return Collections.singletonList(SignGUIAction.runSync(RClaim.getInstance(),() -> RClaim.getInstance().getUiManager().openInventory(player, claim, RClaim.getInstance().getUiManager().getTagMainMenu())));
                    }
                })
                .build();
        gui.open(player);
    }
}
