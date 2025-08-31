package net.weesli.rclaim.ui.inventories;

import me.clip.placeholderapi.PlaceholderAPI;
import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.events.ClaimStatusChangeEvent;
import net.weesli.rclaim.api.model.Claim;
import net.weesli.rclaim.api.model.SubClaim;
import net.weesli.rclaim.api.status.ClaimStatusRegistry;
import net.weesli.rclaim.config.ConfigLoader;
import net.weesli.rclaim.config.adapter.model.Menu;
import net.weesli.rclaim.config.adapter.model.MenuItem;
import net.weesli.rclaim.ui.ClaimInventory;
import net.weesli.rclaim.util.BaseUtil;
import net.weesli.rozslib.inventory.ClickableItemStack;
import net.weesli.rozslib.inventory.types.PageableInventory;
import net.weesli.rozslib.inventory.types.SimpleInventory;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static net.weesli.rclaim.config.lang.LangConfig.sendMessageToPlayer;
public class ClaimSettingsMenu extends ClaimInventory {

    private static final Menu menu = ConfigLoader.getMenuConfig().getOptionsMenu();

    private List<String> alreadyAdded = new ArrayList<>();

    @Override
    public void openInventory(Player player, Claim claim) {
        PageableInventory builder = new PageableInventory(PlaceholderAPI.setPlaceholders(player,menu.getTitle()),54,
                new ClickableItemStack(getItemStack(ConfigLoader.getConfig().getPublicMenu().getPreviousItem()), 45),
                new ClickableItemStack(getItemStack(ConfigLoader.getConfig().getPublicMenu().getNextItem()), 53));
        builder.setLayout("""
                *********
                *       *
                *       *
                *       *
                *       *
                 *******
                """).fill(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), false);
        for (Map.Entry<String, ClaimStatusRegistry> registryEntry : RClaim.getInstance().getStatusService().getRegistries().entrySet()) {
            addClickableItem(builder,claim,player, registryEntry.getKey());
        }
        builder.openInventory(player);
    }

    private void addClickableItem(PageableInventory builder, Claim claim, Player player, String status) {
        String anotherStatus = status.replaceAll("_", "-").toLowerCase();
        if (alreadyAdded.contains(anotherStatus) || alreadyAdded.contains(status)) return;
        MenuItem item = menu.getItems().get(status) == null ? menu.getItems().get(anotherStatus) : menu.getItems().get(status);
        alreadyAdded.add(anotherStatus);
        alreadyAdded.add(status);
        if (item == null) return;
        ItemStack itemStack = getItemStack(item);
        ItemMeta meta = itemStack.getItemMeta();
        if (claim.checkStatus(status)) {
            meta.setLore(meta.getLore().stream().map(line-> line.replaceAll("%status%", BaseUtil.getStatus(true))).toList());
        }else {
            meta.setLore(meta.getLore().stream().map(line-> line.replaceAll("%status%", BaseUtil.getStatus(false))).toList());
        }
        itemStack.setItemMeta(meta);
        builder.addItem(itemStack, event -> {
            event.setCancelled(true);
            if (status.equals("PVP")){
                boolean changeablePvP = isChangeablePvP(claim);
                if (!changeablePvP){
                    sendMessageToPlayer("PVP_STATUS_NOT_CHANGEABLE", player);
                    return;
                }
            }
            if (claim.checkStatus(status)) {
                claim.removeClaimStatus(status);
                ClaimStatusChangeEvent changeEvent = new ClaimStatusChangeEvent(player, claim, status,false);
                RClaim.getInstance().getServer().getPluginManager().callEvent(changeEvent);
            } else {
                claim.addClaimStatus(status);
                ClaimStatusChangeEvent changeEvent = new ClaimStatusChangeEvent(player, claim, status,true);
                RClaim.getInstance().getServer().getPluginManager().callEvent(changeEvent);

            }
            alreadyAdded.clear();
            openInventory(player, claim);
        });
    }

    private boolean isChangeablePvP(Claim claim) {
        LinkedList<Location> locations = new LinkedList<>();
        for (SubClaim subClaim : claim.getSubClaims()) {
            locations.add(new Location(
                    Bukkit.getWorld(claim.getWorldName()),
                    (subClaim.getX() * 16) + 8,
                    0,
                    (subClaim.getZ() * 16) + 8
            ));
        }
        locations.add(claim.getCenter().clone());
        for (Location location : locations) {
            Chunk chunk = location.getChunk();
            for (Entity entity : chunk.getEntities()){
                if (entity instanceof Player player) {
                    if (!claim.isOwner(player.getUniqueId()) && !claim.isMember(player.getUniqueId())){
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
