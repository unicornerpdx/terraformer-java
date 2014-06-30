package com.esri.terraformer.core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MultiPointTest {
    @Test
    public void testGetType() throws Exception {
        assertEquals(GeometryType.MULTIPOINT, getMultiPoint().getType());
        assertEquals(GeometryType.MULTIPOINT, new MultiPoint().getType());
    }

    @Test
    public void testIsValid() throws Exception {
        assertTrue(getMultiPoint().isValid());
        assertTrue(new MultiPoint(getMultiPoint()).isValid());
        assertFalse(new MultiPoint().isValid());
        assertFalse(new MultiPoint(new Point(100d, null)).isValid());
    }

    @Test
    public void testIsEquivalentTo() throws Exception {
        MultiPoint mp = getMultiPoint();
        MultiPoint otherMp = getMultiPointDiffOrder();

        assertTrue(mp.isEquivalentTo(mp));
        assertTrue(mp.isEquivalentTo(otherMp));
        assertTrue(otherMp.isEquivalentTo(mp));
        assertFalse(mp.isEquivalentTo(new Point(100d, 0d)));
        assertFalse(mp.isEquivalentTo(new MultiPoint()));
        assertFalse(mp.isEquivalentTo(new MultiPoint(new Point(100d, 0d))));
        assertFalse(mp.isEquivalentTo(new MultiPoint(new Point(100d, 0d), new Point(100d, 1d))));
    }

    public static MultiPoint getMultiPoint() {
        return new MultiPoint(new Point(100d, 0d), new Point(101d, 1d));
    }

    public static MultiPoint getMultiPointDiffOrder() {
        return new MultiPoint(new Point(101d, 1d), new Point(100d, 0d));
    }
}
