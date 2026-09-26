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
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * An abstract class for converting a {@link List} of entity attribute elements to a single delimited {@code String} db
 * data, and vice versa.
 * <p>
 * Each element is converted by an element converter, joined with a delimiter on the way out, and split on the way in.
 * The order of the elements, and any duplicates among them, are preserved in both directions.
 *
 * @param <X> element type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @apiNote This class joins and splits. It does not escape, quote, or validate, and
 *         {@link #convertToDatabaseColumn(List)} never rejects a list: whatever the elements convert to is written.
 *         Keeping a list round-trippable is therefore the caller's business, and takes all of the following. None of
 *         them is checked; a violation is not reported anywhere, and surfaces only as a different list on the next
 *         read.
 *         <ul>
 *           <li>An element which the {@code elementConverter} converts to {@code null} is <em>silently dropped</em>.
 *               A delimited form has no token meaning "null" &mdash; an empty token already means the empty element
 *               &mdash; so such an element cannot be written at all, and the list that comes back is shorter than the
 *               one that went out. Note that {@link #convertToEntityAttribute(String)} does <em>not</em> drop nulls
 *               coming the other way: an {@code elementConverter} which reads a token as {@code null} yields a list
 *               which, saved again unchanged, loses that element.</li>
 *           <li>The {@code elementConverter} must not convert an element to a value <em>containing</em> the delimiter.
 *               Such a value splits back into two or more elements.</li>
 *           <li>The delimiter must not overlap itself &mdash; no non-empty proper prefix of it may equal a suffix.
 *               A delimiter like {@code "||"} or {@code "aba"} can form across the seam between an element and the
 *               separator which follows it even when no element holds the delimiter itself: {@code "||"} over
 *               {@code ["|", "x"]} joins to {@code "|||x"}, which splits back into {@code ["", "|x"]}. A delimiter
 *               with no such overlap &mdash; {@code ","}, {@code ";"}, {@code "\t"}, {@code ", "} &mdash; cannot
 *               produce a spurious match as long as no element holds it.</li>
 *           <li>An empty list cannot be told from a list holding a single empty element: both convert to an empty
 *               column value, which converts back to the latter.</li>
 *         </ul>
 *         Whether the elements themselves survive is the {@code elementConverter}'s business: one which is not
 *         injective, or whose read is not the inverse of its write, loses values that this class cannot see.
 *         <p>
 *         A {@link List} of {@link String}s needs no element conversion at all, and takes
 *         {@link __AttributeConverterUtils#identity()} as its {@code elementConverter}. No delimiter is blessed with a
 *         ready-made converter: registering one means subclassing this class with the delimiter the values cannot
 *         hold, and carrying {@link jakarta.persistence.Converter @Converter} on the subclass.
 * @see __AttributeConverterUtils#identity()
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public abstract class __JoinedStringAttributeConverter<X> implements __StringAttributeConverter<List<X>> {

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance which joins and splits elements with the specified joiningDelimiter.
     *
     * @param joiningDelimiter a joiningDelimiter; taken literally, not as a regular expression, and not empty.
     * @param elementConverter an attribute converter for converting elements.
     * @throws IllegalArgumentException when the {@code joiningDelimiter} is empty.
     * @apiNote A subclass fixes the joiningDelimiter and the element converter, and is what
     *         {@link jakarta.persistence.Converter @Converter} goes on; a persistence provider instantiates a converter
     *         through a no-argument constructor, and neither {@link jakarta.persistence.Convert @Convert} nor
     *         {@code persistence.xml} can pass either argument. To hold one directly &mdash; composed into another
     *         converter, or delegated to from one which <em>is</em> registered &mdash; an anonymous subclass does:
     *         {@code new __JoinedStringAttributeConverter<>(",", identity()) {}}.
     * @implSpec The {@code joiningDelimiter} is {@link Pattern#quote(String) quoted} for splitting, so one
     *         carrying regular expression metacharacters &mdash; {@code "|"} and {@code "."} among them &mdash; means
     *         what it looks like it means. It is otherwise not inspected; a joiningDelimiter which overlaps itself is
     *         accepted, and the consequences are the caller's (see the class contract).
     * @implNote An <em>empty</em> joiningDelimiter is rejected because it breaks the read:
     *         {@link Pattern#quote(String) quote("")} is {@code \Q\E}, which matches at every position, so a column
     *         would split into one element per character, while the write would concatenate every element into an
     *         unsplittable run. Only the empty string is rejected: a joiningDelimiter of a space, or of a tab, is
     *         perfectly usable and stays allowed.
     */
    protected __JoinedStringAttributeConverter(final String joiningDelimiter,
                                               final AttributeConverter<X, String> elementConverter) {
        super();
        this.joiningDelimiter = Objects.requireNonNull(joiningDelimiter, "joiningDelimiter is null");
        if (this.joiningDelimiter.isEmpty()) {
            throw new IllegalArgumentException("joiningDelimiter is empty");
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
     * @implSpec Elements which the {@code elementConverter} converts to {@code null} are discarded, and the
     *         joined value holds fewer elements than the {@code attribute} did. Nothing else is filtered, rejected, or
     *         escaped &mdash; see the class contract for what that costs.
     */
    @Override
    public @Nullable String convertToDatabaseColumn(final @Nullable List<X> attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.stream()
                .map(elementConverter::convertToDatabaseColumn)
                .filter(Objects::nonNull)
                .collect(Collectors.joining(joiningDelimiter));
    }

    /**
     * Converts the specified delimited string to a list of elements.
     *
     * @param dbData the string to convert.
     * @return a modifiable list of the converted elements; {@code null} when the {@code dbData} is {@code null}.
     * @implSpec The {@code dbData} is split unconditionally; splitting an empty string yields one empty token,
     *         so an empty {@code dbData} converts to a list holding a single element rather than to an empty list.
     *         Tokens are converted as they come, including whatever the {@code elementConverter} makes of an empty one,
     *         and a {@code null} it returns is kept in the list &mdash; unlike {@link #convertToDatabaseColumn(List)},
     *         which drops nulls.
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
