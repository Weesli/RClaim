package net.weesli.rclaim.api.hook.manager;

import net.weesli.rclaim.api.hook.ClaimMap;
import org.bukkit.event.Listener;

public interface MapManager extends Listener {

    ClaimMap getIntegration();

}
