package com.esri.terraformer;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;

public abstract class GeoJson<T> extends ArrayList<T> {
    public static final String TYPE_KEY = "type";

    /**
     * Returns an enum representing one of the GeoJSON types.  See {@link GeoJsonType}.
     *
     * @return
     */
    public abstract GeoJsonType getType();

    /**
     * Get the GeoJSON String representation of the object.
     *
     * @return
     */
    public abstract String toJson();

    /**
     * Let's you know whether your object is up to GeoJson spec.
     *
     * When inflating an object from a JSON String, you'll get an exception if the String
     * is not valid.  This method is mostly intended for checking objects you have created manually
     * or edited after inflation.
     *
     * @return
     */
    public abstract boolean isValid();

    /**
     * Warning: This may be very costly for large Geometries. **Use with discretion**
     *
     * Performs complete comparison between GeoJson objects, include equivalent permutations/rotations
     * for MultiPolygons, Polygons, MultiLineStrings and MultiPoints.
     *
     * @param obj
     * @return
     */
    public abstract boolean isEquivalentTo(GeoJson<?> obj);

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
     * @param obj1
     * @param obj2
     * @return
     */
    static Boolean naiveEquals(GeoJson<?> obj1, GeoJson<?> obj2) {
        if (obj1 == null || obj2 == null) {
            return false;
        }

        if (obj1.getType() != obj2.getType()) {
            return false;
        }

        if (obj1.getClass() != obj2.getClass()) {
            return false;
        }

        if (obj1.size() != obj2.size()) {
            return false;
        }

        if (obj1 == obj2) {
            return true;
        }

        if (obj1.equals(obj2)) {
            return true;
        }

        return null;
    }

    /**
     * Package private.
     *
     * @param json
     * @return
     * @throws TerraformerException
     */
    static JsonObject getObject(String json, String errorPrefix) throws TerraformerException {
        Gson gson = new Gson();
        JsonObject object;

        try {
            JsonElement objElem = gson.fromJson(json, JsonElement.class);
            object = objElem.getAsJsonObject();
        } catch (RuntimeException e) {
            throw new TerraformerException(errorPrefix, TerraformerException.NOT_A_JSON_OBJECT);
        }

        return object;
    }

    /**
     * Package private.
     *
     * @param objectElem
     * @param errorPrefix
     * @return
     * @throws TerraformerException
     */
    static JsonObject objectFromElement(JsonElement objectElem, String errorPrefix) throws TerraformerException {
        JsonObject object;
        try {
            object = objectElem.getAsJsonObject();
        } catch (RuntimeException e) {
            throw new TerraformerException(errorPrefix, TerraformerException.ELEMENT_NOT_OBJECT);
        }

        return object;
    }

    /**
     * Package private.
     *
     * @param arrayElem
     * @param errorPrefix
     * @return
     * @throws TerraformerException
     */
    static JsonArray arrayFromElement(JsonElement arrayElem, String errorPrefix) throws TerraformerException {
        JsonArray array;
        try {
            array = arrayElem.getAsJsonArray();
        } catch (RuntimeException e) {
            throw new TerraformerException(errorPrefix, TerraformerException.ELEMENT_NOT_ARRAY);
        }

        return array;
    }

    /**
     * Package private.
     *
     * @param object
     * @return
     */
    static GeoJsonType getType(JsonObject object) {
        if (object == null) {
            return null;
        }

        JsonElement typeElem = object.get(TYPE_KEY);

        if (typeElem == null) {
            return null;
        }

        String typeString;
        try {
            typeString = typeElem.getAsString();
        } catch (RuntimeException e) {
            return null;
        }

        GeoJsonType foundType;
        try {
            foundType = GeoJsonType.fromJson(typeString);
        } catch (RuntimeException e) {
            return null;
        }

        return foundType;
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

    static GeoJson<?> geoJsonFromElement(JsonElement gjElem, String errorPrefix) throws TerraformerException {
        JsonObject gjObject = objectFromElement(gjElem, errorPrefix);

        GeoJsonType type = getType(gjObject);
        if (type == null) {
            throw new TerraformerException(errorPrefix, TerraformerException.ELEMENT_UNKNOWN_TYPE);
        }

        // TODO: add the rest of the types here
        GeoJson<?> geoJson = null;
        switch (type) {
            case POINT:
                geoJson = Point.fromJsonObject(gjObject);
                break;
            case MULTIPOINT:
                geoJson = MultiPoint.fromJsonObject(gjObject);
                break;
            case GEOMETRYCOLLECTION:
                geoJson = GeometryCollection.fromJsonObject(gjObject);
                break;
        }

        return geoJson;
    }
}
