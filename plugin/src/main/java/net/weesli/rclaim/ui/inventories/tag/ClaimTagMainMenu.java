package net.weesli.rclaim.ui.inventories.tag;

import de.rapha149.signgui.SignGUI;
import de.rapha149.signgui.SignGUIAction;
import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.model.Claim;
import net.weesli.rclaim.api.model.ClaimTag;
import net.weesli.rclaim.config.ConfigLoader;
import net.weesli.rclaim.config.adapter.model.Menu;
import net.weesli.rclaim.model.ClaimTagImpl;
import net.weesli.rclaim.ui.ClaimInventory;
import net.weesli.rclaim.util.BaseUtil;
import net.weesli.rozslib.inventory.ClickableItemStack;
import net.weesli.rozslib.inventory.types.PageableInventory;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
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
        builder.addStaticItem(new ClickableItemStack(getItemStack(menu.getItems().get("add-tag")),menu.getItems().get("add-tag").getIndex()), event -> callSign(player, claim));
        builder.openDefaultInventory(player);
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
                        RClaim.getInstance().getTagManager().addTag(claim,new ClaimTagImpl(
                                claim.getID(),
                                BaseUtil.generateId(),
                                name,
                                new ArrayList<>(),
                                new ArrayList<>()
                        ));
                        return Collections.singletonList(SignGUIAction.runSync(RClaim.getInstance(),() -> RClaim.getInstance().getUiManager().openInventory(player, claim, ClaimTagMainMenu.class)));
                    }
                })
                .build();
        gui.open(player);
    }
}
