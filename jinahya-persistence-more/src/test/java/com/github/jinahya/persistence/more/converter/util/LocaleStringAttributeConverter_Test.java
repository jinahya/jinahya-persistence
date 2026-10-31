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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link LocaleStringAttributeConverter}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("LocaleStringAttributeConverter")
class LocaleStringAttributeConverter_Test {

    @DisplayName("OfLanguage")
    @Nested
    class OfLanguage_Test {

        private final LocaleStringAttributeConverter.OfLanguage converter =
                new LocaleStringAttributeConverter.OfLanguage();

        @DisplayName("every available locale with a language is stored as it, and read back as that language alone")
        @Test
        void __availableLocales() {
            Locale.availableLocales().filter(value -> !value.getLanguage().isEmpty()).forEach(value -> {
                final var column = converter.convertToDatabaseColumn(value);
                assertThat(column).isEqualTo(value.getLanguage());
                assertThat(converter.convertToEntityAttribute(column)).isEqualTo(Locale.of(value.getLanguage()));
            });
        }

        @DisplayName("a language-only locale is read back as itself")
        @Test
        void __roundTrip() {
            for (final var value : new Locale[]{Locale.KOREAN, Locale.ENGLISH, Locale.CHINESE}) {
                assertThat(converter.convertToEntityAttribute(converter.convertToDatabaseColumn(value)))
                        .isEqualTo(value);
            }
        }

        @DisplayName("a locale with a country is stored as its language only")
        @Test
        void __countryDropped() {
            assertThat(converter.convertToDatabaseColumn(Locale.KOREA)).isEqualTo("ko");
            assertThat(converter.convertToEntityAttribute("ko")).isEqualTo(Locale.KOREAN);
        }

        @DisplayName("a column is read back case-insensitively, as Locale.of reads it")
        @ParameterizedTest
        @ValueSource(strings = {"EN", "En", "eN"})
        void __case(final String text) {
            assertThat(converter.convertToEntityAttribute(text)).isEqualTo(Locale.ENGLISH);
        }

        @DisplayName("a locale with no language is stored as null")
        @Test
        void __noLanguage() {
            assertThat(converter.convertToDatabaseColumn(Locale.ROOT)).isNull();
            assertThat(converter.convertToDatabaseColumn(Locale.of("", "KR"))).isNull();
        }

        @DisplayName("an empty or blank column reads back as null")
        @ParameterizedTest
        @ValueSource(strings = {"", " ", "  "})
        void __blank(final String text) {
            assertThat(converter.convertToEntityAttribute(text)).isNull();
        }

        @Test
        void __null() {
            assertThat(converter.convertToDatabaseColumn(null)).isNull();
            assertThat(converter.convertToEntityAttribute(null)).isNull();
        }
    }

    @DisplayName("OfISO3Language")
    @Nested
    class OfISO3Language_Test {

        private final LocaleStringAttributeConverter.OfISO3Language converter =
                new LocaleStringAttributeConverter.OfISO3Language();

        @DisplayName("every available locale with a language is stored as its ISO3 language, and read back as that "
                     + "language alone")
        @Test
        void __availableLocales() {
            Locale.availableLocales().filter(value -> !value.getLanguage().isEmpty()).forEach(value -> {
                final var column = converter.convertToDatabaseColumn(value);
                assertThat(column).isEqualTo(value.getISO3Language());
                assertThat(converter.convertToEntityAttribute(column)).isEqualTo(Locale.of(value.getLanguage()));
            });
        }

        @DisplayName("every ISO 639 alpha-2 language, legacy codes included, is read back as its language")
        @Test
        void __isoLanguages() {
            for (final var language : Locale.getISOLanguages()) {
                final var value = Locale.of(language);
                assertThat(converter.convertToEntityAttribute(converter.convertToDatabaseColumn(value)))
                        .isEqualTo(value);
            }
            assertThat(converter.convertToEntityAttribute(converter.convertToDatabaseColumn(Locale.of("iw"))))
                    .isEqualTo(Locale.of("he"));
        }

