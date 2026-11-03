package com.github.jinahya.persistence.more.converter.javaniocharset;

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

import java.nio.charset.Charset;

/**
 * A converter for an entity attribute of {@link Charset}, stored as its {@link Charset#name() canonical name}.
 * <p>
 * A value is stored as {@link Charset#name()} &mdash; {@code "UTF-8"}, {@code "ISO-8859-1"} &mdash; and read back with
 * {@link Charset#forName(String)}, which also reads an alias of the name, in either case: {@code "utf8"} reads back as
 * the same {@link Charset} as {@code "UTF-8"}, and is stored as {@code "UTF-8"} again.
 * <p>
 * It implements {@link AttributeConverter AttributeConverter&lt;Charset, String&gt;} itself, not the parent's column
 * axis, {@link com.github.jinahya.persistence.more.converter.__StringAttributeConverter}: EclipseLink takes a converter's column type from the type arguments of the interface the class names, as
 * {@link com.github.jinahya.persistence.more.converter.javatime.ZoneIdStringAttributeConverter} explains.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Converter(autoApply = false)
public class CharsetStringAttributeConverter implements AttributeConverter<Charset, String> {

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     *
     * @implNote {@code public}, not {@code protected}: a {@link Converter @Converter} class is instantiated by the
     *         persistence provider, and is documented as needing a public no-argument constructor.
     */
    public CharsetStringAttributeConverter() {
        super();
    }

    // ---------------------------------------------------------------------------------------------- AttributeConverter

    /**
     * {@inheritDoc}
     *
     * @param attribute {@inheritDoc}
     * @return the canonical name of the {@code attribute}; {@code null} when the {@code attribute} is {@code null}.
     */
    @Override
    public String convertToDatabaseColumn(final Charset attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.name();
    }

    /**
     * {@inheritDoc}
     *
     * @param dbData {@inheritDoc}
     * @return the charset of the {@code dbData}; {@code null} when the {@code dbData} is {@code null}.
     * @throws IllegalArgumentException when the {@code dbData} is not the name, or an alias, of a charset the running
     *                                  JDK supports.
     */
    @Override
    public Charset convertToEntityAttribute(final String dbData) {
        if (dbData == null) {
            return null;
        }
        return Charset.forName(dbData);
    }
}
