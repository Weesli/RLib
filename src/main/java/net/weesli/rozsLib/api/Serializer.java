package net.weesli.rozsLib.api;

public interface Serializer<T> {
    String serialize(T object);
    T deserialize(String value);
}