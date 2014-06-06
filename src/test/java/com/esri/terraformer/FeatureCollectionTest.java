package com.esri.terraformer;

import org.junit.Test;

public class FeatureCollectionTest {
    static final String VALID_FEATURE_COLLECTION = "{\"type\":\"FeatureCollection\",\"features\":[{\"type\":\"Feature\",\"geometry\":{\"type\":\"MultiLineString\",\"coordinates\":[[[100.0,0.0],[101.0,1.0]],[[103.0,5.0],[109.0,3.0]]]},\"properties\":{\"innerObject\":{\"herp\":\"derp\",\"nurp\":100.5},\"innerArray\":[1,2,3,4],\"horse\":\"hands\"}},{\"type\":\"Feature\",\"geometry\":{\"type\":\"Polygon\",\"coordinates\":[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]]},\"properties\":{\"innerObject\":{\"herp\":\"derp\",\"nurp\":100.5},\"innerArray\":[1,2,3,4],\"horse\":\"hands\"}},{\"type\":\"Feature\",\"geometry\":{\"type\":\"MultiPolygon\",\"coordinates\":[[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]],[[[95.0,0.0],[99.0,0.0],[99.0,4.0],[95.0,4.0],[95.0,0.0]],[[96.0,0.5],[98.0,0.5],[98.0,1.0],[96.0,1.0],[96.0,0.5]],[[96.0,3.0],[98.0,3.0],[98.0,3.5],[96.0,3.5],[96.0,3.0]]]]},\"properties\":{\"innerObject\":{\"herp\":\"derp\",\"nurp\":100.5},\"innerArray\":[1,2,3,4],\"horse\":\"hands\"}},{\"type\":\"Feature\",\"geometry\":{\"type\":\"GeometryCollection\",\"geometries\":[{\"type\":\"Point\",\"coordinates\":[100.0,0.0,90.0,90.0]},{\"type\":\"MultiPoint\",\"coordinates\":[[100.0,0.0],[101.0,1.0]]},{\"type\":\"LineString\",\"coordinates\":[[100.0,0.0],[101.0,1.0]]},{\"type\":\"MultiLineString\",\"coordinates\":[[[100.0,0.0],[101.0,1.0]],[[103.0,5.0],[109.0,3.0]]]},{\"type\":\"Polygon\",\"coordinates\":[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]]},{\"type\":\"MultiPolygon\",\"coordinates\":[[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]],[[[95.0,0.0],[99.0,0.0],[99.0,4.0],[95.0,4.0],[95.0,0.0]],[[96.0,0.5],[98.0,0.5],[98.0,1.0],[96.0,1.0],[96.0,0.5]],[[96.0,3.0],[98.0,3.0],[98.0,3.5],[96.0,3.5],[96.0,3.0]]]]},{\"type\":\"GeometryCollection\",\"geometries\":[{\"type\":\"Point\",\"coordinates\":[100.0,0.0,90.0,90.0]},{\"type\":\"MultiPoint\",\"coordinates\":[[100.0,0.0],[101.0,1.0]]},{\"type\":\"LineString\",\"coordinates\":[[100.0,0.0],[101.0,1.0]]},{\"type\":\"MultiLineString\",\"coordinates\":[[[100.0,0.0],[101.0,1.0]],[[103.0,5.0],[109.0,3.0]]]},{\"type\":\"Polygon\",\"coordinates\":[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]]},{\"type\":\"MultiPolygon\",\"coordinates\":[[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]],[[[95.0,0.0],[99.0,0.0],[99.0,4.0],[95.0,4.0],[95.0,0.0]],[[96.0,0.5],[98.0,0.5],[98.0,1.0],[96.0,1.0],[96.0,0.5]],[[96.0,3.0],[98.0,3.0],[98.0,3.5],[96.0,3.5],[96.0,3.0]]]]}]}]},\"properties\":{\"innerObject\":{\"herp\":\"derp\",\"nurp\":100.5},\"innerArray\":[1,2,3,4],\"horse\":\"hands\"}}]}";

    @Test
    public void testGetType() throws Exception {

    }

    @Test
    public void testToJson() throws Exception {

    }

    @Test
    public void testToJsonObject() throws Exception {

    }

    @Test
    public void testIsValid() throws Exception {

    }

    @Test
    public void testIsEquivalentTo() throws Exception {

    }

    @Test
    public void testDecodeFeatureCollection() throws Exception {

    }

    static FeatureCollection getFeatureCollection() {
        return new FeatureCollection(
                FeatureTest.getMultiLineStringFeature(),
                FeatureTest.getPolygonFeature(),
                FeatureTest.getMultiPolygonFeature(),
                FeatureTest.getGeometryCollectionFeature()
        );
    }
}
