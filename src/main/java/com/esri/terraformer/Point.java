package com.esri.terraformer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Arrays;

public final class Point extends Geometry<Double> {
    private static final String EXCEPTION_PREFIX = "Error while parsing Point: ";

    /**
     * A Valid Point contains 2 or more non-null {@link Double}'s.
     *
     * @param coords
     */
    public Point(Double... coords) {
        addAll(Arrays.asList(coords));
    }

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

    @Override
    public boolean isEquivalentTo(GeoJson<?> obj) {
        return obj.getClass() == Point.class && equals(obj);

    }

    public static Point decodePoint(String json) throws TerraformerException {
        if (isEmpty(json)) {
            throw new IllegalArgumentException(TerraformerException.JSON_STRING_EMPTY);
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
        if (!(getType(object) == GeoJsonType.POINT)) {
            throw new TerraformerException(EXCEPTION_PREFIX, TerraformerException.NOT_OF_TYPE + "\"Point\"");
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
                throw new TerraformerException(EXCEPTION_PREFIX, TerraformerException.COORDINATE_NOT_NUMERIC + elem);
            }

            returnVal.add(coord);
        }

        return returnVal;
    }
}
