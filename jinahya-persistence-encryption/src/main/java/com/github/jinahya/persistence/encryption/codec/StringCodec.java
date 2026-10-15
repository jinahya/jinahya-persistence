package com.github.jinahya.persistence.encryption.codec;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.IntConsumer;

/**
 * A codec of {@link String}s, as UTF-8, strictly: a string with an unpaired surrogate is not encoded, and bytes which
 * are not well-formed UTF-8 are not decoded.
 * <p>
 * The width varies with the value, and an encoding carries no length of its own: a value is decoded from the specified
 * index to the end of the array.
 */
public final class StringCodec implements ValueCodec<String> {

    /**
     * The instance; stateless, and thread-safe.
     */
    public static final StringCodec INSTANCE = new StringCodec();

    // -----------------------------------------------------------------------------------------------------------------
    private StringCodec() {
        super();
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public byte[] encode(final String decoded) {
        Objects.requireNonNull(decoded, "decoded is null");
        try {
            final var buffer = StandardCharsets.UTF_8.newEncoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT)
                    .encode(CharBuffer.wrap(decoded));
            final var encoded = new byte[buffer.remaining()];
            buffer.get(encoded);
            return encoded;
        } catch (final CharacterCodingException cce) {
            // never the value in the message
            throw new IllegalArgumentException("the string is not encodable as UTF-8 (an unpaired surrogate?)", cce);
        }
    }

    @Override
    public int encode(final String decoded, final byte[] encoded, final int index) {
        Objects.requireNonNull(decoded, "decoded is null");
        Objects.requireNonNull(encoded, "encoded is null");
        // the size is known only once encoded
        final var bytes = encode(decoded);
        Objects.checkFromIndexSize(index, bytes.length, encoded.length);
        System.arraycopy(bytes, 0, encoded, index, bytes.length);
        return bytes.length;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public String decode(final byte[] encoded, final int index, final IntConsumer consumer) {
        Objects.requireNonNull(encoded, "encoded is null");
        // the value runs to the end of the array; an index at the end is an empty string
        Objects.checkFromToIndex(index, encoded.length, encoded.length);
        Objects.requireNonNull(consumer, "consumer is null");
        final var length = encoded.length - index;
        final String decoded;
        try {
            decoded = StandardCharsets.UTF_8.newDecoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT)
                    .decode(ByteBuffer.wrap(encoded, index, length))
                    .toString();
        } catch (final CharacterCodingException cce) {
            throw new IllegalArgumentException("the bytes are not well-formed UTF-8", cce);
        }
        consumer.accept(length);
        return decoded;
    }
}
