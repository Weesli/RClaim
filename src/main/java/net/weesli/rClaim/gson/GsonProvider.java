package net.weesli.rClaim.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.weesli.rClaim.modal.ClaimEffect;
import net.weesli.rClaim.modal.ClaimTag;

public class GsonProvider {

    public static Gson getGson() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(ClaimEffect.class, new ClaimEffectTypeAdapter());
        builder.registerTypeAdapter(ClaimTag.class, new ClaimTagTypeAdapter());
        return builder.create();
    }
}
