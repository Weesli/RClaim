package net.weesli.rclaim.ui.inventories;

import me.clip.placeholderapi.PlaceholderAPI;
import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.events.ClaimStatusChangeEvent;
import net.weesli.rclaim.api.model.Claim;
import net.weesli.rclaim.api.model.SubClaim;
import net.weesli.rclaim.api.status.ClaimStatusRegistry;
import net.weesli.rclaim.config.ConfigLoader;
import net.weesli.rclaim.config.lang.MenuConfig;
import net.weesli.rclaim.ui.ClaimInventory;
import net.weesli.rclaim.util.BaseUtil;
import net.weesli.rozslib.inventory.types.PageableInventory;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

import static net.weesli.rclaim.config.lang.LangConfig.sendMessageToPlayer;
public class ClaimSettingsMenu extends ClaimInventory {

    private static final MenuConfig.PageableMenu menu = (MenuConfig.PageableMenu) ConfigLoader.getMenuConfig().getOptionsMenu();

    private List<String> alreadyAdded = new ArrayList<>();

    @Override
    public void openInventory(Player player, Claim claim) {
        PageableInventory inventory = new PageableInventory(PlaceholderAPI.setPlaceholders(player,menu.getTitle()), menu.getSize(),
                menu.getPreviousItem().asClickableItemStack(player),
                menu.getNextItem().asClickableItemStack(player));
        inventory.setLayout(menu.getFillerSlots()
                .stream()
                .mapToInt(Integer::intValue)
                .toArray()).fill(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), menu.isAutoFill());
        for (Map.Entry<String, ClaimStatusRegistry> registryEntry : RClaim.getInstance().getStatusService().getRegistries().entrySet()) {
            addClickableItem(inventory,claim,player, registryEntry.getKey());
        }
        inventory.openInventory(player);
    }

    private void addClickableItem(PageableInventory builder, Claim claim, Player player, String status) {
        String anotherStatus = status.replaceAll("_", "-").toLowerCase();
        if (alreadyAdded.contains(anotherStatus) || alreadyAdded.contains(status)) return;
        MenuConfig.MenuItem item = menu.getItems().get(status) == null ? menu.getItems().get(anotherStatus) : menu.getItems().get(status);
        alreadyAdded.add(anotherStatus);
        alreadyAdded.add(status);
        if (item == null) return;
        ItemStack itemStack = getItemStack(item, player);
        ItemMeta meta = itemStack.getItemMeta();
        if (claim.checkStatus(status)) {
            meta.setLore(meta.hasLore() ? meta.getLore().stream().map(line-> line.replaceAll("%status%", BaseUtil.getStatus(true))).toList() : null);
        }else {
            meta.setLore(meta.hasLore() ? meta.getLore().stream().map(line-> line.replaceAll("%status%", BaseUtil.getStatus(false))).toList() : null);
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
