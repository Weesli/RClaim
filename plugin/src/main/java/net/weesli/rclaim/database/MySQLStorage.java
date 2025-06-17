package net.weesli.rclaim.database;

import net.weesli.rclaim.config.ConfigLoader;
import net.weesli.rclaim.api.enums.StorageType;
import net.weesli.rozslib.database.ConnectionInfo;
import net.weesli.rozslib.enums.DatabaseType;

public class MySQLStorage extends AbstractDatabase {

    public MySQLStorage(){
        super(new ConnectionInfo(
                DatabaseType.MySQL,
                String.format("jdbc:mysql://%s:%s@%s:%s/",
                        ConfigLoader.getConfig().getDatabase().getUsername(),
                        ConfigLoader.getConfig().getDatabase().getPassword(),
                        ConfigLoader.getConfig().getDatabase().getHost(),
                        ConfigLoader.getConfig().getDatabase().getPort()),
                ConfigLoader.getConfig().getDatabase().getUsername(),
                ConfigLoader.getConfig().getDatabase().getPassword(),
                ConfigLoader.getConfig().getDatabase().getDatabase()
        ));
    }


    @Override
    public StorageType getStorageType() {
        return StorageType.MySQL;
    }

    @Override
    public String tableSQL() {
        return "CREATE TABLE IF NOT EXISTS rclaims_claims (" +
                "claimId VARCHAR(255) NOT NULL PRIMARY KEY, " +
                "data TEXT NOT NULL" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";
    }
}
