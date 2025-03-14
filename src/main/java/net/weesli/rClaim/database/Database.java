package net.weesli.rClaim.database;

import net.weesli.rClaim.enums.StorageType;
import net.weesli.rClaim.modal.Claim;
import net.weesli.rozsLib.database.component.Column;

import java.util.List;

public interface Database {

    void connect();
    String getTableQuery();
    void insertClaim(Claim claim);
    Claim getClaim(String id);
    void updateClaim(Claim claim);
    void deleteClaim(String id);
    boolean hasClaim(String id);
    List<Claim> getClaims();

    StorageType getStorageType();
}
