package com.github.jinahya.persistence.more.interval;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * An entity extending {@link __MappedLocalDateInterval}, for exercising it against a real provider.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Access(AccessType.FIELD)
@Entity
@Table(name = _LocalDateIntervalEntity.TABLE_NAME)
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public class _LocalDateIntervalEntity extends __MappedLocalDateInterval {

    static final String TABLE_NAME = "local_date_interval";

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
