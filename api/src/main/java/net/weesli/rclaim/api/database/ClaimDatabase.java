package net.weesli.rclaim.api.database;

import net.weesli.rclaim.api.enums.StorageType;
import net.weesli.rclaim.api.model.Claim;

import java.util.List;

public interface ClaimDatabase{
    void insertClaim(Claim claim);
    void updateClaim(Claim claim);
    void deleteClaim(String id);
    List<Claim> getAllClaims();
    boolean hasClaim(String id);

    void shutdown();
    StorageType getStorageType();
    String tableSQL();
    void forceSave();
}
