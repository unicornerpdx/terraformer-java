package com.esri.terraformer.formats;

import com.esri.terraformer.core.Feature;
import com.esri.terraformer.core.FeatureCollection;
import com.esri.terraformer.core.GeometryCollection;
import com.esri.terraformer.core.LineString;
import com.esri.terraformer.core.MultiLineString;
import com.esri.terraformer.core.MultiPoint;
import com.esri.terraformer.core.MultiPolygon;
import com.esri.terraformer.core.Point;
import com.esri.terraformer.core.Polygon;
import com.esri.terraformer.core.Terraformer;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EsriJsonTest {
    static Terraformer e;

    @BeforeClass
    public static void setUpTerraformer() {
        e = new Terraformer();
        e.setEncoder(new EsriJson());
        e.setDecoder(new EsriJson());
    }

    @Test
    public void testDecodePoint() throws Exception {
        Point simplePoint = (Point) e.decode("{\"x\":0.5,\"y\":0.25}");
        assertEquals(simplePoint.getX(), 0.5, 0.01);
        assertEquals(simplePoint.getY(), 0.25, 0.01);

        Point complexPoint = (Point) e.decode("{\"x\":100,\"y\":0,\"z\":90,\"m\":90,\"hasZ\":true,\"hasM\":true}");
        assertEquals(complexPoint.getZ(), 90, 0.01);
        assertEquals(complexPoint.get(3), 90, 0.01);
    }

    @Test
    public void testDecodeMultiPoint() throws Exception {
        MultiPoint m1 = (MultiPoint) e.decode("{\"points\":[[0,0],[1,0],[1,1]],\"spatialReference\":{\"wkid\":4326}}");
        assertEquals(m1.get(0), new Point(0.0, 0.0));
        assertEquals(m1.get(1), new Point(1.0, 0.0));
        assertEquals(m1.get(2), new Point(1.0, 1.0));

        MultiPoint m2 = (MultiPoint) e.decode("{\"hasZ\":true,\"points\":[[0,0,1],[1,0,2],[1,1,3]]}");
        assertEquals(m2.get(0), new Point(0.0, 0.0, 1.0));
        assertEquals(m2.get(1), new Point(1.0, 0.0, 2.0));
        assertEquals(m2.get(2), new Point(1.0, 1.0, 3.0));

        MultiPoint m3 = (MultiPoint) e.decode("{\"hasM\":true,\"points\":[[0,0,1],[1,0,2],[1,1,3]]}");
        assertEquals(m3.get(0), new Point(0.0, 0.0, 0.0, 1.0));
        assertEquals(m3.get(1), new Point(1.0, 0.0, 0.0, 2.0));
        assertEquals(m3.get(2), new Point(1.0, 1.0, 0.0, 3.0));

        MultiPoint m4 = (MultiPoint) e.decode("{\"hasZ\":true,\"hasM\":true,\"points\":[[0,1,2,3],[4,5,6,7],[8,9,10,11]]}");
        assertEquals(m4.get(0), new Point(0.0, 1.0, 2.0, 3.0));
        assertEquals(m4.get(1), new Point(4.0, 5.0, 6.0, 7.0));
        assertEquals(m4.get(2), new Point(8.0, 9.0, 10.0, 11.0));
    }

    @Test
    public void testDecodePolyline() throws Exception {
        LineString polyline2D = (LineString) e.decode("{\"paths\":[[[100,0],[101,1],[103,5],[109,3]]],\"spatialReference\":{\"wkid\":4326}}");
        assertEquals(polyline2D.get(0), new Point(100.0, 0.0));
        assertEquals(polyline2D.get(1), new Point(101.0, 1.0));
        assertEquals(polyline2D.get(2), new Point(103.0, 5.0));
        assertEquals(polyline2D.get(3), new Point(109.0, 3.0));

        LineString polyline3D = (LineString) e.decode("{\"hasZ\":true,\"paths\":[[[0,0,1],[1,0,2],[1,1,3]]]}");
        assertEquals(polyline3D.get(0), new Point(0.0, 0.0, 1.0));
        assertEquals(polyline3D.get(1), new Point(1.0, 0.0, 2.0));
        assertEquals(polyline3D.get(2), new Point(1.0, 1.0, 3.0));

        MultiLineString multiPolyline = (MultiLineString) e.decode("{\"hasZ\":true,\"paths\":[[[0,0,1],[1,0,2],[1,1,3]],[[1,1,2],[2,1,3],[2,2,4]]]}");
        assertEquals(multiPolyline.get(0).get(0), new Point(0.0, 0.0, 1.0));
        assertEquals(multiPolyline.get(0).get(1), new Point(1.0, 0.0, 2.0));
        assertEquals(multiPolyline.get(0).get(2), new Point(1.0, 1.0, 3.0));
        assertEquals(multiPolyline.get(1).get(0), new Point(1.0, 1.0, 2.0));
        assertEquals(multiPolyline.get(1).get(1), new Point(2.0, 1.0, 3.0));
        assertEquals(multiPolyline.get(1).get(2), new Point(2.0, 2.0, 4.0));
    }

    @Test
    public void testDecodePolygon() throws Exception {
        Polygon p = (Polygon) e.decode("{\"rings\":[[[100,0],[100,4],[104,4],[104,0],[100,0]],[[101,0.5],[103,0.5],[103,1],[101,1],[101,0.5]],[[101,3],[103,3],[103,3.5],[101,3.5],[101,3]]],\"spatialReference\":{\"wkid\":4326}}");
        assertTrue(p.getOuterRing().isLinearRing());
        assertEquals(p.getOuterRing().size(), 5);
        assertEquals(p.getHoles().size(), 2);

        Polygon p3D = (Polygon) e.decode("{\"hasZ\":true,\"hasM\":true,\"rings\":[[[1,1,1,1],[2,2,2,2],[3,3,3,3],[4,4,4,4],[1,1,1,1]]]}");
        assertTrue(p3D.getOuterRing().isLinearRing());
        assertEquals(p3D.getOuterRing().size(), 5);
        assertEquals(p3D.getHoles().size(), 0);
        assertEquals(p3D.getOuterRing().get(0), new Point(1d, 1d, 1d, 1d));
        assertEquals(p3D.getOuterRing().get(1), new Point(2d, 2d, 2d, 2d));
        assertEquals(p3D.getOuterRing().get(2), new Point(3d,3d,3d,3d));
        assertEquals(p3D.getOuterRing().get(3), new Point(4d,4d,4d,4d));
        assertEquals(p3D.getOuterRing().get(4), new Point(1d,1d,1d,1d));

        Polygon moreHoleThanPolyReally = (Polygon) e.decode("{\"rings\":[[[0,0],[0,1],[1,1],[1,0],[0,0]],[[0.1,0.1],[0.9,0.1],[0.9,0.9],[0.1,0.9],[0.1,0.1]]]}");
        assertEquals(moreHoleThanPolyReally.getOuterRing().size(), 5);
        assertEquals(moreHoleThanPolyReally.getHoles().size(), 1);

        MultiPolygon mp = (MultiPolygon) e.decode("{\"rings\":[[[-122.63,45.52],[-122.57,45.53],[-122.52,45.50],[-122.49,45.48],[-122.64,45.49],[-122.63,45.52],[-122.63,45.52]],[[-83,35],[-74,35],[-74,41],[-83,41],[-83,35]]]}");
        assertEquals(mp.size(), 2);
        assertEquals(mp.get(0).getOuterRing().size(), 7);
        assertEquals(mp.get(0).getHoles().size(), 0);
        assertEquals(mp.get(1).getOuterRing().size(), 5);
        assertEquals(mp.get(1).getHoles().size(), 0);
    }

    @Test
    public void testDecodeFeature() throws Exception {
        Feature f = (Feature) e.decode("{\"geometry\":{\"hasZ\":true,\"paths\":[[[0,0,1],[1,0,2],[1,1,3]]]},\"attributes\":{\"foo\":\"bar\"}}");
        assertEquals(f.getGeometry(), e.decode("{\"hasZ\":true,\"paths\":[[[0,0,1],[1,0,2],[1,1,3]]]}"));
        assertEquals(f.getProperties().getAsJsonPrimitive("foo"), new JsonPrimitive("bar"));
    }

    @Test
    public void testEncodePoint() throws Exception {
        Point p1 = new Point(1d, 2d);
        assertEquals(e.encode(p1), "{\"spatialReference\":{\"wkid\":4326},\"x\":1.0,\"y\":2.0}");

        Point p2 = new Point(1d, 2d, 3d);
        assertEquals(e.encode(p2), "{\"spatialReference\":{\"wkid\":4326},\"x\":1.0,\"y\":2.0,\"z\":3.0,\"hasZ\":true}");

        Point p3 = new Point(1d, 2d, 3d, 4d);
        assertEquals(e.encode(p3), "{\"spatialReference\":{\"wkid\":4326},\"x\":1.0,\"y\":2.0,\"z\":3.0,\"hasZ\":true,\"m\":4.0,\"hasM\":true}");
    }

    @Test
    public void testEncodeMultiPointPoint() throws Exception {
        MultiPoint m1 = new MultiPoint(new Point(0d,0d),
                                       new Point(1d,1d),
                                       new Point(2d,2d));

        assertEquals(e.encode(m1), "{\"hasZ\":false,\"hasM\":false,\"spatialReference\":{\"wkid\":4326},\"points\":[[0.0,0.0],[1.0,1.0],[2.0,2.0]]}");

        MultiPoint m2 = new MultiPoint(new Point(0d,0d,0d),
                                       new Point(1d,1d,1d),
                                       new Point(2d,2d,2d));

        assertEquals(e.encode(m2), "{\"hasZ\":true,\"hasM\":false,\"spatialReference\":{\"wkid\":4326},\"points\":[[0.0,0.0,0.0],[1.0,1.0,1.0],[2.0,2.0,2.0]]}");

        MultiPoint m3 = new MultiPoint(new Point(0d,0d,0d,0d),
                                       new Point(1d,1d,1d,1d),
                                       new Point(2d,2d,2d,2d));

        assertEquals(e.encode(m3), "{\"hasZ\":true,\"hasM\":true,\"spatialReference\":{\"wkid\":4326},\"points\":[[0.0,0.0,0.0,0.0],[1.0,1.0,1.0,1.0],[2.0,2.0,2.0,2.0]]}");
    }

    @Test
    public void testEncodeLineString() throws Exception {
        LineString l1 = new LineString(new Point(0d,0d),
                                       new Point(1d,1d),
                                       new Point(2d,2d));

        assertEquals(e.encode(l1), "{\"hasZ\":false,\"hasM\":false,\"spatialReference\":{\"wkid\":4326},\"paths\":[[[0.0,0.0],[1.0,1.0],[2.0,2.0]]]}");

        LineString l2 = new LineString(new Point(0d,0d,0d),
                                       new Point(1d,1d,1d),
                                       new Point(2d,2d,2d));

        assertEquals(e.encode(l2), "{\"hasZ\":true,\"hasM\":false,\"spatialReference\":{\"wkid\":4326},\"paths\":[[[0.0,0.0,0.0],[1.0,1.0,1.0],[2.0,2.0,2.0]]]}");

        LineString l3 = new LineString(new Point(0d,0d,0d,0d),
                                       new Point(1d,1d,1d,1d),
                                       new Point(2d,2d,2d,2d));

        assertEquals(e.encode(l3), "{\"hasZ\":true,\"hasM\":true,\"spatialReference\":{\"wkid\":4326},\"paths\":[[[0.0,0.0,0.0,0.0],[1.0,1.0,1.0,1.0],[2.0,2.0,2.0,2.0]]]}");
    }

    @Test
    public void testEncodeMultiLineString() throws Exception {
        MultiLineString m1 = new MultiLineString(
                new LineString(
                        new Point(0d,0d),
                        new Point(1d,1d),
                        new Point(2d,2d)
                ),
                new LineString(
                        new Point(10d,10d),
                        new Point(20d,20d),
                        new Point(30d,30d)
                ));

        assertEquals(e.encode(m1), "{\"hasZ\":false,\"hasM\":false,\"spatialReference\":{\"wkid\":4326},\"paths\":[[[0.0,0.0],[1.0,1.0],[2.0,2.0]],[[10.0,10.0],[20.0,20.0],[30.0,30.0]]]}");

        MultiLineString m2 = new MultiLineString(
                new LineString(
                        new Point(0d,0d,0d),
                        new Point(1d,1d,1d),
                        new Point(2d,2d,2d)
                ),
                new LineString(
                        new Point(10d,10d,10d),
                        new Point(20d,20d,20d),
                        new Point(30d,30d,30d)
                ));

        assertEquals(e.encode(m2), "{\"hasZ\":true,\"hasM\":false,\"spatialReference\":{\"wkid\":4326},\"paths\":[[[0.0,0.0,0.0],[1.0,1.0,1.0],[2.0,2.0,2.0]],[[10.0,10.0,10.0],[20.0,20.0,20.0],[30.0,30.0,30.0]]]}");

        MultiLineString m3 = new MultiLineString(
                new LineString(
                        new Point(0d,0d,0d,0d),
                        new Point(1d,1d,1d,1d),
                        new Point(2d,2d,2d,2d)
                ),
                new LineString(
                        new Point(10d,10d,10d,10d),
                        new Point(20d,20d,20d,20d),
                        new Point(30d,30d,30d,30d)
                ));

        assertEquals(e.encode(m3), "{\"hasZ\":true,\"hasM\":true,\"spatialReference\":{\"wkid\":4326},\"paths\":[[[0.0,0.0,0.0,0.0],[1.0,1.0,1.0,1.0],[2.0,2.0,2.0,2.0]],[[10.0,10.0,10.0,10.0],[20.0,20.0,20.0,20.0],[30.0,30.0,30.0,30.0]]]}");
    }

    @Test
    public void testEncodePolygon() throws Exception {
        Polygon p1 = new Polygon(
                new LineString(
                        new Point(0d,0d),
                        new Point(1d,1d),
                        new Point(2d,2d),
                        new Point(0d,0d)
                )
        );

        assertEquals(e.encode(p1), "{\"hasZ\":false,\"hasM\":false,\"spatialReference\":{\"wkid\":4326},\"rings\":[[[0.0,0.0],[2.0,2.0],[1.0,1.0],[0.0,0.0]]]}");

        // outer ring and hole directions are reversed to ensure ring orientations are correctly enforced
        Polygon p2 = new Polygon(
                new LineString(
                        new Point(0d,0d,0d),
                        new Point(0d,10d,0d),
                        new Point(10d,10d,0d),
                        new Point(10d,0d,0d),
                        new Point(0d,0d,0d)

                ),
                new LineString(
                        new Point(1d,1d,0d),
                        new Point(1d,9d,0d),
                        new Point(9d,9d,0d),
                        new Point(9d,1d,0d),
                        new Point(1d,1d,0d)
                ));

        assertEquals(e.encode(p2), "{\"hasZ\":true,\"hasM\":false,\"spatialReference\":{\"wkid\":4326},\"rings\":[[[0.0,0.0,0.0],[10.0,0.0,0.0],[10.0,10.0,0.0],[0.0,10.0,0.0],[0.0,0.0,0.0]],[[1.0,1.0,0.0],[9.0,1.0,0.0],[9.0,9.0,0.0],[1.0,9.0,0.0],[1.0,1.0,0.0]]]}");
    }

    @Test
    public void testEncodeMultiPolygon() throws Exception {
        MultiPolygon m1 = new MultiPolygon(
                new Polygon(new LineString(new Point(0d,0d), new Point(1d,1d), new Point(2d,2d), new Point(0d,0d))),
                new Polygon(new LineString(new Point(0d,0d), new Point(5d,5d), new Point(9d,9d), new Point(0d,0d)))
        );

        assertEquals(e.encode(m1), "{\"hasZ\":false,\"hasM\":false,\"spatialReference\":{\"wkid\":4326},\"rings\":[[[0.0,0.0],[2.0,2.0],[1.0,1.0],[0.0,0.0]],[[0.0,0.0],[9.0,9.0],[5.0,5.0],[0.0,0.0]]]}");
    }

    @Test
    public void testEncodeGeometryCollection() throws Exception {
        GeometryCollection g1 = new GeometryCollection(new Point(0d,0d), new LineString(new Point(0d,0d), new Point(1d,1d)));

        assertEquals(e.encode(g1), "[{\"spatialReference\":{\"wkid\":4326},\"x\":0.0,\"y\":0.0},{\"hasZ\":false,\"hasM\":false,\"spatialReference\":{\"wkid\":4326},\"paths\":[[[0.0,0.0],[1.0,1.0]]]}]");
    }

    @Test
    public void testEncodeFeature() throws Exception {
        Feature f = new Feature(new Point(0d,0d));
        assertEquals(e.encode(f), "{\"geometry\":{\"spatialReference\":{\"wkid\":4326},\"x\":0.0,\"y\":0.0},\"attributes\":{}}");

        JsonObject p = new JsonObject();
        p.add("foo", new JsonPrimitive("bar"));
        f.setProperties(p);

        assertEquals(e.encode(f), "{\"geometry\":{\"spatialReference\":{\"wkid\":4326},\"x\":0.0,\"y\":0.0},\"attributes\":{\"foo\":\"bar\"}}");
    }

    @Test
    public void testEncodeFeatureCollection() throws Exception {
        FeatureCollection f = new FeatureCollection(new Feature(new Point(0d,0d)), new Feature(new Point(1d,1d)));
        assertEquals(e.encode(f), "[{\"geometry\":{\"spatialReference\":{\"wkid\":4326},\"x\":0.0,\"y\":0.0},\"attributes\":{}},{\"geometry\":{\"spatialReference\":{\"wkid\":4326},\"x\":1.0,\"y\":1.0},\"attributes\":{}}]");
    }

    @Test
    public void testRingContainsPoint() throws Exception {
        LineString ring = new LineString(
                new Point(-10d, -10d),
                new Point(-10d, 10d),
                new Point(10d, 10d),
                new Point(10d, -10d),
                new Point(-10d, -10d));
        Point center = new Point(0d, 0d);
        Point outside = new Point(100d, 100d);

        assertTrue(EsriJson.ringContainsPoint(ring, center));
        assertFalse(EsriJson.ringContainsPoint(ring, outside));
    }

    @Test
    public void testLineStringsIntersect() throws Exception {
        LineString horizontal = new LineString(
                new Point(-1d, 0d), new Point(1d, 0d)
        );
        LineString parallel = new LineString(
                new Point(-1d, 1d), new Point(1d, 1d)
        );
        LineString overlapping = new LineString(
                new Point(-2d, 0d), new Point(0d, 0d)
        );
        LineString overlapping2 = new LineString(
                new Point(0d, 0d), new Point(-2d, 0d)
        );
        LineString vertical = new LineString(
                new Point(0d, 1d), new Point(0d, -1d)
        );
        LineString parallelVert = new LineString(
                new Point(-1d, 1d), new Point(-1d, 1d)
        );
        LineString overlappingVert = new LineString(
                new Point(0d, 2d), new Point(0d, 0d)
        );
        LineString overlappingVert2 = new LineString(
                new Point(0d, 0d), new Point(0d, 2d)
        );
        LineString ls = new LineString(
                new Point(-10d, 2d), new Point(10d, 2d)
        );
        LineString pointOnLS = new LineString(
                new Point(-8d, 2d), new Point(-8d, 2d)
        );
        LineString pointOffLS = new LineString(
                new Point(-8d, 20d), new Point(-8d, 20d)
        );

        assertTrue(EsriJson.lineStringsIntersect(horizontal, vertical));
        assertTrue(EsriJson.lineStringsIntersect(horizontal, overlapping));
        assertTrue(EsriJson.lineStringsIntersect(horizontal, overlapping2));
        assertFalse(EsriJson.lineStringsIntersect(horizontal, parallel));
        assertFalse(EsriJson.lineStringsIntersect(horizontal, ls));

        assertTrue(EsriJson.lineStringsIntersect(vertical, horizontal));
        assertTrue(EsriJson.lineStringsIntersect(vertical, overlappingVert));
        assertTrue(EsriJson.lineStringsIntersect(horizontal, overlappingVert2));
        assertFalse(EsriJson.lineStringsIntersect(horizontal, parallelVert));
        assertFalse(EsriJson.lineStringsIntersect(vertical, ls));

        assertFalse(EsriJson.lineStringsIntersect(ls, horizontal));
        assertFalse(EsriJson.lineStringsIntersect(ls, vertical));
        assertTrue(EsriJson.lineStringsIntersect(ls, pointOnLS));
        assertTrue(EsriJson.lineStringsIntersect(pointOnLS, ls));
        assertFalse(EsriJson.lineStringsIntersect(ls, pointOffLS));
        assertFalse(EsriJson.lineStringsIntersect(pointOffLS, ls));
    }
}