        @Test
        void __examples() {
            assertThat(converter.convertToDatabaseColumn(Locale.KOREA)).isEqualTo("kor");
            assertThat(converter.convertToEntityAttribute("kor")).isEqualTo(Locale.KOREAN);
            assertThat(converter.convertToEntityAttribute("KOR")).isEqualTo(Locale.KOREAN);
            assertThat(converter.convertToDatabaseColumn(Locale.of("fil"))).isEqualTo("fil");
            assertThat(converter.convertToEntityAttribute("fil")).isEqualTo(Locale.of("fil"));
        }

        @DisplayName("a locale whose language would not read back as itself is refused")
        @ParameterizedTest
        @ValueSource(strings = {"deu", "x y", "abcd"})
        void __refusedLocale(final String language) {
            assertThatThrownBy(() -> converter.convertToDatabaseColumn(Locale.of(language)))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("a column which is not a three-letter code is refused")
        @ParameterizedTest
        @ValueSource(strings = {"ko", "k r", "kore", "k0r"})
        void __refusedColumn(final String text) {
            assertThatThrownBy(() -> converter.convertToEntityAttribute(text))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void __nullAndBlank() {
            assertThat(converter.convertToDatabaseColumn(null)).isNull();
            assertThat(converter.convertToDatabaseColumn(Locale.ROOT)).isNull();
            assertThat(converter.convertToEntityAttribute(null)).isNull();
            assertThat(converter.convertToEntityAttribute("")).isNull();
            assertThat(converter.convertToEntityAttribute("   ")).isNull();
        }
    }

    @DisplayName("OfCountry")
    @Nested
    class OfCountry_Test {

        private final LocaleStringAttributeConverter.OfCountry converter =
                new LocaleStringAttributeConverter.OfCountry();

        @DisplayName("every available locale with a country is stored as it, and read back as that country alone")
        @Test
        void __availableLocales() {
            Locale.availableLocales().filter(value -> !value.getCountry().isEmpty()).forEach(value -> {
                final var column = converter.convertToDatabaseColumn(value);
                assertThat(column).isEqualTo(value.getCountry());
                assertThat(converter.convertToEntityAttribute(column)).isEqualTo(Locale.of("", value.getCountry()));
            });
        }

        @Test
        void __examples() {
            assertThat(converter.convertToDatabaseColumn(Locale.KOREA)).isEqualTo("KR");
            assertThat(converter.convertToEntityAttribute("KR")).isEqualTo(Locale.of("", "KR"));
            assertThat(converter.convertToEntityAttribute("kr")).isEqualTo(Locale.of("", "KR"));
            assertThat(converter.convertToDatabaseColumn(Locale.of("es", "419"))).isEqualTo("419");
            assertThat(converter.convertToEntityAttribute("419")).isEqualTo(Locale.of("", "419"));
        }

        @Test
        void __nullAndBlank() {
            assertThat(converter.convertToDatabaseColumn(null)).isNull();
            assertThat(converter.convertToDatabaseColumn(Locale.KOREAN)).isNull();
            assertThat(converter.convertToEntityAttribute(null)).isNull();
            assertThat(converter.convertToEntityAttribute("")).isNull();
            assertThat(converter.convertToEntityAttribute("  ")).isNull();
        }
    }

    @DisplayName("OfISO3Country")
    @Nested
    class OfISO3Country_Test {

        private final LocaleStringAttributeConverter.OfISO3Country converter =
                new LocaleStringAttributeConverter.OfISO3Country();

        @DisplayName("every ISO 3166 alpha-2 country is stored as its ISO3 country, and read back as that country")
        @Test
        void __isoCountries() {
            for (final var country : Locale.getISOCountries()) {
                final var value = Locale.of("", country);
                final var column = converter.convertToDatabaseColumn(value);
                assertThat(column).isEqualTo(value.getISO3Country());
                assertThat(converter.convertToEntityAttribute(column)).isEqualTo(value);
            }
        }

        @Test
        void __examples() {
            assertThat(converter.convertToDatabaseColumn(Locale.KOREA)).isEqualTo("KOR");
            assertThat(converter.convertToEntityAttribute("KOR")).isEqualTo(Locale.of("", "KR"));
            assertThat(converter.convertToEntityAttribute("kor")).isEqualTo(Locale.of("", "KR"));
        }

        @DisplayName("a country with no ISO3 code is refused")
        @ParameterizedTest
        @ValueSource(strings = {"419", "150", "XK", "ZZ"})
        void __refusedLocale(final String country) {
            assertThatThrownBy(() -> converter.convertToDatabaseColumn(Locale.of("", country)))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("a column which is the ISO3 code of no country is refused")
        @ParameterizedTest
        @ValueSource(strings = {"KR", "XXX", "KORE"})
        void __refusedColumn(final String text) {
            assertThatThrownBy(() -> converter.convertToEntityAttribute(text))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void __nullAndBlank() {
            assertThat(converter.convertToDatabaseColumn(null)).isNull();
            assertThat(converter.convertToDatabaseColumn(Locale.KOREAN)).isNull();
            assertThat(converter.convertToEntityAttribute(null)).isNull();
            assertThat(converter.convertToEntityAttribute("")).isNull();
            assertThat(converter.convertToEntityAttribute("  ")).isNull();
        }
    }

    @DisplayName("OfLanguageTag")
    @Nested
    class OfLanguageTag_Test {

        private final LocaleStringAttributeConverter.OfLanguageTag converter =
                new LocaleStringAttributeConverter.OfLanguageTag();

        @DisplayName("every available locale but the legacy no_NO_NY is stored as its tag, and read back equal")
        @Test
        void __availableLocales() {
            final var legacy = Locale.of("no", "NO", "NY");
            Locale.availableLocales().filter(value -> !value.equals(legacy)).forEach(value -> {
                final var column = converter.convertToDatabaseColumn(value);
                assertThat(column).isEqualTo(value.toLanguageTag());
                assertThat(converter.convertToEntityAttribute(column)).isEqualTo(value);
            });
        }

        @Test
        void __examples() {
            assertThat(converter.convertToDatabaseColumn(Locale.KOREA)).isEqualTo("ko-KR");
            assertThat(converter.convertToDatabaseColumn(Locale.ROOT)).isEqualTo("und");
            assertThat(converter.convertToEntityAttribute("und")).isEqualTo(Locale.ROOT);
            assertThat(converter.convertToEntityAttribute("KO-kr")).isEqualTo(Locale.KOREA);
            assertThat(converter.convertToEntityAttribute("zh-Hans-CN"))
                    .isEqualTo(Locale.forLanguageTag("zh-Hans-CN"));
            assertThat(converter.convertToEntityAttribute("en-u-nu-thai-ca-japanese"))
                    .isEqualTo(Locale.forLanguageTag("en-u-ca-japanese-nu-thai"));
        }

        @DisplayName("a locale whose tag would not read back equal is refused")
        @Test
        void __refusedLocale() {
            for (final var value : new Locale[]{
                    Locale.of("no", "NO", "NY"), Locale.of("en", "U S"), Locale.of("x y"), Locale.of("en", "US", "a b")
            }) {
                assertThatThrownBy(() -> converter.convertToDatabaseColumn(value))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }

        @DisplayName("an ill-formed tag is refused, not truncated")
        @ParameterizedTest
        @ValueSource(strings = {"ko_KR", "ko-KR-!!", "-ko", "ko--KR"})
        void __refusedColumn(final String text) {
            assertThatThrownBy(() -> converter.convertToEntityAttribute(text))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void __nullAndBlank() {
            assertThat(converter.convertToDatabaseColumn(null)).isNull();
            assertThat(converter.convertToEntityAttribute(null)).isNull();
            assertThat(converter.convertToEntityAttribute("")).isNull();
            assertThat(converter.convertToEntityAttribute("  ")).isNull();
        }
    }
}

