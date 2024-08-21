package net.weesli.rClaim.api.events;

import net.weesli.rClaim.utils.Claim;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ClaimEnterEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Claim claim;
    private Player player;

    public ClaimEnterEvent(Claim claim, Player player) {
        this.claim = claim;
        this.player = player;
    }

    public Claim getClaim() {
        return claim;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
