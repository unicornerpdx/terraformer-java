package com.esri.terraformer;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GeoJsonTypeTest {
    @Test
    public void testToString() throws Exception {
        assertEquals("FeatureCollection", GeometryType.FEATURECOLLECTION.toString());
        assertEquals("Feature", GeometryType.FEATURE.toString());
        assertEquals("GeometryCollection", GeometryType.GEOMETRYCOLLECTION.toString());
        assertEquals("MultiPolygon", GeometryType.MULTIPOLYGON.toString());
        assertEquals("Polygon", GeometryType.POLYGON.toString());
        assertEquals("MultiLineString", GeometryType.MULTILINESTRING.toString());
        assertEquals("LineString", GeometryType.LINESTRING.toString());
        assertEquals("MultiPoint", GeometryType.MULTIPOINT.toString());
        assertEquals("Point", GeometryType.POINT.toString());
    }

    @Test
    public void testFromJson() throws Exception {
        boolean gotException = false;
        try {
            GeometryType.fromJson("BadType");
        } catch (RuntimeException e) {
            gotException = true;
        }

        assertTrue(gotException);

        assertEquals(GeometryType.POINT, GeometryType.fromJson("Point"));
        assertEquals(GeometryType.MULTIPOINT, GeometryType.fromJson("MultiPoint"));
        assertEquals(GeometryType.LINESTRING, GeometryType.fromJson("LineString"));
        assertEquals(GeometryType.MULTILINESTRING, GeometryType.fromJson("MultiLineString"));
        assertEquals(GeometryType.POLYGON, GeometryType.fromJson("Polygon"));
        assertEquals(GeometryType.MULTIPOLYGON, GeometryType.fromJson("MultiPolygon"));
        assertEquals(GeometryType.GEOMETRYCOLLECTION, GeometryType.fromJson("GeometryCollection"));
        assertEquals(GeometryType.FEATURE, GeometryType.fromJson("Feature"));
        assertEquals(GeometryType.FEATURECOLLECTION, GeometryType.fromJson("FeatureCollection"));
    }
}
