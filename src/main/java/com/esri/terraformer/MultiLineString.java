package com.esri.terraformer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Arrays;
import java.util.Collection;

public class MultiLineString extends Geometry<LineString> {
    private static final String ERROR_PREFIX = "Error while parsing MultiLineString: ";

    /**
     * A valid MultiLineString contains 0 or more non-null {@link LineString}'s.
     *
     * @param lineStrings
     */
    public MultiLineString(LineString... lineStrings) {
        addAll(Arrays.asList(lineStrings));
    }

    public MultiLineString(int initialCapacity) {
        super(initialCapacity);
    }

    public MultiLineString(Collection<LineString> c) {
        super(c);
    }

    @Override
    public GeoJsonType getType() {
        return GeoJsonType.MULTILINESTRING;
    }

    @Override
    public boolean isValid() {
        for (LineString ls : this) {
            if (ls == null || !ls.isValid()) {
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

        MultiLineString other;
        try {
            other = (MultiLineString) obj;
        } catch (ClassCastException e) {
            return false;
        }

        // gotta do contains in both directions to account for duplicates that exist only on one side.
        return multiLineStringContainsOther(this, other) && multiLineStringContainsOther(other, this);
    }

    public static MultiLineString decodeMultiLineString(String multiLineStringJSON) throws TerraformerException {
        if (isEmpty(multiLineStringJSON)) {
            throw new IllegalArgumentException(TerraformerException.JSON_STRING_EMPTY);
        }

        JsonObject object = getObject(multiLineStringJSON, ERROR_PREFIX);
        if (!(getType(object) == GeoJsonType.MULTILINESTRING)) {
            throw new TerraformerException(ERROR_PREFIX, TerraformerException.NOT_OF_TYPE + "\"MultiLineString\"");
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
    static MultiLineString fromJsonObject(JsonObject object) throws TerraformerException {
        // assume the type has already been checked
        JsonArray coords = getCoordinateArray(getCoordinates(object, ERROR_PREFIX), 0, ERROR_PREFIX);

        MultiLineString returnVal = new MultiLineString();
        for (JsonElement elem : coords) {
            returnVal.add(LineString.fromCoordinates(elem));
        }

        return returnVal;
    }

    static boolean multiLineStringContainsOther(MultiLineString mls1, MultiLineString mls2) {
        for (LineString ls : mls1) {
            boolean success = false;

            for (LineString otherLS : mls2) {
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
