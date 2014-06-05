package com.esri.terraformer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Arrays;
import java.util.Collection;

public final class Polygon extends Geometry<LineString> {
    private static final String ERROR_PREFIX = "Error while parsing Polygon: ";

    /**
     * A valid Polygon contains 0 or more non-null {@link LineString}'s, each of which
     * is a valid LinearRing.
     *
     * @param lineStrings
     */
    public Polygon(LineString... lineStrings) {
        addAll(Arrays.asList(lineStrings));
    }

    public Polygon(int initialCapacity) {
        super(initialCapacity);
    }

    public Polygon(Collection<LineString> c) {
        super(c);
    }

    @Override
    public GeoJsonType getType() {
        return GeoJsonType.POLYGON;
    }

    @Override
    public boolean isValid() {
        for (LineString ls : this) {
            if (ls == null || !ls.isLinearRing() || !ls.isValid()) {
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

        Polygon other;
        try {
            other = (Polygon) obj;
        } catch (ClassCastException e) {
            return false;
        }

        // this first linestring must be the same in both polygons
        if (!other.get(0).isEquivalentTo(get(0))) {
            return false;
        }

        // if there are no holes, we are done here.
        if (size() <= 1) {
            return true;
        }

        // gotta do contains in both directions to account for duplicates that exist only on one side.
        return polygonContainsOther(this, other) && polygonContainsOther(other, this);
    }

    public static Polygon decodePolygon(String polygonJSON) throws TerraformerException {
        if (isEmpty(polygonJSON)) {
            throw new IllegalArgumentException(TerraformerException.JSON_STRING_EMPTY);
        }

        JsonObject object = getObject(polygonJSON, ERROR_PREFIX);
        if (!(getType(object) == GeoJsonType.POLYGON)) {
            throw new TerraformerException(ERROR_PREFIX, TerraformerException.NOT_OF_TYPE + "\"Polygon\"");
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
    static Polygon fromJsonObject(JsonObject object) throws TerraformerException {
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
    static Polygon fromCoordinates(JsonElement coordsElem) throws TerraformerException {
        JsonArray coords = getCoordinateArray(coordsElem, 0, ERROR_PREFIX);

        Polygon returnVal = new Polygon();
        for (JsonElement elem : coords) {
            LineString lr = LineString.fromCoordinates(elem);

            if (!lr.isLinearRing()) {
                throw new TerraformerException(ERROR_PREFIX, TerraformerException.INNER_LINESTRING_NOT_RING);
            }

            returnVal.add(lr);
        }

        return returnVal;
    }

    static boolean polygonContainsOther(Polygon pg1, Polygon pg2) {
        // only compare polygon holes (index > 0)
        for (int i = 1; i < pg1.size(); i++) {
            boolean success = false;
            LineString ls = pg1.get(i);

            for (int j = 1; j < pg2.size(); j++) {
                LineString otherLS = pg2.get(j);
                if (otherLS.isEquivalentTo(ls)) {
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
