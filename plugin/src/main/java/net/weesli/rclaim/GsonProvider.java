package net.weesli.rclaim;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.weesli.rclaim.database.adapter.*;
import net.weesli.rclaim.model.ClaimEffectImpl;
import net.weesli.rclaim.model.ClaimTagImpl;
import net.weesli.rclaim.model.SubClaimImpl;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GsonProvider {

    private static final Gson gson = new GsonBuilder()
            .registerTypeHierarchyAdapter(Material.class, new MaterialTypeAdapter())
            .registerTypeAdapter(ClaimTagImpl.class, new ClaimTagTypeAdapter())
            .registerTypeAdapter(SubClaimImpl.class, new SubClaimTypeAdapter())
            .registerTypeAdapter(ClaimEffectImpl.class, new ClaimEffectTypeAdapter())
            .registerTypeAdapter(Location.class, new LocationTypeAdapter())
            .registerTypeAdapter(new TypeToken<Map<UUID, List<String>>>() {}.getType(), new ClaimPermissionMapAdapter())
            .create();

    public static Gson getGson() {
        return gson;
    }
}
