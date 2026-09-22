/**
 * Abstract test classes for testing what {@code com.github.jinahya.persistence.more.orderedrange} defines.
 * <p>
 * {@link com.github.jinahya.persistence.more.orderedrange.test.__MappedOrderedRange_Test} covers the two ends of a
 * range and their bound types, and then the part worth extending it for: the
 * {@linkplain com.github.jinahya.persistence.more.orderedrange.__MappedOrderedRange#encode(java.lang.Comparable)
 * encoding} contract — round-tripping, non-empty, prefix-free, order-preserving — which the class it tests documents
 * as relied on rather than checked, and whose breach is silent.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@org.jspecify.annotations.NullMarked
package com.github.jinahya.persistence.more.orderedrange.test;
