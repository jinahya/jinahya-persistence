package com.github.jinahya.persistence.encryption.codec;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.function.IntConsumer;

public interface ValueCodec<T> {

    abstract class OfFixedWidth<T> implements ValueCodec<T> {

        OfFixedWidth(final int width) {
            super();
            if (width < 0) {
                throw new IllegalArgumentException("negative width: " + width);
            }
            this.width = width;
        }

        // -------------------------------------------------------------------------------------------------------------

        // -------------------------------------------------------------------------------------------------------------
        @Override
        public byte[] encode(final T decoded) {
            final var encoded = new byte[width];
            encode(decoded, encoded);
            return encoded;
        }

        // -------------------------------------------------------------------------------------------------------------
        final int width;
    }

    final class OfPrimitives {

        private OfPrimitives() {
            throw new AssertionError("instantiation is not allowed");
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    @PositiveOrZero
    int encode(@Nonnull @NotNull T decoded, @Nonnull @NotNull byte[] encoded, @PositiveOrZero int index);

    @PositiveOrZero
    default int encode(final @Nonnull @NotNull T decoded, final @Nonnull @NotNull byte[] encoded) {
        return encode(decoded, encoded, 0);
    }

    @Nonnull
    @NotNull
    byte[] encode(@Nonnull @NotNull T decoded);

    default void encode(final @Nonnull @NotNull T decoded, final @Nonnull @NotNull ByteBuffer encoded) {
        Objects.requireNonNull(decoded, "decoded is null");
        Objects.requireNonNull(encoded, "encoded is null");
        if (encoded.hasArray()) {
            encoded.position(
                    encoded.position() +
                    encode(decoded, encoded.array(), encoded.arrayOffset() + encoded.position())
            );
            return;
        }
        encoded.put(encode(decoded));
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Nonnull
    @NotNull
    T decode(@Nonnull @NotNull byte[] encoded, @PositiveOrZero int index, @Nonnull @NotNull IntConsumer consumer);

    default @Nonnull @NotNull
    T decode(final @Nonnull @NotNull byte[] encoded, final @Nonnull @NotNull IntConsumer consumer) {
        return decode(encoded, 0, consumer);
    }

    default @Nonnull @NotNull
    T decode(final @Nonnull @NotNull ByteBuffer encoded) {
        Objects.requireNonNull(encoded, "encoded is null");
        if (encoded.hasArray()) {
            return decode(
                    encoded.array(),
                    encoded.arrayOffset() + encoded.position(),
                    l -> encoded.position(encoded.position() + l)
            );
        }
        final var bytes = new byte[encoded.remaining()];
        encoded.get(encoded.position(), bytes);
        return decode(
                bytes,
                l -> {
                    encoded.position(encoded.position() + l);
                }
        );
    }
}
