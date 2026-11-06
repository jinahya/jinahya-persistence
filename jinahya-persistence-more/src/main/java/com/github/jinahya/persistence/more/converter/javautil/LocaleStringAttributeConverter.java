package com.github.jinahya.persistence.more.converter.javautil;

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

import com.github.jinahya.persistence.more.converter.StringAttributeConverter;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.IllformedLocaleException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * An abstract converter for an entity attribute of {@link Locale}, stored as a {@link String}.
 * <p>
 * How a locale is spelled in the column is left to each subclass. The nested converters store one of six forms:
 * <table>
 * <caption>Converters</caption>
 * <thead>
 * <tr><th>Converter</th><th>Form</th><th>Example</th><th>Stored</th></tr>
 * </thead>
 * <tbody>
 * <tr><td>{@link OfLanguage}</td><td>{@link Locale#getLanguage()}</td><td>{@code "ko"}</td><td>the language</td>
 * </tr>
 * <tr><td>{@link OfISO3Language}</td><td>{@link Locale#getISO3Language()}</td><td>{@code "kor"}</td>
 * <td>the language</td></tr>
 * <tr><td>{@link OfCountry}</td><td>{@link Locale#getCountry()}</td><td>{@code "KR"}</td><td>the country</td></tr>
 * <tr><td>{@link OfISO3Country}</td><td>{@link Locale#getISO3Country()}</td><td>{@code "KOR"}</td>
 * <td>the country</td></tr>
 * <tr><td>{@link OfLanguageTag}</td><td>{@link Locale#toLanguageTag()}</td><td>{@code "ko-KR"}</td>
 * <td>the whole locale</td></tr>
 * <tr><td>{@link OfLenient}</td><td>the arguments of {@link Locale#of(String, String, String)}</td>
 * <td>{@code "ko_KR"}</td><td>the language, the country and the variant</td></tr>
 * </tbody>
 * </table>
 * <p>
 * These forms of a locale are read by none of them:
 * <ul>
 * <li>The script and the extensions of {@link Locale#toString()} &mdash; the {@code "#Hans"} of
 * {@code "zh_CN_#Hans"}, the {@code "#u-ca-dangi"} of {@code "ko_KR_#u-ca-dangi"}. The JDK has no method which reads
 * them back, and {@link Locale#toString()} itself points to {@link Locale#toLanguageTag()} for a form to read.
 * {@link OfLenient} reads the rest of such a column, and drops them; a column of this form whose scripts and
 * extensions matter is to be rewritten as language tags, which {@link OfLanguageTag} reads.</li>
 * <li>{@link Locale#getDisplayName() A display name} &mdash; {@code "Korean (South Korea)"}, {@code "한국어 (대한민국)"}
 * &mdash; which depends on the locale it is displayed in, and does not read back.</li>
 * <li>A POSIX locale of an environment &mdash; {@code "en_US.UTF-8"}, {@code "de_DE@euro"} &mdash; whose encoding and
 * modifier are no part of a {@link Locale}.</li>
 * </ul>
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public abstract class LocaleStringAttributeConverter implements StringAttributeConverter<Locale> {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    /**
     * A converter for an entity attribute of {@link Locale}, stored as its {@link Locale#getLanguage() language}.
     *
     * @implSpec A value is stored as {@link Locale#getLanguage()} &mdash; {@code "ko"}, {@code "en"} &mdash;
     *         and read back with {@link Locale#of(String)}, which is its inverse: the column reads back as the locale
     *         of that language alone. A locale with no language, whose {@link Locale#getLanguage()} is empty &mdash;
     *         {@link Locale#ROOT}, or a country-only locale &mdash; is stored as {@code null}, and an empty or blank
     *         column reads back as {@code null}.
     * @apiNote Only the language is stored. Anything else the locale has &mdash; its country, script, variant,
     *         and extensions &mdash; is dropped: {@code en_US} and {@code en_GB} are both stored as {@code "en"}, and
     *         read back as {@link Locale#ENGLISH}. {@link Locale#ROOT} does not survive a round trip; it reads back as
     *         {@code null}.
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
         * @return the language of the {@code attribute}; {@code null} when the {@code attribute} is {@code null}, or
         *         has no language.
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
         * @return the locale of the {@code dbData} language; {@code null} when the {@code dbData} is {@code null}, or
         *         blank.
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
     * A converter for an entity attribute of {@link Locale}, stored as the
     * {@link Locale#getISO3Language() three-letter code} of its language.
     *
     * @implSpec A value is stored as {@link Locale#getISO3Language()} &mdash; {@code "kor"}, {@code "eng"},
     *         {@code "fil"} &mdash; and read back as the locale of that language alone: a code which is the
     *         three-letter code of an {@link Locale#getISOLanguages() ISO 639 alpha-2 language} reads back as that
     *         language ({@code "kor"} as {@code ko}), and any other three-letter code as the language of that code
     *         ({@code "fil"} as {@code fil}). A locale with no language is stored as {@code null}, and an empty or
     *         blank column reads back as {@code null}. A column is read case-insensitively.
     * @apiNote Only the language is stored, as {@link OfLanguage} stores it. A locale is refused, with an
     *         {@link IllegalArgumentException}, when its language has no three-letter code, or when the code would not
     *         read back as that language &mdash; {@code Locale.of("deu")}, whose code {@code "deu"} reads back as
     *         {@code de}. A column holding an ISO 639-2 bibliographic code &mdash; {@code "ger"} rather than
     *         {@code "deu"} &mdash; is not one this converter writes, and reads back as a language of that code, not as
     *         {@code de}.
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
         * @throws IllegalArgumentException when the language of the {@code attribute} has no three-letter code, or the
         *                                  code does not read back as that language.
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
     * @implSpec A value is stored as {@link Locale#getCountry()} &mdash; {@code "KR"}, {@code "US"},
     *         {@code "419"} &mdash; and read back with {@link Locale#of(String, String) Locale.of("", country)}, which
     *         is its inverse: the column reads back as the locale of that country alone. A locale with no country,
     *         whose {@link Locale#getCountry()} is empty, is stored as {@code null}, and an empty or blank column reads
     *         back as {@code null}.
     * @apiNote Only the country is stored. Anything else the locale has &mdash; its language, script, variant,
     *         and extensions &mdash; is dropped: {@code ko_KR} and {@code en_KR} are both stored as {@code "KR"}, and
     *         read back as a locale of no language, which {@link OfLanguage} stores as {@code null}. A UN M.49 region
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
         * @return the country of the {@code attribute}; {@code null} when the {@code attribute} is {@code null}, or has
         *         no country.
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
     * A converter for an entity attribute of {@link Locale}, stored as the
     * {@link Locale#getISO3Country() three-letter code} of its country.
     *
     * @implSpec A value is stored as {@link Locale#getISO3Country()} &mdash; {@code "KOR"}, {@code "USA"}
     *         &mdash; and read back as the locale of the {@link Locale#getISOCountries() ISO 3166 alpha-2 country} of
     *         that code alone ({@code "KOR"} as {@code _KR}). A locale with no country is stored as {@code null}, and
     *         an empty or blank column reads back as {@code null}. A column is read case-insensitively.
     * @apiNote Only the country is stored, as {@link OfCountry} stores it. A locale is refused, with an
     *         {@link IllegalArgumentException}, when its country has no three-letter code &mdash; a UN M.49 region such
     *         as {@code "419"}, or a code outside ISO 3166 such as {@code "XK"} &mdash; and so is a column which is the
     *         three-letter code of no ISO 3166 country.
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
         * @throws IllegalArgumentException when the country of the {@code attribute} has no three-letter code, or the
         *                                  code does not read back as that country.
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
     * A converter for an entity attribute of {@link Locale}, stored as its
     * {@link Locale#toLanguageTag() IETF BCP 47 language tag}.
     *
     * @implSpec A value is stored as {@link Locale#toLanguageTag()} &mdash; {@code "ko-KR"},
     *         {@code "zh-Hans-CN"}, {@code "ja-JP-u-ca-japanese-x-lvariant-JP"}, {@code "und"} for {@link Locale#ROOT}
     *         &mdash; and read back with {@link Locale.Builder#setLanguageTag(String)}, which reads it as
     *         {@link Locale#forLanguageTag(String)} does, but refuses an ill-formed tag rather than dropping what
     *         follows the first ill-formed subtag. The whole locale is stored, and reads back equal to it. An empty or
     *         blank column reads back as {@code null}.
     * @apiNote {@link Locale#toLanguageTag()} drops the fields of a locale which are ill-formed in BCP 47, so a
     *         locale is refused, with an {@link IllegalArgumentException}, when its tag does not read back equal to it
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
         * @return the locale of the {@code dbData} language tag; {@code null} when the {@code dbData} is {@code null},
         *         or blank.
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

    /**
     * A converter for an entity attribute of {@link Locale}, stored as the three arguments of
     * {@link Locale#of(String, String, String)}, of which the last two are optional.
     * <p>
     * A value is stored as its {@link Locale#getLanguage() language}, {@link Locale#getCountry() country} and
     * {@link Locale#getVariant() variant}, joined by {@code '_'}, with the empty ones at the end left out &mdash;
     * {@code "ko"}, {@code "ko_KR"}, {@code "_KR"}, {@code "__POSIX"}, {@code "en_US_POSIX"} &mdash; which is
     * {@link Locale#toString()} for a locale of no script and no extensions.
     * <p>
     * A column is read against three patterns, from the longest to the shortest, and the first which matches it gives
     * the arguments of {@link Locale#of(String, String, String)}, {@link Locale#of(String, String)} or
     * {@link Locale#of(String)}:
     * <table>
     * <caption>Patterns</caption>
     * <thead>
     * <tr><th>Pattern</th><th>Column</th><th>Read as</th></tr>
     * </thead>
     * <tbody>
     * <tr><td><i>language</i>{@code _}<i>country</i>{@code _}<i>variant</i></td><td>{@code "en_US_POSIX"}</td>
     * <td>{@code Locale.of("en", "US", "POSIX")}</td></tr>
     * <tr><td><i>language</i>{@code _}<i>country</i></td><td>{@code "ko_KR"}</td>
     * <td>{@code Locale.of("ko", "KR")}</td></tr>
     * <tr><td><i>language</i></td><td>{@code "ko"}</td><td>{@code Locale.of("ko")}</td></tr>
     * </tbody>
     * </table>
     * <p>
     * A language and a country have neither a {@code '_'} nor a {@code '#'}, and a variant has no {@code '#'}, but may
     * have a {@code '_'} of its own: {@code "de__POSIX_X"} reads as {@code Locale.of("de", "", "POSIX_X")}. Each
     * pattern ends with what {@link Locale#toString()} writes after {@code "_#"}, or {@code "#"} &mdash; the script and
     * the extensions &mdash; which is matched, and dropped: {@code "zh_CN_#Hans"} reads as
     * {@code Locale.of("zh", "CN")}.
     *
     * @implSpec Neither conversion fails. A locale's script and extensions are dropped when it is stored, and a
     *         locale with neither a language, a country nor a variant &mdash; {@link Locale#ROOT} &mdash; is stored as
     *         {@code null}. One of the patterns matches every column, as a column has no {@code '_'}, one, or more
     *         before its first {@code '#'}; and each part is read as {@link Locale#of(String, String, String)} reads
     *         it, which validates nothing, lower-cases the language, upper-cases the country and maps a legacy language
     *         ({@code "iw"} reads back as {@code he}). An empty or blank column reads back as {@code null}.
     * @apiNote A locale of no script and no extensions reads back equal to itself; so do {@code ja_JP_JP} and
     *         {@code th_TH_TH}, whose extension {@link Locale#of(String, String, String)} adds itself. Any other locale
     *         reads back without its script and extensions. A column holding a language tag is not one this converter
     *         reads: {@code "ko-KR"} reads back as a language {@code "ko-kr"}, not as {@code ko_KR}.
     */
    @Converter(autoApply = false)
    public static class OfLenient extends LocaleStringAttributeConverter
            implements AttributeConverter<Locale, String> {

        /**
         * The pattern of a language, or of a country, which has neither a {@code '_'} nor a {@code '#'}.
         */
        private static final String PART = "([^_#]*)";

        /**
         * The pattern of a variant, which may have a {@code '_'} of its own, but no {@code '#'}.
         */
        private static final String VARIANT = "([^#]*?)";

        /**
         * The pattern of the script and the extensions {@link Locale#toString()} writes after {@code "_#"}, or
         * {@code "#"}, which are dropped.
         */
        private static final String REST = "(?:_?#.*)?";

        /**
         * The patterns of a column, of three parts, of two and of one, in the order they are tried.
         */
        private static final List<Pattern> PATTERNS = List.of(
                Pattern.compile(PART + '_' + PART + '_' + VARIANT + REST, Pattern.DOTALL),
                Pattern.compile(PART + '_' + PART + REST, Pattern.DOTALL),
                Pattern.compile(PART + REST, Pattern.DOTALL)
        );

        /**
         * Creates a new instance.
         *
         * @implNote {@code public}, not {@code protected}: a {@link Converter @Converter} class is instantiated
         *         by the persistence provider, and is documented as needing a public no-argument constructor.
         */
        public OfLenient() {
            super();
        }

        /**
         * {@inheritDoc}
         *
         * @param attribute {@inheritDoc}
         * @return the language, the country and the variant of the {@code attribute}, joined by {@code '_'};
         *         {@code null} when the {@code attribute} is {@code null}, or has none of them.
         */
        @Override
        public String convertToDatabaseColumn(final Locale attribute) {
            if (attribute == null) {
                return null;
            }
            final var language = attribute.getLanguage();
            final var country = attribute.getCountry();
            final var variant = attribute.getVariant();
            if (!variant.isEmpty()) {
                return language + '_' + country + '_' + variant;
            }
            if (!country.isEmpty()) {
                return language + '_' + country;
            }
            if (!language.isEmpty()) {
                return language;
            }
            return null;
        }

        /**
         * {@inheritDoc}
         *
         * @param dbData {@inheritDoc}
         * @return the locale of the {@code dbData}'s language, country and variant; {@code null} when the
         *         {@code dbData} is {@code null}, or blank.
         * @implSpec The {@code dbData} is matched against the patterns of three parts, of two and of one, in
         *         that order, and read by the first which matches it.
         */
        @Override
        public Locale convertToEntityAttribute(final String dbData) {
            if (dbData == null || dbData.isBlank()) {
                return null;
            }
            for (final var pattern : PATTERNS) {
                final var matcher = pattern.matcher(dbData);
                if (!matcher.matches()) {
                    continue;
                }
                return switch (matcher.groupCount()) {
                    case 3 -> Locale.of(matcher.group(1), matcher.group(2), matcher.group(3));
                    case 2 -> Locale.of(matcher.group(1), matcher.group(2));
                    default -> Locale.of(matcher.group(1));
                };
            }
            // a column has no '_', one, or more; one of the patterns matches every column
            throw new AssertionError("no pattern matched: " + dbData);
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

