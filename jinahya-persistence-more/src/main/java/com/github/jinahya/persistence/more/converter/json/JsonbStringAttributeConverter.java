package com.github.jinahya.persistence.more.converter.json;

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


import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;
import jakarta.persistence.AttributeConverter;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Objects;

public abstract class JsonbStringAttributeConverter<X>
        implements AttributeConverter<X, String> {

    // -----------------------------------------------------------------------------------------------------------------
    private static final System.Logger LOGGER =
            System.getLogger(JsonbStringAttributeConverter.class.getName());

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    protected JsonbStringAttributeConverter(final Type attributeType) {
        super();
        this.attributeType = Objects.requireNonNull(attributeType, "attributeType is null");
    }

    // ------------------------------------------------------------------------------------------------------- LIFECYCLE

    // both called only when the provider obtains this converter through CDI (jakarta.persistence.bean.manager);
    // without one, the provider calls no lifecycle method, and there is no jsonb to convert with
    @PostConstruct
    private void createJsonb() {
        jsonb = JsonbBuilder.create();
    }

    @PreDestroy
    // the jsonb is there only between @PostConstruct and @PreDestroy, which NullAway cannot tell
    @SuppressWarnings("NullAway")
    private void closeJsonb() {
        try {
            jsonb.close();
        } catch (final Exception e) {
            LOGGER.log(System.Logger.Level.WARNING, "failed to close " + jsonb, e);
        }
        jsonb = null;
    }

    // ---------------------------------------------------------------------------------------------- AttributeConverter

    @Override
    // the jsonb is there only between @PostConstruct and @PreDestroy, which NullAway cannot tell
    @SuppressWarnings("NullAway")
    public @Nullable String convertToDatabaseColumn(final @Nullable X attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            return jsonb.toJson(attribute, attributeType);
        } catch (final JsonbException jsonbe) {
            throw new IllegalArgumentException("failed to write " + attribute, jsonbe);
        }
    }

    @Override
    // the jsonb is there only between @PostConstruct and @PreDestroy, which NullAway cannot tell
    @SuppressWarnings("NullAway")
    public @Nullable X convertToEntityAttribute(final @Nullable String dbData) {
        if (dbData == null) {
            return null;
        }
        try {
            return jsonb.fromJson(dbData, attributeType);
        } catch (final JsonbException jsonbe) {
            throw new IllegalArgumentException(
                    "not a JSON " + attributeType.getTypeName() + ": " + dbData, jsonbe);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    private final Type attributeType;

    private @Nullable Jsonb jsonb;
}

