package net.weesli.rclaim.api.database;

import net.weesli.rclaim.api.RClaimProvider;
import net.weesli.rclaim.api.model.Claim;

public class Loader {

    public static void load() {
        for (Claim claim : RClaimProvider.getStorage().getAllClaims()) {
            RClaimProvider.getCacheManager().getClaims().getCache().put(claim.getID(), claim);
        }
    }

    public static void save() {
        for (Claim claimImpl : RClaimProvider.getCacheManager().getClaims().getCache().values()) {
            if (RClaimProvider.getStorage().hasClaim(claimImpl.getID())) {
                RClaimProvider.getStorage().updateClaim(claimImpl);
            } else {
                RClaimProvider.getStorage().insertClaim(claimImpl);
            }
        }
        RClaimProvider.getStorage().forceSave(); // force the saved data to disk with RozsDBLite
    }

}
