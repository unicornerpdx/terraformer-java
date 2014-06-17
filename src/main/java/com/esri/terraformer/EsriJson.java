package com.esri.terraformer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;

/**
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
 */
public class EsriJson implements Terraformer.Decoder, Terraformer.Encoder {
    static final String ESRIJSON_ERROR_PREFIX = "Error while parsing Esri JSON: ";

    @Override
    public String encode(BaseGeometry geo) {
        return "";
    }

    @Override
    public BaseGeometry decode(String s) throws TerraformerException {
        return decode(Terraformer.getObject(s, ESRIJSON_ERROR_PREFIX));
    }

    public static BaseGeometry decode(JsonObject g) throws TerraformerException {
        if (isPoint(g)) {
            return pointFromJsonObject(g);
        } else if (isMultiPoint(g)) {
            return multiPointFromJsonObject(g);
        } else if (isPolyline(g)) {
            return polyLineFromJsonObject(g);
        } else if (isPolygon(g)) {
            return polygonFromJsonObject(g);
        } else if (isFeature(g)) {
            return featureFromJsonObject(g);
        } else {
            // TODO: throw exception
            return null;
        }
    }

    /* --- Encode EsriJSON --- */
    // TODO: this

    /* --- Decode EsriJSON --- */

    public static Point decodePoint(String pointJSON) throws TerraformerException {
        if (Terraformer.isEmpty(pointJSON)) {
            throw new IllegalArgumentException(TerraformerException.JSON_STRING_EMPTY);
        }

        String ep = Point.ERROR_PREFIX;

        JsonObject object = Terraformer.getObject(pointJSON, ep);
        if (!isPoint(object)) {
            throw new TerraformerException(ep, TerraformerException.NOT_OF_TYPE + "Point");
        }

        return pointFromJsonObject(object);
    }

    static Point pointFromJsonObject(JsonObject g) throws TerraformerException {
        ArrayList<Double> coords = new ArrayList<Double>();

        coords.add(g.get("x").getAsDouble());
        coords.add(g.get("y").getAsDouble());

        boolean hasZ = g.get("z").getAsBoolean();
        if (hasZ) {
            coords.add(g.get("z").getAsDouble());
        }
        if (g.has("m")) {
            if (!hasZ) {
                coords.add(0.0); // z = null
            }
            coords.add(g.get("m").getAsDouble());
        }

        return new Point(coords);
    }

    public static MultiPoint decodeMultiPoint(String multiPointJSON) throws TerraformerException {
        if (Terraformer.isEmpty(multiPointJSON)) {
            throw new IllegalArgumentException(TerraformerException.JSON_STRING_EMPTY);
        }

        String ep = MultiPoint.ERROR_PREFIX;

        JsonObject object = Terraformer.getObject(multiPointJSON, ep);
        if (!isMultiPoint(object)) {
            throw new TerraformerException(ep, TerraformerException.NOT_OF_TYPE + "MultiPoint");
        }

        return multiPointFromJsonObject(object);
    }

    static MultiPoint multiPointFromJsonObject(JsonObject g) {
        ArrayList<Point> points = new ArrayList<Point>();

        for (JsonElement p : g.getAsJsonArray("points")) {
            points.add(pointFromCoordinates(g, p));
        }

        return new MultiPoint(points);
    }

    public static Geometry decodePolyLine(String polyLineJSON) throws TerraformerException {
        if (Terraformer.isEmpty(polyLineJSON)) {
            throw new IllegalArgumentException(TerraformerException.JSON_STRING_EMPTY);
        }

        String ep = LineString.ERROR_PREFIX;

        JsonObject object = Terraformer.getObject(polyLineJSON, ep);
        if (!isPolyline(object)) {
            throw new TerraformerException(ep, TerraformerException.NOT_OF_TYPE + "LineString or MultiLineString");
        }

        return polyLineFromJsonObject(object);
    }

