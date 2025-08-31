package net.weesli.rclaim.ui.inventories;

import me.clip.placeholderapi.PlaceholderAPI;
import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.model.Claim;
import net.weesli.rclaim.config.ConfigLoader;
import net.weesli.rclaim.config.adapter.model.Menu;
import net.weesli.rclaim.ui.ClaimInventory;
import net.weesli.rclaim.util.BaseUtil;
import net.weesli.rozslib.inventory.ClickableItemStack;
import net.weesli.rozslib.inventory.types.PageableInventory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import static net.weesli.rclaim.config.lang.LangConfig.sendMessageToPlayer;
// pageable
public class ClaimBlockMenu extends ClaimInventory {

    private final Menu menu = ConfigLoader.getMenuConfig().getBlockMenu();

    @Override
    public void openInventory(Player player, Claim claim) {
        PageableInventory builder = new PageableInventory(PlaceholderAPI.setPlaceholders(player,menu.getTitle()), 27,
                new ClickableItemStack(getItemStack(ConfigLoader.getConfig().getPublicMenu().getPreviousItem()),21),
                new ClickableItemStack(getItemStack(ConfigLoader.getConfig().getPublicMenu().getNextItem()), 23),
                23,21);
        builder.setLayout("""
                *********
                *       *
                *** * ***
                """).fill(new ItemStack(Material.GRAY_STAINED_GLASS_PANE),false);
        List<String> blockTypes = ConfigLoader.getConfig().getBlockTypes();
        for (String blockType : blockTypes) {
            ClickableItemStack itemStack = new ClickableItemStack(new ItemStack(Material.getMaterial(blockType)), 0);
            itemStack.setSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
            builder.setItem(itemStack , event -> {
                boolean success = BaseUtil.changeBlockMaterial(player, claim, Material.getMaterial(blockType));
                if (!success){
                    sendMessageToPlayer("HASN'T_PERMISSION_TO_CHANGE_CLAIM_BLOCK", player);
                    Bukkit.getScheduler().runTask(RClaim.getInstance(), () -> player.closeInventory());
                }
                Bukkit.getScheduler().runTask(RClaim.getInstance(), () -> player.closeInventory());
            });
        }
        builder.openDefaultInventory(player);
    }

}
