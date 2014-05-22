package com.esri.terraformer;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;

public abstract class GeoJson<T> extends ArrayList<T> {
    private static final String EXCEPTION_PREFIX = "Error while parsing GeoJson Object: ";

    public static final String TYPE_KEY = "type";

    public abstract GeoJsonType getType();
    public abstract String toJson();
    public abstract boolean isValid();
    protected abstract JsonObject toJsonObject(Gson gson);

    public double[] bbox() {
        return null;
    }

    public static GeoJson<?> decodeJson(String json) {
        // create object, check for type value
        // if type value is missing or not a string, exception
        // use GeoJsonType.valueOf() to convert to enum
        // (inside try catch for runtime error when the string doesn't match)
        // switch on enum and use class specific methods for generation.
        return null;
    }

    /**
     * Package private.
     *
     * @param json
     * @return
     * @throws TerraformerException
     */
    static JsonObject getObject(String json) throws TerraformerException {
        Gson gson = new Gson();
        JsonObject object;

        try {
            JsonElement objElem = gson.fromJson(json, JsonElement.class);
            object = objElem.getAsJsonObject();
        } catch (RuntimeException e) {
            throw new TerraformerException(EXCEPTION_PREFIX + "not a JSON Object");
        }

        return object;
    }

    /**
     * Package private. Don't call me with null!
     *
     * @param objectElem
     * @param error
     * @return
     * @throws TerraformerException
     */
    static JsonObject objectFromElement(JsonElement objectElem, String error) throws TerraformerException {
        JsonObject object;
        try {
            object = objectElem.getAsJsonObject();
        } catch (RuntimeException e) {
            throw new TerraformerException(EXCEPTION_PREFIX + error);
        }

        return object;
    }

    /**
     * Package private. Don't call me with null!
     *
     * @param arrayElem
     * @param error
     * @return
     * @throws TerraformerException
     */
    static JsonArray arrayFromElement(JsonElement arrayElem, String error) throws TerraformerException {
        JsonArray array;
        try {
            array = arrayElem.getAsJsonArray();
        } catch (RuntimeException e) {
            throw new TerraformerException(EXCEPTION_PREFIX + error);
        }

        return array;
    }

    /**
     * Package private.
     *
     * @param object
     * @param type
     * @return
     */
    static boolean checkType(JsonObject object, GeoJsonType type) {
        if (object == null) {
            return false;
        }

        JsonElement typeElem = object.get(TYPE_KEY);

        if (typeElem == null) {
            return false;
        }

        String typeString;
        try {
            typeString = typeElem.getAsString();
        } catch (RuntimeException e) {
            return false;
        }

        return GeoJsonType.fromJson(typeString) == type;
    }

    /**
     * Package private.
     *
     * @param json
     * @return
     */
    static boolean isEmpty(String json) {
        return json == null || json.length() <= 0;
    }
}
