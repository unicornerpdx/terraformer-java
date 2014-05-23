package com.esri.terraformer;

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

        // gotta do contains in both directions to account for duplicates that exist only on one side.
        return obj.containsAll(this) && containsAll(obj);
    }
}
