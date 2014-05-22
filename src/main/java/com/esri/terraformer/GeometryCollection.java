package com.esri.terraformer;

// A GeometryCollection contains Geometries, and is itself a Geometry. A GeometryCollection
// may contain other GeometryCollections.
public class GeometryCollection extends Geometry<Geometry<?>> {
    public static final String GEOMETRIES_KEY = "geometries";

    @Override
    public GeoJsonType getType() {
        return GeoJsonType.GEOMETRYCOLLECTION;
    }

    @Override
    public String toJson() {
        return null;
    }
}
