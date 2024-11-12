package net.weesli.rClaim.database;

import com.google.common.reflect.TypeToken;
import lombok.SneakyThrows;
import net.weesli.rClaim.enums.ClaimPermission;
import net.weesli.rClaim.enums.ClaimStatus;
import net.weesli.rClaim.gson.GsonProvider;
import net.weesli.rClaim.modal.Claim;
import net.weesli.rClaim.modal.ClaimEffect;
import net.weesli.rClaim.modal.ClaimTag;
import net.weesli.rClaim.tasks.ClaimTask;
import net.weesli.rClaim.utils.ClaimManager;
import net.weesli.rozsLib.database.component.Insert;
import net.weesli.rozsLib.database.component.Result;
import net.weesli.rozsLib.database.component.Update;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractDatabase implements Database{

    private Connection connection;

    public AbstractDatabase(Connection connection){
        this.connection = connection;
        connect();
    }

    @Override
    public void connect() {
        createTable();
    }

    @SneakyThrows
    private void createTable(){
        try (Statement statement = connection.createStatement()){
            statement.execute(getTableQuery());
        }
    }
    @Override
    public void insertClaim(Claim claim) {
        String formatted_permissions = claim.getClaimPermissions().entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue().stream()
                        .map(ClaimPermission::name)
                        .collect(Collectors.joining(","))).collect((Collectors.joining(";")));
        Optional<Integer> time = ClaimManager.getTasks().stream().filter(task-> task.getClaimId().equals(claim.getID())).map(ClaimTask::getTime).findFirst();
        String location = "";
        if (claim.getHomeLocation() != null){
            location = claim.getHomeLocation().getWorld().getName()+ ":" + claim.getHomeLocation().getX() + ":" + claim.getHomeLocation().getY() + ":" + claim.getHomeLocation().getZ()+":"+claim.getHomeLocation().getPitch() + ":" + claim.getHomeLocation().getYaw();
        }
        List<String> effect = (claim.getEffects() != null) ?
                claim.getEffects().stream()
                        .map(claimEffect -> GsonProvider.getGson().toJson(claimEffect))
                        .toList() :
                Collections.emptyList();
        String tagsJsonArray = GsonProvider.getGson().toJson(claim.getClaimTags());
        Insert insert = new Insert(connection,"rclaims_claims", Arrays.asList("id", "owner", "members", "claim_statues", "chunk", "permissions", "time", "home", "isCenter", "centerId", "effects", "block", "tags"), Arrays.asList(claim.getID(),claim.getOwner().toString(), claim.getMembers().stream().map(UUID::toString).collect(Collectors.toList()).toString(),claim.getClaimStatuses().stream().map(ClaimStatus::name).collect(Collectors.toList()).toString(), claim.getChunk().getWorld().getName() + ":" + claim.getChunk().getX() + ":" + claim.getChunk().getZ(), formatted_permissions, time.get(), location, claim.isCenter(), claim.getCenterId(), effect.toString(), claim.getBlock().name(), tagsJsonArray));
        insert.execute();
    }
    @SneakyThrows
    @Override
    public Claim getClaim(String id) {
        String sql = "SELECT * FROM rclaims_claims WHERE id=?";
        try(PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setString(1,id);
            try(ResultSet rs = statement.executeQuery()){
                Result result = new Result(rs);
                if (rs.next()){
                    UUID owner = UUID.fromString(rs.getString("owner"));
                    List<ClaimStatus> claimStatuses = Stream.of(rs.getString("claim_statues").replace("[" ,"").replaceAll("]", "").split(", ")).filter(key-> {
                        try {
                            ClaimStatus.valueOf(key);
                            return true;
                        }catch (IllegalArgumentException e){
                            return false;
                        }
                    }).map(ClaimStatus::valueOf).collect(Collectors.toList());
                    Chunk chunk = solveChunk(rs.getString("chunk"));
                    List<UUID> members = Arrays.stream(rs.getString("members").replace("[", "").replace("]", "").split(", ")).collect(Collectors.toList()).stream()
                            .filter(member -> {
                                try {
                                    UUID.fromString(member);
                                    return true;
                                } catch (IllegalArgumentException e) {
                                    return false;
                                }
                            })
                            .map(UUID::fromString)
                            .collect(Collectors.toList());
                    Location homeLocation = solveHome(rs.getString("home"));
                    boolean isCenter = rs.getBoolean("isCenter");
                    String centerId = rs.getString("centerId");
                    Map<UUID, List<ClaimPermission>> permissions = solvePermission(rs.getString("permissions"));
                    List<ClaimEffect> effects = GsonProvider.getGson().fromJson(rs.getString("effects"), new TypeToken<List<ClaimEffect>>(){}.getType());
                    Material material = (rs.getString("block") == null ? Material.BEDROCK : Material.getMaterial(rs.getString("block")));
                    List<ClaimTag> claimTags = new ArrayList<>(
                            GsonProvider.getGson().fromJson(rs.getString("tags"), new TypeToken<List<ClaimTag>>(){}.getType())
                    );
                    Claim claim = new Claim(id,owner,members,claimStatuses,chunk, isCenter);
                    claim.setCenterId(centerId);
                    claim.setHomeLocation(homeLocation);
                    claim.setClaimPermissions(permissions);
                    claim.setEffects(new ArrayList<>(effects));
                    claim.setClaimTags(claimTags);
                    if (claim.isCenter()){
                        claim.setBlock(material);
                    }
                    return claim;
                }
            }
        }
        return null;
    }


    @Override
    public void updateClaim(Claim claim) {
        String formatted_permissions = claim.getClaimPermissions().entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue().stream()
                        .map(ClaimPermission::name)
                        .collect(Collectors.joining(",")))
                .collect(Collectors.toList()).stream().collect((Collectors.joining(";")));
        Optional<Integer> time_op = ClaimManager.getTasks().stream().filter(task-> task.getClaimId().equals(claim.getID())).map(ClaimTask::getTime).findFirst();
        int time = 555;
        if (time_op.isPresent()){
            time = time_op.get();
        }
        String location = "";
        if (claim.getHomeLocation() != null){
            location = claim.getHomeLocation().getWorld().getName()+ ":" + claim.getHomeLocation().getX() + ":" + claim.getHomeLocation().getY() + ":" + claim.getHomeLocation().getZ()+":"+claim.getHomeLocation().getPitch() + ":" + claim.getHomeLocation().getYaw();
        }
        HashMap<String, String> where = new HashMap<>();
        where.put("id", claim.getID());
        List<String> effect = (claim.getEffects() != null) ?
                claim.getEffects().stream()
                        .map(claimEffect -> GsonProvider.getGson().toJson(claimEffect))
                        .toList() :
                Collections.emptyList();
        String tagsJsonArray = GsonProvider.getGson().toJson(claim.getClaimTags());
        Update update = new Update(connection,"rclaims_claims", Arrays.asList("id", "owner", "members", "claim_statues", "chunk", "permissions", "time", "home", "isCenter", "centerId", "effects", "block", "tags"), Arrays.asList(claim.getID(),claim.getOwner().toString(), claim.getMembers().stream().map(UUID::toString).collect(Collectors.toList()).toString(),claim.getClaimStatuses().stream().map(ClaimStatus::name).collect(Collectors.toList()).toString(), claim.getChunk().getWorld().getName() + ":" + claim.getChunk().getX() + ":" + claim.getChunk().getZ(), formatted_permissions, time, location, claim.isCenter(), claim.getCenterId(), effect.toString(), claim.getBlock().name(), tagsJsonArray), where);
        update.execute();
    }
    @SneakyThrows
    @Override
    public void deleteClaim(String id) {
        String sql = "DELETE FROM rclaims_claims WHERE id=?";
        try(PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setString(1,id);
            statement.executeUpdate();
        }
    }

    @SneakyThrows
    @Override
    public boolean hasClaim(String id) {
        String sql = "SELECT COUNT(*) FROM rclaims_claims WHERE id=?";
        try(PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setString(1,id);
            ResultSet rs = statement.executeQuery();
            if (rs.next()){
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }
    @SneakyThrows
    @Override
    public List<Claim> getClaims() {
        String sql = "SELECT id, time FROM rclaims_claims";
        try(Statement statement = connection.createStatement()){
            ResultSet rs = statement.executeQuery(sql);
            List<Claim> claims = new ArrayList<>();
            while (rs.next()){
                String id = rs.getString("id");
                Claim claim = getClaim(id);
                int time = rs.getInt("time");
                claims.add(claim);
                if (!isTimerOnline(id)){
                    ClaimManager.getTasks().add(new ClaimTask(id,time, claim.isCenter()));
                }
            }
            return claims;
        }
    }

    // utility methods

    private Map<UUID, List<ClaimPermission>> solvePermission(String value){
        Map<UUID, List<ClaimPermission>> new_maps = new HashMap<>();
        if (value.isEmpty()){return new_maps;}
        String[] entries = value.split(";");
        for (String entry : entries) {
            String[] split = entry.split("=");
            UUID uuid = UUID.fromString(split[0]);
            List<ClaimPermission> claimPermissions = Arrays.stream(split[1].split(","))
                    .map(String::trim)
                    .map(ClaimPermission::valueOf)
                    .collect(Collectors.toList());
            new_maps.put(uuid, claimPermissions);
        }
        return new_maps;
    }

    private Chunk solveChunk(String value){
        String[] split = value.split(":");
        String worldName = split[0];
        int x = Integer.parseInt(split[1]);
        int z = Integer.parseInt(split[2]);
        Chunk chunk = Bukkit.getWorld(worldName).getChunkAt(x, z);
        return chunk;
    }

    private  Location solveHome(String value){
        if (value.isEmpty()){
            return null;
        }
        String[] split = value.split(":");
        double x = Double.parseDouble(split[1]);
        double y = Double.parseDouble(split[2]);
        double z = Double.parseDouble(split[3]);
        float yaw = Float.parseFloat(split[5]);
        float pitch = Float.parseFloat(split[4]);
        return new Location(Bukkit.getWorld(split[0]), x, y, z, yaw, pitch);
    }


    private boolean isTimerOnline(String id){
        return ClaimManager.getTasks().stream().anyMatch(task -> task.getClaimId().equals(id));
    }
}
