package net.weesli.rclaim.hook.hologram;

import net.weesli.rclaim.api.enums.HologramModule;

public interface IClaimHologram {
    void createHologram(String ID);
    void updateHologram(String ID);
    void deleteHologram(String ID);
    boolean hasHologram(String ID);
    HologramModule Type();
}
