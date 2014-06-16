package com.esri.terraformer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class GeoJson implements Terraformer.Serializer<String>, Terraformer.Deserializer<String> {
    @Override
    public BaseGeometry deserialize(String json) {
        return null;
    }

    @Override
    public String serialize(BaseGeometry geo) {
        return null;
    }

    public static FeatureCollection decodeFeatureCollection(String featureCollectionJSON)
            throws TerraformerException {
        String ep = FeatureCollection.ERROR_PREFIX;
        if (Terraformer.isEmpty(featureCollectionJSON)) {
            throw new IllegalArgumentException(TerraformerException.JSON_STRING_EMPTY);
        }

        JsonObject object = getObject(featureCollectionJSON, ep);
        if (!(getType(object) == GeometryType.FEATURECOLLECTION)) {
            throw new TerraformerException(, TerraformerException.NOT_OF_TYPE + "\"FeatureCollection\"");
        }

        return fromJsonObject(object);
    }

    static FeatureCollection fromJsonObject(JsonObject object) throws TerraformerException {
        // assume the type has already been checked
        JsonElement featuresElem = object.get(FEATURES_KEY);

        if (featuresElem == null) {
            throw new TerraformerException(ERROR_PREFIX, TerraformerException.FEATURES_KEY_NOT_FOUND);
        }

        JsonArray features = arrayFromElement(featuresElem, ERROR_PREFIX);

        FeatureCollection returnVal = new FeatureCollection();
        for (JsonElement elem : features) {
            returnVal.add(Feature.featureFromObjectElement(elem, ERROR_PREFIX));
        }

        return returnVal;
    }
}
