package com.esri.terraformer.formats;

import com.esri.terraformer.core.BaseGeometry;
import com.esri.terraformer.core.Feature;
import com.esri.terraformer.core.FeatureCollection;
import com.esri.terraformer.core.Geometry;
import com.esri.terraformer.core.GeometryCollection;
import com.esri.terraformer.core.LineString;
import com.esri.terraformer.core.MultiLineString;
import com.esri.terraformer.core.MultiPoint;
import com.esri.terraformer.core.MultiPolygon;
import com.esri.terraformer.core.Point;
import com.esri.terraformer.core.Polygon;
import com.esri.terraformer.core.Terraformer;
import com.esri.terraformer.core.TerraformerException;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.ArrayList;

/*
 * Resources:
 * ----------
 * http://resources.arcgis.com/en/help/rest/apiref/geometry.html
 * http://resources.arcgis.com/en/help/rest/apiref/feature.html
 *
 * GeoJSON              Esri JSON
 * ---------------------------------------
 * Point                Point
 * MultiPoint           Multipoint
 * LineString           Polyline
 * MultiLineString      Polyline
 * Polygon              Polygon
 * MultiPolygon         Polygon
 * Feature              Feature
 * FeatureCollection    (Array of Feature)
 * GeometryCollection   (Array of Geometry)
 */
public class EsriJson implements Terraformer.Decoder, Terraformer.Encoder {
    private static final String DECODE_ERROR_PREFIX = "Error while parsing Esri JSON: ";
    private static final int DEFAULT_SPATIAL_REFERENCE = 4326;
    private static final String DEFAULT_FEATURE_ID_KEY = "OBJECTID";

    private static final String KEY_X = "x";
    private static final String KEY_Y = "y";
    private static final String KEY_Z = "z";
    private static final String KEY_M = "m";
    private static final String KEY_HAS_M = "hasM";
    private static final String KEY_HAS_Z = "hasZ";
    private static final String KEY_SPATIAL_REFERENCE = "spatialReference";
    private static final String KEY_WKID = "wkid";
    private static final String KEY_POINTS = "points";
    private static final String KEY_RINGS = "rings";
    private static final String KEY_PATHS = "paths";
    private static final String KEY_GEOMETRY = "geometry";
    private static final String KEY_ATTRIBUTES = "attributes";

    private JsonObject spatialReference;
    private String featureIdKey;

    public EsriJson() {
        setSpatialReference(DEFAULT_SPATIAL_REFERENCE);
        setFeatureIdKey(DEFAULT_FEATURE_ID_KEY);
    }

    public EsriJson(JsonObject spatialReference) {
        this.spatialReference = spatialReference;
    }

    /** Specify the full spatial reference JSON object to be used for all Esri JSON output. */
    public void setSpatialReference(JsonObject spatialReference) {
        this.spatialReference = spatialReference;
    }

    /** Specify the WKID of a spatial reference to be used for all Esri JSON output. */
    public void setSpatialReference(int wkid) {
        JsonObject sr = new JsonObject();
        sr.add(KEY_WKID, new JsonPrimitive(wkid));
        setSpatialReference(sr);
    }

    /** Set the key name to be used when encoding and decoding GeoJSON Features with IDs. */
    public void setFeatureIdKey(String key) {
        featureIdKey = key;
    }

    @Override
    public BaseGeometry decode(String s) throws TerraformerException {
        return geometryFromJson(FormatUtils.getObject(s, DECODE_ERROR_PREFIX));
    }

    @Override
    public String encode(BaseGeometry geo) {
        return geometryToJson(geo).toString();
    }

    /** Create a Geometry from Json */
    private static BaseGeometry geometryFromJson(JsonObject g) throws TerraformerException {
        // infer type from keys present in g, then defer to the appropriate method.
        if (isPoint(g)) {
            return pointFromJson(g);
        } else if (isMultiPoint(g)) {
            return multiPointFromJson(g);
        } else if (isPolyline(g)) {
            return polyLineFromJson(g);
        } else if (isPolygon(g)) {
            return polygonFromJson(g);
        } else if (isFeature(g)) {
            return featureFromJson(g);
        } else {
            throw new TerraformerException(DECODE_ERROR_PREFIX, "Unable to determine geometry type.");
        }
    }

