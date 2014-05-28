package com.esri.terraformer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Arrays;
import java.util.Collection;

public final class Point extends Geometry<Double> {
    private static final String ERROR_PREFIX = "Error while parsing Point: ";

    /**
     * A Valid Point contains 2 or more non-null {@link Double}'s.
     *
     * @param coords
     */
    public Point(Double... coords) {
        addAll(Arrays.asList(coords));
    }

    public Point(int initialCapacity) {
        super(initialCapacity);
    }

    public Point(Collection<Double> c) {
        super(c);
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

    public static Point decodePoint(String pointJSON) throws TerraformerException {
        if (isEmpty(pointJSON)) {
            throw new IllegalArgumentException(TerraformerException.JSON_STRING_EMPTY);
        }

        JsonObject object = getObject(pointJSON, ERROR_PREFIX);
        if (!(getType(object) == GeoJsonType.POINT)) {
            throw new TerraformerException(ERROR_PREFIX, TerraformerException.NOT_OF_TYPE + "\"Point\"");
        }

        return fromJsonObject(object);
    }

    /**
     * Package private.
     *
     * @param object
     * @return
     * @throws TerraformerException
     */
    static Point fromJsonObject(JsonObject object) throws TerraformerException {
        // assume the type has already been checked
        return fromCoordinates(getCoordinates(object, ERROR_PREFIX));
    }

    /**
     * Package private.
     *
     * @param coordsElem
     * @return
     * @throws TerraformerException
     */
    static Point fromCoordinates(JsonElement coordsElem) throws TerraformerException {
        JsonArray coords = getCoordinateArray(coordsElem, 2, ERROR_PREFIX);

        Point returnVal = new Point();
        for (JsonElement elem : coords) {
            Double coord;
            try {
                coord = elem.getAsDouble();
            } catch (RuntimeException e) {
                throw new TerraformerException(ERROR_PREFIX, TerraformerException.COORDINATE_NOT_NUMERIC + elem);
            }

            returnVal.add(coord);
        }

        return returnVal;
    }
}
