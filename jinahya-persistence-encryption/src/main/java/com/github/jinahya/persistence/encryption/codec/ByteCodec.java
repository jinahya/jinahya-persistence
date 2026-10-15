package com.github.jinahya.persistence.encryption.codec;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.IntConsumer;

public final class ByteCodec extends ValueCodec.OfFixedWidth<Byte> {

    /**
     * A codec of {@code byte[]}s: the bytes, as they are; never sharing an array with the caller.
     * <p>
     * The width varies with the value, and an encoding carries no length of its own: a value is decoded from the
     * specified index to the end of the array.
     */
    public static final class OfArray implements ValueCodec<byte[]> {

        /**
         * The instance; stateless, and thread-safe.
         */
        public static final OfArray INSTANCE = new OfArray();

        // -------------------------------------------------------------------------------------------------------------
        private OfArray() {
            super();
        }

        // -------------------------------------------------------------------------------------------------------------
        @Override
        public byte[] encode(final byte[] decoded) {
            Objects.requireNonNull(decoded, "decoded is null");
            // a copy: never the caller's own array
            return decoded.clone();
        }

        @Override
        public int encode(final byte[] decoded, final byte[] encoded, final int index) {
            Objects.requireNonNull(decoded, "decoded is null");
            Objects.requireNonNull(encoded, "encoded is null");
            Objects.checkFromIndexSize(index, decoded.length, encoded.length);
            // in place, in bulk: the size is known before encoding
            System.arraycopy(decoded, 0, encoded, index, decoded.length);
            return decoded.length;
        }

        // -------------------------------------------------------------------------------------------------------------
        @Override
        public byte[] decode(final byte[] encoded, final int index, final IntConsumer consumer) {
            Objects.requireNonNull(encoded, "encoded is null");
            // the value runs to the end of the array; an index at the end is an empty array
            Objects.checkFromToIndex(index, encoded.length, encoded.length);
            Objects.requireNonNull(consumer, "consumer is null");
            // a copy: never a view of the caller's array
            final var decoded = Arrays.copyOfRange(encoded, index, encoded.length);
            consumer.accept(decoded.length);
            return decoded;
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    /**
     * The instance; stateless, and thread-safe.
     */
    public static final ByteCodec INSTANCE = new ByteCodec();

    // -----------------------------------------------------------------------------------------------------------------
    private ByteCodec() {
        super(Byte.BYTES);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public int encode(final Byte decoded, final byte[] encoded, final int index) {
        Objects.requireNonNull(decoded, "decoded is null");
        Objects.requireNonNull(encoded, "encoded is null");
        Objects.checkIndex(index, encoded.length);
        encoded[index] = decoded;
        return width;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public Byte decode(final byte[] encoded, final int index, final IntConsumer consumer) {
        Objects.requireNonNull(encoded, "encoded is null");
        Objects.checkIndex(index, encoded.length);
        Objects.requireNonNull(consumer, "consumer is null");
        final var decoded = encoded[index];
        consumer.accept(width);
        return decoded;
    }
}
