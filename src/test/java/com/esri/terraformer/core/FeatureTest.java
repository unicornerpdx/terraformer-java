package com.esri.terraformer.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class FeatureTest {
    @Test
    public void testAdd() throws Exception {
        Feature feat = new Feature();
        assertEquals(0, feat.size());
        Geometry mp = MultiPointTest.getMultiPoint();
        feat.add(mp);
        assertEquals(1, feat.size());
        assertEquals(mp, feat.get());
        Geometry pg = PolygonTest.getPolygon();
        feat.add(pg);
        assertEquals(1, feat.size());
        assertEquals(pg, feat.get());
    }

    @Test
    public void testAddAll() throws Exception {
        Feature feat = new Feature();
        assertEquals(0, feat.size());
        ArrayList<Geometry<?>> geos = new ArrayList<Geometry<?>>();
        Geometry pg = PolygonTest.getPolygon();
        Geometry mp = MultiPointTest.getMultiPoint();
        geos.add(mp);
        geos.add(pg);
        feat.addAll(geos);
        assertEquals(1, feat.size());
        assertEquals(mp, feat.get());
        Collections.reverse(geos);
        feat.addAll(geos);
        assertEquals(1, feat.size());
        assertEquals(pg, feat.get());

        // now with starting index
        Collections.reverse(geos);
        feat.clear();
        assertEquals(0, feat.size());
        feat.addAll(15, geos);
        assertEquals(1, feat.size());
        assertEquals(mp, feat.get(1));
        Collections.reverse(geos);
        feat.addAll(-36, geos);
        assertEquals(1, feat.size());
        assertEquals(pg, feat.get(-1));
    }

    @Test
    public void testSet() throws Exception {
        Feature feat = new Feature();
        assertEquals(0, feat.size());
        Geometry mp = MultiPointTest.getMultiPoint();
        feat.set(mp);
        assertEquals(1, feat.size());
        assertEquals(mp, feat.get());
        Geometry pg = PolygonTest.getPolygon();
        feat.set(pg);
        assertEquals(1, feat.size());
        assertEquals(pg, feat.get());

        // now with index
        feat.clear();
        assertEquals(0, feat.size());
        feat.set(9, mp);
        assertEquals(1, feat.size());
        assertEquals(mp, feat.get(100));
        feat.set(-14, pg);
        assertEquals(1, feat.size());
        assertEquals(pg, feat.get(0));
    }

    @Test
    public void testRemove() throws Exception {
        Feature feat = new Feature();
        assertNull(feat.remove());
        Geometry pg = PolygonTest.getPolygon();
        Geometry mp = MultiPointTest.getMultiPoint();
        feat.add(mp);
        assertEquals(mp, feat.remove());
        assertEquals(0, feat.size());
        feat.add(pg);
        assertEquals(pg, feat.remove());

        // now with index
        feat.clear();
        assertNull(feat.remove(0));
        feat.add(mp);
        assertEquals(mp, feat.remove(-1));
        assertEquals(0, feat.size());
        feat.add(pg);
        assertEquals(pg, feat.remove(1));
    }

    @Test
    public void testRemoveRange() throws Exception {
        Feature feat = new Feature();
        feat.removeRange(0, 123);
        Geometry mp = MultiPointTest.getMultiPoint();
        feat.add(mp);
        feat.removeRange(-99, 146);
        assertEquals(0, feat.size());
    }

    @Test
    public void testSubList() throws Exception {
        Feature feat = new Feature();
        assertEquals(feat, feat.subList(-1, 27836));
        Geometry mp = MultiPointTest.getMultiPoint();
        Geometry pg = PolygonTest.getPolygon();
        feat.add(mp);
        feat.add(pg);
        List<Geometry<?>> geos = feat.subList(0, 2);
        assertEquals(1, feat.size());
        assertEquals(1, geos.size());
        assertEquals(pg, geos.get(0));
    }

    @Test
    public void testGetSetProperties() throws Exception {
        Feature feat = new Feature();
        JsonObject props = getProperties();
        feat.setProperties(props);
        assertEquals(props, feat.getProperties());
        assertTrue(feat.getProperties().equals(getProperties()));
    }

    @Test
    public void testGetType() throws Exception {
        assertEquals(GeometryType.FEATURE, getMultiLineStringFeature().getType());
        assertEquals(GeometryType.FEATURE, getPolygonFeature().getType());
        assertEquals(GeometryType.FEATURE, getMultiPolygonFeature().getType());
        assertEquals(GeometryType.FEATURE, getGeometryCollectionFeature().getType());
        assertEquals(GeometryType.FEATURE, new Feature().getType());
    }

    @Test
    public void testIsValid() throws Exception {
        assertTrue(getMultiLineStringFeature().isValid());
        assertTrue(getPolygonFeature().isValid());
        assertTrue(getMultiPolygonFeature().isValid());
        assertTrue(getGeometryCollectionFeature().isValid());
        // creating a feature from some element of a GeometryCollection
        assertTrue(new Feature(GeometryCollectionTest.getGeometryCollection().get(3), getProperties()).isValid());
        // geometry collection can have empty geometries
        assertTrue(new Feature().isValid());

        // invalid multipolygon
        assertFalse(new Feature(new MultiPolygon(new Polygon(new LineString(
                        new Point(100d, 0d),
                        new Point(104d, 0d),
                        new Point(104d, 4d),
                        new Point(100d, 4d)), // not a linear ring
                new LineString(
                        new Point(101d, 0.5d),
                        new Point(103d, 0.5d),
                        new Point(103d, 1d),
                        new Point(101d, 1d),
                        new Point(101d, 0.5d)),
                new LineString(
                        new Point(101d, 3d),
                        new Point(103d, 3d),
                        new Point(103d, 3.5d),
                        new Point(101d, 3.5d),
                        new Point(101d, 3d)
        )))).isValid());
        // null geometry
        assertFalse(new Feature(null).isValid());
    }

    @Test
    public void testIsEquivalentTo() throws Exception {
        Feature feat = getGeometryCollectionFeature();
        Feature otherFeat = new Feature(GeometryCollectionTest.getGeomCollDiffOrder(), getProperties());

        assertTrue(feat.isEquivalentTo(feat));
        assertTrue(feat.isEquivalentTo(otherFeat));
        assertTrue(otherFeat.isEquivalentTo(feat));
        // rotated linear ring, also no properties
        assertTrue(feat.isEquivalentTo(new Feature(new GeometryCollection(
                PointTest.getPoint(),
                MultiPointTest.getMultiPoint(),
                LineStringTest.getLineString(),
                MultiLineStringTest.getMultiLineString(),
                PolygonTest.getPolygon(),
                new MultiPolygon(new Polygon(
                        new LineString(
                                new Point(100d, 0d),
                                new Point(104d, 0d),
                                new Point(104d, 4d),
                                new Point(100d, 4d),
                                new Point(100d, 0d)),
                        new LineString(
                                new Point(101d, 0.5d),
                                new Point(103d, 0.5d),
                                new Point(103d, 1d),
                                new Point(101d, 1d),
                                new Point(101d, 0.5d)),
                        new LineString(
                                new Point(101d, 3d),
                                new Point(103d, 3d),
                                new Point(103d, 3.5d),
                                new Point(101d, 3.5d),
                                new Point(101d, 3d)
                        )
                ), new Polygon(
                        new LineString(
                                new Point(95d, 0d),
                                new Point(99d, 0d),
                                new Point(99d, 4d),
                                new Point(95d, 4d),
                                new Point(95d, 0d)),
                        new LineString(
                                new Point(96d, 1d),
                                new Point(96d, 0.5d),
                                new Point(98d, 0.5d),
                                new Point(98d, 1d),
                                new Point(96d, 1d)),
                        new LineString(
                                new Point(96d, 3d),
                                new Point(98d, 3d),
                                new Point(98d, 3.5d),
                                new Point(96d, 3.5d),
                                new Point(96d, 3d)
                        )
                )),
                GeometryCollectionTest.geomCollWithoutGeomColl()
        ))));

        assertFalse(feat.isEquivalentTo(GeometryCollectionTest.getGeometryCollection()));
        assertFalse(feat.isEquivalentTo(new Feature()));
        // missing the linestring
        assertFalse(feat.isEquivalentTo(new GeometryCollection(
                PointTest.getPoint(),
                MultiPointTest.getMultiPoint(),
                MultiLineStringTest.getMultiLineString(),
                PolygonTest.getPolygon(),
                MultiPolygonTest.getMultiPolygon(),
                GeometryCollectionTest.geomCollWithoutGeomColl()
        )));
    }

    public static Feature getMultiLineStringFeature() {
        return new Feature(MultiLineStringTest.getMultiLineString(), getProperties());
    }

    public static Feature getPolygonFeature() {
        return new Feature(PolygonTest.getPolygon(), getProperties());
    }

    public static Feature getMultiPolygonFeature() {
        return new Feature(MultiPolygonTest.getMultiPolygon(), getProperties());
    }

    public static Feature getGeometryCollectionFeature() {
        return new Feature(GeometryCollectionTest.getGeometryCollection(), getProperties());
    }

    public static JsonObject getProperties() {
        JsonObject props = new JsonObject();
        JsonObject innerObj = new JsonObject();
        JsonArray innerArray = new JsonArray();

        innerObj.add("herp", new JsonPrimitive("derp"));
        innerObj.add("nurp", new JsonPrimitive(100.5));
        props.add("innerObject", innerObj);

        innerArray.add(new JsonPrimitive(1));
        innerArray.add(new JsonPrimitive(2));
        innerArray.add(new JsonPrimitive(3));
        innerArray.add(new JsonPrimitive(4));
        props.add("innerArray", innerArray);

        props.add("horse", new JsonPrimitive("hands"));
        return props;
    }
}
