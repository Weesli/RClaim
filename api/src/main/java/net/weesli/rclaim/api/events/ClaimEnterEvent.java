package net.weesli.rclaim.api.events;

import lombok.Getter;
import lombok.Setter;
import net.weesli.rclaim.api.model.Claim;
import org.bukkit.entity.Player;

@Getter@Setter
public class ClaimEnterEvent extends ClaimEvent {

    private Claim claim;
    private Player player;

    public ClaimEnterEvent(Claim claim, Player player) {
        this.claim = claim;
        this.player = player;
    }
}
