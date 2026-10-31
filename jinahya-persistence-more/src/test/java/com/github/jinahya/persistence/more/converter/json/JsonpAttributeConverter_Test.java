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
import jakarta.json.JsonValue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings({
        "java:S3577" // Test classes should comply with a naming convention
})
class JsonpAttributeConverter_Test {

    @DisplayName("null <-> null")
    @Test
    void __null() {
        final var converter = new JsonpAttributeConverter.OfString.OfJsonValue();
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
        assertThat(converter.convertToEntityAttribute(null)).isNull();
    }

    @DisplayName("OfJsonValue round-trips every value type")
    @ParameterizedTest
    @ValueSource(strings = {"{\"a\":1}", "[1,2]", "\"s\"", "1.5", "true", "false", "null"})
    void __roundTrip(final String json) {
        final var converter = new JsonpAttributeConverter.OfString.OfJsonValue();
        final var value = converter.convertToEntityAttribute(json);
        assertThat(value).isNotNull();
        assertThat(converter.convertToDatabaseColumn(value)).isEqualTo(json);
    }

    @DisplayName("JsonValue.NULL -> \"null\", not an SQL NULL")
    @Test
    void __jsonNull() {
        final var converter = new JsonpAttributeConverter.OfString.OfJsonValue();
        assertThat(converter.convertToDatabaseColumn(JsonValue.NULL)).isEqualTo("null");
        assertThat(converter.convertToEntityAttribute("null")).isSameAs(JsonValue.NULL);
    }

    @DisplayName("OfJsonObject")
    @Test
    void __object() {
        final var converter = new JsonpAttributeConverter.OfString.OfJsonObject();
        final var object = Json.createObjectBuilder().add("a", 1).build();
        assertThat(converter.convertToEntityAttribute(converter.convertToDatabaseColumn(object)))
                .isEqualTo(object);
    }

    @DisplayName("a value of another type -> IllegalArgumentException")
    @Test
    void _IllegalArgumentException_OtherType() {
        assertThatThrownBy(() -> new JsonpAttributeConverter.OfString.OfJsonObject().convertToEntityAttribute("[]"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new JsonpAttributeConverter.OfString.OfJsonStructure().convertToEntityAttribute("1"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new JsonpAttributeConverter.OfString.OfJsonNumber().convertToEntityAttribute("\"1\""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("not exactly one JSON value -> IllegalArgumentException")
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "1 2", "{} []", "{", "nope"})
    void _IllegalArgumentException_NotOneValue(final String dbData) {
        final var converter = new JsonpAttributeConverter.OfString.OfJsonValue();
        assertThatThrownBy(() -> converter.convertToEntityAttribute(dbData))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // -------------------------------------------------------------------------------------------------------- OfBytes

    private static byte[] utf8(final String string) {
        return string.getBytes(StandardCharsets.UTF_8);
    }

    @DisplayName("OfBytes: null <-> null")
    @Test
    void __bytes_null() {
        final var converter = new JsonpAttributeConverter.OfBytes.OfJsonValue();
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
        assertThat(converter.convertToEntityAttribute(null)).isNull();
    }

    @DisplayName("OfBytes: OfJsonValue round-trips every value type, in UTF-8")
    @ParameterizedTest
    @ValueSource(strings = {"{\"a\":1}", "[1,2]", "\"s\"", "\"한글\"", "1.5", "true", "false", "null"})
    void __bytes_roundTrip(final String json) {
        final var converter = new JsonpAttributeConverter.OfBytes.OfJsonValue();
        final var value = converter.convertToEntityAttribute(utf8(json));
        assertThat(value).isNotNull();
        assertThat(converter.convertToDatabaseColumn(value)).isEqualTo(utf8(json));
    }

    @DisplayName("OfBytes: the JSON text in UTF-16 is read, too")
    @Test
    void __bytes_utf16() {
        final var converter = new JsonpAttributeConverter.OfBytes.OfJsonObject();
        final var object = converter.convertToEntityAttribute("{\"한\":1}".getBytes(StandardCharsets.UTF_16BE));
        assertThat(object).isEqualTo(Json.createObjectBuilder().add("한", 1).build());
    }

    @DisplayName("OfBytes: OfJsonObject")
    @Test
    void __bytes_object() {
        final var converter = new JsonpAttributeConverter.OfBytes.OfJsonObject();
        final var object = Json.createObjectBuilder().add("a", 1).build();
        assertThat(converter.convertToEntityAttribute(converter.convertToDatabaseColumn(object)))
                .isEqualTo(object);
    }

    @DisplayName("OfBytes: a value of another type -> IllegalArgumentException")
    @Test
    void _IllegalArgumentException_bytes_OtherType() {
        final var object = new JsonpAttributeConverter.OfBytes.OfJsonObject();
        assertThatThrownBy(() -> object.convertToEntityAttribute(utf8("[]")))
                .isInstanceOf(IllegalArgumentException.class);
        final var structure = new JsonpAttributeConverter.OfBytes.OfJsonStructure();
        assertThatThrownBy(() -> structure.convertToEntityAttribute(utf8("1")))
                .isInstanceOf(IllegalArgumentException.class);
        final var number = new JsonpAttributeConverter.OfBytes.OfJsonNumber();
        assertThatThrownBy(() -> number.convertToEntityAttribute(utf8("\"1\"")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("OfBytes: not exactly one JSON value -> IllegalArgumentException")
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "1 2", "{} []", "{", "nope"})
    void _IllegalArgumentException_bytes_NotOneValue(final String dbData) {
        final var converter = new JsonpAttributeConverter.OfBytes.OfJsonValue();
        assertThatThrownBy(() -> converter.convertToEntityAttribute(utf8(dbData)))
                .isInstanceOf(IllegalArgumentException.class);
    }
}

