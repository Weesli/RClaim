package net.weesli.rClaim.EconomyManager;

import net.weesli.rClaim.RClaim;
import org.bukkit.entity.Player;

public abstract class EconomyImpl {

    public abstract boolean hasEnough(Player player, double amount);
    public abstract void withdraw(Player player, double amount);
    public abstract EconomyType getEconomyType();
    public abstract boolean isActive();

    public void register(){
        RClaim.getInstance().setEconomy(this);
    }
}
