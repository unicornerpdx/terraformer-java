package com.esri.terraformer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FeatureCollectionTest {
    static final String VALID_FEATURE_COLLECTION = "{\"type\":\"FeatureCollection\",\"features\":[{\"type\":\"Feature\",\"geometry\":{\"type\":\"MultiLineString\",\"coordinates\":[[[100.0,0.0],[101.0,1.0]],[[103.0,5.0],[109.0,3.0]]]},\"properties\":{\"innerObject\":{\"herp\":\"derp\",\"nurp\":100.5},\"innerArray\":[1,2,3,4],\"horse\":\"hands\"}},{\"type\":\"Feature\",\"geometry\":{\"type\":\"Polygon\",\"coordinates\":[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]]},\"properties\":{\"innerObject\":{\"herp\":\"derp\",\"nurp\":100.5},\"innerArray\":[1,2,3,4],\"horse\":\"hands\"}},{\"type\":\"Feature\",\"geometry\":{\"type\":\"MultiPolygon\",\"coordinates\":[[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]],[[[95.0,0.0],[99.0,0.0],[99.0,4.0],[95.0,4.0],[95.0,0.0]],[[96.0,0.5],[98.0,0.5],[98.0,1.0],[96.0,1.0],[96.0,0.5]],[[96.0,3.0],[98.0,3.0],[98.0,3.5],[96.0,3.5],[96.0,3.0]]]]},\"properties\":{\"innerObject\":{\"herp\":\"derp\",\"nurp\":100.5},\"innerArray\":[1,2,3,4],\"horse\":\"hands\"}},{\"type\":\"Feature\",\"geometry\":{\"type\":\"GeometryCollection\",\"geometries\":[{\"type\":\"Point\",\"coordinates\":[100.0,0.0,90.0,90.0]},{\"type\":\"MultiPoint\",\"coordinates\":[[100.0,0.0],[101.0,1.0]]},{\"type\":\"LineString\",\"coordinates\":[[100.0,0.0],[101.0,1.0]]},{\"type\":\"MultiLineString\",\"coordinates\":[[[100.0,0.0],[101.0,1.0]],[[103.0,5.0],[109.0,3.0]]]},{\"type\":\"Polygon\",\"coordinates\":[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]]},{\"type\":\"MultiPolygon\",\"coordinates\":[[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]],[[[95.0,0.0],[99.0,0.0],[99.0,4.0],[95.0,4.0],[95.0,0.0]],[[96.0,0.5],[98.0,0.5],[98.0,1.0],[96.0,1.0],[96.0,0.5]],[[96.0,3.0],[98.0,3.0],[98.0,3.5],[96.0,3.5],[96.0,3.0]]]]},{\"type\":\"GeometryCollection\",\"geometries\":[{\"type\":\"Point\",\"coordinates\":[100.0,0.0,90.0,90.0]},{\"type\":\"MultiPoint\",\"coordinates\":[[100.0,0.0],[101.0,1.0]]},{\"type\":\"LineString\",\"coordinates\":[[100.0,0.0],[101.0,1.0]]},{\"type\":\"MultiLineString\",\"coordinates\":[[[100.0,0.0],[101.0,1.0]],[[103.0,5.0],[109.0,3.0]]]},{\"type\":\"Polygon\",\"coordinates\":[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]]},{\"type\":\"MultiPolygon\",\"coordinates\":[[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]],[[[95.0,0.0],[99.0,0.0],[99.0,4.0],[95.0,4.0],[95.0,0.0]],[[96.0,0.5],[98.0,0.5],[98.0,1.0],[96.0,1.0],[96.0,0.5]],[[96.0,3.0],[98.0,3.0],[98.0,3.5],[96.0,3.5],[96.0,3.0]]]]}]}]},\"properties\":{\"innerObject\":{\"herp\":\"derp\",\"nurp\":100.5},\"innerArray\":[1,2,3,4],\"horse\":\"hands\"}}]}";
    static final String VALID_DIFF_ORDER = "{\"type\":\"FeatureCollection\",\"features\":[{\"type\":\"Feature\",\"geometry\":{\"type\":\"Polygon\",\"coordinates\":[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]]},\"properties\":{\"innerObject\":{\"herp\":\"derp\",\"nurp\":100.5},\"innerArray\":[1,2,3,4],\"horse\":\"hands\"}},{\"type\":\"Feature\",\"geometry\":{\"type\":\"MultiLineString\",\"coordinates\":[[[100.0,0.0],[101.0,1.0]],[[103.0,5.0],[109.0,3.0]]]},\"properties\":{\"innerObject\":{\"herp\":\"derp\",\"nurp\":100.5},\"innerArray\":[1,2,3,4],\"horse\":\"hands\"}},{\"type\":\"Feature\",\"geometry\":{\"type\":\"GeometryCollection\",\"geometries\":[{\"type\":\"Point\",\"coordinates\":[100.0,0.0,90.0,90.0]},{\"type\":\"MultiPoint\",\"coordinates\":[[100.0,0.0],[101.0,1.0]]},{\"type\":\"LineString\",\"coordinates\":[[100.0,0.0],[101.0,1.0]]},{\"type\":\"MultiLineString\",\"coordinates\":[[[100.0,0.0],[101.0,1.0]],[[103.0,5.0],[109.0,3.0]]]},{\"type\":\"Polygon\",\"coordinates\":[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]]},{\"type\":\"MultiPolygon\",\"coordinates\":[[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]],[[[95.0,0.0],[99.0,0.0],[99.0,4.0],[95.0,4.0],[95.0,0.0]],[[96.0,0.5],[98.0,0.5],[98.0,1.0],[96.0,1.0],[96.0,0.5]],[[96.0,3.0],[98.0,3.0],[98.0,3.5],[96.0,3.5],[96.0,3.0]]]]},{\"type\":\"GeometryCollection\",\"geometries\":[{\"type\":\"Point\",\"coordinates\":[100.0,0.0,90.0,90.0]},{\"type\":\"MultiPoint\",\"coordinates\":[[100.0,0.0],[101.0,1.0]]},{\"type\":\"LineString\",\"coordinates\":[[100.0,0.0],[101.0,1.0]]},{\"type\":\"MultiLineString\",\"coordinates\":[[[100.0,0.0],[101.0,1.0]],[[103.0,5.0],[109.0,3.0]]]},{\"type\":\"Polygon\",\"coordinates\":[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]]},{\"type\":\"MultiPolygon\",\"coordinates\":[[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]],[[[95.0,0.0],[99.0,0.0],[99.0,4.0],[95.0,4.0],[95.0,0.0]],[[96.0,0.5],[98.0,0.5],[98.0,1.0],[96.0,1.0],[96.0,0.5]],[[96.0,3.0],[98.0,3.0],[98.0,3.5],[96.0,3.5],[96.0,3.0]]]]}]}]},\"properties\":{\"innerObject\":{\"herp\":\"derp\",\"nurp\":100.5},\"innerArray\":[1,2,3,4],\"horse\":\"hands\"}},{\"type\":\"Feature\",\"geometry\":{\"type\":\"MultiPolygon\",\"coordinates\":[[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]],[[[95.0,0.0],[99.0,0.0],[99.0,4.0],[95.0,4.0],[95.0,0.0]],[[96.0,0.5],[98.0,0.5],[98.0,1.0],[96.0,1.0],[96.0,0.5]],[[96.0,3.0],[98.0,3.0],[98.0,3.5],[96.0,3.5],[96.0,3.0]]]]},\"properties\":{\"innerObject\":{\"herp\":\"derp\",\"nurp\":100.5},\"innerArray\":[1,2,3,4],\"horse\":\"hands\"}}]}";
    static final String EMPTY_FEATURES = "{\"type\":\"FeatureCollection\",\"features\":[]}";
    static final String WRONG_TYPE = "{\"type\":\"GeometryCollection\",\"features\":[{\"type\":\"Feature\",\"geometry\":{\"type\":\"Polygon\",\"coordinates\":[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]]},\"properties\":{\"innerObject\":{\"herp\":\"derp\",\"nurp\":100.5},\"innerArray\":[1,2,3,4],\"horse\":\"hands\"}},{\"type\":\"Feature\",\"geometry\":{\"type\":\"MultiLineString\",\"coordinates\":[[[100.0,0.0],[101.0,1.0]],[[103.0,5.0],[109.0,3.0]]]},\"properties\":{\"innerObject\":{\"herp\":\"derp\",\"nurp\":100.5},\"innerArray\":[1,2,3,4],\"horse\":\"hands\"}},{\"type\":\"Feature\",\"geometry\":{\"type\":\"GeometryCollection\",\"geometries\":[{\"type\":\"Point\",\"coordinates\":[100.0,0.0,90.0,90.0]},{\"type\":\"MultiPoint\",\"coordinates\":[[100.0,0.0],[101.0,1.0]]},{\"type\":\"LineString\",\"coordinates\":[[100.0,0.0],[101.0,1.0]]},{\"type\":\"MultiLineString\",\"coordinates\":[[[100.0,0.0],[101.0,1.0]],[[103.0,5.0],[109.0,3.0]]]},{\"type\":\"Polygon\",\"coordinates\":[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]]},{\"type\":\"MultiPolygon\",\"coordinates\":[[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]],[[[95.0,0.0],[99.0,0.0],[99.0,4.0],[95.0,4.0],[95.0,0.0]],[[96.0,0.5],[98.0,0.5],[98.0,1.0],[96.0,1.0],[96.0,0.5]],[[96.0,3.0],[98.0,3.0],[98.0,3.5],[96.0,3.5],[96.0,3.0]]]]},{\"type\":\"GeometryCollection\",\"geometries\":[{\"type\":\"Point\",\"coordinates\":[100.0,0.0,90.0,90.0]},{\"type\":\"MultiPoint\",\"coordinates\":[[100.0,0.0],[101.0,1.0]]},{\"type\":\"LineString\",\"coordinates\":[[100.0,0.0],[101.0,1.0]]},{\"type\":\"MultiLineString\",\"coordinates\":[[[100.0,0.0],[101.0,1.0]],[[103.0,5.0],[109.0,3.0]]]},{\"type\":\"Polygon\",\"coordinates\":[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]]},{\"type\":\"MultiPolygon\",\"coordinates\":[[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]],[[[95.0,0.0],[99.0,0.0],[99.0,4.0],[95.0,4.0],[95.0,0.0]],[[96.0,0.5],[98.0,0.5],[98.0,1.0],[96.0,1.0],[96.0,0.5]],[[96.0,3.0],[98.0,3.0],[98.0,3.5],[96.0,3.5],[96.0,3.0]]]]}]}]},\"properties\":{\"innerObject\":{\"herp\":\"derp\",\"nurp\":100.5},\"innerArray\":[1,2,3,4],\"horse\":\"hands\"}},{\"type\":\"Feature\",\"geometry\":{\"type\":\"MultiPolygon\",\"coordinates\":[[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]],[[[95.0,0.0],[99.0,0.0],[99.0,4.0],[95.0,4.0],[95.0,0.0]],[[96.0,0.5],[98.0,0.5],[98.0,1.0],[96.0,1.0],[96.0,0.5]],[[96.0,3.0],[98.0,3.0],[98.0,3.5],[96.0,3.5],[96.0,3.0]]]]},\"properties\":{\"innerObject\":{\"herp\":\"derp\",\"nurp\":100.5},\"innerArray\":[1,2,3,4],\"horse\":\"hands\"}}]}";
    static final String NO_TYPE = "{\"features\":[{\"type\":\"Feature\",\"geometry\":{\"type\":\"Polygon\",\"coordinates\":[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]]},\"properties\":{\"innerObject\":{\"herp\":\"derp\",\"nurp\":100.5},\"innerArray\":[1,2,3,4],\"horse\":\"hands\"}},{\"type\":\"Feature\",\"geometry\":{\"type\":\"MultiLineString\",\"coordinates\":[[[100.0,0.0],[101.0,1.0]],[[103.0,5.0],[109.0,3.0]]]},\"properties\":{\"innerObject\":{\"herp\":\"derp\",\"nurp\":100.5},\"innerArray\":[1,2,3,4],\"horse\":\"hands\"}},{\"type\":\"Feature\",\"geometry\":{\"type\":\"GeometryCollection\",\"geometries\":[{\"type\":\"Point\",\"coordinates\":[100.0,0.0,90.0,90.0]},{\"type\":\"MultiPoint\",\"coordinates\":[[100.0,0.0],[101.0,1.0]]},{\"type\":\"LineString\",\"coordinates\":[[100.0,0.0],[101.0,1.0]]},{\"type\":\"MultiLineString\",\"coordinates\":[[[100.0,0.0],[101.0,1.0]],[[103.0,5.0],[109.0,3.0]]]},{\"type\":\"Polygon\",\"coordinates\":[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]]},{\"type\":\"MultiPolygon\",\"coordinates\":[[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]],[[[95.0,0.0],[99.0,0.0],[99.0,4.0],[95.0,4.0],[95.0,0.0]],[[96.0,0.5],[98.0,0.5],[98.0,1.0],[96.0,1.0],[96.0,0.5]],[[96.0,3.0],[98.0,3.0],[98.0,3.5],[96.0,3.5],[96.0,3.0]]]]},{\"type\":\"GeometryCollection\",\"geometries\":[{\"type\":\"Point\",\"coordinates\":[100.0,0.0,90.0,90.0]},{\"type\":\"MultiPoint\",\"coordinates\":[[100.0,0.0],[101.0,1.0]]},{\"type\":\"LineString\",\"coordinates\":[[100.0,0.0],[101.0,1.0]]},{\"type\":\"MultiLineString\",\"coordinates\":[[[100.0,0.0],[101.0,1.0]],[[103.0,5.0],[109.0,3.0]]]},{\"type\":\"Polygon\",\"coordinates\":[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]]},{\"type\":\"MultiPolygon\",\"coordinates\":[[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]],[[[95.0,0.0],[99.0,0.0],[99.0,4.0],[95.0,4.0],[95.0,0.0]],[[96.0,0.5],[98.0,0.5],[98.0,1.0],[96.0,1.0],[96.0,0.5]],[[96.0,3.0],[98.0,3.0],[98.0,3.5],[96.0,3.5],[96.0,3.0]]]]}]}]},\"properties\":{\"innerObject\":{\"herp\":\"derp\",\"nurp\":100.5},\"innerArray\":[1,2,3,4],\"horse\":\"hands\"}},{\"type\":\"Feature\",\"geometry\":{\"type\":\"MultiPolygon\",\"coordinates\":[[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]],[[[95.0,0.0],[99.0,0.0],[99.0,4.0],[95.0,4.0],[95.0,0.0]],[[96.0,0.5],[98.0,0.5],[98.0,1.0],[96.0,1.0],[96.0,0.5]],[[96.0,3.0],[98.0,3.0],[98.0,3.5],[96.0,3.5],[96.0,3.0]]]]},\"properties\":{\"innerObject\":{\"herp\":\"derp\",\"nurp\":100.5},\"innerArray\":[1,2,3,4],\"horse\":\"hands\"}}]}";
    static final String NOT_AN_OBJECT = "[\"type\",\"features\"]";
    // one of the linestrings has too few points
    static final String INVALID_INNER_TYPE = "{\"type\":\"FeatureCollection\",\"features\":[{\"type\":\"Feature\",\"geometry\":{\"type\":\"MultiLineString\",\"coordinates\":[[[100.0,0.0],[101.0,1.0]],[[103.0,5.0],[109.0,3.0]]]},\"properties\":{\"innerObject\":{\"herp\":\"derp\",\"nurp\":100.5},\"innerArray\":[1,2,3,4],\"horse\":\"hands\"}},{\"type\":\"Feature\",\"geometry\":{\"type\":\"Polygon\",\"coordinates\":[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]]},\"properties\":{\"innerObject\":{\"herp\":\"derp\",\"nurp\":100.5},\"innerArray\":[1,2,3,4],\"horse\":\"hands\"}},{\"type\":\"Feature\",\"geometry\":{\"type\":\"MultiPolygon\",\"coordinates\":[[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]],[[[95.0,0.0],[99.0,0.0],[99.0,4.0],[95.0,4.0],[95.0,0.0]],[[96.0,0.5],[98.0,0.5],[98.0,1.0],[96.0,1.0],[96.0,0.5]],[[96.0,3.0],[98.0,3.0],[98.0,3.5],[96.0,3.5],[96.0,3.0]]]]},\"properties\":{\"innerObject\":{\"herp\":\"derp\",\"nurp\":100.5},\"innerArray\":[1,2,3,4],\"horse\":\"hands\"}},{\"type\":\"Feature\",\"geometry\":{\"type\":\"GeometryCollection\",\"geometries\":[{\"type\":\"Point\",\"coordinates\":[100.0,0.0,90.0,90.0]},{\"type\":\"MultiPoint\",\"coordinates\":[[100.0,0.0],[101.0,1.0]]},{\"type\":\"LineString\",\"coordinates\":[[100.0,0.0],[101.0,1.0]]},{\"type\":\"MultiLineString\",\"coordinates\":[[[100.0,0.0],[101.0,1.0]],[[103.0,5.0],[109.0,3.0]]]},{\"type\":\"Polygon\",\"coordinates\":[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]]},{\"type\":\"MultiPolygon\",\"coordinates\":[[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]],[[[95.0,0.0],[99.0,0.0],[99.0,4.0],[95.0,4.0],[95.0,0.0]],[[96.0,0.5],[98.0,0.5],[98.0,1.0],[96.0,1.0],[96.0,0.5]],[[96.0,3.0],[98.0,3.0],[98.0,3.5],[96.0,3.5],[96.0,3.0]]]]},{\"type\":\"GeometryCollection\",\"geometries\":[{\"type\":\"Point\",\"coordinates\":[100.0,0.0,90.0,90.0]},{\"type\":\"MultiPoint\",\"coordinates\":[[100.0,0.0],[101.0,1.0]]},{\"type\":\"LineString\",\"coordinates\":[[101.0,1.0]]},{\"type\":\"MultiLineString\",\"coordinates\":[[[100.0,0.0],[101.0,1.0]],[[103.0,5.0],[109.0,3.0]]]},{\"type\":\"Polygon\",\"coordinates\":[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]]},{\"type\":\"MultiPolygon\",\"coordinates\":[[[[100.0,0.0],[104.0,0.0],[104.0,4.0],[100.0,4.0],[100.0,0.0]],[[101.0,0.5],[103.0,0.5],[103.0,1.0],[101.0,1.0],[101.0,0.5]],[[101.0,3.0],[103.0,3.0],[103.0,3.5],[101.0,3.5],[101.0,3.0]]],[[[95.0,0.0],[99.0,0.0],[99.0,4.0],[95.0,4.0],[95.0,0.0]],[[96.0,0.5],[98.0,0.5],[98.0,1.0],[96.0,1.0],[96.0,0.5]],[[96.0,3.0],[98.0,3.0],[98.0,3.5],[96.0,3.5],[96.0,3.0]]]]}]}]},\"properties\":{\"innerObject\":{\"herp\":\"derp\",\"nurp\":100.5},\"innerArray\":[1,2,3,4],\"horse\":\"hands\"}}]}";
    static final String NO_FEATURES = "{\"type\":\"FeatureCollection\"}";
    static final String FEATURES_NOT_ARRAY = "{\"type\":\"FeatureCollection\",\"features\":100.0}";

    @Test
    public void testGetType() throws Exception {
        assertEquals(GeometryType.FEATURECOLLECTION, getFeatureCollection().getType());
        assertEquals(GeometryType.FEATURECOLLECTION, new FeatureCollection().getType());
    }

    @Test
    public void testIsValid() throws Exception {
        assertTrue(FeatureCollection.decodeFeatureCollection(VALID_FEATURE_COLLECTION).isValid());
        assertTrue(FeatureCollection.decodeFeatureCollection(EMPTY_FEATURES).isValid());
        assertTrue(new FeatureCollection(getFeatureCollection()).isValid());
        // feature collection can have empty features
        assertTrue(new FeatureCollection().isValid());

        // invalid multipolygon
        assertFalse(new FeatureCollection(FeatureTest.getGeometryCollectionFeature(),
                new Feature(new MultiPolygon(new Polygon(new LineString(
                        new Point(100d, 0d),
                        new Point(104d, 0d),
                        new Point(104d, 4d),
                        new Point(100d, 4d)), // not a linear ring
                new LineString(
                        new Point(101d, 0.5d),
                        new Point(103d, 0.5d),
                        new Point(103d, 1d),
                        new Point(101d, 1d),
                        new Point(101d, 0.5d)),
                new LineString(
                        new Point(101d, 3d),
                        new Point(103d, 3d),
                        new Point(103d, 3.5d),
                        new Point(101d, 3.5d),
                        new Point(101d, 3d)
                )))),
                FeatureTest.getPolygonFeature()
        ).isValid());

        // null polygon
        assertFalse(new FeatureCollection(
                FeatureTest.getPolygonFeature(),
                null,
                FeatureTest.getMultiPolygonFeature()
        ).isValid());
    }

    @Test
    public void testIsEquivalentTo() throws Exception {
        FeatureCollection fc = FeatureCollection.decodeFeatureCollection(VALID_FEATURE_COLLECTION);
        FeatureCollection otherFc = getFeatureCollection();
        FeatureCollection anotherFc = FeatureCollection.decodeFeatureCollection(VALID_DIFF_ORDER);

        assertTrue(fc.isEquivalentTo(otherFc));
        assertTrue(fc.isEquivalentTo(anotherFc));
        assertTrue(otherFc.isEquivalentTo(fc));
        assertTrue(otherFc.isEquivalentTo(anotherFc));
        assertTrue(anotherFc.isEquivalentTo(fc));
        assertTrue(anotherFc.isEquivalentTo(otherFc));
        // rotated linear ring
        assertTrue(fc.isEquivalentTo(new FeatureCollection(
                FeatureTest.getMultiLineStringFeature(),
                FeatureTest.getPolygonFeature(),
                new Feature(new MultiPolygon(new Polygon(
                        new LineString(
                                new Point(100d, 0d),
                                new Point(104d, 0d),
                                new Point(104d, 4d),
                                new Point(100d, 4d),
                                new Point(100d, 0d)),
                        new LineString(
                                new Point(101d, 0.5d),
                                new Point(103d, 0.5d),
                                new Point(103d, 1d),
                                new Point(101d, 1d),
                                new Point(101d, 0.5d)),
                        new LineString(
                                new Point(101d, 3d),
                                new Point(103d, 3d),
                                new Point(103d, 3.5d),
                                new Point(101d, 3.5d),
                                new Point(101d, 3d)
                        )
                ), new Polygon(
                        new LineString(
                                new Point(95d, 0d),
                                new Point(99d, 0d),
                                new Point(99d, 4d),
                                new Point(95d, 4d),
                                new Point(95d, 0d)),
                        new LineString(
                                new Point(96d, 1d),
                                new Point(96d, 0.5d),
                                new Point(98d, 0.5d),
                                new Point(98d, 1d),
                                new Point(96d, 1d)),
                        new LineString(
                                new Point(96d, 3d),
                                new Point(98d, 3d),
                                new Point(98d, 3.5d),
                                new Point(96d, 3.5d),
                                new Point(96d, 3d)
                        )
                ))),
                FeatureTest.getGeometryCollectionFeature()
        )));

        assertFalse(fc.isEquivalentTo(GeometryCollectionTest.getGeometryCollection()));
        assertFalse(fc.isEquivalentTo(new FeatureCollection()));
        // missing the polygon
        assertFalse(fc.isEquivalentTo(new FeatureCollection(
                FeatureTest.getMultiLineStringFeature(),
                FeatureTest.getMultiPolygonFeature(),
                FeatureTest.getGeometryCollectionFeature()
        )));
        // one coordinate difference
        assertFalse(fc.isEquivalentTo(new FeatureCollection(
                FeatureTest.getMultiLineStringFeature(),
                new Feature(new Polygon(
                        new LineString(
                                new Point(100d, 0d),
                                new Point(104d, 0d),
                                new Point(104d, 4d),
                                new Point(100d, 4d),
                                new Point(100d, 0d)),
                        new LineString(
                                new Point(101d, 0.5d),
                                new Point(103d, 0.6d),
                                new Point(103d, 1d),
                                new Point(101d, 1d),
                                new Point(101d, 0.5d)),
                        new LineString(
                                new Point(101d, 3d),
                                new Point(103d, 3d),
                                new Point(103d, 3.5d),
                                new Point(101d, 3.5d),
                                new Point(101d, 3d)
                        )
                )),
                FeatureTest.getMultiPolygonFeature(),
                FeatureTest.getGeometryCollectionFeature()
        )));
    }

    @Test
    public void testToJson() throws Exception {
        assertEquals(VALID_FEATURE_COLLECTION, getFeatureCollection().toJson());
    }

    @Test
    public void testToJsonObject() throws Exception {
        JsonObject obj1 = getFeatureCollection().toJsonObject(null);
        JsonObject obj2 = getFeatureCollection().toJsonObject(new Gson());
        assertEquals(obj1.toString(), obj2.toString());
    }

    @Test
    public void testDecodeFeatureCollection() throws Exception {
        assertEquals(VALID_FEATURE_COLLECTION,
                FeatureCollection.decodeFeatureCollection(VALID_FEATURE_COLLECTION).toJson());
        assertEquals(EMPTY_FEATURES,
                FeatureCollection.decodeFeatureCollection(EMPTY_FEATURES).toJson());

        boolean gotException = false;

        try {
            FeatureCollection.decodeFeatureCollection(WRONG_TYPE);
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.NOT_OF_TYPE));
            gotException = true;
        }

        assertTrue(gotException);
        gotException = false;

        try {
            FeatureCollection.decodeFeatureCollection(NO_TYPE);
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.NOT_OF_TYPE));
            gotException = true;
        }

        assertTrue(gotException);
        gotException = false;

        try {
            FeatureCollection.decodeFeatureCollection(NOT_AN_OBJECT);
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.NOT_A_JSON_OBJECT));
            gotException = true;
        }

        assertTrue(gotException);
        gotException = false;

        try {
            FeatureCollection.decodeFeatureCollection(INVALID_INNER_TYPE);
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.COORDINATE_ARRAY_TOO_SHORT));
            gotException = true;
        }

        assertTrue(gotException);
        gotException = false;

        try {
            FeatureCollection.decodeFeatureCollection(NO_FEATURES);
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.FEATURES_KEY_NOT_FOUND));
            gotException = true;
        }

        assertTrue(gotException);
        gotException = false;

        try {
            FeatureCollection.decodeFeatureCollection(FEATURES_NOT_ARRAY);
        } catch (TerraformerException e) {
            assertTrue(e.getMessage().contains(TerraformerException.ELEMENT_NOT_ARRAY));
            gotException = true;
        }

        assertTrue(gotException);
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
