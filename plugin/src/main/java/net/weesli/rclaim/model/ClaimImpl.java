package net.weesli.rclaim.model;

import lombok.Getter;
import lombok.Setter;
import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.events.ClaimDeleteEvent;
import net.weesli.rclaim.api.events.ClaimTrustEvent;
import net.weesli.rclaim.api.events.ClaimUnTrustEvent;
import net.weesli.rclaim.api.model.Claim;
import net.weesli.rclaim.api.model.ClaimEffect;
import net.weesli.rclaim.api.model.ClaimTag;
import net.weesli.rclaim.api.model.SubClaim;
import net.weesli.rclaim.config.ConfigLoader;
import net.weesli.rclaim.api.enums.ClaimPermission;
import net.weesli.rclaim.api.enums.ClaimStatus;
import net.weesli.rclaim.api.enums.Effect;
import net.weesli.rclaim.api.enums.ExplodeCause;
import net.weesli.rclaim.util.BaseUtil;
import net.weesli.rozslib.database.annotation.PrimaryKey;
import org.bukkit.*;
import org.bukkit.block.Block;

import java.util.*;

@Setter
public class ClaimImpl implements Claim {

    @PrimaryKey
    private String ID;
    private String displayName;
    private UUID owner;
    private List<UUID> members;
    private List<ClaimStatus> claimStatuses;
    private int x,z;
    private String worldName;
    private Map<UUID, List<ClaimPermission>> claimPermissions = new HashMap<>();
    private List<ClaimEffectImpl> effects;
    private Material block;
    private boolean enableBlock;
    private Location blockLocation;
    private List<ClaimTagImpl> claimTags;
    private List<SubClaimImpl> subClaims;
    private int timestamp;

    public ClaimImpl(){

    }
    public ClaimImpl(String ID, UUID owner, List<UUID> members, List<ClaimStatus> claimStatuses, int x, int z, String worldName) {
        this.ID = ID;
        this.owner = owner;
        this.members = members;
        this.claimStatuses = claimStatuses;
        effects = new ArrayList<>();
        block = Material.BEDROCK;
        claimTags = new ArrayList<>();
        subClaims = new ArrayList<>();
        this.x = x;
        this.z = z;
        this.worldName = worldName;
        timestamp = BaseUtil.getSec(ConfigLoader.getConfig().getClaimSettings().getClaimDuration());
        this.enableBlock = true;
    }

    public boolean contains(Location location) {
        return location.getX() >= x && location.getX() < x + 16 && location.getZ() >= z && location.getZ() < z + 16;
    }
    public boolean containsWithChild(Location location){
        for (SubClaim subClaimImpl : subClaims) {
            if (subClaimImpl.contains(location)) {
                return true;
            }
        }
        return location.getX() >= x && location.getX() < x + 16 && location.getZ() >= z && location.getZ() < z + 16;
    }

    public boolean isMember(UUID uuid) {
        return members.contains(uuid);
    }

    public boolean isOwner(UUID uuid) {
        return owner.equals(uuid);
    }

    public Location getCenter() {
        int centerX = x + 8;
        int centerZ = z + 8;
        return new Location(Bukkit.getWorld(worldName),  centerX, getYLocation(Bukkit.getWorld(worldName), centerX , centerZ), centerZ);
    }

    public int getYLocation(World world, int x, int z) {
        int y = world.getHighestBlockYAt(x, z);
        Block block = world.getBlockAt(x, y, z);

        while (block.getType().name().contains("LEAVES") ||
                block.getType().name().contains("SLAB") ||
                block.isPassable()) {
            y--;
            block = world.getBlockAt(x, y, z);
        }
        return (int) block.getLocation().getY();
    }

    public boolean checkPermission(UUID uuid, ClaimPermission permission) {
        if (!members.contains(uuid)) {
            return false;
        }
        List<ClaimPermission> permissions = claimPermissions.getOrDefault(uuid, new ArrayList<>());
        return permissions.contains(permission);
    }

    public void addPermission(UUID uuid, ClaimPermission permission) {
        List<ClaimPermission> permissions = claimPermissions.getOrDefault(uuid, new ArrayList<>());
        permissions.add(permission);
        claimPermissions.put(uuid, permissions);
    }

