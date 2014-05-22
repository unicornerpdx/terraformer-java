package com.esri.terraformer;

// A layer of abstraction so that our Geometry types can be
// referred to collectively; primarily for supporting the GeometryCollection.
public abstract class Geometry<T> extends GeoJson<T> {}
