package net.weesli.rclaim.hook.manager;

import lombok.Getter;
import lombok.Setter;
import net.weesli.rclaim.api.hook.manager.EconomyManager;
import net.weesli.rclaim.config.ConfigLoader;
import net.weesli.rclaim.api.enums.EconomyType;
import net.weesli.rclaim.api.hook.ClaimEconomy;
import net.weesli.rclaim.hook.economy.PlayerPointsEconomy;
import net.weesli.rclaim.hook.economy.VaultEconomy;
import net.weesli.rozslib.color.ColorBuilder;
import org.bukkit.Bukkit;
@Getter@Setter
public class EconomyManagerImpl implements EconomyManager {

    private ClaimEconomy economyIntegration;

    public EconomyManagerImpl() {
        EconomyType type = EconomyType.valueOf(ConfigLoader.getConfig().getEconomyType());
        switch (type){
            case VAULT -> economyIntegration = new VaultEconomy();
            case PLAYER_POINTS -> economyIntegration = new PlayerPointsEconomy();
        }
        if (economyIntegration != null && economyIntegration.isActive()){
            Bukkit.getConsoleSender().sendMessage("[RClaim] register economy type is " + economyIntegration.getEconomyType().name());
        }else {
            Bukkit.getConsoleSender().sendMessage(ColorBuilder.convertColors("[RClaim] &cEconomy is not loaded"));
        }
    }
}
