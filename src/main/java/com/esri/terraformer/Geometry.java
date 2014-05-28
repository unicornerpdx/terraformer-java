package com.esri.terraformer;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Collection;

// A layer of abstraction so that our Geometry types can be
// referred to collectively; primarily for supporting the GeometryCollection.
public abstract class Geometry<T> extends GeoJson<T> {
    public static final String COORDINATES_KEY = "coordinates";

    protected Geometry() {}

    protected Geometry(int initialCapacity) {
        super(initialCapacity);
    }

    protected Geometry(Collection<T> c) {
        super(c);
    }

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
    static JsonElement getCoordinates(JsonObject object, String errorPrefix) throws TerraformerException {
        JsonElement coordsElem = object.get(COORDINATES_KEY);

        if (coordsElem == null) {
            throw new TerraformerException(errorPrefix, TerraformerException.COORDINATES_KEY_NOT_FOUND);
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
    static JsonArray getCoordinateArray(JsonElement coordsElem, int minSize, String errorPrefix)
            throws TerraformerException {
        JsonArray coords = arrayFromElement(coordsElem, errorPrefix);

        if (coords.size() < minSize) {
            throw new TerraformerException(errorPrefix, TerraformerException.COORDINATE_ARRAY_TOO_SHORT +
                    minSize + ")");
        }

        return coords;
    }

    static Geometry<?> geometryFromObjectElement(JsonElement geomElem, String errorPrefix) throws TerraformerException {
        GeoJson<?> geoJson = geoJsonFromObjectElement(geomElem, errorPrefix);
        if (!(geoJson instanceof Geometry<?>)) {
            throw new TerraformerException(errorPrefix, TerraformerException.ELEMENT_NOT_GEOMETRY);
        }

        return (Geometry<?>) geoJson;
    }
}
