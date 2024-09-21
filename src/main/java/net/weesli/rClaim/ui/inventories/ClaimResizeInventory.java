package net.weesli.rClaim.ui.inventories;

import net.weesli.rClaim.RClaim;
import net.weesli.rClaim.hooks.HWorldGuard;
import net.weesli.rClaim.ui.ClaimInventory;
import net.weesli.rClaim.modal.Claim;
import net.weesli.rClaim.utils.ClaimManager;
import net.weesli.rozsLib.color.ColorBuilder;
import net.weesli.rozsLib.inventory.lasest.ClickableItemStack;
import net.weesli.rozsLib.inventory.lasest.InventoryBuilder;
import org.bukkit.Chunk;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ClaimResizeInventory implements ClaimInventory {


    @Override
    public void openInventory(Player player, Claim claim, FileConfiguration config) {
        InventoryBuilder builder = new InventoryBuilder().title(ColorBuilder.convertColors(config.getString("resize-menu.title"))).size(54);
        setupUpgradeInventory(player,builder,player.getLocation().getChunk(), claim, config);
        builder.openInventory(player);
    }

    private void setupUpgradeInventory(Player player, InventoryBuilder inventory, Chunk centerChunk, Claim claim, FileConfiguration config) {
        List<Chunk> chunks = new ArrayList<>();
        int chunkRadius = 4;

        int centerX = centerChunk.getX();
        int centerZ = centerChunk.getZ();
        for (int dx = -chunkRadius; dx <= chunkRadius; dx++) {
            for (int dz = -chunkRadius; dz <= chunkRadius; dz++) {
                if (chunks.size() >= 54) break;
                chunks.add(player.getWorld().getChunkAt(centerX + dx, centerZ + dz));
            }
        }

        for (int i = 0; i < 54; i++) {
            if (i < chunks.size()) {
                getAreas(inventory, player, centerChunk, chunks.get(i), claim, i, config);
            }
        }
    }

    private void getAreas(InventoryBuilder inventory, Player player, Chunk currentChunk, Chunk chunk, Claim target_claim, int slot, FileConfiguration config) {
        Claim claim = ClaimManager.getClaims().stream()
                .filter(c -> c.getChunk().equals(chunk))
                .findFirst()
                .orElse(null);

        String key = getKeyForChunk(player, currentChunk, chunk, claim);

        ClickableItemStack itemStack = new ClickableItemStack(getItemStack(key, config), slot);

        inventory.setItem(itemStack, event -> handleItemClick(event, player, chunk, target_claim));

        updateItemMeta(itemStack, chunk);
    }

    private String getKeyForChunk(Player player, Chunk currentChunk, Chunk chunk, Claim claim) {
        if (currentChunk.equals(chunk)) {
            return "resize-menu.children.starter-claim";
        } else if (claim == null) {
            return "resize-menu.children.empty-claim";
        } else if (claim.getOwner().equals(player.getUniqueId())) {
            return "resize-menu.children.self-claim";
        } else {
            return "resize-menu.children.not-available-claim";
        }
    }

    private void handleItemClick(InventoryClickEvent event, Player player, Chunk chunk, Claim claim) {
        if (!HWorldGuard.isAreaEnabled(player)) {
            player.sendMessage(RClaim.getInstance().getMessage("AREA_DISABLED"));
            return;
        }
        if (!ClaimManager.checkWorld(player.getWorld().getName())) {
            player.sendMessage(RClaim.getInstance().getMessage("NOT_IN_CLAIMABLE_WORLD"));
            return;
        }
        if (RClaim.getInstance().getEconomy().isActive() &&
                !RClaim.getInstance().getEconomy().hasEnough(player, RClaim.getInstance().getConfig().getInt("claim-settings.claim-cost"))) {
            player.sendMessage(RClaim.getInstance().getMessage("HASNT_MONEY"));
            return;
        }
        RClaim.getInstance().getEconomy().withdraw(player, RClaim.getInstance().getConfig().getInt("claim-settings.claim-cost"));

        if (!ClaimManager.isSuitable(chunk)) {
            ClaimManager.createClaim(chunk, player, false,
                    ClaimManager.getPlayerData(player.getUniqueId()).getClaims().get(0).getID());
            RClaim.getInstance().getUiManager().openInventory(player,claim,this);
        } else {
            player.sendMessage(RClaim.getInstance().getMessage("IS_NOT_SUITABLE"));
        }
    }

    private void updateItemMeta(ClickableItemStack itemStack, Chunk chunk) {
        ItemMeta meta = itemStack.getItemStack().getItemMeta();
        List<String> updatedLore = meta.getLore().stream()
                .map(line -> line.replace("<cost>", RClaim.getInstance().getConfig().getString("claim-settings.claim-cost"))
                        .replace("<x>", String.valueOf(chunk.getX() * 16))
                        .replace("<z>", String.valueOf(chunk.getZ() * 16)))
                .collect(Collectors.toList());
        meta.setLore(updatedLore);
        itemStack.getItemStack().setItemMeta(meta);
    }
}
