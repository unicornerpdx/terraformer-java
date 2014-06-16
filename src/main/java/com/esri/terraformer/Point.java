package com.esri.terraformer;

import java.util.Arrays;
import java.util.Collection;

public final class Point extends Geometry<Double> {
    static final String ERROR_PREFIX = "Error while parsing Point: ";

    /**
     * A Valid Point contains 2 or more non-null {@link Double}'s.
     *
     * @param coords
     */
    public Point(Double... coords) {
        addAll(Arrays.asList(coords));
    }

    public Point(int initialCapacity) {
        super(initialCapacity);
    }

    public Point(Collection<Double> c) {
        super(c);
    }

    @Override
    public GeometryType getType() {
        return GeometryType.POINT;
    }

    @Override
    public boolean isValid() {
        for (Double dbl : this) {
            if (dbl == null) {
                return false;
            }
        }

        return size() > 1;
    }

    @Override
    public boolean isEquivalentTo(BaseGeometry<?> obj) {
        return obj != null && obj.getClass() == Point.class && equals(obj);
    }
}
