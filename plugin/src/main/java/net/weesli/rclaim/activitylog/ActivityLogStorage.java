package net.weesli.rclaim.activitylog;

import lombok.SneakyThrows;
import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.config.Config;
import net.weesli.rclaim.config.ConfigLoader;
import net.weesli.rozslib.color.ColorBuilder;
import net.weesli.rozslib.database.ConnectionInfo;
import net.weesli.rozslib.database.DatabaseFactory;
import net.weesli.rozslib.enums.DatabaseType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;

/**
 * Standalone JDBC store for claim activity log entries, independent of whichever
 * {@link net.weesli.rclaim.api.database.ClaimDatabase} backend is active for claim data
 * itself (that storage only persists full claim snapshots, it has no per-entry/paginated
 * query shape). MySQL is reused when it's the configured claim storage type; otherwise a
 * dedicated SQLite file is used, since the RozsDBLite key-value store has no relational
 * query support and pagination/retention pruning are inherently SQL-shaped operations.
 */
public class ActivityLogStorage {

    private static ActivityLogStorage instance;

    private final Connection connection;
    private final boolean mysql;

    @SneakyThrows
    private ActivityLogStorage() {
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
            File dataDir = new File(RClaim.getInstance().getDataFolder(), "data");
            if (!dataDir.exists()) dataDir.mkdirs();
            connection = DatabaseFactory.createConnection(new ConnectionInfo(DatabaseType.SQLite, "RClaim-log.db"), null);
        }
        createTable();
    }

    public static synchronized ActivityLogStorage get() {
        if (instance == null) instance = new ActivityLogStorage();
        return instance;
    }

    @SneakyThrows
    private void createTable() {
        String sql = mysql
                ? "CREATE TABLE IF NOT EXISTS rclaims_activity_log (" +
                    "id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                    "claimId VARCHAR(255) NOT NULL, " +
                    "playerId VARCHAR(36) NOT NULL, " +
                    "action VARCHAR(32) NOT NULL, " +
                    "detail VARCHAR(255), " +
                    "timestamp BIGINT NOT NULL" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;"
                : "CREATE TABLE IF NOT EXISTS rclaims_activity_log (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "claimId TEXT NOT NULL, " +
                    "playerId TEXT NOT NULL, " +
                    "action TEXT NOT NULL, " +
                    "detail TEXT, " +
                    "timestamp INTEGER NOT NULL" +
                    ");";
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
            statement.execute("CREATE INDEX IF NOT EXISTS idx_rclaims_activity_log_claim ON rclaims_activity_log(claimId)");
        }
    }

    /**
     * Snapshots the entry data on the calling thread and writes it on the async scheduler,
     * so this is safe to call directly from an event handler.
     */
    public void logAsync(String claimId, UUID playerId, ActivityAction action, String detail) {
        if (!ConfigLoader.getConfig().getActivityLog().isEnabled()) return;
        long timestamp = System.currentTimeMillis();
        RClaim.getInstance().getFoliaLib().getScheduler().runAsync((task) -> {
            String sql = "INSERT INTO rclaims_activity_log (claimId, playerId, action, detail, timestamp) VALUES (?,?,?,?,?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, claimId);
                statement.setString(2, playerId.toString());
                statement.setString(3, action.name());
                statement.setString(4, detail);
                statement.setLong(5, timestamp);
                statement.executeUpdate();
            } catch (SQLException e) {
                Bukkit.getConsoleSender().sendMessage(ColorBuilder.convertColors(
                        "&c[RClaim] Failed to write activity log entry: " + e.getMessage()));
            }
        });
    }

    /**
     * Fetches one page of entries (newest first) plus the total page count, then hands the
     * result back on the requesting player's entity scheduler.
     */
    public void fetchPageAsync(String claimId, int page, int pageSize, Player player, BiConsumer<List<ActivityLogEntry>, Integer> onResult) {
        RClaim.getInstance().getFoliaLib().getScheduler().runAsync((task) -> {
            List<ActivityLogEntry> entries = new ArrayList<>();
            int totalPages = 1;
            try {
                try (PreparedStatement statement = connection.prepareStatement(
                        "SELECT COUNT(*) FROM rclaims_activity_log WHERE claimId = ?")) {
                    statement.setString(1, claimId);
                    try (ResultSet rs = statement.executeQuery()) {
                        if (rs.next()) {
                            int total = rs.getInt(1);
                            totalPages = Math.max(1, (int) Math.ceil(total / (double) pageSize));
                        }
                    }
                }

                int safePage = Math.min(Math.max(page, 1), totalPages);
                try (PreparedStatement statement = connection.prepareStatement(
                        "SELECT playerId, action, detail, timestamp FROM rclaims_activity_log WHERE claimId = ? ORDER BY id DESC LIMIT ? OFFSET ?")) {
                    statement.setString(1, claimId);
                    statement.setInt(2, pageSize);
                    statement.setInt(3, (safePage - 1) * pageSize);
                    try (ResultSet rs = statement.executeQuery()) {
                        while (rs.next()) {
                            entries.add(new ActivityLogEntry(
                                    UUID.fromString(rs.getString("playerId")),
                                    ActivityAction.valueOf(rs.getString("action")),
                                    rs.getString("detail"),
                                    rs.getLong("timestamp")
                            ));
                        }
                    }
                }
            } catch (SQLException e) {
                Bukkit.getConsoleSender().sendMessage(ColorBuilder.convertColors(
                        "&c[RClaim] Failed to read activity log: " + e.getMessage()));
            }

            int finalTotalPages = totalPages;
            RClaim.getInstance().getFoliaLib().getScheduler().runAtEntity(player, (entityTask) ->
                    onResult.accept(entries, finalTotalPages));
        });
    }

    public void pruneAsync() {
        RClaim.getInstance().getFoliaLib().getScheduler().runAsync((task) -> prune());
    }

    private void prune() {
        Config.ActivityLogConfig config = ConfigLoader.getConfig().getActivityLog();
        try {
            if (config.getRetentionDays() > 0) {
                long cutoff = System.currentTimeMillis() - Duration.ofDays(config.getRetentionDays()).toMillis();
                try (PreparedStatement statement = connection.prepareStatement(
                        "DELETE FROM rclaims_activity_log WHERE timestamp < ?")) {
                    statement.setLong(1, cutoff);
                    statement.executeUpdate();
                }
            }

            if (config.getMaxEntriesPerClaim() > 0) {
                List<String> claimIds = new ArrayList<>();
                try (PreparedStatement statement = connection.prepareStatement("SELECT DISTINCT claimId FROM rclaims_activity_log");
                     ResultSet rs = statement.executeQuery()) {
                    while (rs.next()) claimIds.add(rs.getString("claimId"));
                }

                for (String claimId : claimIds) {
                    long cutoffId = -1;
                    try (PreparedStatement statement = connection.prepareStatement(
                            "SELECT id FROM rclaims_activity_log WHERE claimId = ? ORDER BY id DESC LIMIT 1 OFFSET ?")) {
                        statement.setString(1, claimId);
                        statement.setInt(2, config.getMaxEntriesPerClaim() - 1);
                        try (ResultSet rs = statement.executeQuery()) {
                            if (rs.next()) cutoffId = rs.getLong("id");
                        }
                    }
                    if (cutoffId > 0) {
                        try (PreparedStatement statement = connection.prepareStatement(
                                "DELETE FROM rclaims_activity_log WHERE claimId = ? AND id < ?")) {
                            statement.setString(1, claimId);
                            statement.setLong(2, cutoffId);
                            statement.executeUpdate();
                        }
                    }
                }
            }
        } catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage(ColorBuilder.convertColors(
                    "&c[RClaim] Failed to prune activity log: " + e.getMessage()));
        }
    }

    public void shutdown() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
