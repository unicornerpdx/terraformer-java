package com.esri.terraformer;

import com.google.gson.JsonPrimitive;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EsriJsonTest {
    EsriJson e;

    @BeforeClass
    public static void setUpTerraformer() {
        Terraformer.setEncoder(new EsriJson());
        Terraformer.setDecoder(new EsriJson());
    }

    @Before
    public void setUp() {
        e = new EsriJson();
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
        assertEquals(p.getHoles().size(), 1);

        Polygon p3D = (Polygon) e.decode("{\"hasZ\":true,\"hasM\":true,\"rings\":[[[1,1,1,1],[2,2,2,2],[3,3,3,3],[4,4,4,4],[1,1,1,1]]]}");
        assertTrue(p3D.getOuterRing().isLinearRing());
        assertEquals(p3D.getOuterRing().size(), 5);
        assertEquals(p3D.getHoles().size(), 0);
        assertEquals(p3D.getOuterRing().get(0), new Point(1d, 1d, 1d, 1d));
        assertEquals(p3D.getOuterRing().get(1), new Point(2d, 2d, 2d, 2d));
        assertEquals(p3D.getOuterRing().get(2), new Point(3d,3d,3d,3d));
        assertEquals(p3D.getOuterRing().get(3), new Point(4d,4d,4d,4d));
        assertEquals(p3D.getOuterRing().get(4), new Point(1d,1d,1d,1d));

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

    }

    @Test
    public void testEncodeMultiLineString() throws Exception {

    }

    @Test
    public void testEncodePolygon() throws Exception {

    }

    @Test
    public void testEncodeMultiPolygon() throws Exception {

    }

    @Test
    public void testEncodeGeometryCollection() throws Exception {

    }

    @Test
    public void testEncodeFeature() throws Exception {

    }

    @Test
    public void testEncodeFeatureCollection() throws Exception {

    }
}
