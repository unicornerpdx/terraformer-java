package com.esri.terraformer;

public enum GeoJsonType {
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

    private GeoJsonType(String jsonValue) {
        this.jsonValue = jsonValue;
    }

    @Override
    public String toString() {
        return jsonValue;
    }

    public static GeoJsonType fromJson(String jsonValue) {
        return valueOf(jsonValue.toUpperCase());
    }
}
