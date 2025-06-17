package net.weesli.rclaim.model;

import lombok.Getter;
import lombok.Setter;
import net.weesli.rclaim.api.enums.Effect;
import net.weesli.rclaim.api.model.ClaimEffect;

@Getter@Setter
public class ClaimEffectImpl implements ClaimEffect {

    private Effect effect;
    private int level;
    private int maxLevel;
    private boolean enabled;

    public ClaimEffectImpl() {
    }
    public ClaimEffectImpl(Effect effect, int level, int maxLevel, boolean enabled) {
        this.effect = effect;
        this.level = level;
        this.maxLevel = maxLevel;
        this.enabled = enabled;
    }

    public boolean isMaxLevel(){
        return level >= maxLevel;
    }
}