    static Geometry polyLineFromJsonObject(JsonObject g) {
        MultiLineString mls = new MultiLineString();

        for (JsonElement path : g.getAsJsonArray("paths")) {
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

    public static Geometry decodePolygon(String polygonJSON) throws TerraformerException {
        if (Terraformer.isEmpty(polygonJSON)) {
            throw new IllegalArgumentException(TerraformerException.JSON_STRING_EMPTY);
        }

        String ep = Polygon.ERROR_PREFIX;

        JsonObject object = Terraformer.getObject(polygonJSON, ep);
        if (!isPolygon(object)) {
            throw new TerraformerException(ep, TerraformerException.NOT_OF_TYPE + "Polygon or MultiPolygon");
        }

        return polygonFromJsonObject(object);
    }

    static Geometry polygonFromJsonObject(JsonObject g) {
        MultiPolygon outerRings = new MultiPolygon();
        MultiLineString holes = new MultiLineString();

        for (JsonElement ring : g.getAsJsonArray("rings")) {
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

            // no outer rings contain this hole.
            // reverse the direction and add it to outer rings, since it can't be a hole!
            if (!contained) {
               h.reverse();
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

    private static boolean coordinatesContainCoordinates(LineString outer, LineString inner) {
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

    private static boolean lineStringsIntersect(MultiLineString a, LineString b) {
        for (LineString l : a) {
            if (lineStringsIntersect(l, b)) {
                return true;
            }
        }
        return false;
    }

    private static boolean lineStringsIntersect(LineString a, MultiLineString b) {
        for (LineString l : b) {
            if (lineStringsIntersect(a, l)) {
                return true;
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

    public static Feature decodeFeature(String featureJSON) throws TerraformerException {
        if (Terraformer.isEmpty(featureJSON)) {
            throw new IllegalArgumentException(TerraformerException.JSON_STRING_EMPTY);
        }

        String ep = Feature.ERROR_PREFIX;

        JsonObject object = Terraformer.getObject(featureJSON, ep);
        if (!isFeature(object)) {
            throw new TerraformerException(ep, TerraformerException.NOT_OF_TYPE + "Feature");
        }

        return featureFromJsonObject(object);
    }

    static Feature featureFromJsonObject(JsonObject g) throws TerraformerException {
        Geometry geometry = null;
        try {
            geometry = (Geometry) decode(g.get("geometry").getAsJsonObject());
        } catch (TerraformerException e) {
            throw new TerraformerException("Error decoding EsriJSON feature", "Unable to parse 'geometry'.");
        }

        JsonObject attributes = g.get("attributes").getAsJsonObject();

        return new Feature(geometry, attributes);
    }

    // TODO: make sure everything has 'hasZ' and 'hasM'
    static Point pointFromCoordinates(JsonObject g, JsonElement array) {
        JsonArray r = array.getAsJsonArray();

        ArrayList<Double> coords = new ArrayList<Double>();
        coords.add(r.get(0).getAsDouble());
        coords.add(r.get(1).getAsDouble());

        boolean hasZ = g.get("hasZ").getAsBoolean();
        if (hasZ) {
            coords.add(r.get(2).getAsDouble());
        }

        if (g.get("hasM").getAsBoolean()) {
            if (!hasZ) {
                coords.add(0.0); // z = null
            }
            coords.add(r.get(3).getAsDouble());
        }

        return new Point(coords);
    }

    /**
     * Ensures that rings are oriented in the right directions.
     * Outer rings must be clockwise, holes counterclockwise.
     * @param input
     * @return
     */
    static Polygon orientRings(Polygon input) {
        Polygon output = new Polygon();

        LineString outerRing = input.getOuterRing();
        if (outerRing.size() >= 4) {
            if (!ringIsClockwise(outerRing)) {
                outerRing.reverse();
            }
        }

        output.add(outerRing);

        for (LineString hole : input.getHoles()) {
            hole.closeRing();
            if (hole.size() >= 4) {
                if (ringIsClockwise(hole)) {
                    hole.reverse();
                }
                output.add(hole);
            }
        }

        return output;
    }

    static boolean ringIsClockwise(LineString ring) {
        int total = 0;
        Point p1, p2;
        p1 = ring.get(0);
        for (int i = 0; i < ring.size()-1; i++) {
            p2 = ring.get(i+1);
            total += (p2.get(0) - p1.get(0)) * (p2.get(1) + p1.get(1));
            p1 = p2;
        }
        return (total >= 0);
    }

    static boolean polygonContainsPoint(Polygon pg, Point p) {
        if (pg == null || p == null) {
            return false;
        }

        if (pg.size() == 1) {
            return ringContainsPoint(pg.get(0), p);
        }

        if (ringContainsPoint(pg.get(0), p)) {
            for (int i = 1; i < pg.size(); i++) {
                if (ringContainsPoint(pg.get(i), p)) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    static boolean ringContainsPoint(LineString ring, Point p) {
        if (ring == null || p == null) {
            return false;
        }

        boolean returnVal = false;
        int l = ring.size();
        int j = l - 1;
        for (int i = -1; ++i < l; j = i) {
            if (((ring.get(i).get(1) <= p.get(1) && p.get(1) < ring.get(j).get(1)) ||
                    (ring.get(j).get(1) <= p.get(1) && p.get(1) < ring.get(i).get(1))) &&
                    (p.get(0) < (ring.get(j).get(0) - ring.get(i).get(0)) * (p.get(1) - ring.get(i).get(1)) / (ring.get(j).get(1) - ring.get(i).get(1)) + ring.get(i).get(0))) {
                returnVal = !returnVal;
            }
        }

        return returnVal;
    }

    /* --- Type Detection --- */

    static boolean isPoint(JsonObject g) {
        return hasAllKeys(g, "x", "y");
    }

    static boolean isMultiPoint(JsonObject g) {
        return g.has("points");
    }

    static boolean isPolyline(JsonObject g) {
        return g.has("paths");
    }

    static boolean isPolygon(JsonObject g) {
        return g.has("rings");
    }

    static boolean isFeature(JsonObject g) {
        return hasAllKeys(g, "geometry", "attributes");
    }

    static boolean hasAllKeys(JsonObject g, String... keys) {
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