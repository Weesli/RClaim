package net.weesli.rClaim.ui.inventories;

import net.weesli.rClaim.RClaim;
import net.weesli.rClaim.enums.VerifyAction;
import net.weesli.rClaim.ui.ClaimInventory;
import net.weesli.rClaim.modal.Claim;
import net.weesli.rClaim.utils.ClaimManager;
import net.weesli.rozsLib.color.ColorBuilder;
import net.weesli.rozsLib.inventory.ClickableItemStack;
import net.weesli.rozsLib.inventory.InventoryBuilder;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.stream.Collectors;

public class ClaimsMenu implements ClaimInventory {

    @Override
    public void openInventory(Player player, Claim claim, FileConfiguration config) {
        int i = 1;
        InventoryBuilder builder = new InventoryBuilder(RClaim.getInstance(), ColorBuilder.convertColors(config.getString("claims-menu.title")), config.getInt("claims-menu.size"));
        List<Claim> claims = ClaimManager.getPlayerData(player.getUniqueId()).getClaims();
        for (Claim target : claims){
            ClickableItemStack itemStack = new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack("claims-menu.item-settings"), builder.build())
                    .setCancelled(true)
                    .setEvent(e -> {
                        if (e.isShiftClick() && e.isRightClick()){
                            VerifyMenu verifyMenu = RClaim.getInstance().getUiManager().getVerifyMenu();
                            verifyMenu.setup(VerifyAction.UNCLAIM,target.getID());
                            RClaim.getInstance().getUiManager().openInventory(player,claim,verifyMenu);
                            return;
                        }
                        RClaim.getInstance().getUiManager().openInventory(player,claim,RClaim.getInstance().getUiManager().getUpgradeMenu());
                    });
            ItemMeta meta = itemStack.getItemStack().getItemMeta();
            meta.setDisplayName(meta.getDisplayName().replaceAll("<count>", String.valueOf(i)));
            meta.setLore(meta.getLore().stream().map(line -> line.replaceAll("<x>", String.valueOf(target.getX())).replaceAll("<z>", String.valueOf(target.getZ())).replaceAll("<time>", ClaimManager.getTimeFormat(target.getID()))).collect(Collectors.toList()));
            itemStack.getItemStack().setItemMeta(meta);
            builder.addItem(itemStack);
            i++;
        }
        player.openInventory(builder.build());
    }
}
