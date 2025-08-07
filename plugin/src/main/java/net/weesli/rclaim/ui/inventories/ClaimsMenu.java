package net.weesli.rclaim.ui.inventories;

import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.model.Claim;
import net.weesli.rclaim.config.ConfigLoader;
import net.weesli.rclaim.config.adapter.model.Menu;
import net.weesli.rclaim.api.enums.VerifyAction;
import net.weesli.rclaim.ui.ClaimInventory;
import net.weesli.rclaim.util.BaseUtil;
import net.weesli.rozslib.inventory.ClickableItemStack;
import net.weesli.rozslib.inventory.types.PageableInventory;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.stream.Collectors;
import static net.weesli.rclaim.config.lang.LangConfig.sendMessageToPlayer;
public class ClaimsMenu extends ClaimInventory {

    private static final Menu menu = ConfigLoader.getMenuConfig().getClaimsMenu();

    @Override
    public void openInventory(Player player, Claim claim) {
        int i = 1;
        PageableInventory builder = new PageableInventory(menu.getTitle(), 54,
                new ClickableItemStack(getItemStack(ConfigLoader.getConfig().getPublicMenu().getPreviousItem()),0),
                new ClickableItemStack(getItemStack(ConfigLoader.getConfig().getPublicMenu().getNextItem()),0));
        builder.setLayout("""
                *********
                *       *
                *       *
                *       *
                *       *
                 *******
                """).fill(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), false);
        List<Claim> Claims = RClaim.getInstance().getCacheManager().getClaims().getAllClaims(player.getUniqueId());
        for (Claim target : Claims) {
            ItemStack itemStack = getItemStack(menu.getItems().get("item-settings"));
            ItemMeta meta = itemStack.getItemMeta();
            meta.setDisplayName(meta.getDisplayName().replaceAll("<count>", String.valueOf(i)));
            meta.setLore(meta.getLore().stream().map(line -> line.replaceAll("<x>", String.valueOf(target.getX())).replaceAll("<z>", String.valueOf(target.getZ())).replaceAll("<time>", BaseUtil.getTimeFormat(target.getID()))).collect(Collectors.toList()));
            itemStack.setItemMeta(meta);
            builder.addItem(itemStack, e -> {
                if (e.isShiftClick() && e.isRightClick()){
                    VerifyMenu verifyMenu = new VerifyMenu();
                    verifyMenu.setup(VerifyAction.UNCLAIM,target.getID());
                    verifyMenu.openInventory(player, claim);
                    return;
                }
                if (e.isShiftClick() && e.isLeftClick()){ // teleport the claim
                    if (!player.hasPermission("rclaim.claim.tp")) {

                        sendMessageToPlayer("NO_PERMISSION", player);
                        return;
                    }
                    player.teleport(target.getCenter());
                    return;
                }
                RClaim.getInstance().getUiManager().openInventory(player, claim, ClaimUpgradeMenu.class);
            });
            i++;
        }
        builder.openDefaultInventory(player);
    }
}
