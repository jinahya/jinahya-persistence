package com.github.jinahya.persistence.more.converter.javautil;

/*-
 * #%L
 * jinahya-persistence-more
 * %%
 * Copyright (C) 2026 Jinahya, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.lang.invoke.MethodHandles;
import java.nio.ByteBuffer;
import java.util.HexFormat;
import java.util.UUID;

/**
 * An attribute converter for an entity attribute of {@link UUID}, stored as its text or as its bytes.
 * <p>
 * Jakarta Persistence 3.1 made {@link UUID} a basic type, but leaves its column type to the provider, and providers do
 * not agree on a column of an ordinary type: one takes a text column for the text of a UUID, another for hexadecimal
 * bytes, and a column of 16 bytes is a {@link UUID} only to the provider which chose it. A concrete converter is one of
 * the nested classes, by the column, and makes that column type the same for every provider:
 * <table>
 * <caption>Converters</caption>
 * <thead>
 * <tr><th>Converter</th><th>Column</th><th>Stored as</th></tr>
 * </thead>
 * <tbody>
 * <tr><td>{@link OfString}</td><td>{@link String}</td><td>{@link UUID#toString()}, read back with
 * {@link UUID#fromString(String)}</td></tr>
 * <tr><td>{@link OfBytes}</td><td>{@code byte[]}</td><td>the 16 bytes of {@link UUID#getMostSignificantBits()} and
 * {@link UUID#getLeastSignificantBits()}, in big-endian order</td></tr>
 * <tr><td>{@link OfHexString}</td><td>{@link String}</td><td>the same 16 bytes, as 32 hexadecimal digits in lower
 * case, without the hyphens of {@link UUID#toString()}</td></tr>
 * </tbody>
 * </table>
 * <p>
 * A column of a type made for UUIDs is neither, and takes no converter: the provider maps a {@link UUID}
 * attribute to it as it is. Use {@link OfString} for a text column and {@link OfBytes} for a binary column; through
 * a converter, a column of a UUID type is bound as text or as bytes, which the database may refuse, or may convert
 * into a value other than the {@link UUID}.
 * <p>
 * Either form keeps the order of the {@link UUID#version() version} 6 and 7 values, which begin with their
 * timestamp: the texts, in lower case, sort as the values were created, and so do the bytes, compared as unsigned.
 * <p>
 * This class implements {@link AttributeConverter} with both type arguments itself, and each nested class names its
 * column type as this class's only one: EclipseLink takes a converter's column type from those arguments, and did not
 * find it in {@link com.github.jinahya.persistence.more.converter.__StringAttributeConverter}, whose only argument is
 * the attribute type, as {@link com.github.jinahya.persistence.more.converter.javatime.ZoneIdStringAttributeConverter}
 * explains.
 *
 * @param <Y> the column type
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @apiNote Jakarta Persistence applies no converter to an identifier attribute, so an identifier of such a
 *         column is mapped as its column type, not through this converter.
 */
