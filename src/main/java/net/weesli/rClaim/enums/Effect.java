package net.weesli.rClaim.enums;

import lombok.Getter;
import net.weesli.rClaim.utils.ClaimManager;
import org.bukkit.potion.PotionEffectType;

@Getter
public enum Effect {
    SPEED(2, ClaimManager.getEffectType("speed")),
    JUMP(2, ClaimManager.getEffectType("jump")),
    HASTE(2, ClaimManager.getEffectType("haste"));

    private final int maxLevel;
    private final PotionEffectType type;

    Effect(int maxLevel, PotionEffectType type) {
        this.maxLevel = maxLevel;
        this.type = type;
    }
}
