package com.esri.terraformer;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PolygonTest {
    @Test
    public void testGetType() throws Exception {
        assertEquals(GeometryType.POLYGON, getPolygon().getType());
        assertEquals(GeometryType.POLYGON, new Polygon().getType());
    }

    @Test
    public void testIsValid() throws Exception {
        assertTrue(getPolygon().isValid());
        assertTrue(new Polygon(getPolygon()).isValid());
        // polygon can have empty coordinates
        assertTrue(new Polygon().isValid());

        // a valid multilinestring is not necessarily a valid polygon, although the reverse is true
        assertFalse(new Polygon(MultiLineStringTest.getMultiLineString()).isValid());
        // invalid linestring
        assertFalse(new Polygon(new LineString(new Point(100d, 0d))).isValid());

        assertFalse(new Polygon(new LineString(
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
                )).isValid());

        // null linestring
        assertFalse(new Polygon(new LineString(
                        new Point(100d, 0d),
                        new Point(104d, 0d),
                        new Point(104d, 4d),
                        new Point(100d, 4d),
                        new Point(100d, 0d)),
                null,
                new LineString(
                        new Point(101d, 3d),
                        new Point(103d, 3d),
                        new Point(103d, 3.5d),
                        new Point(101d, 3.5d),
                        new Point(101d, 3d)
                )).isValid());
    }

    @Test
    public void testIsEquivalentTo() throws Exception {
        Polygon pg = getPolygon();
        Polygon otherPg = getPolygonDiffOrder();

        assertTrue(pg.isEquivalentTo(pg));
        assertTrue(pg.isEquivalentTo(otherPg));
        assertTrue(otherPg.isEquivalentTo(pg));
        // rotated linear ring
        assertTrue(pg.isEquivalentTo(new Polygon(
                new LineString(
                        new Point(100d, 0d),
                        new Point(104d, 0d),
                        new Point(104d, 4d),
                        new Point(100d, 4d),
                        new Point(100d, 0d)),
                new LineString(
                        new Point(103d, 1d),
                        new Point(101d, 1d),
                        new Point(101d, 0.5d),
                        new Point(103d, 0.5d),
                        new Point(103d, 1d)),
                new LineString(
                        new Point(101d, 3d),
                        new Point(103d, 3d),
                        new Point(103d, 3.5d),
                        new Point(101d, 3.5d),
                        new Point(101d, 3d)
                )
        )));

        assertFalse(pg.isEquivalentTo(MultiLineStringTest.getMultiLineString()));
        assertFalse(pg.isEquivalentTo(new Polygon()));
        // missing 2nd hole
        assertFalse(pg.isEquivalentTo(new Polygon(new LineString(
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
                        new Point(101d, 0.5d)))));
        // one coordinate difference
        assertFalse(pg.isEquivalentTo(new Polygon(new LineString(
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
                        new Point(102d, 3.5d),
                        new Point(101d, 3.5d),
                        new Point(101d, 3d)
                ))));
    }

    static Polygon getPolygon() {
        return new Polygon(
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
        );
    }

    static Polygon getPolygonDiffOrder() {
        return new Polygon(
                new LineString(
                        new Point(100d, 0d),
                        new Point(104d, 0d),
                        new Point(104d, 4d),
                        new Point(100d, 4d),
                        new Point(100d, 0d)),
                new LineString(
                        new Point(101d, 3d),
                        new Point(103d, 3d),
                        new Point(103d, 3.5d),
                        new Point(101d, 3.5d),
                        new Point(101d, 3d)),
                new LineString(
                        new Point(101d, 0.5d),
                        new Point(103d, 0.5d),
                        new Point(103d, 1d),
                        new Point(101d, 1d),
                        new Point(101d, 0.5d))
        );
    }
}