    public void removePermission(UUID uuid, ClaimPermission permission) {
        List<ClaimPermission> permissions = claimPermissions.getOrDefault(uuid, new ArrayList<>());
        permissions.remove(permission);
        claimPermissions.put(uuid, permissions);
    }

    public void addClaimStatus(ClaimStatus status) {
        claimStatuses.add(status);
    }

    public void removeClaimStatus(ClaimStatus status) {
        claimStatuses.remove(status);
    }

    public boolean checkStatus(ClaimStatus status) {
        return claimStatuses.contains(status);
    }

    public void addMember(UUID uuid) {
        ClaimTrustEvent event = new ClaimTrustEvent(owner, uuid);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()){
            members.add(uuid);
        }
    }

    public void removeMember(UUID uuid) {
        ClaimUnTrustEvent event = new ClaimUnTrustEvent(owner, uuid);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()){
            members.removeIf(u -> u.equals(uuid));

        }
    }

    public void addEffect(Effect effect) {
        effects.add(new ClaimEffectImpl(effect, 1, effect.getMaxLevel(), true));
    }

    public void removeEffect(Effect effect) {
        effects.removeIf(e -> e.getEffect().equals(effect));
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ClaimEffect> getEffects() {
        return (List<ClaimEffect>)(List<?>) effects;
    }

    public boolean hasEffect(Effect effect) {
        return effects.stream().anyMatch(e -> e.getEffect().equals(effect));
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ClaimTag> getClaimTags() {
        return (List<ClaimTag>)(List<?>) claimTags;
    }

    public ClaimEffect getEffect(Effect effect){
        return effects.stream().filter(e -> e.getEffect().equals(effect)).findFirst().orElse(null);
    }

    public void setBlock(Material material){
        this.block = material;
        Block blockLocation = getBlockLocation().getBlock();
        blockLocation.setType(material);
    }

    public void addClaimTag(ClaimTag claimTag) {
        claimTags.add((ClaimTagImpl) claimTag);
    }

    public void removeClaimTag(ClaimTag claimTag) {
        claimTags.remove(claimTag);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<SubClaim> getSubClaims() {
        return (List<SubClaim>)(List<?>) subClaims;
    }

    public void addSubClaim(SubClaim subClaim) {
        subClaims.add((SubClaimImpl) subClaim);
    }

    public void removeSubClaim(SubClaim subClaim) {
        subClaims.remove(subClaim);
    }

    public void updateTimestamp(int duration) {
        timestamp = duration;
    }

    public void addTimestamp(int amount){
        timestamp += amount;
    }
    public void removeTimestamp(int amount){
        timestamp -= amount;
    }

    public boolean isExpired() {
        return BaseUtil.getSec(timestamp) <= 0;
    }

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public UUID getOwner() {
        return owner;
    }

    @Override
    public int getTimestamp() {
        return timestamp;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getZ() {
        return z;
    }

    @Override
    public String getWorldName() {
        return worldName;
    }

    @Override
    public List<UUID> getMembers() {
        return members;
    }

    @Override
    public List<ClaimStatus> getClaimStatuses() {
        return claimStatuses;
    }

    @Override
    public Map<UUID, List<ClaimPermission>> getClaimPermissions() {
        return claimPermissions;
    }

    public Location getBlockLocation(){
        if (blockLocation == null){
            return getCenter();
        }
        return blockLocation;
    }

    @Override
    public Material getBlock() {
        return block;
    }

    @Override
    public boolean isEnableBlock() {
        return enableBlock;
    }

    public void moveBlock(Location location) {
        Block lastBlockPosition = getBlockLocation().getBlock();
        lastBlockPosition.setType(Material.AIR);
        blockLocation = location;
        blockLocation.getBlock().setType(block);
    }

    public void toggleBlockStatus(){
        this.enableBlock = !this.enableBlock;
        Block block = getBlockLocation().getBlock();
        block.setType(enableBlock ? block.getType() : Material.AIR);
    }

    public void delete(boolean explode){
        if (explode){
           RClaim.getInstance().getClaimManager().explodeClaim(getID(), ExplodeCause.ADMIN);
        }else {
            ClaimDeleteEvent event = new ClaimDeleteEvent(this,ExplodeCause.ADMIN);
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                RClaim.getInstance().getClaimManager().removeClaim(this);
                RClaim.getInstance().getStorage().deleteClaim(getID());
            }
        }
    }

    public int getSize() {
        return (getSubClaims().size() + 1) * 256;
    }
}