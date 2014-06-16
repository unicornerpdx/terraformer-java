package com.esri.terraformer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Arrays;
import java.util.Collection;

public final class MultiPolygon extends Geometry<Polygon> {
    private static final String ERROR_PREFIX = "Error while parsing MultiPolygon: ";

    /**
     * A valid MultiPolygon contains 0 or more non-null {@link Polygon}'s.
     *
     * @param polygons
     */
    public MultiPolygon(Polygon... polygons) {
        addAll(Arrays.asList(polygons));
    }

    public MultiPolygon(int initialCapacity) {
        super(initialCapacity);
    }

    public MultiPolygon(Collection<Polygon> c) {
        super(c);
    }

    @Override
    public GeometryType getType() {
        return GeometryType.MULTIPOLYGON;
    }

    @Override
    public boolean isValid() {
        for (Polygon pg : this) {
            if (pg == null || !pg.isValid()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean isEquivalentTo(BaseGeometry<?> obj) {
        Boolean equal = naiveEquals(this, obj);
        if (equal != null) {
            return equal;
        }

        MultiPolygon other;
        try {
            other = (MultiPolygon) obj;
        } catch (ClassCastException e) {
            return false;
        }

        // gotta do contains in both directions to account for duplicates that exist only on one side.
        return multiPolygonContainsOther(this, other) && multiPolygonContainsOther(other, this);
    }

    public static MultiPolygon decodeMultiPolygon(String multiPolygonJSON) throws TerraformerException {
        if (isEmpty(multiPolygonJSON)) {
            throw new IllegalArgumentException(TerraformerException.JSON_STRING_EMPTY);
        }

        JsonObject object = getObject(multiPolygonJSON, ERROR_PREFIX);
        if (!(getType(object) == GeometryType.MULTIPOLYGON)) {
            throw new TerraformerException(ERROR_PREFIX, TerraformerException.NOT_OF_TYPE + "\"MultiPolygon\"");
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
    static MultiPolygon fromJsonObject(JsonObject object) throws TerraformerException {
        // assume the type has already been checked
        JsonArray coords = getCoordinateArray(getCoordinates(object, ERROR_PREFIX), 0, ERROR_PREFIX);

        MultiPolygon returnVal = new MultiPolygon();
        for (JsonElement elem : coords) {
            returnVal.add(Polygon.fromCoordinates(elem));
        }

        return returnVal;
    }

    static boolean multiPolygonContainsOther(MultiPolygon mpg1, MultiPolygon mpg2) {
        for (Polygon pg : mpg1) {
            if (pg == null) {
                continue;
            }

            boolean success = false;

            for (Polygon otherPg : mpg2) {
                if (pg.isEquivalentTo(otherPg)) {
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
