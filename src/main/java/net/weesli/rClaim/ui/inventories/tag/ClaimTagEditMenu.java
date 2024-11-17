package net.weesli.rClaim.ui.inventories.tag;

import net.weesli.rClaim.RClaim;
import net.weesli.rClaim.modal.ClaimTag;
import net.weesli.rozsLib.inventory.lasest.ClickableItemStack;
import net.weesli.rozsLib.inventory.lasest.InventoryBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ClaimTagEditMenu implements TagInventory{

    private final int[] glass_slots = {0,1,2,3,4,5,6,7,8,9,10,11,13,15,16,17,18,19,20,21,22,23,24,25,26};

    @Override
    public void openInventory(Player player, ClaimTag tag, FileConfiguration config) {
        InventoryBuilder inv = new InventoryBuilder().title(config.getString("tag-edit-menu.title")).size(config.getInt("tag-edit-menu.size"));

        for (String key : config.getConfigurationSection("tag-edit-menu.children").getKeys(false)){
            ClickableItemStack itemStack = new ClickableItemStack(getItemStack("tag-edit-menu.children." + key, config), config.getInt("tag-edit-menu.children." + key + ".slot"));
            itemStack.setSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
            inv.setItem(itemStack, e -> {
                if (key.equalsIgnoreCase("users")){
                    RClaim.getInstance().getUiManager().openTagInventory(player,tag, "users");
                }else if (key.equalsIgnoreCase("permissions")){
                    RClaim.getInstance().getUiManager().openTagInventory(player, tag, "permission");
                }
            });
        }
        if (config.getBoolean("tag-edit-menu.glass")){
            for (int slot : glass_slots){
                inv.setItem(slot, new ItemStack(Material.GRAY_STAINED_GLASS_PANE),e -> {});
            }
        }

        inv.openInventory(player);
    }
}
