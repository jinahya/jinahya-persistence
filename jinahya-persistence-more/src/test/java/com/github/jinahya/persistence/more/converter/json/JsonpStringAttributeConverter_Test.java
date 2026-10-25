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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings({
        "java:S3577" // Test classes should comply with a naming convention
})
class JsonpStringAttributeConverter_Test {

    @DisplayName("null <-> null")
    @Test
    void __null() {
        final var converter = new JsonpStringAttributeConverter.OfJsonValue();
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
        assertThat(converter.convertToEntityAttribute(null)).isNull();
    }

    @DisplayName("OfJsonValue round-trips every value type")
    @ParameterizedTest
    @ValueSource(strings = {"{\"a\":1}", "[1,2]", "\"s\"", "1.5", "true", "false", "null"})
    void __roundTrip(final String json) {
        final var converter = new JsonpStringAttributeConverter.OfJsonValue();
        final var value = converter.convertToEntityAttribute(json);
        assertThat(value).isNotNull();
        assertThat(converter.convertToDatabaseColumn(value)).isEqualTo(json);
    }

    @DisplayName("JsonValue.NULL -> \"null\", not an SQL NULL")
    @Test
    void __jsonNull() {
        final var converter = new JsonpStringAttributeConverter.OfJsonValue();
        assertThat(converter.convertToDatabaseColumn(JsonValue.NULL)).isEqualTo("null");
        assertThat(converter.convertToEntityAttribute("null")).isSameAs(JsonValue.NULL);
    }

    @DisplayName("OfJsonObject")
    @Test
    void __object() {
        final var converter = new JsonpStringAttributeConverter.OfJsonObject();
        final var object = Json.createObjectBuilder().add("a", 1).build();
        assertThat(converter.convertToEntityAttribute(converter.convertToDatabaseColumn(object)))
                .isEqualTo(object);
    }

    @DisplayName("a value of another type -> IllegalArgumentException")
    @Test
    void _IllegalArgumentException_OtherType() {
        assertThatThrownBy(() -> new JsonpStringAttributeConverter.OfJsonObject().convertToEntityAttribute("[]"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new JsonpStringAttributeConverter.OfJsonStructure().convertToEntityAttribute("1"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new JsonpStringAttributeConverter.OfJsonNumber().convertToEntityAttribute("\"1\""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("not exactly one JSON value -> IllegalArgumentException")
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "1 2", "{} []", "{", "nope"})
    void _IllegalArgumentException_NotOneValue(final String dbData) {
        final var converter = new JsonpStringAttributeConverter.OfJsonValue();
        assertThatThrownBy(() -> converter.convertToEntityAttribute(dbData))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
