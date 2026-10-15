package com.github.jinahya.persistence.encryption.codec;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.nio.ByteOrder;
import java.util.function.IntConsumer;

public final class IntegerCodec extends ValueCodec.OfFixedWidth<Integer> {

    // reads and writes an int as four big-endian bytes of a byte[], at any byte offset
    private static final VarHandle HANDLE = MethodHandles.byteArrayViewVarHandle(int[].class, ByteOrder.BIG_ENDIAN);

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
        HANDLE.set(encoded, index, (int) decoded);
        return width;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public Integer decode(final byte[] encoded, final int index, final IntConsumer consumer) {
        final var decoded = (int) HANDLE.get(encoded, index);
        consumer.accept(width);
        return decoded;
    }
}
