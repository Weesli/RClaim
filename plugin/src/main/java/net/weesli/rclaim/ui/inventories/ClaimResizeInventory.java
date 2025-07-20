package net.weesli.rclaim.ui.inventories;

import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.model.Claim;
import net.weesli.rclaim.api.model.SubClaim;

import net.weesli.rclaim.config.ConfigLoader;
import net.weesli.rclaim.config.adapter.model.Menu;
import net.weesli.rclaim.config.adapter.model.MenuItem;
import net.weesli.rclaim.hook.other.HWorldGuard;
import net.weesli.rclaim.ui.ClaimInventory;
import net.weesli.rclaim.util.BaseUtil;
import net.weesli.rozslib.inventory.ClickableItemStack;
import net.weesli.rozslib.inventory.types.SimpleInventory;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

public class ClaimResizeInventory extends ClaimInventory {
    private static final Menu menu = ConfigLoader.getMenuConfig().getResizeMenu();

    private int pageX, pageY;
    private Player player;
    private Claim claim;

    @Override
    public void openInventory(Player player, Claim claim) {
        SimpleInventory builder = new SimpleInventory(menu.getTitle(), 45);
        builder.setLayout("").fill(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), true);
        setupUpgradeInventory(player, builder, claim.getCenter(), claim);
        builder.openInventory(player);
        this.player = player;
        this.claim = claim;
    }

    private void setupUpgradeInventory(Player player, SimpleInventory builder, Location centerLocation, Claim claim) {
        int left = -4 + (pageX * 7);
        int right = 4 + (pageX * 7);
        int top = -2 + (pageY * 3);
        int bottom = 2 + (pageY * 3);


        Set<Integer> skipSlots = new HashSet<>(Arrays.asList(
                0, 1, 2, 3, 4, 5, 6, 7, 8,
                36, 37, 38, 39, 40, 41, 42, 43, 44,
                9, 18, 27, 35, 26, 17,
                45, 46, 47, 48, 49, 50, 51, 52, 53
        ));

        int index = 0;
        World world = centerLocation.getWorld();
        int centerChunkX = centerLocation.getBlockX() >> 4;
        int centerChunkZ = centerLocation.getBlockZ() >> 4;

        for (int z = top; z <= bottom; z++) {
            for (int x = left; x <= right; x++) {
                int chunkX = centerChunkX + x;
                int chunkZ = centerChunkZ + z;
                Location loc = new Location(world, chunkX * 16, 2, chunkZ * 16);

                if (skipSlots.contains(index)) {
                    index++;
                    continue;
                }
                getAreas(builder, player, loc, claim, index);
                index++;
            }
        }
        setupNavigationButtons(builder);
    }

    private void setupNavigationButtons(SimpleInventory builder) {
        ClickableItemStack topButton = new ClickableItemStack(new ItemStack(Material.ARROW), 4);
        ClickableItemStack bottomButton = new ClickableItemStack(new ItemStack(Material.ARROW), 40);
        ClickableItemStack leftButton = new ClickableItemStack(new ItemStack(Material.ARROW), 18);
        ClickableItemStack rightButton = new ClickableItemStack(new ItemStack(Material.ARROW), 26);

        builder.setItem(topButton, event -> handleNavigation(builder, "up"));
        builder.setItem(bottomButton, event -> handleNavigation(builder, "down"));
        builder.setItem(leftButton, event -> handleNavigation(builder, "left"));
        builder.setItem(rightButton, event -> handleNavigation(builder, "right"));
    }

    private void handleNavigation(SimpleInventory builder, String value) {
        switch (value) {
            case "up" -> pageY--;
            case "down" -> pageY++;
            case "left" -> pageX--;
            case "right" -> pageX++;
        }
        builder.getItems().clear();
        builder.getInventory().clear();
        setupUpgradeInventory(player, builder, claim.getCenter(), claim);
        builder.build();
        builder.openInventory(player);
    }

    private void getAreas(SimpleInventory inventory, Player player, Location location, Claim claim, int slot) {
        MenuItem item = getKeyForChunk(player, claim, location);
        ClickableItemStack itemStack = new ClickableItemStack(getItemStack(item), slot);
        inventory.setItem(itemStack, event -> handleItemClick(player, location, claim));
        updateItemMeta(itemStack, location,claim);
    }

    private MenuItem getKeyForChunk(Player player, Claim claim, Location location) {
        if (claim.contains(location)) {
            return menu.getItems().get("starter-claim");
        }
        Claim targetClaim = RClaim.getInstance().getCacheManager().getClaims().getCache().values().stream().filter(c -> {
            if (c.contains(location)) {
                return true;
            }
            for (SubClaim subClaim : c.getSubClaims()){
                if (subClaim.contains(location)) {
                    return true;
                }
            }
            return false;
        }).findFirst().orElse(null);
        if (targetClaim == null) {
            return menu.getItems().get("empty-claim");
        }
        if (targetClaim.isOwner(player.getUniqueId())) {
            return menu.getItems().get("self-claim");
        }
        return menu.getItems().get("not-available-claim");
    }

    private void handleItemClick(Player player, Location location, Claim claim) {
        if (!HWorldGuard.isAreaEnabled(player)) {
            player.sendMessage(RClaim.getInstance().getMessage("AREA_DISABLED"));
            return;
        }
        if (!BaseUtil.isActiveWorld(player.getWorld().getName())) {
            player.sendMessage(RClaim.getInstance().getMessage("NOT_IN_CLAIMABLE_WORLD"));
            return;
        }
        if (RClaim.getInstance().getEconomyManager().getEconomyIntegration().isActive() &&
                !RClaim.getInstance().getEconomyManager().getEconomyIntegration().hasEnough(player, ConfigLoader.getConfig().getClaimSettings().getClaimCostPerDay() * 30)) {
            player.sendMessage(RClaim.getInstance().getMessage("HASNT_MONEY"));
            return;
        }
        if (RClaim.getInstance().getClaimManager().isSuitable(location)) {
            player.sendMessage(RClaim.getInstance().getMessage("IS_NOT_SUITABLE"));
            return;
        }
        if (!isAdjacent(claim, location)) {
            player.sendMessage(RClaim.getInstance().getMessage("IS_NOT_ADJACENT"));
            return;
        }
        boolean success = RClaim.getInstance().getClaimManager().createSubClaim(player, claim, location);
        if (success) {
            SimpleInventory builder = new SimpleInventory(menu.getTitle(), 45);
            builder.setLayout("").fill(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), true);
            setupUpgradeInventory(player, builder, claim.getCenter(), claim);
            builder.openInventory(player);

            RClaim.getInstance().getEconomyManager().getEconomyIntegration().withdraw(player,
                    calculateSubClaimCost(claim));
        }
    }

    private void updateItemMeta(ClickableItemStack itemStack, Location location, Claim claim) {
        ItemMeta meta = itemStack.getItemStack().getItemMeta();
        List<String> updatedLore = meta.getLore().stream()
                .map(line -> line.replaceAll("<cost>", String.valueOf(calculateSubClaimCost(claim)))
                        .replaceAll("<x>", String.valueOf(location.getX()))
                        .replaceAll("<z>", String.valueOf(location.getZ())))
                .collect(Collectors.toList());
        meta.setLore(updatedLore);
        itemStack.getItemStack().setItemMeta(meta);
    }

    private int calculateSubClaimCost(Claim claim) {
        int totalSubClaimCount = claim.getSubClaims().size();
        int defaultClaimCost = ConfigLoader.getConfig().getClaimSettings().getClaimCostPerDay() * 30;
        return (int) (defaultClaimCost + (totalSubClaimCount * (defaultClaimCost * 0.1)));
    }

    private boolean isAdjacent(Claim claim, Location targetLocation) {
        int targetChunkX = targetLocation.getChunk().getX();
        int targetChunkZ = targetLocation.getChunk().getZ();
        int claimChunkX = claim.getX() / 16;
        int claimChunkZ = claim.getZ() / 16;
        if (isChunkAdjacent(claimChunkX, claimChunkZ, targetChunkX, targetChunkZ)) {
            return true;
        }

        for (SubClaim subClaim : claim.getSubClaims()) {
            int subClaimChunkX = subClaim.getX();
            int subClaimChunkZ = subClaim.getZ();
            if (isChunkAdjacent(subClaimChunkX, subClaimChunkZ, targetChunkX, targetChunkZ)) {
                return true;
            }
        }

        return false;
    }

    private boolean isChunkAdjacent(int chunkX, int chunkZ, int targetX, int targetZ) {
        return Math.abs(targetX - chunkX) <= 1 && Math.abs(targetZ - chunkZ) <= 1
                && !(targetX == chunkX && targetZ == chunkZ);
    }
}
