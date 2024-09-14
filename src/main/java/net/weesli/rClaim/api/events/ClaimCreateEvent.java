package net.weesli.rClaim.api.events;

import net.weesli.rClaim.modal.Claim;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ClaimCreateEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private boolean cancelled = false;

    private Player sender;
    private Claim claim;

    public ClaimCreateEvent(Player sender, Claim claim) {
        this.sender = sender;
        this.claim = claim;
    }

    public Player getSender() {
        return sender;
    }

    public Claim getClaim() {
        return claim;
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
        cancelled = cancel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
