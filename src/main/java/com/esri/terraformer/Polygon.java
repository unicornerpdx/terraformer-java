package com.esri.terraformer;

import java.util.Arrays;
import java.util.Collection;

public final class Polygon extends Geometry<LineString> {
    static final String ERROR_PREFIX = "Error while parsing Polygon: ";

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
    public GeometryType getType() {
        return GeometryType.POLYGON;
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
    public boolean isEquivalentTo(BaseGeometry<?> obj) {
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

    static boolean polygonContainsOther(Polygon pg1, Polygon pg2) {
        // only compare polygon holes (index > 0)
        for (int i = 1; i < pg1.size(); i++) {
            LineString ls = pg1.get(i);
            if (ls == null) {
                continue;
            }

            boolean success = false;

            for (int j = 1; j < pg2.size(); j++) {
                LineString otherLS = pg2.get(j);
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
