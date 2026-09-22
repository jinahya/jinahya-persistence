/**
 * Abstract test classes and utilities for testing what {@code jinahya-persistence-more} defines.
 * <p>
 * These types live in the main source set, rather than the test source set, so that they can be depended on from other
 * modules' tests.
 * <p>
 * There is one base per extension point: wherever {@code jinahya-persistence-more} offers something to extend or
 * implement, the class which says what that thing has to do is here. A subclass names the concrete class and, where
 * the assertions need values to work with, supplies samples; everything else is inherited. Every base asserts through
 * the published API of what it tests, never through the mechanism behind it, so an entity which implements a contract
 * directly and one which delegates to the reflective back door are both covered by the same assertions.
 * <p>
 * The layout mirrors the module under test: what tests {@code more.x} lives in {@code more.x.test}, so this package
 * holds what tests {@code more} itself and the sibling packages hold the rest —
 * {@link com.github.jinahya.persistence.more.converter.test},
 * {@link com.github.jinahya.persistence.more.colormodel.test},
 * {@link com.github.jinahya.persistence.more.temporalinterval.test} and
 * {@link com.github.jinahya.persistence.more.orderedrange.test}.
 * <p>
 * The assertions are {@link org.junit.jupiter.api.Assertions}, and nothing else. A published test layer which dragged
 * an assertion library, a validation implementation, or a persistence provider onto a downstream's classpath would be
 * charging for itself twice.
 *
 * <h2>Attribute enums</h2>
 * {@link com.github.jinahya.persistence.more.test.__AttributeEnum_Test} verifies that an
 * {@link com.github.jinahya.persistence.more.__AttributeEnum} has no {@code null} and no duplicate attribute values,
 * and mirrors the {@code __OfString}, {@code __OfNumber}, {@code __OfInteger} and {@code __OfLong} nests of what it
 * tests, so a subclass names only what those do not already fix. Round-tripping the constants through a converter is
 * {@link com.github.jinahya.persistence.more.converter.test.__AttributeEnumConverter_Test}, next door with the rest
 * of the converters. {@link com.github.jinahya.persistence.more.test.__AttributeEnum_TestUtils} offers the same
 * traversals, including random constant selection, to tests which do not extend those classes.
 *
 * <h2>Self-referencing entities</h2>
 * {@link com.github.jinahya.persistence.more.test.__SelfReferencing_Test} reads the parent and the depth of an
 * instance, and {@link com.github.jinahya.persistence.more.test.__SelfReferencingOrdered_Test} additionally reads the
 * sibling ordinal. Reading them is the test: an entity which carries the association on a member marked
 * {@link com.github.jinahya.persistence.more.__SelfReferencingParent @__SelfReferencingParent} or
 * {@link com.github.jinahya.persistence.more.__SelfReferencingOrdinal @__SelfReferencingOrdinal} has nothing checking
 * that the mark is present, is present once, and sits on a member of the right type until something asks for the
 * value — and an ordinal which throws rather than answering {@code null} aborts the validation pass which existed to
 * report it.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@org.jspecify.annotations.NullMarked
package com.github.jinahya.persistence.more.test;
