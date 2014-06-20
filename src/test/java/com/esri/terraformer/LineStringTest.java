package com.esri.terraformer;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LineStringTest {
    @Test
    public void testGetType() throws Exception {
        assertEquals(GeometryType.LINESTRING, getLineString().getType());
        assertEquals(GeometryType.LINESTRING, new LineString().getType());
    }

    @Test
    public void testIsValid() throws Exception {
        assertTrue(getLineString().isValid());
        assertTrue(new LineString(getLineString()).isValid());
        assertFalse(new LineString().isValid());
        assertFalse(new LineString(new Point(100d, 0d), null).isValid());
    }

    @Test
    public void testIsEquivalentTo() throws Exception {
        LineString ls = getLineString();
        LineString otherLs = getLineStringDiffOrder();

        assertTrue(otherLs.isEquivalentTo(ls));
        assertTrue(otherLs.isEquivalentTo(otherLs));
        assertTrue(otherLs.isEquivalentTo(ls));
        assertTrue(otherLs.isEquivalentTo(otherLs));
        assertFalse(ls.isEquivalentTo(new Point(100d, 0d)));
        assertFalse(ls.isEquivalentTo(new LineString()));
        assertFalse(ls.isEquivalentTo(new LineString(new Point(100d, 0d))));
        assertFalse(ls.isEquivalentTo(new LineString(new Point(100d, 0d), new Point(100d, 1d))));
    }

    @Test
    public void testIsLinearRing() throws Exception {
        assertTrue(getLinearRing().isLinearRing());
        assertFalse(getLineString().isLinearRing());
        assertFalse(new LineString().isLinearRing());
    }

    @Test
    public void testCompareLinearRings() throws Exception {
        LineString lr = getLinearRing();
        assertTrue(LineString.compareLinearRings(lr, getLinearRingRotated()));
        assertTrue(LineString.compareLinearRings(lr, getLinearRingReversed()));
        assertTrue(LineString.compareLinearRings(lr, getLinearRingReversedRotated()));
        assertFalse(LineString.compareLinearRings(lr, getLinearRingWrongOrder()));
    }

    static LineString getLinearRing() {
        return new LineString(new Point(100.0,0.0), new Point(101.0,0.0), new Point(101.0,1.0), new Point(100.0,1.0), new Point(100.0,0.0));
    }

    static LineString getLinearRingRotated() {
        return new LineString(new Point(101.0,1.0), new Point(100.0,1.0), new Point(100.0,0.0), new Point(101.0,0.0), new Point(101.0,1.0));
    }

    static LineString getLinearRingReversed() {
        return new LineString(new Point(100.0,1.0), new Point(101.0,1.0), new Point(101.0,0.0), new Point(100.0,0.0), new Point(100.0,1.0));
    }

    static LineString getLinearRingReversedRotated() {
        return new LineString(new Point(101.0,0.0), new Point(100.0,0.0), new Point(100.0,1.0), new Point(101.0,1.0), new Point(101.0,0.0));
    }

    static LineString getLinearRingWrongOrder() {
        return new LineString(new Point(100.0,0.0), new Point(101.0,1.0), new Point(101.0,0.0), new Point(100.0,1.0), new Point(100.0,0.0));
    }

    static LineString getLineString() {
        return new LineString(new Point(100d, 0d), new Point(101d, 1d));
    }

    static LineString getLineStringDiffOrder() {
        return new LineString(new Point(101d, 1d), new Point(100d, 0d));
    }
}