    /** Encode a Geometry to Esri JSON */
    private JsonElement geometryToJson(BaseGeometry geo) {
        JsonElement json;
        switch (geo.getType()) {
            case POINT:
                json = pointToJson((Point) geo);
                break;
            case MULTIPOINT:
                json = multiPointToJson((MultiPoint) geo);
                break;
            case LINESTRING:
                json = lineStringToJson((LineString) geo);
                break;
            case MULTILINESTRING:
                json = multiLineStringToJson((MultiLineString) geo);
                break;
            case POLYGON:
                json = polygonToJson((Polygon) geo);
                break;
            case MULTIPOLYGON:
                json = multiPolygonToJson((MultiPolygon) geo);
                break;
            case GEOMETRYCOLLECTION:
                json = geometryCollectionToJson((GeometryCollection) geo);
                break;
            case FEATURE:
                json = featureToJson((Feature) geo);
                break;
            case FEATURECOLLECTION:
                json = featureCollectionToJson((FeatureCollection) geo);
                break;
            default:
                json = new JsonObject();
        }

        return json;
    }

    /* --- Encode EsriJSON --- */

    /** Encode a Point to Esri JSON */
    private JsonObject pointToJson(Point p) {
        JsonObject o = new JsonObject();

        o.addProperty(KEY_X, p.getX());
        o.addProperty(KEY_Y, p.getY());
        if (p.size() > 2) {
            o.addProperty(KEY_Z, p.getZ());
        }
        if (p.size() > 3) {
            o.addProperty(KEY_M, p.get(3));
        }

        o.add(KEY_SPATIAL_REFERENCE, spatialReference);

        return o;
    }

    /** Encode a MultiPoint to Esri JSON */
    private JsonObject multiPointToJson(MultiPoint mp) {
        JsonObject o = makeJsonObject(mp.get(0).size());

        JsonArray points = new JsonArray();
        for (Point p : mp) {
            points.add(pointToArray(p));
        }
        o.add(KEY_POINTS, points);

        return o;
    }

    /**
     * Encode a LineString to Esri JSON.
     * LineStrings and MultiLineStrings have identical representation in Esri JSON.
     */
    private JsonObject lineStringToJson(LineString ls) {
        return multiLineStringToJson(new MultiLineString(ls));
    }

    /**
     * Encode a MultiLineString to Esri JSON
     * LineStrings and MultiLineStrings have identical representation in Esri JSON.
     */
    private JsonObject multiLineStringToJson(MultiLineString mls) {
        JsonObject o = makeJsonObject(mls.get(0).get(0).size());

        JsonArray paths = new JsonArray();
        for (LineString ls : mls) {
            JsonArray points = new JsonArray();
            for (Point p : ls) {
                points.add(pointToArray(p));
            }
            paths.add(points);
        }
        o.add(KEY_PATHS, paths);

        return o;
    }

    /**
     * Encode a Polygon to Esri JSON.
     * Polygons and MultiPolygons have identical representation in Esri JSON.
     */
    private JsonObject polygonToJson(Polygon p) {
        return multiPolygonToJson(new MultiPolygon(p));
    }

    /**
     * Encode a MultiPolygon to Esri JSON.
     * Polygons and MultiPolygons have identical representation in Esri JSON.
     */
    private JsonObject multiPolygonToJson(MultiPolygon mp) {
        JsonObject o = makeJsonObject(mp.get(0).getOuterRing().get(0).size());

        JsonArray rings = new JsonArray();
        for (Polygon p : mp) {
            // add oriented rings from all polygons in the multipolygon to the array
            rings.addAll(polygonToOrientedRings(p));
        }
        o.add(KEY_RINGS, rings);

        return o;
    }

    /**
     * Encode a GeometryCollection to Esri JSON.
     * Since there is no GeometryCollection type in Esri JSON, this method will return an array of
     * serialized geometries.
     */
    private JsonArray geometryCollectionToJson(GeometryCollection gc) {
        JsonArray collection = new JsonArray();

        for (Geometry g : gc) {
            collection.add(geometryToJson(g));
        }

        return collection;
    }

    /** Encode a Feature to Esri JSON */
    private JsonObject featureToJson(Feature f) {
        JsonObject o = new JsonObject();

        o.add(KEY_GEOMETRY, geometryToJson(f.getGeometry()));
        o.add(KEY_ATTRIBUTES, f.getProperties());

        if (f.getId() != null) {
            o.addProperty(DEFAULT_FEATURE_ID_KEY, f.getId());
        }

        return o;
    }

    /** Encode a FeatureCollection to Esri JSON */
    private JsonArray featureCollectionToJson(FeatureCollection fc) {
        JsonArray collection = new JsonArray();

        for (Feature f : fc) {
            collection.add(featureToJson(f));
        }

        return collection;
    }

    /* --- Decode EsriJSON --- */

