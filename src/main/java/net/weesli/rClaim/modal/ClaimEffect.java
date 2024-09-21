package net.weesli.rClaim.modal;

import lombok.Getter;
import lombok.Setter;
import net.weesli.rClaim.enums.Effect;

@Getter@Setter
public class ClaimEffect {

    public int max_level;
    private Effect effect;
    private int level;
    private int maxLevel;
    private boolean enabled;

    public ClaimEffect(Effect effect, int level, int maxLevel, boolean enabled) {
        this.effect = effect;
        this.level = level;
        this.maxLevel = maxLevel;
        this.enabled = enabled;
    }

    public boolean isMaxLevel(){
        return level >= maxLevel;
    }
}

