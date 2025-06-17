package net.weesli.rclaim.hook.manager;

import lombok.Getter;
import net.weesli.rclaim.config.ConfigLoader;
import net.weesli.rclaim.hook.combat.IClaimCombat;
import net.weesli.rclaim.hook.combat.CombatLogX;
import net.weesli.rclaim.hook.combat.PvPManager;
import org.bukkit.Bukkit;

@Getter
public class CombatManager {

    private IClaimCombat combatIntegration;

    public CombatManager(){
        if (!ConfigLoader.getConfig().isCombatSystem()){
            combatIntegration = null;
            return;
        }
        if (Bukkit.getServer().getPluginManager().isPluginEnabled("CombatLogX")){
            combatIntegration = new CombatLogX();
        } else if (Bukkit.getServer().getPluginManager().isPluginEnabled("PvPManager")) {
            combatIntegration = new PvPManager();
        }
    }


}
