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
import jakarta.persistence.AttributeConverter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.function.Function;

public abstract class JsonbAttributeConverter<X, Y>
        implements AttributeConverter<X, Y> {

    // stored as the JSON text
    public abstract static class OfString<X>
            extends JsonbAttributeConverter<X, String> {

        protected OfString(final Type attributeType) {
            super(attributeType);
        }

        @Override
        public String convertToDatabaseColumn(final X attribute) {
            if (attribute == null) {
                return null;
            }
            return super.applyJsonb(b -> b.toJson(attribute, super.attributeType));
        }

        @Override
        public X convertToEntityAttribute(final String dbData) {
            if (dbData == null) {
                return null;
            }
            return super.applyJsonb(b -> b.fromJson(dbData, super.attributeType));
        }
    }

    // stored as the JSON text in UTF-8, the encoding a Jsonb created with no config writes and reads
    public abstract static class OfBytes<X>
            extends JsonbAttributeConverter<X, byte[]> {

        protected OfBytes(final Type attributeType) {
            super(attributeType);
        }

        @Override
        public byte[] convertToDatabaseColumn(final X attribute) {
            if (attribute == null) {
                return null;
            }
            return super.applyJsonb(b -> {
                final var output = new ByteArrayOutputStream();
                b.toJson(attribute, super.attributeType, output);
                return output.toByteArray();
            });
        }

        @Override
        public X convertToEntityAttribute(final byte[] dbData) {
            if (dbData == null) {
                return null;
            }
            return super.applyJsonb(b -> b.fromJson(new ByteArrayInputStream(dbData), super.attributeType));
        }
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    private JsonbAttributeConverter(final Type attributeType) {
        super();
        this.attributeType = Objects.requireNonNull(attributeType, "attributeType is null");
    }

    // ------------------------------------------------------------------------------------------------------- LIFECYCLE

    // both called only when the provider obtains this converter through CDI (jakarta.persistence.bean.manager);
    // without one, the jsonb stays null, and each conversion creates and closes its own; see applyJsonb
    @PostConstruct
    private void createJsonb() {
        jsonb = JsonbBuilder.create();
    }

    @PreDestroy
    private void closeJsonb() {
        final var current = jsonb;
        jsonb = null;
        if (current == null) {
            return;
        }
        try {
            current.close();
        } catch (final RuntimeException re) {
            throw re;
        } catch (final Exception e) { // a @PreDestroy method may not throw a checked one
            throw new RuntimeException(e);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    private <R> R applyJsonb(final Function<? super Jsonb, ? extends R> function) {
        final var current = jsonb; // read once; @PreDestroy may null it
        if (current != null) {
            return function.apply(current);
        }
        try (var created = JsonbBuilder.create()) {
            return function.apply(created);
        } catch (final RuntimeException re) {
            throw re;
        } catch (final Exception e) { // only close() throws a checked one
            throw new RuntimeException(e);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    private final Type attributeType;

    private Jsonb jsonb;
}

