package net.weesli.rozslib.database.serializer;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class SerializerPack {

    private final List<ObjectSerializer> serializers = new ArrayList<>();

    public void registerSerializer(ObjectSerializer serializer) {
        serializers.add(serializer);
    }

    public ObjectSerializer getSerializer(Class<?> clazz) {
        return serializers.stream()
               .filter(s -> s.getClass().equals(clazz))
               .findFirst()
               .orElse(null);
    }
}
