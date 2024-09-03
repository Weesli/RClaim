package net.weesli.rClaim.ui;

import de.rapha149.signgui.SignGUI;
import de.rapha149.signgui.SignGUIAction;
import net.weesli.rClaim.RClaim;
import net.weesli.rClaim.hooks.HWorldGuard;
import net.weesli.rClaim.management.ClaimManager;
import net.weesli.rClaim.management.ExplodeCause;
import net.weesli.rClaim.tasks.ClaimTask;
import net.weesli.rClaim.utils.Claim;
import net.weesli.rClaim.utils.ClaimPermission;
import net.weesli.rClaim.utils.ClaimPlayer;
import net.weesli.rClaim.utils.ClaimStatus;
import net.weesli.rozsLib.color.ColorBuilder;
import net.weesli.rozsLib.inventory.ClickableItemStack;
import net.weesli.rozsLib.inventory.InventoryBuilder;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class MenuManagement {
    public static FileConfiguration config = RClaim.getInstance().getMenusFile().load();

    public static Inventory getMainMenu(Player player){
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
        
        InventoryBuilder builder = new InventoryBuilder(RClaim.getInstance(), ColorBuilder.convertColors(config.getString("options-menu.title")), config.getInt("options-menu.size"));
        ClaimPlayer player_data = ClaimManager.getPlayerData(player.getUniqueId());
        ClickableItemStack spawn_animal = new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack("options-menu.children.spawn-animal"), builder.build())
                .setEvent(event -> {
                    Claim claim = player_data.getClaims().get(0);
                    if (claim.checkStatus(ClaimStatus.SPAWN_ANIMAL)){
                        claim.removeClaimStatus(ClaimStatus.SPAWN_ANIMAL);
                    }else {
                        claim.addClaimStatus(ClaimStatus.SPAWN_ANIMAL);
                    }
                    player.openInventory(getSettingsMenu(player));
                })
                .setCancelled(true);
        ClickableItemStack spawn_mob = new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack("options-menu.children.spawn-monster"), builder.build())
                .setEvent(event->{
                    Claim claim = player_data.getClaims().get(0);
                    if (claim.checkStatus(ClaimStatus.SPAWN_MONSTER)){
                        claim.removeClaimStatus(ClaimStatus.SPAWN_MONSTER);
                    }else {
                        claim.addClaimStatus(ClaimStatus.SPAWN_MONSTER);
                    }
                    player.openInventory(getSettingsMenu(player));
                })
                .setCancelled(true);

        ClickableItemStack pvp = new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack("options-menu.children.pvp"), builder.build())
                .setEvent(event -> {
                    Claim claim = player_data.getClaims().get(0);
                    if (claim.checkStatus(ClaimStatus.PVP)){
                        claim.removeClaimStatus(ClaimStatus.PVP);
                    }else {
                        claim.addClaimStatus(ClaimStatus.PVP);
                    }
                    player.openInventory(getSettingsMenu(player));
                })
                .setCancelled(true);

        ClickableItemStack explosion = new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack("options-menu.children.explosion"), builder.build())
                .setEvent(event -> {
                    Claim claim = player_data.getClaims().get(0);
                    if (claim.checkStatus(ClaimStatus.EXPLOSION)){
                        claim.removeClaimStatus(ClaimStatus.EXPLOSION);
                    } else {
                        claim.addClaimStatus(ClaimStatus.EXPLOSION);
                    }
                    player.openInventory(getSettingsMenu(player));
                })
                .setCancelled(true);
        ClickableItemStack spread = new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack("options-menu.children.spread"), builder.build());
        spread.setEvent(event -> {
            Claim claim = player_data.getClaims().get(0);
            if (claim.checkStatus(ClaimStatus.SPREAD)){
                claim.removeClaimStatus(ClaimStatus.SPREAD);
            } else {
                claim.addClaimStatus(ClaimStatus.SPREAD);
            }
            player.openInventory(getSettingsMenu(player));
        }).setCancelled(true);
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
        builder.setItem(config.getInt("options-menu.children.spawn-animal.slot"), setupFlagItem(spawn_animal.getItemStack()));
        builder.setItem(config.getInt("options-menu.children.spawn-monster.slot"), setupFlagItem(spawn_mob.getItemStack()));
        builder.setItem(config.getInt("options-menu.children.pvp.slot"), setupFlagItem(pvp.getItemStack()));
        builder.setItem(config.getInt("options-menu.children.explosion.slot"), setupFlagItem(explosion.getItemStack()));
        builder.setItem(config.getInt("options-menu.children.spread.slot"), setupFlagItem(spread.getItemStack()));
        return builder.build();
    }

    public static Inventory getUsersMenu(Player player){
        
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
        
        InventoryBuilder builder = new InventoryBuilder(RClaim.getInstance(), ColorBuilder.convertColors(config.getString("resize-menu.title")), config.getInt("resize-menu.size"));
        setupUpgradeInventory(player,builder.build(),player.getLocation().getChunk());
        return builder.build();
    }

    public static Inventory getPermissionMenu(Player player, UUID target){
        
        InventoryBuilder builder = new InventoryBuilder(RClaim.getInstance(), ColorBuilder.convertColors(config.getString("permissions-menu.title")), config.getInt("permissions-menu.size"));
        ClaimPlayer player_data = ClaimManager.getPlayerData(player.getUniqueId());
        Claim claim = player_data.getClaims().get(0);
        ClickableItemStack block_break = new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack("permissions-menu.children.block-break"), builder.build())
                .setEvent(event-> {
                    InteractPlayerPermission(target,claim,ClaimPermission.BLOCK_BREAK);
                    player.openInventory(getPermissionMenu(player, target));
                })
                .setCancelled(true);

        ClickableItemStack block_place = new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack("permissions-menu.children.block-place"), builder.build())
                .setEvent(event -> {
                    InteractPlayerPermission(target,claim,ClaimPermission.BLOCK_PLACE);
                    player.openInventory(getPermissionMenu(player, target));
                })
                .setCancelled(true);

        ClickableItemStack pickup_item = new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack("permissions-menu.children.pickup-item"), builder.build())
                .setEvent(event -> {
                    InteractPlayerPermission(target,claim,ClaimPermission.PICKUP_ITEM);
                    player.openInventory(getPermissionMenu(player, target));
                })
                .setCancelled(true);

        ClickableItemStack drop_item = new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack("permissions-menu.children.drop-item"), builder.build())
                .setEvent(event-> {
                    InteractPlayerPermission(target,claim,ClaimPermission.DROP_ITEM);
                    player.openInventory(getPermissionMenu(player, target));
                })
                .setCancelled(true);

        ClickableItemStack container_open = new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack("permissions-menu.children.container-open"), builder.build())
                .setEvent(event-> {
                    InteractPlayerPermission(target,claim,ClaimPermission.CONTAINER_OPEN);
                    player.openInventory(getPermissionMenu(player, target));
                })
                .setCancelled(true);

        ClickableItemStack interact_entity = new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack("permissions-menu.children.interact-entity"), builder.build())
                .setEvent(event-> {
                    InteractPlayerPermission(target,claim,ClaimPermission.INTERACT_ENTITY);
                    player.openInventory(getPermissionMenu(player, target));
                })
                .setCancelled(true);

        ClickableItemStack attack_animal = new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack("permissions-menu.children.attack-animal"), builder.build())
                .setEvent(event->{
                    InteractPlayerPermission(target,claim,ClaimPermission.ATTACK_ANIMAL);
                    player.openInventory(getPermissionMenu(player, target));
                })
                .setCancelled(true);

        ClickableItemStack attack_monster = new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack("permissions-menu.children.attack-monster"), builder.build())
                .setEvent(event-> {
                    InteractPlayerPermission(target,claim,ClaimPermission.ATTACK_MONSTER);
                    player.openInventory(getPermissionMenu(player, target));
                })
                .setCancelled(true);
        ClickableItemStack break_container = new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack("permissions-menu.children.break-container"), builder.build())
                    .setEvent(event -> {
                        InteractPlayerPermission(target,claim,ClaimPermission.BREAK_CONTAINER);
                    player.openInventory(getPermissionMenu(player, target));
                    })
                    .setCancelled(true);
        ClickableItemStack use_door = new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack("permissions-menu.children.use-door"), builder.build())
                .setEvent(event -> {
                    InteractPlayerPermission(target,claim,ClaimPermission.USE_DOOR);
                    player.openInventory(getPermissionMenu(player, target));
                }).setCancelled(true);
        ClickableItemStack use_portal = new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack("permissions-menu.children.use-portal"), builder.build())
                .setEvent(event -> {
                    InteractPlayerPermission(target,claim,ClaimPermission.USE_PORTAL);
                    player.openInventory(getPermissionMenu(player, target));
                }).setCancelled(true);
        ClickableItemStack use_potion = new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack("permissions-menu.children.use-potion"), builder.build())
                .setEvent(event -> {
                    InteractPlayerPermission(target,claim,ClaimPermission.USE_POTION);
                    player.openInventory(getPermissionMenu(player, target));
                }).setCancelled(true);
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

        builder.setItem(config.getInt("permissions-menu.children.block-break.slot"), setupFlagItem(block_break.getItemStack()));
        builder.setItem(config.getInt("permissions-menu.children.block-place.slot"), setupFlagItem(block_place.getItemStack()));
        builder.setItem(config.getInt("permissions-menu.children.pickup-item.slot"), setupFlagItem(pickup_item.getItemStack()));
        builder.setItem(config.getInt("permissions-menu.children.drop-item.slot"), setupFlagItem(drop_item.getItemStack()));
        builder.setItem(config.getInt("permissions-menu.children.container-open.slot"), setupFlagItem(container_open.getItemStack()));
        builder.setItem(config.getInt("permissions-menu.children.interact-entity.slot"), setupFlagItem(interact_entity.getItemStack()));
        builder.setItem(config.getInt("permissions-menu.children.attack-animal.slot"), setupFlagItem(attack_animal.getItemStack()));
        builder.setItem(config.getInt("permissions-menu.children.attack-monster.slot"), setupFlagItem(attack_monster.getItemStack()));
        builder.setItem(config.getInt("permissions-menu.children.break-container.slot"), setupFlagItem(break_container.getItemStack()));
        builder.setItem(config.getInt("permissions-menu.children.use-door.slot"), setupFlagItem(use_door.getItemStack()));
        builder.setItem(config.getInt("permissions-menu.children.use-portal.slot"), setupFlagItem(use_portal.getItemStack()));
        builder.setItem(config.getInt("permissions-menu.children.use-potion.slot"), setupFlagItem(use_potion.getItemStack()));

        return builder.build();
    }

    private static void InteractPlayerPermission(UUID target, Claim claim, ClaimPermission permission){
        if (claim.checkPermission(target, permission)){
            claim.removePermission(target, permission);
        } else {
            claim.addPermission(target, permission);
        }
    }

    private static void setupUpgradeInventory(Player player, Inventory inventory, Chunk centerChunk) {
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
                inventory.setItem(i, getAreas(inventory, player, centerChunk, chunks.get(i)));
            }
        }
    }

    private static ItemStack getAreas(Inventory inventory, Player player, Chunk currentChunk, Chunk chunk) {
        Claim claim = ClaimManager.getClaims().stream()
                .filter(c -> c.getChunk().equals(chunk))
                .findFirst()
                .orElse(null);

        String key = getKeyForChunk(player, currentChunk, chunk, claim);

        ClickableItemStack itemStack = new ClickableItemStack(
                RClaim.getInstance(),
                RClaim.getInstance().getMenusFile().getItemStack(key),
                inventory
        )
                .setEvent(event -> handleItemClick(event, player, chunk))
                .setCancelled(true);

        updateItemMeta(itemStack, chunk);

        return itemStack.getItemStack();
    }

    private static String getKeyForChunk(Player player, Chunk currentChunk, Chunk chunk, Claim claim) {
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

    private static void handleItemClick(InventoryClickEvent event, Player player, Chunk chunk) {
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
            player.openInventory(getResizeInventory(player));
        } else {
            player.sendMessage(RClaim.getInstance().getMessage("IS_NOT_SUITABLE"));
        }
    }

    private static void updateItemMeta(ClickableItemStack itemStack, Chunk chunk) {
        ItemMeta meta = itemStack.getItemStack().getItemMeta();
        List<String> updatedLore = meta.getLore().stream()
                .map(line -> line.replace("<cost>", RClaim.getInstance().getConfig().getString("claim-settings.claim-cost"))
                        .replace("<x>", String.valueOf(chunk.getX() * 16))
                        .replace("<z>", String.valueOf(chunk.getZ() * 16)))
                .collect(Collectors.toList());
        meta.setLore(updatedLore);
        itemStack.getItemStack().setItemMeta(meta);
    }

    public static Inventory verifyMenu(Player player, VerifyAction action, Object varible){
        
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
        OfflinePlayer player = Bukkit.getOfflinePlayer(name);
        return player.hasPlayedBefore();
    }

    private static ItemStack setupFlagItem(ItemStack itemStack){
        ItemMeta meta = itemStack.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

}
