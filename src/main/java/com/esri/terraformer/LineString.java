package com.esri.terraformer;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public final class LineString extends Geometry<Point> {
    static final String ERROR_PREFIX = "Error while parsing LineString: ";

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
    public GeometryType getType() {
        return GeometryType.LINESTRING;
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
