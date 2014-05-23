package com.esri.terraformer;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

// A GeometryCollection contains Geometries, and is itself a Geometry. A GeometryCollection
// may contain other GeometryCollections.
public class GeometryCollection extends Geometry<Geometry<?>> {
    public static final String GEOMETRIES_KEY = "geometries";

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

    @Override
    public boolean isValid() {
        for (Geometry geo : this) {
            if (!geo.isValid()) {
                return false;
            }
        }

        return size() > 0;
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
        return compareGeometries(this, other) && compareGeometries(other, this);
    }

    private static boolean compareGeometries(GeometryCollection gc1, GeometryCollection gc2) {
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
