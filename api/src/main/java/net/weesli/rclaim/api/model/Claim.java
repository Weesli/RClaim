package net.weesli.rclaim.api.model;

import net.weesli.rclaim.api.enums.ClaimPermission;
import net.weesli.rclaim.api.enums.ClaimStatus;
import net.weesli.rclaim.api.enums.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Represents a protected land claim in the game world. Claims are owned by players and can
 * have permissions, effects, statuses, and subclaims. This interface defines the contract
 * for managing all aspects of a claim.
 */
public interface Claim {

    /** @return Unique identifier of the claim. */
    String getID();

    /** @return The display name of the claim. */
    String getDisplayName();

    /** @return UUID of the claim owner. */
    UUID getOwner();

    /** @return The timestamp (e.g., creation or expiration time) associated with the claim. */
    int getTimestamp();

    /** @return X-coordinate of the claim center or origin. */
    int getX();

    /** @return Z-coordinate of the claim center or origin. */
    int getZ();

    /** @return Name of the world where the claim exists. */
    String getWorldName();

    /** @return List of UUIDs representing members added to the claim. */
    List<UUID> getMembers();

    /** @return List of current statuses applied to the claim. */
    List<ClaimStatus> getClaimStatuses();

    /** @return Map of player UUIDs to their associated permissions within the claim. */
    Map<UUID, List<ClaimPermission>> getClaimPermissions();

    /** @return The block location that visually represents the claim. */
    Location getBlockLocation();

    /** @return The material of the block that represents the claim. */
    Material getBlock();

    /** @return Whether the claim’s visual representation block is enabled. */
    boolean isEnableBlock();

    /**
     * Calculates and returns the Y-coordinate for the claim based on a world and X,Z coordinates.
     * @param world The world object.
     * @param x X-coordinate.
     * @param z Z-coordinate.
     * @return The Y-coordinate at the specified location.
     */
    int getYLocation(World world, int x, int z);

    void setID(String ID);
    void setBlockLocation(Location location);
    void setBlock(Material material);
    void setEnableBlock(boolean enableBlock);
    void setDisplayName(String displayName);
    void setOwner(UUID owner);
    void setTimestamp(int timestamp);
    void setX(int x);
    void setZ(int z);
    void setWorldName(String worldName);

    /**
     * Adds to the claim's current timestamp (e.g., extending duration).
     * @param amount The amount of time to add.
     */
    void addTimestamp(int amount);

    /**
     * Removes time from the claim’s timestamp.
     * @param amount The amount of time to remove.
     */
    void removeTimestamp(int amount);

    /**
     * Updates the timestamp by setting a new duration value.
     * @param duration New duration value.
     */
    void updateTimestamp(int duration);

    /** @return The central location of the claim. */
    Location getCenter();

    /** @return Whether the claim has expired based on timestamp. */
    boolean isExpired();

    /** Adds a member to the claim. @param uuid The UUID of the player. */
    void addMember(UUID uuid);

    /** Removes a member from the claim. @param uuid The UUID of the player. */
    void removeMember(UUID uuid);

    void trustPlayer(Player owner, UUID target);

    /** Adds a status to the claim (e.g., locked, under siege). @param claimStatus The status to add. */
    void addClaimStatus(ClaimStatus claimStatus);

    /** Removes a status from the claim. @param claimStatus The status to remove. */
    void removeClaimStatus(ClaimStatus claimStatus);

    /**
     * Checks if a given UUID is the owner of the claim.
     * @param uuid Player UUID to check.
     * @return True if the player is the owner.
     */
    boolean isOwner(UUID uuid);

    /**
     * Checks if a UUID belongs to a member of the claim.
     * @param uuid Player UUID to check.
     * @return True if the player is a member.
     */
    boolean isMember(UUID uuid);

    /**
     * Verifies if a player has a specific permission in the claim.
     * @param uuid Player UUID.
     * @param permission The permission to check.
     * @return True if the player has the permission.
     */
    boolean checkPermission(UUID uuid, ClaimPermission permission);

    /**
     * Checks if the claim has a specific status.
     * @param status The status to check.
     * @return True if the claim has the given status.
     */
    boolean checkStatus(ClaimStatus status);

    /** Grants a specific permission to a player. */
    void addPermission(UUID uuid, ClaimPermission permission);

    /** Revokes a specific permission from a player. */
    void removePermission(UUID uuid, ClaimPermission permission);

    /** Applies an effect to the claim. @param effect The effect to add. */
    void addEffect(Effect effect);

    /** Removes an effect from the claim. @param effect The effect to remove. */
    void removeEffect(Effect effect);

    /** @return List of all active effects applied to the claim. */
    List<ClaimEffect> getEffects();

    /**
     * Retrieves a specific effect instance from the claim.
     * @param effect The effect to retrieve.
     * @return The claim's effect instance.
     */
    ClaimEffect getEffect(Effect effect);

    /**
     * Checks whether a certain effect is applied to the claim.
     * @param effect The effect to check.
     * @return True if the effect is present.
     */
    boolean hasEffect(Effect effect);

    /**
     * Clears all claim effects for a player
     * @param player
     */
    void clearEffects(Player player);

    /**
     * Clears a specific effect for a player
     * @param effect
     * @param player
     */
    void clearEffect(Effect effect, Player player);

    /** @return List of tags associated with the claim. */
    List<ClaimTag> getClaimTags();

    /** Adds a tag to the claim. @param claimTag The tag to add. */
    void addClaimTag(ClaimTag claimTag);

    /** Removes a tag from the claim. @param claimTag The tag to remove. */
    void removeClaimTag(ClaimTag claimTag);

    /** @return List of sub-claims under this claim. */
    List<SubClaim> getSubClaims();

    /** Adds a sub-claim to this claim. */
    void addSubClaim(SubClaim subClaim);

    /** Removes a sub-claim from this claim. */
    void removeSubClaim(SubClaim subClaim);

    /**
     * Checks if the given location is within the boundaries of this claim.
     * @param location The location to check.
     * @return True if the location is inside the claim.
     */
    boolean contains(Location location);

    /**
     * Checks if the given location is within this claim or any of its subclaims.
     * @param location The location to check.
     * @return True if the location is within the claim or subclaims.
     */
    boolean containsWithChild(Location location);

    /** @return The size (area or number of blocks) of the claim. */
    int getSize();

    /**
     * Deletes the claim.
     * @param explode If true, causes a visual or physical explosion effect.
     */
    void delete(boolean explode);

    /** Toggles the state of the claim’s visual block (enabled/disabled). */
    void toggleBlockStatus();

    /**
     * Moves the visual representation block to a new location.
     * @param location The new location to move the block to.
     */
    void moveBlock(Location location);


    /**
     * get all player's in the claim
     * @return a collection of players
     */
    Collection<Player> getAllPlayers();
}
