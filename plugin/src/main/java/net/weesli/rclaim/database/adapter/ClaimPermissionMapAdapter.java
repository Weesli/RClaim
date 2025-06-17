package net.weesli.rclaim.database.adapter;

import com.google.gson.*;
import net.weesli.rclaim.api.enums.ClaimPermission;

import java.lang.reflect.Type;
import java.util.*;

public class ClaimPermissionMapAdapter implements JsonSerializer<Map<UUID, List<ClaimPermission>>>,
        JsonDeserializer<Map<UUID, List<ClaimPermission>>> {

    @Override
    public JsonElement serialize(Map<UUID, List<ClaimPermission>> src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject obj = new JsonObject();
        for (Map.Entry<UUID, List<ClaimPermission>> entry : src.entrySet()) {
            JsonArray array = new JsonArray();
            for (ClaimPermission permission : entry.getValue()) {
                array.add(permission.name());
            }
            obj.add(entry.getKey().toString(), array);
        }
        return obj;
    }

    @Override
    public Map<UUID, List<ClaimPermission>> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Map<UUID, List<ClaimPermission>> map = new HashMap<>();
        JsonObject obj = json.getAsJsonObject();

        for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
            UUID uuid = UUID.fromString(entry.getKey());
            JsonArray array = entry.getValue().getAsJsonArray();
            List<ClaimPermission> permissions = new ArrayList<>();
            for (JsonElement el : array) {
                permissions.add(ClaimPermission.valueOf(el.getAsString()));
            }
            map.put(uuid, permissions);
        }

        return map;
    }
}
