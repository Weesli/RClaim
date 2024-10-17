package net.weesli.rClaim.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.weesli.rClaim.enums.ClaimPermission;
import net.weesli.rClaim.modal.ClaimTag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ClaimTagTypeAdapter extends TypeAdapter<ClaimTag> {
    @Override
    public void write(JsonWriter out, ClaimTag claimTag) throws IOException {
        out.beginObject();
        out.name("claimId").value(claimTag.getClaimId());
        out.name("id").value(claimTag.getId());
        out.name("displayName").value(claimTag.getDisplayName());

        out.name("users").beginArray();
        for (UUID user : claimTag.getUsers()) {
            out.value(user.toString());
        }
        out.endArray();

        out.name("permissions").beginArray();
        for (ClaimPermission permission : claimTag.getPermissions()) {
            out.value(permission.name());
        }
        out.endArray();

        out.endObject();
    }

    @Override
    public ClaimTag read(JsonReader in) throws IOException {
        String claimId = null;
        String id = null;
        String displayName = null;
        List<UUID> users = new ArrayList<>();
        List<ClaimPermission> permissions = new ArrayList<>();

        in.beginObject();
        while (in.hasNext()) {
            String name = in.nextName();
            switch (name) {
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
            }
        }
        in.endObject();

        return new ClaimTag(claimId, id, displayName, users, permissions);
    }
}
