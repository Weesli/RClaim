package net.weesli.rClaim.ui.inventories;

import net.weesli.rClaim.RClaim;
import net.weesli.rClaim.tasks.ClaimTask;
import net.weesli.rClaim.ui.ClaimInventory;
import net.weesli.rClaim.modal.Claim;
import net.weesli.rClaim.utils.ClaimManager;
import net.weesli.rozsLib.color.ColorBuilder;
import net.weesli.rozsLib.inventory.lasest.ClickableItemStack;
import net.weesli.rozsLib.inventory.lasest.InventoryBuilder;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Optional;
import java.util.stream.Collectors;

public class ClaimUpgradeMenu implements ClaimInventory {
    @Override
    public void openInventory(Player player, Claim claim, FileConfiguration config) {
        InventoryBuilder builder = new InventoryBuilder().title(ColorBuilder.convertColors(config.getString("upgrade-menu.title"))).size(config.getInt("upgrade-menu.size"));
        ClickableItemStack itemStack = new ClickableItemStack(getItemStack("upgrade-menu.item-settings", config), config.getInt("upgrade-menu.item-settings.slot"));
        ItemMeta meta = itemStack.getItemStack().getItemMeta();
        meta.setLore(meta.getLore().stream().map(line -> line.replaceAll("<cost>", RClaim.getInstance().getConfig().getString("claim-settings.claim-cost"))).collect(Collectors.toList()));
        itemStack.getItemStack().setItemMeta(meta);
        builder.setItem(itemStack,event -> {
            if (RClaim.getInstance().getEconomy().isActive()){
                if (!RClaim.getInstance().getEconomy().hasEnough(player, RClaim.getInstance().getConfig().getInt("claim-settings.claim-cost"))){
                    player.sendMessage(RClaim.getInstance().getMessage("HASNT_MONEY"));
                    return;
                }
                RClaim.getInstance().getEconomy().withdraw(player, RClaim.getInstance().getConfig().getInt("claim-settings.claim-cost"));
                player.sendMessage(RClaim.getInstance().getMessage("TIME_UPGRADE"));
            }
            Optional<ClaimTask> task = ClaimManager.getTasks().stream().filter(task1 -> task1.getClaimId().equals(claim.getID())).findFirst();
            task.ifPresent(claimTask -> claimTask.addTime(ClaimManager.getSec(RClaim.getInstance().getConfig().getInt("claim-settings.claim-duration"))));
        });
        builder.openInventory(player);
    }
}
