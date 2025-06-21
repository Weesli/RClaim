package net.weesli.rclaim.ui.inventories.tag;

import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.model.Claim;
import net.weesli.rclaim.api.model.ClaimTag;
import net.weesli.rclaim.config.ConfigLoader;
import net.weesli.rclaim.config.adapter.model.Menu;
import net.weesli.rclaim.input.TextInputManager;
import net.weesli.rclaim.ui.ClaimInventory;
import net.weesli.rozslib.inventory.ClickableItemStack;
import net.weesli.rozslib.inventory.types.PageableInventory;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ClaimTagMainMenu extends ClaimInventory {

    private final Menu menu = ConfigLoader.getMenuConfig().getTagMainMenu();

    @Override
    public void openInventory(Player player, Claim claim) {
        PageableInventory builder = new PageableInventory(menu.getTitle(), 27,
                new ClickableItemStack(getItemStack(ConfigLoader.getConfig().getPublicMenu().getPreviousItem()),21),
                new ClickableItemStack(getItemStack(ConfigLoader.getConfig().getPublicMenu().getNextItem()),23),
                menu.getItems().get("add-tag").getIndex(), 21,23);
        builder.setLayout("""
                *********
                *       *
                ***   ***
                """).fill(new ItemStack(Material.GRAY_STAINED_GLASS_PANE),false);
        List<ClaimTag> tags = RClaim.getInstance().getTagManager().getTags(claim.getID());
        for (ClaimTag tag : tags){
            ClickableItemStack itemStack = new ClickableItemStack(getItemStack(menu.getItems().get("item-settings")), 0);
            ItemMeta meta = itemStack.getItemStack().getItemMeta();
            meta.setDisplayName(meta.getDisplayName().replaceAll("<name>", tag.getDisplayName()));
            itemStack.getItemStack().setItemMeta(meta);
            itemStack.setSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
            builder.setItem(itemStack , event -> {
                if (event.isShiftClick() && event.isRightClick()){
                    RClaim.getInstance().getTagManager().removeTag(claim.getID(), tag);
                    RClaim.getInstance().getUiManager().openInventory(player, claim, ClaimTagMainMenu.class);
                } else if (event.isLeftClick() || event.isRightClick()) {
                    RClaim.getInstance().getUiManager().openTagInventory(player, tag, ClaimTagEditMenu.class);
                }
            });
        }
        builder.addStaticItem(new ClickableItemStack(getItemStack(menu.getItems().get("add-tag")),menu.getItems().get("add-tag").getIndex()), event ->{
            player.closeInventory();
            player.sendMessage(RClaim.getInstance().getMessage("ENTER_TAG_NAME"));
            RClaim.getInstance().getTextInputManager().runAction(
                    player,
                    TextInputManager.TextInputAction.ADD_TAG_TO_CLAIM,
                    claim
            );

                });
        builder.openDefaultInventory(player);
    }
}
