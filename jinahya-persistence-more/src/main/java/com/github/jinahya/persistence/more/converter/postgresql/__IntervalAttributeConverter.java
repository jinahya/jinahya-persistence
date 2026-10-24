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

import com.github.jinahya.persistence.more.converter.time.__TemporalAmountAttributeConverter;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An abstract class for converting an entity attribute of a specific subtype of {@link TemporalAmount} to a
 * PostgreSQL {@code interval} column, and vice versa.
 * <p>
 * Towards the database a value is written as the ISO-8601 text its own type spells &mdash; {@code PT26H5M},
 * {@code P1Y2M3D} &mdash; which is one of the forms the server reads, bound as text; the server casts it to
 * {@code interval} when the driver leaves the type to it ({@code stringtype=unspecified} on the JDBC URL). From the
 * database the converter takes whatever the provider hands it, since the two differ: Hibernate passes the interval as
 * text, EclipseLink the driver's own interval object, whose {@code toString()} is parsed. The column side is therefore
 * declared as {@link Object}, which is what makes the converter independent of the provider.
 * <p>
 * Every text the server or the driver produces is read: the default {@code postgres} style ({@code 1 day 02:05:00}),
 * {@code postgres_verbose} ({@code @ 1 day 2 hours 5 mins ago}), {@code sql_standard} ({@code +1-2 +3 +4:05:06}),
 * {@code iso_8601} ({@code P1Y2M3DT4H5M6S}) and the driver's own, which names only the non-zero fields
 * ({@code 1 days 2 hours 5 mins}, or {@code 0 secs} for a zero). The unit spellings the server accepts on input but
 * never prints &mdash; {@code 2h}, {@code 1500ms} &mdash; read as well. What the parsed {@link Fields fields} become is the subclass's &mdash; a {@link java.time.Duration}
 * refuses years and months, which have no fixed length, and a {@link java.time.Period} refuses hours, minutes and
 * seconds. {@link __IntervalAttributeConverters} holds the two.
 *
 * @param <X> temporal amount type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @apiNote An {@code interval} keeps microseconds; a {@link java.time.Duration} keeps nanoseconds. The last
 *         three digits of a nanosecond part are the server's to round, and do not survive a round trip.
 * @see __IntervalAttributeConverters
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public abstract class __IntervalAttributeConverter<X extends TemporalAmount>
        implements __TemporalAmountAttributeConverter<X, Object> {

    /**
     * The fields of an {@code interval}, as the server keeps them: months, days and seconds are independent, because
     * none has a fixed length in the next. Years are kept apart from months, and weeks are folded into days, as the
     * server itself does on output.
     *
     * @param years   years; a whole number.
     * @param months  months; a whole number, not folded into {@code years}.
     * @param days    days; possibly fractional, as the server accepts {@code 1.5 days} on input.
     * @param seconds seconds, with hours and minutes folded in; possibly fractional.
     */
    public record Fields(long years, long months, BigDecimal days, BigDecimal seconds) {

        /**
         * The fields of a zero interval.
         */
        public static final Fields ZERO = new Fields(0L, 0L, BigDecimal.ZERO, BigDecimal.ZERO);

        /**
         * Creates a new instance.
         *
         * @param years   years.
         * @param months  months.
         * @param days    days.
         * @param seconds seconds.
         */
        public Fields {
            Objects.requireNonNull(days, "days is null");
            Objects.requireNonNull(seconds, "seconds is null");
        }

        /**
         * Returns the fields with every value negated.
         *
         * @return the negated fields.
         */
        public Fields negated() {
            return new Fields(-years, -months, days.negate(), seconds.negate());
        }

        /**
         * Returns whether every field is zero.
         *
         * @return {@code true} when every field is zero; {@code false} otherwise.
         */
        public boolean isZero() {
            return years == 0L && months == 0L && days.signum() == 0 && seconds.signum() == 0;
        }
    }

    // --------------------------------------------------------------------------------------------------------- parsing
    private static final String SIGNED_INTEGER = "[-+]?\\d+";

    private static final String SIGNED_NUMBER = SIGNED_INTEGER + "(?:\\.\\d+)?";

    /**
     * ISO-8601 with designators, as the {@code iso_8601} style prints it and as {@link java.time.Duration} and
     * {@link java.time.Period} spell themselves; a sign may sit before {@code P}, as {@code java.time} reads it, or
     * before any number, as both read it.
     */
    private static final Pattern ISO_PATTERN = Pattern.compile(
            "(?<sign>[-+]?)P"
            + "(?:(?<years>" + SIGNED_INTEGER + ")Y)?"
            + "(?:(?<months>" + SIGNED_INTEGER + ")M)?"
            + "(?:(?<weeks>" + SIGNED_INTEGER + ")W)?"
            + "(?:(?<days>" + SIGNED_INTEGER + ")D)?"
            + "(?:T"
            + "(?:(?<hours>" + SIGNED_INTEGER + ")H)?"
            + "(?:(?<minutes>" + SIGNED_INTEGER + ")M)?"
            + "(?:(?<seconds>" + SIGNED_NUMBER + ")S)?"
            + ")?",
            Pattern.CASE_INSENSITIVE);

    /**
     * The unit spellings the server accepts, which include every one it prints. A bare {@code m} is a minute, as the
     * server reads it, not a month.
     */
    private static final String UNIT =
            "years?|yrs?|y|months?|mons?|weeks?|w|days?|d|hours?|hrs?|h|minutes?|mins?|m"
            + "|milliseconds?|ms|microseconds?|us|seconds?|secs?|s";

    private static final Pattern UNIT_PATTERN = Pattern.compile(UNIT, Pattern.CASE_INSENSITIVE);

    private static final Pattern NUMBER_PATTERN = Pattern.compile(SIGNED_NUMBER);

    /**
     * A number with its unit attached, {@code 2hours}, which the server accepts on input.
     */
    private static final Pattern NUMBER_UNIT_PATTERN = Pattern.compile(
            "(?<value>" + SIGNED_NUMBER + ")(?<unit>" + UNIT + ")", Pattern.CASE_INSENSITIVE);

    /**
     * The {@code sql_standard} year-month field, {@code 1-2}.
     */
    private static final Pattern YEAR_MONTH_PATTERN = Pattern.compile("(?<sign>[-+]?)(?<years>\\d+)-(?<months>\\d+)");

    /**
     * A time of day, {@code 02:05:00.5}, hours running past {@code 24} as the server allows.
     */
    private static final Pattern CLOCK_PATTERN = Pattern.compile(
            "(?<sign>[-+]?)(?<hours>\\d+):(?<minutes>\\d{1,2})(?::(?<seconds>\\d{1,2}(?:\\.\\d+)?))?");

    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");

    private static final BigDecimal SECONDS_PER_MINUTE = BigDecimal.valueOf(60L);

    private static final BigDecimal SECONDS_PER_HOUR = BigDecimal.valueOf(3_600L);

    private static final BigDecimal DAYS_PER_WEEK = BigDecimal.valueOf(7L);

    private static final int MILLIS_PER_SECOND_EXPONENT = 3;

    private static final int MICROS_PER_SECOND_EXPONENT = 6;

    /**
     * Parses the specified text, in any form the server or its driver prints an {@code interval}, into its fields.
     *
     * @param text the text to parse.
     * @return the fields of the {@code text}.
     * @throws IllegalArgumentException when the {@code text} is not an interval.
     * @implSpec The ISO-8601 form is tried first, on the whole text. Otherwise the text is taken as whitespace-separated
     *         tokens: a leading {@code @} is ignored and a trailing {@code ago} negates every field; a number followed
     *         by a unit, with or without a space between, is that unit, a bare {@code m} being a minute as the server
     *         reads it; a {@code y-m} token is years and months; an
     *         {@code h:m[:s[.f]]} token is a time; and a bare number is a count of days, as the {@code sql_standard}
     *         style prints one. In that style a sign before the first field and before no other applies to every
     *         field, as the server means it; everywhere else a sign belongs to the field it precedes.
     */
    protected static Fields parse(final String text) {
        Objects.requireNonNull(text, "text is null");
        final var trimmed = text.trim();
        if (trimmed.isEmpty()) {
            throw notAnInterval(text);
        }
        final var iso = ISO_PATTERN.matcher(trimmed);
        if (iso.matches()) {
            return parseIso(text, iso);
        }
        final var tokens = new ArrayList<>(Arrays.asList(WHITESPACE_PATTERN.split(trimmed)));
        if ("@".equals(tokens.get(0))) {
            tokens.remove(0);
        }
        var negate = false;
        if (!tokens.isEmpty() && "ago".equalsIgnoreCase(tokens.get(tokens.size() - 1))) {
            tokens.remove(tokens.size() - 1);
            negate = true;
        }
        if (tokens.isEmpty()) {
            throw notAnInterval(text);
        }
        // sql_standard prints a value whose fields all have one sign as "-1-2 3 4:05:06": one sign, for all of them.
        // Only a value of mixed signs gets a sign per field, "+1-2 -3 +4:05:06". No unit word appears in that style,
        // and every other style signs each field on its own.
        final var first = tokens.get(0);
        if (isSigned(first) && tokens.size() > 1
            && tokens.stream().noneMatch(__IntervalAttributeConverter::hasUnit)
            && tokens.stream().skip(1L).anyMatch(t -> !isSigned(t))) {
            tokens.set(0, first.substring(1));
            negate ^= first.charAt(0) == '-';
        }
        var fields = Fields.ZERO;
        for (var i = 0; i < tokens.size(); ) {
            final var token = tokens.get(i);
            final var numberUnit = NUMBER_UNIT_PATTERN.matcher(token);
            if (numberUnit.matches()) {
                fields = add(text, fields, numberUnit.group("value"), numberUnit.group("unit"));
                i++;
                continue;
            }
            if (NUMBER_PATTERN.matcher(token).matches()) {
                if (i + 1 < tokens.size() && UNIT_PATTERN.matcher(tokens.get(i + 1)).matches()) {
                    fields = add(text, fields, token, tokens.get(i + 1));
                    i += 2;
                    continue;
                }
                fields = add(text, fields, token, "days");
                i++;
                continue;
            }
            final var yearMonth = YEAR_MONTH_PATTERN.matcher(token);
            if (yearMonth.matches()) {
                final var sign = "-".equals(yearMonth.group("sign")) ? -1L : 1L;
                fields = new Fields(
                        fields.years() + sign * Long.parseLong(yearMonth.group("years")),
                        fields.months() + sign * Long.parseLong(yearMonth.group("months")),
                        fields.days(),
                        fields.seconds()
                );
                i++;
                continue;
            }
            final var clock = CLOCK_PATTERN.matcher(token);
            if (clock.matches()) {
                var seconds = new BigDecimal(clock.group("hours")).multiply(SECONDS_PER_HOUR)
                        .add(new BigDecimal(clock.group("minutes")).multiply(SECONDS_PER_MINUTE));
                if (clock.group("seconds") != null) {
                    seconds = seconds.add(new BigDecimal(clock.group("seconds")));
                }
                if ("-".equals(clock.group("sign"))) {
                    seconds = seconds.negate();
                }
                fields = new Fields(fields.years(), fields.months(), fields.days(), fields.seconds().add(seconds));
                i++;
                continue;
            }
            throw notAnInterval(text);
        }
        return negate ? fields.negated() : fields;
    }

    private static Fields parseIso(final String text, final Matcher iso) {
        var any = false;
        var years = 0L;
        var months = 0L;
        var days = BigDecimal.ZERO;
        var seconds = BigDecimal.ZERO;
        String group;
        if ((group = iso.group("years")) != null) {
            any = true;
            years = Long.parseLong(group);
        }
        if ((group = iso.group("months")) != null) {
            any = true;
            months = Long.parseLong(group);
        }
        if ((group = iso.group("weeks")) != null) {
            any = true;
            days = days.add(new BigDecimal(group).multiply(DAYS_PER_WEEK));
        }
        if ((group = iso.group("days")) != null) {
            any = true;
            days = days.add(new BigDecimal(group));
        }
        if ((group = iso.group("hours")) != null) {
            any = true;
            seconds = seconds.add(new BigDecimal(group).multiply(SECONDS_PER_HOUR));
        }
        if ((group = iso.group("minutes")) != null) {
            any = true;
            seconds = seconds.add(new BigDecimal(group).multiply(SECONDS_PER_MINUTE));
        }
        if ((group = iso.group("seconds")) != null) {
            any = true;
            seconds = seconds.add(new BigDecimal(group));
        }
        if (!any) {
            throw notAnInterval(text);
        }
        final var fields = new Fields(years, months, days, seconds);
        return "-".equals(iso.group("sign")) ? fields.negated() : fields;
    }

    private static Fields add(final String text, final Fields fields, final String value, final String unit) {
        final var number = new BigDecimal(value);
        final var lower = unit.toLowerCase();
        if (lower.startsWith("y") || lower.startsWith("mon")) {
            final long whole;
            try {
                whole = number.longValueExact();
            } catch (final ArithmeticException ae) {
                throw new IllegalArgumentException("a fractional count of " + unit + " is not supported: " + text, ae);
            }
            return lower.startsWith("y")
                   ? new Fields(fields.years() + whole, fields.months(), fields.days(), fields.seconds())
                   : new Fields(fields.years(), fields.months() + whole, fields.days(), fields.seconds());
        }
        if (lower.startsWith("w")) {
            return new Fields(fields.years(), fields.months(), fields.days().add(number.multiply(DAYS_PER_WEEK)),
                              fields.seconds());
        }
        if (lower.startsWith("d")) {
            return new Fields(fields.years(), fields.months(), fields.days().add(number), fields.seconds());
        }
        final BigDecimal seconds;
        if (lower.startsWith("h")) {
            seconds = number.multiply(SECONDS_PER_HOUR);
        } else if (lower.startsWith("min") || "m".equals(lower)) {
            seconds = number.multiply(SECONDS_PER_MINUTE);
        } else if (lower.startsWith("mil") || "ms".equals(lower)) {
            seconds = number.movePointLeft(MILLIS_PER_SECOND_EXPONENT);
        } else if (lower.startsWith("mic") || "us".equals(lower)) {
            seconds = number.movePointLeft(MICROS_PER_SECOND_EXPONENT);
        } else {
            seconds = number;
        }
        return new Fields(fields.years(), fields.months(), fields.days(), fields.seconds().add(seconds));
    }

    private static boolean isSigned(final String token) {
        return !token.isEmpty() && (token.charAt(0) == '-' || token.charAt(0) == '+');
    }

    private static boolean hasUnit(final String token) {
        return UNIT_PATTERN.matcher(token).matches() || NUMBER_UNIT_PATTERN.matcher(token).matches();
    }

    private static IllegalArgumentException notAnInterval(final String text) {
        return new IllegalArgumentException("not an interval: " + text);
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance for the specified temporal amount class.
     *
     * @param attributeClass the class of the entity attribute this converter converts.
     */
    protected __IntervalAttributeConverter(final Class<X> attributeClass) {
        super();
        this.attributeClass = Objects.requireNonNull(attributeClass, "attributeClass is null");
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Converts the specified entity attribute to a column value.
     *
     * @param attribute the entity attribute to convert.
     * @return the ISO-8601 text of the {@code attribute}, a {@link String}; {@code null} when the {@code attribute} is
     *         {@code null}.
     * @implSpec The default implementation returns {@link Object#toString() attribute.toString()}. Both
     *         {@link java.time.Duration} and {@link java.time.Period} spell themselves in the ISO-8601 form with
     *         designators, with a sign before any negative number, and the server reads exactly that. A subclass
     *         whose type does not hold to it has to override this method.
     */
    @Override
    public @Nullable Object convertToDatabaseColumn(final @Nullable X attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.toString();
    }

    /**
     * Converts the specified column value to an entity attribute.
     *
     * @param dbData the column value to convert: the interval as text, an instance of the attribute class, or any
     *               object whose {@code toString()} is the interval as text.
     * @return an entity attribute; {@code null} when the {@code dbData} is {@code null}.
     * @throws IllegalArgumentException when the {@code dbData} is not an interval, or holds a field the attribute
     *                                  class cannot carry.
     * @implSpec An instance of the attribute class is returned as it is. Anything else is {@link #parse(String)
     *         parsed} from its {@code toString()} and handed to {@link #fromFields(Fields)}.
     */
    @Override
    public @Nullable X convertToEntityAttribute(final @Nullable Object dbData) {
        if (dbData == null) {
            return null;
        }
        if (attributeClass.isInstance(dbData)) {
            return attributeClass.cast(dbData);
        }
        return fromFields(parse(dbData.toString()));
    }

    /**
     * Converts the specified fields, parsed from a column value, to an entity attribute.
     *
     * @param fields the fields to convert.
     * @return an entity attribute of the {@code fields}.
     * @throws IllegalArgumentException when the {@code fields} hold a value the attribute class cannot carry.
     */
    protected abstract X fromFields(Fields fields);

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The class of the entity attribute this converter converts.
     */
    protected final Class<X> attributeClass;
}
