package net.weesli.rclaim.api.hook;

import net.weesli.rclaim.api.enums.HologramModule;

public interface ClaimHologram {
    void createHologram(String ID);
    void updateHologram(String ID);
    void deleteHologram(String ID);
    boolean hasHologram(String ID);
    HologramModule Type();
}
