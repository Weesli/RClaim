package net.weesli.rClaim.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.weesli.rClaim.modal.ClaimEffect;

import java.util.UUID;

public class GsonProvider {
    public static Gson getGson() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(ClaimEffect.class, new ClaimEffectTypeAdapter());
        return builder.create();
    }
}
