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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings({
        "java:S3577" // Test classes should comply with a naming convention
})
class JsonbStringAttributeConverter_Test {

    public static class Point {

        public int x;

        public int y;
    }

    static class PointConverter extends JsonbStringAttributeConverter<Point> {

        PointConverter() {
            super(Point.class);
        }
    }

    static class NamesConverter extends JsonbStringAttributeConverter<List<String>> {

        NamesConverter() {
            super(new ArrayList<String>() {
            }.getClass().getGenericSuperclass());
        }
    }

    // what the container does when the provider obtains a converter through CDI
    private static <T extends JsonbStringAttributeConverter<?>> T constructed(final T converter) {
        invoke(converter, "createJsonb");
        return converter;
    }

    private static void invoke(final JsonbStringAttributeConverter<?> converter, final String name) {
        try {
            final var method = JsonbStringAttributeConverter.class.getDeclaredMethod(name);
            method.setAccessible(true);
            method.invoke(converter);
        } catch (final ReflectiveOperationException roe) {
            throw new RuntimeException(roe);
        }
    }

    @DisplayName("@PostConstruct, then @PreDestroy")
    @Test
    void __lifecycle() {
        final var converter = constructed(new PointConverter());
        assertThat(converter.convertToDatabaseColumn(new Point())).isEqualTo("{\"x\":0,\"y\":0}");
        invoke(converter, "closeJsonb");
        assertThatThrownBy(() -> converter.convertToDatabaseColumn(new Point()))
                .isInstanceOf(NullPointerException.class);
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

    @DisplayName("not a JSON of the type -> IllegalArgumentException")
    @Test
    void _IllegalArgumentException_NotJson() {
        final var converter = constructed(new PointConverter());
        assertThatThrownBy(() -> converter.convertToEntityAttribute("{"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
