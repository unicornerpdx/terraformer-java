package com.esri.terraformer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MultiPolygonTest {
    static final String VALID_MULTI_POLYGON = "{\"type\":\"MultiPolygon\",\"coordinates\":[[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]],[[[95.0,0.0],[99.0,0.0],[99.0,4.0],[95.0,4.0],[95.0,0.0]],[[96.0,0.5],[98.0,0.5],[98.0,1.0],[96.0,1.0],[96.0,0.5]],[[96.0,3.0],[98.0,3.0],[98.0,3.5],[96.0,3.5],[96.0,3.0]]]]}";
    static final String VALID_DIFF_ORDER = "{\"type\":\"MultiPolygon\",\"coordinates\":[[[[95.0,0.0],[99.0,0.0],[99.0,4.0],[95.0,4.0],[95.0,0.0]],[[96.0,0.5],[98.0,0.5],[98.0,1.0],[96.0,1.0],[96.0,0.5]],[[96.0,3.0],[98.0,3.0],[98.0,3.5],[96.0,3.5],[96.0,3.0]]],[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]]]}";
    static final String EMPTY_COORDS = "{\"type\":\"MultiPolygon\",\"coordinates\":[]}";
    static final String WRONG_TYPE = "{\"type\":\"Polygon\",\"coordinates\":[[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]],[[[95.0,0.0],[99.0,0.0],[99.0,4.0],[95.0,4.0],[95.0,0.0]],[[96.0,0.5],[98.0,0.5],[98.0,1.0],[96.0,1.0],[96.0,0.5]],[[96.0,3.0],[98.0,3.0],[98.0,3.5],[96.0,3.5],[96.0,3.0]]]]}";
    static final String NO_TYPE = "{\"coordinates\":[[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]],[[[95.0,0.0],[99.0,0.0],[99.0,4.0],[95.0,4.0],[95.0,0.0]],[[96.0,0.5],[98.0,0.5],[98.0,1.0],[96.0,1.0],[96.0,0.5]],[[96.0,3.0],[98.0,3.0],[98.0,3.5],[96.0,3.5],[96.0,3.0]]]]}";
    static final String NOT_AN_OBJECT = "[\"type\",\"coordinates\"]";
    // one of the linestrings is not a linear ring
    static final String INVALID_INNER_TYPE = "{\"type\":\"MultiPolygon\",\"coordinates\":[[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]],[[[95.0,0.0],[99.0,0.0],[99.0,4.0],[95.0,4.0],[95.0,0.0]],[[96.0,0.5],[98.0,0.5],[98.0,1.0],[96.0,1.0],[96.0,0.5]],[[96.0,3.0],[98.0,3.0],[98.0,3.5],[96.0,3.5],[96.0,3.1]]]]}";
    static final String NO_COORDINATES = "{\"type\":\"MultiPolygon\"}";
    static final String COORDS_NOT_ARRAY = "{\"type\":\"MultiPolygon\",\"coordinates\":100.0}";

    @Test
    public void testGetType() throws Exception {
        assertEquals(GeometryType.MULTIPOLYGON, getMultiPolygon().getType());
        assertEquals(GeometryType.MULTIPOLYGON, new MultiPolygon().getType());
    }

    @Test
    public void testIsValid() throws Exception {
        assertTrue(MultiPolygon.decodeMultiPolygon(VALID_MULTI_POLYGON).isValid());
        assertTrue(MultiPolygon.decodeMultiPolygon(EMPTY_COORDS).isValid());
        assertTrue(new MultiPolygon(getMultiPolygon()).isValid());
        // multipolygon can have empty coordinates
        assertTrue(new MultiPolygon().isValid());
        assertTrue(new MultiPolygon(PolygonTest.getPolygon()).isValid());

        // invalid polygon
        assertFalse(new MultiPolygon(new Polygon(new LineString(
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
                ))).isValid());

        // null polygon
        assertFalse(new MultiPolygon(new Polygon(
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
                ),
                null,
                new Polygon(
                        new LineString(
                                new Point(95d, 0d),
                                new Point(99d, 0d),
                                new Point(99d, 4d),
                                new Point(95d, 4d),
                                new Point(95d, 0d)),
                        new LineString(
                                new Point(96d, 0.5d),
                                new Point(98d, 0.5d),
                                new Point(98d, 1d),
                                new Point(96d, 1d),
                                new Point(96d, 0.5d)),
                        new LineString(
                                new Point(96d, 3d),
                                new Point(98d, 3d),
                                new Point(98d, 3.5d),
                                new Point(96d, 3.5d),
                                new Point(96d, 3d)
                )
        )).isValid());
    }

    @Test
    public void testIsEquivalentTo() throws Exception {
        MultiPolygon mpg = MultiPolygon.decodeMultiPolygon(VALID_MULTI_POLYGON);
        MultiPolygon otherMpg = getMultiPolygon();
        MultiPolygon anotherMpg = MultiPolygon.decodeMultiPolygon(VALID_DIFF_ORDER);

        assertTrue(mpg.isEquivalentTo(otherMpg));
        assertTrue(mpg.isEquivalentTo(anotherMpg));
        assertTrue(otherMpg.isEquivalentTo(mpg));
        assertTrue(otherMpg.isEquivalentTo(anotherMpg));
        assertTrue(anotherMpg.isEquivalentTo(mpg));
        assertTrue(anotherMpg.isEquivalentTo(otherMpg));
        // rotated linear ring
        assertTrue(mpg.isEquivalentTo(new MultiPolygon(new Polygon(
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
        ))));

        assertFalse(mpg.isEquivalentTo(PolygonTest.getPolygon()));
        assertFalse(mpg.isEquivalentTo(new MultiPolygon()));
        // missing 2nd polygon
        assertFalse(mpg.isEquivalentTo(new MultiPolygon(new Polygon(
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
        ))));
        // one coordinate difference
        assertFalse(mpg.isEquivalentTo(new MultiPolygon(new Polygon(
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
                        new Point(95.4d, 0d)),
                new LineString(
                        new Point(96d, 0.5d),
                        new Point(98d, 0.5d),
                        new Point(98d, 1d),
                        new Point(96d, 1d),
                        new Point(96d, 0.5d)),
                new LineString(
                        new Point(96d, 3d),
                        new Point(98d, 3d),
                        new Point(98d, 3.5d),
                        new Point(96d, 3.5d),
                        new Point(96d, 3d)
                )
        ))));
    }

    @Test
    public void testToJson()  throws Exception {
        assertEquals(VALID_MULTI_POLYGON, getMultiPolygon().toJson());
    }

    @Test
    public void testToJsonObject()  throws Exception {
        JsonObject obj1 = getMultiPolygon().toJsonObject(null);
        JsonObject obj2 = getMultiPolygon().toJsonObject(new Gson());
        assertEquals(obj1.toString(), obj2.toString());
    }

    @Test
    public void testDecodeMultiPolygon() throws Exception {
        assertEquals(VALID_MULTI_POLYGON, MultiPolygon.decodeMultiPolygon(VALID_MULTI_POLYGON).toJson());
        assertEquals(EMPTY_COORDS, MultiPolygon.decodeMultiPolygon(EMPTY_COORDS).toJson());

        boolean gotException = false;

        try {
            MultiPolygon.decodeMultiPolygon(WRONG_TYPE);
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.NOT_OF_TYPE));
            gotException = true;
        }

        assertTrue(gotException);
        gotException = false;

        try {
            MultiPolygon.decodeMultiPolygon(NO_TYPE);
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.NOT_OF_TYPE));
            gotException = true;
        }

        assertTrue(gotException);
        gotException = false;

        try {
            MultiPolygon.decodeMultiPolygon(NOT_AN_OBJECT);
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.NOT_A_JSON_OBJECT));
            gotException = true;
        }

        assertTrue(gotException);
        gotException = false;

        try {
            MultiPolygon.decodeMultiPolygon(INVALID_INNER_TYPE);
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.INNER_LINESTRING_NOT_RING));
            gotException = true;
        }

        assertTrue(gotException);
        gotException = false;

        try {
            MultiPolygon.decodeMultiPolygon(NO_COORDINATES);
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.COORDINATES_KEY_NOT_FOUND));
            gotException = true;
        }

        assertTrue(gotException);
        gotException = false;

        try {
            MultiPolygon.decodeMultiPolygon(COORDS_NOT_ARRAY);
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.ELEMENT_NOT_ARRAY));
            gotException = true;
        }

        assertTrue(gotException);
    }

    static MultiPolygon getMultiPolygon() {
        return new MultiPolygon(new Polygon(
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
                        new Point(96d, 0.5d),
                        new Point(98d, 0.5d),
                        new Point(98d, 1d),
                        new Point(96d, 1d),
                        new Point(96d, 0.5d)),
                new LineString(
                        new Point(96d, 3d),
                        new Point(98d, 3d),
                        new Point(98d, 3.5d),
                        new Point(96d, 3.5d),
                        new Point(96d, 3d)
                )
        ));
    }
}
