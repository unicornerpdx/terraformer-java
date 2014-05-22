package com.esri.terraformer;

import java.util.ArrayList;

public abstract class GeoJson<T> extends ArrayList<T> {
    public abstract GeoJsonType getType();
    public abstract String toJson();

    public double[] bbox() {
        return null;
    }

    public static GeoJson<?> decodeJson(String json) {
        return null;
    }
}
