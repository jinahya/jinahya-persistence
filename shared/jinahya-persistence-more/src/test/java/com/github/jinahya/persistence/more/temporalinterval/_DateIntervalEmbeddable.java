package com.github.jinahya.persistence.more.temporalinterval;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Embeddable;

/**
 * An embeddable extending {@link __MappedLocalDateInterval}, so that one table can carry more than one interval.
 * <p>
 * A {@code @MappedSuperclass} is inherited once, so an entity which extends one gets a single interval. This is the
 * other form, and the one a downstream needs for a table with two: the mapped superclass is extended by an
 * {@link Embeddable @Embeddable} of its own, which is then embedded as often as wanted.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Access(AccessType.FIELD)
@Embeddable
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public class _DateIntervalEmbeddable extends __MappedLocalDateInterval {

}
