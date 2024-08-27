package net.weesli.rClaim.hooks.Spawners;

import org.bukkit.Bukkit;

public class SpawnerManager {

    private static SpawnerIntegration integration;

    public SpawnerManager(){
        if (Bukkit.getPluginManager().isPluginEnabled("SilkSpawners_v2")){
            integration = new SilkSpawner();
        } else if (Bukkit.getPluginManager().isPluginEnabled("SpawnerMeta")) {
            integration = new SpawnerMeta();
        } else if (Bukkit.getPluginManager().isPluginEnabled("UpgradeableSpawners")) {
            integration = new UpgradeableSpawners();
        }
    }

    public SpawnerIntegration getIntegration() {
        return integration;
    }
}
