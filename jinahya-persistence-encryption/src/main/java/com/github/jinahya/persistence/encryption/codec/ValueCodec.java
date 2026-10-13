package com.github.jinahya.persistence.encryption.codec;

interface ValueCodec<T> {

    class OfPrimitives {

        private OfPrimitives() {
            throw new AssertionError("instantiation is not allowed");
        }
    }

    byte[] encode(T decoded);

    T decode(byte[] encoded);
}
