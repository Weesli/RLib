package net.weesli.rozslib.database.serializer;

public interface ObjectSerializer<T> {

    String serialize(T object);
    Object deserialize(String value);

    boolean canSerialize(Class<?> clazz);
}
