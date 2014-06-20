package com.esri.terraformer;

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

    private JsonObject spatialReference;

    public EsriJson() {
        JsonObject sr = new JsonObject();
        sr.add("wkid", new JsonPrimitive(4326));
        spatialReference = sr;
    }

    public EsriJson(JsonObject spatialReference) {
        this.spatialReference = spatialReference;
    }

    @Override
    public BaseGeometry decode(String s) throws TerraformerException {
        return geometryFromJson(Terraformer.getObject(s, DECODE_ERROR_PREFIX));
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

        o.add("spatialReference", spatialReference);
        o.addProperty("x", p.getX());
        o.addProperty("y", p.getY());
        if (p.size() > 2) {
            o.addProperty("z", p.getZ());
            o.addProperty("hasZ", true);
        }
        if (p.size() > 3) {
            o.addProperty("m", p.get(3));
            o.addProperty("hasM", true);
        }

        return o;
    }

    /** Encode a MultiPoint to Esri JSON */
    private JsonObject multiPointToJson(MultiPoint mp) {
        JsonObject o = makeJsonObject(mp.get(0).size());

        JsonArray points = new JsonArray();
        for (Point p : mp) {
            points.add(pointToArray(p));
        }
        o.add("points", points);

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
        o.add("paths", paths);

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
        o.add("rings", rings);

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

        o.add("geometry", geometryToJson(f.getGeometry()));
        o.add("attributes", f.getProperties());

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
            coords.add(g.get("x").getAsDouble());
            coords.add(g.get("y").getAsDouble());

            boolean hasZ = g.has("hasZ") && g.get("hasZ").getAsBoolean();
            boolean hasM = g.has("hasM") && g.get("hasM").getAsBoolean();

            if (hasZ) {
                coords.add(g.get("z").getAsDouble());
            }

            if (hasM) {
                if (!hasZ) {
                    coords.add(0.0); // z = null
                }
                coords.add(g.get("m").getAsDouble());
            }
        } catch (RuntimeException e) {
            throw new TerraformerException(DECODE_ERROR_PREFIX, "Unable to decode point.");
        }

        return new Point(coords);
    }

    private static MultiPoint multiPointFromJson(JsonObject g) {
        ArrayList<Point> points = new ArrayList<Point>();

        for (JsonElement p : g.getAsJsonArray("points")) {
            points.add(pointFromCoordinates(g, p));
        }

        return new MultiPoint(points);
    }


    private static Geometry polyLineFromJson(JsonObject g) {
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

    private static Geometry polygonFromJson(JsonObject g) {
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

    private static Feature featureFromJson(JsonObject g) throws TerraformerException {
        Geometry geometry;
        try {
            geometry = (Geometry) geometryFromJson(g.get("geometry").getAsJsonObject());
        } catch (TerraformerException e) {
            throw new TerraformerException("Error decoding EsriJSON feature", "Unable to parse 'geometry'.");
        }

        JsonObject attributes = g.get("attributes").getAsJsonObject();

        return new Feature(geometry, attributes);
    }

    /* --- Helpers --- */

    private JsonObject makeJsonObject(Integer numCoords) {
        JsonObject o = new JsonObject();

        if (numCoords != null) {
            o.addProperty("hasZ", numCoords > 2);
            o.addProperty("hasM", numCoords > 3);
        }

        o.add("spatialReference", spatialReference);

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

    // TODO: make sure everything has 'hasZ' and 'hasM'
    private static Point pointFromCoordinates(JsonObject g, JsonElement array) {
        JsonArray r = array.getAsJsonArray();

        ArrayList<Double> coords = new ArrayList<Double>();
        coords.add(r.get(0).getAsDouble());
        coords.add(r.get(1).getAsDouble());

        boolean hasZ = g.has("hasZ") && g.get("hasZ").getAsBoolean();
        boolean hasM = g.has("hasM") && g.get("hasM").getAsBoolean();

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

    private static boolean ringIsClockwise(LineString ring) {
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

    private static boolean ringContainsPoint(LineString ring, Point p) {
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
        return hasAll(g, "x", "y");
    }

    private static boolean isMultiPoint(JsonObject g) {
        return g.has("points");
    }

    private static boolean isPolyline(JsonObject g) {
        return g.has("paths");
    }

    private static boolean isPolygon(JsonObject g) {
        return g.has("rings");
    }

    private static boolean isFeature(JsonObject g) {
        return hasAll(g, "geometry", "attributes");
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