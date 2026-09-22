package com.github.jinahya.persistence.more.orderedrange;

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

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * An abstract mapped superclass for a range whose two ends each carry their own bound type, in their own column.
 * <p>
 * Two columns, and each holds a <em>cut</em> rather than a value: the encoded endpoint followed by one character
 * saying whether that endpoint belongs to the range. {@code NULL} is an absent endpoint, and the range is unbounded
 * on that side.
 * <pre>
 * range_lower   range_upper    means
 * -----------   -----------    -----------------------
 * 2026-01-01[   2026-04-01)    [2026-01-01, 2026-04-01)
 * 0090.00[      0100.00]       [90, 100]
 * NULL          0100.00]       (, 100]
 * NULL          NULL           (,)
 * </pre>
 *
 * <h2>Why the bound type is in the column</h2>
 * Because it is <strong>data</strong> here, not schema — it varies row to row. Histogram bins are half-open except
 * the last, which must be closed so the maximum falls somewhere; and on a continuous axis that last bin cannot be
 * rewritten half-open, because there is no next value after the maximum. A range imported from a PostgreSQL range
 * column carries whatever bounds it was written with, for the same reason.
 * <p>
 * This is the one thing that forces the columns to be text. A column holding an endpoint <em>and</em> a bound
 * character holds two facts, and no {@code DATE} or {@code NUMERIC} can carry the second.
 *
 * <h2>The marker trails, and that is not cosmetic</h2>
 * The character goes <em>after</em> the encoded endpoint, never before. A one-character suffix is constant width, so
 * it shifts every key equally and the column still sorts by the endpoint. A leading bracket does not: {@code (} is
 * {@code 0x28} and {@code [} is {@code 0x5B}, so {@code (2026-01-01} would sort before {@code [1999-01-01} and the
 * column could answer no range query at all.
 * <p>
 * Reading is total, whatever the endpoint encodes to: the last character is always the marker and everything before
 * it is always the value, so an encoding which itself ends in a bracket, or which is the empty string, still parses.
 * That is what position-pinning buys over any in-band separator.
 *
 * <h2>Containment in SQL</h2>
 * The upper half is a plain comparison, because the two upper characters already sort in cut order —
 * {@code )} is {@code 0x29} and {@code ]} is {@code 0x5D}, and an exclusive end does come before an inclusive one:
 * {@snippet lang = "sql":
 * range_upper > :t || ')'
 *}
 * The lower half is not, because the two lower characters sort the wrong way round: {@code (} precedes {@code [}
 * where a closed lower bound starts <em>earlier</em> than an open one. So it takes the index-usable comparison plus
 * a recheck, which only touches rows sitting exactly on the boundary:
 * {@snippet lang = "sql":
 * range_lower <= :t || '[' AND (range_lower < :t || '(' OR range_lower LIKE '%[')
 *}
 * Each half guarded with {@code IS NULL} for an absent endpoint, as everywhere else here.
 *
 * <h2>The encoding has to sort, and that is the caller's contract</h2>
 * <strong>{@link #encode(Comparable)} must be order-preserving:</strong> for any two
 * endpoints {@code a} and {@code b}, {@code a.compareTo(b)} and the comparison of their encoded forms must agree in
 * sign. Otherwise the column holds two values which sort the wrong way, and every predicate above answers the wrong
 * rows — silently.
 * <p>
 * It is free for a fixed-width ISO form, {@link java.time.LocalDate} and {@link java.time.YearMonth} among them.
 * It is real work for numbers, which need a fixed width <em>and</em> a sign scheme: {@code "9"} sorts above
 * {@code "10"}, and {@code "-5"} above {@code "0003"}. It is not available at all for a form whose width varies,
 * which rules out {@link java.time.Year#toString()} and {@link java.time.Duration#toString()}.
 * <p>
 * <strong>And the encoding must be prefix-free, which fixed width gives and monotonicity alone does not.</strong>
 * The marker trails the value, so where one text is a proper prefix of another the marker itself falls into the
 * comparison and can invert it — {@code "A"} and {@code "AB"} give the cuts {@code "A]"} and {@code "AB)"}, and
 * {@code ]} at {@code 0x5D} beats {@code B} at {@code 0x42}. The empty string is a prefix of everything, so
 * {@code ""} encodes to the cut {@code "["} and sorts after {@code "A]"}. Encoding {@link String} endpoints as
 * themselves is the case to watch.
 * <p>
 * The full contract is on {@link #encode(Comparable)}. This class does not defend against a subclass which
 * breaks it; it samples.
 * <p>
 * Nothing here checks it. A sample of one pair per row cannot verify a contract over a domain, and it would compare
 * by UTF-16 code unit where the column compares by its collation — so it would give confidence it had not earned.
 * Verify an encoder in a test of the encoder, over the values it will actually see.
 *
 * <h2>Two things the column must be</h2>
 * <strong>{@code VARCHAR}, not {@code CHAR}.</strong> A {@code CHAR(n)} column is blank-padded on storage, so
 * PostgreSQL and Oracle return {@code 2026-01-01[} with trailing spaces and the last character is no longer the
 * marker. MySQL strips them again on the way out and happens to survive, which is worse: the same schema then
 * works on one database and not another. Reading such a column fails loudly here — the marker lookup rejects
 * {@code ' '} — but the message names the character, not the cause. The mapping below asks for no
 * {@code columnDefinition}, so every provider defaults to {@code varchar}; an
 * {@link jakarta.persistence.AttributeOverride @AttributeOverride} which changes that breaks parsing.
 * <p>
 * <strong>A character set which covers the endpoints.</strong> Storage and retrieval are exact whatever the
 * collation — collation decides comparison, not what comes back — but the charset decides what can be held at
 * all. MySQL's {@code utf8} is the three-byte form and cannot store a supplementary character; {@code utf8mb4}
 * can. That is a separate decision from the collation one below, and it is the one which loses data.
 *
 * <h2>One thing the schema must declare</h2>
 * A character column is ordered by its <strong>collation</strong>, and the checks here compare by UTF-16 code unit.
 * Under a case-insensitive or locale-aware collation the two disagree, which for these columns is a correctness bug.
 * Declare them binary or deterministic — {@code COLLATE "C"} on PostgreSQL, a {@code _bin} collation on MySQL.
 * <p>
 * That is necessary and, for text which is not pure ASCII, not quite sufficient: a binary collation orders by code
 * point where Java orders by UTF-16 code unit, and the two invert above the surrogate block. Parsing is unaffected
 * — the marker is one ASCII code unit at a pinned position, so {@code 가나다]} and a surrogate pair both read
 * correctly — but the ordering contract needs the care {@link #encode(Comparable)} sets out.
 *
 * <h2>Two ranges in one table</h2>
 * An entity extends this class and gets one range, because a {@code @MappedSuperclass} is inherited once. A table
 * carrying two needs the second form: a downstream {@link jakarta.persistence.Embeddable @Embeddable} of its own
 * extending this class, embedded as many times as wanted, each with its own
 * {@link jakarta.persistence.AttributeOverride @AttributeOverride} pair.
 * {@snippet lang = "java":
 * @Embeddable
 * @Access(AccessType.FIELD)
 * public class DateRange extends __MappedOrderedRange<LocalDate> {
 *     @Override protected String encode(LocalDate value) { return value.toString(); }
 *     @Override protected LocalDate decode(String encoded) { return LocalDate.parse(encoded); }
 * }
 *
 * @Entity
 * public class Reservation {
 *     @Embedded
 *     @AttributeOverride(name = "rangeLower", column = @Column(name = "stay_lower"))
 *     @AttributeOverride(name = "rangeUpper", column = @Column(name = "stay_upper"))
 *     private DateRange stay;
 *
 *     @Embedded
 *     @AttributeOverride(name = "rangeLower", column = @Column(name = "hold_lower"))
 *     @AttributeOverride(name = "rangeUpper", column = @Column(name = "hold_upper"))
 *     private DateRange hold;
 * }
 *}
 * Which settles what this class may fix and what it may not. The column names below are <em>defaults</em>: a
 * downstream with two ranges replaces both pairs, and {@value #COLUMN_NAME_RANGE_LOWER} is then never written to a
 * schema at all. The attribute names are the opposite — {@code rangeLower} and {@code rangeUpper} are declared
 * here and cannot be renamed, so they are what an {@code @AttributeOverride} and every query path must spell
 * exactly.
 *
 * <h2>Access type</h2>
 * {@link Access @Access}({@code FIELD}) is forced. An entity which puts its {@link jakarta.persistence.Id @Id} on
 * a getter would otherwise flip this hierarchy to property access with it, and the {@link Transient @Transient}
 * accessors here — {@link #isBounded()}, {@link ___OrderedRange#isEmpty() isEmpty}, {@link #getLowerBoundType()} and the
 * rest, all shaped exactly like JavaBeans properties — would then be taken for columns while the two real ones
 * went unmapped.
 * <p>
 * An entity extending this class should declare {@code @Access(AccessType.FIELD)} too: Hibernate infers it,
 * EclipseLink does not. The embeddable form above is what forces the issue — EclipseLink walks the
 * mapped-superclass chain of an {@code @Embeddable} and has been seen to fail where a link in that chain has no
 * access type of its own.
 * <p>
 * An entity which uses <em>property</em> access has to say so, for the mirror reason: {@code @Access(FIELD)} here is
 * scoped to this class by Jakarta Persistence 3.2 &sect;2.3.2, and &sect;2.3.1 decides the hierarchy's default from the
 * classes which do <em>not</em> declare one &mdash; so an {@code @Id} on a getter settles it as {@code PROPERTY}.
 * Hibernate ORM 7.4 and EclipseLink read it that way. ORM 7.2 instead pushes the <em>root</em> mapped superclass's
 * access type onto the entity, looks for an {@code @Id} among its fields, finds none, and rejects the class as having
 * no identifier. This class is that root, so the entity has to declare {@code @Access(AccessType.PROPERTY)} itself.
 * <p>
 * Between the two, the rule is simply: <strong>an entity extending this class should state its access type, whichever
 * one it is.</strong> Inference is unreliable in both directions, and in opposite providers.
 *
 * @param <C> the type of the two endpoints limiting this range
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see __BoundType
 * @see ___OrderedRange
 */
@Access(AccessType.FIELD)
@MappedSuperclass
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public abstract class __MappedOrderedRange<C extends Comparable<? super C>> implements ___OrderedRange<C> {

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The name of the column, {@value}, holding the lower cut.
     */
    public static final String COLUMN_NAME_RANGE_LOWER = "range_lower";

    /**
     * The name of the column, {@value}, holding the upper cut.
     */
    public static final String COLUMN_NAME_RANGE_UPPER = "range_upper";

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected __MappedOrderedRange() {
        super();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object

    /**
     * Returns a string representation of this range, in the notation of its bounds.
     *
     * @return a string representation of this range, with an absent endpoint shown as {@code null}.
     */
    @Override
    public String toString() {
        final var bounds = getRangeBounds();
        return bounds.getLowerBoundCharacter() + String.valueOf(getRangeLower()) + ", " + getRangeUpper()
               + bounds.getUpperBoundCharacter();
    }

    // ------------------------------------------------------------------------------------------------- encode / decode

    /**
     * Encodes the specified endpoint as the text a column holds, without its bound character.
     *
     * @param value the endpoint to encode; never {@code null}.
     * @return the text for the {@code value}; never {@code null}.
     * @implSpec The encoding is this class's one contract with its subclass, and it is relied on rather than
     *         checked. For any non-{@code null} value the text must be:
     *         <ol>
     *         <li><strong>non-null</strong>, and decoded back to an equal value by {@link #decode(String)};
     *         <li><strong>non-empty</strong>, so a stored cut is never the marker alone;
     *         <li><strong>prefix-free</strong> — no value's text a proper prefix of another's;
     *         <li><strong>order-preserving</strong> — {@code a.compareTo(b)} and the comparison of the two texts
     *             agreeing in sign.
     *         </ol>
     *         A fixed-width encoding satisfies (2) and (3) without further thought, which is why the advice
     *         throughout is fixed width rather than merely monotone. See the class documentation for what
     *         qualifies, and for the non-ASCII caveats.
     *         <p>
     *         <strong>(4) means {@code C}'s natural order specifically, not merely some consistent order.</strong>
     *         An encoding may well sort by something else — semantic versions held as {@link String} and encoded
     *         into a sortable form are the obvious case — and it will look correct, because the column really is
     *         ordered and every query really does use the index. But {@link ___OrderedRange#contains(Comparable)} and
     *         {@link ___OrderedRange#isEmpty()} answer by {@code compareTo}, so the database and the object would then
     *         disagree about the same range, and nothing here reports it. Where the wanted order is not the
     *         natural one, the endpoint type is wrong: give it a type whose {@code compareTo} <em>is</em> that
     *         order. That is also the answer for a domain with no natural order at all — {@link java.time.Period},
     *         a status ladder, a size chart: the wrapper is what states the order, and stating it is the part that
     *         was missing.
     * @apiNote This is never called with {@code null}: an absent endpoint is a {@code NULL} column and does not
     *         reach here. An existing {@link jakarta.persistence.AttributeConverter} is reused by delegating to
     *         it in one line — but note that it is being used as a codec and not as a JPA conversion, so it is
     *         never registered with the provider and never has anything injected into it.
     * @see #decode(String)
     */
    protected abstract String encode(C value);

    /**
     * Decodes the specified text, as a column holds it without its bound character, back to an endpoint.
     *
     * @param encoded the text to decode; never {@code null}.
     * @return the endpoint for the {@code encoded} text; never {@code null}.
     * @implSpec An implementation has to be the inverse of {@link #encode(Comparable)}. Whatever it throws for
     *         text this class did not write is thrown on to the caller, which is what a column holding something
     *         else deserves.
     * @see #encode(Comparable)
     */
    protected abstract C decode(String encoded);

    // --------------------------------------------------------------------------------------------- compose / decompose
    private String compose(final C value, final char boundCharacter) {
        final var encoded = encode(value);
        if (encoded == null) {
            throw new IllegalStateException("encode(" + value + ") returned null");
        }
        return encoded + boundCharacter;
    }

    private C decompose(final String cut) {
        if (cut.isEmpty()) {
            throw new IllegalStateException("a cut column holds an empty string, which carries no bound character");
        }
        final var encoded = cut.substring(0, cut.length() - 1);
        final var decoded = decode(encoded);
        if (decoded == null) {
            // symmetric with compose(): a null here would make this end look unbounded while its column still
            // holds a marker, so the endpoint and its bound type would disagree about whether the end exists.
            throw new IllegalStateException("decode('" + encoded + "') returned null");
        }
        return decoded;
    }

    // --------------------------------------------------------------------------------------------------------- ___OrderedRange

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     * @implSpec This overrides the inherited default, which would read both endpoints — and therefore
     *         {@link #decode(String) decode} both — only to test them for {@code null}. A cut column is
     *         {@code NULL} exactly when its end is absent, so the two columns answer this on their own.
     */
    @Override
    @Transient
    public boolean isBounded() {
        return rangeLower != null && rangeUpper != null;
    }

    // ----------------------------------------------------------------------------------------------------------- lower

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    @Transient
    public @Nullable C getRangeLower() {
        return rangeLower == null ? null : decompose(rangeLower);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    @Transient
    public @Nullable __BoundType getLowerBoundType() {
        return rangeLower == null
                ? null
                : __BoundType.ofLowerCharacter(rangeLower.charAt(rangeLower.length() - 1));
    }

    /**
     * Replaces the lower end of this range with the specified endpoint, which belongs to it.
     *
     * @param value the lower endpoint; {@code null} for no lower bound.
     * @implSpec The implementation defers to {@link #setRangeLower(Comparable, __BoundType)} with
     *         {@link __BoundType#CLOSED}.
     * @apiNote This and {@link #setRangeUpper(Comparable)} together write {@code [start, end)}, the convention
     *         everything nearby has settled on: {@code java.time} names its own parameters {@code startInclusive}
     *         and {@code endExclusive} throughout, SQL:2011 defines its {@code PERIOD} as closed-open, and
     *         PostgreSQL canonicalizes every discrete range type to {@code [)}. It is also the only pair which
     *         tiles an axis, two adjacent ranges meeting with neither gap nor overlap.
     *         <p>
     *         The default is a convenience and not a claim about this range. Where the bound type is part of what
     *         the data says — which is the case this class exists for — use
     *         {@link #setRangeLower(Comparable, __BoundType)} and say it.
     */
    public void setRangeLower(final @Nullable C value) {
        setRangeLower(value, __BoundType.CLOSED);
    }

    /**
     * Replaces the lower end of this range with the specified endpoint and bound type.
     *
     * @param value     the lower endpoint; {@code null} for no lower bound, in which case the {@code boundType}
     *                  has nothing to apply to and is not written.
     * @param boundType whether the {@code value} belongs to this range; never {@code null}, whatever the
     *                  {@code value} is.
     * @throws NullPointerException if {@code boundType} is {@code null}.
     * @implSpec The cut column is composed here, not at flush time. That is deliberate: a cut built in a
     *           {@link jakarta.persistence.PrePersist @PrePersist} or
     *           {@link jakarta.persistence.PreUpdate @PreUpdate} callback would leave the mapped column unchanged
     *           when a caller modified this range, so the provider would see no dirty attribute, issue no
     *           {@code UPDATE}, and never reach the callback — the change would be lost silently.
     */
    public void setRangeLower(final @Nullable C value, final __BoundType boundType) {
        Objects.requireNonNull(boundType, "boundType is null");
        rangeLower = value == null ? null : compose(value, boundType.getLowerCharacter());
    }

    // ----------------------------------------------------------------------------------------------------------- upper

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    @Transient
    public @Nullable C getRangeUpper() {
        return rangeUpper == null ? null : decompose(rangeUpper);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    @Transient
    public @Nullable __BoundType getUpperBoundType() {
        return rangeUpper == null
                ? null
                : __BoundType.ofUpperCharacter(rangeUpper.charAt(rangeUpper.length() - 1));
    }

    /**
     * Replaces the upper end of this range with the specified endpoint, which does not belong to it.
     *
     * @param value the upper endpoint; {@code null} for no upper bound.
     * @implSpec The implementation defers to {@link #setRangeUpper(Comparable, __BoundType)} with
     *         {@link __BoundType#OPEN}.
     * @apiNote Exclusive, where {@link #setRangeLower(Comparable)} is inclusive — see there for why the asymmetry
     *         is the right default. On a continuous axis it is also the only honest one: a closed upper bound
     *         written as {@code 23:59:59} stands in for an open one, and the substitution is never exact, only
     *         close enough at whatever precision happens to be in play.
     */
    public void setRangeUpper(final @Nullable C value) {
        setRangeUpper(value, __BoundType.OPEN);
    }

    /**
     * Replaces the upper end of this range with the specified endpoint and bound type.
     *
     * @param value     the upper endpoint; {@code null} for no upper bound, in which case the {@code boundType}
     *                  has nothing to apply to and is not written.
     * @param boundType whether the {@code value} belongs to this range; never {@code null}, whatever the
     *                  {@code value} is.
     * @throws NullPointerException if {@code boundType} is {@code null}.
     * @implSpec As {@link #setRangeLower(Comparable, __BoundType)}: composed here rather than at flush time,
     *           which is what makes the attribute dirty.
     */
    public void setRangeUpper(final @Nullable C value, final __BoundType boundType) {
        Objects.requireNonNull(boundType, "boundType is null");
        rangeUpper = value == null ? null : compose(value, boundType.getUpperCharacter());
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The lower cut — the encoded endpoint followed by its bound character — mapped to the
     * {@value #COLUMN_NAME_RANGE_LOWER} column; {@code null} for no lower bound.
     */
    @Basic(optional = true)
    @Column(name = COLUMN_NAME_RANGE_LOWER, nullable = true, insertable = true, updatable = true)
    private @Nullable String rangeLower;

    /**
     * The upper cut — the encoded endpoint followed by its bound character — mapped to the
     * {@value #COLUMN_NAME_RANGE_UPPER} column; {@code null} for no upper bound.
     */
    @Basic(optional = true)
    @Column(name = COLUMN_NAME_RANGE_UPPER, nullable = true, insertable = true, updatable = true)
    private @Nullable String rangeUpper;
}
