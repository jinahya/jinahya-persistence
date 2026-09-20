package com.github.jinahya.persistence.more.interval;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * An entity extending {@link __MappedYearInterval}, for exercising it against a real provider.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Access(AccessType.FIELD)
@Entity
@Table(name = _YearIntervalEntity.TABLE_NAME)
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public class _YearIntervalEntity extends __MappedYearInterval {

    static final String TABLE_NAME = "year_interval";

    /**
     * Returns the identifier of this entity.
     *
     * @return the identifier of this entity.
     */
    public Long getId() {
        return id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
