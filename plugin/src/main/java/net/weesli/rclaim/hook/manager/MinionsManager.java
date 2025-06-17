package net.weesli.rclaim.hook.manager;

import lombok.Getter;
import lombok.Setter;
import net.weesli.rclaim.hook.minion.IClaimMinion;
import net.weesli.rclaim.hook.minion.AxMinions;
import net.weesli.rclaim.hook.minion.JetMinions;
import net.weesli.rclaim.hook.minion.LitMinions;
import org.bukkit.Bukkit;

@Getter@Setter
public class MinionsManager {

    private IClaimMinion minionIntegration;

    public MinionsManager(){
        if (Bukkit.getPluginManager().isPluginEnabled("AxMinions")){
            minionIntegration = new AxMinions();
        } else if (Bukkit.getPluginManager().isPluginEnabled("LitMinions")) {
            minionIntegration = new LitMinions();
        } else if (Bukkit.getPluginManager().isPluginEnabled("JetMinions")) {
            minionIntegration = new JetMinions();
        }
    }

    public IClaimMinion getIntegration() {
        return minionIntegration;
    }
}
