package net.weesli.rclaim.hook.manager;

import lombok.Getter;
import lombok.Setter;
import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.hook.manager.HologramManager;
import net.weesli.rclaim.config.ConfigLoader;
import net.weesli.rclaim.api.enums.HologramModule;
import net.weesli.rclaim.hook.hologram.HDecentHologram;
import net.weesli.rclaim.api.hook.ClaimHologram;
import net.weesli.rclaim.hook.hologram.HFancyHologram;
import net.weesli.rclaim.task.HologramUpdater;
@Getter@Setter
public class HologramManagerImpl implements HologramManager {

    private ClaimHologram hologramIntegration;

    public HologramManagerImpl(){
        if (ConfigLoader.getConfig().getHologram().isEnabled()){
            HologramModule module = HologramModule.valueOf(ConfigLoader.getConfig().getHologram().getHologramModule());
            switch (module){
                case DecentHolograms -> hologramIntegration = new HDecentHologram();
                case FancyHolograms -> hologramIntegration = new HFancyHologram();
            }
            new HologramUpdater();
        }
    }
}
