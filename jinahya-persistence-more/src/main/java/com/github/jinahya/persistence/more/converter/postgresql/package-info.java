/**
 * Attribute converters for two column types PostgreSQL has and Jakarta Persistence does not: a one-dimensional array
 * and an {@code interval}.
 *
 * <h2>Why a sub-package</h2>
 * Nothing in the parent package names a database. Everything here does: the array literal syntax, the five forms an
 * {@code interval} is printed in, and the one way both get to the server &mdash; bound as text, with
 * {@code stringtype=unspecified} on the JDBC URL so the server casts the text to the column's type. A converter here
 * works on PostgreSQL and on nothing else, which is what the package name says.
 *
 * <h2>One column type, two providers</h2>
 * The column side of every converter here is {@link java.lang.Object}, not {@link java.lang.String}, because the two
 * providers hand a converter different things for the same column. Hibernate passes an array as the driver's
 * {@link java.sql.Array} and an interval as text; EclipseLink passes an array already unpacked into a Java array and
 * an interval as the driver's own object. Each converter accepts all of those, so it is written once and named in
 * {@link jakarta.persistence.Convert @Convert} under either provider.
 *
 * <h2>Arrays</h2>
 * {@link com.github.jinahya.persistence.more.converter.postgresql.__ArrayAttributeConverter} maps a
 * {@link java.util.List} to an array column, each element through an element converter between it and its text,
 * which is where the parent package's converters come back in: the concrete converters in
 * {@link com.github.jinahya.persistence.more.converter.postgresql.__ArrayAttributeConverters} pair the literal with
 * {@link com.github.jinahya.persistence.more.converter.__NumberStringAttributeConverters.OfInteger} and its
 * siblings. A {@code null} element is a {@code NULL} in the column, and back.
 *
 * <h2>Intervals</h2>
 * {@link com.github.jinahya.persistence.more.converter.postgresql.__IntervalAttributeConverter} sits on the parent's
 * {@link com.github.jinahya.persistence.more.converter.time.__TemporalAmountAttributeConverter} axis, writes a value
 * as the ISO-8601 text its type spells, and reads back whatever style the server prints. An {@code interval} holds
 * months, days and seconds at once and neither {@code java.time} type does, so the two converters in
 * {@link com.github.jinahya.persistence.more.converter.postgresql.__IntervalAttributeConverters} each refuse the
 * fields their type has no room for rather than guess at a length for them.
 *
 * <h2>Registration</h2>
 * As in the parent package: only a concrete converter carries {@link jakarta.persistence.Converter @Converter}, every
 * one of them declares {@code autoApply = false}, and naming it in {@link jakarta.persistence.Convert @Convert}, or in
 * {@code persistence.xml}, is what puts it to work.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@org.jspecify.annotations.NullMarked
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
