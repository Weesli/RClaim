package net.weesli.rclaim.ui.inventories;

import me.clip.placeholderapi.PlaceholderAPI;
import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.model.Claim;
import net.weesli.rclaim.config.ConfigLoader;
import net.weesli.rclaim.api.enums.VerifyAction;
import net.weesli.rclaim.config.lang.MenuConfig;
import net.weesli.rclaim.ui.ClaimInventory;
import net.weesli.rclaim.util.BaseUtil;
import net.weesli.rozslib.inventory.types.PageableInventory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import static net.weesli.rclaim.config.lang.LangConfig.sendMessageToPlayer;
public class ClaimsMenu extends ClaimInventory {

    private static final MenuConfig.PageableMenu menu = ConfigLoader.getMenuConfig().getClaimsMenu();

    @Override
    public void openInventory(Player player, Claim claim) {
        int i = 1;
        PageableInventory inventory = new PageableInventory(PlaceholderAPI.setPlaceholders(player,menu.getTitle()), menu.getSize(),
                menu.getPreviousItem().asClickableItemStack(player),
                menu.getNextItem().asClickableItemStack(player));
        inventory.setLayout(menu.getFillerSlots()
                .stream()
                .mapToInt(Integer::intValue)
                .toArray()).fill(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), menu.isAutoFill());
        List<Claim> Claims = RClaim.getInstance().getCacheManager().getClaims().getAllClaims(player.getUniqueId());
        for (Claim target : Claims) {
            ItemStack itemStack = getItemStack(menu.getItems().get("item-settings"),player);
            ItemMeta meta = itemStack.getItemMeta();
            meta.setDisplayName(meta.getDisplayName().replaceAll("<count>", String.valueOf(i)));
            meta.setLore(meta.getLore().stream().map(line -> line.replaceAll("<x>", String.valueOf(target.getX())).replaceAll("<z>", String.valueOf(target.getZ())).replaceAll("<time>", BaseUtil.getTimeFormat(target.getID()))).collect(Collectors.toList()));
            itemStack.setItemMeta(meta);
            inventory.addItem(itemStack, e -> {
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

                    /*Bukkit.getScheduler().runTask(RClaim.getInstance(), () -> {
                        player.closeInventory();
                        player.teleport(target.getBlockLocation().clone().add(0,2,0));
                    });*/
                    RClaim.getInstance().getFoliaLib().getScheduler().runNextTick((wrapper) -> {
                        player.closeInventory();
                        player.teleportAsync(target.getBlockLocation().clone().add(0,2,0));
                    });
                    return;
                }
                RClaim.getInstance().getUiManager().openInventory(player, claim, ClaimUpgradeMenu.class);
            });
            i++;
        }
        inventory.openDefaultInventory(player);
    }
}
