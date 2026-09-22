/**
 * Abstract test classes for testing what {@code com.github.jinahya.persistence.more.temporalinterval} defines.
 * <p>
 * {@link com.github.jinahya.persistence.more.temporalinterval.test.___MappedTemporalInterval_Test} states what every
 * interval does — half-open containment, bounded and empty, the amount and the length — once, against two sample
 * points a subclass supplies, and
 * {@link com.github.jinahya.persistence.more.temporalinterval.test.___DiscreteInterval_Test} adds the closed form
 * that only a discrete axis answers exactly.
 * <p>
 * Every assertion is phrased in terms of those two sample points rather than of any particular comparison, so a model
 * which decides order by something other than the natural one —
 * {@link com.github.jinahya.persistence.more.temporalinterval.__MappedOffsetDateTimeInterval}, which compares
 * instants — is covered by the same ones.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@org.jspecify.annotations.NullMarked
package com.github.jinahya.persistence.more.temporalinterval.test;
