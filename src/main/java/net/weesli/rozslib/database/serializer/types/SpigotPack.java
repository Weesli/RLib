package net.weesli.rozslib.database.serializer.types;

import net.weesli.rozslib.database.serializer.ObjectSerializer;
import net.weesli.rozslib.database.serializer.SerializerPack;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.UUID;

/**
 *
 * Default Spigot Object Serializers
 * @author Weesli
 *
 */
public class SpigotPack extends SerializerPack {

    public SpigotPack(){
        registerSerializer(new LocationSerializer());
        registerSerializer(new ItemStackSerializer());
        registerSerializer(new UUIDSerializer());
    }

    /**
     * Serializer for Bukkit Location
     */
    static class LocationSerializer implements ObjectSerializer<Location>{

        @Override
        public String serialize(Location object) {
            return object.getWorld().getName() + ":" + object.getX() + ":" + object.getY() + ":" + object.getZ() + ":" + object.getPitch() + ":" + object.getYaw();
        }

        @Override
        public Object deserialize(String value) {
            String[] parts = value.split(":");
            if(parts.length!= 6){
                throw new IllegalArgumentException("Invalid location data: " + value);
            }
            return new Location(Bukkit.getWorld(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3]), Float.parseFloat(parts[4]), Float.parseFloat(parts[5]));
        }

        @Override
        public boolean isType(Class<?> clazz) {
            return Location.class.isAssignableFrom(clazz);
        }
    }

    /**
     * Serializer for Bukkit ItemStack
     */
    static class ItemStackSerializer implements ObjectSerializer<ItemStack>{

        @Override
        public String serialize(ItemStack object) {
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                 BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {
                dataOutput.writeObject(object);
                return new String(Base64Coder.encode(outputStream.toByteArray()));
            } catch (Exception exception) {
                throw new RuntimeException("Unable to serialize itemstack", exception);
            }
        }

        @Override
        public Object deserialize(String value) {
            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decode(value));
                 BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {
                return dataInput.readObject();
            } catch (Exception exception) {
                throw new RuntimeException("Unable to deserialize itemstack", exception);
            }
        }

        @Override
        public boolean isType(Class<?> clazz) {
            return ItemStack.class.isAssignableFrom(clazz);
        }
    }

    /**
     * Serializer for Java UUID
     */
    static class UUIDSerializer implements ObjectSerializer<UUID>{

        @Override
        public String serialize(UUID object) {
            return object.toString();
        }

        @Override
        public Object deserialize(String value) {
            return UUID.fromString(value);
        }

        @Override
        public boolean isType(Class<?> clazz) {
            return UUID.class.isAssignableFrom(clazz);
        }
    }

}
