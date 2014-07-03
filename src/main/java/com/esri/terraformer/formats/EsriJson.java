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
            if (i == 0) {
                // make outer ring clockwise
                if (!ringIsClockwise(r)) {
                    r.reverse();
                }
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

    static boolean ringContainsPoint(LineString ring, Point p) {
        if (ring == null || p == null) {
            return false;
        }

        // Ray casting algorithm to determine if the point is inside the
        // ring. For each segment with the coordinates a and b, check to see if
        // point.y is within a.y and b.y. If so, check to see if the point is
        // to the left of the edge. If this is also true, a line drawn from the
        // point to the right will intersect the edge-- if the line intersects
        // the polygon an odd number of times, it is inside.

        // If an edge is horizontal it will not pass the checkY test. This is
        // important, since otherwise you run the risk of dividing by zero in
        // the horizontal check.

        // This stackoverflow answer explains it nicely: http://stackoverflow.com/a/218081/52561
        // This is good too: http://geomalgorithms.com/a03-_inclusion.html

        boolean contains = false;
        int nvert = ring.size();
        for (int i = 0, j = nvert - 1; i < nvert; j = i++) {
            Point a = ring.get(i);
            Point b = ring.get(j);

            boolean checkY = ((a.getY() >= p.getY()) != (b.getY() >= p.getY()));
            boolean checkX = (p.getX() <= (b.getX() - a.getX()) * (p.getY() - a.getY()) / (b.getY() - a.getY()) + a.getX());

            if (checkY && checkX) {
                contains = !contains;
            }
        }

        return contains;
    }

    static boolean coordinatesContainCoordinates(LineString outer, LineString inner) {
        boolean intersects = lineStringsIntersect(outer, inner);
        boolean contains = ringContainsPoint(outer, inner.get(0));

        return (!intersects && contains);
    }

    /**
     * Determines whether two lineStrings intersect
     * @param lineString a LineString
     * @param other another LineString
     * @return true if the given LineStrings intersect, false otherwise.
     */
    static boolean lineStringsIntersect(LineString lineString, LineString other) {
        // See: http://geomalgorithms.com/a05-_intersect-1.html for detailed explanation of this algorithm.
        for (int i = 0; i < lineString.size() - 1; i++) {
            Point a1 = lineString.get(i);
            Point a2 = lineString.get(i+1);
            LineString a = new LineString(a1, a2);

            double[] aVector = new double[] { a2.getX() - a1.getX(), a2.getY() - a1.getY() };
            boolean aIsPoint = (aVector[0] == 0 && aVector[1] == 0);

            for (int j = 0; j < other.size() - 1; j++) {
                Point b1 = other.get(j);
                Point b2 = other.get(j+1);
                LineString b = new LineString(b1, b2);

                double[] bVector = new double[] { b2.getX() - b1.getX(), b2.getY() - b1.getY() };
                boolean bIsPoint = bVector[0] == 0 && bVector[1] == 0;

                double[] abVector = new double[] { a1.getX() - b1.getX(), a1.getY() - b1.getY() };

                // Determine if a and b are parallel. They are parallel if they are both perpendicular to the same vector,
                // which can be boiled down to checking if the difference of the perp products of the two vectors is equal
                // to 0.
                boolean parallel = (Math.abs(aVector[0] * bVector[1] - aVector[1] * bVector[0]) <= 0.0000001);
                if (parallel) {
                    if (aVector[0] * abVector[1] - aVector[1] * abVector[0] != 0 || bVector[0] * abVector[1] - bVector[1] * abVector[0] != 0) {
                        // parallel but not collinear, intersection not possible.
                        continue;
                    }

                    // if both segments are points, they can only intersect if they are equivalent
                    if (aIsPoint && bIsPoint) {
                        if (a1.isEquivalentTo(b1)) {
                            return true;
                        }
                        continue;
                    }

                    // If only one segment is a point check whether it lies on the other line segment. Note that at this
                    // point we know they are collinear, so we only need to check a single dimension.
                    if (aIsPoint) {
                        if (b1.getX() != b2.getX()) {
                            // not vertical, use x
                            if (a1.getX() >= b1.getX() && a1.getX() <= b2.getX()) {
                                return true;
                            }
                            if (a1.getX() <= b1.getX() && a1.getX() >= b2.getX()) {
                                return true;
                            }
                        } else {
                            // vertical, use y
                            if (a1.getY() >= b1.getY() && a1.getY() <= b2.getY()) {
                                return true;
                            }
                            if (a1.getY() <= b1.getY() && a1.getY() >= b2.getY()) {
                                return true;
                            }
                        }

                        // No intersection here
                        continue;
                    }
                    if (bIsPoint) {
                        if (a1.getX() != a2.getX()) {
                            // not vertical, use x
                            if (b1.getX() >= a1.getX() && b1.getX() <= a2.getX()) {
                                return true;
                            }
                            if (b1.getX() <= a1.getX() && b1.getX() >= a2.getX()) {
                                return true;
                            }
                        } else {
                            // vertical, use y
                            if (b1.getY() >= a1.getY() && b1.getY() <= a2.getY()) {
                                return true;
                            }
                            if (b1.getY() <= a1.getY() && b1.getY() >= a2.getY()) {
                                return true;
                            }
                        }

                        // No intersection here
                        continue;
                    }

                    // Segments are parallel and collinear and both have a length > 0, do they intersect?
                    if (a1.getX() != a2.getX()) {
                        // not vertical, use x
                        if ((a1.getX() >= b1.getX() && a1.getX() <= b2.getX()) || a1.getX() <= b1.getX() && a1.getX() >= b2.getX()) {
                            return true;
                        }
                        if ((a2.getX() >= b1.getX() && a2.getX() <= b2.getX()) || a2.getX() <= b1.getX() && a2.getX() >= b2.getX()) {
                            return true;
                        }
                    } else {
                        // vertical, use y
                        if ((a1.getY() >= b1.getY() && a1.getY() <= b2.getY()) || a1.getY() <= b1.getY() && a1.getX() >= b2.getY()) {
                            return true;
                        }
                        if ((a2.getY() >= b1.getY() && a2.getY() <= b2.getY()) || a1.getY() <= b1.getY() && a1.getY() >= b2.getY()) {
                            return true;
                        }
                    }

                    // These two segments are parallel and collinear but not intersecting... next!
                    continue;
                }

                // At this point we have 2 non-parallel lines. Get the direction vector for the difference between their
                // first points, which is used to calculate the distance from those points along their corresponding line
                // at which the intersection occurs. This distance is presented as a ratio of the line segment's length,
                // so if that distance is between 0 and 1, the intersection happens on that line segment. Therefore both
                // the a and the b intersection distance ratio must be between 0 and 1 for this to be a valid intersection.

                // See the Non-Parallel Lines section in the link above for a detailed explanation.
                double aIntersectionDistance = (bVector[1] * abVector[0] - bVector[0] * abVector[1]) /
                        (bVector[0] * aVector[1] - bVector[1] * aVector[0]);
                double bIntersectionDistance = (aVector[0] * abVector[1] - aVector[1] * abVector[0]) /
                        (aVector[0] * bVector[1] - aVector[1] * bVector[0]);

                if (aIntersectionDistance >= 0 && aIntersectionDistance <= 1 &&
                        bIntersectionDistance >= 0 && bIntersectionDistance <= 1) {
                    return true;
                }
            }
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