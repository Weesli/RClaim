package net.weesli.rclaim.api.manager;

import net.weesli.rclaim.api.model.Claim;
import net.weesli.rclaim.api.model.ClaimTag;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

/**
 * Manages {@link ClaimTag} instances for land claims. Tags are used to organize
 * groups of users and their permissions within a specific claim. This interface provides
 * methods to add, remove, query, and update tags.
 */
public interface TagManager {

    /**
     * Adds a tag to the specified claim.
     *
     * @param claim The claim to which the tag will be added.
     * @param tag The {@link ClaimTag} to add.
     */
    void addTag(Claim claim, ClaimTag tag);

    /**
     * Removes a tag from the specified claim.
     *
     * @param claimId The ID of the claim from which to remove the tag.
     * @param tag The {@link ClaimTag} to remove.
     */
    void removeTag(String claimId, ClaimTag tag);

    /**
     * Retrieves all tags associated with a claim.
     *
     * @param claimId The ID of the claim.
     * @return A list of {@link ClaimTag} objects.
     */
    List<ClaimTag> getTags(String claimId);

    /**
     * Checks whether the player (by UUID) is part of any tag within the specified claim.
     *
     * @param uuid The UUID of the player.
     * @param claimId The ID of the claim.
     * @return The {@link ClaimTag} the player belongs to, or null if not found.
     */
    ClaimTag isPlayerInTag(UUID uuid, String claimId);

    /**
     * Checks whether the player is part of any tag within the specified claim.
     *
     * @param player The player to check.
     * @param claimId The ID of the claim.
     * @return The {@link ClaimTag} the player belongs to, or null if not found.
     */
    ClaimTag isPlayerInTag(Player player, String claimId);

    /**
     * Applies changes to an existing tag. This could include renaming, updating users, or modifying permissions.
     *
     * @param tag The updated {@link ClaimTag} object.
     */
    void changeTag(ClaimTag tag);
}
