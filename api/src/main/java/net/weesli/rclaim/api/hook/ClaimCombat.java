package net.weesli.rclaim.api.hook;

import org.bukkit.entity.Player;

public interface ClaimCombat {

    boolean isPvP(Player player);

    boolean isEnabled();

    String getName();
}
