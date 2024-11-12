package net.weesli.rClaim.database;

import com.google.common.reflect.TypeToken;
import eu.decentsoftware.holograms.api.utils.scheduler.S;
import net.weesli.rClaim.RClaim;
import net.weesli.rClaim.enums.StorageType;
import net.weesli.rClaim.gson.GsonProvider;
import net.weesli.rClaim.modal.ClaimEffect;
import net.weesli.rClaim.modal.ClaimTag;
import net.weesli.rClaim.utils.ClaimManager;
import net.weesli.rClaim.tasks.ClaimTask;
import net.weesli.rClaim.modal.Claim;
import net.weesli.rClaim.enums.ClaimPermission;
import net.weesli.rClaim.enums.ClaimStatus;
import net.weesli.rozsLib.color.ColorBuilder;
import net.weesli.rozsLib.database.builders.SQLBuilder;
import net.weesli.rozsLib.database.builders.SQLiteConnectionBuilder;
import net.weesli.rozsLib.database.component.Column;
import net.weesli.rozsLib.database.component.Table;
import net.weesli.rozsLib.database.mysql.Insert;
import net.weesli.rozsLib.database.mysql.Result;
import net.weesli.rozsLib.database.mysql.Update;
import net.weesli.rozsLib.database.sqlite.SQLiteBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        super(new SQLBuilder().build(new SQLiteConnectionBuilder(new File(RClaim.getInstance().getDataFolder(), "data/RClaim.db"))));
    }

    @Override
    public String getTableQuery() {
        return """
            CREATE TABLE IF NOT EXISTS rclaims_claims (
                id TEXT PRIMARY KEY,
                owner TEXT,
                members TEXT,
                claim_statues TEXT,
                chunk TEXT,
                permissions TEXT,
                time INTEGER,
                home TEXT,
                isCenter INTEGER,
                centerId TEXT,
                effects TEXT,
                block TEXT,
                tags TEXT
            );
        """;
    }

    @Override
    public StorageType getStorageType() {
        return StorageType.SQLite;
    }
}
