package net.weesli.rclaim.manager;

import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.manager.TagManager;
import net.weesli.rclaim.api.model.Claim;
import net.weesli.rclaim.api.model.ClaimTag;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class TagManagerImpl implements TagManager {

    public TagManagerImpl() {}

    public void addTag(Claim claim, ClaimTag claimTag) {
        claim.addClaimTag(claimTag);
    }

    public void removeTag(String claimId, ClaimTag claimTag) {
        List<ClaimTag> claimTags = RClaim.getInstance().getClaimManager().getClaim(claimId).getClaimTags();
        if (claimTags != null) {
            claimTags.remove(claimTag);
        }
    }

    public List<ClaimTag> getTags(String claimId) {
        return RClaim.getInstance().getClaimManager().getClaim(claimId).getClaimTags();
    }

    public void changeTag(ClaimTag tag) {
        removeTag(tag.getClaimId(), tag);
        addTag(RClaim.getInstance().getClaimManager().getClaim(tag.getClaimId()), tag);
    }

    public ClaimTag isPlayerInTag(Player player, String claimId) {
        List<ClaimTag> claimTags = getTags(claimId);
        for (ClaimTag tag : claimTags) {
            if (tag.getUsers().contains(player.getUniqueId())) {
                return tag;
            }
        }
        return null;
    }

    public ClaimTag isPlayerInTag(UUID uuid, String claimId) {
        List<ClaimTag> claimTag = getTags(claimId);
        for (ClaimTag tag : claimTag) {
            if (tag.getUsers().contains(uuid)) {
                return tag;
            }
        }
        return null;
    }

}
