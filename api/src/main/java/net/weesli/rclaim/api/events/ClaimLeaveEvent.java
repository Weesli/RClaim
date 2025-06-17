package net.weesli.rclaim.api.events;

import lombok.Getter;
import lombok.Setter;
import net.weesli.rclaim.api.model.Claim;
import org.bukkit.entity.Player;

@Getter@Setter
public class ClaimLeaveEvent extends ClaimEvent {

    private Claim claim;
    private Player player;

    public ClaimLeaveEvent(Claim claim, Player player) {
        this.claim = claim;
        this.player = player;
    }
}
