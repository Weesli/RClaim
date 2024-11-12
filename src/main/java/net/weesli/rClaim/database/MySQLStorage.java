package net.weesli.rClaim.database;

import com.google.common.reflect.TypeToken;
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
import net.weesli.rozsLib.database.builders.MySQLConnectionBuilder;
import net.weesli.rozsLib.database.builders.SQLBuilder;
import net.weesli.rozsLib.database.mysql.*;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MySQLStorage extends AbstractDatabase {

    public MySQLStorage(){
        super(new SQLBuilder().build(new MySQLConnectionBuilder(
                RClaim.getInstance().getConfig().getString("options.database.host"),
                RClaim.getInstance().getConfig().getInt("options.database.port"),
                RClaim.getInstance().getConfig().getString("options.database.database"),
                RClaim.getInstance().getConfig().getString("options.database.username"),
                RClaim.getInstance().getConfig().getString("options.database.password")
        )));
    }

    @Override
    public String getTableQuery() {
        return "CREATE TABLE IF NOT EXISTS rclaims_claims ("
                + "id VARCHAR(255) PRIMARY KEY, "
                + "owner VARCHAR(255), "
                + "members TEXT, "
                + "claim_statues TEXT, "
                + "chunk TEXT, "
                + "permissions TEXT, "
                + "time INT, "
                + "home VARCHAR(255), "
                + "isCenter BOOL, "
                + "centerId VARCHAR(255), "
                + "effects VARCHAR(255), "
                + "block VARCHAR(255), "
                + "tags TEXT"
                + ");";
    }

    @Override
    public StorageType getStorageType() {
        return StorageType.MySQL;
    }


}
