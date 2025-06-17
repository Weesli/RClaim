package net.weesli.rclaim.hook.minion;


import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.model.Claim;
import net.weesli.rclaim.model.ClaimImpl;
import net.weesli.rclaim.util.NameSpaceUtil;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface IClaimMinion {

    default boolean availableArea(Player player, Location location){
        Chunk chunk = location.getChunk();
        if (!chunk.getPersistentDataContainer().has(NameSpaceUtil.getKey())){
            return false;
        }
        Claim claim = RClaim.getInstance().getClaimManager().getClaim(chunk);
        return claim.isOwner(player.getUniqueId());
    }
    String getName();
}
