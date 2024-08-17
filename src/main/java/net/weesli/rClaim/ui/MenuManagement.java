package net.weesli.rClaim.ui;

import net.weesli.rClaim.RClaim;
import net.weesli.rClaim.management.ClaimManager;
import net.weesli.rClaim.tasks.ClaimTask;
import net.weesli.rClaim.utils.Claim;
import net.weesli.rClaim.utils.ClaimPermission;
import net.weesli.rClaim.utils.ClaimPlayer;
import net.weesli.rClaim.utils.ClaimStatus;
import net.weesli.rozsLib.ColorManager.ColorBuilder;
import net.weesli.rozsLib.InventoryManager.ClickableItemStack;
import net.weesli.rozsLib.InventoryManager.InventoryBuilder;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MenuManagement {

    static FileConfiguration config = RClaim.getInstance().getMenusFile().load();

    public static Inventory getMainMenu(Player player){
        InventoryBuilder builder = new InventoryBuilder(RClaim.getInstance(), ColorBuilder.convertColors( config.getString("main-menu.title")), config.getInt("main-menu.size"));
        builder.setItem(config.getInt("main-menu.children.claims.slot"),        new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack("main-menu.children.claims"), builder.build()) {
            @Override
            protected void addListener(InventoryClickEvent inventoryClickEvent) {
                player.openInventory(getClaimsMenu(player));
            }
        }.setCancelled(true));
        builder.setItem(config.getInt("main-menu.children.upgrade-claim.slot"),new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack("main-menu.children.upgrade-claim"), builder.build()) {
            @Override
            protected void addListener(InventoryClickEvent inventoryClickEvent) {
                player.openInventory(getResizeInventory(player));
            }
        }.setCancelled(true));
        builder.setItem(config.getInt("main-menu.children.members.slot"),new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack("main-menu.children.members"), builder.build()) {
            @Override
            protected void addListener(InventoryClickEvent inventoryClickEvent) {
                player.openInventory(getUsersMenu(player));
            }
        }.setCancelled(true));
        builder.setItem(config.getInt("main-menu.children.options.slot"),new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack("main-menu.children.options"), builder.build()) {
            @Override
            protected void addListener(InventoryClickEvent inventoryClickEvent) {
                player.openInventory(getSettingsMenu(player));
            }
        }.setCancelled(true));
        return builder.build();
    }

    public static Inventory getClaimsMenu(Player player){
        int i = 1;
        InventoryBuilder builder = new InventoryBuilder(RClaim.getInstance(), ColorBuilder.convertColors(config.getString("claims-menu.title")), config.getInt("claims-menu.size"));
        for (Claim claim : ClaimManager.getPlayerData(player.getUniqueId()).getClaims()){
            ClickableItemStack itemStack = new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack("claims-menu.item-settings"), builder.build()) {
                @Override
                protected void addListener(InventoryClickEvent inventoryClickEvent) {
                    player.openInventory(getUpgradeClaimMenu(claim,player));
                }
            }.setCancelled(true);
            ItemMeta meta = itemStack.getItemStack().getItemMeta();
            meta.setDisplayName(meta.getDisplayName().replaceAll("<count>", String.valueOf(i)));
            meta.setLore(meta.getLore().stream().map(line -> line.replaceAll("<x>", String.valueOf(claim.getX())).replaceAll("<z>", String.valueOf(claim.getZ())).replaceAll("<time>", ClaimManager.getTimeFormat(claim.getID()))).toList());
            itemStack.getItemStack().setItemMeta(meta);
            builder.addItem(itemStack);
            i++;
        }
        return builder.build();
    }

    public static Inventory getUpgradeClaimMenu(Claim claim, Player player){
        InventoryBuilder builder = new InventoryBuilder(RClaim.getInstance(), ColorBuilder.convertColors(config.getString("upgrade-menu.title")), config.getInt("upgrade-menu.size"));
        ClickableItemStack itemStack = new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack("upgrade-menu.item-settings"), builder.build()) {
            @Override
            protected void addListener(InventoryClickEvent inventoryClickEvent) {
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
            }
        }.setCancelled(true);
        ItemMeta meta = itemStack.getItemStack().getItemMeta();
        meta.setLore(meta.getLore().stream().map(line -> line.replaceAll("<cost>", RClaim.getInstance().getConfig().getString("claim-settings.claim-cost"))).toList());
        itemStack.getItemStack().setItemMeta(meta);
        builder.setItem(config.getInt("upgrade-menu.item-settings.slot"), itemStack);
        return builder.build();
    }

    public static Inventory getSettingsMenu(Player player){
        InventoryBuilder builder = new InventoryBuilder(RClaim.getInstance(), ColorBuilder.convertColors(config.getString("options-menu.title")), config.getInt("options-menu.size"));
        ClaimPlayer player_data = ClaimManager.getPlayerData(player.getUniqueId());
        ClickableItemStack spawn_animal = new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack("options-menu.children.spawn-animal"), builder.build()) {
            @Override
            protected void addListener(InventoryClickEvent inventoryClickEvent) {
                List<Claim> player_claims = player_data.getClaims();
                player_claims.forEach(claim -> {
                    if (claim.checkStatus(ClaimStatus.SPAWN_ANIMAL)){
                        claim.removeClaimStatus(ClaimStatus.SPAWN_ANIMAL);
                    }else {
                        claim.addClaimStatus(ClaimStatus.SPAWN_ANIMAL);
                    }
                    RClaim.getInstance().getStorage().updateClaim(claim);
                });
                player.openInventory(getSettingsMenu(player));
            }
        }.setCancelled(true);
        ClickableItemStack spawn_mob = new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack("options-menu.children.spawn-monster"), builder.build()) {
            @Override
            protected void addListener(InventoryClickEvent inventoryClickEvent) {
                List<Claim> player_claims = player_data.getClaims();
                player_claims.forEach(claim -> {
                    if (claim.checkStatus(ClaimStatus.SPAWN_MONSTER)){
                        claim.removeClaimStatus(ClaimStatus.SPAWN_MONSTER);
                    }else {
                        claim.addClaimStatus(ClaimStatus.SPAWN_MONSTER);
                    }
                    RClaim.getInstance().getStorage().updateClaim(claim);
                });
                player.openInventory(getSettingsMenu(player));
            }
        }.setCancelled(true);

        ClickableItemStack pvp = new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack("options-menu.children.pvp"), builder.build()) {
            @Override
            protected void addListener(InventoryClickEvent inventoryClickEvent) {
                List<Claim> player_claims = player_data.getClaims();
                player_claims.forEach(claim -> {
                    if (claim.checkStatus(ClaimStatus.PVP)){
                        claim.removeClaimStatus(ClaimStatus.PVP);
                    }else {
                        claim.addClaimStatus(ClaimStatus.PVP);
                    }
                    RClaim.getInstance().getStorage().updateClaim(claim);
                });
                player.openInventory(getSettingsMenu(player));
            }
        }.setCancelled(true);

        ClickableItemStack explosion = new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack("options-menu.children.explosion"), builder.build()) {
            @Override
            protected void addListener(InventoryClickEvent inventoryClickEvent) {
                List<Claim> player_claims = player_data.getClaims();
                player_claims.forEach(claim -> {
                    if (claim.checkStatus(ClaimStatus.EXPLOSION)){
                        claim.removeClaimStatus(ClaimStatus.EXPLOSION);
                    } else {
                        claim.addClaimStatus(ClaimStatus.EXPLOSION);
                    }
                    RClaim.getInstance().getStorage().updateClaim(claim);
                });
                player.openInventory(getSettingsMenu(player));
            }
        }.setCancelled(true);

        for (ClaimStatus status : player_data.getClaims().get(0).getClaimStatuses()){
            switch (status){
                case SPAWN_ANIMAL -> spawn_animal.getItemStack().addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);
                case SPAWN_MONSTER -> spawn_mob.getItemStack().addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);
                case PVP -> pvp.getItemStack().addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);
                case EXPLOSION -> explosion.getItemStack().addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);
            }
        }
        builder.setItem(config.getInt("options-menu.children.spawn-animal.slot"), spawn_animal);
        builder.setItem(config.getInt("options-menu.children.spawn-monster.slot"), spawn_mob);
        builder.setItem(config.getInt("options-menu.children.pvp.slot"), pvp);
        builder.setItem(config.getInt("options-menu.children.explosion.slot"), explosion);
        return builder.build();
    }

    public static Inventory getUsersMenu(Player player){
        InventoryBuilder builder = new InventoryBuilder(RClaim.getInstance(), ColorBuilder.convertColors(config.getString("members-menu.title")), config.getInt("members-menu.size"));
        for (UUID member : ClaimManager.getPlayerData(player.getUniqueId()).getClaims().get(0).getMembers()){
            ClickableItemStack itemStack = new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack("members-menu.item-settings"), builder.build()) {
                @Override
                protected void addListener(InventoryClickEvent inventoryClickEvent) {
                    player.openInventory(getPermissionMenu(player, member));
                }
            }.setCancelled(true);
            ItemMeta meta = itemStack.getItemStack().getItemMeta();
            meta.setDisplayName(meta.getDisplayName().replaceAll("<name>", Bukkit.getOfflinePlayer(member).getName()));
            itemStack.getItemStack().setItemMeta(meta);
            builder.addItem(itemStack);
        }
        return builder.build();
    }

    public static Inventory getResizeInventory(Player player){
        InventoryBuilder builder = new InventoryBuilder(RClaim.getInstance(), ColorBuilder.convertColors(config.getString("resize-menu.title")), config.getInt("resize-menu.size"));
        setupUpgradeInventory(player,builder.build(),player.getChunk());
        return builder.build();
    }

    public static Inventory getPermissionMenu(Player player, UUID target){
        InventoryBuilder builder = new InventoryBuilder(RClaim.getInstance(), ColorBuilder.convertColors(config.getString("permissions-menu.title")), config.getInt("permissions-menu.size"));
        ClaimPlayer player_data = ClaimManager.getPlayerData(player.getUniqueId());

        ClickableItemStack block_break = new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack("permissions-menu.children.block-break"), builder.build()) {
            @Override
            protected void addListener(InventoryClickEvent inventoryClickEvent) {
                List<Claim> target_claims = player_data.getClaims();
                target_claims.forEach(claim -> {
                    if (claim.checkPermission(target, ClaimPermission.BLOCK_BREAK)){
                        claim.removePermission(target, ClaimPermission.BLOCK_BREAK);
                    } else {
                        claim.addPermission(target, ClaimPermission.BLOCK_BREAK);
                    }
                    RClaim.getInstance().getStorage().updateClaim(claim);
                });
                player.openInventory(getPermissionMenu(player, target));
            }
        }.setCancelled(true);

        ClickableItemStack block_place = new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack("permissions-menu.children.block-place"), builder.build()) {
            @Override
            protected void addListener(InventoryClickEvent inventoryClickEvent) {
                List<Claim> target_claims = player_data.getClaims();
                target_claims.forEach(claim -> {
                    if (claim.checkPermission(target, ClaimPermission.BLOCK_PLACE)){
                        claim.removePermission(target, ClaimPermission.BLOCK_PLACE);
                    } else {
                        claim.addPermission(target, ClaimPermission.BLOCK_PLACE);
                    }
                    RClaim.getInstance().getStorage().updateClaim(claim);
                });
                player.openInventory(getPermissionMenu(player, target));
            }
        }.setCancelled(true);

        ClickableItemStack pickup_item = new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack("permissions-menu.children.pickup-item"), builder.build()) {
            @Override
            protected void addListener(InventoryClickEvent inventoryClickEvent) {
                List<Claim> target_claims = player_data.getClaims();
                target_claims.forEach(claim -> {
                    if (claim.checkPermission(target, ClaimPermission.PICKUP_ITEM)){
                        claim.removePermission(target, ClaimPermission.PICKUP_ITEM);
                    } else {
                        claim.addPermission(target, ClaimPermission.PICKUP_ITEM);
                    }
                    RClaim.getInstance().getStorage().updateClaim(claim);
                });
                player.openInventory(getPermissionMenu(player, target));
            }
        }.setCancelled(true);

        ClickableItemStack drop_item = new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack("permissions-menu.children.drop-item"), builder.build()) {
            @Override
            protected void addListener(InventoryClickEvent inventoryClickEvent) {
                List<Claim> target_claims = player_data.getClaims();
                target_claims.forEach(claim -> {
                    if (claim.checkPermission(target, ClaimPermission.DROP_ITEM)){
                        claim.removePermission(target, ClaimPermission.DROP_ITEM);
                    } else {
                        claim.addPermission(target, ClaimPermission.DROP_ITEM);
                    }
                    RClaim.getInstance().getStorage().updateClaim(claim);
                });
                player.openInventory(getPermissionMenu(player, target));
            }
        }.setCancelled(true);

        ClickableItemStack container_open = new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack("permissions-menu.children.container-open"), builder.build()) {
            @Override
            protected void addListener(InventoryClickEvent inventoryClickEvent) {
                List<Claim> target_claims = player_data.getClaims();
                target_claims.forEach(claim -> {
                    if (claim.checkPermission(target, ClaimPermission.CONTAINER_OPEN)){
                        claim.removePermission(target, ClaimPermission.CONTAINER_OPEN);
                    } else {
                        claim.addPermission(target, ClaimPermission.CONTAINER_OPEN);
                    }
                    RClaim.getInstance().getStorage().updateClaim(claim);
                });
                player.openInventory(getPermissionMenu(player, target));
            }
        }.setCancelled(true);

        ClickableItemStack interact_entity = new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack("permissions-menu.children.interact-entity"), builder.build()) {
            @Override
            protected void addListener(InventoryClickEvent inventoryClickEvent) {
                List<Claim> target_claims = player_data.getClaims();
                target_claims.forEach(claim -> {
                    if (claim.checkPermission(target, ClaimPermission.INTERACT_ENTITY)){
                        claim.removePermission(target, ClaimPermission.INTERACT_ENTITY);
                    } else {
                        claim.addPermission(target, ClaimPermission.INTERACT_ENTITY);
                    }
                    RClaim.getInstance().getStorage().updateClaim(claim);
                });
                player.openInventory(getPermissionMenu(player, target));
            }
        }.setCancelled(true);

        ClickableItemStack attack_animal = new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack("permissions-menu.children.attack-animal"), builder.build()) {
            @Override
            protected void addListener(InventoryClickEvent inventoryClickEvent) {
                List<Claim> target_claims = player_data.getClaims();
                target_claims.forEach(claim -> {
                    if (claim.checkPermission(target, ClaimPermission.ATTACK_ANIMAL)){
                        claim.removePermission(target, ClaimPermission.ATTACK_ANIMAL);
                    } else {
                        claim.addPermission(target, ClaimPermission.ATTACK_ANIMAL);
                    }
                    RClaim.getInstance().getStorage().updateClaim(claim);
                });
                player.openInventory(getPermissionMenu(player, target));
            }
        }.setCancelled(true);

        ClickableItemStack attack_monster = new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack("permissions-menu.children.attack-monster"), builder.build()) {
            @Override
            protected void addListener(InventoryClickEvent inventoryClickEvent) {
                List<Claim> target_claims = player_data.getClaims();
                target_claims.forEach(claim -> {
                    if (claim.checkPermission(target, ClaimPermission.ATTACK_MONSTER)){
                        claim.removePermission(target, ClaimPermission.ATTACK_MONSTER);
                    } else {
                        claim.addPermission(target, ClaimPermission.ATTACK_MONSTER);
                    }
                    RClaim.getInstance().getStorage().updateClaim(claim);
                });
                player.openInventory(getPermissionMenu(player, target));
            }
        }.setCancelled(true);

        for (ClaimPermission permission : player_data.getClaims().get(0).getClaimPermissions().getOrDefault(target, new ArrayList<>())) {
            switch (permission) {
                case BLOCK_BREAK -> block_break.getItemStack().addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);
                case BLOCK_PLACE -> block_place.getItemStack().addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);
                case PICKUP_ITEM -> pickup_item.getItemStack().addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);
                case DROP_ITEM -> drop_item.getItemStack().addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);
                case CONTAINER_OPEN -> container_open.getItemStack().addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);
                case INTERACT_ENTITY -> interact_entity.getItemStack().addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);
                case ATTACK_ANIMAL -> attack_animal.getItemStack().addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);
                case ATTACK_MONSTER -> attack_monster.getItemStack().addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);
            }
        }

        builder.setItem(config.getInt("permissions-menu.children.block-break.slot"), block_break);
        builder.setItem(config.getInt("permissions-menu.children.block-place.slot"), block_place);
        builder.setItem(config.getInt("permissions-menu.children.pickup-item.slot"), pickup_item);
        builder.setItem(config.getInt("permissions-menu.children.drop-item.slot"), drop_item);
        builder.setItem(config.getInt("permissions-menu.children.container-open.slot"), container_open);
        builder.setItem(config.getInt("permissions-menu.children.interact-entity.slot"), interact_entity);
        builder.setItem(config.getInt("permissions-menu.children.attack-animal.slot"), attack_animal);
        builder.setItem(config.getInt("permissions-menu.children.attack-monster.slot"), attack_monster);

        return builder.build();
    }


    private static void setupUpgradeInventory(Player player, Inventory inventory, Chunk chunk) {
        List<Chunk> chunks = new ArrayList<>();
        int chunkRadius = 4;
        int totalChunks = 54;

        for (int dx = -chunkRadius; dx <= chunkRadius && chunks.size() < totalChunks; dx++) {
            for (int dz = -chunkRadius; dz <= chunkRadius && chunks.size() < totalChunks; dz++) {
                Chunk targeted_chunk = player.getWorld().getChunkAt(chunk.getX() + dx, chunk.getZ() + dz);
                chunks.add(targeted_chunk);
            }
        }

        for (int i = 0; i < totalChunks; i++) {
            Chunk targeted_chunk = chunks.get(i);
            inventory.setItem(i, getAreas(inventory, player, chunk, targeted_chunk));
        }
    }

    private static ItemStack getAreas(Inventory inventory, Player player, Chunk currentChunk, Chunk chunk) {
        Optional<Claim> claim = ClaimManager.getClaims().stream()
                .filter(claim1 -> {
                    Chunk claimChunk = claim1.getChunk();
                    return claimChunk.getX() == chunk.getX() && claimChunk.getZ() == chunk.getZ();
                })
                .findFirst();


        String key;
        if (currentChunk.equals(chunk)) {
            key = "resize-menu.children.starter-claim";
        } else if (claim.isEmpty()) {
            key = "resize-menu.children.empty-claim";
        } else if (claim.get().getOwner().equals(player.getUniqueId())) {
            key = "resize-menu.children.self-claim";
        } else {
            key = "resize-menu.children.not-available-claim";
        }

        ClickableItemStack itemStack = new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack(key), inventory) {
            @Override
            protected void addListener(InventoryClickEvent inventoryClickEvent) {
                if (RClaim.getInstance().getEconomy().isActive()){
                    if (!RClaim.getInstance().getEconomy().hasEnough(player, RClaim.getInstance().getConfig().getInt("claim-settings.claim-cost"))){
                        player.sendMessage(RClaim.getInstance().getMessage("HASNT_MONEY"));
                        return;
                    }
                    RClaim.getInstance().getEconomy().withdraw(player, RClaim.getInstance().getConfig().getInt("claim-settings.claim-cost"));
                }
                if (!ClaimManager.isSuitable(chunk)){
                    ClaimManager.createClaim(chunk, player, false);
                    player.openInventory(getResizeInventory(player));
                }else {
                    player.sendMessage(RClaim.getInstance().getMessage("IS_NOT_SUITABLE"));
                }
            }
        }.setCancelled(true);
        ItemMeta meta = itemStack.getItemStack().getItemMeta();
        meta.setLore(meta.getLore().stream().map(line -> line.replaceAll("<cost>", RClaim.getInstance().getConfig().getString("claim-settings.claim-cost")).replaceAll("<x>", String.valueOf(chunk.getX()*16)).replaceAll("<z>", String.valueOf(chunk.getZ() *16))).toList());
        itemStack.getItemStack().setItemMeta(meta);
        return itemStack.getItemStack();
    }

}
