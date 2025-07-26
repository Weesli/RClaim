package net.weesli.rclaim.database;

import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.model.Claim;
import net.weesli.rclaim.model.ClaimImpl;

public class Loader {

    public static void load() {
        for (Claim claim : RClaim.getInstance().getStorage().getAllClaims()) {
            RClaim.getInstance().getCacheManager().getClaims().addClaim(claim);
        }
    }

    public static void save() {
        for (Claim claimImpl : RClaim.getInstance().getCacheManager().getClaims().getCache().values()) {
            if (RClaim.getInstance().getStorage().hasClaim(claimImpl.getID())) {
                RClaim.getInstance().getStorage().updateClaim(claimImpl);
            } else {
                RClaim.getInstance().getStorage().insertClaim(claimImpl);
            }
        }
        RClaim.getInstance().getStorage().forceSave(); // force the saved data to disk with RozsDBLite
    }

}
