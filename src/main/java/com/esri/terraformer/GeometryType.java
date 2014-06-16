package com.esri.terraformer;

public enum GeometryType {
    POINT("Point"),
    MULTIPOINT("MultiPoint"),
    LINESTRING("LineString"),
    MULTILINESTRING("MultiLineString"),
    POLYGON("Polygon"),
    MULTIPOLYGON("MultiPolygon"),
    GEOMETRYCOLLECTION("GeometryCollection"),
    FEATURE("Feature"),
    FEATURECOLLECTION("FeatureCollection");

    private final String jsonValue;

    private GeometryType(String jsonValue) {
        this.jsonValue = jsonValue;
    }

    @Override
    public String toString() {
        return jsonValue;
    }

    public static GeometryType fromJson(String jsonValue) {
        return valueOf(jsonValue.toUpperCase());
    }
}
