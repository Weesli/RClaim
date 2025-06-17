package net.weesli.rclaim.ui;

import lombok.Getter;
import net.weesli.rclaim.api.model.Claim;
import net.weesli.rclaim.api.model.ClaimTag;
import net.weesli.rclaim.model.ClaimImpl;
import net.weesli.rclaim.model.ClaimTagImpl;
import net.weesli.rclaim.ui.inventories.*;
import net.weesli.rclaim.ui.inventories.tag.*;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@Getter
public class UIManager {

    private final Map<Class<? extends TagInventory>, TagInventory> tagMenus = new HashMap<>();

    private final Map<Class<? extends ClaimInventory>, ClaimInventory> claimMenus = new HashMap<>();

    public UIManager() {
        claimMenus.put(ClaimMainMenu.class, new ClaimMainMenu());
        claimMenus.put(ClaimPermissionMenu.class, new ClaimPermissionMenu());
        claimMenus.put(ClaimSettingsMenu.class, new ClaimSettingsMenu());
        claimMenus.put(ClaimsMenu.class, new ClaimsMenu());
        claimMenus.put(ClaimUpgradeMenu.class, new ClaimUpgradeMenu());
        claimMenus.put(ClaimUsersMenu.class, new ClaimUsersMenu());
        claimMenus.put(VerifyMenu.class, new VerifyMenu());
        claimMenus.put(ClaimEffectMenu.class, new ClaimEffectMenu());
        claimMenus.put(ClaimBlockMenu.class, new ClaimBlockMenu());
        claimMenus.put(ClaimTagMainMenu.class, new ClaimTagMainMenu());
        // tag menu setup
        tagMenus.put(ClaimTagEditMenu.class, new ClaimTagEditMenu());
        tagMenus.put(ClaimTagPermissionMenu.class, new ClaimTagPermissionMenu());
        tagMenus.put(ClaimTagUsersMenu.class, new ClaimTagUsersMenu());
    }

    public void openInventory(Player player, Claim claim, Class<? extends ClaimInventory> clazz){
        claimMenus.get(clazz).openInventory(player, claim);
    }

    public void openTagInventory(Player player, ClaimTag tag, Class<? extends TagInventory> clazz){
        tagMenus.get(clazz).openInventory(player,tag);
    }

    public void openResizeMenu(Player player, Claim claim, int page){
        ClaimResizeInventory claimResizeInventory = new ClaimResizeInventory();
        claimResizeInventory.openInventory(player, claim);
    }

}
