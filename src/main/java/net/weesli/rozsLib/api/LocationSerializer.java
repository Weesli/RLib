package net.weesli.rozsLib.api;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationSerializer implements Serializer<Location> {

    @Override
    public String serialize(Location object) {
        return object.getWorld().getName() + ":" + object.getX() + ":" + object.getY() + ":" + object.getZ() + ":" + object.getPitch() + ":" + object.getYaw();
    }

    @Override
    public Location deserialize(String value) {
        return new Location(
                Bukkit.getWorld(value.split(":")[0]),
                Double.parseDouble(value.split(":")[1]),
                Double.parseDouble(value.split(":")[2]),
                Double.parseDouble(value.split(":")[3]),
                Float.parseFloat(value.split(":")[5]),
                Float.parseFloat(value.split(":")[4])
        );
    }
}
