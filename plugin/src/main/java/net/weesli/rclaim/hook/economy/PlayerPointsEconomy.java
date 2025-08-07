package net.weesli.rclaim.hook.economy;

import net.weesli.rclaim.api.enums.EconomyType;
import net.weesli.rclaim.api.hook.ClaimEconomy;
import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerPointsEconomy implements ClaimEconomy {

    @Override
    public boolean hasEnough(Player player, double amount) {
        return PlayerPoints.getInstance().getAPI().look(player.getUniqueId()) >= amount;
    }

    @Override
    public void withdraw(Player player, double amount) {
        PlayerPoints.getInstance().getAPI().take(player.getUniqueId(), (int) amount);
    }

    @Override
    public void deposit(Player player, double amount) {
        PlayerPoints.getInstance().getAPI().give(player.getUniqueId(), (int) amount);
    }

    @Override
    public EconomyType getEconomyType() {
        return EconomyType.PLAYER_POINTS;
    }

    @Override
    public boolean isActive() {
        if (Bukkit.getPluginManager().isPluginEnabled("PlayerPoints")){
            return true;
        }
        return false;
    }
}
