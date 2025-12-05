package net.weesli.rclaim.hook.manager;

import lombok.Getter;
import net.weesli.rclaim.api.hook.manager.CombatManager;
import net.weesli.rclaim.config.ConfigLoader;
import net.weesli.rclaim.api.hook.ClaimCombat;
import net.weesli.rclaim.hook.combat.CombatLogX;
import net.weesli.rclaim.hook.combat.DeluxeCombat;
import net.weesli.rclaim.hook.combat.PvPManager;
import org.bukkit.Bukkit;

@Getter
public class CombatManagerImpl implements CombatManager {

    private ClaimCombat combatIntegration;

    public CombatManagerImpl(){
        if (!ConfigLoader.getConfig().isCombatSystem()){
            combatIntegration = null;
            return;
        }
        if (Bukkit.getServer().getPluginManager().isPluginEnabled("CombatLogX")){
            combatIntegration = new CombatLogX();
        } else if (Bukkit.getServer().getPluginManager().isPluginEnabled("PvPManager")) {
            combatIntegration = new PvPManager();
        } else if (Bukkit.getServer().getPluginManager().isPluginEnabled("DeluxeCombat")) {
            combatIntegration = new DeluxeCombat();
        }
    }


}
