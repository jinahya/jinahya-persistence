/**
 * Interfaces and mapped superclasses for intervals on a temporal axis.
 *
 * <h2>One convention, fixed</h2>
 * An interval here runs from an inclusive start to an exclusive end — {@code [start, end)} — and that is not a
 * per-instance choice. {@link com.github.jinahya.persistence.more.interval.__Interval} carries the reasoning; in
 * short, a closed upper bound does not exist on a continuous axis, so any attempt to write one is exact only at the
 * precision which happens to be in play, while the half-open form ends at the next point exactly and lets adjacent
 * intervals meet with neither gap nor overlap.
 * <p>
 * The consequence for a schema is that an interval is two columns and nothing else. There is no bound token to store
 * and nothing to canonicalize, so a containment reads {@code start <= :t AND end > :t} and uses an ordinary index.
 *
 * <h2>Either point may be absent</h2>
 * A schema needs that constantly — in effect from a date, with no end decided — and an absent point is simply
 * {@code null} in its column. It is not an exception to the convention above: where there is no point, there is
 * nothing to include or to exclude.
 *
 * <h2>Points, not amounts</h2>
 * Of the three self-contained forms ISO 8601-1:2019 gives an interval in clause 4.4 — start and end, start and
 * duration, duration and end — only the first can express an absent bound at all, and only it derives the other two
 * without ambiguity: calendar arithmetic does not run backwards, so a stored amount and a stored point disagree about
 * the third value across a month boundary. The two points are therefore what is kept, and an amount is measured from
 * them when asked for.
 *
 * <h2>Order, and only order</h2>
 * The one requirement an interval has is that its start is not after its end, which needs its two points ordered and
 * nothing else. So {@link com.github.jinahya.persistence.more.interval.__Interval} is bound by
 * {@link java.lang.Comparable} — written {@code Comparable<? super T>}, since {@link java.time.LocalDate} and its
 * siblings compare against their {@code Chrono} interfaces rather than against themselves — and everything order alone
 * decides is defaulted there: the invariant, emptiness, and containment.
 * <p>
 * Measuring is not among them. {@link java.time.temporal.Temporal} supplies arithmetic but not order, and an amount is
 * anyway particular to the point type — a {@link java.time.Period} where the points are dates, a
 * {@link java.time.Duration} where they are instants — so it is declared by the class which knows which it has.
 * <p>
 * The bound admits every point type in {@code java.time}, which is worth stating plainly: it excludes nothing, and it
 * does not promise that a type's natural order is the timeline. For the offset-carrying types it is not.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="https://www.iso.org/standard/70907.html">ISO 8601-1:2019</a>
 */
@org.jspecify.annotations.NullMarked
package com.github.jinahya.persistence.more.interval;

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
