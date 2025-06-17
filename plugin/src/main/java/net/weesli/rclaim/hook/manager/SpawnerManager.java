package net.weesli.rclaim.hook.manager;

import net.weesli.rclaim.hook.spawner.IClaimSpawner;
import net.weesli.rclaim.hook.spawner.SilkSpawner;
import net.weesli.rclaim.hook.spawner.SpawnerMeta;
import net.weesli.rclaim.hook.spawner.UpgradeableSpawners;
import org.bukkit.Bukkit;

public class SpawnerManager {

    private IClaimSpawner spawnerIntegration;

    public SpawnerManager(){
        if (Bukkit.getPluginManager().isPluginEnabled("SilkSpawners_v2")){
            spawnerIntegration = new SilkSpawner();
        } else if (Bukkit.getPluginManager().isPluginEnabled("SpawnerMeta")) {
            spawnerIntegration = new SpawnerMeta();
        } else if (Bukkit.getPluginManager().isPluginEnabled("UpgradeableSpawners")) {
            spawnerIntegration = new UpgradeableSpawners();
        }
    }

    public IClaimSpawner getIntegration() {
        return spawnerIntegration;
    }
}
