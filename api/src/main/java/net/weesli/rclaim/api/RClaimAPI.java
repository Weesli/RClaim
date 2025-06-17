package net.weesli.rclaim.api;

import net.weesli.rclaim.api.manager.CacheManager;
import net.weesli.rclaim.api.enums.ClaimPermission;
import net.weesli.rclaim.api.enums.ClaimStatus;
import net.weesli.rclaim.api.manager.ClaimManager;
import net.weesli.rclaim.api.model.Claim;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * This class is deprecated. Please use {@link RClaimProvider}
 */
@Deprecated
public class RClaimAPI {

    private static RClaimAPI instance;

    private ClaimManager claimManager;
    private CacheManager cacheProvider;

    public static RClaimAPI getInstance() {
        if (instance == null) instance = new RClaimAPI();
        return instance;
    }


    /**
     * Creates a new claim.
     * @param chunk The chunk where the claim will be created
     * @param owner The player who owns the claim
     */
    public void createClaim(Chunk chunk, Player owner){
        
    }

    /**
     * Deletes an existing claim.
     * @param claim The claim to be deleted
     */
    public void deleteClaim(Claim claim){
        claim.delete(false);
    }

    /**
     * Checks if the specified chunk is already claimed.
     * @param chunk The chunk to check
     * @return Returns true if the chunk is claimed and not available for use
     */
    public boolean isClaimed(Chunk chunk){
        return false;
    }

    /**
     * Retrieves a claim based on its ID.
     * @param claimId The ID of the claim to retrieve
     * @return The claim if found, otherwise null
     */
    public Claim getClaim(String claimId){
        return null;
    }

    /**
     * Retrieves a claim based on the specified chunk.
     * @param chunk The chunk to check for a claim
     * @return The claim if found, otherwise null
     */
    public Claim getClaim(Chunk chunk){
        return null;
    }

    /**
     * Checks if the specified player is the owner of the claim.
     * @param player The player to check
     * @param claim The claim to check against
     * @return Returns true if the player is the owner of the claim
     */
    public boolean isOwner(Player player, Claim claim){
        return claim.getOwner().equals(player.getUniqueId());
    }

    /**
     * Checks if the specified player is inside the claim area.
     * @param player The player to check
     * @param claim The claim to check against
     * @return Returns true if the player is inside the claim
     */
    public boolean isInsideClaim(Player player, Claim claim){
        return claim.contains(player.getLocation());
    }

    /**
     * Checks if the specified player has the given permission for the claim.
     * @param player The player to check
     * @param claim The claim to check against
     * @param permission The permission to check for
     * @return Returns true if the player has the specified permission
     */
    public boolean hasPermission(Player player, Claim claim, ClaimPermission permission){
        return claim.checkPermission(player.getUniqueId(), permission);
    }

    /**
     * Checks the status of the claim against the specified status.
     * @param claim The claim to check
     * @param status The status to check against
     * @return Returns true if the claim matches the specified status
     */
    public boolean checkClaimStatus(Claim claim, ClaimStatus status){
        return claim.checkStatus(status);
    }

    /**
     * Retrieves all claims.
     * @return A list of all claims
     */
    public static List<Claim> getClaims(){
        return List.of();
    }
}
