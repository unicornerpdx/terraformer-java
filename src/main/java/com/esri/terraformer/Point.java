package com.esri.terraformer;

import java.util.Arrays;
import java.util.Collection;

public final class Point extends Geometry<Double> {
    static final String ERROR_PREFIX = "Error while parsing Point: ";

    public Point(Double x, Double y, Double z, Double... coords) {
        add(x);
        add(y);
        add(z);
        addAll(Arrays.asList(coords));
    }

    public Point(Double x, Double y, Double z) {
        add(x);
        add(y);
        add(z);
    }

    public Point(Double x, Double y) {
        add(x);
        add(y);
    }

    public Point() {}

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

    public double getX() {
        return get(0);
    }

    public double getY() { return get(1); }

    public double getZ() { return get(2); }

    public void setX(double x) { set(0, x); }

    public void setY(double y) { set(1, y); }

    public void setZ(double z) { set(2, z); }

    public Double getLatitude() { return getY(); }

    public Double getLongitude() { return getX(); }

    public void setLatitude(double latitude) { setY(latitude); }

    public void setLongitude(double longitude) { setZ(longitude); }
}
