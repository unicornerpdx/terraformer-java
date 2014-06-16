package com.esri.terraformer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PolygonTest {
    static final String VALID_POLYGON = "{\"type\":\"Polygon\",\"coordinates\":[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]]}";
    static final String VALID_DIFF_ORDER = "{\"type\":\"Polygon\",\"coordinates\":[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]]]}";
    static final String EMPTY_COORDS = "{\"type\":\"Polygon\",\"coordinates\":[]}";
    static final String WRONG_TYPE = "{\"type\":\"MultiLineString\",\"coordinates\":[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]]}";
    static final String NO_TYPE = "{\"coordinates\":[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]]}";
    static final String NOT_AN_OBJECT = "[\"type\",\"coordinates\"]";
    // one of the linestrings is not a linear ring
    static final String INVALID_INNER_TYPE = "{\"type\":\"Polygon\",\"coordinates\":[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]]}";
    static final String NO_COORDINATES = "{\"type\":\"Polygon\"}";
    static final String COORDS_NOT_ARRAY = "{\"type\":\"Polygon\",\"coordinates\":100.0}";

    @Test
    public void testGetType() throws Exception {
        assertEquals(GeometryType.POLYGON, getPolygon().getType());
        assertEquals(GeometryType.POLYGON, new Polygon().getType());
    }

    @Test
    public void testIsValid() throws Exception {
        assertTrue(Polygon.decodePolygon(VALID_POLYGON).isValid());
        assertTrue(Polygon.decodePolygon(EMPTY_COORDS).isValid());
        assertTrue(new Polygon(getPolygon()).isValid());
        // polygon can have empty coordinates
        assertTrue(new Polygon().isValid());

        // a valid multilinestring is not necessarily a valid polygon, although the reverse is true
        assertFalse(new Polygon(MultiLineStringTest.getMultiLineString()).isValid());
        // invalid linestring
        assertFalse(new Polygon(new LineString(new Point(100d, 0d))).isValid());

        assertFalse(new Polygon(new LineString(
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
                )).isValid());

        // null linestring
        assertFalse(new Polygon(new LineString(
                        new Point(100d, 0d),
                        new Point(104d, 0d),
                        new Point(104d, 4d),
                        new Point(100d, 4d),
                        new Point(100d, 0d)),
                null,
                new LineString(
                        new Point(101d, 3d),
                        new Point(103d, 3d),
                        new Point(103d, 3.5d),
                        new Point(101d, 3.5d),
                        new Point(101d, 3d)
                )).isValid());
    }

    @Test
    public void testIsEquivalentTo() throws Exception {
        Polygon pg = Polygon.decodePolygon(VALID_POLYGON);
        Polygon otherPg = getPolygon();
        Polygon anotherPg = Polygon.decodePolygon(VALID_DIFF_ORDER);

        assertTrue(pg.isEquivalentTo(otherPg));
        assertTrue(pg.isEquivalentTo(anotherPg));
        assertTrue(otherPg.isEquivalentTo(pg));
        assertTrue(otherPg.isEquivalentTo(anotherPg));
        assertTrue(anotherPg.isEquivalentTo(pg));
        assertTrue(anotherPg.isEquivalentTo(otherPg));
        // rotated linear ring
        assertTrue(pg.isEquivalentTo(new Polygon(
                new LineString(
                        new Point(100d, 0d),
                        new Point(104d, 0d),
                        new Point(104d, 4d),
                        new Point(100d, 4d),
                        new Point(100d, 0d)),
                new LineString(
                        new Point(103d, 1d),
                        new Point(101d, 1d),
                        new Point(101d, 0.5d),
                        new Point(103d, 0.5d),
                        new Point(103d, 1d)),
                new LineString(
                        new Point(101d, 3d),
                        new Point(103d, 3d),
                        new Point(103d, 3.5d),
                        new Point(101d, 3.5d),
                        new Point(101d, 3d)
                )
        )));

        assertFalse(pg.isEquivalentTo(MultiLineStringTest.getMultiLineString()));
        assertFalse(pg.isEquivalentTo(new Polygon()));
        // missing 2nd hole
        assertFalse(pg.isEquivalentTo(new Polygon(new LineString(
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
                        new Point(101d, 0.5d)))));
        // one coordinate difference
        assertFalse(pg.isEquivalentTo(new Polygon(new LineString(
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
                        new Point(102d, 3.5d),
                        new Point(101d, 3.5d),
                        new Point(101d, 3d)
                ))));
    }

    @Test
    public void testToJson()  throws Exception {
        assertEquals(VALID_POLYGON, getPolygon().toJson());
    }

    @Test
    public void testToJsonObject()  throws Exception {
        JsonObject obj1 = getPolygon().toJsonObject(null);
        JsonObject obj2 = getPolygon().toJsonObject(new Gson());
        assertEquals(obj1.toString(), obj2.toString());
    }

    @Test
    public void testDecodePolygon() throws Exception {
        assertEquals(VALID_POLYGON, Polygon.decodePolygon(VALID_POLYGON).toJson());
        assertEquals(EMPTY_COORDS, Polygon.decodePolygon(EMPTY_COORDS).toJson());

        boolean gotException = false;

        try {
            Polygon.decodePolygon(WRONG_TYPE);
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.NOT_OF_TYPE));
            gotException = true;
        }

        assertTrue(gotException);
        gotException = false;

        try {
            Polygon.decodePolygon(NO_TYPE);
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.NOT_OF_TYPE));
            gotException = true;
        }

        assertTrue(gotException);
        gotException = false;

        try {
            Polygon.decodePolygon(NOT_AN_OBJECT);
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.NOT_A_JSON_OBJECT));
            gotException = true;
        }

        assertTrue(gotException);
        gotException = false;

        try {
            Polygon.decodePolygon(INVALID_INNER_TYPE);
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.INNER_LINESTRING_NOT_RING));
            gotException = true;
        }

        assertTrue(gotException);
        gotException = false;

        try {
            Polygon.decodePolygon(NO_COORDINATES);
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.COORDINATES_KEY_NOT_FOUND));
            gotException = true;
        }

        assertTrue(gotException);
        gotException = false;

        try {
            Polygon.decodePolygon(COORDS_NOT_ARRAY);
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.ELEMENT_NOT_ARRAY));
            gotException = true;
        }

        assertTrue(gotException);
    }

    static Polygon getPolygon() {
        return new Polygon(
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
        );
    }
}
