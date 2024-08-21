package net.weesli.rClaim.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class TrustedPlayerEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;

    private Player truster;
    private Player trusted;

    public TrustedPlayerEvent(Player truster, Player trusted) {
        this.truster = truster;
        this.trusted = trusted;
    }

    public Player getTruster() {
        return truster;
    }

    public Player getTrusted() {
        return trusted;
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
}
