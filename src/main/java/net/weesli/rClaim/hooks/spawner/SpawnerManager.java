package net.weesli.rClaim.hooks.spawner;

import org.bukkit.Bukkit;

public class SpawnerManager {

    private static ClaimSpawner integration;

    public SpawnerManager(){
        if (Bukkit.getPluginManager().isPluginEnabled("SilkSpawners_v2")){
            integration = new SilkSpawner();
        } else if (Bukkit.getPluginManager().isPluginEnabled("SpawnerMeta")) {
            integration = new SpawnerMeta();
        } else if (Bukkit.getPluginManager().isPluginEnabled("UpgradeableSpawners")) {
            integration = new UpgradeableSpawners();
        }
    }

    public ClaimSpawner getIntegration() {
        return integration;
    }
}
