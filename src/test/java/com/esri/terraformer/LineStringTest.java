package com.esri.terraformer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LineStringTest {
    static final String VALID_LINE_STRING = "{\"type\":\"LineString\",\"coordinates\":[[100.0,0.0],[101.0,1.0]]}";
    static final String VALID_DIFF_ORDER = "{\"type\":\"LineString\",\"coordinates\":[[101.0,1.0],[100.0,0.0]]}";
    static final String WRONG_TYPE = "{\"type\":\"MultiPoint\",\"coordinates\":[100.0,0.0]}";
    static final String NO_TYPE = "{\"coordinates\":[[100.0,0.0],[101.0,1.0]]}";
    static final String NOT_AN_OBJECT = "[\"type\",\"coordinates\"]";
    static final String INVALID_INNER_TYPE = "{\"type\":\"LineString\",\"coordinates\":[[100.0,3.0],[100.0,\"squid\"],[101.0,1.0]]}";
    static final String NO_COORDINATES = "{\"type\":\"LineString\"}";
    static final String COORDS_NOT_ARRAY = "{\"type\":\"LineString\",\"coordinates\":\"horse\"}";
    static final String COORDS_TOO_SHORT = "{\"type\":\"LineString\",\"coordinates\":[[101.0,1.0]]}";

    static final String LINEAR_RING = "{\"type\":\"LineString\",\"coordinates\":[[100.0,0.0],[101.0,0.0],[101.0,1.0],[100.0,1.0],[100.0,0.0]]}";
    static final String LINEAR_RING_ROTATED = "{\"type\":\"LineString\",\"coordinates\":[[101.0,1.0],[100.0,1.0],[100.0,0.0],[101.0,0.0],[101.0,1.0]]}";
    static final String LINEAR_RING_REVERSED = "{\"type\":\"LineString\",\"coordinates\":[[100.0,1.0],[101.0,1.0],[101.0,0.0],[100.0,0.0],[100.0,1.0]]}";
    static final String LINEAR_RING_REVERSED_ROTATED = "{\"type\":\"LineString\",\"coordinates\":[[101.0,0.0],[100.0,0.0],[100.0,1.0],[101.0,1.0],[101.0,0.0]]}";
    static final String LINEAR_RING_WRONG_ORDER = "{\"type\":\"LineString\",\"coordinates\":[[100.0,0.0],[101.0,1.0],[101.0,0.0],[100.0,1.0],[100.0,0.0]]}";

    @Test
    public void testGetType() throws Exception {
        assertEquals(GeoJsonType.LINESTRING, LineString.decodeLineString(VALID_LINE_STRING).getType());
        assertEquals(GeoJsonType.LINESTRING, new LineString().getType());
    }

    @Test
    public void testIsValid() throws Exception {
        assertTrue(LineString.decodeLineString(VALID_LINE_STRING).isValid());
        assertTrue(new LineString(getLineString()).isValid());
        assertFalse(new LineString().isValid());
        assertFalse(new LineString(new Point(100d)).isValid());
        assertFalse(new LineString(new Point(100d, 0d), null).isValid());
    }

    @Test
    public void testIsEquivalentTo() throws Exception {
        LineString ls = LineString.decodeLineString(VALID_LINE_STRING);
        LineString otherLs = getLineString();
        LineString anotherLs = LineString.decodeLineString(VALID_DIFF_ORDER);

        assertTrue(ls.isEquivalentTo(otherLs));
        assertTrue(ls.isEquivalentTo(anotherLs));
        assertTrue(otherLs.isEquivalentTo(ls));
        assertTrue(otherLs.isEquivalentTo(anotherLs));
        assertTrue(anotherLs.isEquivalentTo(ls));
        assertTrue(anotherLs.isEquivalentTo(otherLs));
        assertFalse(ls.isEquivalentTo(new Point(100d, 0d)));
        assertFalse(ls.isEquivalentTo(new LineString()));
        assertFalse(ls.isEquivalentTo(new LineString(new Point(100d, 0d))));
        assertFalse(ls.isEquivalentTo(new LineString(new Point(100d, 0d), new Point(100d, 1d))));
    }

    @Test
    public void testToJson()  throws Exception {
        assertEquals(VALID_LINE_STRING, getLineString().toJson());
    }

    @Test
    public void testToJsonObject()  throws Exception {
        JsonObject obj1 = getLineString().toJsonObject(null);
        JsonObject obj2 = getLineString().toJsonObject(new Gson());
        assertEquals(obj1.toString(), obj2.toString());
    }

    @Test
    public void testIsLinearRing() throws Exception {
        assertTrue(LineString.decodeLineString(LINEAR_RING).isLinearRing());
        assertFalse(LineString.decodeLineString(VALID_LINE_STRING).isLinearRing());
        assertFalse(new LineString().isLinearRing());
    }

    @Test
    public void testCompareLinearRings() throws Exception {
        LineString lr = LineString.decodeLineString(LINEAR_RING);
        assertTrue(LineString.compareLinearRings(lr, LineString.decodeLineString(LINEAR_RING_ROTATED)));
        assertTrue(LineString.compareLinearRings(lr, LineString.decodeLineString(LINEAR_RING_REVERSED)));
        assertTrue(LineString.compareLinearRings(lr, LineString.decodeLineString(LINEAR_RING_REVERSED_ROTATED)));
        assertFalse(LineString.compareLinearRings(lr, LineString.decodeLineString(LINEAR_RING_WRONG_ORDER)));
    }

    @Test
    public void testDecodeLineString() throws Exception {
        assertEquals(VALID_LINE_STRING, LineString.decodeLineString(VALID_LINE_STRING).toJson());

        boolean gotException = false;

        try {
            LineString.decodeLineString(WRONG_TYPE);
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.NOT_OF_TYPE));
            gotException = true;
        }

        assertTrue(gotException);
        gotException = false;

        try {
            LineString.decodeLineString(NO_TYPE);
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.NOT_OF_TYPE));
            gotException = true;
        }

        assertTrue(gotException);
        gotException = false;

        try {
            LineString.decodeLineString(NOT_AN_OBJECT);
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.NOT_A_JSON_OBJECT));
            gotException = true;
        }

        assertTrue(gotException);
        gotException = false;

        try {
            LineString.decodeLineString(INVALID_INNER_TYPE);
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.COORDINATE_NOT_NUMERIC));
            gotException = true;
        }

        assertTrue(gotException);
        gotException = false;

        try {
            LineString.decodeLineString(NO_COORDINATES);
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.COORDINATES_KEY_NOT_FOUND));
            gotException = true;
        }

        assertTrue(gotException);
        gotException = false;

        try {
            LineString.decodeLineString(COORDS_NOT_ARRAY);
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.ELEMENT_NOT_ARRAY));
            gotException = true;
        }

        assertTrue(gotException);
        gotException = false;

        try {
            LineString.decodeLineString(COORDS_TOO_SHORT);
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.COORDINATE_ARRAY_TOO_SHORT));
            gotException = true;
        }

        assertTrue(gotException);
    }

    static LineString getLineString() {
        return new LineString(new Point(100d, 0d), new Point(101d, 1d));
    }
}
