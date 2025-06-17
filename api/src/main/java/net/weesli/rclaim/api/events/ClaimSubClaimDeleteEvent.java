package net.weesli.rclaim.api.events;

import lombok.Getter;
import lombok.Setter;
import net.weesli.rclaim.api.model.Claim;
import net.weesli.rclaim.api.model.SubClaim;
import org.bukkit.event.Cancellable;
@Getter@Setter
public class ClaimSubClaimDeleteEvent extends ClaimEvent implements Cancellable {

    private Claim ownClaim;
    private SubClaim subClaim;
    private boolean cancelled;

    public ClaimSubClaimDeleteEvent(Claim claim, SubClaim subClaim) {
        this.ownClaim = claim;
        this.subClaim = subClaim;

    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
