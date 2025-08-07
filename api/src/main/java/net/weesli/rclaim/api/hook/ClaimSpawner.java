package net.weesli.rclaim.api.hook;


import net.weesli.rclaim.api.RClaimProvider;
import net.weesli.rclaim.api.model.Claim;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface ClaimSpawner {

    default boolean availableArea(Player player, Location location){
        Chunk chunk = location.getChunk();
        Claim claim = RClaimProvider.getClaimManager().getClaim(chunk);
        if (claim == null) return false;
        return claim.isOwner(player.getUniqueId());
    }

    String getName();
}
