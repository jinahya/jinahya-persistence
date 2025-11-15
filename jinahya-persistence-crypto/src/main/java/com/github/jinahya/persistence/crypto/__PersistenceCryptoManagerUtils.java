package com.github.jinahya.persistence.crypto;

import jakarta.annotation.Nonnull;

import java.nio.ByteBuffer;
import java.util.Objects;

final class __PersistenceCryptoManagerUtils {

    // ------------------------------------------------------------------------------------------------------------ byte
    @Deprecated(forRemoval = true)
    static @Nonnull byte[] byteToBytes(final byte value) {
        return ByteBuffer.allocate(Byte.BYTES).put(value).array();
    }

    @Deprecated(forRemoval = true)
    static byte bytesToByte(final @Nonnull byte[] bytes) {
        if (Objects.requireNonNull(bytes, "bytes is null").length != Byte.BYTES) {
            throw new IllegalArgumentException("bytes.length != " + Byte.BYTES);
        }
        return ByteBuffer.wrap(bytes).get();
    }

    // ----------------------------------------------------------------------------------------------------------- short
    @Deprecated(forRemoval = true)
    static @Nonnull byte[] shortToBytes(final short value) {
        return ByteBuffer.allocate(Short.BYTES).putShort(value).array();
    }

    @Deprecated(forRemoval = true)
    static short bytesToShort(final @Nonnull byte[] bytes) {
        return ByteBuffer.wrap(bytes).getShort();
    }

    // ------------------------------------------------------------------------------------------------------------- int
    static @Nonnull byte[] intToBytes(final int value) {
        return ByteBuffer.allocate(Integer.BYTES).putInt(value).array();
    }

    static int bytesToInt(final @Nonnull byte[] bytes) {
        return ByteBuffer.wrap(bytes).getInt();
    }

    // ------------------------------------------------------------------------------------------------------------ long
    static @Nonnull byte[] longToBytes(final long value) {
        return ByteBuffer.allocate(Long.BYTES).putLong(value).array();
    }

    static long bytesToLong(final @Nonnull byte[] bytes) {
        return ByteBuffer.wrap(bytes).getLong();
    }

    // -----------------------------------------------------------------------------------------------------------------
    private __PersistenceCryptoManagerUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
