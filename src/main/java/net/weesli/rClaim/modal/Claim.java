package net.weesli.rClaim.modal;

import net.weesli.rClaim.enums.ClaimPermission;
import net.weesli.rClaim.enums.ClaimStatus;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.*;

public class Claim {

    private String ID;
    private UUID owner;
    private List<UUID> members;
    private List<ClaimStatus> claimStatuses;
    private Chunk chunk;
    private Map<UUID, List<ClaimPermission>> claimPermissions = new HashMap<>();
    private Location location;
    private boolean isCenter;
    private String centerId;

    public Claim(String ID, UUID owner, List<UUID> members, List<ClaimStatus> claimStatuses, Chunk chunk, boolean isCenter) {
        this.ID = ID;
        this.owner = owner;
        this.members = members;
        this.claimStatuses = claimStatuses;
        this.chunk = chunk;
        this.isCenter = isCenter;
    }

    public String getID() {
        return ID;
    }

    public UUID getOwner() {
        return owner;
    }

    public List<UUID> getMembers() {
        return members;
    }

    public List<ClaimStatus> getClaimStatuses() {
        return claimStatuses;
    }

    public Chunk getChunk() {
        return chunk;
    }

    public boolean contains(Location location) {
        int x = chunk.getX() * 16;
        int z = chunk.getZ() * 16;
        return location.getX() >= x && location.getX() < x + 16 && location.getZ() >= z && location.getZ() < z + 16;
    }

    public boolean isMember(UUID uuid) {
        return members.contains(uuid);
    }

    public boolean isOwner(UUID uuid) {
        return owner.equals(uuid);
    }

    public Location getCenter() {
        int x = chunk.getX() * 16 + 8;
        int z = chunk.getZ() * 16 + 8;
        return new Location(chunk.getWorld(), x, getYLocation(chunk.getWorld(), x, z), z);
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

    public int getX() {
        return chunk.getX() * 16;
    }

    public int getZ() {
        return chunk.getZ() * 16;
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
        members.add(uuid);
    }

    public void removeMember(UUID uuid) {
        members.removeIf(member -> member.equals(uuid));
    }

    public Map<UUID, List<ClaimPermission>> getClaimPermissions() {
        return claimPermissions;
    }

    public void setClaimPermissions(Map<UUID, List<ClaimPermission>> claimPermissions) {
        this.claimPermissions = claimPermissions;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public Location getHomeLocation() {
        return location;
    }

    public void setHomeLocation(Location location) {
        this.location = location;
    }

    public boolean isCenter() {
        return isCenter;
    }

    public void setCenter(boolean isCenter) {
        this.isCenter = isCenter;
    }

    public String getCenterId() {
        if (centerId == null){
            return "";
        }
        return centerId;
    }

    public void setCenterId(String centerId) {
        this.centerId = centerId;
    }
}