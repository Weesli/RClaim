package net.weesli.rclaim.hook.manager;

import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.hook.ClaimStacker;
import net.weesli.rclaim.api.hook.manager.StackerManager;
import net.weesli.rclaim.hook.spawner.SilkSpawner;
import net.weesli.rclaim.hook.spawner.SpawnerMeta;
import net.weesli.rclaim.hook.stacker.HRoseStacker;
import net.weesli.rclaim.hook.stacker.HWildStacker;
import org.bukkit.Bukkit;

public class StackerManagerImpl implements StackerManager {

    private ClaimStacker stackerIntegration;

    public StackerManagerImpl(RClaim plugin) {
        if (Bukkit.getPluginManager().isPluginEnabled("RoseStacker")){
            stackerIntegration = new HRoseStacker(plugin);
        } else if (Bukkit.getPluginManager().isPluginEnabled("WildStacker")) {
            stackerIntegration = new HWildStacker(plugin);
        }
    }

    @Override
    public ClaimStacker getIntegration() {
        return this.stackerIntegration;
    }
}
