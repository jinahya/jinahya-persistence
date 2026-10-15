package com.github.jinahya.persistence.encryption.codec;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.nio.ByteOrder;
import java.util.Objects;
import java.util.function.IntConsumer;

public final class IntegerCodec extends ValueCodec.OfFixedWidth<Integer> {

    // reads and writes an int as four big-endian bytes of a byte[], at any byte offset
    private static final VarHandle HANDLE = MethodHandles.byteArrayViewVarHandle(int[].class, ByteOrder.BIG_ENDIAN);

    // unchecked: the caller has checked the arguments, and the room
    private static void put(final int value, final byte[] encoded, final int index) {
        HANDLE.set(encoded, index, value);
    }

    // unchecked: the caller has checked the arguments, and the room
    private static int get(final byte[] encoded, final int index) {
        return (int) HANDLE.get(encoded, index);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * A codec of {@code int[]}s: four big-endian bytes for each element, as {@link IntegerCodec}.
     * <p>
     * The width varies with the value, and an encoding carries no length of its own: a value is decoded from the
     * specified index to the end of the array, which has to hold a whole number of elements.
     */
    public static final class OfArray implements ValueCodec<int[]> {

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
        public byte[] encode(final int[] decoded) {
            Objects.requireNonNull(decoded, "decoded is null");
            final var encoded = new byte[Math.multiplyExact(decoded.length, Integer.BYTES)];
            for (int i = 0, j = 0; i < decoded.length; i++, j += Integer.BYTES) {
                put(decoded[i], encoded, j);
            }
            return encoded;
        }

        @Override
        public int encode(final int[] decoded, final byte[] encoded, final int index) {
            Objects.requireNonNull(decoded, "decoded is null");
            Objects.requireNonNull(encoded, "encoded is null");
            final var size = Math.multiplyExact(decoded.length, Integer.BYTES);
            // the whole range, once; the elements are then written unchecked
            Objects.checkFromIndexSize(index, size, encoded.length);
            for (int i = 0, j = index; i < decoded.length; i++, j += Integer.BYTES) {
                put(decoded[i], encoded, j);
            }
            return size;
        }

        // -------------------------------------------------------------------------------------------------------------
        @Override
        public int[] decode(final byte[] encoded, final int index, final IntConsumer consumer) {
            Objects.requireNonNull(encoded, "encoded is null");
            // the value runs to the end of the array; an index at the end is an empty array
            Objects.checkFromToIndex(index, encoded.length, encoded.length);
            Objects.requireNonNull(consumer, "consumer is null");
            final var size = encoded.length - index;
            if (size % Integer.BYTES != 0) {
                // the last element would otherwise be dropped
                throw new IllegalArgumentException(size + " byte(s), not a multiple of " + Integer.BYTES);
            }
            final var decoded = new int[size / Integer.BYTES];
            for (int i = 0, j = index; i < decoded.length; i++, j += Integer.BYTES) {
                decoded[i] = get(encoded, j);
            }
            consumer.accept(size);
            return decoded;
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    /**
     * The instance; stateless, and thread-safe.
     */
    public static final IntegerCodec INSTANCE = new IntegerCodec();

    // -----------------------------------------------------------------------------------------------------------------
    private IntegerCodec() {
        super(Integer.BYTES);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public int encode(final Integer decoded, final byte[] encoded, final int index) {
        Objects.requireNonNull(decoded, "decoded is null");
        Objects.requireNonNull(encoded, "encoded is null");
        Objects.checkFromIndexSize(index, width, encoded.length);
        put(decoded, encoded, index);
        return width;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public Integer decode(final byte[] encoded, final int index, final IntConsumer consumer) {
        Objects.requireNonNull(encoded, "encoded is null");
        Objects.checkFromIndexSize(index, width, encoded.length);
        Objects.requireNonNull(consumer, "consumer is null");
        final var decoded = get(encoded, index);
        consumer.accept(width);
        return decoded;
    }
}
