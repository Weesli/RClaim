package net.weesli.rClaim.api.events;

import lombok.Getter;
import lombok.Setter;
import net.weesli.rClaim.enums.ClaimStatus;
import net.weesli.rClaim.modal.Claim;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
@Getter@Setter
public class ClaimStatusChangeEvent extends Event {

     private static final HandlerList handlers = new HandlerList();

    private Claim claim;
    private ClaimStatus status;
    private Player player;
    private boolean changeStatus;

    public ClaimStatusChangeEvent(Player player, Claim claim, ClaimStatus status, boolean changeStatus) {
        this.player = player;
        this.claim = claim;
        this.status = status;
        this.changeStatus = changeStatus;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
