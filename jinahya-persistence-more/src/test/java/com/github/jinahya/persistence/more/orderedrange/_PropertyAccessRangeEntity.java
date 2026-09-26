package com.github.jinahya.persistence.more.orderedrange;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

/**
 * An entity which puts its {@code @Id} on a <em>getter</em>, making its own access type {@code PROPERTY}.
 * <p>
 * Without the forced {@code @Access(AccessType.FIELD)} on {@link __MappedOrderedRange}, the hierarchy would flip with
 * it and the two cut columns would be looked for on accessors instead of fields — unmapping them, and picking up the
 * {@code @Transient} ones that are shaped like properties.
 */
@Entity
@Table(name = _PropertyAccessRangeEntity.TABLE_NAME)
@SuppressWarnings({"java:S101"})
public class _PropertyAccessRangeEntity extends __MappedOrderedRange<LocalDate> {

    static final String TABLE_NAME = "property_access_range";

    @Override
    protected String encode(final LocalDate value) {
        return value.toString();
    }

    @Override
    protected LocalDate decode(final String encoded) {
        return LocalDate.parse(encoded);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    private Long id;
}
