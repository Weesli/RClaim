package net.weesli.rclaim.api.hook;

import net.weesli.rclaim.api.model.Claim;

public interface ClaimMap {
    void update(Claim claim);
    void delete(Claim claim);
    void disable();
}
