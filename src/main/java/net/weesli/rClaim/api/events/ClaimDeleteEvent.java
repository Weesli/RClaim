package net.weesli.rClaim.api.events;

import net.weesli.rClaim.enums.ExplodeCause;
import net.weesli.rClaim.modal.Claim;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ClaimDeleteEvent extends Event implements Cancellable {


    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;

    private Claim claim;
    private ExplodeCause cause;
    private boolean isCenter;

    public ClaimDeleteEvent(Claim claim, ExplodeCause cause, boolean isCenter) {
        this.claim = claim;
        this.cause = cause;
        this.isCenter = isCenter;
    }

    public Claim getClaim() {
        return claim;
    }

    public ExplodeCause getCause() {
        return cause;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public boolean isCenter() {
        return isCenter;
    }

    public void setCenter(boolean center) {
        isCenter = center;
    }
}
