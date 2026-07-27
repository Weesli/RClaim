package net.weesli.rclaim.marketplace;

import lombok.SneakyThrows;
import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.config.ConfigLoader;
import net.weesli.rozslib.color.ColorBuilder;
import net.weesli.rozslib.database.ConnectionInfo;
import net.weesli.rozslib.database.DatabaseFactory;
import net.weesli.rozslib.enums.DatabaseType;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Durable store for active claim rentals (who's renting, who to hand the claim back to, and
 * when). This must survive a restart - unlike a market listing (an unaccepted offer, fine to
 * lose), losing an active rental would permanently strand a claim with the wrong owner.
 *
 * Reads happen against an in-memory cache (loaded once at startup) so the per-second
 * PublicTask expiry loop never blocks on I/O; writes go to both the cache and the database.
 */
public class RentalStorage {

    private static RentalStorage instance;

    private final Connection connection;
    private final boolean mysql;
    private final Map<String, RentalRecord> cache = new ConcurrentHashMap<>();

    @SneakyThrows
    private RentalStorage() {
        mysql = "MySQL".equalsIgnoreCase(ConfigLoader.getConfig().getStorageType());
        if (mysql) {
            connection = DatabaseFactory.createConnection(new ConnectionInfo(
                    DatabaseType.MySQL,
                    ConfigLoader.getConfig().getDatabase().getHost(),
                    ConfigLoader.getConfig().getDatabase().getPort(),
                    ConfigLoader.getConfig().getDatabase().getUsername(),
                    ConfigLoader.getConfig().getDatabase().getPassword(),
                    ConfigLoader.getConfig().getDatabase().getDatabase()
            ), null);
        } else {
            connection = DatabaseFactory.createConnection(new ConnectionInfo(DatabaseType.SQLite, "RClaim-rentals.db"), null);
        }
        createTable();
        loadAll();
    }

    public static synchronized RentalStorage get() {
        if (instance == null) instance = new RentalStorage();
        return instance;
    }

    @SneakyThrows
    private void createTable() {
        String sql = mysql
                ? "CREATE TABLE IF NOT EXISTS rclaims_rentals (" +
                    "claimId VARCHAR(255) NOT NULL PRIMARY KEY, " +
                    "originalOwnerId VARCHAR(36) NOT NULL, " +
                    "renterId VARCHAR(36) NOT NULL, " +
                    "rentEndMillis BIGINT NOT NULL" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;"
                : "CREATE TABLE IF NOT EXISTS rclaims_rentals (" +
                    "claimId TEXT NOT NULL PRIMARY KEY, " +
                    "originalOwnerId TEXT NOT NULL, " +
                    "renterId TEXT NOT NULL, " +
                    "rentEndMillis INTEGER NOT NULL" +
                    ");";
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

    private void loadAll() {
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery("SELECT claimId, originalOwnerId, renterId, rentEndMillis FROM rclaims_rentals")) {
            while (rs.next()) {
                RentalRecord record = new RentalRecord(
                        rs.getString("claimId"),
                        UUID.fromString(rs.getString("originalOwnerId")),
                        UUID.fromString(rs.getString("renterId")),
                        rs.getLong("rentEndMillis")
                );
                cache.put(record.claimId(), record);
            }
        } catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage(ColorBuilder.convertColors(
                    "&c[RClaim] Failed to load active rentals: " + e.getMessage()));
        }
    }

    public RentalRecord getActiveRental(String claimId) {
        return cache.get(claimId);
    }

    public void startRentalAsync(String claimId, UUID originalOwnerId, UUID renterId, long rentEndMillis) {
        RentalRecord record = new RentalRecord(claimId, originalOwnerId, renterId, rentEndMillis);
        cache.put(claimId, record);
        RClaim.getInstance().getFoliaLib().getScheduler().runAsync((task) -> {
            String sql = "REPLACE INTO rclaims_rentals (claimId, originalOwnerId, renterId, rentEndMillis) VALUES (?,?,?,?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, claimId);
                statement.setString(2, originalOwnerId.toString());
                statement.setString(3, renterId.toString());
                statement.setLong(4, rentEndMillis);
                statement.executeUpdate();
            } catch (SQLException e) {
                Bukkit.getConsoleSender().sendMessage(ColorBuilder.convertColors(
                        "&c[RClaim] Failed to persist rental start: " + e.getMessage()));
            }
        });
    }

    public void endRentalAsync(String claimId) {
        cache.remove(claimId);
        RClaim.getInstance().getFoliaLib().getScheduler().runAsync((task) -> {
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM rclaims_rentals WHERE claimId = ?")) {
                statement.setString(1, claimId);
                statement.executeUpdate();
            } catch (SQLException e) {
                Bukkit.getConsoleSender().sendMessage(ColorBuilder.convertColors(
                        "&c[RClaim] Failed to persist rental end: " + e.getMessage()));
            }
        });
    }

    public void shutdown() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
