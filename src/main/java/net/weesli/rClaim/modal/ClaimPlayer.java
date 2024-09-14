package net.weesli.rClaim.modal;

import net.weesli.rClaim.utils.ClaimManager;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ClaimPlayer {


    private UUID uuid;
    private String username;

    public ClaimPlayer(UUID uuid, String username) {
        this.uuid = uuid;
        this.username = username;
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    public List<Claim> getClaims() {
        return ClaimManager.getClaims().stream().filter(claim -> claim.isOwner(uuid)).collect(Collectors.toList());
    }


}
