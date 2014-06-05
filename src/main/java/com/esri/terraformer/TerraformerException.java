package com.esri.terraformer;

public class TerraformerException extends Exception {
    public static final String JSON_STRING_EMPTY = "JSON String cannot be empty.";
    public static final String NOT_VALID_JSON = "not valid JSON";
    public static final String NOT_A_JSON_OBJECT = "not a JSON Object";
    public static final String NOT_OF_TYPE = "not of \"type\":";
    public static final String COORDINATE_NOT_NUMERIC = "coordinate was not numeric: ";
    public static final String COORDINATES_KEY_NOT_FOUND = "\"coordinates\": key not found";
    public static final String GEOMETRIES_KEY_NOT_FOUND = "\"geometries\": key not found";
    public static final String GEOMETRY_KEY_NOT_FOUND = "\"geometry\": key not found";
    public static final String COORDINATE_ARRAY_TOO_SHORT = "coordinate array too short (< ";
    public static final String INNER_LINESTRING_NOT_RING = "an inner line string was not a linear ring";
    public static final String PROPERTIES_NOT_OBJECT = "value for the \"properties\": key was not a JSON Object";
    public static final String ELEMENT_NOT_GEOMETRY = "element should be a Geometry, but was not";
    public static final String ELEMENT_NOT_ARRAY = "element should be a JSON Array, but was not";
    public static final String ELEMENT_NOT_OBJECT = "element should be a JSON Object, but was not";
    public static final String ELEMENT_UNKNOWN_TYPE = "element had unknown type";

    public TerraformerException(String prefix, String error) {
        super(prefix + error);
    }
}
