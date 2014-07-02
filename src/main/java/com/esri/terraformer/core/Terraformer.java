package com.esri.terraformer.core;

public final class Terraformer {
    private Encoder encoder;
    private Decoder decoder;

    public interface Decoder {
        public BaseGeometry decode(String in) throws TerraformerException;
    }

    public interface Encoder {
        public String encode(BaseGeometry geo);
    }

    public Terraformer() { }

    public Terraformer(Decoder decoder, Encoder encoder) {
        this.decoder = decoder;
        this.encoder = encoder;
    }

    public Encoder getEncoder() {
        return this.encoder;
    }

    public void setEncoder(Encoder encoder) {
        this.encoder = encoder;
    }

    public Decoder getDecoder() {
        return this.decoder;
    }

    public void setDecoder(Decoder decoder) {
        this.decoder = decoder;
    }

    public BaseGeometry decode(String input) throws TerraformerException {
        if (decoder != null) {
            return decoder.decode(input);
        }

        throw new TerraformerException("", "There is no active decoder available!");
    }

    public String encode(BaseGeometry geometry) throws TerraformerException {
        if (encoder != null) {
            return encoder.encode(geometry);
        }

        throw new TerraformerException("", "There is no active encoder available!");
    }

    public String convert(String input) throws TerraformerException {
        return encode(decode(input));
    }
}
