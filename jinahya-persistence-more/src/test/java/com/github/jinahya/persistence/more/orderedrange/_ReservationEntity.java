package com.github.jinahya.persistence.more.orderedrange;

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
 * An entity carrying <em>two</em> ranges, each overriding the two column names it inherits.
 */
@Access(AccessType.FIELD)
@Entity
@Table(name = _ReservationEntity.TABLE_NAME)
@SuppressWarnings({"java:S101"})
public class _ReservationEntity {

    static final String TABLE_NAME = "reservation";

    static final String COLUMN_NAME_STAY_LOWER = "stay_lower";

    static final String COLUMN_NAME_STAY_UPPER = "stay_upper";

    static final String COLUMN_NAME_HOLD_LOWER = "hold_lower";

    static final String COLUMN_NAME_HOLD_UPPER = "hold_upper";

    Long getId() {
        return id;
    }

    public @Nullable _DateRangeEmbeddable getStay() {
        return stay;
    }

    public void setStay(final @Nullable _DateRangeEmbeddable stay) {
        this.stay = stay;
    }

    public @Nullable _DateRangeEmbeddable getHold() {
        return hold;
    }

    public void setHold(final @Nullable _DateRangeEmbeddable hold) {
        this.hold = hold;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @AttributeOverride(name = "rangeLower", column = @Column(name = COLUMN_NAME_STAY_LOWER))
    @AttributeOverride(name = "rangeUpper", column = @Column(name = COLUMN_NAME_STAY_UPPER))
    @Embedded
    private @Nullable _DateRangeEmbeddable stay;

    @AttributeOverride(name = "rangeLower", column = @Column(name = COLUMN_NAME_HOLD_LOWER))
    @AttributeOverride(name = "rangeUpper", column = @Column(name = COLUMN_NAME_HOLD_UPPER))
    @Embedded
    private @Nullable _DateRangeEmbeddable hold;
}
