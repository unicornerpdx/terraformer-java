package com.esri.terraformer.core;

import java.util.Arrays;
import java.util.Collection;

// A GeometryCollection contains Geometries, and is itself a Geometry. A GeometryCollection
// may contain other GeometryCollections.
public final class GeometryCollection extends Geometry<Geometry<?>> {
    public static final String ERROR_PREFIX = "Error while parsing GeometryCollection: ";

    /**
     * A valid GeometryCollection contains 0 or more non-null {@link Geometry}'s.
     *
     * A {@link Geometry} is one of the following:
     * <ul>
     *     <li>{@link Point}</li>
     *     <li>{@link MultiPoint}</li>
     *     <li>{@link LineString}</li>
     *     <li>{@link MultiLineString}</li>
     *     <li>{@link Polygon}</li>
     *     <li>{@link MultiPolygon}</li>
     *     <li>{@link GeometryCollection}</li>
     * </ul>
     *
     * @param geometries
     */
    public GeometryCollection(Geometry<?>... geometries) {
        addAll(Arrays.asList(geometries));
    }

    public GeometryCollection(int initialCapacity) {
        super(initialCapacity);
    }

    public GeometryCollection(Collection<Geometry<?>> c) {
        super(c);
    }

    @Override
    public GeometryType getType() {
        return GeometryType.GEOMETRYCOLLECTION;
    }

    @Override
    public boolean isValid() {
        for (Geometry geo : this) {
            if (geo == null || !geo.isValid()) {
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

        GeometryCollection other;
        try {
            other = (GeometryCollection) obj;
        } catch (ClassCastException e) {
            return false;
        }

        // gotta do contains in both directions to account for duplicates that exist only on one side.
        return geometryCollectionContainsOther(this, other) && geometryCollectionContainsOther(other, this);
    }

    static boolean geometryCollectionContainsOther(GeometryCollection gc1, GeometryCollection gc2) {
        for (Geometry<?> geo : gc1) {
            if (geo == null) {
                continue;
            }

            boolean success = false;

            for (Geometry<?> otherGeo : gc2) {
                if (geo.isEquivalentTo(otherGeo)) {
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
