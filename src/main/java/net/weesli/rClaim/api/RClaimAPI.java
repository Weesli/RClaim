package net.weesli.rClaim.api;

import com.artillexstudios.axminions.libs.axapi.packetentity.meta.serializer.EntityDataSerializers;
import net.weesli.rClaim.utils.ClaimManager;
import net.weesli.rClaim.enums.ExplodeCause;
import net.weesli.rClaim.modal.Claim;
import net.weesli.rClaim.enums.ClaimPermission;
import net.weesli.rClaim.enums.ClaimStatus;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import java.util.List;

public class RClaimAPI {

    private static RClaimAPI instance;

    // Method to get the singleton instance of RClaimAPI
    public static RClaimAPI getInstance(){
        if (instance == null){
            instance = new RClaimAPI();
        }
        return instance;
    }

    /**
     * Creates a new claim.
     * @param chunk The chunk where the claim will be created
     * @param owner The player who owns the claim
     * @param isCenter If true, creates a bedrock block at the center of the claim
     * @param centerId The identifier for the center block
     */
    public void createClaim(Chunk chunk, Player owner, boolean isCenter, String centerId){
        ClaimManager.createClaim(chunk, owner, isCenter, centerId);
    }

    /**
     * Deletes an existing claim.
     * @param claim The claim to be deleted
     * @param isCenter Indicates whether the center block should also be deleted
     */
    public void deleteClaim(Claim claim, boolean isCenter){
        ClaimManager.ExplodeClaim(claim.getID(), ExplodeCause.ADMIN, isCenter);
    }

    /**
     * Checks if the specified chunk is already claimed.
     * @param chunk The chunk to check
     * @return Returns true if the chunk is claimed and not available for use
     */
    public boolean isClaimed(Chunk chunk){
        return ClaimManager.isSuitable(chunk);
    }

    /**
     * Retrieves a claim based on its ID.
     * @param claimId The ID of the claim to retrieve
     * @return The claim if found, otherwise null
     */
    public Claim getClaim(String claimId){
        return ClaimManager.getClaims().stream()
                .filter(claim -> claim.getID().equals(claimId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Retrieves a claim based on the specified chunk.
     * @param chunk The chunk to check for a claim
     * @return The claim if found, otherwise null
     */
    public Claim getClaim(Chunk chunk){
        return ClaimManager.getClaims().stream()
                .filter(claim -> claim.getChunk().getX() == chunk.getX() && claim.getChunk().getZ() == chunk.getZ())
                .findFirst()
                .orElse(null);
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
    public List<Claim> getClaims(){
        return ClaimManager.getClaims();
    }
}
