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
import jakarta.json.stream.JsonParser;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Supplier;

public abstract class JsonpAttributeConverter<X extends JsonValue, Y>
        implements AttributeConverter<X, Y> {

    // stored as the JSON text
    public abstract static class OfString<X extends JsonValue>
            extends JsonpAttributeConverter<X, String> {

        @Converter(autoApply = false)
        public static class OfJsonValue
                extends OfString<JsonValue> {

            public OfJsonValue() {
                super(JsonValue.class);
            }
        }

        @Converter(autoApply = false)
        public static class OfJsonStructure
                extends OfString<JsonStructure> {

            public OfJsonStructure() {
                super(JsonStructure.class);
            }
        }

        @Converter(autoApply = false)
        public static class OfJsonObject
                extends OfString<JsonObject> {

            public OfJsonObject() {
                super(JsonObject.class);
            }
        }

        @Converter(autoApply = false)
        public static class OfJsonArray
                extends OfString<JsonArray> {

            public OfJsonArray() {
                super(JsonArray.class);
            }
        }

        @Converter(autoApply = false)
        public static class OfJsonString
                extends OfString<JsonString> {

            public OfJsonString() {
                super(JsonString.class);
            }
        }

        @Converter(autoApply = false)
        public static class OfJsonNumber
                extends OfString<JsonNumber> {

            public OfJsonNumber() {
                super(JsonNumber.class);
            }
        }

        private OfString(final Class<X> attributeClass) {
            super(attributeClass);
        }

        @Override
        public String convertToDatabaseColumn(final X attribute) {
            if (attribute == null) {
                return null;
            }
            return attribute.toString();
        }

        @Override
        public X convertToEntityAttribute(final String dbData) {
            if (dbData == null) {
                return null;
            }
            return super.read(() -> Json.createParser(new StringReader(dbData)), dbData);
        }
    }

    // stored as the JSON text in UTF-8; read in whichever of UTF-8, UTF-16 and UTF-32 the bytes are in
    public abstract static class OfBytes<X extends JsonValue>
            extends JsonpAttributeConverter<X, byte[]> {

        @Converter(autoApply = false)
        public static class OfJsonValue
                extends OfBytes<JsonValue> {

            public OfJsonValue() {
                super(JsonValue.class);
            }
        }

        @Converter(autoApply = false)
        public static class OfJsonStructure
                extends OfBytes<JsonStructure> {

            public OfJsonStructure() {
                super(JsonStructure.class);
            }
        }

        @Converter(autoApply = false)
        public static class OfJsonObject
                extends OfBytes<JsonObject> {

            public OfJsonObject() {
                super(JsonObject.class);
            }
        }

        @Converter(autoApply = false)
        public static class OfJsonArray
                extends OfBytes<JsonArray> {

            public OfJsonArray() {
                super(JsonArray.class);
            }
        }

        @Converter(autoApply = false)
        public static class OfJsonString
                extends OfBytes<JsonString> {

            public OfJsonString() {
                super(JsonString.class);
            }
        }

        @Converter(autoApply = false)
        public static class OfJsonNumber
                extends OfBytes<JsonNumber> {

            public OfJsonNumber() {
                super(JsonNumber.class);
            }
        }

        private OfBytes(final Class<X> attributeClass) {
            super(attributeClass);
        }

        @Override
        public byte[] convertToDatabaseColumn(final X attribute) {
            if (attribute == null) {
                return null;
            }
            return attribute.toString().getBytes(StandardCharsets.UTF_8);
        }

        @Override
        public X convertToEntityAttribute(final byte[] dbData) {
            if (dbData == null) {
                return null;
            }
            return super.read(() -> Json.createParser(new ByteArrayInputStream(dbData)), dbData);
        }
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    private JsonpAttributeConverter(final Class<X> attributeClass) {
        super();
        this.attributeClass = Objects.requireNonNull(attributeClass, "attributeClass is null");
    }

    // -----------------------------------------------------------------------------------------------------------------

    // dbData is only for the messages
    private X read(final Supplier<? extends JsonParser> parserSupplier, final Object dbData) {
        final JsonValue value;
        // a parser, not a JsonReader: JsonReader#readValue() reads the first value and ignores
        // whatever follows it, "1 2" or "{} []", which the server rejects
        try (var parser = parserSupplier.get()) {
            if (!parser.hasNext()) {
                throw new IllegalArgumentException("not a JSON value: " + text(dbData));
            }
            parser.next();
            value = parser.getValue();
            if (parser.hasNext()) {
                throw new IllegalArgumentException("more than one JSON value: " + text(dbData));
            }
        } catch (final JsonException | IllegalStateException | NoSuchElementException e) {
            throw new IllegalArgumentException("not a JSON value: " + text(dbData), e);
        }
        if (!attributeClass.isInstance(value)) {
            throw new IllegalArgumentException(
                    "not a " + attributeClass.getSimpleName() + ": " + text(dbData));
        }
        return attributeClass.cast(value);
    }

    private static String text(final Object dbData) {
        if (dbData instanceof byte[] bytes) {
            return bytes.length + " byte(s)";
        }
        return String.valueOf(dbData);
    }

    // -----------------------------------------------------------------------------------------------------------------
    private final Class<X> attributeClass;
}

