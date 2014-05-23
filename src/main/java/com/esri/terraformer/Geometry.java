package com.esri.terraformer;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

// A layer of abstraction so that our Geometry types can be
// referred to collectively; primarily for supporting the GeometryCollection.
public abstract class Geometry<T> extends GeoJson<T> {
    private static final String EXCEPTION_PREFIX = "Error while parsing Geometry: ";
    protected static final String COORDINATES_NOT_ARRAY = "coordinates not a JSON Array";

    public static final String COORDINATES_KEY = "coordinates";

    @Override
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(toJsonObject(gson));
    }

    @Override
    protected JsonObject toJsonObject(Gson gson) {
        if (gson == null) {
            gson = new Gson();
        }

        JsonObject object = new JsonObject();
        object.addProperty(TYPE_KEY, getType().toString());

        JsonElement coords = gson.toJsonTree(this);
        object.add(COORDINATES_KEY, coords);

        return object;
    }

    /**
     * Package private. Don't call me with null!
     *
     * @param object
     * @return
     * @throws TerraformerException
     */
    static JsonElement getCoordinates(JsonObject object) throws TerraformerException {
        // assume at this point that the type is correct
        JsonElement coordsElem = object.get(COORDINATES_KEY);

        if (coordsElem == null) {
            throw new TerraformerException(EXCEPTION_PREFIX + "\"coordinates\": key not found");
        }

        return coordsElem;
    }

    /**
     * Package private.
     *
     * @param coordsElem
     * @return
     * @throws TerraformerException
     */
    static JsonArray getCoordinateArray(JsonElement coordsElem, int minSize) throws TerraformerException {
        JsonArray coords = arrayFromElement(coordsElem, COORDINATES_NOT_ARRAY);

        if (coords.size() < minSize) {
            throw new TerraformerException(EXCEPTION_PREFIX + "coordinate array too small (< " + minSize + ")");
        }

        return coords;
    }
}
