package net.weesli.rClaim.ui.inventories;

import net.weesli.rClaim.RClaim;
import net.weesli.rClaim.enums.Effect;
import net.weesli.rClaim.modal.Claim;
import net.weesli.rClaim.modal.ClaimEffect;
import net.weesli.rClaim.ui.ClaimInventory;
import net.weesli.rClaim.utils.ClaimManager;
import net.weesli.rozsLib.color.ColorBuilder;
import net.weesli.rozsLib.inventory.lasest.InventoryBuilder;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ClaimEffectMenu implements ClaimInventory {

    @Override
    public void openInventory(Player player, Claim claim, FileConfiguration config) {
        InventoryBuilder builder = new InventoryBuilder().title(config.getString("effect-menu.title")).size(config.getInt("effect-menu.size"));
        builder.setItem(config.getInt("effect-menu.children.speed.slot"), getItem("effect-menu.children.speed", config, Effect.SPEED, claim), event -> {
            if (event.isShiftClick()){
                ShiftInteractEffect(player,claim,Effect.SPEED);
            }else {
                InteractEffect(player,claim,Effect.SPEED);
            }
        });
        builder.setItem(config.getInt("effect-menu.children.jump.slot"), getItem("effect-menu.children.jump", config, Effect.JUMP, claim), event -> {
            if (event.isShiftClick()){
                ShiftInteractEffect(player,claim,Effect.JUMP);
            }else {
                InteractEffect(player,claim,Effect.JUMP);
            }
        });
        builder.setItem(config.getInt("effect-menu.children.haste.slot"), getItem("effect-menu.children.haste", config, Effect.HASTE, claim), event -> {
            if (event.isShiftClick()){
                ShiftInteractEffect(player,claim,Effect.HASTE);
            } else {
                InteractEffect(player,claim,Effect.HASTE);
            }
        });
        builder.openInventory(player);
    }

    private ItemStack getItem(String path, FileConfiguration config, Effect effect, Claim claim) {
        ItemStack itemStack = getItemStack(path,config);
        ItemMeta meta = itemStack.getItemMeta();
        ClaimEffect model = claim.getEffect(effect);
        List<String> lore = meta.getLore().stream().map(line -> line
                .replaceAll("%status%", ClaimManager.getStatus(model != null && model.isEnabled() && claim.hasEffect(effect)))
                .replaceAll("%level%", String.valueOf(model == null ? 0 : model.getLevel()))
                .replaceAll("%cost%", String.valueOf(model == null ? ClaimManager.getCost(effect,claim) : model.isMaxLevel() ? ColorBuilder.convertColors(RClaim.getInstance().getConfig().getString("options.effects.max-level-message")) : ClaimManager.getCost(effect,claim)))
        ).toList();
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    private void InteractEffect(Player player, Claim claim, Effect effect){
        boolean isValid = claim.hasEffect(effect);
        int cost = ClaimManager.getCost(effect,claim);
        if (!isValid){
            if (RClaim.getInstance().getEconomy().isActive()){
                if (RClaim.getInstance().getEconomy().hasEnough(player, cost)){
                    RClaim.getInstance().getEconomy().withdraw(player, cost);
                }else {
                    player.sendMessage(RClaim.getInstance().getMessage("HASNT_MONEY"));
                    return;
                }
                claim.addEffect(effect);
                player.sendMessage(RClaim.getInstance().getMessage("EFFECT_BOUGHT"));
            }
        }else {
            int currentLevel = claim.getEffect(effect).getLevel();
            if (currentLevel == claim.getEffect(effect).getMaxLevel()){
                player.sendMessage(RClaim.getInstance().getMessage("EFFECT_MAX_LEVEL"));
                return;
            }
            if (RClaim.getInstance().getEconomy().isActive()){
                if (RClaim.getInstance().getEconomy().hasEnough(player, cost)){
                    RClaim.getInstance().getEconomy().withdraw(player, cost);
                }else {
                    player.sendMessage(RClaim.getInstance().getMessage("HASNT_MONEY"));
                    return;
                }
                claim.getEffect(effect).setLevel(currentLevel + 1);
                player.sendMessage(RClaim.getInstance().getMessage("EFFECT_LEVEL_UP").replaceAll("%level%", String.valueOf(currentLevel+1)));
            }
        }
        RClaim.getInstance().getUiManager().openInventory(player,claim,RClaim.getInstance().getUiManager().getEffectMenu());
    }

    private void ShiftInteractEffect(Player player, Claim claim, Effect effect){
        ClaimEffect modal = claim.getEffect(effect);
        if (modal == null){
            return;
        }
        boolean enabled = claim.getEffect(effect).isEnabled();
        claim.getEffect(effect).setEnabled(!enabled);
        RClaim.getInstance().getUiManager().openInventory(player,claim,RClaim.getInstance().getUiManager().getEffectMenu());
    }
}
