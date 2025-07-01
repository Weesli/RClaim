package net.weesli.rclaim.api.events;

import lombok.Getter;
import lombok.Setter;
import net.weesli.rclaim.api.enums.ExplodeCause;
import net.weesli.rclaim.api.model.Claim;
import org.bukkit.event.Cancellable;

@Getter@Setter
public class ClaimDeleteEvent extends ClaimEvent implements Cancellable {

    private boolean cancelled = false;

    private Claim claim;
    private ExplodeCause cause;

    public ClaimDeleteEvent(Claim claim, ExplodeCause cause) {
        this.claim = claim;
        this.cause = cause;
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
