package com.github.jinahya.persistence.more.orderedrange;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * A range of integers, and the worked example of what an order-preserving encoding costs for a number.
 * <p>
 * {@code Integer.toString()} will not do — {@code "9"} sorts above {@code "10"}, and {@code "-5"} above
 * {@code "0003"}. Both problems are fixed at once by biasing the value into the unsigned range and padding it to a
 * fixed ten digits, which is exactly wide enough for {@code 0} through {@code 4294967295}.
 */
@Entity
@Table(name = _IntegerRangeEntity.TABLE_NAME)
@Access(AccessType.FIELD)
@SuppressWarnings({"java:S101"})
public class _IntegerRangeEntity extends __MappedOrderedRange<Integer> {

    static final String TABLE_NAME = "integer_range";

    @Override
    protected String encode(final Integer value) {
        return String.format("%010d", (long) value - Integer.MIN_VALUE);
    }

    @Override
    protected Integer decode(final String encoded) {
        return (int) (Long.parseLong(encoded) + Integer.MIN_VALUE);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
