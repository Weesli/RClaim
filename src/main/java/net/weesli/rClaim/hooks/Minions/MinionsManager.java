package net.weesli.rClaim.hooks.Minions;

import org.bukkit.Bukkit;


public class MinionsManager {

    private static MinionsIntegration integration;

    public MinionsManager(){
        if (Bukkit.getPluginManager().isPluginEnabled("AxMinions")){
            integration = new AxMinions();
        } else if (Bukkit.getPluginManager().isPluginEnabled("LitMinions")) {
            integration = new LitMinions();
        } else if (Bukkit.getPluginManager().isPluginEnabled("JetMinions")) {
            integration = new JetMinions();
        }
    }

    public MinionsIntegration getIntegration() {
        return integration;
    }
}
