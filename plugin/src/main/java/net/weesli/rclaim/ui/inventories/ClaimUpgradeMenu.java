package net.weesli.rclaim.ui.inventories;

import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.model.Claim;
import net.weesli.rclaim.config.ConfigLoader;
import net.weesli.rclaim.config.adapter.model.Menu;
import net.weesli.rclaim.model.ClaimImpl;
import net.weesli.rclaim.ui.ClaimInventory;
import net.weesli.rclaim.util.BaseUtil;
import net.weesli.rozslib.inventory.ClickableItemStack;
import net.weesli.rozslib.inventory.types.SimpleInventory;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.stream.Collectors;

public class ClaimUpgradeMenu extends ClaimInventory {

    private final Menu menu = ConfigLoader.getMenuConfig().getUpgradeMenu();

    @Override
    public void openInventory(Player player, Claim claim) {
        SimpleInventory inventory = new SimpleInventory(menu.getTitle(),menu.getSize());
        inventory.setLayout("").fill(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), true);
        ItemStack itemStack = getItemStack(menu.getItems().get("item-settings"));
        ItemMeta meta = itemStack.getItemMeta();
        meta.setLore(meta.getLore().stream().map(line -> line.replaceAll("<cost>", String.valueOf(ConfigLoader.getConfig().getClaimSettings().getClaimCost()))).collect(Collectors.toList()));
        itemStack.setItemMeta(meta);
        inventory.setItem(new ClickableItemStack(itemStack, menu.getItems().get("item-settings").getIndex()), event -> {
            if (RClaim.getInstance().getEconomyManager().getEconomyIntegration().isActive()){
                if (!RClaim.getInstance().getEconomyManager().getEconomyIntegration().hasEnough(player, ConfigLoader.getConfig().getClaimSettings().getClaimCost())){
                    player.sendMessage(RClaim.getInstance().getMessage("HASNT_MONEY"));
                    return;
                }
                RClaim.getInstance().getEconomyManager().getEconomyIntegration().withdraw(player, ConfigLoader.getConfig().getClaimSettings().getClaimCost());
                player.sendMessage(RClaim.getInstance().getMessage("TIME_UPGRADE"));
            }
            int duration = BaseUtil.getSec(ConfigLoader.getConfig().getClaimSettings().getClaimDuration());
            claim.addTimestamp(duration);
        });
        inventory.openInventory(player);
    }
}
