package com.esri.terraformer;

public final class Point extends Geometry<Double> {
    @Override
    public GeoJsonType getType() {
        return GeoJsonType.POINT;
    }

    @Override
    public String toJson() {
        return null;
    }
}
