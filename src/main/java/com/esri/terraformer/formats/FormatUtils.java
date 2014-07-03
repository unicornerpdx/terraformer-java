package com.esri.terraformer.formats;

import com.esri.terraformer.core.TerraformerException;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class FormatUtils {

    /**
     * Package private.
     *
     * @param json
     * @param errorPrefix
     * @return
     * @throws com.esri.terraformer.core.TerraformerException
     */
    public static JsonElement getElement(String json, String errorPrefix) throws TerraformerException {
        if (isEmpty(json)) {
            throw new IllegalArgumentException(TerraformerException.JSON_STRING_EMPTY);
        }

        Gson gson = new Gson();
        JsonElement elem;

        try {
            elem = gson.fromJson(json, JsonElement.class);
        } catch (RuntimeException e) {
           throw new TerraformerException(errorPrefix, TerraformerException.NOT_VALID_JSON, e);
        }

        return elem;
    }

    /**
     * Package private.
     *
     * @param json
     * @return
     * @throws TerraformerException
     */
    public static JsonObject getObject(String json, String errorPrefix) throws TerraformerException {
        if (isEmpty(json)) {
            throw new IllegalArgumentException(TerraformerException.JSON_STRING_EMPTY);
        }

        Gson gson = new Gson();
        JsonObject object;

        try {
            JsonElement objElem = gson.fromJson(json, JsonElement.class);
            object = objElem.getAsJsonObject();
        } catch (RuntimeException e) {
            throw new TerraformerException(errorPrefix, TerraformerException.NOT_A_JSON_OBJECT, e);
        }

        return object;
    }

    /**
     * Package private.
     *
     * @param objectElem
     * @param errorPrefix
     * @return
     * @throws TerraformerException
     */
    public static JsonObject objectFromElement(JsonElement objectElem, String errorPrefix) throws TerraformerException {
        JsonObject object;
        try {
            object = objectElem.getAsJsonObject();
        } catch (RuntimeException e) {
            throw new TerraformerException(errorPrefix, TerraformerException.ELEMENT_NOT_OBJECT, e);
        }

        return object;
    }

    /**
     * Package private.
     *
     * @param arrayElem
     * @param errorPrefix
     * @return
     * @throws TerraformerException
     */
    public static JsonArray arrayFromElement(JsonElement arrayElem, String errorPrefix) throws TerraformerException {
        JsonArray array;
        try {
            array = arrayElem.getAsJsonArray();
        } catch (RuntimeException e) {
            throw new TerraformerException(errorPrefix, TerraformerException.ELEMENT_NOT_ARRAY, e);
        }

        return array;
    }

    public static boolean isEmpty(String json) {
        return json == null || json.length() <= 0;
    }
}
