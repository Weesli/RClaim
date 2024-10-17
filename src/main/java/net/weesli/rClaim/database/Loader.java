package net.weesli.rClaim.database;

import net.weesli.rClaim.RClaim;
import net.weesli.rClaim.modal.ClaimTag;
import net.weesli.rClaim.utils.ClaimManager;
import net.weesli.rClaim.modal.Claim;
import net.weesli.rClaim.utils.TagManager;

import java.util.List;

public class Loader {

    public static void load(){
        for (Claim claim : RClaim.getInstance().getStorage().getClaims()){
            ClaimManager.addClaim(claim);
        }
    }

    public static void save(){
        for (Claim claim : ClaimManager.getClaims()){
            if(RClaim.getInstance().getStorage().hasClaim(claim.getID())){
                RClaim.getInstance().getStorage().updateClaim(claim);
            }else {
                RClaim.getInstance().getStorage().insertClaim(claim);
            }
        }
    }

}
