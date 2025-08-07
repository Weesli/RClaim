package net.weesli.rclaim.api.hook;

import net.weesli.rclaim.api.enums.EconomyType;
import org.bukkit.entity.Player;

public interface ClaimEconomy {
    boolean hasEnough(Player player, double amount);
    void withdraw(Player player, double amount);
    void deposit(Player player, double amount);
    EconomyType getEconomyType();
    boolean isActive();
}
