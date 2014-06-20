package com.esri.terraformer;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PointTest {
    @Test
    public void testGetType() throws Exception {
        assertEquals(GeometryType.POINT, getPoint().getType());
        assertEquals(GeometryType.POINT, new Point().getType());
    }

    @Test
    public void testIsValid() throws Exception {
        assertTrue(getPoint().isValid());
        assertTrue(new Point(getPoint()).isValid());
        assertFalse(new Point().isValid());
        assertFalse(new Point(100d, null).isValid());
    }

    @Test
    public void testIsEquivalentTo() throws Exception {
        Point p = getPoint();
        Point otherP = getPoint();
        assertTrue(p.isEquivalentTo(otherP));
        assertTrue(otherP.isEquivalentTo(p));
        assertFalse(p.isEquivalentTo(new Point()));
        assertFalse(p.isEquivalentTo(new Point(4)));
        assertFalse(p.isEquivalentTo(new Point(100d,0d)));
        assertFalse(p.isEquivalentTo(new Point(100d,0d,90d)));
    }

    static Point getPoint() {
        return new Point(100d,0d,90d,90d);
    }
}
