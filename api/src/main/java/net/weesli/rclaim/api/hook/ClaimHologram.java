package net.weesli.rclaim.api.hook;

import net.weesli.rclaim.api.RClaimProvider;
import net.weesli.rclaim.api.enums.HologramModule;
import net.weesli.rclaim.api.model.Claim;
import org.bukkit.Location;

public interface ClaimHologram {
    void createHologram(String ID);
    void updateHologram(String ID);
    void deleteHologram(String ID);
    boolean hasHologram(String ID);
    HologramModule Type();
}