public abstract class UuidAttributeConverter<Y>
        implements AttributeConverter<UUID, Y> {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    /**
     * Stores a value as its text.
     *
     * @implSpec A value is stored as {@link UUID#toString()} &mdash;
     *         {@code "6f9619ff-8b86-d011-b42d-00c04fc964ff"}, in lower case &mdash; and read back with
     *         {@link UUID#fromString(String)}, which is its inverse and reads either case: a column the database writes
     *         in upper case reads back as the same {@link UUID}.
     */
    @Converter(autoApply = false)
    public static class OfString
            extends UuidAttributeConverter<String> {

        /**
         * Creates a new instance.
         *
         * @implNote {@code public}, not {@code protected}: a {@link Converter @Converter} class is instantiated
         *         by the persistence provider, and is documented as needing a public no-argument constructor.
         */
        public OfString() {
            super();
        }

        /**
         * {@inheritDoc}
         *
         * @param attribute {@inheritDoc}
         * @return the text of the {@code attribute}, in lower case; {@code null} when the {@code attribute} is
         *         {@code null}.
         */
        @Override
        public String convertToDatabaseColumn(final UUID attribute) {
            if (attribute == null) {
                return null;
            }
            return attribute.toString();
        }

        /**
         * {@inheritDoc}
         *
         * @param dbData {@inheritDoc}
         * @return the UUID of the {@code dbData}, in either case; {@code null} when the {@code dbData} is
         *         {@code null}.
         * @throws IllegalArgumentException when the {@code dbData} is not the text of a UUID.
         */
        @Override
        public UUID convertToEntityAttribute(final String dbData) {
            if (dbData == null) {
                return null;
            }
            return UUID.fromString(dbData);
        }
    }

    /**
     * Stores a value as its 16 bytes.
     *
     * @implSpec A value is stored as its {@link UUID#getMostSignificantBits() most significant} and then its
     *         {@link UUID#getLeastSignificantBits() least significant} bits, each in big-endian order &mdash; the order
     *         of its text, and of RFC 9562 &mdash; and read back from exactly 16 bytes.
     * @apiNote The bytes of a {@code uniqueidentifier}, as SQL Server and .NET's {@code Guid.ToByteArray()}
     *         give them, are in another order, the first three fields little-endian, and do not read back as the same
     *         {@link UUID}; nor do the bytes MySQL's {@code UUID_TO_BIN(uuid, 1)} swaps.
     */
    @Converter(autoApply = false)
    public static class OfBytes
            extends UuidAttributeConverter<byte[]> {

        /**
         * Creates a new instance.
         *
         * @implNote {@code public}, not {@code protected}: a {@link Converter @Converter} class is instantiated
         *         by the persistence provider, and is documented as needing a public no-argument constructor.
         */
        public OfBytes() {
            super();
        }

        /**
         * {@inheritDoc}
         *
         * @param attribute {@inheritDoc}
         * @return the 16 bytes of the {@code attribute}; {@code null} when the {@code attribute} is {@code null}.
         */
        @Override
        public byte[] convertToDatabaseColumn(final UUID attribute) {
            if (attribute == null) {
                return null;
            }
            return bytes(attribute);
        }

        /**
         * {@inheritDoc}
         *
         * @param dbData {@inheritDoc}
         * @return the UUID of the {@code dbData}; {@code null} when the {@code dbData} is {@code null}.
         * @throws IllegalArgumentException when the {@code dbData} is not 16 bytes long.
         */
        @Override
        public UUID convertToEntityAttribute(final byte[] dbData) {
            if (dbData == null) {
                return null;
            }
            return uuid(dbData);
        }
    }

    /**
     * Stores a value as the hexadecimal digits of its 16 bytes.
     *
     * @implSpec A value is stored as the bytes {@link OfBytes} stores, formatted with {@link HexFormat#of()}
     *         &mdash; {@code "6f9619ff8b86d011b42d00c04fc964ff"}, in lower case &mdash; and read back from exactly 32
     *         hexadecimal digits, in either case.
     */
    @Converter(autoApply = false)
    public static class OfHexString
            extends UuidAttributeConverter<String> {

        /**
         * Creates a new instance.
         *
         * @implNote {@code public}, not {@code protected}: a {@link Converter @Converter} class is instantiated
         *         by the persistence provider, and is documented as needing a public no-argument constructor.
         */
        public OfHexString() {
            super();
        }

        /**
         * {@inheritDoc}
         *
         * @param attribute {@inheritDoc}
         * @return the 32 hexadecimal digits of the {@code attribute}, in lower case; {@code null} when the
         *         {@code attribute} is {@code null}.
         */
        @Override
        public String convertToDatabaseColumn(final UUID attribute) {
            if (attribute == null) {
                return null;
            }
            return HexFormat.of().formatHex(bytes(attribute));
        }

        /**
         * {@inheritDoc}
         *
         * @param dbData {@inheritDoc}
         * @return the UUID of the {@code dbData}, in either case; {@code null} when the {@code dbData} is
         *         {@code null}.
         * @throws IllegalArgumentException when the {@code dbData} is not 32 hexadecimal digits.
         */
        @Override
        public UUID convertToEntityAttribute(final String dbData) {
            if (dbData == null) {
                return null;
            }
            return uuid(HexFormat.of().parseHex(dbData));
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    // the number of bytes of a UUID
    private static final int BYTES = Long.BYTES << 1;

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    private UuidAttributeConverter() {
        super();
    }

    // -----------------------------------------------------------------------------------------------------------------

    // the 16 bytes of the value, its most significant bits first, each in big-endian order
    private static byte[] bytes(final UUID value) {
        return ByteBuffer.allocate(BYTES)
                .putLong(value.getMostSignificantBits())
                .putLong(value.getLeastSignificantBits())
                .array();
    }

    // the value of the 16 bytes
    private static UUID uuid(final byte[] bytes) {
        if (bytes.length != BYTES) {
            throw new IllegalArgumentException("bytes.length(" + bytes.length + ") != " + BYTES);
        }
        final var buffer = ByteBuffer.wrap(bytes);
        return new UUID(buffer.getLong(), buffer.getLong());
    }
}
