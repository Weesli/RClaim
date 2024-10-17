package net.weesli.rClaim.ui.inventories.tag;

import de.rapha149.signgui.SignGUI;
import de.rapha149.signgui.SignGUIAction;
import net.weesli.rClaim.RClaim;
import net.weesli.rClaim.modal.Claim;
import net.weesli.rClaim.modal.ClaimTag;
import net.weesli.rClaim.utils.ClaimManager;
import net.weesli.rClaim.utils.PlayerUtils;
import net.weesli.rClaim.utils.TagManager;
import net.weesli.rozsLib.color.ColorBuilder;
import net.weesli.rozsLib.inventory.lasest.ClickableItemStack;
import net.weesli.rozsLib.inventory.lasest.InventoryBuilder;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ClaimTagUsersMenu implements TagInventory{

    private final int ITEMS_PER_PAGE = 7;
    private static final int INVENTORY_SIZE = 27;
    private static final int[] block_slots = {10,11,12,13,14,15,16};
    private static final int[] glass_slots = {0,1,2,3,4,5,6,7,8,9,17,18,19,20,24,25,26};

    @Override
    public void openInventory(Player player, ClaimTag tag, FileConfiguration config) {
        final int totalPages = (int) Math.ceil((double) tag.getUsers().size() / ITEMS_PER_PAGE);
        final int[] currentPage = {0};
        openPage(player, tag, config, tag.getUsers(), currentPage[0], totalPages);
    }

    private void openPage(Player player, ClaimTag tag, FileConfiguration config, List<UUID> users, int page, int totalPages) {
        InventoryBuilder builder = new InventoryBuilder().title(config.getString("tag-users-menu.title")).size(INVENTORY_SIZE).size(27);

        int start = page * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, users.size());

        for (int i = start; i < end; i++) {
            int slotIndex = i - start;
            if (slotIndex < block_slots.length) {
                UUID uuid = users.get(i);
                ItemStack itemStack = getItemStack("tag-users-menu.children.item-settings", config);
                ItemMeta meta = itemStack.getItemMeta();
                meta.setDisplayName(meta.getDisplayName().replaceAll("<name>", Bukkit.getOfflinePlayer(uuid).getName()));
                itemStack.setItemMeta(meta);
                ClickableItemStack clickableItemStack  = new ClickableItemStack(itemStack, block_slots[slotIndex]);
                clickableItemStack.setSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
                builder.setItem(clickableItemStack,event -> {
                    tag.removeUser(uuid);
                    RClaim.getInstance().getUiManager().openTagInventory(player,tag,"users");
                });
            }

        }

        for (int i : glass_slots){
            builder.setItem(i, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        }

        if (page > 0) {
            builder.setItem(21, createArrowItem(ColorBuilder.convertColors(config.getString("block-menu.previous-item-name")), Material.ARROW), event -> {
                openPage(player, tag, config, users, page - 1, totalPages);
            });
        }

        if (page < totalPages - 1) {
            builder.setItem(23, createArrowItem(ColorBuilder.convertColors(config.getString("block-menu.next-item-name")), Material.ARROW), event -> {
                openPage(player, tag, config, users, page + 1, totalPages);
            });
        }

        builder.setItem(config.getInt("tag-users-menu.children.add-user.slot"), getItemStack("tag-users-menu.children.add-user", config),
                event -> callSign(player,tag));

        builder.openInventory(player);
    }


    private ItemStack createArrowItem(String name, Material material) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }


    private void callSign(Player player, ClaimTag tag){
        SignGUI gui = SignGUI.builder()
                .setLines("", "----------","^^^^^^^^", "----------")
                .setType(Material.DARK_OAK_SIGN)

                .setColor(DyeColor.WHITE)

                .setHandler((p, result) -> {
                    String name = result.getLine(0);
                    if (name.isEmpty()) {
                        return Collections.emptyList();
                    }else {
                        OfflinePlayer user = PlayerUtils.getPlayer(name);
                        if (user == null){
                            player.sendMessage(RClaim.getInstance().getMessage("TARGET_NOT_FOUND"));
                            return Collections.singletonList(SignGUIAction.runSync(RClaim.getInstance(), ()-> {
                                RClaim.getInstance().getUiManager().openTagInventory(player,tag, "users");
                            }));
                        }
                        if (!ClaimManager.getClaim(tag.getClaimId()).get().isMember(user.getUniqueId())){
                            player.sendMessage(RClaim.getInstance().getMessage("NOT_TRUSTED_PLAYER"));
                            return Collections.emptyList();
                        }
                        if (tag.hasUser(user.getUniqueId())){
                            player.sendMessage(RClaim.getInstance().getMessage("ALREADY_ADDED_USER"));
                            return Collections.emptyList();
                        }
                        ClaimTag foundedTag = TagManager.isPlayerInTag(user.getUniqueId(), tag.getClaimId());
                        if (foundedTag != null){
                            foundedTag.removeUser(user.getUniqueId());
                        }
                        tag.addUser(Bukkit.getOfflinePlayer(name).getUniqueId());
                        return Collections.singletonList(SignGUIAction.runSync(RClaim.getInstance(), ()-> {
                            RClaim.getInstance().getUiManager().openTagInventory(player,tag, "users");
                        }));
                    }
                })
                .build();
        gui.open(player);
    }

}
