package net.weesli.rclaim.ui.inventories;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.model.Claim;
import net.weesli.rclaim.config.ConfigLoader;
import net.weesli.rclaim.api.enums.VerifyAction;
import net.weesli.rclaim.config.lang.MenuConfig;
import net.weesli.rclaim.input.TextInputManager;
import net.weesli.rclaim.ui.ClaimInventory;
import net.weesli.rclaim.util.PlayerUtil;
import net.weesli.rozslib.inventory.ClickableItemStack;
import net.weesli.rozslib.inventory.types.PageableInventory;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.util.UUID;
import static net.weesli.rclaim.config.lang.LangConfig.sendMessageToPlayer;
public class ClaimUsersMenu extends ClaimInventory {

    private final MenuConfig.PageableMenu menu = ConfigLoader.getMenuConfig().getMembersMenu();
    @Override
    public void openInventory(Player player, Claim claim){
        PageableInventory inventory = new PageableInventory(PlaceholderAPI.setPlaceholders(player,menu.getTitle()), menu.getSize(),
                menu.getPreviousItem().asClickableItemStack(player),
                menu.getNextItem().asClickableItemStack(player));
        inventory.setLayout(menu.getFillerSlots()
                .stream()
                .mapToInt(Integer::intValue)
                .toArray()).fill(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), menu.isAutoFill());
        for (UUID member : claim.getMembers()){
            ItemStack itemStack = getItemStack(menu.getItems().get("item-settings"),player, Placeholder.parsed("name", PlayerUtil.getPlayer(member).getName()));
            inventory.addItem(itemStack,event-> {
                if (event.isShiftClick()){
                    VerifyMenu verifyMenu = new VerifyMenu();
                    verifyMenu.setup(VerifyAction.UNTRUST_PLAYER,member.toString());
                    verifyMenu.openInventory(player, claim);
                    return;
                }
                ClaimPermissionMenu permissionMenu = new ClaimPermissionMenu();
                permissionMenu.setup(member);
                permissionMenu.openInventory(player, claim);
            });
        }
        inventory.addStaticItem(new ClickableItemStack(getItemStack(menu.getItems().get("add-member"),player),menu.getItems().get("add-member").getIndex()),event ->{
            Bukkit.getScheduler().runTask(RClaim.getInstance(), () -> player.closeInventory());
            sendMessageToPlayer("ENTER_A_PLAYER_NAME", player);
            RClaim.getInstance().getTextInputManager().runAction(player,
                    TextInputManager.TextInputAction.ADD_PLAYER_TO_CLAIM, claim);
        });
        inventory.openDefaultInventory(player);
    }
}
