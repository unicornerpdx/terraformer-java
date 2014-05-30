package com.esri.terraformer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class LineString extends Geometry<Point> {
    private static final String ERROR_PREFIX = "Error while parsing LineString: ";

    /**
     * A valid LineString contains 2 or more non-null {@link Point}'s.
     *
     * A LineString with length > 4, and first and last position the same, is a Linear Ring.
     * You can use {@link LineString#isLinearRing} to determine this for any given LineString.
     *
     * @param points
     */
    public LineString(Point... points) {
        addAll(Arrays.asList(points));
    }

    public LineString(int initialCapacity) {
        super(initialCapacity);
    }

    public LineString(Collection<Point> c) {
        super(c);
    }

    @Override
    public GeoJsonType getType() {
        return GeoJsonType.LINESTRING;
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
        LineString other;
        try {
            other = (LineString) obj;
        } catch (ClassCastException e) {
            return false;
        }

        boolean isLinearRing = isLinearRing();
        if (isLinearRing != other.isLinearRing()) {
            return false;
        }

        if (isLinearRing) {
            return compareLinearRings(this, other);
        }

        LineString reversed = new LineString(other);
        Collections.reverse(reversed);

        return equals(other) || equals(reversed);
    }

    public boolean isLinearRing() {
        return size() > 3 && get(0).equals(get(size() -1));
    }

    public static LineString decodeLineString(String lineStringJSON) throws TerraformerException {
        if (isEmpty(lineStringJSON)) {
            throw new IllegalArgumentException(TerraformerException.JSON_STRING_EMPTY);
        }

        JsonObject object = getObject(lineStringJSON, ERROR_PREFIX);
        if (!(getType(object) == GeoJsonType.LINESTRING)) {
            throw new TerraformerException(ERROR_PREFIX, TerraformerException.NOT_OF_TYPE + "\"LineString\"");
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
    static LineString fromJsonObject(JsonObject object) throws TerraformerException {
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
    static LineString fromCoordinates(JsonElement coordsElem) throws TerraformerException {
        JsonArray coords = getCoordinateArray(coordsElem, 2, ERROR_PREFIX);

        LineString returnVal = new LineString();
        for (JsonElement elem : coords) {
            returnVal.add(Point.fromCoordinates(elem));
        }

        return returnVal;
    }

    /**
     * Package private. Might return null.
     *
     * @param lr1
     * @param lr2
     * @return
     */
    static Boolean compareLinearRings(LineString lr1, LineString lr2) {
        if (lr1.isLinearRing() != lr2.isLinearRing()) {
            return false;
        }

        if (!lr1.isLinearRing()) {
            return null;
        }

        if (lr1.size() != lr2.size()) {
            return false;
        }

        // copy lr1 and lr2 for editing
        LineString lr1Copy = new LineString(lr1);
        LineString lr2Copy = new LineString(lr2);
        LineString lr2Reverse = new LineString(lr2);
        Collections.reverse(lr2Reverse);

        // remove wrap points
        int size = lr1.size() - 1;
        lr1Copy.remove(size);
        lr2Copy.remove(size);
        lr2Reverse.remove(size);

        // Rotate in both directions and compare at each rotation.
        for (int i = 0; i < size; i++) {
            Collections.rotate(lr2Copy, i);
            Collections.rotate(lr2Reverse, i);
            if (lr1Copy.equals(lr2Copy) || lr1Copy.equals(lr2Reverse)) {
                return true;
            }
        }

        return false;
    }
}
