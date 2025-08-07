package net.weesli.rclaim.hook.combat;


import net.weesli.rclaim.api.hook.ClaimCombat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PvPManager implements ClaimCombat {

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
