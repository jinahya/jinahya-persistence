/**
 * Classes for a range — two ends over a totally ordered domain, each of which may be absent, and each of which, when
 * present, either includes its endpoint or does not.
 *
 * <h2>The concept, which is nine shapes</h2>
 * Each end has three states, not four, because an absent endpoint has nothing for open or closed to apply to: absent,
 * present-and-open, present-and-closed. Three by three is nine, and all nine are expressible here — {@code (,)},
 * {@code (,b)}, {@code (,b]}, {@code (a,)}, {@code (a,b)}, {@code (a,b]}, {@code [a,)}, {@code [a,b)}, {@code [a,b]} —
 * independently, row by row.
 *
 * <h2>Why the bound type is data</h2>
 * It would be tempting to fix it per column, and for most schemas it would be right: a bookings column is {@code [)} in
 * every row. It is not right in general, and two cases settle it.
 * <p>
 * Histogram bins are half-open except the last, which must be closed so the maximum falls somewhere — one column pair,
 * mixed bounds. And on a continuous axis that last bin <em>cannot</em> be rewritten half-open, because there is no next
 * value after the maximum. That rewrite, {@code [a,b]} to {@code [a,b+1)}, is available only on a discrete domain,
 * which is exactly why PostgreSQL canonicalizes {@code daterange} and {@code int4range} and refuses to canonicalize
 * {@code tstzrange} and {@code numrange}. A column populated from a PostgreSQL range carries whatever bounds it was
 * written with, for the same reason.
 * <p>
 * So each end records its own bound type, and {@link __BoundType} — not the pair — is the primitive.
 * {@link com.github.jinahya.persistence.more.orderedrange.__RangeBounds} remains as the derived pair, for the notation
 * and for the rules that genuinely concern both ends at once.
 *
 * <h2>What a row looks like</h2>
 * Two columns, and each holds a <em>cut</em> rather than a value: the encoded endpoint followed by one character saying
 * whether that endpoint belongs to the range. {@code NULL} is an absent endpoint.
 * <pre>
 * range_lower   range_upper    means
 * -----------   -----------    ------------------------
 * 2026-01-01[   2026-04-01)    [2026-01-01, 2026-04-01)
 * 0090.00[      0100.00]       [90, 100]
 * NULL          0100.00]       (, 100]
 * NULL          NULL           (,)
 * </pre>
 * An absent end has a {@code null} bound type, always — which is how the combination PostgreSQL has to normalize away,
 * a missing bound declared inclusive, is kept unwritable here rather than corrected.
 *
 * <h2>What it costs, and it is not nothing</h2>
 * A column carrying an endpoint <em>and</em> a marker holds two facts, and no {@code DATE} or {@code NUMERIC} can carry
 * the second. So the columns are text, the database no longer validates the values, and the endpoint encoding has to
 * be
 * <strong>order-preserving</strong> or every predicate answers the wrong rows.
 * <p>
 * That is free for a fixed-width ISO form and real work for a number, which needs a fixed width and a sign scheme both.
 * It must also be <strong>prefix-free</strong>, because the marker trails the value and falls into the comparison where
 * one encoding prefixes another. {@link __MappedOrderedRange#encode(Comparable)} states the contract in full. Nothing
 * checks it: a sample of one pair per row cannot verify an encoding over a domain, and it would compare by UTF-16 code
 * unit where the column compares by its collation. Verify an encoder in a test of the encoder.
 * <p>
 * <strong>If the bound type is in fact uniform for a column, this package is the wrong tool.</strong> Two plain
 * typed columns say it better: see
 * {@link com.github.jinahya.persistence.more.temporalinterval the temporal-interval package}, which fixes
 * {@code [start, end)}, keeps a real {@code DATE}, measures its own length, and needs no encoding at all.
 *
 * <h2>The marker trails</h2>
 * One character, after the encoded endpoint, never before. A constant-width suffix shifts every key equally, so the
 * column still sorts by the endpoint; a leading bracket would not, since {@code (} is {@code 0x28} and {@code [} is
 * {@code 0x5B}. Reading is total whatever the endpoint encodes to — the last character is the marker, everything before
 * it is the value — so an encoding which itself ends in a bracket, or which is empty, still parses.
 * <p>
 * One asymmetry falls out of the ASCII, and it is worth knowing before writing a query. The two upper markers sort in
 * cut order ({@code )} before {@code ]}), so the upper half of a containment is a plain comparison. The two lower
 * markers do not ({@code (} before {@code [}, where a closed lower bound starts earlier), so the lower half needs an
 * index-usable comparison plus a recheck on the boundary rows. Both are written out on {@link __MappedOrderedRange}.
 *
 * <h2>How this differs from an interval</h2>
 * {@link ___TemporalInterval} is bound by {@link java.time.temporal.Temporal}: a span between two <em>points on an
 * axis</em>, which is what makes its length a value. This is bound by {@link java.lang.Comparable} alone, which admits
 * what an interval cannot be — {@link java.time.Duration}, an <em>amount</em> rather than a point, and
 * {@link java.math.BigDecimal}, {@link java.lang.Integer}, {@link java.lang.String}, which have no axis at all.
 * <p>
 * {@link java.time.Period} is admitted by neither: it is not {@link java.lang.Comparable}, because {@code P1M} and
 * {@code P30D} have no defined order.
 *
 * <h2>One thing the schema must declare</h2>
 * A character column is ordered by its <strong>collation</strong>, and everything here compares by UTF-16 code unit.
 * Under a case-insensitive or locale-aware collation the two disagree, which for these columns is a correctness bug
 * rather than a nuisance. Declare them binary or deterministic — {@code COLLATE "C"} on PostgreSQL, a {@code _bin}
 * collation on MySQL.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="https://www.postgresql.org/docs/current/rangetypes.html">PostgreSQL range types</a>
 */
@org.jspecify.annotations.NullMarked
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

import com.github.jinahya.persistence.more.temporalinterval.___TemporalInterval;