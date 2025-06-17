package net.weesli.rclaim.hook.combat;

import org.bukkit.entity.Player;

public interface IClaimCombat {

    boolean isPvP(Player player);

    boolean isEnabled();

    String getName();
}
