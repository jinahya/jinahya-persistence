package com.github.jinahya.persistence.more.converter.javatime;

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
import java.time.DateTimeException;
import java.time.ZoneOffset;

/**
 * A converter for an entity attribute of {@link ZoneOffset}, stored as its {@link ZoneOffset#getId() identifier}.
 * <p>
 * A value is stored as {@link ZoneOffset#getId()} &mdash; {@code "+09:00"}, {@code "-03:30"}, {@code "Z"} for UTC
 * &mdash; and read back with {@link ZoneOffset#of(String)}, which is its inverse. Where an attribute of
 * {@link java.time.ZoneId} holds an offset or a region, {@link ZoneIdStringAttributeConverter} stores it.
 * <p>
 * It implements {@link AttributeConverter AttributeConverter&lt;ZoneOffset, String&gt;} itself, not the parent's column
 * axis, {@link com.github.jinahya.persistence.more.converter.StringAttributeConverter}: EclipseLink takes a
 * converter's column type from the type arguments of the interface the class names, as
 * {@link com.github.jinahya.persistence.more.converter.javatime.ZoneIdStringAttributeConverter} explains.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Converter(autoApply = false)
public class ZoneOffsetStringAttributeConverter implements AttributeConverter<ZoneOffset, String> {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     *
     * @implNote {@code public}, not {@code protected}: a {@link Converter @Converter} class is instantiated by
     *         the persistence provider, and is documented as needing a public no-argument constructor.
     */
    public ZoneOffsetStringAttributeConverter() {
        super();
    }

    // ---------------------------------------------------------------------------------------------- AttributeConverter

    /**
     * {@inheritDoc}
     *
     * @param attribute {@inheritDoc}
     * @return the identifier of the {@code attribute}; {@code null} when the {@code attribute} is {@code null}.
     */
    @Override
    public String convertToDatabaseColumn(final ZoneOffset attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getId();
    }

    /**
     * {@inheritDoc}
     *
     * @param dbData {@inheritDoc}
     * @return the offset the {@code dbData} identifies; {@code null} when the {@code dbData} is {@code null}.
     * @throws IllegalArgumentException when the {@code dbData} is not the identifier of an offset.
     */
    @Override
    public ZoneOffset convertToEntityAttribute(final String dbData) {
        if (dbData == null) {
            return null;
        }
        try {
            return ZoneOffset.of(dbData);
        } catch (final DateTimeException dte) {
            throw new IllegalArgumentException("not an offset: " + dbData, dte);
        }
    }
}
