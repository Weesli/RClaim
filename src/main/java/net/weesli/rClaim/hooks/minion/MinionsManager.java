package net.weesli.rClaim.hooks.minion;

import org.bukkit.Bukkit;


public class MinionsManager {

    private static ClaimMinion integration;

    public MinionsManager(){
        if (Bukkit.getPluginManager().isPluginEnabled("AxMinions")){
            integration = new AxMinions();
        } else if (Bukkit.getPluginManager().isPluginEnabled("LitMinions")) {
            integration = new LitMinions();
        } else if (Bukkit.getPluginManager().isPluginEnabled("JetMinions")) {
            integration = new JetMinions();
        }
    }

    public ClaimMinion getIntegration() {
        return integration;
    }
}
