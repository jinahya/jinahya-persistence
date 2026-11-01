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

import java.util.UUID;

/**
 * A converter for an entity attribute of {@link UUID}, stored as its {@link UUID#toString() text}.
 * <p>
 * Jakarta Persistence 3.1 made {@link UUID} a basic type, but leaves its column type to the provider, and providers do
 * not agree: a column which the JDBC driver reads as the text of a UUID &mdash; SQL Server's {@code uniqueidentifier},
 * {@code 6F9619FF-8B86-D011-B42D-00C04FC964FF} &mdash; is a {@link UUID} to one provider, and fails to read on another,
 * which takes the text for hexadecimal bytes. This converter makes the text the column type for every provider.
 * <p>
 * It implements {@link AttributeConverter AttributeConverter&lt;UUID, String&gt;} itself, not the parent's column
 * axis, {@link com.github.jinahya.persistence.more.converter.__StringAttributeConverter}: EclipseLink takes a
 * converter's column type from the type arguments of the interface the class names, as
 * {@link com.github.jinahya.persistence.more.converter.javatime.ZoneIdStringAttributeConverter} explains.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @implSpec A value is stored as {@link UUID#toString()} &mdash; {@code "6f9619ff-8b86-d011-b42d-00c04fc964ff"}, in
 *         lower case &mdash; and read back with {@link UUID#fromString(String)}, which is its inverse and reads either
 *         case: a column the database writes in upper case reads back as the same {@link UUID}.
 * @apiNote Jakarta Persistence applies no converter to an identifier attribute, so an identifier of such a column is
 *         mapped as a {@link String}, not through this converter.
 */
@Converter(autoApply = false)
public class UuidStringAttributeConverter implements AttributeConverter<UUID, String> {

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     *
     * @implNote {@code public}, not {@code protected}: a {@link Converter @Converter} class is instantiated by the
     *         persistence provider, and is documented as needing a public no-argument constructor.
     */
    public UuidStringAttributeConverter() {
        super();
    }

    // ---------------------------------------------------------------------------------------------- AttributeConverter

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
     * @return the UUID of the {@code dbData}, in either case; {@code null} when the {@code dbData} is {@code null}.
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
