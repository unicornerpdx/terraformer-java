package com.esri.terraformer;

// A GeometryCollection contains Geometries, and is itself a Geometry. A GeometryCollection
// may contain other GeometryCollections.
public class GeometryCollection extends Geometry<Geometry<?>> {
    @Override
    public GeoJsonType getType() {
        return GeoJsonType.GEOMETRYCOLLECTION;
    }

    @Override
    public String toJson() {
        return null;
    }
}
