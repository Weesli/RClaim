package net.weesli.rclaim.api.events;

import lombok.Getter;
import lombok.Setter;
import net.weesli.rclaim.api.model.Claim;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
@Getter@Setter
public class ClaimSubClaimCreateEvent extends ClaimEvent implements Cancellable {

    private Claim ownClaim;
    private Location location;
    private Player player;
    private boolean cancelled;

    public ClaimSubClaimCreateEvent(Claim claim, Location location, Player player) {
        this.ownClaim = claim;
        this.location = location;
        this.player = player;
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