    private static Point pointFromJson(JsonObject g) throws TerraformerException {
        ArrayList<Double> coords = new ArrayList<Double>();

        try {
            coords.add(g.get(KEY_X).getAsDouble());
            coords.add(g.get(KEY_Y).getAsDouble());

            if (g.has(KEY_Z)) {
                coords.add(g.get(KEY_Z).getAsDouble());
            }

            if (g.has(KEY_M)) {
                if (!g.has(KEY_Z)) {
                    coords.add(0.0); // z = null
                }
                coords.add(g.get(KEY_M).getAsDouble());
            }
        } catch (RuntimeException e) {
            throw new TerraformerException(DECODE_ERROR_PREFIX, "Unable to decode point.", e);
        }

        return new Point(coords);
    }

    private static MultiPoint multiPointFromJson(JsonObject g) {
        ArrayList<Point> points = new ArrayList<Point>();

        for (JsonElement p : g.getAsJsonArray(KEY_POINTS)) {
            points.add(pointFromCoordinates(g, p));
        }

        return new MultiPoint(points);
    }


    private static Geometry polyLineFromJson(JsonObject g) {
        MultiLineString mls = new MultiLineString();

        for (JsonElement path : g.getAsJsonArray(KEY_PATHS)) {
            LineString l = new LineString();
            for (JsonElement p : path.getAsJsonArray()) {
                l.add(pointFromCoordinates(g, p));
            }
            mls.add(l);
        }

        if (mls.size() == 1) {
            return mls.get(0);
        }

        return mls;
    }

    private static Geometry polygonFromJson(JsonObject g) {
        MultiPolygon outerRings = new MultiPolygon();
        MultiLineString holes = new MultiLineString();

        for (JsonElement ring : g.getAsJsonArray(KEY_RINGS)) {
           LineString r = new LineString();
            for (JsonElement p : ring.getAsJsonArray()) {
                r.add(pointFromCoordinates(g, p));
            }

            r.closeRing();
            if (r.size() < 4) {
                continue;
            }

            // r is an outer ring if it is clockwise, otherwise it is a hole
            if (ringIsClockwise(r)) {
                outerRings.add(new Polygon(r));
            } else {
                holes.add(r);
            }
        }

        for (LineString h : holes) {
            boolean contained = false;

            // loop over all outer rings and see if they contain the hole
            for (Polygon p : outerRings) {
                if (coordinatesContainCoordinates(p.getOuterRing(), h)) {
                    // the hole is contained. add it to our polygon
                    p.add(h);

                    contained = true;
                    break;
                }
            }

            // no outer rings contain this hole. add it to outer rings, since it can't be a hole!
            if (!contained) {
               outerRings.add(new Polygon(h));
            }
        }

        // return a Polygon or MultiPolygon depending on how many outer rings we are left with.
        if (outerRings.size() == 1) {
            return outerRings.get(0);
        } else {
            return outerRings;
        }
    }

    private static Feature featureFromJson(JsonObject g) throws TerraformerException {
        Geometry geometry;
        try {
            geometry = (Geometry) geometryFromJson(g.get(KEY_GEOMETRY).getAsJsonObject());
        } catch (TerraformerException e) {
            throw new TerraformerException("Error decoding EsriJSON feature", "Unable to parse 'geometry'.", e);
        }

        JsonObject attributes = g.get(KEY_ATTRIBUTES).getAsJsonObject();
        String id = null;
        if (g.has(DEFAULT_FEATURE_ID_KEY)) {
            id = g.getAsJsonPrimitive(DEFAULT_FEATURE_ID_KEY).getAsString();
        }

        return new Feature(id, geometry, attributes);
    }

    /* --- Helpers --- */

    private JsonObject makeJsonObject(Integer numCoords) {
        JsonObject o = new JsonObject();

        if (numCoords != null && numCoords > 2) {
            o.addProperty(KEY_HAS_Z, true);
        }
        if (numCoords != null && numCoords > 3) {
            o.addProperty(KEY_HAS_M, true);
        }

        o.add(KEY_SPATIAL_REFERENCE, spatialReference);

        return o;
    }

    private static JsonArray pointToArray(Point p) {
        JsonArray r = new JsonArray();
        for (double d : p) {
            r.add((new JsonPrimitive(d)));
        }
        return r;
    }

    private static JsonArray polygonToOrientedRings(Polygon input) {
        JsonArray rings = new JsonArray();

        for (int i = 0; i < input.size(); i++) {
            LineString r = input.get(i);

            // ignore invalid rings
            if (r.size() < 4) {
                continue;
            }

            // orient rings
            if (i == 0 && !ringIsClockwise(r)) {
                // make outer ring clockwise
                r.reverse();
            } else if (ringIsClockwise(r)) {
                // make holes counter clockwise
                r.reverse();
            }

            JsonArray ring = new JsonArray();
            for (Point p : r) {
                ring.add(pointToArray(p));
            }
            rings.add(ring);
        }

        return rings;
    }

