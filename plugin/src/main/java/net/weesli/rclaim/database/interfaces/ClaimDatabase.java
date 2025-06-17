package net.weesli.rclaim.database.interfaces;

import net.weesli.rclaim.api.model.Claim;
import net.weesli.rclaim.model.ClaimImpl;

import java.util.List;

public interface ClaimDatabase {
    void insertClaim(Claim claim);
    void updateClaim(Claim claim);
    void deleteClaim(String id);
    List<Claim> getAllClaims();
    boolean hasClaim(String id);
}
