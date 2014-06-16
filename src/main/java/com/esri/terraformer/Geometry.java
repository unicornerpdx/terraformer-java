package com.esri.terraformer;

import java.util.Collection;

// A layer of abstraction so that our Geometry types can be
// referred to collectively; primarily for supporting the GeometryCollection.
public abstract class Geometry<T> extends BaseGeometry<T> {
    protected Geometry() {}

    protected Geometry(int initialCapacity) {
        super(initialCapacity);
    }

    protected Geometry(Collection<T> c) {
        super(c);
    }
}
