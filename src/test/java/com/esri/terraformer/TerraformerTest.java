package com.esri.terraformer;

import com.google.gson.JsonElement;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class TerraformerTest {
    @Test
    public void testGetElement() throws Exception {
        boolean gotException = false;
        try {
            Terraformer.getElement("", "derp");
        } catch (IllegalArgumentException e) {
            gotException = true;
        }

        assertTrue(gotException);
        gotException = false;

        try {
            Terraformer.getElement("[", "derp");
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.NOT_VALID_JSON));
            gotException = true;
        }

        assertTrue(gotException);

        assertNotEquals(null, Terraformer.getElement(GeoJsonTest.VALID_MULTIPOINT, "derp"));
    }

    @Test
    public void testGetObject() throws Exception {
        boolean gotException = false;
        try {
            Terraformer.getObject("", "derp");
        } catch (IllegalArgumentException e) {
            gotException = true;
        }

        assertTrue(gotException);
        gotException = false;

        try {
            Terraformer.getObject("[", "derp");
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.NOT_A_JSON_OBJECT));
            gotException = true;
        }

        assertTrue(gotException);

        assertNotEquals(null, Terraformer.getObject(GeoJsonTest.VALID_MULTIPOINT, "derp"));
    }

    @Test
    public void testObjectFromElement() throws Exception {
        JsonElement arrayElem = Terraformer.getElement("[1,2,3,4,5]", "derp");
        JsonElement objElem = Terraformer.getElement("{\"derp\":\"herp\"}", "derp");

        assertNotEquals(null, Terraformer.arrayFromElement(arrayElem, "derp"));

        boolean gotException = false;
        try {
            Terraformer.arrayFromElement(objElem, "derp");
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.ELEMENT_NOT_ARRAY));
            gotException = true;
        }

        assertTrue(gotException);
    }

    @Test
    public void testArrayFromElement() throws Exception {
        JsonElement arrayElem = Terraformer.getElement("[1,2,3,4,5]", "derp");
        JsonElement objElem = Terraformer.getElement("{\"derp\":\"herp\"}", "derp");

        assertNotEquals(null, Terraformer.objectFromElement(objElem, "derp"));

        boolean gotException = false;
        try {
            Terraformer.objectFromElement(arrayElem, "derp");
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.ELEMENT_NOT_OBJECT));
            gotException = true;
        }

        assertTrue(gotException);
    }

    @Test
    public void testIsEmpty() throws Exception {
        assertEquals(true, Terraformer.isEmpty(null));
        assertEquals(true, Terraformer.isEmpty(""));
        assertEquals(false, Terraformer.isEmpty("derp"));
    }
}
