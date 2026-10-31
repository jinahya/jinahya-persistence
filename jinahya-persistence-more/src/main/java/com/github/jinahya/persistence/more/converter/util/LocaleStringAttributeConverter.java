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
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.IllformedLocaleException;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * An abstract converter for an entity attribute of {@link Locale}, stored as a {@link String}.
 * <p>
 * How a locale is spelled in the column is left to each subclass.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public abstract class LocaleStringAttributeConverter implements __StringAttributeConverter<Locale> {

    /**
     * A converter for an entity attribute of {@link Locale}, stored as its {@link Locale#getLanguage() language}.
     *
     * @implSpec A value is stored as {@link Locale#getLanguage()} &mdash; {@code "ko"}, {@code "en"} &mdash; and
     *         read back with {@link Locale#of(String)}, which is its inverse: the column reads back as the locale of
     *         that language alone. A locale with no language, whose {@link Locale#getLanguage()} is empty
     *         &mdash; {@link Locale#ROOT}, or a country-only locale &mdash; is stored as {@code null}, and an empty or
     *         blank column reads back as {@code null}.
     * @apiNote Only the language is stored. Anything else the locale has &mdash; its country, script, variant,
     *         and extensions &mdash; is dropped: {@code en_US} and {@code en_GB} are both stored as {@code "en"}, and
     *         read back as {@link Locale#ENGLISH}. {@link Locale#ROOT} does not survive a round trip; it reads back
     *         as {@code null}.
     */
    @Converter(autoApply = false)
    public static class OfLanguage extends LocaleStringAttributeConverter
            implements AttributeConverter<Locale, String> {

        /**
         * Creates a new instance.
         *
         * @implNote {@code public}, not {@code protected}: a {@link Converter @Converter} class is instantiated
         *         by the persistence provider, and is documented as needing a public no-argument constructor.
         */
        public OfLanguage() {
            super();
        }

        /**
         * {@inheritDoc}
         *
         * @param attribute {@inheritDoc}
         * @return the language of the {@code attribute}; {@code null} when the {@code attribute} is {@code null},
         *         or has no language.
         */
        @Override
        public String convertToDatabaseColumn(final Locale attribute) {
            if (attribute == null) {
                return null;
            }
            final var language = attribute.getLanguage();
            if (language.isEmpty()) {
                return null;
            }
            return language;
        }

        /**
         * {@inheritDoc}
         *
         * @param dbData {@inheritDoc}
         * @return the locale of the {@code dbData} language; {@code null} when the {@code dbData} is {@code null},
         *         or blank.
         */
        @Override
        public Locale convertToEntityAttribute(final String dbData) {
            if (dbData == null || dbData.isBlank()) {
                return null;
            }
            return Locale.of(dbData);
        }
    }

    /**
     * A converter for an entity attribute of {@link Locale}, stored as the {@link Locale#getISO3Language() three-letter
     * code} of its language.
     *
     * @implSpec A value is stored as {@link Locale#getISO3Language()} &mdash; {@code "kor"}, {@code "eng"},
     *         {@code "fil"} &mdash; and read back as the locale of that language alone: a code which is the
     *         three-letter code of an {@link Locale#getISOLanguages() ISO 639 alpha-2 language} reads back as that
     *         language ({@code "kor"} as {@code ko}), and any other three-letter code as the language of that code
     *         ({@code "fil"} as {@code fil}). A locale with no language is stored as {@code null}, and an empty or
     *         blank column reads back as {@code null}. A column is read case-insensitively.
     * @apiNote Only the language is stored, as {@link OfLanguage} stores it. A locale is refused, with an
     *         {@link IllegalArgumentException}, when its language has no three-letter code, or when the code would
     *         not read back as that language &mdash; {@code Locale.of("deu")}, whose code {@code "deu"} reads back as
     *         {@code de}. A column holding an ISO 639-2 bibliographic code &mdash; {@code "ger"} rather than
     *         {@code "deu"} &mdash; is not one this converter writes, and reads back as a language of that code, not
     *         as {@code de}.
     */
    @Converter(autoApply = false)
    public static class OfISO3Language extends LocaleStringAttributeConverter
            implements AttributeConverter<Locale, String> {

        /**
         * The language of each {@link Locale#getISOLanguages() ISO 639 alpha-2 language}, keyed by its three-letter
         * code.
         */
        private static final Map<String, String> LANGUAGES = Arrays.stream(Locale.getISOLanguages())
                .map(Locale::of)
                .collect(Collectors.toUnmodifiableMap(Locale::getISO3Language, Locale::getLanguage, (l1, l2) -> l1));

        /**
         * The pattern of a three-letter language code, in lower case.
         */
        private static final Pattern ALPHA3 = Pattern.compile("[a-z]{3}");

        /**
         * Creates a new instance.
         *
         * @implNote {@code public}, not {@code protected}: a {@link Converter @Converter} class is instantiated
         *         by the persistence provider, and is documented as needing a public no-argument constructor.
         */
        public OfISO3Language() {
            super();
        }

        /**
         * {@inheritDoc}
         *
         * @param attribute {@inheritDoc}
         * @return the three-letter code of the language of the {@code attribute}; {@code null} when the
         *         {@code attribute} is {@code null}, or has no language.
         * @throws IllegalArgumentException when the language of the {@code attribute} has no three-letter code, or
         *                                  the code does not read back as that language.
         */
        @Override
        public String convertToDatabaseColumn(final Locale attribute) {
            if (attribute == null) {
                return null;
            }
            final var language = attribute.getLanguage();
            if (language.isEmpty()) {
                return null;
            }
            final String dbData;
            try {
                dbData = attribute.getISO3Language();
            } catch (final MissingResourceException mre) {
                throw new IllegalArgumentException("no three-letter code for the language: " + attribute, mre);
            }
            if (!Locale.of(language).equals(read(dbData))) {
                throw new IllegalArgumentException("not read back as its language: " + attribute);
            }
            return dbData;
        }

        /**
         * {@inheritDoc}
         *
         * @param dbData {@inheritDoc}
         * @return the locale of the language the {@code dbData} codes; {@code null} when the {@code dbData} is
         *         {@code null}, or blank.
         * @throws IllegalArgumentException when the {@code dbData} is not a three-letter code.
         */
        @Override
        public Locale convertToEntityAttribute(final String dbData) {
            if (dbData == null || dbData.isBlank()) {
                return null;
            }
            final var locale = read(dbData);
            if (locale == null) {
                throw new IllegalArgumentException("not a three-letter language code: " + dbData);
            }
            return locale;
        }

        /**
         * Reads the specified three-letter language code.
         *
         * @param dbData the code to read.
         * @return the locale of the language the {@code dbData} codes; {@code null} when the {@code dbData} is not a
         *         three-letter code.
         */
        private static Locale read(final String dbData) {
            final var code = dbData.toLowerCase(Locale.ROOT);
            final var language = LANGUAGES.get(code);
            if (language != null) {
                return Locale.of(language);
            }
            if (!ALPHA3.matcher(code).matches()) {
                return null;
            }
            return Locale.of(code);
        }
    }

    /**
     * A converter for an entity attribute of {@link Locale}, stored as its {@link Locale#getCountry() country}.
     *
     * @implSpec A value is stored as {@link Locale#getCountry()} &mdash; {@code "KR"}, {@code "US"}, {@code "419"}
     *         &mdash; and read back with {@link Locale#of(String, String) Locale.of("", country)}, which is its
     *         inverse: the column reads back as the locale of that country alone. A locale with no country, whose
     *         {@link Locale#getCountry()} is empty, is stored as {@code null}, and an empty or blank column reads back
     *         as {@code null}.
     * @apiNote Only the country is stored. Anything else the locale has &mdash; its language, script, variant, and
     *         extensions &mdash; is dropped: {@code ko_KR} and {@code en_KR} are both stored as {@code "KR"}, and read
     *         back as a locale of no language, which {@link OfLanguage} stores as {@code null}. A UN M.49 region
     *         &mdash; {@code "419"}, {@code "150"} &mdash; takes three characters.
     */
    @Converter(autoApply = false)
    public static class OfCountry extends LocaleStringAttributeConverter
            implements AttributeConverter<Locale, String> {

        /**
         * Creates a new instance.
         *
         * @implNote {@code public}, not {@code protected}: a {@link Converter @Converter} class is instantiated
         *         by the persistence provider, and is documented as needing a public no-argument constructor.
         */
        public OfCountry() {
            super();
        }

        /**
         * {@inheritDoc}
         *
         * @param attribute {@inheritDoc}
         * @return the country of the {@code attribute}; {@code null} when the {@code attribute} is {@code null}, or
         *         has no country.
         */
        @Override
        public String convertToDatabaseColumn(final Locale attribute) {
            if (attribute == null) {
                return null;
            }
            final var country = attribute.getCountry();
            if (country.isEmpty()) {
                return null;
            }
            return country;
        }

        /**
         * {@inheritDoc}
         *
         * @param dbData {@inheritDoc}
         * @return the locale of the {@code dbData} country; {@code null} when the {@code dbData} is {@code null}, or
         *         blank.
         */
        @Override
        public Locale convertToEntityAttribute(final String dbData) {
            if (dbData == null || dbData.isBlank()) {
                return null;
            }
            return Locale.of("", dbData);
        }
    }

    /**
     * A converter for an entity attribute of {@link Locale}, stored as the {@link Locale#getISO3Country() three-letter
     * code} of its country.
     *
     * @implSpec A value is stored as {@link Locale#getISO3Country()} &mdash; {@code "KOR"}, {@code "USA"} &mdash; and
     *         read back as the locale of the {@link Locale#getISOCountries() ISO 3166 alpha-2 country} of that code
     *         alone ({@code "KOR"} as {@code _KR}). A locale with no country is stored as {@code null}, and an empty
     *         or blank column reads back as {@code null}. A column is read case-insensitively.
     * @apiNote Only the country is stored, as {@link OfCountry} stores it. A locale is refused, with an
     *         {@link IllegalArgumentException}, when its country has no three-letter code &mdash; a UN M.49 region
     *         such as {@code "419"}, or a code outside ISO 3166 such as {@code "XK"} &mdash; and so is a column which
     *         is the three-letter code of no ISO 3166 country.
     */
    @Converter(autoApply = false)
    public static class OfISO3Country extends LocaleStringAttributeConverter
            implements AttributeConverter<Locale, String> {

        /**
         * Each {@link Locale#getISOCountries() ISO 3166 alpha-2 country}, keyed by its three-letter code.
         */
        private static final Map<String, String> COUNTRIES = Arrays.stream(Locale.getISOCountries())
                .collect(Collectors.toUnmodifiableMap(c -> Locale.of("", c).getISO3Country(), Function.identity()));

        /**
         * Creates a new instance.
         *
         * @implNote {@code public}, not {@code protected}: a {@link Converter @Converter} class is instantiated
         *         by the persistence provider, and is documented as needing a public no-argument constructor.
         */
        public OfISO3Country() {
            super();
        }

        /**
         * {@inheritDoc}
         *
         * @param attribute {@inheritDoc}
         * @return the three-letter code of the country of the {@code attribute}; {@code null} when the
         *         {@code attribute} is {@code null}, or has no country.
         * @throws IllegalArgumentException when the country of the {@code attribute} has no three-letter code, or
         *                                  the code does not read back as that country.
         */
        @Override
        public String convertToDatabaseColumn(final Locale attribute) {
            if (attribute == null) {
                return null;
            }
            final var country = attribute.getCountry();
            if (country.isEmpty()) {
                return null;
            }
            final String dbData;
            try {
                dbData = attribute.getISO3Country();
            } catch (final MissingResourceException mre) {
                throw new IllegalArgumentException("no three-letter code for the country: " + attribute, mre);
            }
            if (!country.equals(COUNTRIES.get(dbData))) {
                throw new IllegalArgumentException("not read back as its country: " + attribute);
            }
            return dbData;
        }

        /**
         * {@inheritDoc}
         *
         * @param dbData {@inheritDoc}
         * @return the locale of the country the {@code dbData} codes; {@code null} when the {@code dbData} is
         *         {@code null}, or blank.
         * @throws IllegalArgumentException when the {@code dbData} is the three-letter code of no ISO 3166 country.
         */
        @Override
        public Locale convertToEntityAttribute(final String dbData) {
            if (dbData == null || dbData.isBlank()) {
                return null;
            }
            final var country = COUNTRIES.get(dbData.toUpperCase(Locale.ROOT));
            if (country == null) {
                throw new IllegalArgumentException("not a three-letter country code: " + dbData);
            }
            return Locale.of("", country);
        }
    }

    /**
     * A converter for an entity attribute of {@link Locale}, stored as its {@link Locale#toLanguageTag() IETF BCP 47
     * language tag}.
     *
     * @implSpec A value is stored as {@link Locale#toLanguageTag()} &mdash; {@code "ko-KR"}, {@code "zh-Hans-CN"},
     *         {@code "ja-JP-u-ca-japanese-x-lvariant-JP"}, {@code "und"} for {@link Locale#ROOT} &mdash; and read back
     *         with {@link Locale.Builder#setLanguageTag(String)}, which reads it as
     *         {@link Locale#forLanguageTag(String)} does, but refuses an ill-formed tag rather than dropping what
     *         follows the first ill-formed subtag. The whole locale is stored, and reads back equal to it. An empty or
     *         blank column reads back as {@code null}.
     * @apiNote {@link Locale#toLanguageTag()} drops the fields of a locale which are ill-formed in BCP 47, so a locale
     *         is refused, with an {@link IllegalArgumentException}, when its tag does not read back equal to it
     *         &mdash; {@code Locale.of("en", "U S")}, whose tag is {@code "en"}, or the legacy {@code no_NO_NY}, whose
     *         tag {@code "nn-NO"} reads back as {@code nn_NO}. A column is read case-insensitively, and a tag
     *         equivalent to one this converter writes &mdash; {@code "KO-kr"}, extensions in another order, a
     *         grandfathered tag such as {@code "i-klingon"} &mdash; reads back as the locale it means.
     */
    @Converter(autoApply = false)
    public static class OfLanguageTag extends LocaleStringAttributeConverter
            implements AttributeConverter<Locale, String> {

        /**
         * Creates a new instance.
         *
         * @implNote {@code public}, not {@code protected}: a {@link Converter @Converter} class is instantiated
         *         by the persistence provider, and is documented as needing a public no-argument constructor.
         */
        public OfLanguageTag() {
            super();
        }

        /**
         * {@inheritDoc}
         *
         * @param attribute {@inheritDoc}
         * @return the language tag of the {@code attribute}; {@code null} when the {@code attribute} is {@code null}.
         * @throws IllegalArgumentException when the language tag of the {@code attribute} does not read back equal to
         *                                  it.
         */
        @Override
        public String convertToDatabaseColumn(final Locale attribute) {
            if (attribute == null) {
                return null;
            }
            final var dbData = attribute.toLanguageTag();
            if (!attribute.equals(convertToEntityAttribute(dbData))) {
                throw new IllegalArgumentException(
                        "not read back from its language tag (" + dbData + "): " + attribute);
            }
            return dbData;
        }

        /**
         * {@inheritDoc}
         *
         * @param dbData {@inheritDoc}
         * @return the locale of the {@code dbData} language tag; {@code null} when the {@code dbData} is
         *         {@code null}, or blank.
         * @throws IllegalArgumentException when the {@code dbData} is not a well-formed language tag.
         */
        @Override
        public Locale convertToEntityAttribute(final String dbData) {
            if (dbData == null || dbData.isBlank()) {
                return null;
            }
            try {
                return new Locale.Builder().setLanguageTag(dbData).build();
            } catch (final IllformedLocaleException ile) {
                throw new IllegalArgumentException("not a well-formed language tag: " + dbData, ile);
            }
        }
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected LocaleStringAttributeConverter() {
        super();
    }
}

