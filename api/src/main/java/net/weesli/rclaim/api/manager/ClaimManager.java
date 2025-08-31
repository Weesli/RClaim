package net.weesli.rclaim.api.manager;

import net.weesli.rclaim.api.enums.ExplodeCause;
import net.weesli.rclaim.api.model.Claim;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Provides an API for managing land claims and sub-claims.
 * Handles retrieval, creation, and validation of claims based on
 * locations and chunks in the game world.
 */
public interface ClaimManager {

    /**
     * Retrieves the claim that contains the specified location.
     *
     * @param location The location to query.
     * @return The {@link Claim} at the given location, or null if none exists.
     */
    Claim getClaim(Location location);

    /**
     * Retrieves the claim associated with a specific chunk.
     *
     * @param chunk The chunk to check.
     * @return The {@link Claim} in the chunk, or null if none exists.
     */
    Claim getClaim(Chunk chunk);

    /**
     * Retrieves a claim by its unique identifier.
     *
     * @param id The ID of the claim.
     * @return The {@link Claim} with the given ID, or null if not found.
     */
    Claim getClaim(String id);

    /**
     * Creates a sub-claim within an existing claim at the specified location.
     * Sub-claims allow restricted or extended permissions within a smaller region of a claim.
     *
     * @param player The player requesting the sub-claim.
     * @param claim The parent claim.
     * @param location The location for the sub-claim.
     * @return True if the sub-claim was successfully created.
     */
    boolean createSubClaim(Player player, Claim claim, Location location);

    /**
     * Creates a new claim for the specified player within the given chunk.
     *
     * @param chunk The chunk where the claim will be created.
     * @param owner The player who will own the claim.
     */
    void createClaim(Chunk chunk, Player owner);

    /**
     * Checks if a chunk is suitable for creating a new claim.
     * Suitability may depend on whether the chunk is already claimed, restricted, or protected.
     *
     * @param chunk The chunk to evaluate.
     * @return True if the chunk can be claimed.
     */
    boolean isSuitable(Chunk chunk);

    /**
     * Checks if a specific location is suitable for claim creation.
     *
     * @param location The location to evaluate.
     * @return True if the location is suitable for a claim.
     */
    boolean isSuitable(Location location);

    void explodeClaim(String id, ExplodeCause explodeCause);
}
