package com.github.jinahya.persistence.more.converter.time;

/*-
 * #%L
 * jinahya-persistence-more
 * %%
 * Copyright (C) 2025 - 2026 Jinahya, Inc.
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

import com.github.jinahya.persistence.more.converter.__ChainingAttributeConverter;
import com.github.jinahya.persistence.more.converter.__StringAttributeConverter;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.util.TimeZone;

/**
 * A converter for an entity attribute of {@link ZoneId}, stored as its {@link ZoneId#getId() identifier}.
 * <p>
 * A {@link ZoneId} is not a {@link java.time.temporal.TemporalAccessor}, so this converter sits on none of this
 * package's attribute axes. It implements {@link AttributeConverter AttributeConverter&lt;ZoneId, String&gt;} itself,
 * not the parent's column axis, {@link __StringAttributeConverter}: EclipseLink takes a converter's column type from
 * the type arguments of the interface the class names, and from {@code __StringAttributeConverter<ZoneId>}, whose
 * only argument is the attribute type, it took {@link ZoneId} for the column, and failed to bind the {@link String}
 * this converter returns ({@code EclipseLink-3002}).
 * <p>
 * It is also the core of {@link TimeZoneStringAttributeConverter}, which stores a {@link TimeZone} as the identifier of
 * its zone.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @implSpec A value is stored as {@link ZoneId#getId()} &mdash; {@code "Asia/Seoul"}, {@code "Z"}, {@code "+09:00"}
 *         &mdash; and read back with {@link ZoneId#of(String)}, which is its inverse: a region reads back as the same
 *         region, and an offset as a {@link java.time.ZoneOffset}.
 * @apiNote Only the identifier is stored, never the rules. A region whose rules change with a later time-zone
 *         database reads back under the new rules, which is what a zone column is for; and a region the running JDK
 *         does not know cannot be read back at all.
 */
@Converter(autoApply = false)
public class ZoneIdStringAttributeConverter implements AttributeConverter<ZoneId, String> {

    /**
     * A converter for an entity attribute of {@link TimeZone}, stored as the identifier of its
     * {@link TimeZone#toZoneId() zone}, through a {@link ZoneIdStringAttributeConverter}.
     *
     * @implSpec A value is converted to a {@link ZoneId} with {@link TimeZone#toZoneId()}, which is then stored by
     *         {@link ZoneIdStringAttributeConverter}; a column is read back by {@link ZoneIdStringAttributeConverter},
     *         and the zone converted with {@link TimeZone#getTimeZone(ZoneId)}. {@code "Asia/Seoul"}, {@code "GMT"}
     *         and {@code "GMT+09:00"} come back as they went in.
     * @apiNote The column follows {@link ZoneId}, not {@link TimeZone}. A deprecated three-letter ID is stored
     *         as the region it stands for &mdash; {@code "PST"} as {@code "America/Los_Angeles"}, with the same rules
     *         &mdash; and an ID no zone has is refused when read, where {@link TimeZone#getTimeZone(String)} would
     *         quietly read it as {@code GMT}.
     */
    @Converter(autoApply = false)
    public static class TimeZoneStringAttributeConverter
            extends __ChainingAttributeConverter<TimeZone, ZoneId, String>
            implements __StringAttributeConverter<TimeZone> {

        /**
         * Creates a new instance.
         *
         * @implNote {@code public}, not {@code protected}: a {@link Converter @Converter} class is instantiated
         *         by the persistence provider, and is documented as needing a public no-argument constructor.
         */
        public TimeZoneStringAttributeConverter() {
            super(new AttributeConverter<>() {
                @Override
                public ZoneId convertToDatabaseColumn(final TimeZone attribute) {
                    return attribute == null ? null : attribute.toZoneId();
                }

                @Override
                public TimeZone convertToEntityAttribute(final ZoneId dbData) {
                    return dbData == null ? null : TimeZone.getTimeZone(dbData);
                }
            }, new ZoneIdStringAttributeConverter());
        }
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     *
     * @implNote {@code public}, not {@code protected}: a {@link Converter @Converter} class is instantiated by the
     *         persistence provider, and is documented as needing a public no-argument constructor.
     */
    public ZoneIdStringAttributeConverter() {
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
    public String convertToDatabaseColumn(final ZoneId attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getId();
    }

    /**
     * {@inheritDoc}
     *
     * @param dbData {@inheritDoc}
     * @return the zone the {@code dbData} identifies; {@code null} when the {@code dbData} is {@code null}.
     * @throws IllegalArgumentException when the {@code dbData} identifies no zone the running JDK knows.
     */
    @Override
    public ZoneId convertToEntityAttribute(final String dbData) {
        if (dbData == null) {
            return null;
        }
        try {
            return ZoneId.of(dbData);
        } catch (final DateTimeException dte) {
            throw new IllegalArgumentException("not a zone: " + dbData, dte);
        }
    }
}
