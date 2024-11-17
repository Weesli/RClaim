package net.weesli.rClaim.utils;

import net.weesli.rClaim.modal.Claim;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.persistence.PersistentDataType;

public class ClaimUtils {

    public static Claim getClaim(Location location){
        if (!location.getChunk().getPersistentDataContainer().has(RClaimNameSpaceKey.getKey())){
            return ClaimManager.getClaims().stream().filter(c -> c.contains(location)).findFirst().orElse(null);
        }
        return getClaim(location.getChunk().getPersistentDataContainer().get(RClaimNameSpaceKey.getKey(), PersistentDataType.STRING));
    }

    public static Claim getClaim(Chunk chunk){
        if (!chunk.getPersistentDataContainer().has(RClaimNameSpaceKey.getKey())){
            return ClaimManager.getClaims().stream().filter(c -> c.getChunk().equals(chunk)).findFirst().orElse(null);
        }
        return getClaim(chunk.getPersistentDataContainer().get(RClaimNameSpaceKey.getKey(), PersistentDataType.STRING));
    }

    public static Claim getClaim(String id){
        return ClaimManager.getClaim(id).orElse(null);
    }

}
