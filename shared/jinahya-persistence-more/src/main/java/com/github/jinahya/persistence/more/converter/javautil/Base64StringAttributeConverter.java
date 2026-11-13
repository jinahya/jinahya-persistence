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
import java.util.Base64;
import java.util.Objects;

/**
 * An attribute converter which stores a {@code byte[]} as its {@link Base64} text.
 * <p>
 * A concrete converter is one of the nested classes, one for each encoder {@link Base64} offers:
 * <table>
 * <caption>Converters</caption>
 * <thead>
 * <tr><th>Converter</th><th>Encoder</th><th>Decoder</th></tr>
 * </thead>
 * <tbody>
 * <tr><td>{@link OfBasic}</td><td>{@link Base64#getEncoder()}</td><td>{@link Base64#getDecoder()}</td></tr>
 * <tr><td>{@link OfBasicWithoutPadding}</td><td>{@link Base64#getEncoder()}{@code .withoutPadding()}</td>
 * <td>{@link Base64#getDecoder()}</td></tr>
 * <tr><td>{@link OfUrl}</td><td>{@link Base64#getUrlEncoder()}</td><td>{@link Base64#getUrlDecoder()}</td></tr>
 * <tr><td>{@link OfUrlWithoutPadding}</td><td>{@link Base64#getUrlEncoder()}{@code .withoutPadding()}</td>
 * <td>{@link Base64#getUrlDecoder()}</td></tr>
 * <tr><td>{@link OfMime}</td><td>{@link Base64#getMimeEncoder()}</td><td>{@link Base64#getMimeDecoder()}</td></tr>
 * <tr><td>{@link OfMimeWithoutPadding}</td><td>{@link Base64#getMimeEncoder()}{@code .withoutPadding()}</td>
 * <td>{@link Base64#getMimeDecoder()}</td></tr>
 * </tbody>
 * </table>
 * <p>
 * It implements {@link AttributeConverter AttributeConverter&lt;byte[], String&gt;} itself, not the parent's column
 * axis, {@link com.github.jinahya.persistence.more.converter.StringAttributeConverter}: EclipseLink takes a
 * converter's column type from the type arguments of the interface the class names, as
 * {@link com.github.jinahya.persistence.more.converter.javatime.ZoneIdStringAttributeConverter} explains.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @implSpec A value is stored as the text of its encoder, and read back with the decoder of the same alphabet.
 *         A decoder of {@link Base64} reads its text with or without the padding, so a converter without padding reads
 *         a padded column, and the other way round. The basic and the URL decoders refuse any character out of their
 *         alphabets, a line separator included; the MIME decoder skips them.
 * @apiNote Jakarta Persistence applies no converter to an identifier attribute, so an identifier of such a
 *         column is mapped as a {@link String}, not through this converter.
 */
public abstract class Base64StringAttributeConverter
        implements AttributeConverter<byte[], String> {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    /**
     * Stores a value in the basic alphabet, padded.
     *
     * @see Base64#getEncoder()
     * @see Base64#getDecoder()
     */
    @Converter(autoApply = false)
    public static class OfBasic
            extends Base64StringAttributeConverter {

        /**
         * Creates a new instance.
         */
        public OfBasic() {
            super(Base64.getEncoder(), Base64.getDecoder());
        }
    }

    /**
     * Stores a value in the basic alphabet, without the padding.
     *
     * @see Base64#getEncoder()
     * @see Base64.Encoder#withoutPadding()
     * @see Base64#getDecoder()
     */
    @Converter(autoApply = false)
    public static class OfBasicWithoutPadding
            extends Base64StringAttributeConverter {

        /**
         * Creates a new instance.
         */
        public OfBasicWithoutPadding() {
            super(Base64.getEncoder().withoutPadding(), Base64.getDecoder());
        }
    }

    /**
     * Stores a value in the URL and filename safe alphabet, padded.
     *
     * @see Base64#getUrlEncoder()
     * @see Base64#getUrlDecoder()
     */
    @Converter(autoApply = false)
    public static class OfUrl
            extends Base64StringAttributeConverter {

        /**
         * Creates a new instance.
         */
        public OfUrl() {
            super(Base64.getUrlEncoder(), Base64.getUrlDecoder());
        }
    }

    /**
     * Stores a value in the URL and filename safe alphabet, without the padding.
     *
     * @see Base64#getUrlEncoder()
     * @see Base64.Encoder#withoutPadding()
     * @see Base64#getUrlDecoder()
     */
    @Converter(autoApply = false)
    public static class OfUrlWithoutPadding
            extends Base64StringAttributeConverter {

        /**
         * Creates a new instance.
         */
        public OfUrlWithoutPadding() {
            super(Base64.getUrlEncoder().withoutPadding(), Base64.getUrlDecoder());
        }
    }

    /**
     * Stores a value in the MIME format, padded, in lines of 76 characters separated by {@code "\r\n"}.
     *
     * @see Base64#getMimeEncoder()
     * @see Base64#getMimeDecoder()
     */
    @Converter(autoApply = false)
    public static class OfMime
            extends Base64StringAttributeConverter {

        /**
         * Creates a new instance.
         */
        public OfMime() {
            super(Base64.getMimeEncoder(), Base64.getMimeDecoder());
        }
    }

    /**
     * Stores a value in the MIME format, without the padding, in lines of 76 characters separated by {@code "\r\n"}.
     *
     * @see Base64#getMimeEncoder()
     * @see Base64.Encoder#withoutPadding()
     * @see Base64#getMimeDecoder()
     */
    @Converter(autoApply = false)
    public static class OfMimeWithoutPadding
            extends Base64StringAttributeConverter {

        /**
         * Creates a new instance.
         */
        public OfMimeWithoutPadding() {
            super(Base64.getMimeEncoder().withoutPadding(), Base64.getMimeDecoder());
        }
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    private Base64StringAttributeConverter(final Base64.Encoder encoder, final Base64.Decoder decoder) {
        super();
        this.encoder = Objects.requireNonNull(encoder, "encoder is null");
        this.decoder = Objects.requireNonNull(decoder, "decoder is null");
    }

    // ---------------------------------------------------------------------------------------------- AttributeConverter

    /**
     * {@inheritDoc}
     *
     * @param attribute {@inheritDoc}
     * @return the text of the {@code attribute}; {@code null} when the {@code attribute} is {@code null}.
     */
    @Override
    public String convertToDatabaseColumn(final byte[] attribute) {
        if (attribute == null) {
            return null;
        }
        return encoder.encodeToString(attribute);
    }

    /**
     * {@inheritDoc}
     *
     * @param dbData {@inheritDoc}
     * @return the bytes of the {@code dbData}; {@code null} when the {@code dbData} is {@code null}.
     * @throws IllegalArgumentException when the {@code dbData} is not a text of the decoder.
     */
    @Override
    public byte[] convertToEntityAttribute(final String dbData) {
        if (dbData == null) {
            return null;
        }
        return decoder.decode(dbData);
    }

    // -----------------------------------------------------------------------------------------------------------------
    private final Base64.Encoder encoder;

    private final Base64.Decoder decoder;
}
