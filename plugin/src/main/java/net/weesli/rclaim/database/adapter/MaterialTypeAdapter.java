package net.weesli.rclaim.database.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.bukkit.Material;

import java.io.IOException;

public class MaterialTypeAdapter extends TypeAdapter<Material> {
    @Override
    public void write(JsonWriter out, Material value) throws IOException {
        if (value == null){
            out.nullValue();
            return;
        }
        out.beginObject();
        out.name("material").value(value.name());
        out.endObject();
    }

    @Override
    public Material read(JsonReader in) throws IOException {
        String materialName = null;
        in.beginObject();
        while (in.hasNext()) {
            if (in.nextName().equals("material")) {
                materialName = in.nextString();
            } else {
                in.skipValue();
            }
        }
        in.endObject();
        return Material.valueOf(materialName);
    }
}
