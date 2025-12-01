package net.weesli.rclaim.ui.inventories.tag;

import me.clip.placeholderapi.PlaceholderAPI;
import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.model.Claim;
import net.weesli.rclaim.api.model.ClaimTag;
import net.weesli.rclaim.config.ConfigLoader;
import net.weesli.rclaim.config.lang.MenuConfig;
import net.weesli.rclaim.input.TextInputManager;
import net.weesli.rclaim.ui.ClaimInventory;
import net.weesli.rozslib.inventory.ClickableItemStack;
import net.weesli.rozslib.inventory.types.PageableInventory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import static net.weesli.rclaim.config.lang.LangConfig.sendMessageToPlayer;
public class ClaimTagMainMenu extends ClaimInventory {

    private final MenuConfig.PageableMenu menu = ConfigLoader.getMenuConfig().getTagMainMenu();

    @Override
    public void openInventory(Player player, Claim claim) {
        PageableInventory inventory = new PageableInventory(PlaceholderAPI.setPlaceholders(player,menu.getTitle()), menu.getSize(),
                menu.getPreviousItem().asClickableItemStack(player),
                menu.getNextItem().asClickableItemStack(player),
                menu.getItems().get("add-tag").getIndex());
        inventory.setLayout(menu.getFillerSlots()
                .stream()
                .mapToInt(Integer::intValue)
                .toArray()).fill(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), menu.isAutoFill());
        List<ClaimTag> tags = RClaim.getInstance().getTagManager().getTags(claim.getID());
        for (ClaimTag tag : tags){
            ClickableItemStack itemStack = new ClickableItemStack(getItemStack(menu.getItems().get("item-settings"),player), 0);
            ItemMeta meta = itemStack.getItemStack().getItemMeta();
            meta.setDisplayName(meta.hasDisplayName() ? meta.getDisplayName().replaceAll("<name>", tag.getDisplayName()) : null);
            itemStack.getItemStack().setItemMeta(meta);
            itemStack.setSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
            inventory.setItem(itemStack , event -> {
                if (event.isShiftClick() && event.isRightClick()){
                    RClaim.getInstance().getTagManager().removeTag(claim.getID(), tag);
                    RClaim.getInstance().getUiManager().openInventory(player, claim, ClaimTagMainMenu.class);
                } else if (event.isLeftClick() || event.isRightClick()) {
                    RClaim.getInstance().getUiManager().openTagInventory(player, tag, ClaimTagEditMenu.class);
                }
            });
        }
        inventory.addStaticItem(new ClickableItemStack(getItemStack(menu.getItems().get("add-tag"),player),menu.getItems().get("add-tag").getIndex()), event ->{
            //Bukkit.getScheduler().runTask(RClaim.getInstance(), () -> player.closeInventory());
            RClaim.getInstance().getFoliaLib().getScheduler().runNextTick((wrapper) -> player.closeInventory());
            sendMessageToPlayer("ENTER_TAG_NAME", player);
            RClaim.getInstance().getTextInputManager().runAction(
                    player,
                    TextInputManager.TextInputAction.ADD_TAG_TO_CLAIM,
                    claim
            );

                });
        inventory.openDefaultInventory(player);
    }
}
