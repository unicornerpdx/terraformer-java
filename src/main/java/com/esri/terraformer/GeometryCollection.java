package com.esri.terraformer;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Arrays;

// A GeometryCollection contains Geometries, and is itself a Geometry. A GeometryCollection
// may contain other GeometryCollections.
public class GeometryCollection extends Geometry<Geometry<?>> {
    private static final String ERROR_PREFIX = "Error while parsing GeometryCollection: ";

    public static final String GEOMETRIES_KEY = "geometries";

    /**
     * A valid GeometryCollection contains 0 or more non-null {@link Geometry}'s.
     *
     * @param geometries
     */
    public GeometryCollection(Geometry<?>... geometries) {
        addAll(Arrays.asList(geometries));
    }

    @Override
    public GeoJsonType getType() {
        return GeoJsonType.GEOMETRYCOLLECTION;
    }

    @Override
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(toJsonObject(gson));
    }

    @Override
    public boolean isValid() {
        for (Geometry geo : this) {
            if (geo == null || !geo.isValid()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean isEquivalentTo(GeoJson<?> obj) {
        Boolean equal = naiveEquals(this, obj);
        if (equal != null) {
            return equal;
        }

        GeometryCollection other;
        try {
            other = (GeometryCollection) obj;
        } catch (ClassCastException e) {
            return false;
        }

        // gotta do contains in both directions to account for duplicates that exist only on one side.
        return geometryCollectionContainsOther(this, other) && geometryCollectionContainsOther(other, this);
    }

    @Override
    protected JsonObject toJsonObject(Gson gson) {
        JsonObject object = new JsonObject();
        object.addProperty(TYPE_KEY, getType().toString());

        JsonArray geometries = new JsonArray();

        for (Geometry geo : this) {
            geometries.add(geo.toJsonObject(gson));
        }

        object.add(GEOMETRIES_KEY, geometries);

        return object;
    }

    public static GeometryCollection decodeGeometryCollection(String geometryCollectionJSON)
            throws TerraformerException {
        if (isEmpty(geometryCollectionJSON)) {
            throw new IllegalArgumentException(TerraformerException.JSON_STRING_EMPTY);
        }

        JsonObject object = getObject(geometryCollectionJSON, ERROR_PREFIX);
        if (!(getType(object) == GeoJsonType.GEOMETRYCOLLECTION)) {
            throw new TerraformerException(ERROR_PREFIX,
                    TerraformerException.NOT_OF_TYPE + "\"GeometryCollection\"");
        }

        return fromJsonObject(object);
    }

    static GeometryCollection fromJsonObject(JsonObject object) throws TerraformerException {
        // assume the type has already been checked
        JsonElement geomsElem = object.get(GEOMETRIES_KEY);

        if (geomsElem == null) {
            throw new TerraformerException(ERROR_PREFIX, TerraformerException.GEOMETRIES_KEY_NOT_FOUND);
        }

        JsonArray geoms = arrayFromElement(geomsElem, ERROR_PREFIX);

        GeometryCollection returnVal = new GeometryCollection();

        for (JsonElement elem : geoms) {
            returnVal.add(geometryFromObjectElement(elem, ERROR_PREFIX));
        }

        return returnVal;
    }

    static boolean geometryCollectionContainsOther(GeometryCollection gc1, GeometryCollection gc2) {
        for (Geometry<?> geo : gc1) {
            boolean success = false;

            for (Geometry<?> otherGeo : gc2) {
                if (otherGeo.isEquivalentTo(geo)) {
                    success = true;
                    break;
                }
            }

            if (!success) {
                return false;
            }
        }

        return true;
    }
}
