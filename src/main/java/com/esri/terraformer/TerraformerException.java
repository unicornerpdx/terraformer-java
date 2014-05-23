package com.esri.terraformer;

public class TerraformerException extends Exception {
    public static final String JSON_STRING_EMPTY = "JSON String cannot be empty.";
    public static final String NOT_A_JSON_OBJECT = "not a JSON Object";
    public static final String NOT_OF_TYPE = "not of \"type\":";
    public static final String COORDINATE_NOT_NUMERIC = "coordinate was not numeric: ";
    public static final String COORDINATES_NOT_ARRAY = "coordinates not a JSON Array";
    public static final String COORDINATES_KEY_NOT_FOUND = "\"coordinates\": key not found";
    public static final String COORDINATE_ARRAY_TOO_SHORT = "coordinate array too short (< ";

    public TerraformerException(String prefix, String error) {
        super(prefix + error);
    }
}
