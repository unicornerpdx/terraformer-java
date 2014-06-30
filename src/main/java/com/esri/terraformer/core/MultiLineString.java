package com.esri.terraformer.core;

import java.util.Arrays;
import java.util.Collection;

public final class MultiLineString extends Geometry<LineString> {
    public static final String ERROR_PREFIX = "Error while parsing MultiLineString: ";

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
    public GeometryType getType() {
        return GeometryType.MULTILINESTRING;
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
    public boolean isEquivalentTo(BaseGeometry<?> obj) {
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

    static boolean multiLineStringContainsOther(MultiLineString mls1, MultiLineString mls2) {
        for (LineString ls : mls1) {
            if (ls == null) {
                continue;
            }

            boolean success = false;

            for (LineString otherLS : mls2) {
                if (ls.isEquivalentTo(otherLS)) {
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
