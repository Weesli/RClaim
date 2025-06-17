package net.weesli.rclaim.ui.inventories;

import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.events.ClaimStatusChangeEvent;
import net.weesli.rclaim.api.model.Claim;
import net.weesli.rclaim.config.ConfigLoader;
import net.weesli.rclaim.config.adapter.model.Menu;
import net.weesli.rclaim.config.adapter.model.MenuItem;
import net.weesli.rclaim.api.enums.ClaimStatus;
import net.weesli.rclaim.ui.ClaimInventory;
import net.weesli.rclaim.util.BaseUtil;
import net.weesli.rozslib.inventory.ClickableItemStack;
import net.weesli.rozslib.inventory.types.SimpleInventory;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ClaimSettingsMenu extends ClaimInventory {

    private static final Menu menu = ConfigLoader.getMenuConfig().getOptionsMenu();

    @Override
    public void openInventory(Player player, Claim claim) {
        SimpleInventory builder = new SimpleInventory(menu.getTitle(),menu.getSize());
        builder.setLayout("").fill(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), true);
        addClickableItem(builder, claim, player, ClaimStatus.SPAWN_ANIMAL, "spawn-animal");
        addClickableItem(builder, claim, player, ClaimStatus.SPAWN_MONSTER, "spawn-monster");
        addClickableItem(builder, claim, player, ClaimStatus.PVP, "pvp");
        addClickableItem(builder, claim, player, ClaimStatus.EXPLOSION, "explosion");
        addClickableItem(builder, claim, player, ClaimStatus.SPREAD, "spread");
        addClickableItem(builder, claim,player,ClaimStatus.TIME, "time");
        addClickableItem(builder, claim,player,ClaimStatus.WEATHER, "weather");
        builder.openInventory(player);
    }

    private void addClickableItem(SimpleInventory builder, Claim claim, Player player, ClaimStatus status, String configPath) {
        MenuItem item = menu.getItems().entrySet().stream().filter(menuItemEntry -> menuItemEntry.getKey().equals(configPath)).findFirst().get().getValue();
        ItemStack itemStack = getItemStack(item);

        ClickableItemStack clickableItem = new ClickableItemStack(itemStack, item.getIndex());
        ItemMeta meta = clickableItem.getItemStack().getItemMeta();
        if (claim.checkStatus(status)) {
            meta.setLore(meta.getLore().stream().map(line-> line.replaceAll("%status%", BaseUtil.getStatus(true))).toList());
        }else {
            meta.setLore(meta.getLore().stream().map(line-> line.replaceAll("%status%", BaseUtil.getStatus(false))).toList());
        }
        clickableItem.getItemStack().setItemMeta(meta);
        builder.setItem(clickableItem,event -> {
            if (claim.checkStatus(status)) {
                claim.removeClaimStatus(status);
                ClaimStatusChangeEvent changeEvent = new ClaimStatusChangeEvent(player, claim,status,false);
                RClaim.getInstance().getServer().getPluginManager().callEvent(changeEvent);
            } else {
                claim.addClaimStatus(status);
                ClaimStatusChangeEvent changeEvent = new ClaimStatusChangeEvent(player, claim,status,true);
                RClaim.getInstance().getServer().getPluginManager().callEvent(changeEvent);

            }
            openInventory(player, claim);
        });
    }
}
