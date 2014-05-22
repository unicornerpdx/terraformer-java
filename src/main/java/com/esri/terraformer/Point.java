package com.esri.terraformer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public final class Point extends Geometry<Double> {
    private static final String EXCEPTION_PREFIX = "Error while parsing Point: ";

    @Override
    public GeoJsonType getType() {
        return GeoJsonType.POINT;
    }

    @Override
    public boolean isValid() {
        for (Double dbl : this) {
            if (dbl == null) {
                return false;
            }
        }

        return size() > 1;
    }

    public static Point decodePoint(String json) throws TerraformerException {
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
    static Point fromJsonObject(JsonObject object) throws TerraformerException {
        if (!checkType(object, GeoJsonType.POINT)) {
            throw new TerraformerException(EXCEPTION_PREFIX + "not of \"type\":\"Point\"");
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
    static Point fromCoordinates(JsonElement coordsElem) throws TerraformerException {
        JsonArray coords = getCoordinateArray(coordsElem, 2);

        Point returnVal = new Point();
        for (JsonElement elem : coords) {
            Double coord;
            try {
                coord = elem.getAsDouble();
            } catch (RuntimeException e) {
                throw new TerraformerException(EXCEPTION_PREFIX + "coordinate was not numeric: " + elem);
            }

            returnVal.add(coord);
        }

        return returnVal;
    }
}
