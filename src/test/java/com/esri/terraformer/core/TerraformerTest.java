package com.esri.terraformer.core;

import com.esri.terraformer.formats.FormatUtils;
import com.esri.terraformer.formats.GeoJsonTest;
import com.google.gson.JsonElement;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertEquals;

public class TerraformerTest {
    @Test
    public void testGetElement() throws Exception {
        boolean gotException = false;
        try {
            FormatUtils.getElement("", "derp");
        } catch (IllegalArgumentException e) {
            gotException = true;
        }

        assertTrue(gotException);
        gotException = false;

        try {
            FormatUtils.getElement("[", "derp");
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.NOT_VALID_JSON));
            gotException = true;
        }

        assertTrue(gotException);

        assertNotEquals(null, FormatUtils.getElement(GeoJsonTest.VALID_MULTIPOINT, "derp"));
    }

    @Test
    public void testGetObject() throws Exception {
        boolean gotException = false;
        try {
            FormatUtils.getObject("", "derp");
        } catch (IllegalArgumentException e) {
            gotException = true;
        }

        assertTrue(gotException);
        gotException = false;

        try {
            FormatUtils.getObject("[", "derp");
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.NOT_A_JSON_OBJECT));
            gotException = true;
        }

        assertTrue(gotException);

        assertNotEquals(null, FormatUtils.getObject(GeoJsonTest.VALID_MULTIPOINT, "derp"));
    }

    @Test
    public void testObjectFromElement() throws Exception {
        JsonElement arrayElem = FormatUtils.getElement("[1,2,3,4,5]", "derp");
        JsonElement objElem = FormatUtils.getElement("{\"derp\":\"herp\"}", "derp");

        assertNotEquals(null, FormatUtils.arrayFromElement(arrayElem, "derp"));

        boolean gotException = false;
        try {
            FormatUtils.arrayFromElement(objElem, "derp");
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.ELEMENT_NOT_ARRAY));
            gotException = true;
        }

        assertTrue(gotException);
    }

    @Test
    public void testArrayFromElement() throws Exception {
        JsonElement arrayElem = FormatUtils.getElement("[1,2,3,4,5]", "derp");
        JsonElement objElem = FormatUtils.getElement("{\"derp\":\"herp\"}", "derp");

        assertNotEquals(null, FormatUtils.objectFromElement(objElem, "derp"));

        boolean gotException = false;
        try {
            FormatUtils.objectFromElement(arrayElem, "derp");
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.ELEMENT_NOT_OBJECT));
            gotException = true;
        }

        assertTrue(gotException);
    }

    @Test
    public void testIsEmpty() throws Exception {
        assertEquals(true, FormatUtils.isEmpty(null));
        assertEquals(true, FormatUtils.isEmpty(""));
        assertEquals(false, FormatUtils.isEmpty("derp"));
    }
}
