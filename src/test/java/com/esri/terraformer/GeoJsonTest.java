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
        Point pt = (Point) BaseGeometry.decodeJson(PointTest.VALID_POINT);
        MultiPoint mpt = (MultiPoint) BaseGeometry.decodeJson(MultiPointTest.VALID_MULTIPOINT);
        LineString ls = (LineString) BaseGeometry.decodeJson(LineStringTest.VALID_LINE_STRING);
        LineString lr = (LineString) BaseGeometry.decodeJson(LineStringTest.LINEAR_RING);
        MultiLineString mls = (MultiLineString) BaseGeometry.decodeJson(MultiLineStringTest.VALID_MULTI_LINE_STRING);
        Polygon pg = (Polygon) BaseGeometry.decodeJson(PolygonTest.VALID_POLYGON);
        MultiPolygon mpg = (MultiPolygon) BaseGeometry.decodeJson(MultiPolygonTest.VALID_MULTI_POLYGON);
        GeometryCollection gc = (GeometryCollection) BaseGeometry.decodeJson(
                GeometryCollectionTest.VALID_GEOMETRY_COLLECTION);
        Feature feat1 = (Feature) BaseGeometry.decodeJson(FeatureTest.MULTILINESTRING_FEATURE);
        Feature feat2 = (Feature) BaseGeometry.decodeJson(FeatureTest.POLYGON_FEATURE);
        Feature feat3 = (Feature) BaseGeometry.decodeJson(FeatureTest.MULTIPOLYGON_FEATURE);
        Feature feat4 = (Feature) BaseGeometry.decodeJson(FeatureTest.GEOMETRYCOLLECTION_FEATURE);
        FeatureCollection fc = (FeatureCollection) BaseGeometry.decodeJson(
                FeatureCollectionTest.VALID_FEATURE_COLLECTION);

        boolean gotException = false;
        try {
            BaseGeometry<?> gj = BaseGeometry.decodeJson(BAD_TYPE);
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.ELEMENT_UNKNOWN_TYPE));
            gotException = true;
        }
        assertTrue(gotException);
    }

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

    @Test
    public void testGetElement() throws Exception {
        boolean gotException = false;
        try {
            BaseGeometry.getElement("", "derp");
        } catch (IllegalArgumentException e) {
            gotException = true;
        }

        assertTrue(gotException);
        gotException = false;

        try {
            BaseGeometry.getElement("[", "derp");
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.NOT_VALID_JSON));
            gotException = true;
        }

        assertTrue(gotException);

        assertNotEquals(null, BaseGeometry.getElement(MultiPointTest.VALID_MULTIPOINT, "derp"));
    }

    @Test
    public void testGetObject() throws Exception {
        boolean gotException = false;
        try {
            BaseGeometry.getObject("", "derp");
        } catch (IllegalArgumentException e) {
            gotException = true;
        }

        assertTrue(gotException);
        gotException = false;

        try {
            BaseGeometry.getObject("[", "derp");
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.NOT_A_JSON_OBJECT));
            gotException = true;
        }

        assertTrue(gotException);

        assertNotEquals(null, BaseGeometry.getObject(MultiPointTest.VALID_MULTIPOINT, "derp"));
    }

    @Test
    public void testObjectFromElement() throws Exception {
        JsonElement arrayElem = BaseGeometry.getElement("[1,2,3,4,5]", "derp");
        JsonElement objElem = BaseGeometry.getElement("{\"derp\":\"herp\"}", "derp");

        assertNotEquals(null, BaseGeometry.arrayFromElement(arrayElem, "derp"));

        boolean gotException = false;
        try {
            BaseGeometry.arrayFromElement(objElem, "derp");
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.ELEMENT_NOT_ARRAY));
            gotException = true;
        }

        assertTrue(gotException);
    }

    @Test
    public void testArrayFromElement() throws Exception {
        JsonElement arrayElem = BaseGeometry.getElement("[1,2,3,4,5]", "derp");
        JsonElement objElem = BaseGeometry.getElement("{\"derp\":\"herp\"}", "derp");

        assertNotEquals(null, BaseGeometry.objectFromElement(objElem, "derp"));

        boolean gotException = false;
        try {
            BaseGeometry.objectFromElement(arrayElem, "derp");
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.ELEMENT_NOT_OBJECT));
            gotException = true;
        }

        assertTrue(gotException);
    }

    @Test
    public void testGetType() throws Exception {
        assertEquals(null, BaseGeometry.getType(null));

        JsonObject typeless = BaseGeometry.getObject("{\"coordinates\":[[100.0,0.0],[101.0,1.0]]}", "derp");
        assertEquals(null, BaseGeometry.getType(typeless));

        JsonObject numericType = BaseGeometry.getObject("{\"type\":5,\"coordinates\":[[100.0,0.0],[101.0,1.0]]}",
                                                        "derp");
        assertEquals(null, BaseGeometry.getType(numericType));

        JsonObject unknownType = BaseGeometry.getObject(
                "{\"type\":\"nknwn\",\"coordinates\":[[100.0,0.0],[101.0,1.0]]}", "derp");
        assertEquals(null, BaseGeometry.getType(unknownType));

        assertEquals(GeometryType.MULTIPOINT, BaseGeometry.getType(
                BaseGeometry.getObject(MultiPointTest.VALID_MULTIPOINT, "derp")));
    }

    @Test
    public void testIsEmpty() throws Exception {
        assertEquals(true, BaseGeometry.isEmpty(null));
        assertEquals(true, BaseGeometry.isEmpty(""));
        assertEquals(false, BaseGeometry.isEmpty("derp"));
    }
}
