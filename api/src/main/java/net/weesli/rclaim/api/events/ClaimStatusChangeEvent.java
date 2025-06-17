package net.weesli.rclaim.api.events;

import lombok.Getter;
import lombok.Setter;
import net.weesli.rclaim.api.enums.ClaimStatus;
import net.weesli.rclaim.api.model.Claim;
import org.bukkit.entity.Player;

@Getter@Setter
public class ClaimStatusChangeEvent extends ClaimEvent {


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
}
