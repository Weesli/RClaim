package net.weesli.rClaim.StorageManager;

import net.weesli.rClaim.RClaim;
import net.weesli.rClaim.tasks.ClaimTask;
import net.weesli.rClaim.utils.Claim;

import java.util.List;

public abstract class StorageImpl {

    public abstract void insertClaim(Claim claim);
    public abstract Claim getClaim(String id);
    public abstract void updateClaim(Claim claim);
    public abstract void deleteClaim(String id);
    public abstract boolean hasClaim(String id);
    public abstract List<Claim> getClaims();
    public abstract void updateTime(ClaimTask task);

    public abstract StorageType getStorageType();

    public void register(){
        RClaim.getInstance().setStorage(this);
    }
}
