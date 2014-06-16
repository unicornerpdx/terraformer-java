package com.esri.terraformer;

public final class Terraformer {
    public static Serializer<?> serializer;
    public static Deserializer<?> deserializer;

    public interface Deserializer<T> {
        public BaseGeometry deserialize(T t);
    }

    public interface Serializer<T> {
        public T serialize(BaseGeometry geo);
    }

    public static boolean isEmpty(String json) {
        return json == null || json.length() <= 0;
    }
}
