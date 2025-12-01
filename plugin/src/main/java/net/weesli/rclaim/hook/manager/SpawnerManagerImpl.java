package net.weesli.rclaim.hook.manager;

import com.tcoded.folialib.FoliaLib;
import net.weesli.rclaim.api.hook.ClaimSpawner;
import net.weesli.rclaim.api.hook.manager.SpawnerManager;
import net.weesli.rclaim.hook.spawner.SilkSpawner;
import net.weesli.rclaim.hook.spawner.SmartSpawner;
import net.weesli.rclaim.hook.spawner.SpawnerMeta;
import net.weesli.rclaim.hook.spawner.UpgradeableSpawners;
import org.bukkit.Bukkit;

public class SpawnerManagerImpl  implements SpawnerManager {

    private ClaimSpawner spawnerIntegration;

    public SpawnerManagerImpl(FoliaLib foliaLib){
        if (Bukkit.getPluginManager().isPluginEnabled("SilkSpawners_v2")){
            spawnerIntegration = new SilkSpawner();
        } else if (Bukkit.getPluginManager().isPluginEnabled("SpawnerMeta")) {
            spawnerIntegration = new SpawnerMeta(foliaLib);
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
