package net.weesli.rClaim.module;

import net.weesli.rClaim.RClaim;
import org.bukkit.plugin.Plugin;

public interface Module {

    // Reference to the main plugin instance (RClaim).
    // This allows implementing classes to interact with the main plugin.
    Plugin plugin = RClaim.getInstance();

    /**
     * This method will be called when the module is enabled.
     * Implement this method to define the setup logic or initialization code
     * for the module.
     */
    void enable();

    /**
     * This method should return the name of the module or add-on.
     *
     * @return The name of the add-on.
     */
    String getAddonName();

    /**
     * This method should return the version of the module or add-on.
     *
     * @return The version of the add-on.
     */
    String getVersion();

}
