package net.weesli.rclaim.ui;

import lombok.Getter;
import net.weesli.rclaim.api.model.Claim;
import net.weesli.rclaim.api.model.ClaimTag;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

@Getter
public class UIManager {

    public void openInventory(Player player, Claim claim, Class<? extends ClaimInventory> clazz){
        try {
            ClaimInventory claimClass = clazz.getDeclaredConstructor().newInstance();

            claimClass.openInventory(player, claim);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public void openTagInventory(Player player, ClaimTag tag, Class<? extends TagInventory> clazz){
        try {
            TagInventory claimClass = clazz.getDeclaredConstructor().newInstance();
            claimClass.openInventory(player, tag);
        } catch (InstantiationException
                 | IllegalAccessException
                 | InvocationTargetException
                 | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
