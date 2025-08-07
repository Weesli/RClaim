package net.weesli.rclaim.database;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import net.weesli.rclaim.GsonProvider;
import net.weesli.rclaim.api.model.Claim;
import net.weesli.rclaim.api.database.ClaimDatabase;
import net.weesli.rclaim.model.ClaimImpl;
import net.weesli.rozslib.database.ConnectionInfo;
import net.weesli.rozslib.database.Database;
import net.weesli.rozslib.database.DatabaseFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

public abstract class AbstractDatabase extends Database implements ClaimDatabase {

    private final ConnectionInfo info;
    private final Gson gson = GsonProvider.getGson();
    private Connection connection;

    @SneakyThrows
    public AbstractDatabase(ConnectionInfo info) {
        super(info);
        this.info = info;
        connect();
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

    @Override
    public void shutdown() {
        try {
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void forceSave() {
        // ignored for mysql and sqlite
    }
}