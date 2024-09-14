package net.weesli.rClaim.ui.inventories;

import net.weesli.rClaim.RClaim;
import net.weesli.rClaim.enums.ExplodeCause;
import net.weesli.rClaim.enums.VerifyAction;
import net.weesli.rClaim.ui.ClaimInventory;
import net.weesli.rClaim.modal.Claim;
import net.weesli.rClaim.utils.ClaimManager;
import net.weesli.rozsLib.color.ColorBuilder;
import net.weesli.rozsLib.inventory.ClickableItemStack;
import net.weesli.rozsLib.inventory.InventoryBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class VerifyMenu implements ClaimInventory {

    private VerifyAction action;
    private String varible;

    public void setup(VerifyAction action, String varible) {
        this.action = action;
        this.varible = varible;
    }

    @Override
    public void openInventory(Player player, Claim claim, FileConfiguration config) {
        InventoryBuilder builder = new InventoryBuilder(RClaim.getInstance(), ColorBuilder.convertColors(config.getString("verify-menu.title")), config.getInt("verify-menu.size"));
        builder.setItem(config.getInt("verify-menu.children.confirm.slot"), new ClickableItemStack(RClaim.getInstance(), RClaim.getInstance().getMenusFile().getItemStack("verify-menu.children.confirm"), builder.build())
                .setEvent(event->{
                    switch (action){
                        case UNTRUST_PLAYER:
                            player.performCommand("claim untrust " + Bukkit.getOfflinePlayer(UUID.fromString(String.valueOf(varible))).getName());
                            RClaim.getInstance().getUiManager().openInventory(player,claim,RClaim.getInstance().getUiManager().getUsersMenu());
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
                    RClaim.getInstance().getUiManager().openInventory(player, claim, RClaim.getInstance().getUiManager().getMainMenu());
                })
                .setCancelled(true));
        player.openInventory(builder.build());
    }
}
