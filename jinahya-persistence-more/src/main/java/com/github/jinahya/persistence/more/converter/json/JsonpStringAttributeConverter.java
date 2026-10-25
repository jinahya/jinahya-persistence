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

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonException;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import jakarta.json.JsonStructure;
import jakarta.json.JsonValue;
import jakarta.persistence.AttributeConverter;
import org.jspecify.annotations.Nullable;
import jakarta.persistence.Converter;

import java.io.StringReader;
import java.util.NoSuchElementException;
import java.util.Objects;

public abstract class JsonpStringAttributeConverter<X extends JsonValue>
        implements AttributeConverter<X, String> {

    @Converter(autoApply = false)
    public static class OfJsonValue
            extends JsonpStringAttributeConverter<JsonValue> {

        public OfJsonValue() {
            super(JsonValue.class);
        }
    }

    private static class _OfJsonStructure<X extends JsonStructure>
            extends JsonpStringAttributeConverter<X> {

        _OfJsonStructure(final Class<X> attributeClass) {
            super(attributeClass);
        }
    }

    @Converter(autoApply = false)
    public static class OfJsonStructure
            extends _OfJsonStructure<JsonStructure> {

        public OfJsonStructure() {
            super(JsonStructure.class);
        }
    }

    @Converter(autoApply = false)
    public static class OfJsonObject
            extends _OfJsonStructure<JsonObject> {

        public OfJsonObject() {
            super(JsonObject.class);
        }
    }

    @Converter(autoApply = false)
    public static class OfJsonArray
            extends _OfJsonStructure<JsonArray> {

        public OfJsonArray() {
            super(JsonArray.class);
        }
    }

    @Converter(autoApply = false)
    public static class OfJsonString
            extends JsonpStringAttributeConverter<JsonString> {

        public OfJsonString() {
            super(JsonString.class);
        }
    }

    @Converter(autoApply = false)
    public static class OfJsonNumber
            extends JsonpStringAttributeConverter<JsonNumber> {

        public OfJsonNumber() {
            super(JsonNumber.class);
        }
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    protected JsonpStringAttributeConverter(final Class<X> attributeClass) {
        super();
        this.attributeClass = Objects.requireNonNull(attributeClass, "attributeClass is null");
    }

    // ---------------------------------------------------------------------------------------------- AttributeConverter

    @Override
    public @Nullable String convertToDatabaseColumn(final @Nullable X attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.toString();
    }

    @Override
    public @Nullable X convertToEntityAttribute(final @Nullable String dbData) {
        if (dbData == null) {
            return null;
        }
        final JsonValue value;
        // a parser, not a JsonReader: JsonReader#readValue() reads the first value and ignores
        // whatever follows it, "1 2" or "{} []", which the server rejects
        try (var parser = Json.createParser(new StringReader(dbData))) {
            if (!parser.hasNext()) {
                throw new IllegalArgumentException("not a JSON value: " + dbData);
            }
            parser.next();
            value = parser.getValue();
            if (parser.hasNext()) {
                throw new IllegalArgumentException("more than one JSON value: " + dbData);
            }
        } catch (final JsonException | IllegalStateException | NoSuchElementException e) {
            throw new IllegalArgumentException("not a JSON value: " + dbData, e);
        }
        if (!attributeClass.isInstance(value)) {
            throw new IllegalArgumentException(
                    "not a " + attributeClass.getSimpleName() + ": " + dbData);
        }
        return attributeClass.cast(value);
    }

    // -----------------------------------------------------------------------------------------------------------------
    private final Class<X> attributeClass;
}

