package com.github.jinahya.persistence.more.converter.postgresql;

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

import jakarta.persistence.AttributeConverter;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * An abstract class for converting a {@link List} of {@code E} to a one-dimensional PostgreSQL array column, and vice
 * versa, each element through a converter of its own.
 * <p>
 * This class owns what every array has in common: the literal syntax &mdash; {@code {1,2,3}}, {@code {"a b",NULL}}
 * &mdash; with its quoting and escaping, and the shapes a value arrives in from the database, which differ by
 * provider. Hibernate passes the driver's {@link java.sql.Array}; EclipseLink the same already unpacked into a Java
 * array; either may pass the literal as text. What is specific to the element type is the element converter a
 * subclass hands the constructor, an {@link AttributeConverter} between one element and its text, which this class
 * applies to each element in turn. An element the driver already unpacked goes through its {@code toString()} first.
 * A {@code NULL} element becomes a {@code null} in the list, and back.
 * <p>
 * Towards the database the list becomes the array literal, bound as text; the server casts it to the column's type
 * when the driver leaves the type to it &mdash; {@code stringtype=unspecified} on the JDBC URL. The column side is
 * therefore declared as {@link Object}, which is what makes the converter independent of the provider.
 * {@link __ArrayAttributeConverters} holds the concrete converters written on this class.
 *
 * @param <E> element type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @apiNote Only a one-dimensional array is handled. A literal holding a nested one, {@code {{1},{2}}}, is
 *         rejected on the way in rather than flattened, and nothing on the way out can produce one.
 * @see __ArrayAttributeConverters
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public abstract class __ArrayAttributeConverter<E> implements AttributeConverter<List<E>, Object> {

    /**
     * The text of a {@code NULL} element. The value is {@value}.
     */
    protected static final String NULL_ELEMENT = "NULL";

    /**
     * One element of a literal: quoted, with backslash escapes, or bare, with none of the characters which would need
     * the quotes.
     */
    private static final String ELEMENT = "\"(?:[^\"\\\\]|\\\\.)*\"|[^\\s\",{}\\\\]+";

    private static final Pattern ELEMENT_PATTERN = Pattern.compile(ELEMENT);

    /**
     * A one-dimensional literal: braces around zero or more elements separated by commas.
     */
    private static final Pattern LITERAL_PATTERN = Pattern.compile(
            "\\s*\\{\\s*(?:(?:" + ELEMENT + ")(?:\\s*,\\s*(?:" + ELEMENT + "))*\\s*)?\\}\\s*");

    private static final Pattern ESCAPE_PATTERN = Pattern.compile("\\\\(.)");

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance with the specified element converter.
     *
     * @param elementConverter a converter between one element and its text.
     */
    protected __ArrayAttributeConverter(final AttributeConverter<E, String> elementConverter) {
        super();
        this.elementConverter = Objects.requireNonNull(elementConverter, "elementConverter is null");
    }

    // ---------------------------------------------------------------------------------------------- AttributeConverter

    /**
     * Converts the specified list to the array literal of its elements.
     *
     * @param attribute the list to convert.
     * @return the array literal of the {@code attribute}, a {@link String}; {@code null} when the {@code attribute} is
     *         {@code null}.
     * @implSpec An element which is {@code null}, or which the element converter converts to {@code null}, is
     *         written as {@code NULL}. Every other element's text is written bare when it can be, and quoted &mdash;
     *         with a backslash before each backslash and double quote in it &mdash; when it is empty, spells
     *         {@code NULL} in any case, or holds a double quote, a backslash, a comma, a brace or whitespace.
     */
    @Override
    public @Nullable Object convertToDatabaseColumn(final @Nullable List<E> attribute) {
        if (attribute == null) {
            return null;
        }
        final var builder = new StringBuilder("{");
        var first = true;
        for (final var element : attribute) {
            if (!first) {
                builder.append(',');
            }
            first = false;
            final var text = element == null ? null : elementConverter.convertToDatabaseColumn(element);
            builder.append(text == null ? NULL_ELEMENT : quote(text));
        }
        return builder.append('}').toString();
    }

    /**
     * Converts the specified column value to a list of its elements.
     *
     * @param dbData the column value to convert: a {@link java.sql.Array}, a Java array, or the array literal as text
     *               &mdash; anything else through its {@code toString()}.
     * @return a modifiable list of the elements of the {@code dbData}; {@code null} when the {@code dbData} is
     *         {@code null}.
     * @throws IllegalArgumentException when a {@link java.sql.Array} cannot be unpacked, or the text is not a
     *                                  one-dimensional array literal.
     * @implSpec A {@link java.sql.Array} is {@link java.sql.Array#getArray() unpacked} and handled as the Java
     *         array it yields. An element of a Java array goes through its {@code toString()} and then the element
     *         converter; a {@code null} element stays {@code null}. The text form is parsed with the same quoting
     *         rules {@link #convertToDatabaseColumn(List)} writes by, and a bare {@code NULL}, in any case, is a
     *         {@code null} element while a quoted {@code "NULL"} is the four-letter text.
     */
    @Override
    @SuppressWarnings({
            "java:S1168" // Empty arrays and collections should be returned instead of null
    })
    public @Nullable List<E> convertToEntityAttribute(final @Nullable Object dbData) {
        if (dbData == null) {
            return null;
        }
        if (dbData instanceof java.sql.Array array) {
            try {
                return convertToEntityAttribute(array.getArray());
            } catch (final SQLException sqle) {
                throw new IllegalArgumentException("failed to unpack " + dbData, sqle);
            }
        }
        if (dbData.getClass().isArray()) {
            final var length = Array.getLength(dbData);
            final var list = new ArrayList<E>(length);
            for (var i = 0; i < length; i++) {
                final var element = Array.get(dbData, i);
                list.add(element == null ? null : elementConverter.convertToEntityAttribute(element.toString()));
            }
            return list;
        }
        return parse(dbData.toString());
    }

    // --------------------------------------------------------------------------------------------------------- literal
    private List<E> parse(final String literal) {
        if (!LITERAL_PATTERN.matcher(literal).matches()) {
            throw new IllegalArgumentException("not a one-dimensional array literal: " + literal);
        }
        final var list = new ArrayList<E>();
        final var elements = ELEMENT_PATTERN.matcher(literal);
        while (elements.find()) {
            final var element = elements.group();
            if (element.charAt(0) == '"') {
                final var unquoted = ESCAPE_PATTERN.matcher(element.substring(1, element.length() - 1))
                        .replaceAll("$1");
                list.add(elementConverter.convertToEntityAttribute(unquoted));
            } else {
                list.add(element.equalsIgnoreCase(NULL_ELEMENT)
                         ? null : elementConverter.convertToEntityAttribute(element));
            }
        }
        return list;
    }

    private static String quote(final String element) {
        var needed = element.isEmpty() || element.equalsIgnoreCase(NULL_ELEMENT);
        for (var i = 0; !needed && i < element.length(); i++) {
            final var c = element.charAt(i);
            needed = c == '"' || c == '\\' || c == ',' || c == '{' || c == '}' || Character.isWhitespace(c);
        }
        if (!needed) {
            return element;
        }
        final var builder = new StringBuilder("\"");
        for (var i = 0; i < element.length(); i++) {
            final var c = element.charAt(i);
            if (c == '"' || c == '\\') {
                builder.append('\\');
            }
            builder.append(c);
        }
        return builder.append('"').toString();
    }

    // -----------------------------------------------------------------------------------------------------------------
    private final AttributeConverter<E, String> elementConverter;
}
