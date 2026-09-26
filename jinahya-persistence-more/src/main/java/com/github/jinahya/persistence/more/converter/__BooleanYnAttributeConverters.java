package com.github.jinahya.persistence.more.converter;

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

import jakarta.persistence.Converter;
import org.jspecify.annotations.Nullable;

/**
 * Converters for a {@link Boolean} entity attribute stored as a yes-or-no flag.
 * <p>
 * The pairing is the one a {@code CHAR(1)} flag column has carried since long before a database had a boolean type, and
 * it is what makes the column readable in a query written by hand: {@code where active = 'Y'}.
 * <p>
 * Two choices, crossed. The column type is either a {@link Character} or a {@link String}, depending on what the
 * provider and the driver hand over; and a column value which is neither yes nor no is either refused or read as a no:
 *
 * <table class="striped">
 *   <caption>The four converters</caption>
 *   <thead>
 *     <tr><th scope="col"></th><th scope="col">refuses what it does not recognize</th>
 *         <th scope="col">reads it as {@link Boolean#FALSE}</th></tr>
 *   </thead>
 *   <tbody>
 *     <tr><th scope="row">{@link Character}</th><td>{@link OfCharacter}</td><td>{@link OfCharacterLenient}</td></tr>
 *     <tr><th scope="row">{@link String}</th><td>{@link OfString}</td><td>{@link OfStringLenient}</td></tr>
 *   </tbody>
 * </table>
 * <p>
 * Every one of them reads case-insensitively, writes upper case, leaves {@code null} as {@code null}, and implements
 * {@link __BooleanYnAttributeConverter}. A lenient converter is a sibling of its strict counterpart and not a subtype
 * of it: the strict contract promises to raise {@link IllegalArgumentException}, and something holding a strict
 * converter is entitled to that.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see __BooleanYnAttributeConverter
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public final class __BooleanYnAttributeConverters {

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * A converter for a {@link Boolean} entity attribute stored as a {@link Character} column holding {@value #YES} or
     * {@value #NO}.
     *
     * @implSpec The read is case-insensitive, and accepts nothing else: a column holding neither yes nor no is
     *         one this converter was not meant to read, and raises {@link IllegalArgumentException} rather than being
     *         guessed at. {@link OfCharacterLenient} is the one which reads such a value as {@link Boolean#FALSE}
     *         instead.
     * @see OfCharacterLenient
     */
    @Converter(autoApply = false)
    public static class OfCharacter
            implements __BooleanYnAttributeConverter<Character> {

        /**
         * The column value for {@link Boolean#TRUE}. The value is {@value}.
         */
        public static final char YES = 'Y';

        /**
         * The column value for {@link Boolean#FALSE}. The value is {@value}.
         */
        public static final char NO = 'N';

        /**
         * Creates a new instance.
         *
         * @implNote {@code public}, not {@code protected}: a {@link Converter @Converter} class is instantiated
         *         by the persistence provider, and is documented as needing a public no-argument constructor.
         */
        public OfCharacter() {
            super();
        }

        /**
         * Converts the specified entity attribute to a database column value.
         *
         * @param attribute the entity attribute to convert.
         * @return {@value #YES} when the {@code attribute} is {@link Boolean#TRUE}, {@value #NO} when it is
         *         {@link Boolean#FALSE}; {@code null} when it is {@code null}.
         */
        @Override
        public @Nullable Character convertToDatabaseColumn(final @Nullable Boolean attribute) {
            if (attribute == null) {
                return null;
            }
            return attribute ? YES : NO;
        }

        /**
         * Converts the specified database column value to an entity attribute.
         *
         * @param dbData the database column value to convert.
         * @return {@link Boolean#TRUE} when the {@code dbData} is {@value #YES}, {@link Boolean#FALSE} when it is
         *         {@value #NO}, in either case; {@code null} when it is {@code null}.
         * @throws IllegalArgumentException when the {@code dbData} is neither.
         */
        @Override
        public @Nullable Boolean convertToEntityAttribute(final @Nullable Character dbData) {
            if (dbData == null) {
                return null;
            }
            return switch (Character.toUpperCase(dbData)) {
                case YES -> Boolean.TRUE;
                case NO -> Boolean.FALSE;
                default -> throw new IllegalArgumentException(
                        "dbData('" + dbData + "') is neither '" + YES + "' nor '" + NO + "'");
            };
        }
    }

    /**
     * A converter for a {@link Boolean} entity attribute stored as a {@link Character} column, which reads anything but
     * a yes as {@link Boolean#FALSE}.
     * <p>
     * It writes exactly what {@link OfCharacter} writes, and differs only in what it accepts back. Where the strict
     * converter rejects a column value it does not recognize, this one treats every such value the way it treats a no
     * &mdash; the reading a flag column gets when only the yes is meaningful, and everything else, whatever it turns
     * out to be, means no.
     *
     * @implSpec The read never fails: a yes in either case is {@link Boolean#TRUE}, and every other value
     *         &mdash; a no, a space, a letter nobody expected &mdash; is {@link Boolean#FALSE}. Only {@code null} is
     *         kept apart, and stays {@code null}.
     * @apiNote Use this for a column whose contents are not under this application's control, and prefer
     *         {@link OfCharacter the strict one} everywhere else. What is bought here is that no row can fail to load;
     *         what is paid is that a column being read by the wrong converter looks exactly like a column full of noes,
     *         and says nothing.
     * @see OfCharacter
     */
    @Converter(autoApply = false)
    public static class OfCharacterLenient
            implements __BooleanYnAttributeConverter<Character> {

        /**
         * Creates a new instance.
         *
         * @implNote {@code public}, not {@code protected}: a {@link Converter @Converter} class is instantiated
         *         by the persistence provider, and is documented as needing a public no-argument constructor.
         */
        public OfCharacterLenient() {
            super();
            strictConverter = new OfCharacter();
        }

        /**
         * Converts the specified entity attribute to a database column value.
         *
         * @param attribute the entity attribute to convert.
         * @return {@link OfCharacter#YES 'Y'} when the {@code attribute} is {@link Boolean#TRUE},
         *         {@link OfCharacter#NO 'N'} when it is {@link Boolean#FALSE}; {@code null} when it is {@code null}.
         * @implSpec Delegated to {@link OfCharacter} unchanged.
         */
        @Override
        public @Nullable Character convertToDatabaseColumn(final @Nullable Boolean attribute) {
            return strictConverter.convertToDatabaseColumn(attribute);
        }

        /**
         * Converts the specified database column value to an entity attribute.
         *
         * @param dbData the database column value to convert.
         * @return {@link Boolean#TRUE} when the {@code dbData} is {@link OfCharacter#YES 'Y'}, in either case;
         *         {@link Boolean#FALSE} for every other value; {@code null} when it is {@code null}.
         * @implSpec {@link OfCharacter} decides the value, and its rejection is what becomes
         *         {@link Boolean#FALSE}. That is the whole difference between the two, so it is written as the whole
         *         difference: what counts as a yes is never spelled out twice, and cannot drift.
         * @implNote The unrecognized path costs an exception, which is why it is the unrecognized one. A column
         *         holding what this converter writes never reaches the {@code catch}.
         */
        @Override
        public @Nullable Boolean convertToEntityAttribute(final @Nullable Character dbData) {
            try {
                return strictConverter.convertToEntityAttribute(dbData);
            } catch (final IllegalArgumentException iae) {
                return Boolean.FALSE;
            }
        }

        /**
         * The converter this one delegates both directions to; only its rejection is treated differently.
         */
        private final OfCharacter strictConverter;
    }

    /**
     * A converter for a {@link Boolean} entity attribute stored as a {@link String} column holding {@value #YES} or
     * {@value #NO}.
     * <p>
     * The same flag as {@link OfCharacter}, for a {@code VARCHAR(1)} column rather than a {@code CHAR(1)} one, and for
     * a provider or a driver which hands that column over as a {@code String}.
     *
     * @implSpec The column value is compared to {@value #YES} and to {@value #NO} with
     *         {@link String#equalsIgnoreCase(String) equalsIgnoreCase}, and anything else raises
     *         {@link IllegalArgumentException}. No length is checked separately: {@code equalsIgnoreCase} is already
     *         false for a {@code String} of any other length, which is what keeps {@code "YES"} from being read as a
     *         yes.
     * @see OfCharacter
     * @see OfStringLenient
     */
    @Converter(autoApply = false)
    public static class OfString
            implements __BooleanYnAttributeConverter<String> {

        /**
         * The column value for {@link Boolean#TRUE}. The value is {@value}.
         *
         * @implNote Written as a concatenation of {@link OfCharacter#YES} rather than spelled out, so that the
         *         two converters can not come to disagree, and still a compile-time constant, so that it can be named
         *         in an annotation and shown by {@code @value}.
         */
        public static final String YES = "" + OfCharacter.YES;

        /**
         * The column value for {@link Boolean#FALSE}. The value is {@value}.
         *
         * @implNote See {@link #YES}.
         */
        public static final String NO = "" + OfCharacter.NO;

        /**
         * Creates a new instance.
         *
         * @implNote {@code public}, not {@code protected}: a {@link Converter @Converter} class is instantiated
         *         by the persistence provider, and is documented as needing a public no-argument constructor.
         */
        public OfString() {
            super();
        }

        /**
         * Converts the specified entity attribute to a database column value.
         *
         * @param attribute the entity attribute to convert.
         * @return {@value #YES} when the {@code attribute} is {@link Boolean#TRUE}, {@value #NO} when it is
         *         {@link Boolean#FALSE}; {@code null} when it is {@code null}.
         */
        @Override
        public @Nullable String convertToDatabaseColumn(final @Nullable Boolean attribute) {
            if (attribute == null) {
                return null;
            }
            return attribute ? YES : NO;
        }

        /**
         * Converts the specified database column value to an entity attribute.
         *
         * @param dbData the database column value to convert.
         * @return {@link Boolean#TRUE} when the {@code dbData} is {@value #YES}, {@link Boolean#FALSE} when it is
         *         {@value #NO}, in either case; {@code null} when it is {@code null}.
         * @throws IllegalArgumentException when the {@code dbData} is neither &mdash; a longer or shorter
         *                                  {@code String} included.
         */
        @Override
        public @Nullable Boolean convertToEntityAttribute(final @Nullable String dbData) {
            if (dbData == null) {
                return null;
            }
            if (YES.equalsIgnoreCase(dbData)) {
                return Boolean.TRUE;
            }
            if (NO.equalsIgnoreCase(dbData)) {
                return Boolean.FALSE;
            }
            throw new IllegalArgumentException(
                    "dbData(\"" + dbData + "\") is neither \"" + YES + "\" nor \"" + NO + "\"");
        }
    }

    /**
     * A converter for a {@link Boolean} entity attribute stored as a {@link String} column, which reads anything but a
     * yes as {@link Boolean#FALSE}.
     * <p>
     * It stands to {@link OfString} exactly as {@link OfCharacterLenient} stands to
     * {@link OfCharacter the strict character one}: the same write, and a read which turns a refusal into a no instead
     * of raising it.
     *
     * @implSpec The read never fails: a yes in either case is {@link Boolean#TRUE}, and every other value
     *         &mdash; a no, an empty string, a word, a letter nobody expected &mdash; is {@link Boolean#FALSE}. Only
     *         {@code null} is kept apart, and stays {@code null}.
     * @apiNote Use this for a column whose contents are not under this application's control, and prefer
     *         {@link OfString the strict one} everywhere else. What is bought here is that no row can fail to load;
     *         what is paid is that a column being read by the wrong converter looks exactly like a column full of noes,
     *         and says nothing.
     * @see OfString
     * @see OfCharacterLenient
     */
    @Converter(autoApply = false)
    public static class OfStringLenient
            implements __BooleanYnAttributeConverter<String> {

        /**
         * Creates a new instance.
         *
         * @implNote {@code public}, not {@code protected}: a {@link Converter @Converter} class is instantiated
         *         by the persistence provider, and is documented as needing a public no-argument constructor.
         */
        public OfStringLenient() {
            super();
            strictConverter = new OfString();
        }

        /**
         * Converts the specified entity attribute to a database column value.
         *
         * @param attribute the entity attribute to convert.
         * @return {@link OfString#YES "Y"} when the {@code attribute} is {@link Boolean#TRUE}, {@link OfString#NO "N"}
         *         when it is {@link Boolean#FALSE}; {@code null} when it is {@code null}.
         * @implSpec Delegated to {@link OfString} unchanged.
         */
        @Override
        public @Nullable String convertToDatabaseColumn(final @Nullable Boolean attribute) {
            return strictConverter.convertToDatabaseColumn(attribute);
        }

        /**
         * Converts the specified database column value to an entity attribute.
         *
         * @param dbData the database column value to convert.
         * @return {@link Boolean#TRUE} when the {@code dbData} is {@link OfString#YES "Y"}, in either case;
         *         {@link Boolean#FALSE} for every other value; {@code null} when it is {@code null}.
         * @implSpec {@link OfString} decides the value, and its rejection is what becomes
         *         {@link Boolean#FALSE}. That is the whole difference between the two, so it is written as the whole
         *         difference: what counts as a yes is never spelled out twice, and cannot drift.
         * @implNote The unrecognized path costs an exception, which is why it is the unrecognized one. A column
         *         holding what this converter writes never reaches the {@code catch}.
         */
        @Override
        public @Nullable Boolean convertToEntityAttribute(final @Nullable String dbData) {
            try {
                return strictConverter.convertToEntityAttribute(dbData);
            } catch (final IllegalArgumentException iae) {
                return Boolean.FALSE;
            }
        }

        /**
         * The converter this one delegates both directions to; only its rejection is treated differently.
         */
        private final OfString strictConverter;
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance, which is not allowed.
     */
    private __BooleanYnAttributeConverters() {
        throw new AssertionError("instantiation is not allowed");
    }
}
