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

import java.util.HexFormat;
import java.util.Objects;

/**
 * An attribute converter which stores a {@code byte[]} as its hexadecimal digits, through {@link HexFormat}.
 * <p>
 * A concrete converter is one of the nested classes, one for each case {@link HexFormat} writes in:
 * <table>
 * <caption>Converters</caption>
 * <thead>
 * <tr><th>Converter</th><th>Format</th><th>Example</th></tr>
 * </thead>
 * <tbody>
 * <tr><td>{@link OfLowerCase}</td><td>{@link HexFormat#of()}</td><td>{@code "fbff"}</td></tr>
 * <tr><td>{@link OfUpperCase}</td><td>{@link HexFormat#of()}{@code .withUpperCase()}</td><td>{@code "FBFF"}</td></tr>
 * </tbody>
 * </table>
 * <p>
 * It implements {@link AttributeConverter AttributeConverter&lt;byte[], String&gt;} itself, not the parent's column
 * axis, {@link com.github.jinahya.persistence.more.converter.__StringAttributeConverter}: EclipseLink takes a
 * converter's column type from the type arguments of the interface the class names, as
 * {@link com.github.jinahya.persistence.more.converter.javatime.ZoneIdStringAttributeConverter} explains.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @implSpec A value is stored as {@link HexFormat#formatHex(byte[])}, two digits for each byte with no delimiter,
 *         and read back with {@link HexFormat#parseHex(CharSequence)} of {@link HexFormat#of()}, whichever case the
 *         converter writes in, as it reads the digits in either case: a converter reads a column the other one
 *         writes. The digits of the bytes sort, in either case, as the bytes
 *         do, compared as unsigned.
 * @apiNote The formats of a delimiter, a prefix or a suffix, which {@link HexFormat} also offers, are not among
 *         these converters.
 */
public abstract class HexFormatStringAttributeConverter
        implements AttributeConverter<byte[], String> {

    /**
     * Stores a value in lower case.
     *
     * @see HexFormat#of()
     */
    @Converter(autoApply = false)
    public static class OfLowerCase
            extends HexFormatStringAttributeConverter {

        /**
         * Creates a new instance.
         */
        public OfLowerCase() {
            super(HexFormat.of());
        }
    }

    /**
     * Stores a value in upper case.
     *
     * @see HexFormat#of()
     * @see HexFormat#withUpperCase()
     */
    @Converter(autoApply = false)
    public static class OfUpperCase
            extends HexFormatStringAttributeConverter {

        /**
         * Creates a new instance.
         */
        public OfUpperCase() {
            super(HexFormat.of().withUpperCase());
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    // parses the digits in either case, whichever case the converter writes in
    private static final HexFormat PARSER = HexFormat.of();

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    private HexFormatStringAttributeConverter(final HexFormat format) {
        super();
        this.format = Objects.requireNonNull(format, "format is null");
    }

    // ---------------------------------------------------------------------------------------------- AttributeConverter

    /**
     * {@inheritDoc}
     *
     * @param attribute {@inheritDoc}
     * @return the hexadecimal digits of the {@code attribute}; {@code null} when the {@code attribute} is
     *         {@code null}.
     */
    @Override
    public String convertToDatabaseColumn(final byte[] attribute) {
        if (attribute == null) {
            return null;
        }
        return format.formatHex(attribute);
    }

    /**
     * {@inheritDoc}
     *
     * @param dbData {@inheritDoc}
     * @return the bytes of the {@code dbData}, in either case; {@code null} when the {@code dbData} is {@code null}.
     * @throws IllegalArgumentException when the {@code dbData} is not an even number of hexadecimal digits.
     */
    @Override
    public byte[] convertToEntityAttribute(final String dbData) {
        if (dbData == null) {
            return null;
        }
        return PARSER.parseHex(dbData);
    }

    // -----------------------------------------------------------------------------------------------------------------
    private final HexFormat format;
}
