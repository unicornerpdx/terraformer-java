package com.esri.terraformer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class GeoJsonTest {
    static final String BAD_TYPE = "{\"type\":\"WubbaLubbaDingDong\",\"coordinates\":[[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]],[[[95.0,0.0],[99.0,0.0],[99.0,4.0],[95.0,4.0],[95.0,0.0]],[[96.0,0.5],[98.0,0.5],[98.0,1.0],[96.0,1.0],[96.0,0.5]],[[96.0,3.0],[98.0,3.0],[98.0,3.5],[96.0,3.5],[96.0,3.0]]]]}";

    @Test
    public void testDecodeJson() throws Exception {
        // we are mostly verifying that the function discovers the type successfully
        Point pt = (Point) GeoJson.decodeJson(PointTest.VALID_POINT);
        MultiPoint mpt = (MultiPoint) GeoJson.decodeJson(MultiPointTest.VALID_MULTIPOINT);
        LineString ls = (LineString) GeoJson.decodeJson(LineStringTest.VALID_LINE_STRING);
        LineString lr = (LineString) GeoJson.decodeJson(LineStringTest.LINEAR_RING);
        MultiLineString mls = (MultiLineString) GeoJson.decodeJson(MultiLineStringTest.VALID_MULTI_LINE_STRING);
        Polygon pg = (Polygon) GeoJson.decodeJson(PolygonTest.VALID_POLYGON);
        MultiPolygon mpg = (MultiPolygon) GeoJson.decodeJson(MultiPolygonTest.VALID_MULTI_POLYGON);
        GeometryCollection gc = (GeometryCollection) GeoJson.decodeJson(GeometryCollectionTest.VALID_GEOMETRY_COLLECTION);
        Feature feat1 = (Feature) GeoJson.decodeJson(FeatureTest.MULTILINESTRING_FEATURE);
        Feature feat2 = (Feature) GeoJson.decodeJson(FeatureTest.POLYGON_FEATURE);
        Feature feat3 = (Feature) GeoJson.decodeJson(FeatureTest.MULTIPOLYGON_FEATURE);
        Feature feat4 = (Feature) GeoJson.decodeJson(FeatureTest.GEOMETRYCOLLECTION_FEATURE);
        FeatureCollection fc = (FeatureCollection) GeoJson.decodeJson(FeatureCollectionTest.VALID_FEATURE_COLLECTION);

        boolean gotException = false;
        try {
            GeoJson<?> gj = GeoJson.decodeJson(BAD_TYPE);
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.ELEMENT_UNKNOWN_TYPE));
            gotException = true;
        }
        assertTrue(gotException);
    }

    @Test
    public void testNaiveEquals() throws Exception {
        GeoJson<Double> pt = new Point(100d, 0d);
        GeoJson<Double> pt2 = new Point(100d, 0d, 3d);
        GeoJson<Double> pt3 = new Point(0d, 100d);
        GeoJson<Double> pt4 = new Point(100d, 0d);  // same as pt
        GeoJson<Point> mp = new MultiPoint((Point)pt, (Point)pt);
        GeoJson<Point> mp2 = new MultiPoint((Point)pt, (Point)pt3);
        GeoJson<Point> mp3 = new MultiPoint((Point)pt3, (Point)pt);

        assertEquals(false, GeoJson.naiveEquals(null, null));
        assertEquals(false, GeoJson.naiveEquals(null, pt));
        assertEquals(false, GeoJson.naiveEquals(pt, null));
        assertEquals(false, GeoJson.naiveEquals(pt, mp));
        assertEquals(false, GeoJson.naiveEquals(pt, pt2));
        assertEquals(true, GeoJson.naiveEquals(pt, pt));
        assertEquals(true, GeoJson.naiveEquals(pt, pt4));
        assertEquals(null, GeoJson.naiveEquals(mp2, mp3));
    }

    @Test
    public void testGetElement() throws Exception {
        boolean gotException = false;
        try {
            GeoJson.getElement("", "derp");
        } catch (IllegalArgumentException e) {
            gotException = true;
        }

        assertTrue(gotException);
        gotException = false;

        try {
            GeoJson.getElement("[", "derp");
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.NOT_VALID_JSON));
            gotException = true;
        }

        assertTrue(gotException);

        assertNotEquals(null, GeoJson.getElement(MultiPointTest.VALID_MULTIPOINT, "derp"));
    }

    @Test
    public void testGetObject() throws Exception {
        boolean gotException = false;
        try {
            GeoJson.getObject("", "derp");
        } catch (IllegalArgumentException e) {
            gotException = true;
        }

        assertTrue(gotException);
        gotException = false;

        try {
            GeoJson.getObject("[", "derp");
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.NOT_A_JSON_OBJECT));
            gotException = true;
        }

        assertTrue(gotException);

        assertNotEquals(null, GeoJson.getObject(MultiPointTest.VALID_MULTIPOINT, "derp"));
    }

    @Test
    public void testObjectFromElement() throws Exception {
        JsonElement arrayElem = GeoJson.getElement("[1,2,3,4,5]", "derp");
        JsonElement objElem = GeoJson.getElement("{\"derp\":\"herp\"}", "derp");

        assertNotEquals(null, GeoJson.arrayFromElement(arrayElem, "derp"));

        boolean gotException = false;
        try {
            GeoJson.arrayFromElement(objElem, "derp");
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.ELEMENT_NOT_ARRAY));
            gotException = true;
        }

        assertTrue(gotException);
    }

    @Test
    public void testArrayFromElement() throws Exception {
        JsonElement arrayElem = GeoJson.getElement("[1,2,3,4,5]", "derp");
        JsonElement objElem = GeoJson.getElement("{\"derp\":\"herp\"}", "derp");

        assertNotEquals(null, GeoJson.objectFromElement(objElem, "derp"));

        boolean gotException = false;
        try {
            GeoJson.objectFromElement(arrayElem, "derp");
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.ELEMENT_NOT_OBJECT));
            gotException = true;
        }

        assertTrue(gotException);
    }

    @Test
    public void testGetType() throws Exception {
        assertEquals(null, GeoJson.getType(null));

        JsonObject typeless = GeoJson.getObject("{\"coordinates\":[[100.0,0.0],[101.0,1.0]]}", "derp");
        assertEquals(null, GeoJson.getType(typeless));

        JsonObject numericType = GeoJson.getObject("{\"type\":5,\"coordinates\":[[100.0,0.0],[101.0,1.0]]}",
                "derp");
        assertEquals(null, GeoJson.getType(numericType));

        JsonObject unknownType = GeoJson.getObject("{\"type\":\"nknwn\",\"coordinates\":[[100.0,0.0],[101.0,1.0]]}",
                "derp");
        assertEquals(null, GeoJson.getType(unknownType));

        assertEquals(GeoJsonType.MULTIPOINT, GeoJson.getType(GeoJson.getObject(MultiPointTest.VALID_MULTIPOINT,
                "derp")));
    }

    @Test
    public void testIsEmpty() throws Exception {
        assertEquals(true, GeoJson.isEmpty(null));
        assertEquals(true, GeoJson.isEmpty(""));
        assertEquals(false, GeoJson.isEmpty("derp"));
    }
}
