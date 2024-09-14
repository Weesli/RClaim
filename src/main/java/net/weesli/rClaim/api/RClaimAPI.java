package net.weesli.rClaim.api;

import net.weesli.rClaim.utils.ClaimManager;
import net.weesli.rClaim.enums.ExplodeCause;
import net.weesli.rClaim.modal.Claim;
import net.weesli.rClaim.enums.ClaimPermission;
import net.weesli.rClaim.enums.ClaimStatus;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import java.util.List;

public class RClaimAPI {

    private static RClaimAPI instance;

    public static RClaimAPI getInstance(){
        if (instance == null){
            instance = new RClaimAPI();
        }
        return instance;
    }

    /**
     * create a new claim
     * @param chunk
     * @param owner
     * @param isCenter ( if true then created a bedrock block in center )
     */
    public void createClaim(Chunk chunk, Player owner, boolean isCenter, String centerId){
        ClaimManager.createClaim(chunk,owner,isCenter, centerId);
    }

    /**
     *
     * @param claim
     */
    public void deleteClaim(Claim claim, boolean isCenter){
        ClaimManager.ExplodeClaim(claim.getID(), ExplodeCause.ADMIN, isCenter);
    }

    /**
     *
     * @param chunk
     * @return if it returns true, the field is not available!
     */
    public boolean isClaimed(Chunk chunk){
        return ClaimManager.isSuitable(chunk);
    }

    /**
     *
     * @param claimId
     * @return
     */
    public Claim getClaim(String claimId){
        return ClaimManager.getClaims().stream().filter(claim -> claim.getID().equals(claimId)).findFirst().orElse(null);
    }

    public Claim getClaim(Chunk chunk){
        return ClaimManager.getClaims().stream().filter(claim -> claim.getChunk().getX() == chunk.getX() && claim.getChunk().getZ() == chunk.getZ()).findFirst().orElse(null);
    }

    /**
     *
     * @param player
     * @param claim
     * @return
     */
    public boolean isOwner(Player player, Claim claim){
        return claim.getOwner().equals(player.getUniqueId());
    }

    /**
     *
     * @param player
     * @param claim
     * @return
     */
    public boolean isInsideClaim(Player player, Claim claim){
        return claim.contains(player.getLocation());
    }

    /**
     *
     * @param player
     * @param claim
     * @param permission
     * @return
     */
    public boolean hasPermission(Player player, Claim claim, ClaimPermission permission){
        return claim.checkPermission(player.getUniqueId(), permission);
    }

    /**
     *
     * @param claim
     * @param status
     * @return
     */
    public boolean checkClaimStatus(Claim claim, ClaimStatus status){
        return claim.checkStatus(status);
    }

    /**
     *
     * @return all claims
     */
    public List<Claim> getClaims(){
        return ClaimManager.getClaims();
    }

}