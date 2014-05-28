package com.esri.terraformer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Arrays;
import java.util.Collection;

public final class MultiPoint extends Geometry<Point> {
    private static final String ERROR_PREFIX = "Error while parsing MultiPoint: ";

    /**
     * A valid MultiPoint contains 2 or more non-null {@link Point}'s.
     *
     * @param points
     */
    public MultiPoint(Point... points) {
        addAll(Arrays.asList(points));
    }

    public MultiPoint(int initialCapacity) {
        super(initialCapacity);
    }

    public MultiPoint(Collection<Point> c) {
        super(c);
    }

    @Override
    public GeoJsonType getType() {
        return GeoJsonType.MULTIPOINT;
    }

    @Override
    public boolean isValid() {
        for (Point p : this) {
            if (p == null || !p.isValid()) {
                return false;
            }
        }

        return size() > 1;
    }

    @Override
    public boolean isEquivalentTo(GeoJson<?> obj) {
        Boolean equal = naiveEquals(this, obj);
        if (equal != null) {
            return equal;
        }

        // gotta do contains in both directions to account for duplicates that exist only on one side.
        return obj.containsAll(this) && containsAll(obj);
    }

    public static MultiPoint decodeMultiPoint(String multiPointJSON) throws TerraformerException {
        if (isEmpty(multiPointJSON)) {
            throw new IllegalArgumentException(TerraformerException.JSON_STRING_EMPTY);
        }

        JsonObject object = getObject(multiPointJSON, ERROR_PREFIX);
        if (!(getType(object) == GeoJsonType.MULTIPOINT)) {
            throw new TerraformerException(ERROR_PREFIX, TerraformerException.NOT_OF_TYPE + "\"MultiPoint\"");
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
    static MultiPoint fromJsonObject(JsonObject object) throws TerraformerException {
        // assume the type has already been checked
        JsonArray coords = getCoordinateArray(getCoordinates(object, ERROR_PREFIX), 2, ERROR_PREFIX);

        MultiPoint returnVal = new MultiPoint();
        for (JsonElement elem : coords) {
            returnVal.add(Point.fromCoordinates(elem));
        }

        return returnVal;
    }
}
