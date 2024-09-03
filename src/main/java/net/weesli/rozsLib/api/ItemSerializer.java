package net.weesli.rozsLib.api;

import lombok.SneakyThrows;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class ItemSerializer implements Serializer<ItemStack>{

    @SneakyThrows
    @Override
    public String serialize(ItemStack item) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             BukkitObjectOutputStream oos = new BukkitObjectOutputStream(baos)) {
            oos.writeObject(item);
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    @Override
    public ItemStack deserialize(String value) {
        try {
            byte[] data = Base64.getDecoder().decode(value);
            try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
                 BukkitObjectInputStream ois = new BukkitObjectInputStream(bais)) {
                return (ItemStack) ois.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


}
