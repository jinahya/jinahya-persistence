package com.github.jinahya.persistence.more.orderedrange;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Embeddable;

import java.time.LocalDate;

/**
 * The embeddable form — what a table carrying two ranges needs, since a {@code @MappedSuperclass} is inherited once.
 * EclipseLink walks the mapped-superclass chain of an {@code @Embeddable} and has been seen to fail where a link in
 * that chain declares no access type of its own, which is why {@link __MappedOrderedRange} forces one.
 */
@Access(AccessType.FIELD)
@Embeddable
@SuppressWarnings({"java:S101"})
public class _DateRangeEmbeddable extends __MappedOrderedRange<LocalDate> {

    @Override
    protected String encode(final LocalDate value) {
        return value.toString();
    }

    @Override
    protected LocalDate decode(final String encoded) {
        return LocalDate.parse(encoded);
    }
}
