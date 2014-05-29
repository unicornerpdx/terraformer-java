package com.esri.terraformer;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LineStringTest {
    static final String LINEAR_RING = "{\"type\":\"LineString\",\"coordinates\":[[100.0,0.0],[101.0,0.0],[101.0,1.0],[100.0,1.0],[100.0,0.0]]}";
    static final String LINEAR_RING_ROTATED = "{\"type\":\"LineString\",\"coordinates\":[[101.0,1.0],[100.0,1.0],[100.0,0.0],[101.0,0.0],[101.0,1.0]]}";
    static final String LINEAR_RING_REVERSED = "{\"type\":\"LineString\",\"coordinates\":[[100.0,1.0],[101.0,1.0],[101.0,0.0],[100.0,0.0],[100.0,1.0]]}";
    static final String LINEAR_RING_REVERSED_ROTATED = "{\"type\":\"LineString\",\"coordinates\":[[101.0,0.0],[100.0,0.0],[100.0,1.0],[101.0,1.0],[101.0,0.0]]}";
    static final String LINEAR_RING_WRONG_ORDER = "{\"type\":\"LineString\",\"coordinates\":[[100.0,0.0],[101.0,1.0],[101.0,0.0],[100.0,1.0],[100.0,0.0]]}";

    @Test
    public void testGetType() throws Exception {

    }

    @Test
    public void testIsValid() throws Exception {

    }

    @Test
    public void testIsEquivalentTo() throws Exception {

    }

    @Test
    public void testToJson()  throws Exception {

    }

    @Test
    public void testToJsonObject()  throws Exception {

    }

    @Test
    public void testIsLinearRing() throws Exception {

    }

    @Test
    public void testDecodeLineString() throws Exception {

    }

    @Test
    public void testCompareLinearRings() throws Exception {
        LineString lr = getLinearRing();
        assertTrue(LineString.compareLinearRings(lr, LineString.decodeLineString(LINEAR_RING_ROTATED)));
        assertTrue(LineString.compareLinearRings(lr, LineString.decodeLineString(LINEAR_RING_REVERSED)));
        assertTrue(LineString.compareLinearRings(lr, LineString.decodeLineString(LINEAR_RING_REVERSED_ROTATED)));
        assertFalse(LineString.compareLinearRings(lr, LineString.decodeLineString(LINEAR_RING_WRONG_ORDER)));
    }

    public LineString getLinearRing() throws TerraformerException {
        return LineString.decodeLineString(LINEAR_RING);
    }
}
