package com.esri.terraformer;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class FeatureTest {
    static final String MULTILINESTRING_FEATURE = "{\"type\":\"Feature\",\"geometry\":{\"type\":\"MultiLineString\",\"coordinates\":[[[100.0,0.0],[101.0,1.0]],[[103.0,5.0],[109.0,3.0]]]},\"properties\":{\"innerObject\":{\"herp\":\"derp\",\"nurp\":100.5},\"innerArray\":[1,2,3,4],\"horse\":\"hands\"}}";
    static final String POLYGON_FEATURE = "{\"type\":\"Feature\",\"geometry\":{\"type\":\"Polygon\",\"coordinates\":[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]]},\"properties\":{\"innerObject\":{\"herp\":\"derp\",\"nurp\":100.5},\"innerArray\":[1,2,3,4],\"horse\":\"hands\"}}";
    static final String MULTIPOLYGON_FEATURE = "{\"type\":\"Feature\",\"geometry\":{\"type\":\"MultiPolygon\",\"coordinates\":[[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]],[[[95.0,0.0],[99.0,0.0],[99.0,4.0],[95.0,4.0],[95.0,0.0]],[[96.0,0.5],[98.0,0.5],[98.0,1.0],[96.0,1.0],[96.0,0.5]],[[96.0,3.0],[98.0,3.0],[98.0,3.5],[96.0,3.5],[96.0,3.0]]]]},\"properties\":{\"innerObject\":{\"herp\":\"derp\",\"nurp\":100.5},\"innerArray\":[1,2,3,4],\"horse\":\"hands\"}}";
    static final String GEOMETRYCOLLECTION_FEATURE = "{\"type\":\"Feature\",\"geometry\":{\"type\":\"GeometryCollection\",\"geometries\":[{\"type\":\"Point\",\"coordinates\":[100.0,0.0,90.0,90.0]},{\"type\":\"MultiPoint\",\"coordinates\":[[100.0,0.0],[101.0,1.0]]},{\"type\":\"LineString\",\"coordinates\":[[100.0,0.0],[101.0,1.0]]},{\"type\":\"MultiLineString\",\"coordinates\":[[[100.0,0.0],[101.0,1.0]],[[103.0,5.0],[109.0,3.0]]]},{\"type\":\"Polygon\",\"coordinates\":[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]]},{\"type\":\"MultiPolygon\",\"coordinates\":[[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]],[[[95.0,0.0],[99.0,0.0],[99.0,4.0],[95.0,4.0],[95.0,0.0]],[[96.0,0.5],[98.0,0.5],[98.0,1.0],[96.0,1.0],[96.0,0.5]],[[96.0,3.0],[98.0,3.0],[98.0,3.5],[96.0,3.5],[96.0,3.0]]]]},{\"type\":\"GeometryCollection\",\"geometries\":[{\"type\":\"Point\",\"coordinates\":[100.0,0.0,90.0,90.0]},{\"type\":\"MultiPoint\",\"coordinates\":[[100.0,0.0],[101.0,1.0]]},{\"type\":\"LineString\",\"coordinates\":[[100.0,0.0],[101.0,1.0]]},{\"type\":\"MultiLineString\",\"coordinates\":[[[100.0,0.0],[101.0,1.0]],[[103.0,5.0],[109.0,3.0]]]},{\"type\":\"Polygon\",\"coordinates\":[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]]},{\"type\":\"MultiPolygon\",\"coordinates\":[[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]],[[[95.0,0.0],[99.0,0.0],[99.0,4.0],[95.0,4.0],[95.0,0.0]],[[96.0,0.5],[98.0,0.5],[98.0,1.0],[96.0,1.0],[96.0,0.5]],[[96.0,3.0],[98.0,3.0],[98.0,3.5],[96.0,3.5],[96.0,3.0]]]]}]}]},\"properties\":{\"innerObject\":{\"herp\":\"derp\",\"nurp\":100.5},\"innerArray\":[1,2,3,4],\"horse\":\"hands\"}}";
    static final String VALID_DIFF_ORDER = "{\"type\":\"Feature\",\"geometry\":{\"type\":\"GeometryCollection\",\"geometries\":[{\"type\":\"MultiPolygon\",\"coordinates\":[[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]],[[[95.0,0.0],[99.0,0.0],[99.0,4.0],[95.0,4.0],[95.0,0.0]],[[96.0,0.5],[98.0,0.5],[98.0,1.0],[96.0,1.0],[96.0,0.5]],[[96.0,3.0],[98.0,3.0],[98.0,3.5],[96.0,3.5],[96.0,3.0]]]]},{\"type\":\"Point\",\"coordinates\":[100.0,0.0,90.0,90.0]},{\"type\":\"MultiPoint\",\"coordinates\":[[100.0,0.0],[101.0,1.0]]},{\"type\":\"MultiLineString\",\"coordinates\":[[[100.0,0.0],[101.0,1.0]],[[103.0,5.0],[109.0,3.0]]]},{\"type\":\"GeometryCollection\",\"geometries\":[{\"type\":\"Point\",\"coordinates\":[100.0,0.0,90.0,90.0]},{\"type\":\"MultiPoint\",\"coordinates\":[[100.0,0.0],[101.0,1.0]]},{\"type\":\"LineString\",\"coordinates\":[[100.0,0.0],[101.0,1.0]]},{\"type\":\"MultiLineString\",\"coordinates\":[[[100.0,0.0],[101.0,1.0]],[[103.0,5.0],[109.0,3.0]]]},{\"type\":\"Polygon\",\"coordinates\":[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]]},{\"type\":\"MultiPolygon\",\"coordinates\":[[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]],[[[95.0,0.0],[99.0,0.0],[99.0,4.0],[95.0,4.0],[95.0,0.0]],[[96.0,0.5],[98.0,0.5],[98.0,1.0],[96.0,1.0],[96.0,0.5]],[[96.0,3.0],[98.0,3.0],[98.0,3.5],[96.0,3.5],[96.0,3.0]]]]}]},{\"type\":\"Polygon\",\"coordinates\":[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]]},{\"type\":\"LineString\",\"coordinates\":[[100.0,0.0],[101.0,1.0]]}]},\"properties\":{\"innerObject\":{\"herp\":\"derp\",\"nurp\":100.5},\"innerArray\":[1,2,3,4],\"horse\":\"hands\"}}";
    static final String EMPTY_GEOMETRY = "{\"type\":\"Feature\",\"geometry\":{}}";
    static final String WRONG_TYPE = "{\"type\":\"GeometryCollection\",\"geometry\":{\"type\":\"GeometryCollection\",\"geometries\":[{\"type\":\"MultiPolygon\",\"coordinates\":[[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]],[[[95.0,0.0],[99.0,0.0],[99.0,4.0],[95.0,4.0],[95.0,0.0]],[[96.0,0.5],[98.0,0.5],[98.0,1.0],[96.0,1.0],[96.0,0.5]],[[96.0,3.0],[98.0,3.0],[98.0,3.5],[96.0,3.5],[96.0,3.0]]]]},{\"type\":\"Point\",\"coordinates\":[100.0,0.0,90.0,90.0]},{\"type\":\"MultiPoint\",\"coordinates\":[[100.0,0.0],[101.0,1.0]]},{\"type\":\"MultiLineString\",\"coordinates\":[[[100.0,0.0],[101.0,1.0]],[[103.0,5.0],[109.0,3.0]]]},{\"type\":\"GeometryCollection\",\"geometries\":[{\"type\":\"Point\",\"coordinates\":[100.0,0.0,90.0,90.0]},{\"type\":\"MultiPoint\",\"coordinates\":[[100.0,0.0],[101.0,1.0]]},{\"type\":\"LineString\",\"coordinates\":[[100.0,0.0],[101.0,1.0]]},{\"type\":\"MultiLineString\",\"coordinates\":[[[100.0,0.0],[101.0,1.0]],[[103.0,5.0],[109.0,3.0]]]},{\"type\":\"Polygon\",\"coordinates\":[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]]},{\"type\":\"MultiPolygon\",\"coordinates\":[[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]],[[[95.0,0.0],[99.0,0.0],[99.0,4.0],[95.0,4.0],[95.0,0.0]],[[96.0,0.5],[98.0,0.5],[98.0,1.0],[96.0,1.0],[96.0,0.5]],[[96.0,3.0],[98.0,3.0],[98.0,3.5],[96.0,3.5],[96.0,3.0]]]]}]},{\"type\":\"Polygon\",\"coordinates\":[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]]},{\"type\":\"LineString\",\"coordinates\":[[100.0,0.0],[101.0,1.0]]}]},\"properties\":{\"innerObject\":{\"herp\":\"derp\",\"nurp\":100.5},\"innerArray\":[1,2,3,4],\"horse\":\"hands\"}}";
    static final String NO_TYPE = "{\"geometry\":{\"type\":\"GeometryCollection\",\"geometries\":[{\"type\":\"MultiPolygon\",\"coordinates\":[[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]],[[[95.0,0.0],[99.0,0.0],[99.0,4.0],[95.0,4.0],[95.0,0.0]],[[96.0,0.5],[98.0,0.5],[98.0,1.0],[96.0,1.0],[96.0,0.5]],[[96.0,3.0],[98.0,3.0],[98.0,3.5],[96.0,3.5],[96.0,3.0]]]]},{\"type\":\"Point\",\"coordinates\":[100.0,0.0,90.0,90.0]},{\"type\":\"MultiPoint\",\"coordinates\":[[100.0,0.0],[101.0,1.0]]},{\"type\":\"MultiLineString\",\"coordinates\":[[[100.0,0.0],[101.0,1.0]],[[103.0,5.0],[109.0,3.0]]]},{\"type\":\"GeometryCollection\",\"geometries\":[{\"type\":\"Point\",\"coordinates\":[100.0,0.0,90.0,90.0]},{\"type\":\"MultiPoint\",\"coordinates\":[[100.0,0.0],[101.0,1.0]]},{\"type\":\"LineString\",\"coordinates\":[[100.0,0.0],[101.0,1.0]]},{\"type\":\"MultiLineString\",\"coordinates\":[[[100.0,0.0],[101.0,1.0]],[[103.0,5.0],[109.0,3.0]]]},{\"type\":\"Polygon\",\"coordinates\":[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]]},{\"type\":\"MultiPolygon\",\"coordinates\":[[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]],[[[95.0,0.0],[99.0,0.0],[99.0,4.0],[95.0,4.0],[95.0,0.0]],[[96.0,0.5],[98.0,0.5],[98.0,1.0],[96.0,1.0],[96.0,0.5]],[[96.0,3.0],[98.0,3.0],[98.0,3.5],[96.0,3.5],[96.0,3.0]]]]}]},{\"type\":\"Polygon\",\"coordinates\":[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]]},{\"type\":\"LineString\",\"coordinates\":[[100.0,0.0],[101.0,1.0]]}]},\"properties\":{\"innerObject\":{\"herp\":\"derp\",\"nurp\":100.5},\"innerArray\":[1,2,3,4],\"horse\":\"hands\"}}";
    static final String NOT_AN_OBJECT = "[\"type\",\"geometry\"]";
    // one of the linestrings inside the polygon, inside the geometry collection, is not a linear ring
    static final String INVALID_INNER_TYPE = "{\"type\":\"Feature\",\"geometry\":{\"type\":\"GeometryCollection\",\"geometries\":[{\"type\":\"MultiPolygon\",\"coordinates\":[[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]],[[[95.0,0.0],[99.0,0.0],[99.0,4.0],[95.0,4.0],[95.0,0.0]],[[96.0,0.5],[98.0,0.5],[98.0,1.0],[96.0,1.0],[96.0,0.5]],[[96.0,3.0],[98.0,3.0],[98.0,3.5],[96.0,3.5],[96.0,3.0]]]]},{\"type\":\"Point\",\"coordinates\":[100.0,0.0,90.0,90.0]},{\"type\":\"MultiPoint\",\"coordinates\":[[100.0,0.0],[101.0,1.0]]},{\"type\":\"MultiLineString\",\"coordinates\":[[[100.0,0.0],[101.0,1.0]],[[103.0,5.0],[109.0,3.0]]]},{\"type\":\"GeometryCollection\",\"geometries\":[{\"type\":\"Point\",\"coordinates\":[100.0,0.0,90.0,90.0]},{\"type\":\"MultiPoint\",\"coordinates\":[[100.0,0.0],[101.0,1.0]]},{\"type\":\"LineString\",\"coordinates\":[[100.0,0.0],[101.0,1.0]]},{\"type\":\"MultiLineString\",\"coordinates\":[[[100.0,0.0],[101.0,1.0]],[[103.0,5.0],[109.0,3.0]]]},{\"type\":\"Polygon\",\"coordinates\":[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]]},{\"type\":\"MultiPolygon\",\"coordinates\":[[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]],[[[95.0,0.0],[99.0,0.0],[99.0,4.0],[95.0,4.0],[95.0,0.0]],[[96.0,0.5],[98.0,0.5],[98.0,1.0],[96.0,1.0],[96.0,0.5]],[[96.0,3.0],[98.0,3.0],[98.0,3.5],[96.0,3.5],[96.0,3.0]]]]}]},{\"type\":\"Polygon\",\"coordinates\":[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]]},{\"type\":\"LineString\",\"coordinates\":[[100.0,0.0],[101.0,1.0]]}]},\"properties\":{\"innerObject\":{\"herp\":\"derp\",\"nurp\":100.5},\"innerArray\":[1,2,3,4],\"horse\":\"hands\"}}";
    static final String NO_GEOMETRY = "{\"type\":\"Feature\"}";
    static final String GEOMETRY_NOT_OBJECT = "{\"type\":\"Feature\",\"geometry\":100.0}";
    static final String PROPERTIES_NOT_OBJECT = "{\"type\":\"Feature\",\"geometry\":{\"type\":\"MultiPolygon\",\"coordinates\":[[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]],[[[95.0,0.0],[99.0,0.0],[99.0,4.0],[95.0,4.0],[95.0,0.0]],[[96.0,0.5],[98.0,0.5],[98.0,1.0],[96.0,1.0],[96.0,0.5]],[[96.0,3.0],[98.0,3.0],[98.0,3.5],[96.0,3.5],[96.0,3.0]]]]},\"properties\":100}";

    @Test
    public void testAdd() throws Exception {
        Feature feat = new Feature();
        assertEquals(0, feat.size());
        Geometry mp = MultiPointTest.getMultiPoint();
        feat.add(mp);
        assertEquals(1, feat.size());
        assertEquals(mp, feat.get());
        Geometry pg = PolygonTest.getPolygon();
        feat.add(pg);
        assertEquals(1, feat.size());
        assertEquals(pg, feat.get());
    }

    @Test
    public void testAddAll() throws Exception {
        Feature feat = new Feature();
        assertEquals(0, feat.size());
        ArrayList<Geometry<?>> geos = new ArrayList<Geometry<?>>();
        Geometry pg = PolygonTest.getPolygon();
        Geometry mp = MultiPointTest.getMultiPoint();
        geos.add(mp);
        geos.add(pg);
        feat.addAll(geos);
        assertEquals(1, feat.size());
        assertEquals(mp, feat.get());
        Collections.reverse(geos);
        feat.addAll(geos);
        assertEquals(1, feat.size());
        assertEquals(pg, feat.get());

        // now with starting index
        Collections.reverse(geos);
        feat.clear();
        assertEquals(0, feat.size());
        feat.addAll(15, geos);
        assertEquals(1, feat.size());
        assertEquals(mp, feat.get(1));
        Collections.reverse(geos);
        feat.addAll(-36, geos);
        assertEquals(1, feat.size());
        assertEquals(pg, feat.get(-1));
    }

    @Test
    public void testSet() throws Exception {
        Feature feat = new Feature();
        assertEquals(0, feat.size());
        Geometry mp = MultiPointTest.getMultiPoint();
        feat.set(mp);
        assertEquals(1, feat.size());
        assertEquals(mp, feat.get());
        Geometry pg = PolygonTest.getPolygon();
        feat.set(pg);
        assertEquals(1, feat.size());
        assertEquals(pg, feat.get());

        // now with index
        feat.clear();
        assertEquals(0, feat.size());
        feat.set(9, mp);
        assertEquals(1, feat.size());
        assertEquals(mp, feat.get(100));
        feat.set(-14, pg);
        assertEquals(1, feat.size());
        assertEquals(pg, feat.get(0));
    }

    @Test
    public void testRemove() throws Exception {
        Feature feat = new Feature();
        assertNull(feat.remove());
        Geometry pg = PolygonTest.getPolygon();
        Geometry mp = MultiPointTest.getMultiPoint();
        feat.add(mp);
        assertEquals(mp, feat.remove());
        assertEquals(0, feat.size());
        feat.add(pg);
        assertEquals(pg, feat.remove());

        // now with index
        feat.clear();
        assertNull(feat.remove(0));
        feat.add(mp);
        assertEquals(mp, feat.remove(-1));
        assertEquals(0, feat.size());
        feat.add(pg);
        assertEquals(pg, feat.remove(1));
    }

    @Test
    public void testRemoveRange() throws Exception {
        Feature feat = new Feature();
        feat.removeRange(0, 123);
        Geometry mp = MultiPointTest.getMultiPoint();
        feat.add(mp);
        feat.removeRange(-99, 146);
        assertEquals(0, feat.size());
    }

    @Test
    public void testSubList() throws Exception {
        Feature feat = new Feature();
        assertEquals(feat, feat.subList(-1, 27836));
        Geometry mp = MultiPointTest.getMultiPoint();
        Geometry pg = PolygonTest.getPolygon();
        feat.add(mp);
        feat.add(pg);
        List<Geometry<?>> geos = feat.subList(0, 2);
        assertEquals(1, feat.size());
        assertEquals(1, geos.size());
        assertEquals(pg, geos.get(0));
    }

    @Test
    public void testGetSetProperties() throws Exception {
        Feature feat = new Feature();
        assertNull(feat.getProperties());
        JsonObject props = getProperties();
        feat.setProperties(props);
        assertEquals(props, feat.getProperties());
        assertTrue(feat.getProperties().equals(getProperties()));
    }

    @Test
    public void testGetType() throws Exception {
        assertEquals(GeometryType.FEATURE, getMultiLineStringFeature().getType());
        assertEquals(GeometryType.FEATURE, getPolygonFeature().getType());
        assertEquals(GeometryType.FEATURE, getMultiPolygonFeature().getType());
        assertEquals(GeometryType.FEATURE, getGeometryCollectionFeature().getType());
        assertEquals(GeometryType.FEATURE, new Feature().getType());
    }

    @Test
    public void testToJson() throws Exception {
        assertEquals(MULTILINESTRING_FEATURE, getMultiLineStringFeature().toJson());
        assertEquals(POLYGON_FEATURE, getPolygonFeature().toJson());
        assertEquals(MULTIPOLYGON_FEATURE, getMultiPolygonFeature().toJson());
        assertEquals(GEOMETRYCOLLECTION_FEATURE, getGeometryCollectionFeature().toJson());
        assertEquals(GEOMETRYCOLLECTION_FEATURE, new Feature(GeometryCollectionTest.getGeometryCollection(),
                getProperties()).toJson());
    }

    @Test
    public void testToJsonObject() throws Exception {
        JsonObject obj1 = getGeometryCollectionFeature().toJsonObject(null);
        JsonObject obj2 = getGeometryCollectionFeature().toJsonObject(new Gson());
        assertEquals(obj1.toString(), obj2.toString());
    }

    @Test
    public void testIsValid() throws Exception {
        assertTrue(Feature.decodeFeature(MULTILINESTRING_FEATURE).isValid());
        assertTrue(Feature.decodeFeature(POLYGON_FEATURE).isValid());
        assertTrue(Feature.decodeFeature(MULTIPOLYGON_FEATURE).isValid());
        assertTrue(Feature.decodeFeature(GEOMETRYCOLLECTION_FEATURE).isValid());
        assertTrue(Feature.decodeFeature(EMPTY_GEOMETRY).isValid());
        // creating a feature from some element of a GeometryCollection
        assertTrue(new Feature(GeometryCollectionTest.getGeometryCollection().get(3), getProperties()).isValid());
        // geometry collection can have empty geometries
        assertTrue(new Feature().isValid());

        // invalid multipolygon
        assertFalse(new Feature(new MultiPolygon(new Polygon(new LineString(
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
        )))).isValid());
        // null geometry
        assertFalse(new Feature(null).isValid());
    }

    @Test
    public void testIsEquivalentTo() throws Exception {
        Feature feat = Feature.decodeFeature(GEOMETRYCOLLECTION_FEATURE);
        Feature otherFeat = getGeometryCollectionFeature();
        Feature anotherFeat = Feature.decodeFeature(VALID_DIFF_ORDER);

        assertTrue(feat.isEquivalentTo(otherFeat));
        assertTrue(feat.isEquivalentTo(anotherFeat));
        assertTrue(otherFeat.isEquivalentTo(feat));
        assertTrue(otherFeat.isEquivalentTo(anotherFeat));
        assertTrue(anotherFeat.isEquivalentTo(feat));
        assertTrue(anotherFeat.isEquivalentTo(otherFeat));
        // rotated linear ring, also no properties
        assertTrue(feat.isEquivalentTo(new Feature(new GeometryCollection(
                PointTest.getPoint(),
                MultiPointTest.getMultiPoint(),
                LineStringTest.getLineString(),
                MultiLineStringTest.getMultiLineString(),
                PolygonTest.getPolygon(),
                new MultiPolygon(new Polygon(
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
                )),
                GeometryCollectionTest.geomCollWithoutGeomColl()
        ))));

        assertFalse(feat.isEquivalentTo(GeometryCollectionTest.getGeometryCollection()));
        assertFalse(feat.isEquivalentTo(new Feature()));
        // missing the linestring
        assertFalse(feat.isEquivalentTo(new GeometryCollection(
                PointTest.getPoint(),
                MultiPointTest.getMultiPoint(),
                MultiLineStringTest.getMultiLineString(),
                PolygonTest.getPolygon(),
                MultiPolygonTest.getMultiPolygon(),
                GeometryCollectionTest.geomCollWithoutGeomColl()
        )));
    }

    @Test
    public void testDecodeFeature() throws Exception {
        assertEquals(MULTILINESTRING_FEATURE, Feature.decodeFeature(MULTILINESTRING_FEATURE).toJson());
        assertEquals(POLYGON_FEATURE, Feature.decodeFeature(POLYGON_FEATURE).toJson());
        assertEquals(MULTIPOLYGON_FEATURE, Feature.decodeFeature(MULTIPOLYGON_FEATURE).toJson());
        assertEquals(GEOMETRYCOLLECTION_FEATURE, Feature.decodeFeature(GEOMETRYCOLLECTION_FEATURE).toJson());
        assertEquals(EMPTY_GEOMETRY, Feature.decodeFeature(EMPTY_GEOMETRY).toJson());

        boolean gotException = false;

        try {
            Feature.decodeFeature(WRONG_TYPE);
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.NOT_OF_TYPE));
            gotException = true;
        }

        assertTrue(gotException);
        gotException = false;

        try {
            Feature.decodeFeature(NO_TYPE);
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.NOT_OF_TYPE));
            gotException = true;
        }

        assertTrue(gotException);
        gotException = false;

        try {
            Feature.decodeFeature(NOT_AN_OBJECT);
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.NOT_A_JSON_OBJECT));
            gotException = true;
        }

        assertTrue(gotException);
        gotException = false;

        try {
            Feature.decodeFeature(INVALID_INNER_TYPE);
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.INNER_LINESTRING_NOT_RING));
            gotException = true;
        }

        assertTrue(gotException);
        gotException = false;

        try {
            Feature.decodeFeature(NO_GEOMETRY);
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.GEOMETRY_KEY_NOT_FOUND));
            gotException = true;
        }

        assertTrue(gotException);
        gotException = false;

        try {
            Feature.decodeFeature(GEOMETRY_NOT_OBJECT);
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.ELEMENT_NOT_OBJECT));

            gotException = true;
        }

        assertTrue(gotException);
        gotException = false;

        try {
            Feature.decodeFeature(PROPERTIES_NOT_OBJECT);
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.PROPERTIES_NOT_OBJECT));

            gotException = true;
        }

        assertTrue(gotException);
    }

    static Feature getMultiLineStringFeature() {
        return new Feature(MultiLineStringTest.getMultiLineString(), getProperties());
    }

    static Feature getPolygonFeature() {
        return new Feature(PolygonTest.getPolygon(), getProperties());
    }

    static Feature getMultiPolygonFeature() {
        return new Feature(MultiPolygonTest.getMultiPolygon(), getProperties());
    }

    static Feature getGeometryCollectionFeature() {
        return new Feature(GeometryCollectionTest.getGeometryCollection(), getProperties());
    }

    static JsonObject getProperties() {
        JsonObject props = new JsonObject();
        JsonObject innerObj = new JsonObject();
        JsonArray innerArray = new JsonArray();

        innerObj.add("herp", new JsonPrimitive("derp"));
        innerObj.add("nurp", new JsonPrimitive(100.5));
        props.add("innerObject", innerObj);

        innerArray.add(new JsonPrimitive(1));
        innerArray.add(new JsonPrimitive(2));
        innerArray.add(new JsonPrimitive(3));
        innerArray.add(new JsonPrimitive(4));
        props.add("innerArray", innerArray);

        props.add("horse", new JsonPrimitive("hands"));
        return props;
    }

    @Test
    public void testFeatureFromObjectElement() throws Exception {
        Feature ft1 = Feature.featureFromObjectElement(BaseGeometry.getElement(MULTILINESTRING_FEATURE, "derp"), "derp");
        Feature ft2 = Feature.featureFromObjectElement(BaseGeometry.getElement(POLYGON_FEATURE, "derp"), "derp");
        Feature ft3 = Feature.featureFromObjectElement(BaseGeometry.getElement(MULTIPOLYGON_FEATURE, "derp"), "derp");
        Feature ft4 = Feature.featureFromObjectElement(BaseGeometry.getElement(GEOMETRYCOLLECTION_FEATURE, "derp"), "derp");

        boolean gotException = false;

        try {
            Feature ft5 = Feature.featureFromObjectElement(
                    BaseGeometry.getElement(MultiPolygonTest.VALID_MULTI_POLYGON, "derp"), "derp");
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.ELEMENT_NOT_FEATURE));
            gotException = true;
        }

        assertTrue(gotException);
        gotException = false;

        try {
            Feature ft6 = Feature.featureFromObjectElement(
                    BaseGeometry.getElement(GeometryCollectionTest.VALID_GEOMETRY_COLLECTION, "derp"), "derp");
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.ELEMENT_NOT_FEATURE));
            gotException = true;
        }

        assertTrue(gotException);
    }
}
