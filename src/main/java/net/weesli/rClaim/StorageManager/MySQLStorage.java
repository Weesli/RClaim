package net.weesli.rClaim.StorageManager;

import net.weesli.rClaim.RClaim;
import net.weesli.rClaim.management.ClaimManager;
import net.weesli.rClaim.tasks.ClaimTask;
import net.weesli.rClaim.utils.Claim;
import net.weesli.rClaim.utils.ClaimPermission;
import net.weesli.rClaim.utils.ClaimStatus;
import net.weesli.rozsLib.ColorManager.ColorBuilder;
import net.weesli.rozsLib.DataBaseManager.MySQL.*;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MySQLStorage extends StorageImpl{

    private String host = RClaim.getInstance().getConfig().getString("options.mysql.host");
    private int port = RClaim.getInstance().getConfig().getInt("options.mysql.port");
    private String user = RClaim.getInstance().getConfig().getString("options.mysql.username");
    private String pass = RClaim.getInstance().getConfig().getString("options.mysql.password");
    private String db = RClaim.getInstance().getConfig().getString("options.mysql.database");
    private static MySQLStorage instance;
    private static Connection connection;

    MySQLBuilder builder = new MySQLBuilder(host,port,db,user,pass);
    public MySQLStorage(){
        connection= builder.build();
        createTable();
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
        try {
            builder.createTable("rclaims_claims", connection, columns);
        } catch (SQLException e) {
            Bukkit.getServer().getConsoleSender().sendMessage(ColorBuilder.convertColors("&cFailed to create table: rclaims_claims"));
        }
    }

    public static MySQLStorage getInstance(){
        try {
            if (connection == null || connection.isClosed() || instance == null){
                instance = new MySQLStorage();
            }
        } catch (SQLException e) {
            Bukkit.getServer().getConsoleSender().sendMessage(ColorBuilder.convertColors("&cMySQL database is failed. please check your config."));
            Bukkit.getServer().getPluginManager().disablePlugin(RClaim.getInstance());
        }
        return instance;
    }


    @Override
    public void insertClaim(Claim claim) {
        String formatted_permissions = claim.getClaimPermissions().entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue().stream()
                        .map(ClaimPermission::name)
                        .collect(Collectors.joining(",")))
                .toList().stream().collect((Collectors.joining(";")));
        Optional<Integer> time = ClaimManager.getTasks().stream().filter(task-> task.getClaimId().equals(claim.getID())).map(ClaimTask::getTime).findFirst();
        String location = "";
        if (claim.getHomeLocation() != null){
            location = claim.getHomeLocation().getWorld().getName()+ ":" + claim.getHomeLocation().getX() + ":" + claim.getHomeLocation().getY() + ":" + claim.getHomeLocation().getZ()+":"+claim.getHomeLocation().getPitch() + ":" + claim.getHomeLocation().getYaw();
        }
        Insert insert = new Insert("rclaims_claims", List.of("id", "owner", "members", "claim_statues", "chunk", "permissions", "time", "home"), List.of(claim.getID(),claim.getOwner().toString(), claim.getMembers().stream().map(UUID::toString).toList().toString(),claim.getClaimStatuses().stream().map(ClaimStatus::name).toList().toString(), claim.getChunk().getWorld().getName() + ":" + claim.getChunk().getX() + ":" + claim.getChunk().getZ(), formatted_permissions, time.get(), location));
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
                    List<ClaimStatus> claimStatuses = new ArrayList<>(Stream.of(rs.getString("claim_statues").replace("[" ,"").replaceAll("]", "").split(", ")).filter(key-> {
                        try {
                            ClaimStatus.valueOf(key);
                            return true;
                        }catch (IllegalArgumentException e){
                            return false;
                        }
                    }).map(ClaimStatus::valueOf).toList());
                    Chunk chunk = solveChunk(rs.getString("chunk"));
                    List<UUID> members = new ArrayList<>(Arrays.stream(rs.getString("members").replace("[", "").replace("]", "").split(", ")).toList().stream()
                            .filter(member -> {
                                try {
                                    UUID.fromString(member);
                                    return true;
                                } catch (IllegalArgumentException e) {
                                    return false;
                                }
                            })
                            .map(UUID::fromString)
                            .toList());
                    Location homeLocation = solveHome(rs.getString("home"));
                    Map<UUID, List<ClaimPermission>> permissions = solvePermission(rs.getString("permissions"));
                    Claim claim = new Claim(id,owner,members,claimStatuses,chunk);
                    claim.setHomeLocation(homeLocation);
                    claim.setClaimPermissions(permissions);
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
                .toList().stream().collect((Collectors.joining(";")));
        Optional<Integer> time_op = ClaimManager.getTasks().stream().filter(task-> task.getClaimId().equals(claim.getID())).map(ClaimTask::getTime).findFirst();
        int time = 555;
        if (time_op.isPresent()){
            time = time_op.get();
        }
        String location = "";
        if (claim.getHomeLocation() != null){
            location = claim.getHomeLocation().getWorld().getName()+ ":" + claim.getHomeLocation().getX() + ":" + claim.getHomeLocation().getY() + ":" + claim.getHomeLocation().getZ()+":"+claim.getHomeLocation().getPitch() + ":" + claim.getHomeLocation().getYaw();
        }
        Update update = new Update("rclaims_claims", List.of("id", "owner", "members", "claim_statues", "chunk", "permissions", "time", "home"), List.of(claim.getID(),claim.getOwner().toString(), claim.getMembers().stream().map(UUID::toString).toList().toString(),claim.getClaimStatuses().stream().map(ClaimStatus::name).toList().toString(), claim.getChunk().getWorld().getName() + ":" + claim.getChunk().getX() + ":" + claim.getChunk().getZ(), formatted_permissions, time, location), Map.of("id", claim.getID()));
        try {
            builder.update(connection,update);
        } catch (SQLException e) {
            Bukkit.getServer().getConsoleSender().sendMessage(ColorBuilder.convertColors("&cFailed to update claim to MySQL"));
        }
    }

    @Override
    public void deleteClaim(String id) {
        Delete delete = new Delete(connection,"rclaims_claims", Map.of("id", id));
        try {
            builder.delete(delete);
        } catch (SQLException e) {
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
                List<ClaimStatus> claimStatuses = new ArrayList<>(Stream.of(rs.getString("claim_statues").replace("[" ,"").replaceAll("]", "").split(", ")).filter(key-> {
                    try {
                        ClaimStatus.valueOf(key);
                        return true;
                    }catch (IllegalArgumentException e){
                        return false;
                    }
                }).map(ClaimStatus::valueOf).toList());
                Chunk chunk = solveChunk(rs.getString("chunk"));
                List<UUID> members = new ArrayList<>(Arrays.stream(rs.getString("members").replace("[", "").replace("]", "").split(", ")).toList().stream()
                        .filter(member -> {
                            try {
                                UUID.fromString(member);
                                return true;
                            } catch (IllegalArgumentException e) {
                                return false;
                            }
                        })
                        .map(UUID::fromString)
                        .toList());
                Map<UUID, List<ClaimPermission>> permissions = solvePermission(rs.getString("permissions"));
                int time = rs.getInt("time");
                Location homeLocation = solveHome(rs.getString("home"));
                Claim claim = new Claim(id,owner,members,claimStatuses,chunk);
                claim.setClaimPermissions(permissions);
                claim.setHomeLocation(homeLocation);
                claims.add(claim);
                if (!isTimerOnline(id)){
                    ClaimManager.getTasks().add(new ClaimTask(id,time));
                }
            }
            return claims;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public StorageType getStorageType() {
        return StorageType.MySQL;
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
            List<ClaimPermission> claimPermissions = new ArrayList<>(Arrays.stream(split[1].split(","))
                    .map(String::trim)
                    .map(ClaimPermission::valueOf)
                    .toList());
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

    private Location solveHome(String value){
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
}
