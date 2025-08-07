package net.weesli.rclaim.hook.manager;

import net.weesli.rclaim.api.hook.ClaimSpawner;
import net.weesli.rclaim.api.hook.manager.SpawnerManager;
import net.weesli.rclaim.hook.spawner.*;
import org.bukkit.Bukkit;

public class SpawnerManagerImpl  implements SpawnerManager {

    private ClaimSpawner spawnerIntegration;

    public SpawnerManagerImpl(){
        if (Bukkit.getPluginManager().isPluginEnabled("SilkSpawners_v2")){
            spawnerIntegration = new SilkSpawner();
        } else if (Bukkit.getPluginManager().isPluginEnabled("SpawnerMeta")) {
            spawnerIntegration = new SpawnerMeta();
        } else if (Bukkit.getPluginManager().isPluginEnabled("UpgradeableSpawners")) {
            spawnerIntegration = new UpgradeableSpawners();
        } else if (Bukkit.getPluginManager().isPluginEnabled("SmartSpawner")) {
            spawnerIntegration = new SmartSpawner();
        }
    }

    public ClaimSpawner getIntegration() {
        return spawnerIntegration;
    }
}
