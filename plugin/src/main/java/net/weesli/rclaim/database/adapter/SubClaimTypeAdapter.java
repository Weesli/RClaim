package net.weesli.rclaim.database.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.weesli.rclaim.model.SubClaimImpl;

import java.io.IOException;

public class SubClaimTypeAdapter extends TypeAdapter<SubClaimImpl> {
    @Override
    public void write(JsonWriter out, SubClaimImpl value) throws IOException {
        out.beginObject();
        out.name("mainClaim").value(value.getMainClaim());
        out.name("x").value(value.getX());
        out.name("z").value(value.getZ());
        out.endObject();
    }


    @Override
    public SubClaimImpl read(JsonReader in) throws IOException {
        String mainClaim = null;
        int x = 0;
        int z = 0;

        in.beginObject();
        while (in.hasNext()) {
            switch (in.nextName()) {
                case "mainClaim":
                    mainClaim = in.nextString();
                    break;
                case "x":
                    x = in.nextInt();
                    break;
                case "z":
                    z = in.nextInt();
                    break;
                default:
                    in.skipValue();
            }
        }
        in.endObject();
        return new SubClaimImpl(mainClaim, x, z);
    }

}
