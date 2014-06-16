package com.esri.terraformer;

import java.util.ArrayList;
import java.util.Collection;

public abstract class BaseGeometry<T> extends ArrayList<T> {
    protected BaseGeometry() {}

    protected BaseGeometry(int initialCapacity) {
        super(initialCapacity);
    }

    protected BaseGeometry(Collection<T> c) {
        super(c);
    }

    /**
     * Returns an enum representing one of the GeoJSON types.  See {@link GeometryType}.
     *
     * @return
     */
    public abstract GeometryType getType();

    /**
     * Let's you know whether your object is up to BaseGeometry spec.
     *
     * When inflating an object from a JSON String, you'll get an exception if the String
     * is not valid.  This method is mostly intended for checking objects you have created manually
     * or edited after inflation.
     *
     * @return
     */
    public abstract boolean isValid();

    /**
     * Warning: This may be very costly for large Geometries. **Use with discretion**
     *
     * Performs complete comparison between Geometry objects, include equivalent permutations/rotations
     * for MultiPolygons, Polygons, MultiLineStrings and MultiPoints.
     *
     * @param obj
     * @return
     */
    public abstract boolean isEquivalentTo(BaseGeometry<?> obj);

    /**
     * returns the String representation as determined by the current serializer set on the {@link Terraformer} class.
     *
     * @return
     */
    public String encode() {
        return Terraformer.serializer.encode(this);
    }

    /**
     * Package private.
     *
     * @param obj1
     * @param obj2
     * @return
     */
    static Boolean naiveEquals(BaseGeometry<?> obj1, BaseGeometry<?> obj2) {
        if (obj1 == null || obj2 == null) {
            return false;
        }

        if (obj1.getType() != obj2.getType()) {
            return false;
        }

        if (obj1.getClass() != obj2.getClass()) {
            return false;
        }

        if (obj1.size() != obj2.size()) {
            return false;
        }

        if (obj1 == obj2) {
            return true;
        }

        if (obj1.equals(obj2)) {
            return true;
        }

        return null;
    }
}
