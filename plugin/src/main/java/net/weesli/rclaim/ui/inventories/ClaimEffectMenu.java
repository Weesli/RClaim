package net.weesli.rclaim.ui.inventories;

import me.clip.placeholderapi.PlaceholderAPI;
import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.model.Claim;
import net.weesli.rclaim.api.model.ClaimEffect;
import net.weesli.rclaim.config.ConfigLoader;
import net.weesli.rclaim.api.enums.Effect;
import net.weesli.rclaim.config.lang.MenuConfig;
import net.weesli.rclaim.ui.ClaimInventory;
import net.weesli.rclaim.util.BaseUtil;
import net.weesli.rozslib.color.ColorBuilder;
import net.weesli.rozslib.inventory.ClickableItemStack;
import net.weesli.rozslib.inventory.types.SimpleInventory;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import static net.weesli.rclaim.config.lang.LangConfig.sendMessageToPlayer;
import static net.weesli.rclaim.util.ChatUtil.createTagResolver;

// pageable
public class ClaimEffectMenu extends ClaimInventory {

    private static final MenuConfig.Menu menu = ConfigLoader.getMenuConfig().getEffectMenu();

    @Override
    public void openInventory(Player player, Claim claim) {
        SimpleInventory inventory = new SimpleInventory(PlaceholderAPI.setPlaceholders(player,menu.getTitle()),menu.getSize());
        inventory.setLayout(menu.getFillerSlots()
                .stream()
                .mapToInt(Integer::intValue)
                .toArray()).fill(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), menu.isAutoFill());
        for (Map.Entry<String, MenuConfig.MenuItem> item : menu.getItems().entrySet()){
            switch (item.getKey()){
                case "speed" -> inventory.setItem(new ClickableItemStack(getItem(item.getValue(), Effect.SPEED, claim,player),item.getValue().getIndex()), event -> {
                    if (event.isShiftClick()){
                        ShiftInteractEffect(player, claim,Effect.SPEED);
                    }else {
                        InteractEffect(player, claim,Effect.SPEED);
                    }
                });
                case "jump" -> inventory.setItem(new ClickableItemStack(getItem(item.getValue(), Effect.JUMP, claim,player),item.getValue().getIndex()), event -> {
                    if (event.isShiftClick()){
                        ShiftInteractEffect(player, claim,Effect.JUMP);
                    } else {
                        InteractEffect(player, claim,Effect.JUMP);
                    }
                });
                case "haste" -> inventory.setItem(new ClickableItemStack(getItem(item.getValue(), Effect.HASTE, claim,player),item.getValue().getIndex()), event -> {
                    if (event.isShiftClick()){
                        ShiftInteractEffect(player, claim,Effect.HASTE);
                    } else {
                        InteractEffect(player, claim,Effect.HASTE);
                    }
                });
            }
        }
        inventory.openInventory(player);
    }

    private ItemStack getItem(MenuConfig.MenuItem item, Effect effect, Claim claim, Player player){
        ItemStack itemStack = getItemStack(item,player);
        ItemMeta meta = itemStack.getItemMeta();
        ClaimEffect model = claim.getEffect(effect);
        List<String> lore = meta.hasLore() ? meta.getLore().stream().map(line -> line
                .replaceAll("%status%", BaseUtil.getStatus(model != null && model.isEnabled() && claim.hasEffect(effect)))
                .replaceAll("%level%", String.valueOf(model == null ? 0 : model.getLevel()))
                .replaceAll("%cost%", String.valueOf(model == null ? BaseUtil.getCost(effect, claim) : model.isMaxLevel() ? ColorBuilder.convertColors(ConfigLoader.getConfig().getEffects().getMaxLevelMessage()) : BaseUtil.getCost(effect, claim)))
        ).toList() : null;
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    private void InteractEffect(Player player, Claim claim, Effect effect){
        boolean isValid = claim.hasEffect(effect);
        int cost = BaseUtil.getCost(effect, claim);
        if (!isValid){
            if (RClaim.getInstance().getEconomyManager().getEconomyIntegration().isActive()){
                if (RClaim.getInstance().getEconomyManager().getEconomyIntegration().hasEnough(player, cost)){
                    RClaim.getInstance().getEconomyManager().getEconomyIntegration().withdraw(player, cost);
                }else {
                    sendMessageToPlayer("HASNT_MONEY", player);
                    return;
                }
                claim.addEffect(effect);
                sendMessageToPlayer("EFFECT_BOUGHT", player);
            }
        }else {
            int currentLevel = claim.getEffect(effect).getLevel();
            if (currentLevel == claim.getEffect(effect).getMaxLevel()){
                sendMessageToPlayer("EFFECT_MAX_LEVEL", player);
                return;
            }
            if (RClaim.getInstance().getEconomyManager().getEconomyIntegration().isActive()){
                if (RClaim.getInstance().getEconomyManager().getEconomyIntegration().hasEnough(player, cost)){
                    RClaim.getInstance().getEconomyManager().getEconomyIntegration().withdraw(player, cost);
                }else {
                    sendMessageToPlayer("HASNT_MONEY", player);
                    return;
                }
                claim.getEffect(effect).setLevel(currentLevel + 1);
                sendMessageToPlayer("EFFECT_LEVEL_UP", player, createTagResolver("%level%", String.valueOf(currentLevel+1)));
            }
        }
        RClaim.getInstance().getUiManager().openInventory(player, claim, ClaimEffectMenu.class);
    }

    private void ShiftInteractEffect(Player player, Claim claim, Effect effect){
        ClaimEffect modal = claim.getEffect(effect);
        if (modal == null){
            return;
        }
        boolean enabled = claim.getEffect(effect).isEnabled();
        claim.getEffect(effect).setEnabled(!enabled);
        RClaim.getInstance().getUiManager().openInventory(player, claim, ClaimEffectMenu.class);
    }
}
