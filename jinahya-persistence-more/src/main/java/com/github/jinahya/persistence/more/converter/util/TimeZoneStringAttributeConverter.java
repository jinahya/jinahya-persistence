package com.github.jinahya.persistence.more.converter.util;

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

import com.github.jinahya.persistence.more.converter.__StringAttributeConverter;
import com.github.jinahya.persistence.more.converter.time.ZoneIdStringAttributeConverter;
import jakarta.persistence.Converter;

import java.util.TimeZone;

/**
 * A converter for an entity attribute of {@link TimeZone}, stored as its {@link TimeZone#getID() ID}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @implSpec A value is stored as {@link TimeZone#getID()} &mdash; {@code "Asia/Seoul"}, {@code "GMT+09:00"} &mdash;
 *         and read back with {@link TimeZone#getTimeZone(String)}, as it is: an ID it does not know, {@code "GTM"} as
 *         much as {@code "Asia/Nowhere"}, reads as {@code GMT}.
 * @deprecated {@link TimeZone#getTimeZone(String)} never fails, so a column holding a mistyped or unknown ID reads
 *         back as {@code GMT} with no error, and the three-letter IDs it accepts are themselves deprecated by the JDK.
 *         Use {@link ZoneIdStringAttributeConverter.TimeZoneStringAttributeConverter}, which stores the same
 *         {@link TimeZone} through {@link java.time.ZoneId} and refuses an ID no zone has.
 */
@Deprecated
@Converter(autoApply = false)
public class TimeZoneStringAttributeConverter implements __StringAttributeConverter<TimeZone> {

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     *
     * @implNote {@code public}, not {@code protected}: a {@link Converter @Converter} class is instantiated by the
     *         persistence provider, and is documented as needing a public no-argument constructor.
     */
    public TimeZoneStringAttributeConverter() {
        super();
    }

    // ---------------------------------------------------------------------------------------------- AttributeConverter

    /**
     * {@inheritDoc}
     *
     * @param attribute {@inheritDoc}
     * @return the ID of the {@code attribute}; {@code null} when the {@code attribute} is {@code null}.
     */
    @Override
    public String convertToDatabaseColumn(final TimeZone attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getID();
    }

    /**
     * {@inheritDoc}
     *
     * @param dbData {@inheritDoc}
     * @return the time zone of the {@code dbData}, {@code GMT} for an ID the JDK does not know; {@code null} when
     *         the {@code dbData} is {@code null}.
     */
    @Override
    public TimeZone convertToEntityAttribute(final String dbData) {
        if (dbData == null) {
            return null;
        }
        return TimeZone.getTimeZone(dbData);
    }
}
