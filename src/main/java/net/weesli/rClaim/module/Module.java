package net.weesli.rClaim.module;

import net.weesli.rClaim.RClaim;
import org.bukkit.plugin.Plugin;

public interface Module {

    Plugin plugin = RClaim.getInstance();

    void enable();

    String getAddonName();
    String getVersion();

}
