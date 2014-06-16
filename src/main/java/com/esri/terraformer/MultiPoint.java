package com.esri.terraformer;

import java.util.Arrays;
import java.util.Collection;

public final class MultiPoint extends Geometry<Point> {
    static final String ERROR_PREFIX = "Error while parsing MultiPoint: ";

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
    public GeometryType getType() {
        return GeometryType.MULTIPOINT;
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
    public boolean isEquivalentTo(BaseGeometry<?> obj) {
        Boolean equal = naiveEquals(this, obj);
        if (equal != null) {
            return equal;
        }

        // gotta do contains in both directions to account for duplicates that exist only on one side.
        return obj.containsAll(this) && containsAll(obj);
    }
}
