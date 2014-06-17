package com.esri.terraformer;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FeatureCollectionTest {
    @Test
    public void testGetType() throws Exception {
        assertEquals(GeometryType.FEATURECOLLECTION, getFeatureCollection().getType());
        assertEquals(GeometryType.FEATURECOLLECTION, new FeatureCollection().getType());
    }

    @Test
    public void testIsValid() throws Exception {
        assertTrue(new FeatureCollection(getFeatureCollection()).isValid());
        // feature collection can have empty features
        assertTrue(new FeatureCollection().isValid());

        // invalid multipolygon
        assertFalse(new FeatureCollection(FeatureTest.getGeometryCollectionFeature(),
                new Feature(new MultiPolygon(new Polygon(new LineString(
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
                )))),
                FeatureTest.getPolygonFeature()
        ).isValid());

        // null polygon
        assertFalse(new FeatureCollection(
                FeatureTest.getPolygonFeature(),
                null,
                FeatureTest.getMultiPolygonFeature()
        ).isValid());
    }

    @Test
    public void testIsEquivalentTo() throws Exception {
        FeatureCollection fc = getFeatureCollection();
        FeatureCollection otherFc = fcDiffOrder();

        assertTrue(fc.isEquivalentTo(fc));
        assertTrue(fc.isEquivalentTo(otherFc));
        assertTrue(otherFc.isEquivalentTo(fc));
        assertTrue(otherFc.isEquivalentTo(fc));
        // rotated linear ring
        assertTrue(fc.isEquivalentTo(new FeatureCollection(
                FeatureTest.getMultiLineStringFeature(),
                FeatureTest.getPolygonFeature(),
                new Feature(new MultiPolygon(new Polygon(
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
                ))),
                FeatureTest.getGeometryCollectionFeature()
        )));

        assertFalse(fc.isEquivalentTo(GeometryCollectionTest.getGeometryCollection()));
        assertFalse(fc.isEquivalentTo(new FeatureCollection()));
        // missing the polygon
        assertFalse(fc.isEquivalentTo(new FeatureCollection(
                FeatureTest.getMultiLineStringFeature(),
                FeatureTest.getMultiPolygonFeature(),
                FeatureTest.getGeometryCollectionFeature()
        )));
        // one coordinate difference
        assertFalse(fc.isEquivalentTo(new FeatureCollection(
                FeatureTest.getMultiLineStringFeature(),
                new Feature(new Polygon(
                        new LineString(
                                new Point(100d, 0d),
                                new Point(104d, 0d),
                                new Point(104d, 4d),
                                new Point(100d, 4d),
                                new Point(100d, 0d)),
                        new LineString(
                                new Point(101d, 0.5d),
                                new Point(103d, 0.6d),
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
                )),
                FeatureTest.getMultiPolygonFeature(),
                FeatureTest.getGeometryCollectionFeature()
        )));
    }

    static FeatureCollection getFeatureCollection() {
        return new FeatureCollection(
                FeatureTest.getMultiLineStringFeature(),
                FeatureTest.getPolygonFeature(),
                FeatureTest.getMultiPolygonFeature(),
                FeatureTest.getGeometryCollectionFeature()
        );
    }

    static FeatureCollection fcDiffOrder() {
        return new FeatureCollection(
                FeatureTest.getGeometryCollectionFeature(),
                FeatureTest.getPolygonFeature(),
                FeatureTest.getMultiLineStringFeature(),
                FeatureTest.getMultiPolygonFeature()
        );
    }
}
