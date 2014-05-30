package com.esri.terraformer;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GeoJsonTypeTest {
    @Test
    public void testToString() throws Exception {
        assertEquals("FeatureCollection", GeoJsonType.FEATURECOLLECTION.toString());
        assertEquals("Feature", GeoJsonType.FEATURE.toString());
        assertEquals("GeometryCollection", GeoJsonType.GEOMETRYCOLLECTION.toString());
        assertEquals("MultiPolygon", GeoJsonType.MULTIPOLYGON.toString());
        assertEquals("Polygon", GeoJsonType.POLYGON.toString());
        assertEquals("MultiLineString", GeoJsonType.MULTILINESTRING.toString());
        assertEquals("LineString", GeoJsonType.LINESTRING.toString());
        assertEquals("MultiPoint", GeoJsonType.MULTIPOINT.toString());
        assertEquals("Point", GeoJsonType.POINT.toString());
    }

    @Test
    public void testFromJson() throws Exception {
        boolean gotException = false;
        try {
            GeoJsonType.fromJson("BadType");
        } catch (RuntimeException e) {
            gotException = true;
        }

        assertTrue(gotException);

        assertEquals(GeoJsonType.POINT, GeoJsonType.fromJson("Point"));
        assertEquals(GeoJsonType.MULTIPOINT, GeoJsonType.fromJson("MultiPoint"));
        assertEquals(GeoJsonType.LINESTRING, GeoJsonType.fromJson("LineString"));
        assertEquals(GeoJsonType.MULTILINESTRING, GeoJsonType.fromJson("MultiLineString"));
        assertEquals(GeoJsonType.POLYGON, GeoJsonType.fromJson("Polygon"));
        assertEquals(GeoJsonType.MULTIPOLYGON, GeoJsonType.fromJson("MultiPolygon"));
        assertEquals(GeoJsonType.GEOMETRYCOLLECTION, GeoJsonType.fromJson("GeometryCollection"));
        assertEquals(GeoJsonType.FEATURE, GeoJsonType.fromJson("Feature"));
        assertEquals(GeoJsonType.FEATURECOLLECTION, GeoJsonType.fromJson("FeatureCollection"));
    }
}
