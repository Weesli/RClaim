package net.weesli.rClaim.hooks.combat;

import lombok.Getter;
import org.bukkit.plugin.Plugin;
@Getter
public class CombatManager {

    private Combat combat;


    public CombatManager(Plugin plugin){
        if (plugin.getServer().getPluginManager().isPluginEnabled("CombatLogX")){
            combat = new CombatLogX();
        } else if (plugin.getServer().getPluginManager().isPluginEnabled("PvPManager")) {
            combat = new PvPManager();
        }
    }
}
