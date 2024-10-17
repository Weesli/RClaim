package net.weesli.rClaim.utils;

import lombok.Getter;
import net.weesli.rClaim.modal.Claim;
import net.weesli.rClaim.modal.ClaimTag;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * TagManager handles the management of ClaimTags including adding, removing, and checking tags for specific claims.
 */
public class TagManager {

    /**
     * Adds a ClaimTag to the specified claim.
     *
     * @param claimTag The ClaimTag to be added.
     */
    public static void addTag(Claim claim, ClaimTag claimTag) {
        claim.addClaimTag(claimTag);
    }

    /**
     * Removes a ClaimTag from the specified claim.
     *
     * @param claimId  The ID of the claim from which the tag is to be removed.
     * @param claimTag The ClaimTag to be removed.
     */
    public static void removeTag(String claimId, ClaimTag claimTag) {
        List<ClaimTag> claimTags = ClaimManager.getClaim(claimId).get().getClaimTags();
        if (claimTags != null) {
            claimTags.remove(claimTag);
        }
    }

    /**
     * Retrieves the list of ClaimTags for the specified claim.
     *
     * @param claimId The ID of the claim.
     * @return A list of ClaimTags associated with the claim.
     */
    public static List<ClaimTag> getTags(String claimId) {
        return ClaimManager.getClaim(claimId).get().getClaimTags();
    }

    /**
     * Changes an existing ClaimTag by removing the old one and adding the new one.
     *
     * @param tag The ClaimTag to be changed.
     */
    public static void changeTag(ClaimTag tag) {
        removeTag(tag.getClaimId(), tag);
        addTag(ClaimManager.getClaim(tag.getClaimId()).get(), tag);
    }

    /**
     * Checks if a player is in any of the tags associated with the specified claim.
     *
     * @param player  The player to be checked.
     * @param claimId The ID of the claim.
     * @return The ClaimTag that the player is in, or null if not found.
     */
    public static ClaimTag isPlayerInTag(Player player, String claimId) {
        List<ClaimTag> claimTags = getTags(claimId);
        for (ClaimTag tag : claimTags) {
            if (tag.getUsers().contains(player.getUniqueId())) {
                return tag;
            }
        }
        return null;
    }

    /**
     * Checks if a player (by UUID) is in any of the tags associated with the specified claim.
     *
     * @param uuid    The UUID of the player to be checked.
     * @param claimId The ID of the claim.
     * @return The ClaimTag that the player is in, or null if not found.
     */
    public static ClaimTag isPlayerInTag(UUID uuid, String claimId) {
        List<ClaimTag> claimTags = getTags(claimId);
        for (ClaimTag tag : claimTags) {
            if (tag.getUsers().contains(uuid)) {
                return tag;
            }
        }
        return null;
    }
}
