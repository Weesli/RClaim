package net.weesli.rclaim.ui.inventories;

import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.model.Claim;
import net.weesli.rclaim.config.ConfigLoader;
import net.weesli.rclaim.config.adapter.model.Menu;
import net.weesli.rclaim.config.adapter.model.MenuItem;
import net.weesli.rclaim.api.enums.ExplodeCause;
import net.weesli.rclaim.api.enums.VerifyAction;
import net.weesli.rclaim.model.ClaimImpl;
import net.weesli.rclaim.ui.ClaimInventory;
import net.weesli.rozslib.inventory.ClickableItemStack;
import net.weesli.rozslib.inventory.types.SimpleInventory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class VerifyMenu extends ClaimInventory {

    private VerifyAction action;
    private String varible;

    public void setup(VerifyAction action, String varible) {
        this.action = action;
        this.varible = varible;
    }

    private final Menu menu = ConfigLoader.getMenuConfig().getVerifyMenu();

    @Override
    public void openInventory(Player player, Claim claim) {
        SimpleInventory inventory = new SimpleInventory(menu.getTitle(),menu.getSize());
        inventory.setLayout("").fill(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), true);
        MenuItem confirm = menu.getItems().get("confirm");
        MenuItem deny = menu.getItems().get("deny");

        inventory.setItem(new ClickableItemStack(getItemStack(confirm),confirm.getIndex()), event->{
            switch (action){
                case UNTRUST_PLAYER:
                    player.performCommand("claim untrust " + Bukkit.getOfflinePlayer(UUID.fromString(String.valueOf(varible))).getName());
                    RClaim.getInstance().getUiManager().openInventory(player, claim, ClaimUsersMenu.class);
                    break;
                case UNCLAIM:
                    RClaim.getInstance().getClaimManager().explodeClaim(String.valueOf(varible), ExplodeCause.UNCLAIM);
                    player.sendMessage(RClaim.getInstance().getMessage("UNCLAIMED_CLAIM"));
                    player.closeInventory();
                    break;
            }
        });

        inventory.setItem(new ClickableItemStack(getItemStack(deny), deny.getIndex()), event -> {
            RClaim.getInstance().getUiManager().openInventory(player, claim, ClaimMainMenu.class);
        });

        inventory.openInventory(player);
    }
}
