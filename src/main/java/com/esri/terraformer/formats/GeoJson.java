package com.esri.terraformer.formats;

import com.esri.terraformer.core.BaseGeometry;
import com.esri.terraformer.core.Feature;
import com.esri.terraformer.core.FeatureCollection;
import com.esri.terraformer.core.Geometry;
import com.esri.terraformer.core.GeometryCollection;
import com.esri.terraformer.core.GeometryType;
import com.esri.terraformer.core.LineString;
import com.esri.terraformer.core.MultiLineString;
import com.esri.terraformer.core.MultiPoint;
import com.esri.terraformer.core.MultiPolygon;
import com.esri.terraformer.core.Point;
import com.esri.terraformer.core.Polygon;
import com.esri.terraformer.core.Terraformer;
import com.esri.terraformer.core.TerraformerException;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class GeoJson implements Terraformer.Encoder, Terraformer.Decoder {
    public static final String GEOJSON_ERROR_PREFIX = "Error while parsing GeoJson: ";
    public static final String TYPE_KEY = "type";
    public static final String COORDINATES_KEY = "coordinates";
    public static final String GEOMETRIES_KEY = "geometries";
    public static final String GEOMETRY_KEY = "geometry";
    public static final String PROPERTIES_KEY = "properties";
    public static final String FEATURES_KEY = "features";

    @Override
    public BaseGeometry decode(String json) throws TerraformerException {
        return fromJson(json, GEOJSON_ERROR_PREFIX);
    }

    @Override
    public String encode(BaseGeometry geo) {
        return toJson(geo);
    }

    public static BaseGeometry<?> fromJson(String json, String errorPrefix) throws TerraformerException {
        return fromJsonObject(FormatUtils.getElement(json, errorPrefix), errorPrefix);
    }

    public static String toJson(BaseGeometry geo) {
        if (geo == null) {
            return null;
        }

        JsonObject obj = null;
        switch (geo.getType()) {
            case POINT:
            case MULTIPOINT:
            case LINESTRING:
            case MULTILINESTRING:
            case POLYGON:
            case MULTIPOLYGON:
            case GEOMETRYCOLLECTION:
                obj = geometryToJsonObject((Geometry)geo);
                break;
            case FEATURE:
                obj = featureToJsonObject((Feature)geo);
                break;
            case FEATURECOLLECTION:
                obj = featureCollectionToJsonObject((FeatureCollection)geo);
                break;
        }

        return new Gson().toJson(obj);
    }

    static BaseGeometry<?> fromJsonObject(JsonElement gjElem, String errorPrefix) throws TerraformerException {
        JsonObject gjObject = FormatUtils.objectFromElement(gjElem, errorPrefix);

        GeometryType type = getType(gjObject);
        if (type == null) {
            throw new TerraformerException(errorPrefix, TerraformerException.ELEMENT_UNKNOWN_TYPE);
        }

        BaseGeometry<?> geo = null;
        switch (type) {
            case POINT:
                geo = pointFromJsonObject(gjObject, Point.ERROR_PREFIX);
                break;
            case MULTIPOINT:
                geo = multiPointFromJsonObject(gjObject, MultiPoint.ERROR_PREFIX);
                break;
            case LINESTRING:
                geo = lineStringFromJsonObject(gjObject, LineString.ERROR_PREFIX);
                break;
            case MULTILINESTRING:
                geo = multiLineStringFromJsonObject(gjObject, MultiLineString.ERROR_PREFIX);
                break;
            case POLYGON:
                geo = polygonFromJsonObject(gjObject, Polygon.ERROR_PREFIX);
                break;
            case MULTIPOLYGON:
                geo = multiPolygonFromJsonObject(gjObject, MultiPolygon.ERROR_PREFIX);
                break;
            case GEOMETRYCOLLECTION:
                geo = geometryCollectionFromJsonObject(gjObject, GeometryCollection.ERROR_PREFIX);
                break;
            case FEATURE:
                geo = featureFromJsonObject(gjObject, Feature.ERROR_PREFIX);
                break;
            case FEATURECOLLECTION:
                geo = featureCollectionFromJsonObject(gjObject, FeatureCollection.ERROR_PREFIX);
                break;
        }

        return geo;
    }

    public static FeatureCollection decodeFeatureCollection(String featureCollectionJSON)
            throws TerraformerException {
        if (FormatUtils.isEmpty(featureCollectionJSON)) {
            throw new IllegalArgumentException(TerraformerException.JSON_STRING_EMPTY);
        }

        String ep = FeatureCollection.ERROR_PREFIX;

        JsonObject object = FormatUtils.getObject(featureCollectionJSON, ep);
        if (!(getType(object) == GeometryType.FEATURECOLLECTION)) {
            throw new TerraformerException(ep, TerraformerException.NOT_OF_TYPE + "\"FeatureCollection\"");
        }

        return featureCollectionFromJsonObject(object, ep);
    }

    static FeatureCollection featureCollectionFromJsonObject(JsonObject object, String errorPrefix) throws TerraformerException {
        // assume the type has already been checked
        JsonElement featuresElem = object.get(FEATURES_KEY);

        if (featuresElem == null) {
            throw new TerraformerException(errorPrefix, TerraformerException.FEATURES_KEY_NOT_FOUND);
        }

        JsonArray features = FormatUtils.arrayFromElement(featuresElem, errorPrefix);

        FeatureCollection returnVal = new FeatureCollection();
        for (JsonElement elem : features) {
            returnVal.add(featureFromObjectElement(elem, errorPrefix));
        }

        return returnVal;
    }

    static JsonObject featureCollectionToJsonObject(FeatureCollection fc) {
        JsonObject object = new JsonObject();
        object.addProperty(TYPE_KEY, fc.getType().toString());

        JsonArray features = new JsonArray();

        for (Feature feat : fc) {
            if (feat != null) {
                features.add(featureToJsonObject(feat));
            }
        }

        object.add(FEATURES_KEY, features);

        return object;
    }

    public static Feature decodeFeature(String featureJSON) throws TerraformerException {
        if (FormatUtils.isEmpty(featureJSON)) {
            throw new IllegalArgumentException(TerraformerException.JSON_STRING_EMPTY);
        }

        String ep = Feature.ERROR_PREFIX;

        JsonObject object = FormatUtils.getObject(featureJSON, ep);
        if (!(getType(object) == GeometryType.FEATURE)) {
            throw new TerraformerException(ep, TerraformerException.NOT_OF_TYPE + "\"Feature\"");
        }

        return featureFromJsonObject(object, ep);
    }

    static Feature featureFromJsonObject(JsonObject object, String errorPrefix) throws TerraformerException {
        // assume the type has already been checked
        JsonElement geomElement = object.get(GEOMETRY_KEY);

        if (geomElement == null) {
            throw new TerraformerException(errorPrefix, TerraformerException.GEOMETRY_KEY_NOT_FOUND);
        }

        JsonElement propsElem = object.get(PROPERTIES_KEY);
        JsonObject propsObj = null;

        if (propsElem != null) {
            try {
                propsObj = propsElem.getAsJsonObject();
            } catch (RuntimeException e) {
                throw new TerraformerException(errorPrefix, TerraformerException.PROPERTIES_NOT_OBJECT);
            }
        }

        JsonObject tempObj;
        try {
            tempObj = geomElement.getAsJsonObject();
        } catch (RuntimeException e) {
            throw new TerraformerException(errorPrefix, TerraformerException.ELEMENT_NOT_OBJECT);
        }

        Feature returnVal = new Feature();
        if (tempObj.entrySet().size() > 0) {
            returnVal.add(geometryFromObjectElement(geomElement, errorPrefix));
        }

        if (propsObj != null) {
            returnVal.setProperties(propsObj);
        }

        return returnVal;
    }

    static Feature featureFromObjectElement(JsonElement featureElem, String errorPrefix) throws TerraformerException {
        BaseGeometry<?> geoJson = fromJsonObject(featureElem, errorPrefix);
        if (!(geoJson instanceof Feature)) {
            throw new TerraformerException(errorPrefix, TerraformerException.ELEMENT_NOT_FEATURE);
        }

        return (Feature) geoJson;
    }

    static JsonObject featureToJsonObject(Feature feature) {
        JsonObject object = new JsonObject();
        object.addProperty(TYPE_KEY, feature.getType().toString());

        JsonObject geometry = new JsonObject();
        Geometry<?> geomObj = feature.get();
        if (geomObj != null) {
            geometry = geometryToJsonObject(geomObj);
        }
        object.add(GEOMETRY_KEY, geometry);

        if (feature.getProperties() != null) {
            object.add(PROPERTIES_KEY, feature.getProperties());
        }

        return object;
    }

    public static GeometryCollection decodeGeometryCollection(String geometryCollectionJSON)
            throws TerraformerException {
        if (FormatUtils.isEmpty(geometryCollectionJSON)) {
            throw new IllegalArgumentException(TerraformerException.JSON_STRING_EMPTY);
        }

        String ep = GeometryCollection.ERROR_PREFIX;

        JsonObject object = FormatUtils.getObject(geometryCollectionJSON, ep);
        if (!(getType(object) == GeometryType.GEOMETRYCOLLECTION)) {
            throw new TerraformerException(ep, TerraformerException.NOT_OF_TYPE + "\"GeometryCollection\"");
        }

        return geometryCollectionFromJsonObject(object, ep);
    }

    static GeometryCollection geometryCollectionFromJsonObject(JsonObject object, String errorPrefix) throws TerraformerException {
        // assume the type has already been checked
        JsonElement geomsElem = object.get(GEOMETRIES_KEY);

        if (geomsElem == null) {
            throw new TerraformerException(errorPrefix, TerraformerException.GEOMETRIES_KEY_NOT_FOUND);
        }

        JsonArray geoms = FormatUtils.arrayFromElement(geomsElem, errorPrefix);

        GeometryCollection returnVal = new GeometryCollection();
        for (JsonElement elem : geoms) {
            returnVal.add(geometryFromObjectElement(elem, errorPrefix));
        }

        return returnVal;
    }

    static Geometry<?> geometryFromObjectElement(JsonElement geomElem, String errorPrefix) throws TerraformerException {
        BaseGeometry<?> geoJson = fromJsonObject(geomElem, errorPrefix);
        if (!(geoJson instanceof Geometry<?>)) {
            throw new TerraformerException(errorPrefix, TerraformerException.ELEMENT_NOT_GEOMETRY);
        }

        return (Geometry<?>) geoJson;
    }

    public static MultiPolygon decodeMultiPolygon(String multiPolygonJSON) throws TerraformerException {
        if (FormatUtils.isEmpty(multiPolygonJSON)) {
            throw new IllegalArgumentException(TerraformerException.JSON_STRING_EMPTY);
        }

        String ep = MultiPolygon.ERROR_PREFIX;

        JsonObject object = FormatUtils.getObject(multiPolygonJSON, ep);
        if (!(getType(object) == GeometryType.MULTIPOLYGON)) {
            throw new TerraformerException(ep, TerraformerException.NOT_OF_TYPE + "\"MultiPolygon\"");
        }

        return multiPolygonFromJsonObject(object, ep);
    }

    static MultiPolygon multiPolygonFromJsonObject(JsonObject object, String errorPrefix) throws TerraformerException {
        // assume the type has already been checked
        JsonArray coords = getCoordinateArray(getCoordinates(object, errorPrefix), 0, errorPrefix);

        MultiPolygon returnVal = new MultiPolygon();
        for (JsonElement elem : coords) {
            returnVal.add(polygonFromCoordinates(elem, errorPrefix));
        }

        return returnVal;
    }

    public static Polygon decodePolygon(String polygonJSON) throws TerraformerException {
        if (FormatUtils.isEmpty(polygonJSON)) {
            throw new IllegalArgumentException(TerraformerException.JSON_STRING_EMPTY);
        }

        String ep = Polygon.ERROR_PREFIX;

        JsonObject object = FormatUtils.getObject(polygonJSON, ep);
        if (!(getType(object) == GeometryType.POLYGON)) {
            throw new TerraformerException(ep, TerraformerException.NOT_OF_TYPE + "\"Polygon\"");
        }

        return polygonFromJsonObject(object, ep);
    }

    static Polygon polygonFromJsonObject(JsonObject object, String errorPrefix) throws TerraformerException {
        // assume the type has already been checked
        return polygonFromCoordinates(getCoordinates(object, errorPrefix), errorPrefix);
    }

    static Polygon polygonFromCoordinates(JsonElement coordsElem, String errorPrefix) throws TerraformerException {
        JsonArray coords = getCoordinateArray(coordsElem, 0, errorPrefix);

        Polygon returnVal = new Polygon();
        for (JsonElement elem : coords) {
            LineString lr = lineStringFromCoordinates(elem, errorPrefix);

            if (!lr.isLinearRing()) {
                throw new TerraformerException(errorPrefix, TerraformerException.INNER_LINESTRING_NOT_RING);
            }

            returnVal.add(lr);
        }

        return returnVal;
    }

    public static MultiLineString decodeMultiLineString(String multiLineStringJSON) throws TerraformerException {
        if (FormatUtils.isEmpty(multiLineStringJSON)) {
            throw new IllegalArgumentException(TerraformerException.JSON_STRING_EMPTY);
        }

        String ep = MultiLineString.ERROR_PREFIX;

        JsonObject object = FormatUtils.getObject(multiLineStringJSON, ep);
        if (!(getType(object) == GeometryType.MULTILINESTRING)) {
            throw new TerraformerException(ep, TerraformerException.NOT_OF_TYPE + "\"MultiLineString\"");
        }

        return multiLineStringFromJsonObject(object, ep);
    }

    static MultiLineString multiLineStringFromJsonObject(JsonObject object, String errorPrefix) throws TerraformerException {
        // assume the type has already been checked
        JsonArray coords = getCoordinateArray(getCoordinates(object, errorPrefix), 0, errorPrefix);

        MultiLineString returnVal = new MultiLineString();
        for (JsonElement elem : coords) {
            returnVal.add(lineStringFromCoordinates(elem, errorPrefix));
        }

        return returnVal;
    }

    public static LineString decodeLineString(String lineStringJSON) throws TerraformerException {
        if (FormatUtils.isEmpty(lineStringJSON)) {
            throw new IllegalArgumentException(TerraformerException.JSON_STRING_EMPTY);
        }

        String ep = LineString.ERROR_PREFIX;

        JsonObject object = FormatUtils.getObject(lineStringJSON, ep);
        if (!(getType(object) == GeometryType.LINESTRING)) {
            throw new TerraformerException(ep, TerraformerException.NOT_OF_TYPE + "\"LineString\"");
        }

        return lineStringFromJsonObject(object, ep);
    }

    static LineString lineStringFromJsonObject(JsonObject object, String errorPrefix) throws TerraformerException {
        // assume the type has already been checked
        return lineStringFromCoordinates(getCoordinates(object, errorPrefix), errorPrefix);
    }

    static LineString lineStringFromCoordinates(JsonElement coordsElem, String errorPrefix) throws TerraformerException {
        JsonArray coords = getCoordinateArray(coordsElem, 2, errorPrefix);

        LineString returnVal = new LineString();
        for (JsonElement elem : coords) {
            returnVal.add(pointFromCoordinates(elem, errorPrefix));
        }

        return returnVal;
    }

    public static MultiPoint decodeMultiPoint(String multiPointJSON) throws TerraformerException {
        if (FormatUtils.isEmpty(multiPointJSON)) {
            throw new IllegalArgumentException(TerraformerException.JSON_STRING_EMPTY);
        }

        String ep = MultiPoint.ERROR_PREFIX;

        JsonObject object = FormatUtils.getObject(multiPointJSON, ep);
        if (!(getType(object) == GeometryType.MULTIPOINT)) {
            throw new TerraformerException(ep, TerraformerException.NOT_OF_TYPE + "\"MultiPoint\"");
        }

        return multiPointFromJsonObject(object, ep);
    }

    static MultiPoint multiPointFromJsonObject(JsonObject object, String errorPrefix) throws TerraformerException {
        // assume the type has already been checked
        JsonArray coords = getCoordinateArray(getCoordinates(object, errorPrefix), 2, errorPrefix);

        MultiPoint returnVal = new MultiPoint();
        for (JsonElement elem : coords) {
            returnVal.add(pointFromCoordinates(elem, errorPrefix));
        }

        return returnVal;
    }

    public static Point decodePoint(String pointJSON) throws TerraformerException {
        if (FormatUtils.isEmpty(pointJSON)) {
            throw new IllegalArgumentException(TerraformerException.JSON_STRING_EMPTY);
        }

        String ep = Point.ERROR_PREFIX;

        JsonObject object = FormatUtils.getObject(pointJSON, ep);
        if (!(getType(object) == GeometryType.POINT)) {
            throw new TerraformerException(ep, TerraformerException.NOT_OF_TYPE + "\"Point\"");
        }

        return pointFromJsonObject(object, ep);
    }

    static Point pointFromJsonObject(JsonObject object, String errorPrefix) throws TerraformerException {
        // assume the type has already been checked
        return pointFromCoordinates(getCoordinates(object, errorPrefix), errorPrefix);
    }

    static Point pointFromCoordinates(JsonElement coordsElem, String errorPrefix) throws TerraformerException {
        JsonArray coords = getCoordinateArray(coordsElem, 2, errorPrefix);

        Point returnVal = new Point();
        for (JsonElement elem : coords) {
            Double coord;
            try {
                coord = elem.getAsDouble();
            } catch (RuntimeException e) {
                throw new TerraformerException(errorPrefix, TerraformerException.COORDINATE_NOT_NUMERIC + elem);
            }

            returnVal.add(coord);
        }

        return returnVal;
    }

    /**
     * Don't call me with null!
     *
     * @param object
     * @return
     * @throws TerraformerException
     */
    static JsonElement getCoordinates(JsonObject object, String errorPrefix) throws TerraformerException {
        JsonElement coordsElem = object.get(COORDINATES_KEY);

        if (coordsElem == null) {
            throw new TerraformerException(errorPrefix, TerraformerException.COORDINATES_KEY_NOT_FOUND);
        }

        return coordsElem;
    }

    static JsonArray getCoordinateArray(JsonElement coordsElem, int minSize, String errorPrefix)
            throws TerraformerException {
        JsonArray coords = FormatUtils.arrayFromElement(coordsElem, errorPrefix);

        if (coords.size() < minSize) {
            throw new TerraformerException(errorPrefix, TerraformerException.COORDINATE_ARRAY_TOO_SHORT +
                    minSize + ")");
        }

        return coords;
    }

    static GeometryType getType(JsonObject object) {
        if (object == null) {
            return null;
        }

        JsonElement typeElem = object.get(TYPE_KEY);

        if (typeElem == null) {
            return null;
        }

        String typeString;
        try {
            typeString = typeElem.getAsString();
        } catch (RuntimeException e) {
            return null;
        }

        GeometryType foundType;
        try {
            foundType = GeometryType.fromJson(typeString);
        } catch (RuntimeException e) {
            return null;
        }

        return foundType;
    }

    static JsonObject geometryToJsonObject(Geometry geo) {
        Gson gson = new Gson();

        JsonObject object = new JsonObject();
        object.addProperty(TYPE_KEY, geo.getType().toString());

        if (geo.getType() == GeometryType.GEOMETRYCOLLECTION) {
            // geometry collections have special structure
            GeometryCollection gc = (GeometryCollection) geo;
            JsonArray geometries = new JsonArray();

            for (Geometry g : gc) {
                if (g != null) {
                    geometries.add(geometryToJsonObject(g));
                }
            }

            object.add(GEOMETRIES_KEY, geometries);
        } else {
            // points, linestrings, polygons etc
            JsonElement coords = gson.toJsonTree(geo);
            object.add(COORDINATES_KEY, coords);
        }

        return object;
    }
}
