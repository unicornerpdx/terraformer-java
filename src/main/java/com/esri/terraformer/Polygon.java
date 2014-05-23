package com.esri.terraformer;

import java.util.Arrays;
import java.util.Collection;

public class Polygon extends Geometry<LineString> {
    private static final String ERROR_PREFIX = "Error while parsing Polygon: ";

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
    public GeoJsonType getType() {
        return null;
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public boolean isEquivalentTo(GeoJson<?> obj) {
        return false;
    }
}
