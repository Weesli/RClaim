package net.weesli.rclaim.ui.inventories;

import me.clip.placeholderapi.PlaceholderAPI;
import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.model.Claim;
import net.weesli.rclaim.config.ConfigLoader;
import net.weesli.rclaim.config.lang.MenuConfig;
import net.weesli.rclaim.ui.ClaimInventory;
import net.weesli.rclaim.util.BaseUtil;
import net.weesli.rozslib.inventory.types.PageableInventory;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import static net.weesli.rclaim.config.lang.LangConfig.sendMessageToPlayer;
// pageable
public class ClaimBlockMenu extends ClaimInventory {

    private final MenuConfig.PageableMenu menu = ConfigLoader.getMenuConfig().getBlockMenu();

    @Override
    public void openInventory(Player player, Claim claim) {
        PageableInventory inventory = new PageableInventory(PlaceholderAPI.setPlaceholders(player,menu.getTitle()), menu.getSize(),
                menu.getPreviousItem().asClickableItemStack(player),
                menu.getNextItem().asClickableItemStack(player));
        inventory.setLayout(menu.getFillerSlots()
                .stream()
                .mapToInt(Integer::intValue)
                .toArray()).fill(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), menu.isAutoFill());
        List<String> blockTypes = ConfigLoader.getConfig().getBlockTypes();
        for (String blockType : blockTypes) {
            Material material = Material.getMaterial(blockType);
            inventory.addItem(new ItemStack(material != null ? material : Material.BEDROCK) , event -> {
                boolean success = BaseUtil.changeBlockMaterial(player, claim, Material.getMaterial(blockType));
                if (!success){
                    sendMessageToPlayer("HASN'T_PERMISSION_TO_CHANGE_CLAIM_BLOCK", player);
                    //Bukkit.getScheduler().runTask(RClaim.getInstance(), () -> player.closeInventory());
                    RClaim.getInstance().getFoliaLib().getScheduler().runNextTick((wrapper) -> player.closeInventory());
                }
                //Bukkit.getScheduler().runTask(RClaim.getInstance(), () -> player.closeInventory());
                RClaim.getInstance().getFoliaLib().getScheduler().runNextTick((wrapper) -> player.closeInventory());
            });
        }
        inventory.openDefaultInventory(player);
    }

}
