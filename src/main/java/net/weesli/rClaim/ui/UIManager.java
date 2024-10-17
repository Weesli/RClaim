package net.weesli.rClaim.ui;

import lombok.Getter;
import net.weesli.rClaim.RClaim;
import net.weesli.rClaim.modal.ClaimTag;
import net.weesli.rClaim.ui.inventories.*;
import net.weesli.rClaim.modal.Claim;
import net.weesli.rClaim.ui.inventories.tag.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@Getter
public class UIManager {

    public FileConfiguration config = RClaim.getInstance().getMenusFile().load();

    private Map<String, TagInventory> tagInventories = new HashMap<>();

    private final ClaimMainMenu mainMenu;
    private final ClaimPermissionMenu permissionMenu;
    private final ClaimResizeInventory resizeInventory;
    private final ClaimSettingsMenu settingsMenu;
    private final ClaimsMenu claimsMenu;
    private final ClaimUpgradeMenu upgradeMenu;
    private final ClaimUsersMenu usersMenu;
    private final VerifyMenu verifyMenu;
    private final ClaimEffectMenu effectMenu;
    private final ClaimBlockMenu blockMenu;
    private final ClaimTagMainMenu tagMainMenu;

    public UIManager() {
        mainMenu = new ClaimMainMenu();
        permissionMenu = new ClaimPermissionMenu();
        resizeInventory = new ClaimResizeInventory();
        settingsMenu = new ClaimSettingsMenu();
        claimsMenu = new ClaimsMenu();
        upgradeMenu = new ClaimUpgradeMenu();
        usersMenu = new ClaimUsersMenu();
        verifyMenu = new VerifyMenu();
        effectMenu = new ClaimEffectMenu();
        blockMenu = new ClaimBlockMenu();
        tagMainMenu = new ClaimTagMainMenu();
        // tag menu setup
        tagInventories.put("edit", new ClaimTagEditMenu());
        tagInventories.put("permission", new ClaimTagPermissionMenu());
        tagInventories.put("users", new ClaimTagUsersMenu());
    }

    public void openInventory(Player player, Claim claim, ClaimInventory claimInventory){
        claimInventory.openInventory(player,claim,config);
    }


    public void openTagInventory(Player player, ClaimTag tag, String value){
        tagInventories.get(value).openInventory(player,tag,config);
    }

}
