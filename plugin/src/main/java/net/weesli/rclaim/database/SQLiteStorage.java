package net.weesli.rclaim.database;

import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.enums.StorageType;
import net.weesli.rozslib.database.ConnectionInfo;
import net.weesli.rozslib.enums.DatabaseType;

import java.io.File;
import java.io.IOException;

public class SQLiteStorage extends AbstractDatabase {

    static {
        File file = new File(RClaim.getInstance().getDataFolder(), "data");
        if (!file.exists()) {
            file.mkdirs();
        }
        File dbFile = new File(file, "RClaim.db");
        if (!dbFile.exists()) {
            try {
                dbFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public SQLiteStorage(){
        super(new ConnectionInfo(DatabaseType.SQLite,
                "jdbc:sqlite:" + new File(RClaim.getInstance().getDataFolder(), "data/RClaim.db").getAbsolutePath()));
    }


    @Override
    public StorageType getStorageType() {
        return StorageType.SQLite;
    }

    @Override
    public String tableSQL() {
        return "CREATE TABLE IF NOT EXISTS rclaims_claims (" +
                "claimId TEXT NOT NULL PRIMARY KEY, " +
                "data TEXT NOT NULL" +
                ");";
    }
}
