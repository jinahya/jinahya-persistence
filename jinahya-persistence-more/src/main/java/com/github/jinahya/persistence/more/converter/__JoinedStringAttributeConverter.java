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

import jakarta.persistence.AttributeConverter;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

/**
 * An abstract class for converting a {@link List} of entity attribute elements to a single delimited {@code String} db
 * data, and vice versa.
 * <p>
 * Each element is converted by an element converter, joined with a delimiter on the way out, and split on the way in.
 * The order of the elements, and any duplicates among them, are preserved in both directions.
 *
 * @param <X> element type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @apiNote A delimited form cannot tell an empty list from a list holding a single empty element: both convert
 *         to an empty column value, which converts back to the latter. Every other list survives a round trip <em>as
 *         far as this class is concerned</em> &mdash; a list whose joined form would not split back into the same
 *         tokens is rejected by {@link #convertToDatabaseColumn(List)} rather than written. Whether the elements
 *         themselves survive is the {@code elementConverter}'s business: one which is not injective, or whose read is
 *         not the inverse of its write, loses values that this class cannot see.
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public class __JoinedStringAttributeConverter<X> implements __StringAttributeConverter<List<X>> {

    /**
     * An abstract class for converting a {@link List} of {@link String} elements, which requires no element
     * conversion.
     */
    @SuppressWarnings({
            "java:S101" // Class names should comply with a naming convention
    })
    public static class __OfStrings extends __JoinedStringAttributeConverter<String> {

        /**
         * Creates a new instance which joins and splits elements with the specified delimiter.
         *
         * @param delimiter a delimiter; taken literally, not as a regular expression.
         */
        public __OfStrings(final String delimiter) {
            super(delimiter, __AttributeConverterUtils.using(UnaryOperator.identity(), UnaryOperator.identity()));
        }
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance which joins and splits elements with the specified delimiter.
     *
     * @param delimiter        a delimiter; taken literally, not as a regular expression, and not empty.
     * @param elementConverter an attribute converter for converting elements.
     * @throws IllegalArgumentException when the {@code delimiter} is empty.
     * @implSpec The {@code delimiter} is {@link Pattern#quote(String) quoted} for splitting, so one carrying
     *         regular expression metacharacters &mdash; {@code "|"} and {@code "."} among them &mdash; means what it
     *         looks like it means.
     * @implNote An <em>empty</em> delimiter is rejected because it breaks both directions:
     *         {@link String#contains(CharSequence) contains("")} is always {@code true}, so every element would trip
     *         the guard in {@link #convertToDatabaseColumn(List)}, and {@link Pattern#quote(String) quote("")} is
     *         {@code \Q\E}, which matches at every position, so a read would split a column into one element per
     *         character. Only the empty string is rejected: a delimiter of a space, or of a tab, is perfectly usable
     *         and stays allowed.
     */
    public __JoinedStringAttributeConverter(final String delimiter,
                                            final AttributeConverter<X, String> elementConverter) {
        super();
        this.joiningDelimiter = Objects.requireNonNull(delimiter, "delimiter is null");
        if (this.joiningDelimiter.isEmpty()) {
            throw new IllegalArgumentException("delimiter is empty");
        }
        this.splittingPattern = Pattern.compile(Pattern.quote(this.joiningDelimiter));
        this.elementConverter = Objects.requireNonNull(elementConverter, "elementConverter is null");
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Converts the specified list to a single delimited string.
     *
     * @param attribute the list to convert.
     * @return a string of the converted elements, joined with the delimiter; {@code null} when the {@code attribute} is
     *         {@code null}.
     * @throws IllegalArgumentException when an element converts to {@code null}, or to a value containing the joining
     *                                  delimiter.
     */
    @Override
    public @Nullable String convertToDatabaseColumn(final @Nullable List<X> attribute) {
        if (attribute == null) {
            return null;
        }
        final var converteds = new ArrayList<String>();
        final var joiner = new StringJoiner(joiningDelimiter);
        for (final var element : attribute) {
            final var converted = elementConverter.convertToDatabaseColumn(element);
            if (converted == null) {
                throw new IllegalArgumentException("element converted to null: " + element);
            }
            if (converted.contains(joiningDelimiter)) {
                throw new IllegalArgumentException(
                        "element(" + element + ") converted to a value containing the joiningDelimiter(" +
                        joiningDelimiter + "): " + converted
                );
            }
            converteds.add(converted);
            joiner.add(converted);
        }
        final var joined = joiner.toString();
        // An element holding no delimiter of its own is not enough: a delimiter can also form across the seam
        // between an element and the separator that follows it, whenever a non-empty proper suffix of the
        // delimiter equals one of its prefixes. "||" over ["|", "x"] joins to "|||x", which splits back into
        // ["", "|x"] -- every element passed the guard above, and the value is still lost. Rather than
        // characterize that overlap analytically, just split the result and check it reproduces what was joined.
        // The empty list is exempt: it joins to "", which splits into [""], and that documented asymmetry is
        // older than this check (see the class javadoc).
        if (!converteds.isEmpty()) {
            final var split = splittingPattern.split(joined, -1);
            if (!Arrays.asList(split).equals(converteds)) {
                final var at = firstDifference(split, converteds);
                throw new IllegalArgumentException(
                        "joining with the joiningDelimiter(" + joiningDelimiter + ") does not survive a round trip" +
                        "; the delimiter forms across the boundary after element " + at +
                        " (" + converteds.get(at) + ")" +
                        "; joined: " + joined +
                        "; splits back into: " + Arrays.toString(split)
                );
            }
        }
        return joined;
    }

    private static int firstDifference(final String[] split, final List<String> converteds) {
        for (int i = 0; i < converteds.size(); i++) {
            if (i >= split.length || !split[i].equals(converteds.get(i))) {
                return Math.max(0, i - 1);
            }
        }
        return converteds.size() - 1;
    }

    /**
     * Converts the specified delimited string to a list of elements.
     *
     * @param dbData the string to convert.
     * @return a modifiable list of the converted elements; {@code null} when the {@code dbData} is {@code null}.
     * @implSpec The {@code dbData} is split unconditionally; splitting an empty string yields one empty token,
     *         so an empty {@code dbData} converts to a list holding a single element rather than to an empty list.
     */
    @Override
    @SuppressWarnings({
            "java:S1168" // Empty arrays and collections should be returned instead of null
    })
    public @Nullable List<X> convertToEntityAttribute(final @Nullable String dbData) {
        if (dbData == null) {
            return null;
        }
        final List<X> attribute = new ArrayList<>();
        // a negative limit keeps trailing empty elements, which the default limit would silently drop
        for (final var token : splittingPattern.split(dbData, -1)) {
            attribute.add(elementConverter.convertToEntityAttribute(token));
        }
        return attribute;
    }

    // -----------------------------------------------------------------------------------------------------------------
    private final String joiningDelimiter;

    private final Pattern splittingPattern;

    private final AttributeConverter<X, String> elementConverter;
}
