package com.esri.terraformer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class GeoJsonTest {
    private static final String MULTIPOINT = "{\"type\":\"MultiPoint\",\"coordinates\":[[100.0,0.0],[101.0,1.0]]}";

    @Test
    public void testDecodeJson() throws Exception {
        // TODO once all the geometries are written
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
            gotException = true;
        }

        assertTrue(gotException);

        JsonElement elem = null;
        try {
            elem = GeoJson.getElement(MULTIPOINT, "derp");
        } catch (TerraformerException e) {
            fail(e.getMessage());
        }
        assertNotEquals(null, elem);
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
            gotException = true;
        }

        assertTrue(gotException);

        JsonObject obj = null;
        try {
            obj = GeoJson.getObject(MULTIPOINT, "derp");
        } catch (TerraformerException e) {
            fail(e.getMessage());
        }
        assertNotEquals(null, obj);
    }

    @Test
    public void testObjectFromElement() throws Exception {
        JsonElement arrayElem = null;
        JsonElement objElem = null;
        try {
            arrayElem = GeoJson.getElement("[1,2,3,4,5]", "derp");
            objElem = GeoJson.getElement("{\"derp\":\"herp\"}", "derp");
        } catch (TerraformerException e) {
            fail(e.getMessage());
        }

        try {
            JsonArray array = GeoJson.arrayFromElement(arrayElem, "derp");
            assertNotEquals(null, array);
        } catch (TerraformerException e) {
            fail(e.getMessage());
        }

        boolean gotException = false;
        try {
            GeoJson.arrayFromElement(objElem, "derp");
        } catch (TerraformerException e) {
            gotException = true;
        }

        assertTrue(gotException);
    }

    @Test
    public void testArrayFromElement() throws Exception {
        JsonElement arrayElem = null;
        JsonElement objElem = null;
        try {
            arrayElem = GeoJson.getElement("[1,2,3,4,5]", "derp");
            objElem = GeoJson.getElement("{\"derp\":\"herp\"}", "derp");
        } catch (TerraformerException e) {
            fail(e.getMessage());
        }

        try {
            JsonObject obj = GeoJson.objectFromElement(objElem, "derp");
            assertNotEquals(null, obj);
        } catch (TerraformerException e) {
            fail(e.getMessage());
        }

        boolean gotException = false;
        try {
            GeoJson.objectFromElement(arrayElem, "derp");
        } catch (TerraformerException e) {
            gotException = true;
        }

        assertTrue(gotException);
    }

    @Test
    public void testGetType() throws Exception {
        assertEquals(null, GeoJson.getType(null));

        try {
            JsonObject typeless = GeoJson.getObject("{\"coordinates\":[[100.0,0.0],[101.0,1.0]]}", "derp");
            assertEquals(null, GeoJson.getType(typeless));
        } catch (TerraformerException e) {
            fail(e.getMessage());
        }

        try {
            JsonObject numericType = GeoJson.getObject("{\"type\":5,\"coordinates\":[[100.0,0.0],[101.0,1.0]]}",
                    "derp");
            assertEquals(null, GeoJson.getType(numericType));
        } catch (TerraformerException e) {
            fail(e.getMessage());
        }

        try {
            JsonObject unknownType = GeoJson.getObject("{\"type\":\"nknwn\",\"coordinates\":[[100.0,0.0],[101.0,1.0]]}",
                    "derp");
            assertEquals(null, GeoJson.getType(unknownType));
        } catch (TerraformerException e) {
            fail(e.getMessage());
        }

        try {
            assertEquals(GeoJsonType.MULTIPOINT, GeoJson.getType(GeoJson.getObject(MULTIPOINT, "derp")));
        } catch (TerraformerException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testIsEmpty() throws Exception {
        assertEquals(true, GeoJson.isEmpty(null));
        assertEquals(true, GeoJson.isEmpty(""));
        assertEquals(false, GeoJson.isEmpty("derp"));
    }

    @Test
    public void testGeoJsonFromObjectElement() throws Exception {
        // TODO once all the geometries are written
    }
}
