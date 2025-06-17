package net.weesli.rclaim.api.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ClaimEvent extends Event {

    private static final HandlerList handlers = new HandlerList();


    public ClaimEvent(){
        super(false);
    }

    public ClaimEvent(boolean async){
        super(async);
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
