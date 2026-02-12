package net.weesli.rclaim.model;

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
import net.weesli.rclaim.api.enums.Effect;
import net.weesli.rclaim.api.enums.ExplodeCause;
import net.weesli.rclaim.util.BaseUtil;
import net.weesli.rclaim.util.PermissionUtil;
import net.weesli.rozslib.database.annotation.PrimaryKey;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;

import static net.weesli.rclaim.config.lang.LangConfig.sendMessageToPlayer;

@Setter
public class ClaimImpl implements Claim {

    @PrimaryKey
    private String ID;
    private String displayName;
    private UUID owner;
    private List<UUID> members;
    private List<String> claimStatuses;
    private int x,z;
    private transient World world;
    private Map<UUID, List<String>> claimPermissions = new HashMap<>();
    private List<ClaimEffectImpl> effects;
    private Material block;
    private boolean enableBlock;
    private Location blockLocation;
    private List<ClaimTagImpl> claimTags;
    private List<SubClaimImpl> subClaims;
    private int timestamp;

    public ClaimImpl(){

    }

    public ClaimImpl(String ID, UUID owner, List<UUID> members, List<String> claimStatuses, int x, int z, World world) {
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
        this.world = world;
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
        return new Location(world,  centerX, getYLocation(world, centerX , centerZ), centerZ);
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

    public boolean checkPermission(UUID uuid, String key) {
        if (!members.contains(uuid)) {
            return false;
        }
        List<String> permissions = claimPermissions.getOrDefault(uuid, new ArrayList<>());
        return permissions.contains(key);
    }

    public void addPermission(UUID uuid, String key) {
        List<String> permissions = claimPermissions.getOrDefault(uuid, new ArrayList<>());
        permissions.add(key);
        claimPermissions.put(uuid, permissions);
    }

    public void removePermission(UUID uuid, String key) {
        List<String> permissions = claimPermissions.getOrDefault(uuid, new ArrayList<>());
        permissions.remove(key);
        claimPermissions.put(uuid, permissions);
    }

    public void addClaimStatus(String key) {
        claimStatuses.add(key);
    }

    public void removeClaimStatus(String key) {
        claimStatuses.remove(key);
    }

    public boolean checkStatus(String key) {
        return claimStatuses.contains(key);
    }

    public void addMember(UUID uuid) {
        ClaimTrustEvent event = new ClaimTrustEvent(this,owner, uuid);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()){
            members.add(uuid);
        }
    }

    @Override
    public void trustPlayer(Player owner, UUID target) {
        if (members.contains(target)) {
            sendMessageToPlayer("ALREADY_TRUSTED_PLAYER", owner);
            return;
        }
        if (owner.getUniqueId().equals(target)) {
            int maxTrustablePlayer = PermissionUtil.getMemberLimit(owner);
            if (members.size() >= maxTrustablePlayer) {
                sendMessageToPlayer("MAX_TRUSTED_PLAYERS", owner);
                return;
            }
        }
        addMember(target);
        sendMessageToPlayer("TRUSTED_PLAYER", owner);
    }

    public void removeMember(UUID uuid) {
        ClaimUnTrustEvent event = new ClaimUnTrustEvent(this,owner, uuid);
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

    @Override
    public void clearEffect(Effect effect, Player player) {
        if (player.hasPotionEffect(effect.getType())) {
            player.removePotionEffect(effect.getType());
        }
    }

    @Override
    public void clearEffects(Player player) {
        for (ClaimEffect effect : effects) {
            if (player.hasPotionEffect(effect.getEffect().getType())) {
                player.removePotionEffect(effect.getEffect().getType());
            }
        }
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

    @Override
    public void setWorldName(String worldName) {
        this.world = Bukkit.getWorld(worldName);
    }

    public void addClaimTag(ClaimTag claimTag) {
        claimTags.add((ClaimTagImpl) claimTag);
    }

    public void removeClaimTag(ClaimTag claimTag) {
        claimTags.remove((ClaimTagImpl) claimTag);
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
        subClaims.remove((SubClaimImpl) subClaim);
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
        return timestamp <= 0;
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
        if (world == null){
            this.world = blockLocation.getWorld();
        }
        return world.getName();
    }

    @Override
    public List<UUID> getMembers() {
        return members;
    }

    @Override
    public List<String> getClaimStatuses() {
        return claimStatuses;
    }

    @Override
    public Map<UUID, List<String>> getClaimPermissions() {
        return claimPermissions;
    }

    public Location getBlockLocation(){
        if (blockLocation == null){
            blockLocation = getCenter();
            return blockLocation;
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
        if (enableBlock){
            RClaim.getInstance().getHologramManager().getHologramIntegration().createHologram(getID());
            Block block = getBlockLocation().getBlock();
            block.setType(getBlock());
        }else {
            RClaim.getInstance().getHologramManager().getHologramIntegration().deleteHologram(getID());
            Block block = getBlockLocation().getBlock();
            block.setType(Material.AIR);
        }
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

    @Override
    public Collection<Player> getAllPlayers() {
        Set<Player> players = new HashSet<>();
        World world = getBlockLocation().getWorld();
        Set<Chunk> chunks = new HashSet<>();

        for (SubClaim subClaim : getSubClaims()) {
            int chunkX = subClaim.getX();
            int chunkZ = subClaim.getZ();
            Chunk chunk = world.getChunkAt(chunkX, chunkZ);
            if (chunk.isLoaded()) {
                chunks.add(chunk);
            }
        }

        int blockX = getX();
        int blockZ = getZ();
        Chunk mainChunk = world.getChunkAt(blockX >> 4, blockZ >> 4);
        if (mainChunk.isLoaded()) {
            chunks.add(mainChunk);
        }

        for (Chunk chunk : chunks) {
            for (Entity entity : chunk.getEntities()) {
                if (entity instanceof Player) {
                    players.add((Player) entity);
                }
            }
        }
        return players;
    }

}