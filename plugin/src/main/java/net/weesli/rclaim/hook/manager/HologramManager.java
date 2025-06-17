package net.weesli.rclaim.hook.manager;

import lombok.Getter;
import lombok.Setter;
import net.weesli.rclaim.config.ConfigLoader;
import net.weesli.rclaim.api.enums.HologramModule;
import net.weesli.rclaim.hook.hologram.HDecentHologram;
import net.weesli.rclaim.hook.hologram.IClaimHologram;
import net.weesli.rclaim.task.HologramUpdater;
@Getter@Setter
public class HologramManager {

    private IClaimHologram hologramIntegration;

    public HologramManager(){
        if (ConfigLoader.getConfig().getHologram().isEnabled()){
            HologramModule module = HologramModule.valueOf(ConfigLoader.getConfig().getHologram().getHologramModule());
            switch (module){
                case DecentHologram -> hologramIntegration = new HDecentHologram();
            }
            new HologramUpdater();
        }
    }
}
