package com.esri.terraformer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MultiPointTest {
    static final String VALID_MULTIPOINT = "{\"type\":\"MultiPoint\",\"coordinates\":[[100.0,0.0],[101.0,1.0]]}";
    static final String VALID_DIFF_ORDER = "{\"type\":\"MultiPoint\",\"coordinates\":[[101.0,1.0],[100.0,0.0]]}";
    static final String WRONG_TYPE = "{\"type\":\"Point\",\"coordinates\":[100.0,0.0]}";
    static final String NO_TYPE = "{\"coordinates\":[[100.0,0.0],[101.0,1.0]]}";
    static final String NOT_AN_OBJECT = "[\"type\",\"coordinates\"]";
    static final String INVALID_INNER_TYPE = "{\"type\":\"MultiPoint\",\"coordinates\":[[100.0,3.0],[100.0,\"squid\"],[101.0,1.0]]}";
    static final String NO_COORDINATES = "{\"type\":\"MultiPoint\"}";
    static final String COORDS_NOT_ARRAY = "{\"type\":\"MultiPoint\",\"coordinates\":\"horse\"}";
    static final String COORDS_TOO_SHORT = "{\"type\":\"MultiPoint\",\"coordinates\":[[101.0,1.0]]}";

    @Test
    public void testGetType() throws Exception {
        assertEquals(GeoJsonType.MULTIPOINT, MultiPoint.decodeMultiPoint(VALID_MULTIPOINT).getType());
        assertEquals(GeoJsonType.MULTIPOINT, new MultiPoint().getType());
    }

    @Test
    public void testIsValid() throws Exception {
        assertTrue(MultiPoint.decodeMultiPoint(VALID_MULTIPOINT).isValid());
        assertTrue(new MultiPoint(new MultiPoint(new Point(100d, 0d), new Point(101d, 1d))).isValid());
        assertFalse(new MultiPoint().isValid());
        assertFalse(new MultiPoint(new Point(100d)).isValid());
        assertFalse(new MultiPoint(new Point(100d, null)).isValid());
    }

    @Test
    public void testIsEquivalentTo() throws Exception {
        MultiPoint mp = MultiPoint.decodeMultiPoint(VALID_MULTIPOINT);
        MultiPoint otherMp = new MultiPoint(new Point(100d, 0d), new Point(101d, 1d));
        MultiPoint anotherMp = MultiPoint.decodeMultiPoint(VALID_DIFF_ORDER);

        assertTrue(mp.isEquivalentTo(otherMp));
        assertTrue(mp.isEquivalentTo(anotherMp));
        assertTrue(otherMp.isEquivalentTo(mp));
        assertTrue(otherMp.isEquivalentTo(anotherMp));
        assertTrue(anotherMp.isEquivalentTo(mp));
        assertTrue(anotherMp.isEquivalentTo(otherMp));
        assertFalse(mp.isEquivalentTo(new Point(100d, 0d)));
        assertFalse(mp.isEquivalentTo(new MultiPoint()));
        assertFalse(mp.isEquivalentTo(new MultiPoint(new Point(100d, 0d))));
        assertFalse(mp.isEquivalentTo(new MultiPoint(new Point(100d, 0d), new Point(100d, 1d))));
    }

    @Test
    public void testToJson()  throws Exception {
        assertEquals(VALID_MULTIPOINT, new MultiPoint(new Point(100d, 0d), new Point(101d, 1d)).toJson());
    }

    @Test
    public void testToJsonObject()  throws Exception {
        JsonObject obj1 = new MultiPoint(new Point(100d, 0d), new Point(101d, 1d)).toJsonObject(null);
        JsonObject obj2 = new MultiPoint(new Point(100d, 0d), new Point(101d, 1d)).toJsonObject(new Gson());
        assertEquals(obj1.toString(), obj2.toString());
    }

    @Test
    public void testDecodeMultiPoint() throws Exception {
        assertEquals(VALID_MULTIPOINT, MultiPoint.decodeMultiPoint(VALID_MULTIPOINT).toJson());

        boolean gotException = false;

        try {
            MultiPoint.decodeMultiPoint(WRONG_TYPE);
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.NOT_OF_TYPE));
            gotException = true;
        }

        assertTrue(gotException);
        gotException = false;

        try {
            MultiPoint.decodeMultiPoint(NO_TYPE);
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.NOT_OF_TYPE));
            gotException = true;
        }

        assertTrue(gotException);
        gotException = false;

        try {
            MultiPoint.decodeMultiPoint(NOT_AN_OBJECT);
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.NOT_A_JSON_OBJECT));
            gotException = true;
        }

        assertTrue(gotException);
        gotException = false;

        try {
            MultiPoint.decodeMultiPoint(INVALID_INNER_TYPE);
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.COORDINATE_NOT_NUMERIC));
            gotException = true;
        }

        assertTrue(gotException);
        gotException = false;

        try {
            MultiPoint.decodeMultiPoint(NO_COORDINATES);
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.COORDINATES_KEY_NOT_FOUND));
            gotException = true;
        }

        assertTrue(gotException);
        gotException = false;

        try {
            MultiPoint.decodeMultiPoint(COORDS_NOT_ARRAY);
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.ELEMENT_NOT_ARRAY));
            gotException = true;
        }

        assertTrue(gotException);
        gotException = false;

        try {
            MultiPoint.decodeMultiPoint(COORDS_TOO_SHORT);
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.COORDINATE_ARRAY_TOO_SHORT));
            gotException = true;
        }

        assertTrue(gotException);
    }
}
