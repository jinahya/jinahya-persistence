/**
 * Utilities for working with the Jakarta Persistence metamodel.
 * <p>
 * These classes bridge the gap between the metamodel and plain reflection:
 * {@link com.github.jinahya.persistence.metamodel.__ManagedTypeUtils} and
 * {@link com.github.jinahya.persistence.metamodel.__EntityTypeUtils} resolve a
 * {@link jakarta.persistence.metamodel.ManagedType managedType} or an
 * {@link jakarta.persistence.metamodel.EntityType entityType} for a class, from the first of several
 * {@link jakarta.persistence.EntityManagerFactory entityManagerFactories} which knows it, while
 * {@link com.github.jinahya.persistence.metamodel.__AttributeUtils} reads and writes the value of an
 * {@link jakarta.persistence.metamodel.Attribute attribute}, whether it is mapped to a field or to a property.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@org.jspecify.annotations.NullMarked
package com.github.jinahya.persistence.metamodel;
