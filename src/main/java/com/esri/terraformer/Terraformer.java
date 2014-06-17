package com.esri.terraformer;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public final class Terraformer {
    public static Encoder encoder;
    public static Decoder decoder;

    public interface Decoder {
        public BaseGeometry decode(String in) throws TerraformerException;
    }

    public interface Encoder {
        public String encode(BaseGeometry geo);
    }

    public static Encoder getEncoder() {
        return encoder;
    }

    public static void setEncoder(Encoder encoder) {
        Terraformer.encoder = encoder;
    }

    public static Decoder getDecoder() {
        return decoder;
    }

    public static void setDecoder(Decoder decoder) {
        Terraformer.decoder = decoder;
    }

    public BaseGeometry decode(String in) throws TerraformerException {
        if (decoder != null) {
            return decoder.decode(in);
        }

        throw new TerraformerException("", "There is no active decoder available!");
    }

    /**
     * Package private.
     *
     * @param json
     * @param errorPrefix
     * @return
     * @throws TerraformerException
     */
    static JsonElement getElement(String json, String errorPrefix) throws TerraformerException {
        if (Terraformer.isEmpty(json)) {
            throw new IllegalArgumentException(TerraformerException.JSON_STRING_EMPTY);
        }

        Gson gson = new Gson();
        JsonElement elem;

        try {
            elem = gson.fromJson(json, JsonElement.class);
        } catch (RuntimeException e) {
            throw new TerraformerException(errorPrefix, TerraformerException.NOT_VALID_JSON);
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
    static JsonObject getObject(String json, String errorPrefix) throws TerraformerException {
        if (isEmpty(json)) {
            throw new IllegalArgumentException(TerraformerException.JSON_STRING_EMPTY);
        }

        Gson gson = new Gson();
        JsonObject object;

        try {
            JsonElement objElem = gson.fromJson(json, JsonElement.class);
            object = objElem.getAsJsonObject();
        } catch (RuntimeException e) {
            throw new TerraformerException(errorPrefix, TerraformerException.NOT_A_JSON_OBJECT);
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
    static JsonObject objectFromElement(JsonElement objectElem, String errorPrefix) throws TerraformerException {
        JsonObject object;
        try {
            object = objectElem.getAsJsonObject();
        } catch (RuntimeException e) {
            throw new TerraformerException(errorPrefix, TerraformerException.ELEMENT_NOT_OBJECT);
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
    static JsonArray arrayFromElement(JsonElement arrayElem, String errorPrefix) throws TerraformerException {
        JsonArray array;
        try {
            array = arrayElem.getAsJsonArray();
        } catch (RuntimeException e) {
            throw new TerraformerException(errorPrefix, TerraformerException.ELEMENT_NOT_ARRAY);
        }

        return array;
    }

    public static boolean isEmpty(String json) {
        return json == null || json.length() <= 0;
    }
}
