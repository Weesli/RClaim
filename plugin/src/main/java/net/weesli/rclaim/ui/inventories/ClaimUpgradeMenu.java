package net.weesli.rclaim.ui.inventories;

import me.clip.placeholderapi.PlaceholderAPI;
import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.model.Claim;
import net.weesli.rclaim.config.ConfigLoader;
import net.weesli.rclaim.config.lang.MenuConfig;
import net.weesli.rclaim.ui.ClaimInventory;
import net.weesli.rclaim.util.BaseUtil;
import net.weesli.rozslib.inventory.ClickableItemStack;
import net.weesli.rozslib.inventory.types.SimpleInventory;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.stream.Collectors;
import static net.weesli.rclaim.config.lang.LangConfig.sendMessageToPlayer;
public class ClaimUpgradeMenu extends ClaimInventory {

    private final MenuConfig.Menu menu = ConfigLoader.getMenuConfig().getUpgradeMenu();

    @Override
    public void openInventory(Player player, Claim claim) {
        SimpleInventory inventory = new SimpleInventory(PlaceholderAPI.setPlaceholders(player,menu.getTitle()),menu.getSize());
        inventory.setLayout(menu.getFillerSlots()
                .stream()
                .mapToInt(Integer::intValue)
                .toArray()).fill(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), menu.isAutoFill());
        ItemStack itemStack = getItemStack(menu.getItems().get("item-settings"),player);
        ItemMeta meta = itemStack.getItemMeta();
        // calculate cost for claim
        int claimCostPerDay = ConfigLoader.getConfig().getClaimSettings().getClaimCostPerDay();
        int currentDayCount = claim.getTimestamp() / (60 * 60 * 24);
        if (currentDayCount >= (ConfigLoader.getConfig().getClaimSettings().getClaimDuration() - 1)){
            sendMessageToPlayer("ALREADY_MAX_DAY", player);
            return;
        }
        int suitableDayCount = (ConfigLoader.getConfig().getClaimSettings().getClaimDuration() - currentDayCount);
        int totalClaimCost = claimCostPerDay * suitableDayCount;
        meta.setLore(meta.getLore().stream().map(line -> line.replaceAll("<cost>", String.valueOf(totalClaimCost))).collect(Collectors.toList()));
        itemStack.setItemMeta(meta);
        inventory.setItem(new ClickableItemStack(itemStack, menu.getItems().get("item-settings").getIndex()), event -> {
            if (RClaim.getInstance().getEconomyManager().getEconomyIntegration().isActive()){
                if (!RClaim.getInstance().getEconomyManager().getEconomyIntegration().hasEnough(player, totalClaimCost)){
                    sendMessageToPlayer("HASNT_MONEY", player);
                    return;
                }
                RClaim.getInstance().getEconomyManager().getEconomyIntegration().withdraw(player, totalClaimCost);
                sendMessageToPlayer("TIME_UPGRADE", player);
            }
            int duration = BaseUtil.getSec(suitableDayCount * 24 * 60 * 60);
            claim.addTimestamp(duration);
        });
        inventory.openInventory(player);
    }
}
