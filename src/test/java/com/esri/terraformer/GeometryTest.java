package com.esri.terraformer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.junit.Test;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class GeometryTest {
    @Test
    public void testGetCoordinates() throws Exception {
        boolean gotException = false;

        try {
            Geometry.getCoordinates(GeoJson.getObject(MultiPointTest.NO_COORDINATES, "derp"), "derp");
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.COORDINATES_KEY_NOT_FOUND));
            gotException = true;
        }

        assertTrue(gotException);

        JsonObject obj = GeoJson.getObject(MultiPointTest.VALID_MULTIPOINT, "derp");
        assertNotEquals(null, Geometry.getCoordinates(obj, "derp"));
    }

    @Test
    public void testGetCoordinateArray() throws Exception {
        boolean gotException = false;

        try {
            Geometry.getCoordinateArray(Geometry.getCoordinates(GeoJson.getObject(MultiPointTest.COORDS_TOO_SHORT,
                    "derp"), "derp"), 2, "derp");
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.COORDINATE_ARRAY_TOO_SHORT));
            gotException = true;
        }

        assertTrue(gotException);

        JsonElement elem = Geometry.getCoordinates(GeoJson.getObject(MultiPointTest.VALID_MULTIPOINT, "derp"), "derp");
        assertNotEquals(null, Geometry.getCoordinateArray(elem, 2, "derp"));
    }

    @Test
    public void testGeometryFromObjectElement() throws Exception {
        Point pt = (Point) Geometry.geometryFromObjectElement(GeoJson.getElement(PointTest.VALID_POINT, "derp"), "derp");
        MultiLineString mls = (MultiLineString) Geometry.geometryFromObjectElement(GeoJson.getElement(MultiLineStringTest.VALID_MULTI_LINE_STRING, "derp"), "derp");
        GeometryCollection gc = (GeometryCollection) Geometry.geometryFromObjectElement(GeoJson.getElement(GeometryCollectionTest.VALID_GEOMETRY_COLLECTION, "derp"), "derp");

        boolean gotException = false;

        try {
            Polygon pg = (Polygon) Geometry.geometryFromObjectElement(GeoJson.getElement(FeatureTest.POLYGON_FEATURE, "derp"), "derp");
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.ELEMENT_NOT_GEOMETRY));
            gotException = true;
        }

        assertTrue(gotException);
        gotException = false;

        try {
            GeometryCollection gc2 = (GeometryCollection) Geometry.geometryFromObjectElement(GeoJson.getElement(FeatureCollectionTest.VALID_FEATURE_COLLECTION, "derp"), "derp");
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.ELEMENT_NOT_GEOMETRY));
            gotException = true;
        }

        assertTrue(gotException);
    }
}
