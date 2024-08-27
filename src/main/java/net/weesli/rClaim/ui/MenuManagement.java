package net.weesli.rClaim.ui;

import de.rapha149.signgui.SignGUI;
import de.rapha149.signgui.SignGUIAction;
import net.weesli.rClaim.RClaim;
import net.weesli.rClaim.management.ClaimManager;
import net.weesli.rClaim.management.ExplodeCause;
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

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class MenuManagement {

    public static Inventory getMainMenu(Player player){
        FileConfiguration config = RClaim.getInstance().getMenusFile().load();
        InventoryBuilder builder = new InventoryBuilder(RClaim.getInstance(), ColorBuilder.convertColors( config.getString("main-menu.title")), config.getInt("main-menu.size"));
        builder.setItem(config.getInt("main-menu.children.claims.slot"),new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack("main-menu.children.claims"), builder.build())
                .setCancelled(true)
                .setEvent(event -> player.openInventory(getClaimsMenu(player))));
        builder.setItem(config.getInt("main-menu.children.upgrade-claim.slot"),new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack("main-menu.children.upgrade-claim"), builder.build())
                .setEvent(event -> player.openInventory(getResizeInventory(player)))
                .setCancelled(true));
        builder.setItem(config.getInt("main-menu.children.members.slot"),new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack("main-menu.children.members"), builder.build())
                .setEvent(event -> player.openInventory(getUsersMenu(player)))
                .setCancelled(true));
        builder.setItem(config.getInt("main-menu.children.options.slot"),new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack("main-menu.children.options"), builder.build())
                .setEvent(event-> player.openInventory(getSettingsMenu(player)))
                .setCancelled(true));
        return builder.build();
    }

    public static Inventory getClaimsMenu(Player player){
        FileConfiguration config = RClaim.getInstance().getMenusFile().load();
        int i = 1;
        InventoryBuilder builder = new InventoryBuilder(RClaim.getInstance(), ColorBuilder.convertColors(config.getString("claims-menu.title")), config.getInt("claims-menu.size"));
        List<Claim> claims = ClaimManager.getPlayerData(player.getUniqueId()).getClaims();
        for (Claim claim : claims){
            ClickableItemStack itemStack = new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack("claims-menu.item-settings"), builder.build())
                    .setCancelled(true)
                    .setEvent(e -> {
                        if (e.isShiftClick() && e.isRightClick()){
                            player.openInventory(verifyMenu(player,VerifyAction.UNCLAIM, claim.getID()));
                            return;
                        }
                        player.openInventory(getUpgradeClaimMenu(claim,player));
                    });
            ItemMeta meta = itemStack.getItemStack().getItemMeta();
            meta.setDisplayName(meta.getDisplayName().replaceAll("<count>", String.valueOf(i)));
            meta.setLore(meta.getLore().stream().map(line -> line.replaceAll("<x>", String.valueOf(claim.getX())).replaceAll("<z>", String.valueOf(claim.getZ())).replaceAll("<time>", ClaimManager.getTimeFormat(claim.getID()))).collect(Collectors.toList()));
            itemStack.getItemStack().setItemMeta(meta);
            builder.addItem(itemStack);
            i++;
        }
        return builder.build();
    }

    public static Inventory getUpgradeClaimMenu(Claim claim, Player player){
        FileConfiguration config = RClaim.getInstance().getMenusFile().load();
        InventoryBuilder builder = new InventoryBuilder(RClaim.getInstance(), ColorBuilder.convertColors(config.getString("upgrade-menu.title")), config.getInt("upgrade-menu.size"));
        ClickableItemStack itemStack = new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack("upgrade-menu.item-settings"), builder.build())
                .setEvent(event -> {
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
                })
                .setCancelled(true);
        ItemMeta meta = itemStack.getItemStack().getItemMeta();
        meta.setLore(meta.getLore().stream().map(line -> line.replaceAll("<cost>", RClaim.getInstance().getConfig().getString("claim-settings.claim-cost"))).collect(Collectors.toList()));
        itemStack.getItemStack().setItemMeta(meta);
        builder.setItem(config.getInt("upgrade-menu.item-settings.slot"), itemStack);
        return builder.build();
    }

    public static Inventory getSettingsMenu(Player player){
        FileConfiguration config = RClaim.getInstance().getMenusFile().load();
        InventoryBuilder builder = new InventoryBuilder(RClaim.getInstance(), ColorBuilder.convertColors(config.getString("options-menu.title")), config.getInt("options-menu.size"));
        ClaimPlayer player_data = ClaimManager.getPlayerData(player.getUniqueId());
        ClickableItemStack spawn_animal = new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack("options-menu.children.spawn-animal"), builder.build())
                .setEvent(event -> {
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
                })
                .setCancelled(true);
        ClickableItemStack spawn_mob = new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack("options-menu.children.spawn-monster"), builder.build())
                .setEvent(event->{
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
                })
                .setCancelled(true);

        ClickableItemStack pvp = new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack("options-menu.children.pvp"), builder.build())
                .setEvent(event -> {
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
                })
                .setCancelled(true);

        ClickableItemStack explosion = new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack("options-menu.children.explosion"), builder.build())
                .setEvent(event -> {
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
                })
                .setCancelled(true);
        ClickableItemStack spread = new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack("options-menu.children.spread"), builder.build());
        spread.setEvent(event -> {
            List<Claim> player_claims = player_data.getClaims();
            player_claims.forEach(claim -> {
                if (claim.checkStatus(ClaimStatus.SPREAD)){
                    claim.removeClaimStatus(ClaimStatus.SPREAD);
                } else {
                    claim.addClaimStatus(ClaimStatus.SPREAD);
                }
                RClaim.getInstance().getStorage().updateClaim(claim);
            });
            player.openInventory(getSettingsMenu(player));
        }).setCancelled(true);

        spawn_animal.getItemStack().addItemFlags(ItemFlag.HIDE_ENCHANTS);
        spawn_mob.getItemStack().addItemFlags(ItemFlag.HIDE_ENCHANTS);
        pvp.getItemStack().addItemFlags(ItemFlag.HIDE_ENCHANTS);
        explosion.getItemStack().addItemFlags(ItemFlag.HIDE_ENCHANTS);
        spread.getItemStack().addItemFlags(ItemFlag.HIDE_ENCHANTS);
        for (ClaimStatus status : player_data.getClaims().get(0).getClaimStatuses()){
            switch (status){
                case SPAWN_ANIMAL:
                    spawn_animal.getItemStack().addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);
                    break;
                case SPAWN_MONSTER:
                    spawn_mob.getItemStack().addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);
                    break;

                case PVP:
                    pvp.getItemStack().addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);
                    break;

                case EXPLOSION:
                    explosion.getItemStack().addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);
                    break;

                case SPREAD:
                    spread.getItemStack().addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);
                    break;

            }
        }
        builder.setItem(config.getInt("options-menu.children.spawn-animal.slot"), spawn_animal);
        builder.setItem(config.getInt("options-menu.children.spawn-monster.slot"), spawn_mob);
        builder.setItem(config.getInt("options-menu.children.pvp.slot"), pvp);
        builder.setItem(config.getInt("options-menu.children.explosion.slot"), explosion);
        builder.setItem(config.getInt("options-menu.children.spread.slot"), spread);
        return builder.build();
    }

    public static Inventory getUsersMenu(Player player){
        FileConfiguration config = RClaim.getInstance().getMenusFile().load();
        InventoryBuilder builder = new InventoryBuilder(RClaim.getInstance(), ColorBuilder.convertColors(config.getString("members-menu.title")), config.getInt("members-menu.size"));
        int i = 0;
        Claim claim = ClaimManager.getPlayerData(player.getUniqueId()).getClaims().get(0);
        for (UUID member : claim.getMembers()){
            ClickableItemStack itemStack = new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack("members-menu.item-settings"), builder.build())
                    .setEvent(event-> {
                        if (event.isShiftClick()){
                            player.openInventory(verifyMenu(player, VerifyAction.UNTRUST_PLAYER, member));
                            return;
                        }
                        player.openInventory(getPermissionMenu(player,member));
                    })
                    .setCancelled(true)
                    .setSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
            ItemMeta meta = itemStack.getItemStack().getItemMeta();
            meta.setDisplayName(meta.getDisplayName().replaceAll("<name>", Bukkit.getOfflinePlayer(member).getName()));
            itemStack.getItemStack().setItemMeta(meta);
            builder.setItem(i,itemStack); i++;
        }
        ClickableItemStack add_member = new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack("members-menu.add-member"), builder.build())
                .setEvent(event -> callSign(player))
                .setCancelled(true);
        builder.setItem(config.getInt("members-menu.add-member.slot"), add_member);
        return builder.build();
    }

    public static Inventory getResizeInventory(Player player){
        FileConfiguration config = RClaim.getInstance().getMenusFile().load();
        InventoryBuilder builder = new InventoryBuilder(RClaim.getInstance(), ColorBuilder.convertColors(config.getString("resize-menu.title")), config.getInt("resize-menu.size"));
        setupUpgradeInventory(player,builder.build(),player.getChunk());
        return builder.build();
    }

    public static Inventory getPermissionMenu(Player player, UUID target){
        FileConfiguration config = RClaim.getInstance().getMenusFile().load();
        InventoryBuilder builder = new InventoryBuilder(RClaim.getInstance(), ColorBuilder.convertColors(config.getString("permissions-menu.title")), config.getInt("permissions-menu.size"));
        ClaimPlayer player_data = ClaimManager.getPlayerData(player.getUniqueId());
        List<Claim> target_claims = player_data.getClaims();
        ClickableItemStack block_break = new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack("permissions-menu.children.block-break"), builder.build())
                .setEvent(event-> {
                    target_claims.forEach(claim -> {
                        InteractPlayerPermission(target,claim,ClaimPermission.BLOCK_BREAK);
                        RClaim.getInstance().getStorage().updateClaim(claim);
                    });
                    player.openInventory(getPermissionMenu(player, target));
                })
                .setCancelled(true);

        ClickableItemStack block_place = new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack("permissions-menu.children.block-place"), builder.build())
                .setEvent(event -> {
                    target_claims.forEach(claim -> {
                        InteractPlayerPermission(target,claim,ClaimPermission.BLOCK_PLACE);
                        RClaim.getInstance().getStorage().updateClaim(claim);
                    });
                    player.openInventory(getPermissionMenu(player, target));
                })
                .setCancelled(true);

        ClickableItemStack pickup_item = new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack("permissions-menu.children.pickup-item"), builder.build())
                .setEvent(event -> {
                    target_claims.forEach(claim -> {
                        InteractPlayerPermission(target,claim,ClaimPermission.PICKUP_ITEM);
                        RClaim.getInstance().getStorage().updateClaim(claim);
                    });
                    player.openInventory(getPermissionMenu(player, target));
                })
                .setCancelled(true);

        ClickableItemStack drop_item = new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack("permissions-menu.children.drop-item"), builder.build())
                .setEvent(event-> {
                    target_claims.forEach(claim -> {
                        InteractPlayerPermission(target,claim,ClaimPermission.DROP_ITEM);
                        RClaim.getInstance().getStorage().updateClaim(claim);
                    });
                    player.openInventory(getPermissionMenu(player, target));
                })
                .setCancelled(true);

        ClickableItemStack container_open = new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack("permissions-menu.children.container-open"), builder.build())
                .setEvent(event-> {
                    target_claims.forEach(claim -> {
                        InteractPlayerPermission(target,claim,ClaimPermission.CONTAINER_OPEN);
                        RClaim.getInstance().getStorage().updateClaim(claim);
                    });
                    player.openInventory(getPermissionMenu(player, target));
                })
                .setCancelled(true);

        ClickableItemStack interact_entity = new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack("permissions-menu.children.interact-entity"), builder.build())
                .setEvent(event-> {
                    target_claims.forEach(claim -> {
                        InteractPlayerPermission(target,claim,ClaimPermission.INTERACT_ENTITY);
                        RClaim.getInstance().getStorage().updateClaim(claim);
                    });
                    player.openInventory(getPermissionMenu(player, target));
                })
                .setCancelled(true);

        ClickableItemStack attack_animal = new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack("permissions-menu.children.attack-animal"), builder.build())
                .setEvent(event->{
                    target_claims.forEach(claim -> {
                        InteractPlayerPermission(target,claim,ClaimPermission.ATTACK_ANIMAL);
                        RClaim.getInstance().getStorage().updateClaim(claim);
                    });
                    player.openInventory(getPermissionMenu(player, target));
                })
                .setCancelled(true);

        ClickableItemStack attack_monster = new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack("permissions-menu.children.attack-monster"), builder.build())
                .setEvent(event-> {
                    target_claims.forEach(claim -> {
                        InteractPlayerPermission(target,claim,ClaimPermission.ATTACK_MONSTER);
                        RClaim.getInstance().getStorage().updateClaim(claim);
                        });
                    player.openInventory(getPermissionMenu(player, target));
                })
                .setCancelled(true);
        ClickableItemStack break_container = new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack("permissions-menu.children.break-container"), builder.build())
                    .setEvent(event -> {
                    target_claims.forEach(claim -> {
                        InteractPlayerPermission(target,claim,ClaimPermission.BREAK_CONTAINER);
                        RClaim.getInstance().getStorage().updateClaim(claim);
                    });
                    player.openInventory(getPermissionMenu(player, target));
                    })
                    .setCancelled(true);
        ClickableItemStack use_door = new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack("permissions-menu.children.use-door"), builder.build())
                .setEvent(event -> {
                    target_claims.forEach(claim -> {
                        InteractPlayerPermission(target,claim,ClaimPermission.USE_DOOR);
                        RClaim.getInstance().getStorage().updateClaim(claim);
                    });
                    player.openInventory(getPermissionMenu(player, target));
                }).setCancelled(true);
        ClickableItemStack use_portal = new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack("permissions-menu.children.use-portal"), builder.build())
                .setEvent(event -> {
                    target_claims.forEach(claim -> {
                        InteractPlayerPermission(target,claim,ClaimPermission.USE_PORTAL);
                        RClaim.getInstance().getStorage().updateClaim(claim);
                    });
                    player.openInventory(getPermissionMenu(player, target));
                }).setCancelled(true);
        ClickableItemStack use_potion = new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack("permissions-menu.children.use-potion"), builder.build())
                .setEvent(event -> {
                    target_claims.forEach(claim -> {
                        InteractPlayerPermission(target,claim,ClaimPermission.USE_POTION);
                        RClaim.getInstance().getStorage().updateClaim(claim);
                    });
                    player.openInventory(getPermissionMenu(player, target));
                }).setCancelled(true);

        block_break.getItemStack().addItemFlags(ItemFlag.HIDE_ENCHANTS);
        block_place.getItemStack().addItemFlags(ItemFlag.HIDE_ENCHANTS);
        pickup_item.getItemStack().addItemFlags(ItemFlag.HIDE_ENCHANTS);
        container_open.getItemStack().addItemFlags(ItemFlag.HIDE_ENCHANTS);
        interact_entity.getItemStack().addItemFlags(ItemFlag.HIDE_ENCHANTS);
        attack_animal.getItemStack().addItemFlags(ItemFlag.HIDE_ENCHANTS);
        attack_monster.getItemStack().addItemFlags(ItemFlag.HIDE_ENCHANTS);
        break_container.getItemStack().addItemFlags(ItemFlag.HIDE_ENCHANTS);
        use_door.getItemStack().addItemFlags(ItemFlag.HIDE_ENCHANTS);
        use_portal.getItemStack().addItemFlags(ItemFlag.HIDE_ENCHANTS);
        use_potion.getItemStack().addItemFlags(ItemFlag.HIDE_ENCHANTS);

        for (ClaimPermission permission : player_data.getClaims().get(0).getClaimPermissions().getOrDefault(target, new ArrayList<>())) {
            switch (permission) {
                case BLOCK_BREAK:
                    block_break.getItemStack().addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);
                    break;
                case BLOCK_PLACE:
                    block_place.getItemStack().addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);
                    break;
                case PICKUP_ITEM:
                    pickup_item.getItemStack().addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);
                    break;
                case DROP_ITEM:
                    drop_item.getItemStack().addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);
                    break;
                case CONTAINER_OPEN:
                    container_open.getItemStack().addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);
                    break;
                case INTERACT_ENTITY:
                    interact_entity.getItemStack().addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);
                    break;
                case ATTACK_ANIMAL:
                    attack_animal.getItemStack().addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);
                    break;
                case ATTACK_MONSTER:
                    attack_monster.getItemStack().addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);
                    break;
                case BREAK_CONTAINER:
                    break_container.getItemStack().addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);
                    break;
                case USE_DOOR:
                    use_door.getItemStack().addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);
                    break;
                case USE_PORTAL:
                    use_portal.getItemStack().addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);
                    break;
                case USE_POTION:
                    use_potion.getItemStack().addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);
                    break;
                default:
                    break;
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
        builder.setItem(config.getInt("permissions-menu.children.break-container.slot"), break_container);
        builder.setItem(config.getInt("permissions-menu.children.use-door.slot"), use_door);
        builder.setItem(config.getInt("permissions-menu.children.use-portal.slot"), use_portal);
        builder.setItem(config.getInt("permissions-menu.children.use-potion.slot"), use_potion);

        return builder.build();
    }

    private static void InteractPlayerPermission(UUID target, Claim claim, ClaimPermission permission){
        if (claim.checkPermission(target, permission)){
            claim.removePermission(target, permission);
        } else {
            claim.addPermission(target, permission);
        }
        RClaim.getInstance().getStorage().updateClaim(claim);
    }


    private static void setupUpgradeInventory(Player player, Inventory inventory, Chunk centerChunk) {
        List<Chunk> chunks = new ArrayList<>();
        int chunkRadius = 4;
        int totalChunks = 54;

        int centerX = centerChunk.getX();
        int centerZ = centerChunk.getZ();

        for (int dx = -chunkRadius; dx <= chunkRadius; dx++) {
            for (int dz = -chunkRadius; dz <= chunkRadius; dz++) {
                if (chunks.size() < totalChunks) {
                    Chunk targetedChunk = player.getWorld().getChunkAt(centerX + dx, centerZ + dz);
                    chunks.add(targetedChunk);
                }
            }
        }
        for (int i = 0; i < totalChunks; i++) {
            if (i < chunks.size()) {
                Chunk targetedChunk = chunks.get(i);
                inventory.setItem(i, getAreas(inventory, player, centerChunk, targetedChunk));
            }
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
        } else if (!claim.isPresent()) {
            key = "resize-menu.children.empty-claim";
        } else if (claim.get().getOwner().equals(player.getUniqueId())) {
            key = "resize-menu.children.self-claim";
        } else {
            key = "resize-menu.children.not-available-claim";
        }

        ClickableItemStack itemStack = new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack(key), inventory)
                .setEvent(event->{
                    if(!ClaimManager.checkWorld(player.getWorld().getName())){
                        player.sendMessage(RClaim.getInstance().getMessage("NOT_IN_CLAIMABLE_WORLD"));
                        return;
                    }
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
                })
                .setCancelled(true);
        ItemMeta meta = itemStack.getItemStack().getItemMeta();
        meta.setLore(meta.getLore().stream().map(line -> line.replaceAll("<cost>", RClaim.getInstance().getConfig().getString("claim-settings.claim-cost")).replaceAll("<x>", String.valueOf(chunk.getX()*16)).replaceAll("<z>", String.valueOf(chunk.getZ() *16))).collect(Collectors.toList()));
        itemStack.getItemStack().setItemMeta(meta);
        return itemStack.getItemStack();
    }

    public static Inventory verifyMenu(Player player, VerifyAction action, Object varible){
        FileConfiguration config = RClaim.getInstance().getMenusFile().load();
        InventoryBuilder builder = new InventoryBuilder(RClaim.getInstance(), ColorBuilder.convertColors(config.getString("verify-menu.title")), config.getInt("verify-menu.size"));
        builder.setItem(config.getInt("verify-menu.children.confirm.slot"), new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack("verify-menu.children.confirm"), builder.build())
                .setEvent(event->{
                            switch (action){
                                case UNTRUST_PLAYER:
                                    player.performCommand("claim untrust " + Bukkit.getOfflinePlayer(UUID.fromString(String.valueOf(varible))).getName());
                                    player.openInventory(getUsersMenu(player));
                                    break;
                                case UNCLAIM:
                                    List<Claim> claims = ClaimManager.getClaims().stream().filter(c -> c.isOwner(player.getUniqueId())).collect(Collectors.toList());
                                    boolean isCenter = claims.get(0).getID().equals(String.valueOf(varible));
                                    ClaimManager.ExplodeClaim(String.valueOf(varible), ExplodeCause.UNCLAIM, isCenter);
                                    player.sendMessage(RClaim.getInstance().getMessage("UNCLAIMED_CLAIM"));
                                    player.closeInventory();
                                    break;
                            }
                        })
                .setCancelled(true)
                .setSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP));

        builder.setItem(config.getInt("verify-menu.children.deny.slot"), new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack("verify-menu.children.deny"), builder.build())
                .setEvent(event->{
                    player.openInventory(getMainMenu(player));
                })
                .setCancelled(true));
        return builder.build();
    }


    private static void callSign(Player player){
        SignGUI gui = SignGUI.builder().callHandlerSynchronously(RClaim.getInstance())
                .setLines("", "----------","Enter player name", "----------")
                .setType(Material.DARK_OAK_SIGN)

                .setColor(DyeColor.WHITE)

                .setHandler((p, result) -> {
                    String name = result.getLine(0);

                    if (name.isEmpty()) {
                        return Collections.emptyList();
                    }else {
                        ClaimPlayer player_data = ClaimManager.getPlayerData(player.getUniqueId());
                        if (name.equals(player.getName())){
                            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 5, 1);
                            return new ArrayList<>(Collections.singleton(SignGUIAction.displayNewLines("", "----------", "Enter player name", "----------")));
                        }else if (!isCheckPlayer(name)){
                            player.getWorld().playSound(player.getLocation(),Sound.ENTITY_VILLAGER_NO, 5, 1);
                            return new ArrayList<>(Collections.singleton(SignGUIAction.displayNewLines("", "----------", "Player not found", "----------")));
                        }else if (player_data.getClaims().get(0).getMembers().contains(Bukkit.getOfflinePlayer(name).getUniqueId())){
                            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 5, 1);
                            return new ArrayList<>(Collections.singleton(SignGUIAction.displayNewLines("", "----------", "Enter player name", "----------")));
                        }else {
                            player.performCommand("claim trust " + name);
                            player.getWorld().playSound(player.getLocation(),Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 5, 1);
                            player.openInventory(getUsersMenu(player));
                            return Collections.emptyList();
                        }
                    }
                })
                .build();
        gui.open(player);
    }


    private static boolean isCheckPlayer(String name){
        for (OfflinePlayer player : Bukkit.getOfflinePlayers()){
            if (player.getName().equalsIgnoreCase(name)){
                return true;
            }
        }
        return false;
    }

}
