package net.weesli.rClaim.hooks.combat;

import org.bukkit.entity.Player;

public interface Combat {

    boolean isPvP(Player player);

    boolean isEnabled();
}
