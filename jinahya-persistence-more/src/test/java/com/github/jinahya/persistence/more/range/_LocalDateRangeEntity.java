package com.github.jinahya.persistence.more.range;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A range of dates. The ISO form is fixed-width — the year is padded to four digits — so it sorts, is prefix-free,
 * and the encoding is nothing but {@code toString} and {@code parse}.
 */
@Entity
@Table(name = _LocalDateRangeEntity.TABLE_NAME)
@Access(AccessType.FIELD)
@SuppressWarnings({"java:S101"})
public class _LocalDateRangeEntity extends __MappedRange<LocalDate> {

    static final String TABLE_NAME = "local_date_range";

    /** Counts {@link #decode(String)} calls, so a test can show that decoding happens on demand and not on load. */
    static final AtomicInteger DECODE_COUNT = new AtomicInteger();

    @Override
    protected String encode(final LocalDate value) {
        return value.toString();
    }

    @Override
    protected LocalDate decode(final String encoded) {
        DECODE_COUNT.incrementAndGet();
        return LocalDate.parse(encoded);
    }

    Long getId() {
        return id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
