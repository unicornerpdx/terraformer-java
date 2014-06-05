package com.esri.terraformer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MultiLineStringTest {
    static final String VALID_MULTI_LINE_STRING = "{\"type\":\"MultiLineString\",\"coordinates\":[[[100.0,0.0],[101.0,1.0]],[[103.0,5.0],[109.0,3.0]]]}";
    static final String VALID_DIFF_ORDER = "{\"type\":\"MultiLineString\",\"coordinates\":[[[103.0,5.0],[109.0,3.0]],[[100.0,0.0],[101.0,1.0]]]}";
    static final String EMPTY_COORDS = "{\"type\":\"MultiLineString\",\"coordinates\":[]}";
    static final String WRONG_TYPE = "{\"type\":\"LineString\",\"coordinates\":[[100.0,0.0],[101.0,1.0]]}";
    static final String NO_TYPE = "{\"coordinates\":[[[100.0,0.0],[101.0,1.0]],[[100.0,0.0],[101.0,1.0]]]}";
    static final String NOT_AN_OBJECT = "[\"type\",\"coordinates\"]";
    static final String INVALID_INNER_TYPE = "{\"type\":\"MultiLineString\",\"coordinates\":[[[100.0,0.0],[101.0,\"squid\"]],[[100.0,0.0],[101.0,1.0]]]}";
    static final String NO_COORDINATES = "{\"type\":\"MultiLineString\"}";
    static final String COORDS_NOT_ARRAY = "{\"type\":\"MultiLineString\",\"coordinates\":\"horse\"}";

    @Test
    public void testGetType() throws Exception {
        assertEquals(GeoJsonType.MULTILINESTRING, getMultiLineString().getType());
        assertEquals(GeoJsonType.MULTILINESTRING, new MultiLineString().getType());
    }

    @Test
    public void testIsValid() throws Exception {
        assertTrue(MultiLineString.decodeMultiLineString(VALID_MULTI_LINE_STRING).isValid());
        assertTrue(MultiLineString.decodeMultiLineString(EMPTY_COORDS).isValid());
        assertTrue(new MultiLineString(getMultiLineString()).isValid());
        // multilinestring can have empty coordinates
        assertTrue(new MultiLineString().isValid());
        // a valid polygon is a valid multilinestring, although the reverse is not necessarily true
        assertTrue(new MultiLineString(PolygonTest.getPolygon()).isValid());

        // invalid linestring
        assertFalse(new MultiLineString(new LineString(new Point(100d, 0d))).isValid());

        // null linestring
        assertFalse(new MultiLineString(new LineString(new Point(100d, 0d), new Point(100d, 0d)), null).isValid());
    }

    @Test
    public void testIsEquivalentTo() throws Exception {
        MultiLineString ls = MultiLineString.decodeMultiLineString(VALID_MULTI_LINE_STRING);
        MultiLineString otherLs = getMultiLineString();
        MultiLineString anotherLs = MultiLineString.decodeMultiLineString(VALID_DIFF_ORDER);

        assertTrue(ls.isEquivalentTo(otherLs));
        assertTrue(ls.isEquivalentTo(anotherLs));
        assertTrue(otherLs.isEquivalentTo(ls));
        assertTrue(otherLs.isEquivalentTo(anotherLs));
        assertTrue(anotherLs.isEquivalentTo(ls));
        assertTrue(anotherLs.isEquivalentTo(otherLs));
        assertFalse(ls.isEquivalentTo(new LineString(new Point(100d, 0d), new Point(101d, 1d))));
        assertFalse(ls.isEquivalentTo(new MultiLineString()));
        assertFalse(ls.isEquivalentTo(new MultiLineString(new LineString(new Point(100d, 0d), new Point(101d, 1d)))));
        assertFalse(ls.isEquivalentTo(new MultiLineString(new LineString(new Point(100d, 0d), new Point(101d, 1d)),
                new LineString(new Point(100d, 1d), new Point(101d, 1d)))));
    }

    @Test
    public void testToJson()  throws Exception {
        assertEquals(VALID_MULTI_LINE_STRING, getMultiLineString().toJson());
    }

    @Test
    public void testToJsonObject()  throws Exception {
        JsonObject obj1 = getMultiLineString().toJsonObject(null);
        JsonObject obj2 = getMultiLineString().toJsonObject(new Gson());
        assertEquals(obj1.toString(), obj2.toString());
    }

    @Test
    public void testDecodeMultiLineString() throws Exception {
        assertEquals(VALID_MULTI_LINE_STRING, MultiLineString.decodeMultiLineString(VALID_MULTI_LINE_STRING).toJson());
        assertEquals(EMPTY_COORDS, MultiLineString.decodeMultiLineString(EMPTY_COORDS).toJson());

        boolean gotException = false;

        try {
            MultiLineString.decodeMultiLineString(WRONG_TYPE);
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.NOT_OF_TYPE));
            gotException = true;
        }

        assertTrue(gotException);
        gotException = false;

        try {
            MultiLineString.decodeMultiLineString(NO_TYPE);
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.NOT_OF_TYPE));
            gotException = true;
        }

        assertTrue(gotException);
        gotException = false;

        try {
            MultiLineString.decodeMultiLineString(NOT_AN_OBJECT);
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.NOT_A_JSON_OBJECT));
            gotException = true;
        }

        assertTrue(gotException);
        gotException = false;

        try {
            MultiLineString.decodeMultiLineString(INVALID_INNER_TYPE);
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.COORDINATE_NOT_NUMERIC));
            gotException = true;
        }

        assertTrue(gotException);
        gotException = false;

        try {
            MultiLineString.decodeMultiLineString(NO_COORDINATES);
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.COORDINATES_KEY_NOT_FOUND));
            gotException = true;
        }

        assertTrue(gotException);
        gotException = false;

        try {
            MultiLineString.decodeMultiLineString(COORDS_NOT_ARRAY);
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.ELEMENT_NOT_ARRAY));
            gotException = true;
        }

        assertTrue(gotException);
    }

     static MultiLineString getMultiLineString() {
        return new MultiLineString(new LineString(new Point(100d, 0d),
                new Point(101d, 1d)), new LineString(new Point(103d, 5d), new Point(109d, 3d)));
    }
}
