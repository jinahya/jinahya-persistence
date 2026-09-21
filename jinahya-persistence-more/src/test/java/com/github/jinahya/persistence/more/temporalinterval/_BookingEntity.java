package com.github.jinahya.persistence.more.temporalinterval;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.jspecify.annotations.Nullable;

/**
 * An entity carrying two intervals, each in columns of its own.
 * <p>
 * This is what settles that the column names declared by {@link ___MappedTemporalInterval} are defaults rather than
 * commitments: neither pair below is written to this table, and a downstream naming its own columns is the ordinary
 * case rather than the exception.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Access(AccessType.FIELD)
@Entity
@Table(name = _BookingEntity.TABLE_NAME)
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public class _BookingEntity {

    static final String TABLE_NAME = "booking";

    static final String COLUMN_NAME_STAY_START = "stay_start";

    static final String COLUMN_NAME_STAY_END = "stay_end";

    static final String COLUMN_NAME_HOLD_START = "hold_start";

    static final String COLUMN_NAME_HOLD_END = "hold_end";

    /**
     * Returns the identifier of this entity.
     *
     * @return the identifier of this entity.
     */
    public Long getId() {
        return id;
    }

    /**
     * Returns the period this booking is stayed for.
     *
     * @return the period this booking is stayed for.
     */
    public @Nullable _DateIntervalEmbeddable getStay() {
        return stay;
    }

    /**
     * Replaces the period this booking is stayed for.
     *
     * @param stay new value for the {@code stay} attribute.
     */
    public void setStay(final @Nullable _DateIntervalEmbeddable stay) {
        this.stay = stay;
    }

    /**
     * Returns the period this booking is held for.
     *
     * @return the period this booking is held for.
     */
    public @Nullable _DateIntervalEmbeddable getHold() {
        return hold;
    }

    /**
     * Replaces the period this booking is held for.
     *
     * @param hold new value for the {@code hold} attribute.
     */
    public void setHold(final @Nullable _DateIntervalEmbeddable hold) {
        this.hold = hold;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @AttributeOverride(name = "intervalStart", column = @Column(name = COLUMN_NAME_STAY_START))
    @AttributeOverride(name = "intervalEnd", column = @Column(name = COLUMN_NAME_STAY_END))
    @Embedded
    private @Nullable _DateIntervalEmbeddable stay;

    @AttributeOverride(name = "intervalStart", column = @Column(name = COLUMN_NAME_HOLD_START))
    @AttributeOverride(name = "intervalEnd", column = @Column(name = COLUMN_NAME_HOLD_END))
    @Embedded
    private @Nullable _DateIntervalEmbeddable hold;
}
