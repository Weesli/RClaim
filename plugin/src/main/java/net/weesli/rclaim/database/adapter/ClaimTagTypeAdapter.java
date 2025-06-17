package net.weesli.rclaim.database.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.weesli.rclaim.api.enums.ClaimPermission;
import net.weesli.rclaim.model.ClaimTagImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ClaimTagTypeAdapter extends TypeAdapter<ClaimTagImpl> {
    @Override
    public void write(JsonWriter out, ClaimTagImpl value) throws IOException {
        out.beginObject();
        out.name("claimId").value(value.getClaimId());
        out.name("id").value(value.getId());
        out.name("displayName").value(value.getDisplayName());

        out.name("users");
        out.beginArray();
        for (UUID user : value.getUsers()) {
            out.value(user.toString());
        }
        out.endArray();

        out.name("permissions");
        out.beginArray();
        for (ClaimPermission permission : value.getPermissions()) {
            out.value(permission.toString());
        }
        out.endArray();

        out.endObject();
    }


    @Override
    public ClaimTagImpl read(JsonReader in) throws IOException {
        String claimId = null;
        String id = null;
        String displayName = null;
        List<UUID> users = new ArrayList<>();
        List<ClaimPermission> permissions = new ArrayList<>();

        in.beginObject();
        while (in.hasNext()) {
            switch (in.nextName()) {
                case "claimId":
                    claimId = in.nextString();
                    break;
                case "id":
                    id = in.nextString();
                    break;
                case "displayName":
                    displayName = in.nextString();
                    break;
                case "users":
                    in.beginArray();
                    while (in.hasNext()) {
                        users.add(UUID.fromString(in.nextString()));
                    }
                    in.endArray();
                    break;
                case "permissions":
                    in.beginArray();
                    while (in.hasNext()) {
                        permissions.add(ClaimPermission.valueOf(in.nextString()));
                    }
                    in.endArray();
                    break;
                default:
                    in.skipValue();
            }
        }
        in.endObject();

        return new ClaimTagImpl(claimId, id, displayName, users, permissions);
    }

}
