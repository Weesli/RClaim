package net.weesli.rClaim.hooks.combat;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@Getter
public class CombatLogX implements Combat{

    ICombatLogX api;

    private boolean enabled;

    ICombatManager manager;
    public CombatLogX(){
        api = (ICombatLogX) Bukkit.getServer().getPluginManager().getPlugin("CombatLogX");
        if (api == null) {
            Bukkit.getConsoleSender().sendMessage("CombatLogX not found. Combat hooks disabled.");
            enabled = false;
        }
        enabled = true;
        manager = api.getCombatManager();
    }

    @Override
    public boolean isPvP(Player player) {
        return manager.isInCombat(player);
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public String getName() {
        return "CombatLogX";
    }
}