    private static Point pointFromCoordinates(JsonObject g, JsonElement array) {
        JsonArray r = array.getAsJsonArray();

        ArrayList<Double> coords = new ArrayList<Double>();
        coords.add(r.get(0).getAsDouble());
        coords.add(r.get(1).getAsDouble());

        boolean hasZ = g.has(KEY_HAS_Z) && g.get(KEY_HAS_Z).getAsBoolean();
        boolean hasM = g.has(KEY_HAS_M) && g.get(KEY_HAS_M).getAsBoolean();

        if (hasZ) {
            coords.add(r.get(2).getAsDouble());
        }

        if (hasM) {
            if (!hasZ) {
                coords.add(0.0); // z = null
                coords.add(r.get(2).getAsDouble());
            } else {
                coords.add(r.get(3).getAsDouble());
            }
        }

        return new Point(coords);
    }

    /**
     * Determines whether or not the direction of the coordinates in a ring is clockwise using the Shoelace Formula.
     * http://en.wikipedia.org/wiki/Shoelace_formula
     **/
    private static boolean ringIsClockwise(LineString ring) {
        double total = 0;
        Point p1, p2;
        p1 = ring.get(0);
        for (int i = 0; i < ring.size()-1; i++) {
            p2 = ring.get(i+1);
            total += (p2.get(0) - p1.get(0)) * (p2.get(1) + p1.get(1));
            p1 = p2;
        }
        return (total >= 0);
    }

    private static boolean ringContainsPoint(LineString ring, Point p) {
        if (ring == null || p == null) {
            return false;
        }

        boolean returnVal = false;
        int l = ring.size();
        int j = l - 1;
        for (int i = -1; ++i < l; j = i) {
            double ring_i_x = ring.get(i).getX();
            double ring_i_y = ring.get(i).getY();
            double ring_j_x = ring.get(j).getX();
            double ring_j_y = ring.get(j).getY();
            double point_x = p.getX();
            double point_y = p.getY();

            if (((ring_i_y <= point_y && point_y < ring_j_y) ||
                 (ring_j_y <= point_y && point_y < ring_i_y)) &&
                (point_x < (ring_j_x - ring_i_x) * (point_y - ring_i_y) / (ring_j_y - ring_i_y) + ring_i_x)) {
                returnVal = !returnVal;
            }
        }

        return returnVal;
    }

    static boolean coordinatesContainCoordinates(LineString outer, LineString inner) {
        boolean intersects = lineStringsIntersect(outer, inner);
        boolean contains = ringContainsPoint(outer, inner.get(0));

        return (!intersects && contains);
    }

    private static boolean lineStringsIntersect(LineString a, LineString b) {
        for (int i = 0; i < a.size() - 1; i++) {
            for (int j = 0; j < b.size() - 1; j++) {
                if (lineLineIntersect(a.get(i), a.get(i + 1), b.get(j), b.get(j + 1))) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean lineLineIntersect(Point a1, Point a2, Point b1, Point b2) {
        double a1_x = a1.get(0);
        double a1_y = a1.get(1);
        double a2_x = a2.get(0);
        double a2_y = a2.get(1);
        double b1_x = b1.get(0);
        double b1_y = b1.get(1);
        double b2_x = b2.get(0);
        double b2_y = b2.get(1);

        // compute determinants
        double ua_t = (b2_x - b1_x) * (a1_y - b1_y) - (b2_y - b1_y) * (a1_x - b1_x);
        double ub_t = (a2_x - a1_x) * (a1_y - b1_y) - (a2_y - a1_y) * (a1_x - b1_x);
        double u_b  = (b2_y - b1_y) * (a2_x - a1_x) - (b2_x - b1_x) * (a2_y - a1_y);

        // if segments are not parallel
        if (u_b != 0) {
            double ua = ua_t / u_b;
            double ub = ub_t / u_b;

            // check for segment intersection only, not infinite line intersection
            return (0 <= ua && ua <= 1 && 0 <= ub && ub <= 1);
        }

        return false;
    }

    /* --- Type Detection --- */

    private static boolean isPoint(JsonObject g) {
        return hasAll(g, KEY_X, KEY_Y);
    }

    private static boolean isMultiPoint(JsonObject g) {
        return g.has(KEY_POINTS);
    }

    private static boolean isPolyline(JsonObject g) {
        return g.has(KEY_PATHS);
    }

    private static boolean isPolygon(JsonObject g) {
        return g.has(KEY_RINGS);
    }

    private static boolean isFeature(JsonObject g) {
        return hasAll(g, KEY_GEOMETRY, KEY_ATTRIBUTES);
    }

    private static boolean hasAll(JsonObject g, String... keys) {
        boolean hasAll = true;
        for (String k : keys) {
            if (!g.has(k)) {
                hasAll = false;
                break;
            }
        }
        return hasAll;
    }
}