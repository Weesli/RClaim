package net.weesli.rClaim.modal;

import lombok.Getter;
import lombok.Setter;
import net.weesli.rClaim.utils.ClaimManager;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter@Setter
public class ClaimPlayer {


    private UUID uuid;
    private String username;

    public ClaimPlayer(UUID uuid, String username) {
        this.uuid = uuid;
        this.username = username;
    }

    public List<Claim> getClaims() {
        return ClaimManager.getClaims().stream().filter(claim -> claim.isOwner(uuid)).collect(Collectors.toList());
    }


}
