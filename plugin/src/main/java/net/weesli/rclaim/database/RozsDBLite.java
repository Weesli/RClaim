package net.weesli.rclaim.database;

import net.weesli.rclaim.GsonProvider;
import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.enums.StorageType;
import net.weesli.rclaim.api.model.Claim;
import net.weesli.rclaim.database.interfaces.ClaimDatabase;

import net.weesli.rozsdblite.interfaces.Database;
import net.weesli.rozsdblite.interfaces.Table;

import java.util.ArrayList;
import java.util.List;

public class RozsDBLite implements ClaimDatabase {

    private Database database;
    private Table claimsTable;

    public RozsDBLite(){
        database = net.weesli.rozsdblite.RozsDBLite.open("RClaims", RClaim.getInstance().getDataFolder().toPath());
        claimsTable = database.getTable("rclaims_claims");
    }

    @Override
    public void insertClaim(Claim claim) {
        String json = GsonProvider.getGson().toJson(claim);
        claimsTable.put(claim.getID(), json);
    }

    @Override
    public void updateClaim(Claim claim) {
        String json = GsonProvider.getGson().toJson(claim);
        claimsTable.put(claim.getID(), json);
    }

    @Override
    public void deleteClaim(String id) {
        claimsTable.remove(id);
    }

    @Override
    public List<Claim> getAllClaims() {
        List<Claim> claims = new ArrayList<>();
        for (String value : claimsTable.getAll()) {
            claims.add(GsonProvider.getGson().fromJson(value, Claim.class));
        }
        return claims;
    }

    @Override
    public boolean hasClaim(String id) {
        return claimsTable.get(id) != null;
    }

    @Override
    public void shutdown() {
        // ignore for RozsDBLite
    }

    @Override
    public StorageType getStorageType() {
        return StorageType.RozsDBLite;
    }

    @Override
    public void forceSave() {
        database.save();
    }

    @Override
    public String tableSQL() {
        return "";
    }
}
