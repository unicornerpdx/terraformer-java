package com.esri.terraformer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public final class MultiPoint extends Geometry<Point> {
    private static final String EXCEPTION_PREFIX = "Error while parsing MultiPoint: ";

    @Override
    public GeoJsonType getType() {
        return GeoJsonType.MULTIPOINT;
    }

    @Override
    public boolean isValid() {
        for (Point p : this) {
            if (!p.isValid()) {
                return false;
            }
        }

        return size() > 0;
    }

    public static MultiPoint decodeMultiPoint(String json) throws TerraformerException {
        if (isEmpty(json)) {
            throw new IllegalArgumentException("JSON String cannot be empty.");
        }

        return fromJsonObject(getObject(json));
    }

    /**
     * Package private.
     *
     * @param object
     * @return
     * @throws TerraformerException
     */
    static MultiPoint fromJsonObject(JsonObject object) throws TerraformerException {
        if (!checkType(object, GeoJsonType.MULTIPOINT)) {
            throw new TerraformerException(EXCEPTION_PREFIX + "not of \"type\":\"MultiPoint\"");
        }

        return fromCoordinates(getCoordinates(object));
    }

    /**
     * Package private.
     *
     * @param coordsElem
     * @return
     * @throws TerraformerException
     */
    static MultiPoint fromCoordinates(JsonElement coordsElem) throws TerraformerException {
        JsonArray coords = getCoordinateArray(coordsElem, 1);

        MultiPoint returnVal = new MultiPoint();
        for (JsonElement elem : coords) {
            returnVal.add(Point.fromCoordinates(elem));
        }

        return returnVal;
    }
}
