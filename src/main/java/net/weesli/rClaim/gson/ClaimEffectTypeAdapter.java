package net.weesli.rClaim.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.weesli.rClaim.enums.Effect;
import net.weesli.rClaim.modal.ClaimEffect;

import java.io.IOException;

public class ClaimEffectTypeAdapter extends TypeAdapter<ClaimEffect> {
    @Override
    public void write(JsonWriter out, ClaimEffect value) throws IOException {
        if (value == null) {
            throw new IOException("ClaimEffect cannot be null");
        }

        out.beginObject();
        out.name("type").value(value.getEffect().name());
        out.name("enabled").value(value.isEnabled());
        out.name("level").value(value.getLevel());
        out.name("max_level").value(value.getMaxLevel());
        out.endObject();
    }

    @Override
    public ClaimEffect read(JsonReader in) throws IOException {
        in.beginObject();
        Effect effect = null;
        boolean enabled = false;
        int level = 0;
        int maxLevel = 0;

        while (in.hasNext()) {
            String name = in.nextName();
            switch (name) {
                case "type":
                    effect = Effect.valueOf(in.nextString());
                    break;
                case "enabled":
                    enabled = in.nextBoolean();
                    break;
                case "level":
                    level = in.nextInt();
                    break;
                case "max_level":
                    maxLevel = in.nextInt();
                    break;
                default:
                    in.skipValue();
            }
        }

        in.endObject();

        if (effect == null) {
            throw new IOException("Effect type is missing or invalid");
        }

        return new ClaimEffect(effect, level, maxLevel, enabled);
    }

}
