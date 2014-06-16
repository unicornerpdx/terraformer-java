package com.esri.terraformer;

import java.util.Arrays;
import java.util.Collection;

public final class MultiPolygon extends Geometry<Polygon> {
    static final String ERROR_PREFIX = "Error while parsing MultiPolygon: ";

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
