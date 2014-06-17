package com.esri.terraformer;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BaseGeometryTest {
    @Test
    public void testNaiveEquals() throws Exception {
        BaseGeometry<Double> pt = new Point(100d, 0d);
        BaseGeometry<Double> pt2 = new Point(100d, 0d, 3d);
        BaseGeometry<Double> pt3 = new Point(0d, 100d);
        BaseGeometry<Double> pt4 = new Point(100d, 0d);  // same as pt
        BaseGeometry<Point> mp = new MultiPoint((Point)pt, (Point)pt);
        BaseGeometry<Point> mp2 = new MultiPoint((Point)pt, (Point)pt3);
        BaseGeometry<Point> mp3 = new MultiPoint((Point)pt3, (Point)pt);

        assertEquals(false, BaseGeometry.naiveEquals(null, null));
        assertEquals(false, BaseGeometry.naiveEquals(null, pt));
        assertEquals(false, BaseGeometry.naiveEquals(pt, null));
        assertEquals(false, BaseGeometry.naiveEquals(pt, mp));
        assertEquals(false, BaseGeometry.naiveEquals(pt, pt2));
        assertEquals(true, BaseGeometry.naiveEquals(pt, pt));
        assertEquals(true, BaseGeometry.naiveEquals(pt, pt4));
        assertEquals(null, BaseGeometry.naiveEquals(mp2, mp3));
    }
}
