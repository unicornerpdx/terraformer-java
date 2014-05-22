package com.esri.terraformer;

public final class MultiPoint extends Geometry<Point> {
    @Override
    public GeoJsonType getType() {
        return GeoJsonType.MULTIPOINT;
    }

    @Override
    public String toJson() {
        return null;
    }
}
