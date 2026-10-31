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

import jakarta.json.bind.JsonbException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings({
        "java:S3577" // Test classes should comply with a naming convention
})
class JsonbAttributeConverter_Test {

    public static class Point {

        public int x;

        public int y;
    }

    static class PointConverter extends JsonbAttributeConverter.OfString<Point> {

        PointConverter() {
            super(Point.class);
        }
    }

    static class NamesConverter extends JsonbAttributeConverter.OfString<List<String>> {

        NamesConverter() {
            super(new ArrayList<String>() {
            }.getClass().getGenericSuperclass());
        }
    }

    // what the container does when the provider obtains a converter through CDI
    private static <T extends JsonbAttributeConverter<?, ?>> T constructed(final T converter) {
        invoke(converter, "createJsonb");
        return converter;
    }

    private static void invoke(final JsonbAttributeConverter<?, ?> converter, final String name) {
        try {
            final var method = JsonbAttributeConverter.class.getDeclaredMethod(name);
            method.setAccessible(true);
            method.invoke(converter);
        } catch (final ReflectiveOperationException roe) {
            throw new RuntimeException(roe);
        }
    }

    @DisplayName("@PostConstruct, then @PreDestroy, after which each conversion uses its own jsonb")
    @Test
    void __lifecycle() {
        final var converter = constructed(new PointConverter());
        assertThat(converter.convertToDatabaseColumn(new Point())).isEqualTo("{\"x\":0,\"y\":0}");
        invoke(converter, "closeJsonb");
        assertThat(converter.convertToDatabaseColumn(new Point())).isEqualTo("{\"x\":0,\"y\":0}");
        invoke(converter, "closeJsonb"); // twice is harmless
    }

    @DisplayName("without CDI, no lifecycle method is called, and each conversion uses its own jsonb")
    @Test
    void __withoutCdi() {
        final var converter = new PointConverter();
        final var point = new Point();
        point.x = 1;
        point.y = 2;
        final var column = converter.convertToDatabaseColumn(point);
        assertThat(column).isEqualTo("{\"x\":1,\"y\":2}");
        final var read = converter.convertToEntityAttribute(column);
        assertThat(read).isNotNull();
        assertThat(read.x).isEqualTo(1);
        assertThat(read.y).isEqualTo(2);
        assertThat(new NamesConverter().convertToEntityAttribute("[\"a\",\"b\"]")).containsExactly("a", "b");
    }

    @DisplayName("without CDI, not a JSON of the type -> JsonbException")
    @Test
    void _JsonbException_NotJson_WithoutCdi() {
        final var converter = new PointConverter();
        assertThatThrownBy(() -> converter.convertToEntityAttribute("{"))
                .isInstanceOf(JsonbException.class);
    }

    @DisplayName("null <-> null")
    @Test
    void __null() {
        final var converter = constructed(new PointConverter());
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
        assertThat(converter.convertToEntityAttribute(null)).isNull();
    }

    @DisplayName("a class round-trips")
    @Test
    void __class() {
        final var converter = constructed(new PointConverter());
        final var point = new Point();
        point.x = 1;
        point.y = 2;
        final var column = converter.convertToDatabaseColumn(point);
        assertThat(column).isEqualTo("{\"x\":1,\"y\":2}");
        final var read = converter.convertToEntityAttribute(column);
        assertThat(read).isNotNull();
        assertThat(read.x).isEqualTo(1);
        assertThat(read.y).isEqualTo(2);
    }

    @DisplayName("a parameterized type round-trips")
    @Test
    void __parameterized() {
        final var converter = constructed(new NamesConverter());
        assertThat(converter.convertToDatabaseColumn(List.of("a", "b"))).isEqualTo("[\"a\",\"b\"]");
        assertThat(converter.convertToEntityAttribute("[\"a\",\"b\"]")).containsExactly("a", "b");
    }

    @DisplayName("not a JSON of the type -> JsonbException")
    @Test
    void _JsonbException_NotJson() {
        final var converter = constructed(new PointConverter());
        assertThatThrownBy(() -> converter.convertToEntityAttribute("{"))
                .isInstanceOf(JsonbException.class);
    }

    // -------------------------------------------------------------------------------------------------------- OfBytes

    static class PointBytesConverter extends JsonbAttributeConverter.OfBytes<Point> {

        PointBytesConverter() {
            super(Point.class);
        }
    }

    static class NamesBytesConverter extends JsonbAttributeConverter.OfBytes<List<String>> {

        NamesBytesConverter() {
            super(new ArrayList<String>() {
            }.getClass().getGenericSuperclass());
        }
    }

    @DisplayName("OfBytes: null <-> null")
    @Test
    void __bytes_null() {
        final var converter = new PointBytesConverter();
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
        assertThat(converter.convertToEntityAttribute(null)).isNull();
    }

    @DisplayName("OfBytes: a class round-trips, with and without CDI")
    @Test
    void __bytes_class() {
        for (final var converter : List.of(new PointBytesConverter(), constructed(new PointBytesConverter()))) {
            final var point = new Point();
            point.x = 1;
            point.y = 2;
            final var column = converter.convertToDatabaseColumn(point);
            assertThat(column).isEqualTo("{\"x\":1,\"y\":2}".getBytes(StandardCharsets.UTF_8));
            final var read = converter.convertToEntityAttribute(column);
            assertThat(read).isNotNull();
            assertThat(read.x).isEqualTo(1);
            assertThat(read.y).isEqualTo(2);
        }
    }

    @DisplayName("OfBytes: the JSON text is stored in UTF-8")
    @Test
    void __bytes_utf8() {
        final var converter = new NamesBytesConverter();
        final var column = converter.convertToDatabaseColumn(List.of("한글", "é"));
        assertThat(column).isEqualTo("[\"한글\",\"é\"]".getBytes(StandardCharsets.UTF_8));
        assertThat(converter.convertToEntityAttribute(column)).containsExactly("한글", "é");
    }

    @DisplayName("OfBytes: not a JSON of the type -> JsonbException")
    @Test
    void _JsonbException_bytes_NotJson() {
        final var converter = new PointBytesConverter();
        assertThatThrownBy(() -> converter.convertToEntityAttribute("{".getBytes(StandardCharsets.UTF_8)))
                .isInstanceOf(JsonbException.class);
    }
}

