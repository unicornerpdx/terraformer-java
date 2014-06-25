package com.esri.terraformer.core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GeometryCollectionTest {
    @Test
    public void testGetType() throws Exception {
        assertEquals(GeometryType.GEOMETRYCOLLECTION, getGeometryCollection().getType());
        assertEquals(GeometryType.GEOMETRYCOLLECTION, new GeometryCollection().getType());
    }

    @Test
    public void testIsValid() throws Exception {
        assertTrue(getGeometryCollection().isValid());
        assertTrue(new GeometryCollection(getGeometryCollection()).isValid());
        // geometry collection can have empty geometries
        assertTrue(new GeometryCollection().isValid());

        // invalid multipolygon
        GeometryCollection geom = getGeometryCollection();
        geom.add(new MultiPolygon(new Polygon(new LineString(
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
                ))));
        assertFalse(geom.isValid());

        // null polygon
        geom.remove(geom.size() - 1);
        geom.add(null);
        geom.add(PolygonTest.getPolygon());
        assertFalse(geom.isValid());
    }

    @Test
    public void testIsEquivalentTo() throws Exception {
        GeometryCollection gc = getGeometryCollection();
        GeometryCollection otherGc = getGeomCollDiffOrder();

        assertTrue(gc.isEquivalentTo(gc));
        assertTrue(gc.isEquivalentTo(otherGc));
        assertTrue(otherGc.isEquivalentTo(gc));
        // rotated linear ring
        assertTrue(gc.isEquivalentTo(new GeometryCollection(
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
                geomCollWithoutGeomColl()
        )));

        assertFalse(gc.isEquivalentTo(MultiPolygonTest.getMultiPolygon()));
        assertFalse(gc.isEquivalentTo(new GeometryCollection()));
        // missing the linestring
        assertFalse(gc.isEquivalentTo(new GeometryCollection(
                PointTest.getPoint(),
                MultiPointTest.getMultiPoint(),
                MultiLineStringTest.getMultiLineString(),
                PolygonTest.getPolygon(),
                MultiPolygonTest.getMultiPolygon(),
                geomCollWithoutGeomColl()
        )));
        // one coordinate difference
        assertFalse(gc.isEquivalentTo(new GeometryCollection(
                PointTest.getPoint(),
                MultiPointTest.getMultiPoint(),
                MultiLineStringTest.getMultiLineString(),
                new Polygon(
                        new LineString(
                                new Point(100d, 0d),
                                new Point(104d, 0d),
                                new Point(104d, 4d),
                                new Point(100d, 4d),
                                new Point(100d, 0d)),
                        new LineString(
                                new Point(101d, 0.5d),
                                new Point(103d, 0.6d),
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
                ),
                MultiPolygonTest.getMultiPolygon(),
                geomCollWithoutGeomColl()
        )));
    }

    public static GeometryCollection getGeometryCollection() {
        GeometryCollection geom = geomCollWithoutGeomColl();
        geom.add(geomCollWithoutGeomColl());
        return geom;
    }

    public static GeometryCollection getGeomCollDiffOrder() {
        GeometryCollection geom = geomCollWithoutGeomCollDiffOrder();
        geom.add(geomCollWithoutGeomCollDiffOrder());
        return geom;
    }

    public static GeometryCollection geomCollWithoutGeomColl() {
        return new GeometryCollection(
                PointTest.getPoint(),
                MultiPointTest.getMultiPoint(),
                LineStringTest.getLineString(),
                MultiLineStringTest.getMultiLineString(),
                PolygonTest.getPolygon(),
                MultiPolygonTest.getMultiPolygon()
        );
    }

    public static GeometryCollection geomCollWithoutGeomCollDiffOrder() {
        return new GeometryCollection(
                MultiLineStringTest.getMultiLineString(),
                PointTest.getPoint(),
                PolygonTest.getPolygon(),
                MultiPointTest.getMultiPoint(),
                MultiPolygonTest.getMultiPolygon(),
                LineStringTest.getLineString()
        );
    }
}
