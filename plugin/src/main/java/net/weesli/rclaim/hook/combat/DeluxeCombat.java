package net.weesli.rclaim.hook.combat;

import net.weesli.rclaim.api.hook.ClaimCombat;
import nl.marido.deluxecombat.api.DeluxeCombatAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class DeluxeCombat implements ClaimCombat {

    private boolean enabled;
    private DeluxeCombatAPI api;

    public DeluxeCombat(){
        if (Bukkit.getPluginManager().isPluginEnabled("DeluxeCombat")) {
            Bukkit.getConsoleSender().sendMessage("DeluxeCombat not found. DeluxeCombat hooks disabled.");
            enabled = false;
            return;
        }
        api = new DeluxeCombatAPI();
        enabled = true;
    }

    @Override
    public boolean isPvP(Player player) {
        return api.isInCombat(player);
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public String getName() {
        return "DeluxeCombat";
    }
}
