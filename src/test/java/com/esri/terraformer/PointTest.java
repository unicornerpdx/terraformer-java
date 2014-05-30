package com.esri.terraformer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PointTest {
    static final String VALID_POINT = "{\"type\":\"Point\",\"coordinates\":[100.0,0.0,90.0,90.0]}";
    static final String WRONG_TYPE = "{\"type\":\"MultiPoint\",\"coordinates\":[[100.0,0.0],[101.0,1.0]]}";
    static final String NO_TYPE = "{\"coordinates\":[100.0,0.0]}";
    static final String NOT_AN_OBJECT = "[\"type\",\"coordinates\"]";
    static final String INVALID_INNER_TYPE = "{\"type\":\"Point\",\"coordinates\":[100.0,\"squid\"]}";
    static final String NO_COORDINATES = "{\"type\":\"Point\"}";
    static final String COORDS_NOT_ARRAY = "{\"type\":\"Point\",\"coordinates\":\"horse\"}";
    static final String COORDS_TOO_SHORT = "{\"type\":\"Point\",\"coordinates\":[100.0]}";

    @Test
    public void testGetType() throws Exception {
        assertEquals(GeoJsonType.POINT, Point.decodePoint(VALID_POINT).getType());
        assertEquals(GeoJsonType.POINT, new Point().getType());
    }

    @Test
    public void testIsValid() throws Exception {
        assertTrue(Point.decodePoint(VALID_POINT).isValid());
        assertTrue(new Point(new Point(100d, 0d)).isValid());
        assertFalse(new Point().isValid());
        assertFalse(new Point(100d, null).isValid());
        assertFalse(new Point(100d).isValid());
    }

    @Test
    public void testIsEquivalentTo() throws Exception {
        Point p = Point.decodePoint(VALID_POINT);
        Point otherP = new Point(100d,0d,90d,90d);
        assertTrue(p.isEquivalentTo(otherP));
        assertTrue(otherP.isEquivalentTo(p));
        assertFalse(p.isEquivalentTo(new Point()));
        assertFalse(p.isEquivalentTo(new Point(4)));
        assertFalse(p.isEquivalentTo(new Point(100d,0d)));
        assertFalse(p.isEquivalentTo(new Point(100d,0d,90d)));
        assertFalse(p.isEquivalentTo(new Point(100d,0d,90d,null)));
    }

    @Test
    public void testToJson()  throws Exception {
        assertEquals(VALID_POINT, new Point(100d, 0d, 90d, 90d).toJson());
    }

    @Test
    public void testToJsonObject()  throws Exception {
        JsonObject obj1 = new Point(100d, 0d, 90d, 90d).toJsonObject(null);
        JsonObject obj2 = new Point(100d, 0d, 90d, 90d).toJsonObject(new Gson());
        assertEquals(obj1.toString(), obj2.toString());
    }

    @Test
    public void testDecodePoint() throws Exception {
        assertEquals(VALID_POINT, Point.decodePoint(VALID_POINT).toJson());

        boolean gotException = false;

        try {
            Point.decodePoint(WRONG_TYPE);
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.NOT_OF_TYPE));
            gotException = true;
        }

        assertTrue(gotException);
        gotException = false;

        try {
            Point.decodePoint(NO_TYPE);
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.NOT_OF_TYPE));
            gotException = true;
        }

        assertTrue(gotException);
        gotException = false;

        try {
            Point.decodePoint(NOT_AN_OBJECT);
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.NOT_A_JSON_OBJECT));
            gotException = true;
        }

        assertTrue(gotException);
        gotException = false;

        try {
            Point.decodePoint(INVALID_INNER_TYPE);
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.COORDINATE_NOT_NUMERIC));
            gotException = true;
        }

        assertTrue(gotException);
        gotException = false;

        try {
            Point.decodePoint(NO_COORDINATES);
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.COORDINATES_KEY_NOT_FOUND));
            gotException = true;
        }

        assertTrue(gotException);
        gotException = false;

        try {
            Point.decodePoint(COORDS_NOT_ARRAY);
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.ELEMENT_NOT_ARRAY));
            gotException = true;
        }

        assertTrue(gotException);
        gotException = false;

        try {
            Point.decodePoint(COORDS_TOO_SHORT);
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.COORDINATE_ARRAY_TOO_SHORT));
            gotException = true;
        }

        assertTrue(gotException);
    }
}
