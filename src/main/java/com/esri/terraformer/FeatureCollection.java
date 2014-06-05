package com.esri.terraformer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public final class FeatureCollection extends GeoJson<Feature> {

    @Override
    public GeoJsonType getType() {
        return GeoJsonType.FEATURECOLLECTION;
    }

    @Override
    public String toJson() {
        return null;
    }

    @Override
    protected JsonObject toJsonObject(Gson gson) {
        return null;
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public boolean isEquivalentTo(GeoJson<?> obj) {
        return false;
    }
}
