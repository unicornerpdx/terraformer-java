package com.esri.terraformer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
public class EsriJson implements Terraformer.Serializer, Terraformer.Deserializer {

    @Override
    public String encode(BaseGeometry geo) {
        return "";
    }

    @Override
    public BaseGeometry decode(String s) throws TerraformerException {
        JsonObject g = new JsonParser().parse(s).getAsJsonObject();

        return decode(g);
    }

    public BaseGeometry decode(JsonObject g) throws TerraformerException {
        if (isPoint(g)) {
            return decodePoint(g);
        } else if (isMultiPoint(g)) {
            return decodeMultiPoint(g);
        } else if (isPolyline(g)) {
            return decodePolyline(g);
        } else if (isPolygon(g)) {
            return decodePolygon(g);
        } else if (isFeature(g)) {
            return decodeFeature(g);
        } else {
            // TODO: throw exception
            return null;
        }
    }

    /* --- Encode EsriJSON --- */
    // TODO: this

    /* --- Decode EsriJSON --- */

    private Point decodePoint(JsonObject g) throws TerraformerException {
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

    private MultiPoint decodeMultiPoint(JsonObject g) {
        ArrayList<Point> points = new ArrayList<Point>();

        for (JsonElement p : g.getAsJsonArray("points")) {
            points.add(getPointFromCoordinates(g, p));
        }

        return new MultiPoint(points);
    }

    private Geometry decodePolyline(JsonObject g) {
        MultiLineString mls = new MultiLineString();

        for (JsonElement path : g.getAsJsonArray("paths")) {
            LineString l = new LineString();
            for (JsonElement p : path.getAsJsonArray()) {
                l.add(getPointFromCoordinates(g, p));
            }
            mls.add(l);
        }

        if (mls.size() == 1) {
            return mls.get(0);
        }

        return mls;
    }

    private Geometry decodePolygon(JsonObject g) {
        MultiPolygon outerRings = new MultiPolygon();
        MultiLineString holes = new MultiLineString();

        for (JsonElement ring : g.getAsJsonArray("rings")) {
           LineString r = new LineString();
            for (JsonElement p : ring.getAsJsonArray()) {
                r.add(getPointFromCoordinates(g, p));
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

            //TODO: finish me

        }

        return new Polygon();
    }

    private Feature decodeFeature(JsonObject g) throws TerraformerException {
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
    private Point getPointFromCoordinates(JsonObject g, JsonElement array) {
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
    private Polygon orientRings(Polygon input) {
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

    private boolean ringIsClockwise(LineString ring) {
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

    /* --- Type Detection --- */

    private boolean isPoint(JsonObject g) {
        return hasAllKeys(g, "x", "y");
    }

    private boolean isMultiPoint(JsonObject g) {
        return g.has("points");
    }

    private boolean isPolyline(JsonObject g) {
        return g.has("paths");
    }

    private boolean isPolygon(JsonObject g) {
        return g.has("rings");
    }

    private boolean isFeature(JsonObject g) {
        return hasAllKeys(g, "geometry", "attributes");
    }

    private boolean hasAllKeys(JsonObject g, String... keys) {
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