package com.esri.terraformer;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class LineStringTest {
    private static final String LINEAR_RING = "{\"type\":\"LineString\",\"coordinates\":[[100.0,0.0],[101.0,0.0],[101.0,1.0],[100.0,1.0],[100.0,0.0]]}";
    private static final String LINEAR_RING_ROTATED = "{\"type\":\"LineString\",\"coordinates\":[[101.0,1.0],[100.0,1.0],[100.0,0.0],[101.0,0.0],[101.0,1.0]]}";
    private static final String LINEAR_RING_REVERSED = "{\"type\":\"LineString\",\"coordinates\":[[100.0,1.0],[101.0,1.0],[101.0,0.0],[100.0,0.0],[100.0,1.0]]}";
    private static final String LINEAR_RING_REVERSED_ROTATED = "{\"type\":\"LineString\",\"coordinates\":[[101.0,0.0],[100.0,0.0],[100.0,1.0],[101.0,1.0],[101.0,0.0]]}";
    private static final String LINEAR_RING_WRONG_ORDER = "{\"type\":\"LineString\",\"coordinates\":[[100.0,0.0],[101.0,1.0],[101.0,0.0],[100.0,1.0],[100.0,0.0]]}";

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
    public void testIsLinearRing() throws Exception {

    }

    @Test
    public void testDecodeLineString() throws Exception {

    }

    @Test
    public void testCompareLinearRings() throws Exception {
        LineString lr = getLinearRing();
        LineString compare = null;
        try {
            compare = LineString.decodeLineString(LINEAR_RING_ROTATED);
        } catch (TerraformerException e) {
            fail(e.getMessage());
        }

        assertTrue(LineString.compareLinearRings(lr, compare));

        try {
            compare = LineString.decodeLineString(LINEAR_RING_REVERSED);
        } catch (TerraformerException e) {
            fail(e.getMessage());
        }

        assertTrue(LineString.compareLinearRings(lr, compare));

        try {
            compare = LineString.decodeLineString(LINEAR_RING_REVERSED_ROTATED);
        } catch (TerraformerException e) {
            fail(e.getMessage());
        }

        assertTrue(LineString.compareLinearRings(lr, compare));

        try {
            compare = LineString.decodeLineString(LINEAR_RING_WRONG_ORDER);
        } catch (TerraformerException e) {
            fail(e.getMessage());
        }

        assertFalse(LineString.compareLinearRings(lr, compare));
    }

    public LineString getLinearRing() {
        LineString lr;
        try {
            lr = LineString.decodeLineString(LINEAR_RING);
        } catch (TerraformerException e) {
            fail(e.getMessage());
            return null;
        }

        return lr;
    }
}
