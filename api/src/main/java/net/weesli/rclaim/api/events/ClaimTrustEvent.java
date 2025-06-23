package net.weesli.rclaim.api.events;

import lombok.Getter;
import lombok.Setter;
import net.weesli.rclaim.api.model.Claim;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import java.util.UUID;

@Getter@Setter
public class ClaimTrustEvent extends ClaimEvent implements Cancellable {

    private boolean cancelled = false;

    private Claim claim;
    private UUID player;
    private UUID target;

    public ClaimTrustEvent(Claim claim, UUID player, UUID target) {
        this.player = player;
        this.target = target;
        this.claim = claim;
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
