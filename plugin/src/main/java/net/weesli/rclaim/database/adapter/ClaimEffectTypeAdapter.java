package net.weesli.rclaim.database.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.weesli.rclaim.api.enums.Effect;
import net.weesli.rclaim.model.ClaimEffectImpl;

import java.io.IOException;

public class ClaimEffectTypeAdapter extends TypeAdapter<ClaimEffectImpl> {
    @Override
    public void write(JsonWriter out, ClaimEffectImpl value) throws IOException {
        out.beginObject();
        out.name("effect").value(value.getEffect().name());
        out.name("level").value(value.getLevel());
        out.name("maxLevel").value(value.getMaxLevel());
        out.name("enabled").value(value.isEnabled());
        out.endObject();
    }

    @Override
    public ClaimEffectImpl read(JsonReader in) throws IOException {
        String effectName = null;
        int level = 0;
        int maxLevel = 0;
        boolean enabled = false;

        in.beginObject();
        while (in.hasNext()) {
            switch (in.nextName()) {
                case "effect":
                    effectName = in.nextString();
                    break;
                case "level":
                    level = in.nextInt();
                    break;
                case "maxLevel":
                    maxLevel = in.nextInt();
                    break;
                case "enabled":
                    enabled = in.nextBoolean();
                    break;
                default:
                    in.skipValue();
            }
        }
        in.endObject();
        return new ClaimEffectImpl(Effect.valueOf(effectName), level, maxLevel, enabled);
    }
}
