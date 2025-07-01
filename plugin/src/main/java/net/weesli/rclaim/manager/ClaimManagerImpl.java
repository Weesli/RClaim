package net.weesli.rclaim.manager;

import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.enums.ClaimPermission;
import net.weesli.rclaim.api.events.ClaimCreateEvent;
import net.weesli.rclaim.api.events.ClaimDeleteEvent;
import net.weesli.rclaim.api.events.ClaimSubClaimCreateEvent;
import net.weesli.rclaim.api.manager.ClaimManager;
import net.weesli.rclaim.api.model.Claim;
import net.weesli.rclaim.api.model.ClaimEffect;
import net.weesli.rclaim.api.model.ClaimTag;
import net.weesli.rclaim.api.model.SubClaim;
import net.weesli.rclaim.config.ConfigLoader;
import net.weesli.rclaim.api.enums.ClaimStatus;
import net.weesli.rclaim.api.enums.ExplodeCause;
import net.weesli.rclaim.model.ClaimEffectImpl;
import net.weesli.rclaim.model.ClaimImpl;
import net.weesli.rclaim.model.SubClaimImpl;
import net.weesli.rclaim.util.BaseUtil;
import net.weesli.rclaim.util.NameSpaceUtil;
import net.weesli.rozslib.color.ColorBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static net.weesli.rclaim.util.BaseUtil.generateId;

public class ClaimManagerImpl implements ClaimManager {

    public Claim getClaim(Location location){
        return getClaim(location.getChunk().getPersistentDataContainer().get(NameSpaceUtil.getKey(), PersistentDataType.STRING));
    }

    public Claim getClaim(Chunk chunk){
        return getClaim(chunk.getPersistentDataContainer().get(NameSpaceUtil.getKey(), PersistentDataType.STRING));
    }

    public Claim getClaim(String id){
        return RClaim.getInstance().getCacheManager().getClaims().get(id);
    }

    public boolean createSubClaim(Player player, Claim claim, Location location){
        Chunk chunk = location.getChunk();
        SubClaim subClaim = new SubClaimImpl(claim.getID(), chunk.getX(), chunk.getZ());
        ClaimSubClaimCreateEvent createEvent = new ClaimSubClaimCreateEvent(claim,location,player);
        RClaim.getInstance().getServer().getPluginManager().callEvent(createEvent);
        if(createEvent.isCancelled()){
            return false;
        }
        chunk.getPersistentDataContainer().set(
                NameSpaceUtil.getKey(),
                PersistentDataType.STRING,
                claim.getID()
        );
        claim.addSubClaim(subClaim);
        return true;
    }

    public void createClaim(Chunk chunk, Player owner) {
        boolean hasLimit = BaseUtil.checkPlayerClaimLimit(owner); // if player has limit for create claim
        if(!hasLimit) return;
        boolean isBetweenAnyClaim = BaseUtil.isBetweenAnyClaim(chunk); // if chunk is between any claim
        if (!isBetweenAnyClaim) {
            owner.sendMessage(RClaim.getInstance().getMessage("VERY_CLOSE_TO_ANOTHER_CLAIM"));
            return;
        }
        String id = generateId();
        Claim claim = new ClaimImpl(id, owner.getUniqueId(), new ArrayList<>() , new ArrayList<>(), chunk.getX() * 16,chunk.getZ() * 16, chunk.getWorld().getName());
        ClaimCreateEvent event = new ClaimCreateEvent(owner, claim);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            RClaim.getInstance().getCacheManager().getClaims().addClaim(claim);
            chunk.getPersistentDataContainer().set(
                    NameSpaceUtil.getKey(),
                    PersistentDataType.STRING,
                    claim.getID()
            );
            Block block = owner.getWorld().getBlockAt(claim.getCenter().add(0,1,0));

            block.setType(Material.BEDROCK);

            ConfigLoader.getConfig().getClaimSettings().getDefaultClaimStatus().getDeclaration().getFields()
                    .forEach(value -> {
                        if (ConfigLoader.getConfig().getClaimSettings().getDefaultClaimStatus().get(value.getName(), Boolean.class)) {
                            ClaimStatus status = ClaimStatus.valueOf(value.getName());
                            claim.addClaimStatus(status);
                        }
                    });
            owner.sendMessage(RClaim.getInstance().getMessage("SUCCESS_CLAIM_CREATED"));
        }

    }

    public void removeClaim(Claim claim) {
        Chunk chunk = Bukkit.getWorld(claim.getWorldName()).getChunkAt(claim.getX(), claim.getZ()); // get base claim chunk for remove
        chunk.getPersistentDataContainer().remove(NameSpaceUtil.getKey());
        claim.getSubClaims().forEach(subClaim -> {
            Chunk subClaimChunk = chunk.getWorld().getChunkAt(subClaim.getX(),subClaim.getZ());
            subClaimChunk.getPersistentDataContainer().remove(NameSpaceUtil.getKey());
        });
        RClaim.getInstance().getCacheManager().getClaims().getCache().remove(claim.getID());
    }

    public void explodeClaim(String ID, ExplodeCause cause){
        Claim claim = getClaim(ID);
        if (claim == null){
            return;
        }
        ClaimDeleteEvent event = new ClaimDeleteEvent(claim,cause);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()){
            removeClaim(claim);
            RClaim.getInstance().getStorage().deleteClaim(ID);
            if (ConfigLoader.getConfig().getClaimTimeoutMessage().isEnabled()){
                ConfigLoader.getConfig().getClaimTimeoutMessage().getText()
                        .stream().map(line-> ColorBuilder.convertColors(line)
                                .replaceAll("%player%", Bukkit.getPlayer(claim.getOwner()).getName())
                                .replaceAll("%x%", String.valueOf(claim.getX()))
                                .replaceAll("%z%", String.valueOf(claim.getZ()))
                                .replaceAll("%world%", claim.getWorldName())
                        ).forEach(Bukkit::broadcastMessage);
            }
        }
    }

    public boolean isSuitable(Chunk chunk){
        Claim claim = getClaim(chunk);
        return claim != null;
    }

    public boolean isSuitable(Location location){
        Claim claim = getClaim(location);
        return claim != null;
    }
}
