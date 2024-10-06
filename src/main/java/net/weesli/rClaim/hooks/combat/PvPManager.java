package net.weesli.rClaim.hooks.combat;


import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PvPManager implements Combat{

    me.NoChance.PvPManager.PvPManager pvpmanager;
    private boolean enabled;
    public PvPManager(){
        if (Bukkit.getPluginManager().isPluginEnabled("PvPManager"))
            pvpmanager = (me.NoChance.PvPManager.PvPManager) Bukkit.getPluginManager().getPlugin("PvPManager");
        enabled= pvpmanager != null;
    }


    @Override
    public boolean isPvP(Player player) {
        return pvpmanager.getPlayerHandler().get(player).isInCombat();
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public String getName() {
        return "PvPManager";
    }
}
