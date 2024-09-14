package net.weesli.rClaim.hooks.economy;

import net.weesli.rClaim.RClaim;
import net.weesli.rClaim.enums.EconomyType;
import org.bukkit.entity.Player;

public interface ClaimEconomy {

    public abstract boolean hasEnough(Player player, double amount);
    public abstract void withdraw(Player player, double amount);
    public abstract void deposit(Player player, double amount);
    public abstract EconomyType getEconomyType();
    public abstract boolean isActive();

    default void register(){
        RClaim.getInstance().setEconomy(this);
    }
}
