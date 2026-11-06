package com.github.jinahya.persistence.more.converter.javanet;

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
import java.net.URI;

/**
 * A converter for an entity attribute of {@link URI}, stored as its {@link URI#toString() text}.
 * <p>
 * A value is stored as {@link URI#toString()} and read back with {@link URI#create(String)}, which is its inverse: the
 * text is the one the {@link URI} was parsed from, so it reads back as an {@link URI#equals(Object) equal} one.
 * <p>
 * There is no converter for {@link java.net.URL}, on purpose: its {@link java.net.URL#equals(Object) equals} and
 * {@link java.net.URL#hashCode() hashCode} resolve the host, and its constructors are deprecated. Map a {@link URI},
 * and convert it with {@link URI#toURL()} where a {@link java.net.URL} is needed.
 * <p>
 * It implements {@link AttributeConverter AttributeConverter&lt;URI, String&gt;} itself, not the parent's column axis,
 * {@link com.github.jinahya.persistence.more.converter.StringAttributeConverter}: EclipseLink takes a converter's
 * column type from the type arguments of the interface the class names, as
 * {@link com.github.jinahya.persistence.more.converter.javatime.ZoneIdStringAttributeConverter} explains.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Converter(autoApply = false)
public class UriStringAttributeConverter implements AttributeConverter<URI, String> {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     *
     * @implNote {@code public}, not {@code protected}: a {@link Converter @Converter} class is instantiated by
     *         the persistence provider, and is documented as needing a public no-argument constructor.
     */
    public UriStringAttributeConverter() {
        super();
    }

    // ---------------------------------------------------------------------------------------------- AttributeConverter

    /**
     * {@inheritDoc}
     *
     * @param attribute {@inheritDoc}
     * @return the text of the {@code attribute}; {@code null} when the {@code attribute} is {@code null}.
     */
    @Override
    public String convertToDatabaseColumn(final URI attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.toString();
    }

    /**
     * {@inheritDoc}
     *
     * @param dbData {@inheritDoc}
     * @return the URI of the {@code dbData}; {@code null} when the {@code dbData} is {@code null}.
     * @throws IllegalArgumentException when the {@code dbData} is not a URI.
     */
    @Override
    public URI convertToEntityAttribute(final String dbData) {
        if (dbData == null) {
            return null;
        }
        return URI.create(dbData);
    }
}
