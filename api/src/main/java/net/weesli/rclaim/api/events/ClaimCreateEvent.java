package net.weesli.rclaim.api.events;

import lombok.Getter;
import lombok.Setter;
import net.weesli.rclaim.api.model.Claim;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

@Getter@Setter
public class ClaimCreateEvent extends ClaimEvent implements Cancellable {

    private boolean cancelled = false;

    private Player sender;
    private Claim claim;

    public ClaimCreateEvent(Player sender, Claim claim) {
        this.sender = sender;
        this.claim = claim;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }
}
