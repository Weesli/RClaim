package net.weesli.rClaim.ui;

import lombok.Getter;
import net.weesli.rClaim.RClaim;
import net.weesli.rClaim.ui.inventories.*;
import net.weesli.rClaim.modal.Claim;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
@Getter
public class UIManager {

    public FileConfiguration config = RClaim.getInstance().getMenusFile().load();


    private final ClaimMainMenu mainMenu;
    private final ClaimPermissionMenu permissionMenu;
    private final ClaimResizeInventory resizeInventory;
    private final ClaimSettingsMenu settingsMenu;
    private final ClaimsMenu claimsMenu;
    private final ClaimUpgradeMenu upgradeMenu;
    private final ClaimUsersMenu usersMenu;
    private final VerifyMenu verifyMenu;

    public UIManager() {
        mainMenu = new ClaimMainMenu();
        permissionMenu = new ClaimPermissionMenu();
        resizeInventory = new ClaimResizeInventory();
        settingsMenu = new ClaimSettingsMenu();
        claimsMenu = new ClaimsMenu();
        upgradeMenu = new ClaimUpgradeMenu();
        usersMenu = new ClaimUsersMenu();
        verifyMenu = new VerifyMenu();
    }

    public void openInventory(Player player, Claim claim, ClaimInventory claimInventory){
        claimInventory.openInventory(player,claim,config);
    }

}
