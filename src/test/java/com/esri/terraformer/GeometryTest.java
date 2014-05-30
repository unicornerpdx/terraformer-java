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
        // TODO once all the geometries are written
    }
}
