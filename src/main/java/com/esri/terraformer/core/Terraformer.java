package com.esri.terraformer.core;

import com.esri.terraformer.formats.GeoJson;

public final class Terraformer {
    static Encoder encoder;
    static Decoder decoder;

    // use GeoJson by default
    static {
        GeoJson gj = new GeoJson();
        encoder = gj;
        decoder = gj;
    }

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
}
