package net.weesli.rozsLib.api;

import lombok.SneakyThrows;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

public class ItemSerializer implements Serializer<ItemStack>{

    @SneakyThrows
    @Override
    public String serialize(ItemStack object) {
        ByteArrayOutputStream io = new ByteArrayOutputStream();
        BukkitObjectOutputStream os = new BukkitObjectOutputStream(io);
        os.writeObject(object);
        os.flush();
        byte[] serialized = io.toByteArray();
        return Base64.getEncoder().encodeToString(serialized);
    }

    @SneakyThrows
    @Override
    public ItemStack deserialize(String value) {
        byte[] bytes = Base64.getDecoder().decode(value);
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        BukkitObjectInputStream is = new BukkitObjectInputStream(in);
        return (ItemStack) is.readObject();
    }


}
