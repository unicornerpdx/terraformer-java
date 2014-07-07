package com.esri.terraformer.formats;

import com.esri.terraformer.core.Terraformer;
import com.esri.terraformer.core.TerraformerException;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ConversionTest {

    // Point

    static final String GEOJSON_POINT = "{\"type\":\"Point\",\"coordinates\":[-58.7109375,47.4609375]}";
    static final String ESRI_POINT = "{\"x\":-58.7109375,\"y\":47.4609375,\"spatialReference\":{\"wkid\":4326}}";
    static final String GEOJSON_POINT_NULL_ISLAND = "{\"type\":\"Point\",\"coordinates\":[0.0,0.0]}";
    static final String ESRI_POINT_NULL_ISLAND = "{\"x\":0.0,\"y\":0.0,\"spatialReference\":{\"wkid\":4326}}";
    static final String GEOJSON_POINT_WITH_Z = "{\"type\":\"Point\",\"coordinates\":[-58.7109375,47.4609375,100.0]}";
    static final String ESRI_POINT_WITH_Z =
            "{\"x\":-58.7109375,\"y\":47.4609375,\"z\":100.0,\"spatialReference\":{\"wkid\":4326}}";
    static final String GEOJSON_POINT_WITH_Z_AND_M =
            "{\"type\":\"Point\",\"coordinates\":[-58.7109375,47.4609375,100.0,50.0]}";
    static final String ESRI_POINT_WITH_Z_AND_M =
            "{\"x\":-58.7109375,\"y\":47.4609375,\"z\":100.0,\"m\":50.0,\"spatialReference\":{\"wkid\":4326}}";

    // LineString

    static final String GEOJSON_LINE =
            "{\"type\":\"LineString\",\"coordinates\":[[21.4453125,-14.0625],[33.3984375,-20.7421875],[38.3203125," +
            "-24.609375]]}";
    static final String ESRI_LINE =
            "{\"paths\":[[[21.4453125,-14.0625],[33.3984375,-20.7421875],[38.3203125,-24.609375]]]," +
            "\"spatialReference\":{\"wkid\":4326}}";
    static final String GEOJSON_LINE_WITH_Z =
            "{\"type\":\"LineString\",\"coordinates\":[[21.4453125,-14.0625,100.0],[33.3984375,-20.7421875,100.0]," +
            "[38.3203125,-24.609375,100.0]]}";
    static final String ESRI_LINE_WITH_Z =
            "{\"paths\":[[[21.4453125,-14.0625,100.0],[33.3984375,-20.7421875,100.0],[38.3203125,-24.609375,100.0]]]," +
            "\"spatialReference\":{\"wkid\":4326},\"hasZ\":true}";
    static final String GEOJSON_LINE_WITH_Z_AND_M =
            "{\"type\":\"LineString\",\"coordinates\":[[21.4453125,-14.0625,100.0,50.0],[33.3984375,-20.7421875," +
            "100.0,50.0],[38.3203125,-24.609375,100.0,50.0]]}";
    static final String ESRI_LINE_WITH_Z_AND_M =
            "{\"paths\":[[[21.4453125,-14.0625,100.0,50.0],[33.3984375,-20.7421875,100.0,50.0],[38.3203125," +
            "-24.609375,100.0,50.0]]],\"spatialReference\":{\"wkid\":4326},\"hasZ\":true,\"hasM\":true}";

    // Polygon

    static final String GEOJSON_POLYGON =
            "{\"type\":\"Polygon\",\"coordinates\":[[[41.8359375,71.015625],[56.953125,33.75],[21.796875,36.5625]," +
            "[41.8359375,71.015625]]]}";
    static final String ESRI_POLYGON =
            "{\"spatialReference\":{\"wkid\":4326},\"rings\":[[[41.8359375,71.015625],[56.953125,33.75],[21.796875," +
            "36.5625],[41.8359375,71.015625]]]}";
    static final String GEOJSON_POLYGON_WITH_Z =
            "{\"type\":\"Polygon\",\"coordinates\":[[[41.8359375,71.015625,100.0],[56.953125,33.75,100.0],[21.796875," +
            "36.5625,100.0],[41.8359375,71.015625,100.0]]]}";
    static final String ESRI_POLYGON_WITH_Z =
            "{\"spatialReference\":{\"wkid\":4326},\"rings\":[[[41.8359375,71.015625,100.0],[56.953125,33.75,100.0]," +
            "[21.796875,36.5625,100.0],[41.8359375,71.015625,100.0]]],\"hasZ\":true}";
    static final String GEOJSON_POLYGON_WITH_Z_AND_M =
            "{\"type\":\"Polygon\",\"coordinates\":[[[41.8359375,71.015625,100.0,50.0],[56.953125,33.75,100.0,50.0]," +
            "[21.796875,36.5625,100.0,50.0],[41.8359375,71.015625,100.0,50.0]]]}";
    static final String ESRI_POLYGON_WITH_Z_AND_M =
            "{\"spatialReference\":{\"wkid\":4326},\"rings\":[[[41.8359375,71.015625,100.0,50.0],[56.953125,33.75," +
            "100.0,50.0],[21.796875,36.5625,100.0,50.0],[41.8359375,71.015625,100.0,50.0]]],\"hasZ\":true," +
            "\"hasM\":true}";
    static final String GEOJSON_POLYGON_WITH_HOLE =
            "{\"type\":\"Polygon\",\"coordinates\":[[[100.0,0.0],[100.0,1.0],[101.0,1.0],[101.0,0.0],[100.0,0.0]]," +
            "[[100.2,0.2],[100.8,0.2],[100.8,0.8],[100.2,0.8],[100.2,0.2]]]}";
    static final String ESRI_POLYGON_WITH_HOLE =
            "{\"rings\":[[[100.0,0.0],[100.0,1.0],[101.0,1.0],[101.0,0.0],[100.0,0.0]],[[100.2,0.2],[100.8,0.2]," +
            "[100.8,0.8],[100.2,0.8],[100.2,0.2]]],\"spatialReference\":{\"wkid\":4326}}";

    // MultiPoint

    static final String GEOJSON_MULTIPOINT = "{\"type\": \"MultiPoint\",\"coordinates\":[[41.8359375,71.015625]," +
                                             "[56.953125,33.75],[21.796875,36.5625]]}";
    static final String ESRI_MULTIPOINT = "{\"points\":[[41.8359375,71.015625],[56.953125,33.75],[21.796875," +
                                          "36.5625]],\"spatialReference\":{\"wkid\":4326}}";

    // MultiLineString

    static final String GEOJSON_MULTILINE = "{\"type\": \"MultiLineString\",\"coordinates\": [[[41.8359375," +
                                            "71.015625],[56.953125,33.75]],[[21.796875,36.5625],[47.8359375," +
                                            "71.015625]]]}";
    static final String ESRI_MULTILINE = "{\"paths\":[[[41.8359375,71.015625],[56.953125,33.75]],[[21.796875," +
                                         "36.5625],[47.8359375,71.015625]]],\"spatialReference\":{\"wkid\":4326}}";

    // MultiPolygon

    static final String GEOJSON_MULTIPOLYGON =
            "{\"type\":\"MultiPolygon\",\"coordinates\":[[[[102.0,2.0],[102.0,3.0],[103.0,3.0],[103.0,2.0],[102.0," +
            "2.0]]],[[[100.0,0.0],[100.0,1.0],[101.0,1.0],[101.0,0.0],[100.0,0.0]]]]}";
    static final String ESRI_MULTIPOLYGON =
            "{\"rings\":[[[102.0,2.0],[102.0,3.0],[103.0,3.0],[103.0,2.0],[102.0,2.0]],[[100.0,0.0],[100.0,1.0]," +
            "[101.0,1.0],[101.0,0.0],[100.0,0.0]]],\"spatialReference\":{\"wkid\":4326}}";
    static final String GEOJSON_MULTIPOLYGON_WITH_HOLES =
            "{\"type\":\"MultiPolygon\",\"coordinates\":[[[[102.0,2.0],[102.0,3.0],[103.0,3.0],[103.0,2.0],[102.0," +
            "2.0]]],[[[100.0,0.0],[100.0,1.0],[101.0,1.0],[101.0,0.0],[100.0,0.0]],[[100.2,0.2],[100.8,0.2],[100.8," +
            "0.8],[100.2,0.8],[100.2,0.2]]]]}";
    static final String ESRI_MULTIPOLYGON_WITH_HOLES =
            "{\"spatialReference\":{\"wkid\":4326},\"rings\":[[[102.0,2.0],[102.0,3.0],[103.0,3.0],[103.0,2.0]," +
            "[102.0,2.0]],[[100.0,0.0],[100.0,1.0],[101.0,1.0],[101.0,0.0],[100.0,0.0]],[[100.2,0.2],[100.8,0.2]," +
            "[100.8,0.8],[100.2,0.8],[100.2,0.2]]]}";

    // Feature

    static final String GEOJSON_FEATURE = "{\"type\":\"Feature\",\"id\":\"foo\",\"geometry\":{\"type\":\"Polygon\",\"coordinates\":[[[41.8359375,71.015625],[56.953125,33.75],[21.796875,36.5625],[41.8359375,71.015625]]]},\"properties\":{\"foo\":\"bar\"}}";
    static final String ESRI_FEATURE = "{\"geometry\":{\"rings\":[[[41.8359375,71.015625],[56.953125,33.75],[21.796875,36.5625],[41.8359375,71.015625]]],\"spatialReference\":{\"wkid\":4326}},\"attributes\":{\"foo\":\"bar\",\"OBJECTID\":\"foo\"}}";
    static final String GEOJSON_FEATURE_CUSTOM_ID = "{\"type\":\"Feature\",\"id\":\"foo\",\"geometry\":{\"type\":\"Polygon\",\"coordinates\":[[[41.8359375,71.015625],[56.953125,33.75],[21.796875,36.5625],[41.8359375,71.015625]]]},\"properties\":{\"foo\":\"bar\"}}";
    static final String ESRI_FEATURE_CUSTOM_ID = "{\"geometry\":{\"rings\":[[[41.8359375,71.015625],[56.953125,33.75],[21.796875,36.5625],[41.8359375,71.015625]]],\"spatialReference\":{\"wkid\":4326}},\"attributes\":{\"foo\":\"bar\",\"myId\":\"foo\"}}";

    // FeatureCollection

    static final String GEOJSON_FEATURE_COLLECTION = "{\"type\":\"FeatureCollection\",\"features\":[{\"type\":\"Feature\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[102.0,0.5]},\"properties\":{\"prop0\":\"value0\"}},{\"type\":\"Feature\",\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[102.0,0.0],[103.0,1.0],[104.0,0.0],[105.0,1.0]]},\"properties\":{\"prop0\":\"value0\"}},{\"type\":\"Feature\",\"geometry\":{\"type\":\"Polygon\",\"coordinates\":[[[100.0,0.0],[101.0,0.0],[101.0,1.0],[100.0,1.0],[100.0,0.0]]]},\"properties\":{\"prop0\":\"value0\"}}]}";
    static final String ESRI_FEATURE_ARRAY = "[{\"geometry\":{\"x\":102,\"y\":0.5,\"spatialReference\":{\"wkid\":4326}},\"attributes\":{\"prop0\":\"value0\"}},{\"geometry\":{\"paths\":[[[102,0],[103,1],[104,0],[105,1]]],\"spatialReference\":{\"wkid\":4326}},\"attributes\":{\"prop0\":\"value0\"}},{\"geometry\":{\"rings\":[[[100,0],[100,1],[101,1],[101,0],[100,0]]],\"spatialReference\":{\"wkid\":4326}},\"attributes\":{\"prop0\":\"value0\"}}]";

    // GeometryCollection

    static final String GEOJSON_GEOMETRY_COLLECTION = "{\"type\":\"GeometryCollection\",\"geometries\":[{\"type\":\"Polygon\",\"coordinates\":[[[-95,43],[-95,50],[-90,50],[-91,42],[-95,43]]]},{\"type\":\"LineString\",\"coordinates\":[[-89,42],[-89,50],[-80,50],[-80,42]]},{\"type\":\"Point\",\"coordinates\":[-94,46]}]}";
    static final String ESRI_GEOMETRY_ARRAY = "[{\"rings\":[[[-95,43],[-95,50],[-90,50],[-91,42],[-95,43]]],\"spatialReference\":{\"wkid\":4326}},{\"paths\":[[[-89,42],[-89,50],[-80,50],[-80,42]]],\"spatialReference\":{\"wkid\":4326}},{\"x\":-94,\"y\":46,\"spatialReference\":{\"wkid\":4326}}]";

    static Terraformer esri2geo;
    static Terraformer geo2esri;

    @BeforeClass
    public static void setUp() {
        esri2geo = new Terraformer(new EsriJson(), new GeoJson());
        geo2esri = new Terraformer(new GeoJson(), new EsriJson());
    }

    @Test
    public void testConvertPoint() throws Exception {
        // test conversion between GeoJSON Point and ArcGIS Point
        assertEqualGeo(ESRI_POINT, GEOJSON_POINT);
        // test conversion of Null Island point between GeoJSON and ArcGIS
        assertEqualGeo(ESRI_POINT_NULL_ISLAND, GEOJSON_POINT_NULL_ISLAND);
        // test conversion between GeoJSON Point and ArcGIS Point with a Z value
        assertEqualGeo(ESRI_POINT_WITH_Z, GEOJSON_POINT_WITH_Z);
        // test conversion between GeoJSON Point and ArcGIS Point with Z and M values
        assertEqualGeo(ESRI_POINT_WITH_Z_AND_M, GEOJSON_POINT_WITH_Z_AND_M);
    }

    @Test
    public void testConvertLine() throws Exception {
        // test conversion between GeoJSON Line and ArcGIS Line
        assertEqualGeo(ESRI_LINE, GEOJSON_LINE);
        // test conversion between GeoJSON Line and ArcGIS Line with Z values
        assertEqualGeo(ESRI_LINE_WITH_Z, GEOJSON_LINE_WITH_Z);
        // test conversion between GeoJSON Line and ArcGIS Line with Z and M values
        assertEqualGeo(ESRI_LINE_WITH_Z_AND_M, GEOJSON_LINE_WITH_Z_AND_M);
    }

    @Test
    public void testConvertPolygon() throws Exception {
        // test conversion between GeoJSON Polygon and ArcGIS Polygon
        assertEqualGeo(ESRI_POLYGON, GEOJSON_POLYGON);
        // test conversion between GeoJSON Polygon and ArcGIS Polygon with Z values
        assertEqualGeo(ESRI_POLYGON_WITH_Z, GEOJSON_POLYGON_WITH_Z);
        // test conversion between GeoJSON Polygon and ArcGIS Polygon with Z and M values
        assertEqualGeo(ESRI_POLYGON_WITH_Z_AND_M, GEOJSON_POLYGON_WITH_Z_AND_M);
        // test conversion between a GeoJSON Polygon with a hole and an ArcGIS Polygon with 2 rings
        assertEqualGeo(ESRI_POLYGON_WITH_HOLE, GEOJSON_POLYGON_WITH_HOLE);
    }

    @Test
    public void testConvertMultiPoint() throws Exception {
        // test conversion between GeoJSON MultiPoint and ArcGIS MultiPoint
        assertEqualGeo(ESRI_MULTIPOINT, GEOJSON_MULTIPOINT);
    }

    @Test
    public void testConvertMultiLine() throws Exception {
        // test conversion between GeoJSON MultiLine and ArcGIS MultiLine
        assertEqualGeo(ESRI_MULTILINE, GEOJSON_MULTILINE);
    }

    @Test
    public void testConvertMultiPolygon() throws Exception {
        // test conversion between GeoJSON MultiPolygon and ArcGIS MultiPolygon
        assertEqualGeo(ESRI_MULTIPOLYGON, GEOJSON_MULTIPOLYGON);
        // test conversion between GeoJSON MultiPolygon with holes and ArcGIS MultiPolygon
        assertEqualGeo(ESRI_MULTIPOLYGON_WITH_HOLES, GEOJSON_MULTIPOLYGON_WITH_HOLES);
    }

    @Test
    public void testConvertFeature() throws Exception {
        // test conversion between GeoJSON Feature and ArcGIS Feature
        assertEqualGeo(ESRI_FEATURE, GEOJSON_FEATURE);

        // test custom ID key
        EsriJson e = new EsriJson();
        e.setFeatureIdKey("myId");
        Terraformer g2e = new Terraformer(new GeoJson(), e);
        Terraformer e2g = new Terraformer(e, new GeoJson());
        assertEqualJsonObjects(ESRI_FEATURE_CUSTOM_ID, g2e.convert(GEOJSON_FEATURE_CUSTOM_ID));
        assertEqualJsonObjects(GEOJSON_FEATURE_CUSTOM_ID, e2g.convert(ESRI_FEATURE_CUSTOM_ID));
    }


    @Test
    public void testConvertFeatureCollection() throws Exception {
        // test conversion from GeoJSON FeatureCollection to Array of ArcGIS Features
        assertEqualJsonArrays(ESRI_FEATURE_ARRAY, geo2esri.convert(GEOJSON_FEATURE_COLLECTION));

        // note: conversion from an Array of ArcGIS Features to GeoJSON is not directly supported.
    }

    @Test
    public void testConvertGeometryCollection() throws Exception {
        // test conversion from GeoJSON GeometryCollection to Array of ArcGIS Geometries
        assertEqualJsonArrays(ESRI_GEOMETRY_ARRAY, geo2esri.convert(GEOJSON_GEOMETRY_COLLECTION));

        // note: conversion from an Array of ArcGIS Geometries to GeoJSON is not directly supported.
    }

    static void assertEqualGeo(String esriJson, String geoJson) throws TerraformerException {
        // Test conversion of GeoJSON to Esri JSON
        assertEqualJsonObjects(esriJson, geo2esri.convert(geoJson));
        // Test conversion of Esri JSON to GeoJSON
        assertEqualJsonObjects(geoJson, esri2geo.convert(esriJson));
    }

    public static void assertEqualJsonObjects(String expected, String actual) {
        JsonObject exp, act;
        try {
            exp = FormatUtils.getObject(expected, "");
            act = FormatUtils.getObject(actual, "");
        } catch (TerraformerException e) {
            throw new RuntimeException(e);
        }

        assertEquals(exp, act);
    }

    public static void assertEqualJsonArrays(String expected, String actual) {
        JsonArray exp, act;
        try {
            exp = FormatUtils.getArray(expected, "");
            act = FormatUtils.getArray(actual, "");
        } catch (TerraformerException e) {
            throw new RuntimeException(e);
        }

        assertEquals(exp, act);
    }
}