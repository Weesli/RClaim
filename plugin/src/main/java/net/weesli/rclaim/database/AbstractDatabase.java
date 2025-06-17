package net.weesli.rclaim.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.SneakyThrows;
import net.weesli.rclaim.api.enums.ClaimPermission;
import net.weesli.rclaim.api.model.Claim;
import net.weesli.rclaim.api.model.ClaimEffect;
import net.weesli.rclaim.api.model.ClaimTag;
import net.weesli.rclaim.api.model.SubClaim;
import net.weesli.rclaim.database.adapter.*;
import net.weesli.rclaim.database.interfaces.ClaimDatabase;
import net.weesli.rclaim.database.interfaces.IDatabase;
import net.weesli.rclaim.model.ClaimEffectImpl;
import net.weesli.rclaim.model.ClaimImpl;
import net.weesli.rclaim.model.ClaimTagImpl;
import net.weesli.rclaim.model.SubClaimImpl;
import net.weesli.rozslib.database.ConnectionInfo;
import net.weesli.rozslib.database.Database;
import net.weesli.rozslib.database.DatabaseFactory;
import org.bukkit.Location;
import org.bukkit.Material;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

public abstract class AbstractDatabase extends Database implements IDatabase, ClaimDatabase {

    private final ConnectionInfo info;
    private Gson gson;
    private Connection connection;

    @SneakyThrows
    public AbstractDatabase(ConnectionInfo info) {
        super(info);
        this.info = info;
        connect();
        gson = new GsonBuilder()
                .registerTypeHierarchyAdapter(Material.class, new MaterialTypeAdapter())
                .registerTypeAdapter(ClaimTagImpl.class, new ClaimTagTypeAdapter())
                .registerTypeAdapter(SubClaimImpl.class, new SubClaimTypeAdapter())
                .registerTypeAdapter(ClaimEffectImpl.class, new ClaimEffectTypeAdapter())
                .registerTypeAdapter(Location.class, new LocationTypeAdapter())
                .registerTypeAdapter(new TypeToken<Map<UUID, List<ClaimPermission>>>() {}.getType(), new ClaimPermissionMapAdapter())

                .create();
    }
    public void connect() {
        connection = DatabaseFactory.createConnection(info);
        createTable();
    }
    @SneakyThrows
    private void createTable() {
        String sql = tableSQL();
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }

    }

    @Override
    public void insertClaim(Claim item) {
        String jsonClaim = gson.toJson(item);
        String sql = "INSERT INTO rclaims_claims (claimId,data) VALUES (?,?)";
        try(PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setString(1, item.getID());
            statement.setString(2, jsonClaim);
            statement.execute();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void updateClaim(Claim claim) {
        String jsonClaim = gson.toJson(claim);
        String sql = "UPDATE rclaims_claims SET data = ? WHERE claimId = ?";
        try(PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setString(1, jsonClaim);
            statement.setString(2, claim.getID());
            statement.execute();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean hasClaim(String id) {
        String sql = "SELECT * FROM rclaims_claims WHERE claimId = ?";
        try(PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setString(1, id);
            return statement.executeQuery().next();
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @SneakyThrows
    @Override
    public void deleteClaim(String id) {
        String sql = "DELETE FROM rclaims_claims WHERE claimId = ?";
        try(PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setString(1, id);
            statement.execute();
        }
    }

    @Override
    public List<Claim> getAllClaims() {
        String sql = "SELECT * FROM rclaims_claims";
        List<Claim> claims = new ArrayList<>();
        try(PreparedStatement statement = connection.prepareStatement(sql)){
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                String jsonClaim = resultSet.getString("data");
                Claim claim = gson.fromJson(jsonClaim, ClaimImpl.class);
                claims.add(claim);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return claims;
    }
}