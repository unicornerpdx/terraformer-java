package com.esri.terraformer;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MultiLineStringTest {
    @Test
    public void testGetType() throws Exception {
        assertEquals(GeometryType.MULTILINESTRING, getMultiLineString().getType());
        assertEquals(GeometryType.MULTILINESTRING, new MultiLineString().getType());
    }

    @Test
    public void testIsValid() throws Exception {
        assertTrue(getMultiLineString().isValid());
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
        MultiLineString mls = getMultiLineString();
        MultiLineString otherMls = getMultiLineStringDiffOrder();

        assertTrue(mls.isEquivalentTo(mls));
        assertTrue(mls.isEquivalentTo(otherMls));
        assertTrue(otherMls.isEquivalentTo(mls));
        assertFalse(mls.isEquivalentTo(new LineString(new Point(100d, 0d), new Point(101d, 1d))));
        assertFalse(mls.isEquivalentTo(new MultiLineString()));
        assertFalse(mls.isEquivalentTo(new MultiLineString(new LineString(new Point(100d, 0d), new Point(101d, 1d)))));
        assertFalse(mls.isEquivalentTo(new MultiLineString(new LineString(new Point(100d, 0d), new Point(101d, 1d)),
                new LineString(new Point(100d, 1d), new Point(101d, 1d)))));
    }

    static MultiLineString getMultiLineString() {
        return new MultiLineString(new LineString(new Point(100d, 0d),
                new Point(101d, 1d)), new LineString(new Point(103d, 5d), new Point(109d, 3d)));
    }

    static MultiLineString getMultiLineStringDiffOrder() {
        return new MultiLineString(new LineString(new Point(103d, 5d), new Point(109d, 3d)),
                new LineString(new Point(100d, 0d), new Point(101d, 1d)));
    }
}
