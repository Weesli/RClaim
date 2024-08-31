package net.weesli.rClaim.EconomyManager;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import static org.bukkit.Bukkit.getServer;

public class VaultEconomy extends EconomyImpl{

    private boolean active = false;
    private Economy econ;

    public VaultEconomy(){
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            active = false;
            return;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            active = false;
            return;
        }
        econ = rsp.getProvider();
        active = true;
    }

    @Override
    public boolean hasEnough(Player player, double amount) {
        return getEcon().getBalance(player) >= amount;
    }

    @Override
    public void withdraw(Player player, double amount) {
        getEcon().withdrawPlayer(player, amount);
    }

    @Override
    public void deposit(Player player, double amount) {
        getEcon().depositPlayer(player, amount);
    }

    @Override
    public EconomyType getEconomyType() {
        return EconomyType.VAULT;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public Economy getEcon() {
        return econ;
    }
}
