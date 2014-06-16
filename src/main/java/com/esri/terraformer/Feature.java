package com.esri.terraformer;

import com.google.gson.JsonObject;

import java.util.Collection;
import java.util.List;

/**
 * Feature contains a single Geometry, and this is enforced during editing.
 */
public class Feature extends BaseGeometry<Geometry<?>> {
    static final String ERROR_PREFIX = "Error while parsing Feature: ";

    private JsonObject mProperties;

    /**
     * A valid Feature contains 0 or exactly 1 {@link Geometry}. It may also optionally contain a "properties" object.
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
     */
    public Feature() {}

    /**
     *
     * A valid Feature contains 0 or exactly 1 {@link Geometry}. It may also optionally contain a "properties" object.
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
     * @param geometry
     */
    public Feature(Geometry<?> geometry) {
        add(geometry);
    }

    /**
     *
     * A valid Feature contains 0 or exactly 1 {@link Geometry}. It may also optionally contain a "properties" object.
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
     * @param geometry
     * @param properties
     */
    public Feature(Geometry<?> geometry, JsonObject properties) {
        add(geometry);
        mProperties = properties;
    }

    /**
     * returns the {@link Geometry} (if existing) associated with this Feature, or null.
     *
     * @return may be null if the Feature has no geometry
     */
    public Geometry<?> get() {
        return get(0);
    }

    /**
     * returns the {@link Geometry} (if existing) associated with this Feature, or null.
     *
     * @param i
     * @return may be null if the Feature has no geometry
     */
    @Override
    public Geometry<?> get(int i) {
        if (size() > 0) {
            return super.get(0);
        }

        return null;
    }

    /**
     * replaces the {@link Geometry} (if existing) associated with this Feature.
     *
     * @param geometry
     * @return
     */
    @Override
    public final boolean add(Geometry<?> geometry) {
        if (size() < 1) {
            super.add(geometry);
        } else {
            super.set(0, geometry);
        }

        return true;
    }

    /**
     * replaces the {@link Geometry} (if existing) associated with this Feature.
     *
     * @param geometry
     * @return may be null if the Feature had no geometry previously
     */
    public final Geometry<?> set(Geometry<?> geometry) {
        return set(0, geometry);
    }

    /**
     * replaces the {@link Geometry} (if existing) associated with this Feature.
     *
     * @param i
     * @param geometry
     * @return may be null if the Feature had no geometry previously
     */
    @Override
    public final Geometry<?> set(int i, Geometry<?> geometry) {
        if (size() < 1) {
            super.add(geometry);
            return null;
        } else {
            return super.set(0, geometry);
        }
    }

    /**
     * replaces the {@link Geometry} (if existing) associated with this Feature with the first
     * {@link Geometry} in the provided collection.
     *
     * @param geometries
     * @return
     */
    @Override
    public final boolean addAll(Collection<? extends Geometry<?>> geometries) {
        if (geometries != null) {
            for (Geometry<?> geometry : geometries) {
                add(geometry);
                break;
            }

            return true;
        }

        return false;
    }

    /**
     * replaces the {@link Geometry} (if existing) associated with this Feature with the first
     * {@link Geometry} in the provided collection.
     *
     * @param i
     * @param geometries
     * @return
     */
    @Override
    public final boolean addAll(int i, Collection<? extends Geometry<?>> geometries) {
        if (geometries != null) {
            for (Geometry<?> geometry : geometries) {
                add(geometry);
                break;
            }

            return true;
        }

        return false;
    }

    /**
     * removes the {@link Geometry} (if existing) associated with this Feature.
     *
     * @return the geometry that was removed, or null
     */
    public Geometry<?> remove() {
        return remove(0);
    }

    /**
     * removes the {@link Geometry} (if existing) associated with this Feature.
     *
     * @param i
     * @return the geometry that was removed, or null
     */
    @Override
    public Geometry<?> remove(int i) {
        if (size() > 0) {
            return super.remove(0);
        }
        return null;
    }

    /**
     * removes the {@link Geometry} (if existing) associated with this Feature.
     *
     * @param i
     * @return
     */
    @Override
    protected void removeRange(int i, int i2) {
        if (size() > 0) {
            remove(0);
        }
    }

    @Override
    public List<Geometry<?>> subList(int i, int i2) {
        return this;
    }

    @Override
    public final void ensureCapacity(int i) {}

    public JsonObject getProperties() {
        return mProperties;
    }

    /**
     * A null properties value is not included in the JSON representation of this object.
     *
     * @param properties
     */
    public void setProperties(JsonObject properties) {
        mProperties = properties;
    }

    @Override
    public GeometryType getType() {
        return GeometryType.FEATURE;
    }

    /**
     * a null geometry is represented as an empty JSON object.
     *
     * @return
     */
    @Override
    public Object encode() {
        return Terraformer.serializer.serialize(this);
    }

    @Override
    public boolean isValid() {
        if (size() > 0) {
            Geometry geo = get(0);
            if (geo == null || !geo.isValid()) {
                return false;
            }
        }

        return size() < 2;
    }

    @Override
    public boolean isEquivalentTo(BaseGeometry<?> obj) {
        Boolean equal = naiveEquals(this, obj);
        if (equal != null) {
            return equal;
        }

        Feature other;
        try {
            other = (Feature) obj;
        } catch (ClassCastException e) {
            return false;
        }

        Geometry<?> geo = get(0);
        if (geo == null) {
            return other.get(0) == null;
        }

        return geo.isEquivalentTo(other.get(0));
    }
}
