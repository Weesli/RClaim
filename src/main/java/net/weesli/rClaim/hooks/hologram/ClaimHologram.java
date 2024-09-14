package net.weesli.rClaim.hooks.hologram;

import net.weesli.rClaim.enums.HologramModule;

public interface ClaimHologram {

    public abstract void createHologram(String ID);

    public abstract void updateHologram(String ID);

    public abstract void deleteHologram(String ID);

    public abstract boolean hasHologram(String ID);

    public abstract HologramModule Type();
}
