package com.esri.terraformer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GeometryCollectionTest {
    static final String VALID_GEOMETRY_COLLECTION = "{\"type\":\"GeometryCollection\",\"geometries\":[{\"type\":\"Point\",\"coordinates\":[100.0,0.0,90.0,90.0]},{\"type\":\"MultiPoint\",\"coordinates\":[[100.0,0.0],[101.0,1.0]]},{\"type\":\"LineString\",\"coordinates\":[[100.0,0.0],[101.0,1.0]]},{\"type\":\"MultiLineString\",\"coordinates\":[[[100.0,0.0],[101.0,1.0]],[[103.0,5.0],[109.0,3.0]]]},{\"type\":\"Polygon\",\"coordinates\":[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]]},{\"type\":\"MultiPolygon\",\"coordinates\":[[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]],[[[95.0,0.0],[99.0,0.0],[99.0,4.0],[95.0,4.0],[95.0,0.0]],[[96.0,0.5],[98.0,0.5],[98.0,1.0],[96.0,1.0],[96.0,0.5]],[[96.0,3.0],[98.0,3.0],[98.0,3.5],[96.0,3.5],[96.0,3.0]]]]},{\"type\":\"GeometryCollection\",\"geometries\":[{\"type\":\"Point\",\"coordinates\":[100.0,0.0,90.0,90.0]},{\"type\":\"MultiPoint\",\"coordinates\":[[100.0,0.0],[101.0,1.0]]},{\"type\":\"LineString\",\"coordinates\":[[100.0,0.0],[101.0,1.0]]},{\"type\":\"MultiLineString\",\"coordinates\":[[[100.0,0.0],[101.0,1.0]],[[103.0,5.0],[109.0,3.0]]]},{\"type\":\"Polygon\",\"coordinates\":[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]]},{\"type\":\"MultiPolygon\",\"coordinates\":[[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]],[[[95.0,0.0],[99.0,0.0],[99.0,4.0],[95.0,4.0],[95.0,0.0]],[[96.0,0.5],[98.0,0.5],[98.0,1.0],[96.0,1.0],[96.0,0.5]],[[96.0,3.0],[98.0,3.0],[98.0,3.5],[96.0,3.5],[96.0,3.0]]]]}]}]}";
    static final String VALID_DIFF_ORDER = "{\"type\":\"GeometryCollection\",\"geometries\":[{\"type\":\"MultiPolygon\",\"coordinates\":[[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]],[[[95.0,0.0],[99.0,0.0],[99.0,4.0],[95.0,4.0],[95.0,0.0]],[[96.0,0.5],[98.0,0.5],[98.0,1.0],[96.0,1.0],[96.0,0.5]],[[96.0,3.0],[98.0,3.0],[98.0,3.5],[96.0,3.5],[96.0,3.0]]]]},{\"type\":\"Point\",\"coordinates\":[100.0,0.0,90.0,90.0]},{\"type\":\"MultiPoint\",\"coordinates\":[[100.0,0.0],[101.0,1.0]]},{\"type\":\"MultiLineString\",\"coordinates\":[[[100.0,0.0],[101.0,1.0]],[[103.0,5.0],[109.0,3.0]]]},{\"type\":\"GeometryCollection\",\"geometries\":[{\"type\":\"Point\",\"coordinates\":[100.0,0.0,90.0,90.0]},{\"type\":\"MultiPoint\",\"coordinates\":[[100.0,0.0],[101.0,1.0]]},{\"type\":\"LineString\",\"coordinates\":[[100.0,0.0],[101.0,1.0]]},{\"type\":\"MultiLineString\",\"coordinates\":[[[100.0,0.0],[101.0,1.0]],[[103.0,5.0],[109.0,3.0]]]},{\"type\":\"Polygon\",\"coordinates\":[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]]},{\"type\":\"MultiPolygon\",\"coordinates\":[[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]],[[[95.0,0.0],[99.0,0.0],[99.0,4.0],[95.0,4.0],[95.0,0.0]],[[96.0,0.5],[98.0,0.5],[98.0,1.0],[96.0,1.0],[96.0,0.5]],[[96.0,3.0],[98.0,3.0],[98.0,3.5],[96.0,3.5],[96.0,3.0]]]]}]},{\"type\":\"Polygon\",\"coordinates\":[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]]},{\"type\":\"LineString\",\"coordinates\":[[100.0,0.0],[101.0,1.0]]}]}";
    static final String EMPTY_GEOMETRIES = "{\"type\":\"GeometryCollection\",\"geometries\":[]}";
    static final String WRONG_TYPE = "{\"type\":\"MultiPolygon\",\"geometries\":[{\"type\":\"Point\",\"coordinates\":[100.0,0.0,90.0,90.0]},{\"type\":\"MultiPoint\",\"coordinates\":[[100.0,0.0],[101.0,1.0]]},{\"type\":\"LineString\",\"coordinates\":[[100.0,0.0],[101.0,1.0]]},{\"type\":\"GeometryCollection\",\"geometries\":[{\"type\":\"Point\",\"coordinates\":[100.0,0.0,90.0,90.0]},{\"type\":\"MultiPoint\",\"coordinates\":[[100.0,0.0],[101.0,1.0]]},{\"type\":\"LineString\",\"coordinates\":[[100.0,0.0],[101.0,1.0]]},{\"type\":\"MultiLineString\",\"coordinates\":[[[100.0,0.0],[101.0,1.0]],[[103.0,5.0],[109.0,3.0]]]},{\"type\":\"Polygon\",\"coordinates\":[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]]},{\"type\":\"MultiPolygon\",\"coordinates\":[[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]],[[[95.0,0.0],[99.0,0.0],[99.0,4.0],[95.0,4.0],[95.0,0.0]],[[96.0,0.5],[98.0,0.5],[98.0,1.0],[96.0,1.0],[96.0,0.5]],[[96.0,3.0],[98.0,3.0],[98.0,3.5],[96.0,3.5],[96.0,3.0]]]]}]},{\"type\":\"MultiLineString\",\"coordinates\":[[[100.0,0.0],[101.0,1.0]],[[103.0,5.0],[109.0,3.0]]]},{\"type\":\"Polygon\",\"coordinates\":[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]]},{\"type\":\"MultiPolygon\",\"coordinates\":[[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]],[[[95.0,0.0],[99.0,0.0],[99.0,4.0],[95.0,4.0],[95.0,0.0]],[[96.0,0.5],[98.0,0.5],[98.0,1.0],[96.0,1.0],[96.0,0.5]],[[96.0,3.0],[98.0,3.0],[98.0,3.5],[96.0,3.5],[96.0,3.0]]]]}]}";
    static final String NO_TYPE = "{\"geometries\":[{\"type\":\"Point\",\"coordinates\":[100.0,0.0,90.0,90.0]},{\"type\":\"MultiPoint\",\"coordinates\":[[100.0,0.0],[101.0,1.0]]},{\"type\":\"LineString\",\"coordinates\":[[100.0,0.0],[101.0,1.0]]},{\"type\":\"MultiLineString\",\"coordinates\":[[[100.0,0.0],[101.0,1.0]],[[103.0,5.0],[109.0,3.0]]]},{\"type\":\"Polygon\",\"coordinates\":[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]]},{\"type\":\"MultiPolygon\",\"coordinates\":[[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]],[[[95.0,0.0],[99.0,0.0],[99.0,4.0],[95.0,4.0],[95.0,0.0]],[[96.0,0.5],[98.0,0.5],[98.0,1.0],[96.0,1.0],[96.0,0.5]],[[96.0,3.0],[98.0,3.0],[98.0,3.5],[96.0,3.5],[96.0,3.0]]]]}]}";
    static final String NOT_AN_OBJECT = "[\"type\",\"geometries\"]";
    // one of the linestrings inside the polygon is not a linear ring
    static final String INVALID_INNER_TYPE = "{\"type\":\"GeometryCollection\",\"geometries\":[{\"type\":\"Point\",\"coordinates\":[100.0,0.0,90.0,90.0]},{\"type\":\"MultiPoint\",\"coordinates\":[[100.0,0.0],[101.0,1.0]]},{\"type\":\"LineString\",\"coordinates\":[[100.0,0.0],[101.0,1.0]]},{\"type\":\"MultiLineString\",\"coordinates\":[[[100.0,0.0],[101.0,1.0]],[[103.0,5.0],[109.0,3.0]]]},{\"type\":\"Polygon\",\"coordinates\":[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]]},{\"type\":\"MultiPolygon\",\"coordinates\":[[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]],[[[95.0,0.0],[99.0,0.0],[99.0,4.0],[95.0,4.0],[95.0,0.0]],[[96.0,0.5],[98.0,0.5],[98.0,1.0],[96.0,1.0],[96.0,0.5]],[[96.0,3.0],[98.0,3.0],[98.0,3.5],[96.0,3.5],[96.0,3.0]]]]}]}";
    static final String NO_GEOMETRIES = "{\"type\":\"GeometryCollection\"}";
    static final String GEOMS_NOT_ARRAY = "{\"type\":\"GeometryCollection\",\"geometries\":100.0}";

    @Test
    public void testGetType() throws Exception {
        assertEquals(GeometryType.GEOMETRYCOLLECTION, getGeometryCollection().getType());
        assertEquals(GeometryType.GEOMETRYCOLLECTION, new GeometryCollection().getType());
    }

    @Test
    public void testIsValid() throws Exception {
        assertTrue(GeometryCollection.decodeGeometryCollection(VALID_GEOMETRY_COLLECTION).isValid());
        assertTrue(GeometryCollection.decodeGeometryCollection(EMPTY_GEOMETRIES).isValid());
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
        GeometryCollection gc = GeometryCollection.decodeGeometryCollection(VALID_GEOMETRY_COLLECTION);
        GeometryCollection otherGc = getGeometryCollection();
        GeometryCollection anotherGc = GeometryCollection.decodeGeometryCollection(VALID_DIFF_ORDER);

        assertTrue(gc.isEquivalentTo(otherGc));
        assertTrue(gc.isEquivalentTo(anotherGc));
        assertTrue(otherGc.isEquivalentTo(gc));
        assertTrue(otherGc.isEquivalentTo(anotherGc));
        assertTrue(anotherGc.isEquivalentTo(gc));
        assertTrue(anotherGc.isEquivalentTo(otherGc));
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

    @Test
    public void testToJson() throws Exception {
        assertEquals(VALID_GEOMETRY_COLLECTION, getGeometryCollection().toJson());
    }

    @Test
    public void testToJsonObject() throws Exception {
        JsonObject obj1 = getGeometryCollection().toJsonObject(null);
        JsonObject obj2 = getGeometryCollection().toJsonObject(new Gson());
        assertEquals(obj1.toString(), obj2.toString());
    }

    @Test
    public void testDecodeGeometryCollection() throws Exception {
        assertEquals(VALID_GEOMETRY_COLLECTION,
                GeometryCollection.decodeGeometryCollection(VALID_GEOMETRY_COLLECTION).toJson());
        assertEquals(EMPTY_GEOMETRIES,
                GeometryCollection.decodeGeometryCollection(EMPTY_GEOMETRIES).toJson());

        boolean gotException = false;

        try {
            GeometryCollection.decodeGeometryCollection(WRONG_TYPE);
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.NOT_OF_TYPE));
            gotException = true;
        }

        assertTrue(gotException);
        gotException = false;

        try {
            GeometryCollection.decodeGeometryCollection(NO_TYPE);
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.NOT_OF_TYPE));
            gotException = true;
        }

        assertTrue(gotException);
        gotException = false;

        try {
            GeometryCollection.decodeGeometryCollection(NOT_AN_OBJECT);
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.NOT_A_JSON_OBJECT));
            gotException = true;
        }

        assertTrue(gotException);
        gotException = false;

        try {
            GeometryCollection.decodeGeometryCollection(INVALID_INNER_TYPE);
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.INNER_LINESTRING_NOT_RING));
            gotException = true;
        }

        assertTrue(gotException);
        gotException = false;

        try {
            GeometryCollection.decodeGeometryCollection(NO_GEOMETRIES);
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.GEOMETRIES_KEY_NOT_FOUND));
            gotException = true;
        }

        assertTrue(gotException);
        gotException = false;

        try {
            GeometryCollection.decodeGeometryCollection(GEOMS_NOT_ARRAY);
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.ELEMENT_NOT_ARRAY));
            gotException = true;
        }

        assertTrue(gotException);
    }

    static GeometryCollection getGeometryCollection() {
        GeometryCollection geom = geomCollWithoutGeomColl();
        geom.add(geomCollWithoutGeomColl());
        return geom;
    }

    static GeometryCollection geomCollWithoutGeomColl() {
        return new GeometryCollection(
                PointTest.getPoint(),
                MultiPointTest.getMultiPoint(),
                LineStringTest.getLineString(),
                MultiLineStringTest.getMultiLineString(),
                PolygonTest.getPolygon(),
                MultiPolygonTest.getMultiPolygon()
        );
    }
}
