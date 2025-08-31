package net.weesli.rclaim.database.adapter;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.*;

public class ClaimPermissionMapAdapter implements JsonSerializer<Map<UUID, List<String>>>,
        JsonDeserializer<Map<UUID, List<String>>> {

    @Override
    public JsonElement serialize(Map<UUID, List<String>> src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject obj = new JsonObject();
        for (Map.Entry<UUID, List<String>> entry : src.entrySet()) {
            JsonArray array = new JsonArray();
            for (String permission : entry.getValue()) {
                array.add(permission);
            }
            obj.add(entry.getKey().toString(), array);
        }
        return obj;
    }

    @Override
    public Map<UUID, List<String>> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Map<UUID, List<String>> map = new HashMap<>();
        JsonObject obj = json.getAsJsonObject();

        for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
            UUID uuid = UUID.fromString(entry.getKey());
            JsonArray array = entry.getValue().getAsJsonArray();
            List<String> permissions = new ArrayList<>();
            for (JsonElement el : array) {
                permissions.add(el.getAsString());
            }
            map.put(uuid, permissions);
        }

        return map;
    }
}
