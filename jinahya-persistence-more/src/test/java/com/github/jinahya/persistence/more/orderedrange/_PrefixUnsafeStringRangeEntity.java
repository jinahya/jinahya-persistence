package com.github.jinahya.persistence.more.orderedrange;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * A range of strings encoded as themselves — order-preserving, but neither prefix-free nor non-empty, so it breaks
 * the contract on {@link __MappedOrderedRange#encode(Comparable)} in the one way monotonicity alone does not cover.
 */
@Entity
@Table(name = _PrefixUnsafeStringRangeEntity.TABLE_NAME)
@Access(AccessType.FIELD)
@SuppressWarnings({"java:S101"})
public class _PrefixUnsafeStringRangeEntity extends __MappedOrderedRange<String> {

    static final String TABLE_NAME = "prefix_unsafe_string_range";

    @Override
    protected String encode(final String value) {
        return value;
    }

    @Override
    protected String decode(final String encoded) {
        return encoded;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
