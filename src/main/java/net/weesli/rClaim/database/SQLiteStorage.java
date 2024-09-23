package net.weesli.rClaim.database;

import com.google.common.reflect.TypeToken;
import net.weesli.rClaim.RClaim;
import net.weesli.rClaim.enums.StorageType;
import net.weesli.rClaim.gson.GsonProvider;
import net.weesli.rClaim.modal.ClaimEffect;
import net.weesli.rClaim.utils.ClaimManager;
import net.weesli.rClaim.tasks.ClaimTask;
import net.weesli.rClaim.modal.Claim;
import net.weesli.rClaim.enums.ClaimPermission;
import net.weesli.rClaim.enums.ClaimStatus;
import net.weesli.rozsLib.color.ColorBuilder;
import net.weesli.rozsLib.database.mysql.Column;
import net.weesli.rozsLib.database.mysql.Insert;
import net.weesli.rozsLib.database.mysql.Update;
import net.weesli.rozsLib.database.sqlite.SQLiteBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;

import java.io.File;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SQLiteStorage implements Database {

    private static SQLiteStorage instance;
    private static Connection connection;
    private SQLiteBuilder builder;

    public SQLiteStorage(){
        File path = new File(RClaim.getInstance().getDataFolder(), "data");
        if (!path.exists()) {
            path.mkdirs();
        }
        SQLiteBuilder builder = new SQLiteBuilder("RClaim").setPath(path.getPath());
        this.builder = builder;
        try {
            connection = builder.build();
            createTable();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void createTable() {
        List<Column> columns = new ArrayList<>();
        columns.add(new Column("id", "VARCHAR(255)", 255).setPrimary(true));
        columns.add(new Column("owner", "VARCHAR(255)", 255));
        columns.add(new Column("members", "TEXT(65000)", 65000));
        columns.add(new Column("claim_statues", "TEXT(65000)", 65000));
        columns.add(new Column("chunk", "TEXT(65000)", 65000));
        columns.add(new Column("permissions", "TEXT(65000)", 65000));
        columns.add(new Column("time", "INT", 9999));
        columns.add(new Column("home", "VARCHAR(255)", 255));
        columns.add(new Column("isCenter", "BOOL", 255));
        columns.add(new Column("centerId", "VARCHAR(255)", 255));
        columns.add(new Column("effects", "VARCHAR(255)", 65000));
        try {
            builder.createTable("rclaims_claims", connection, columns);
        } catch (SQLException e) {
            Bukkit.getServer().getConsoleSender().sendMessage(ColorBuilder.convertColors("&cFailed to create table: rclaims_claims"));
        }
        addColumn("effects");
        addColumn("block");
    }

    public static SQLiteStorage getInstance(){
        try {
            if(instance == null || connection == null || connection.isClosed()){
                instance = new SQLiteStorage();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return instance;
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
        Insert insert = new Insert("rclaims_claims", Arrays.asList("id", "owner", "members", "claim_statues", "chunk", "permissions", "time", "home", "isCenter", "centerId", "effects", "block"), Arrays.asList(claim.getID(),claim.getOwner().toString(), claim.getMembers().stream().map(UUID::toString).collect(Collectors.toList()).toString(),claim.getClaimStatuses().stream().map(ClaimStatus::name).collect(Collectors.toList()).toString(), claim.getChunk().getWorld().getName() + ":" + claim.getChunk().getX() + ":" + claim.getChunk().getZ(), formatted_permissions, time.get(), location, claim.isCenter(), claim.getCenterId(), effect.toString(), claim.getBlock().name()));
        try {
            builder.insert(connection,insert);
        } catch (SQLException e) {
            Bukkit.getServer().getConsoleSender().sendMessage(ColorBuilder.convertColors("&cFailed to insert claim to MySQL"));
        }
    }

    @Override
    public Claim getClaim(String id) {
        String sql = "SELECT * FROM rclaims_claims WHERE id=?";
        try(PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setString(1,id);
            try(ResultSet rs = statement.executeQuery()){
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
                    Claim claim = new Claim(id,owner,members,claimStatuses,chunk, isCenter);
                    claim.setCenterId(centerId);
                    claim.setHomeLocation(homeLocation);
                    claim.setClaimPermissions(permissions);
                    claim.setEffects(new ArrayList<>(effects));
                    if (claim.isCenter()){
                        claim.setBlock(material);
                    }
                    return claim;
                }
            }
        }catch (SQLException e){
            Bukkit.getServer().getConsoleSender().sendMessage(ColorBuilder.convertColors("&cFailed to get claim from MySQL"));
            return null;
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
        Update update = new Update("rclaims_claims", Arrays.asList("id", "owner", "members", "claim_statues", "chunk", "permissions", "time", "home", "isCenter", "centerId", "effects", "block"), Arrays.asList(claim.getID(),claim.getOwner().toString(), claim.getMembers().stream().map(UUID::toString).collect(Collectors.toList()).toString(),claim.getClaimStatuses().stream().map(ClaimStatus::name).collect(Collectors.toList()).toString(), claim.getChunk().getWorld().getName() + ":" + claim.getChunk().getX() + ":" + claim.getChunk().getZ(), formatted_permissions, time, location, claim.isCenter(), claim.getCenterId(), effect.toString(), claim.getBlock().name()), where);
        try {
            builder.update(connection,update);
        } catch (SQLException e) {
            Bukkit.getServer().getConsoleSender().sendMessage(ColorBuilder.convertColors("&cFailed to update claim to MySQL"));
        }
    }

    @Override
    public void deleteClaim(String id) {
        String sql = "DELETE FROM rclaims_claims WHERE id=?";
        try(PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setString(1,id);
            statement.executeUpdate();
        }catch (SQLException e){
            Bukkit.getServer().getConsoleSender().sendMessage(ColorBuilder.convertColors("&cFailed to delete claim from MySQL"));
        }
    }

    @Override
    public boolean hasClaim(String id) {
        String sql = "SELECT COUNT(*) FROM rclaims_claims WHERE id=?";
        try(PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setString(1,id);
            ResultSet rs = statement.executeQuery();
            if (rs.next()){
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    @Override
    public List<Claim> getClaims() {
        String sql = "SELECT * FROM rclaims_claims";
        try(Statement statement = connection.createStatement()){
            ResultSet rs = statement.executeQuery(sql);
            List<Claim> claims = new ArrayList<>();
            while (rs.next()){
                String id = rs.getString("id");
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
                Map<UUID, List<ClaimPermission>> permissions = solvePermission(rs.getString("permissions"));
                int time = rs.getInt("time");
                boolean isCenter = rs.getBoolean("isCenter");
                String centerId = rs.getString("centerId");
                Location homeLocation = solveHome(rs.getString("home"));
                List<ClaimEffect> effects = GsonProvider.getGson().fromJson(rs.getString("effects"), new TypeToken<List<ClaimEffect>>(){}.getType());
                Material material = (rs.getString("block") == null ? Material.BEDROCK : Material.getMaterial(rs.getString("block")));
                Claim claim = new Claim(id,owner,members,claimStatuses,chunk, isCenter);
                claim.setCenterId(centerId);
                claim.setClaimPermissions(permissions);
                claim.setHomeLocation(homeLocation);
                claim.setEffects(new ArrayList<>(effects));
                if (claim.isCenter()){
                    claim.setBlock(material);
                }
                claims.add(claim);
                if (!isTimerOnline(id)){
                    ClaimManager.getTasks().add(new ClaimTask(id,time, isCenter));
                }
            }
            return claims;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public StorageType getStorageType() {
        return StorageType.SQLite;
    }


    private boolean isTimerOnline(String id){
        return ClaimManager.getTasks().stream().anyMatch(task -> task.getClaimId().equals(id));
    }

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

    public void addColumn(String value) {
        String tableName = "rclaims_claims";

        try {
            DatabaseMetaData dbMetaData = connection.getMetaData();
            ResultSet columns = dbMetaData.getColumns(null, null, tableName, value);

            if (!columns.next()) {
                String alterTableSQL = "ALTER TABLE " + tableName + " ADD COLUMN " + value + " TEXT(65000);";
                try (Statement statement = connection.createStatement()) {
                    statement.execute(alterTableSQL);
                }
            }
        } catch (SQLException e) {
            Bukkit.getServer().getConsoleSender().sendMessage(ColorBuilder.convertColors("&cFailed to add column '" + value + "' to table: " + tableName));
        }
    }
}
