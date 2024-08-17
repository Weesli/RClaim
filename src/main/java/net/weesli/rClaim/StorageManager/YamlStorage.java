package net.weesli.rClaim.StorageManager;

import net.weesli.rClaim.RClaim;
import net.weesli.rClaim.tasks.ClaimTask;
import net.weesli.rClaim.utils.Claim;
import net.weesli.rClaim.utils.ClaimPermission;
import net.weesli.rClaim.utils.ClaimStatus;
import net.weesli.rozsLib.ConfigurationManager.YamlFileBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class YamlStorage extends StorageImpl{

    YamlFileBuilder claim_builder = RClaim.getClaim_builder();
    FileConfiguration config_claim = claim_builder.load();

    @Override
    public void insertClaim(Claim claim) {
        config_claim.set("claims." + claim.getID() + ".owner", claim.getOwner().toString());
        config_claim.set("claims." + claim.getID() + ".status", claim.getClaimStatuses().stream().map(ClaimStatus::name).toList());
        config_claim.set("claims." + claim.getID() + ".members", claim.getMembers().stream().map(UUID::toString).toList());
        claim.getClaimPermissions().forEach((key, value) -> {
            config_claim.set("claims." + claim.getID() + ".permissions." + key, value.stream().map(ClaimPermission::name).toList());
        });
        config_claim.set("claims." + claim.getID() + ".chunk.world", claim.getChunk().getWorld().getName());
        config_claim.set("claims." + claim.getID() + ".chunk.x", claim.getChunk().getX() * 16);
        config_claim.set("claims." + claim.getID() + ".chunk.z", claim.getChunk().getZ() * 16);
        claim_builder.save();
    }

    @Override
    public Claim getClaim(String id) {
        if (config_claim.contains("claims." + id)) {
            UUID uuid = UUID.fromString(Objects.requireNonNull(config_claim.getString("claims." + id + ".owner")));
            List<UUID> members = config_claim.getStringList("claims." + id + ".members").stream()
                    .map(UUID::fromString)
                    .collect(Collectors.toCollection(ArrayList::new));
            List<ClaimStatus> statuses = config_claim.getStringList("claims." + id + ".status").stream()
                    .map(ClaimStatus::valueOf)
                    .collect(Collectors.toCollection(ArrayList::new));
            Map<UUID, List<ClaimPermission>> userCache = new HashMap<>();
            if (config_claim.get("claims." + id + ".permissions") != null) {
                config_claim.getConfigurationSection("claims." + id + ".permissions").getKeys(false).forEach(key -> {
                    UUID playerId = UUID.fromString(key);
                    List<ClaimPermission> permissions = config_claim.getStringList("claims." + id + ".permissions." + key).stream()
                            .map(ClaimPermission::valueOf)
                            .collect(Collectors.toCollection(ArrayList::new));
                    userCache.put(playerId, permissions);
                });
            }
            World world = Bukkit.getWorld(Objects.requireNonNull(config_claim.getString("claims." + id + ".chunk.world")));
            Chunk chunk = world.getChunkAt(config_claim.getInt("claims." + id + ".chunk.x") / 16,
                    config_claim.getInt("claims." + id + ".chunk.z") / 16);
            Claim claim = new Claim(id, uuid, members, statuses, chunk);
            claim.setClaimPermissions(userCache);
            return claim;
        }
        return null;
    }

    @Override
    public void updateClaim(Claim claim) {
        config_claim.set("claims." + claim.getID() + ".owner", claim.getOwner().toString());
        config_claim.set("claims." + claim.getID() + ".status", claim.getClaimStatuses().stream().map(ClaimStatus::name).collect(Collectors.toList()));
        config_claim.set("claims." + claim.getID() + ".members", claim.getMembers().stream().map(UUID::toString).collect(Collectors.toList()));
        claim.getClaimPermissions().forEach((key, value) -> {
            config_claim.set("claims." + claim.getID() + ".permissions." + key, value.stream().map(ClaimPermission::name).collect(Collectors.toList()));
        });
        config_claim.set("claims." + claim.getID() + ".chunk.world", claim.getChunk().getWorld().getName());
        config_claim.set("claims." + claim.getID() + ".chunk.x", claim.getChunk().getX() * 16);
        config_claim.set("claims." + claim.getID() + ".chunk.z", claim.getChunk().getZ()* 16);
        claim_builder.save();
    }

    @Override
    public void deleteClaim(String id) {
        config_claim.set("claims." + id, null);
        claim_builder.save();
    }

    @Override
    public boolean hasClaim(String id) {
        return config_claim.contains("claims." + id);
    }

    @Override
    public List<Claim> getClaims() {
        List<Claim> claims = new ArrayList<>();
        for (String id : config_claim.getConfigurationSection("claims").getKeys(false)){
            UUID uuid = UUID.fromString(Objects.requireNonNull(config_claim.getString("claims." + id + ".owner")));
            List<UUID> members = config_claim.getStringList("claims." + id + ".members").stream()
                    .map(UUID::fromString)
                    .collect(Collectors.toCollection(ArrayList::new));
            List<ClaimStatus> statuses = config_claim.getStringList("claims." + id + ".status").stream()
                    .map(ClaimStatus::valueOf)
                    .collect(Collectors.toCollection(ArrayList::new));
            Map<UUID, List<ClaimPermission>> userCache = new HashMap<>();
            if (config_claim.get("claims." + id + ".permissions") != null) {
                config_claim.getConfigurationSection("claims." + id + ".permissions").getKeys(false).forEach(key -> {
                    UUID playerId = UUID.fromString(key);
                    List<ClaimPermission> permissions = config_claim.getStringList("claims." + id + ".permissions." + key).stream()
                            .map(ClaimPermission::valueOf)
                            .collect(Collectors.toCollection(ArrayList::new));
                    userCache.put(playerId, permissions);
                });
            }
            World world = Bukkit.getWorld(Objects.requireNonNull(config_claim.getString("claims." + id + ".chunk.world")));
            Chunk chunk = world.getChunkAt(config_claim.getInt("claims." + id + ".chunk.x") / 16,
                    config_claim.getInt("claims." + id + ".chunk.z") / 16);
            Claim claim = new Claim(id, uuid, members, statuses, chunk);
            claim.setClaimPermissions(userCache);
            claims.add(claim);
        }
        return claims;
    }

    @Override
    public StorageType getStorageType() {
        return StorageType.YAML;
    }
}
