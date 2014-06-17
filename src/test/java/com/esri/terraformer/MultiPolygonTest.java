package com.esri.terraformer;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MultiPolygonTest {
    @Test
    public void testGetType() throws Exception {
        assertEquals(GeometryType.MULTIPOLYGON, getMultiPolygon().getType());
        assertEquals(GeometryType.MULTIPOLYGON, new MultiPolygon().getType());
    }

    @Test
    public void testIsValid() throws Exception {
        assertTrue(getMultiPolygon().isValid());
        assertTrue(new MultiPolygon(getMultiPolygon()).isValid());
        // multipolygon can have empty coordinates
        assertTrue(new MultiPolygon().isValid());
        assertTrue(new MultiPolygon(PolygonTest.getPolygon()).isValid());

        // invalid polygon
        assertFalse(new MultiPolygon(new Polygon(new LineString(
                        new Point(100d, 0d),
                        new Point(104d, 0d),
                        new Point(104d, 4d),
                        new Point(100d, 4d)), // not a linear ring
                new LineString(
                        new Point(101d, 0.5d),
                        new Point(103d, 0.5d),
                        new Point(103d, 1d),
                        new Point(101d, 1d),
                        new Point(101d, 0.5d)),
                new LineString(
                        new Point(101d, 3d),
                        new Point(103d, 3d),
                        new Point(103d, 3.5d),
                        new Point(101d, 3.5d),
                        new Point(101d, 3d)
                ))).isValid());

        // null polygon
        assertFalse(new MultiPolygon(new Polygon(
                        new LineString(
                                new Point(100d, 0d),
                                new Point(104d, 0d),
                                new Point(104d, 4d),
                                new Point(100d, 4d),
                                new Point(100d, 0d)),
                        new LineString(
                                new Point(101d, 0.5d),
                                new Point(103d, 0.5d),
                                new Point(103d, 1d),
                                new Point(101d, 1d),
                                new Point(101d, 0.5d)),
                        new LineString(
                                new Point(101d, 3d),
                                new Point(103d, 3d),
                                new Point(103d, 3.5d),
                                new Point(101d, 3.5d),
                                new Point(101d, 3d)
                        )
                ),
                null,
                new Polygon(
                        new LineString(
                                new Point(95d, 0d),
                                new Point(99d, 0d),
                                new Point(99d, 4d),
                                new Point(95d, 4d),
                                new Point(95d, 0d)),
                        new LineString(
                                new Point(96d, 0.5d),
                                new Point(98d, 0.5d),
                                new Point(98d, 1d),
                                new Point(96d, 1d),
                                new Point(96d, 0.5d)),
                        new LineString(
                                new Point(96d, 3d),
                                new Point(98d, 3d),
                                new Point(98d, 3.5d),
                                new Point(96d, 3.5d),
                                new Point(96d, 3d)
                )
        )).isValid());
    }

    @Test
    public void testIsEquivalentTo() throws Exception {
        MultiPolygon mpg = getMultiPolygon();
        MultiPolygon otherMpg = getMultiPolygonDiffOrder();

        assertTrue(mpg.isEquivalentTo(mpg));
        assertTrue(mpg.isEquivalentTo(otherMpg));
        assertTrue(otherMpg.isEquivalentTo(mpg));
        // rotated linear ring
        assertTrue(mpg.isEquivalentTo(new MultiPolygon(new Polygon(
                new LineString(
                        new Point(100d, 0d),
                        new Point(104d, 0d),
                        new Point(104d, 4d),
                        new Point(100d, 4d),
                        new Point(100d, 0d)),
                new LineString(
                        new Point(101d, 0.5d),
                        new Point(103d, 0.5d),
                        new Point(103d, 1d),
                        new Point(101d, 1d),
                        new Point(101d, 0.5d)),
                new LineString(
                        new Point(101d, 3d),
                        new Point(103d, 3d),
                        new Point(103d, 3.5d),
                        new Point(101d, 3.5d),
                        new Point(101d, 3d)
                )
        ), new Polygon(
                new LineString(
                        new Point(95d, 0d),
                        new Point(99d, 0d),
                        new Point(99d, 4d),
                        new Point(95d, 4d),
                        new Point(95d, 0d)),
                new LineString(
                        new Point(96d, 1d),
                        new Point(96d, 0.5d),
                        new Point(98d, 0.5d),
                        new Point(98d, 1d),
                        new Point(96d, 1d)),
                new LineString(
                        new Point(96d, 3d),
                        new Point(98d, 3d),
                        new Point(98d, 3.5d),
                        new Point(96d, 3.5d),
                        new Point(96d, 3d)
                )
        ))));

        assertFalse(mpg.isEquivalentTo(PolygonTest.getPolygon()));
        assertFalse(mpg.isEquivalentTo(new MultiPolygon()));
        // missing 2nd polygon
        assertFalse(mpg.isEquivalentTo(new MultiPolygon(new Polygon(
                new LineString(
                        new Point(100d, 0d),
                        new Point(104d, 0d),
                        new Point(104d, 4d),
                        new Point(100d, 4d),
                        new Point(100d, 0d)),
                new LineString(
                        new Point(101d, 0.5d),
                        new Point(103d, 0.5d),
                        new Point(103d, 1d),
                        new Point(101d, 1d),
                        new Point(101d, 0.5d)),
                new LineString(
                        new Point(101d, 3d),
                        new Point(103d, 3d),
                        new Point(103d, 3.5d),
                        new Point(101d, 3.5d),
                        new Point(101d, 3d)
                )
        ))));
        // one coordinate difference
        assertFalse(mpg.isEquivalentTo(new MultiPolygon(new Polygon(
                new LineString(
                        new Point(100d, 0d),
                        new Point(104d, 0d),
                        new Point(104d, 4d),
                        new Point(100d, 4d),
                        new Point(100d, 0d)),
                new LineString(
                        new Point(101d, 0.5d),
                        new Point(103d, 0.5d),
                        new Point(103d, 1d),
                        new Point(101d, 1d),
                        new Point(101d, 0.5d)),
                new LineString(
                        new Point(101d, 3d),
                        new Point(103d, 3d),
                        new Point(103d, 3.5d),
                        new Point(101d, 3.5d),
                        new Point(101d, 3d)
                )
        ), new Polygon(
                new LineString(
                        new Point(95d, 0d),
                        new Point(99d, 0d),
                        new Point(99d, 4d),
                        new Point(95d, 4d),
                        new Point(95.4d, 0d)),
                new LineString(
                        new Point(96d, 0.5d),
                        new Point(98d, 0.5d),
                        new Point(98d, 1d),
                        new Point(96d, 1d),
                        new Point(96d, 0.5d)),
                new LineString(
                        new Point(96d, 3d),
                        new Point(98d, 3d),
                        new Point(98d, 3.5d),
                        new Point(96d, 3.5d),
                        new Point(96d, 3d)
                )
        ))));
    }

    static MultiPolygon getMultiPolygon() {
        return new MultiPolygon(new Polygon(
                new LineString(
                        new Point(100d, 0d),
                        new Point(104d, 0d),
                        new Point(104d, 4d),
                        new Point(100d, 4d),
                        new Point(100d, 0d)),
                new LineString(
                        new Point(101d, 0.5d),
                        new Point(103d, 0.5d),
                        new Point(103d, 1d),
                        new Point(101d, 1d),
                        new Point(101d, 0.5d)),
                new LineString(
                        new Point(101d, 3d),
                        new Point(103d, 3d),
                        new Point(103d, 3.5d),
                        new Point(101d, 3.5d),
                        new Point(101d, 3d)
                )
        ), new Polygon(
                new LineString(
                        new Point(95d, 0d),
                        new Point(99d, 0d),
                        new Point(99d, 4d),
                        new Point(95d, 4d),
                        new Point(95d, 0d)),
                new LineString(
                        new Point(96d, 0.5d),
                        new Point(98d, 0.5d),
                        new Point(98d, 1d),
                        new Point(96d, 1d),
                        new Point(96d, 0.5d)),
                new LineString(
                        new Point(96d, 3d),
                        new Point(98d, 3d),
                        new Point(98d, 3.5d),
                        new Point(96d, 3.5d),
                        new Point(96d, 3d)
                )
        ));
    }

    static MultiPolygon getMultiPolygonDiffOrder() {
        return new MultiPolygon(new Polygon(
                new LineString(
                        new Point(95d, 0d),
                        new Point(99d, 0d),
                        new Point(99d, 4d),
                        new Point(95d, 4d),
                        new Point(95d, 0d)),
                new LineString(
                        new Point(96d, 0.5d),
                        new Point(98d, 0.5d),
                        new Point(98d, 1d),
                        new Point(96d, 1d),
                        new Point(96d, 0.5d)),
                new LineString(
                        new Point(96d, 3d),
                        new Point(98d, 3d),
                        new Point(98d, 3.5d),
                        new Point(96d, 3.5d),
                        new Point(96d, 3d)
                )
        ), new Polygon(
                new LineString(
                        new Point(100d, 0d),
                        new Point(104d, 0d),
                        new Point(104d, 4d),
                        new Point(100d, 4d),
                        new Point(100d, 0d)),
                new LineString(
                        new Point(101d, 0.5d),
                        new Point(103d, 0.5d),
                        new Point(103d, 1d),
                        new Point(101d, 1d),
                        new Point(101d, 0.5d)),
                new LineString(
                        new Point(101d, 3d),
                        new Point(103d, 3d),
                        new Point(103d, 3.5d),
                        new Point(101d, 3.5d),
                        new Point(101d, 3d)
                )
        ));
    }
}
