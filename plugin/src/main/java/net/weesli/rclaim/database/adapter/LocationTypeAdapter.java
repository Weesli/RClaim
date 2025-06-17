package net.weesli.rclaim.database.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.IOException;

public class LocationTypeAdapter extends TypeAdapter<Location> {
    @Override
    public void write(JsonWriter out, Location value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        out.beginObject();
        out.name("world").value(value.getWorld().getName());
        out.name("x").value(value.getX());
        out.name("y").value(value.getY());
        out.name("z").value(value.getZ());
        out.name("yaw").value(value.getYaw());
        out.name("pitch").value(value.getPitch());
        out.endObject();
    }

    @Override
    public Location read(JsonReader in) throws IOException {
        String worldName = null;
        double x = 0;
        double y = 0;
        double z = 0;
        float yaw = 0;
        float pitch = 0;

        in.beginObject();
        while (in.hasNext()) {
            switch (in.nextName()) {
                case "world":
                    worldName = in.nextString();
                    break;
                case "x":
                    x = in.nextDouble();
                    break;
                case "y":
                    y = in.nextDouble();
                    break;
                case "z":
                    z = in.nextDouble();
                    break;
                case "yaw":
                    yaw = (float) in.nextDouble();
                    break;
                case "pitch":
                    pitch = (float) in.nextDouble();
                    break;
                default:
                    in.skipValue();
            }
        }
        in.endObject();
        return new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
    }
}
