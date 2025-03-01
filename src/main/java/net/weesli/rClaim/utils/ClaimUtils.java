package net.weesli.rClaim.utils;

import net.weesli.rClaim.modal.Claim;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.persistence.PersistentDataType;

public class ClaimUtils {

    public static Claim getClaim(Location location){
        return getClaim(location.getChunk().getPersistentDataContainer().get(RClaimNameSpaceKey.getKey(), PersistentDataType.STRING));
    }

    public static Claim getClaim(Chunk chunk){
        return getClaim(chunk.getPersistentDataContainer().get(RClaimNameSpaceKey.getKey(), PersistentDataType.STRING));
    }

    public static Claim getClaim(String id){
        return ClaimManager.getClaim(id).orElse(null);
    }

}
