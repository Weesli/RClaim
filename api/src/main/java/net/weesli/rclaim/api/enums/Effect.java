package net.weesli.rclaim.api.enums;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.potion.PotionEffectType;

@Getter
public enum Effect {
    SPEED(2, getEffectType("speed")),
    JUMP(2, getEffectType("jump")),
    HASTE(2, getEffectType("haste"));

    private final int maxLevel;
    private final PotionEffectType type;

    Effect(int maxLevel, PotionEffectType type) {
        this.maxLevel = maxLevel;
        this.type = type;
    }

    private static PotionEffectType getEffectType(String type){
        String currentVersion = Bukkit.getVersion();
        if (currentVersion.equals("1.21") || currentVersion.equals("1.21.1")){
            switch (type){
                case "jump" -> {
                    return PotionEffectType.JUMP_BOOST;
                }
                case "haste" -> {
                    return PotionEffectType.HASTE;
                }
                case "speed" -> {
                    return PotionEffectType.SPEED;
                }
            }
        } else {
            switch (type){
                case "jump" -> {
                    return PotionEffectType.getByName("JUMP");
                }
                case "haste" -> {
                    return PotionEffectType.getByName("FAST_DIGGING");
                }
                case "speed" -> {
                    return PotionEffectType.SPEED;
                }
            }
        }
        return null;
    }
}
