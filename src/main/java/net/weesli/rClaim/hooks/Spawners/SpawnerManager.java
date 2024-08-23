package net.weesli.rClaim.hooks.Spawners;

import org.bukkit.Bukkit;

public class SpawnerManager {

    SpawnerIntegration integration;

    public SpawnerManager(){
        if (Bukkit.getPluginManager().isPluginEnabled("SilkSpawners_v2")){
            System.out.println("awdawd");
            integration = new SilkSpawner();
        } else if (Bukkit.getPluginManager().isPluginEnabled("SpawnerMeta")) {
            integration = new SpawnerMeta();
        } else if (Bukkit.getPluginManager().isPluginEnabled("UpgradeableSpawners")) {
            integration = new UpgradeableSpawners();
        }
    }

}
