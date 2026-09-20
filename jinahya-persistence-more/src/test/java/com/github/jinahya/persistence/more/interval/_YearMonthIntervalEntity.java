package com.github.jinahya.persistence.more.interval;

import com.github.jinahya.persistence.more.converter.__TemporalAccessorStringAttributeConverters.OfYearMonth;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * An entity extending {@link __MappedYearMonthInterval}, for exercising it against a real provider.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
// restated here: EclipseLink does not apply a @Convert(attributeName) which a mapped superclass declares against an
// attribute inherited from a higher one. Hibernate does. See __MappedYearMonthInterval.
@Access(AccessType.FIELD)
@Convert(attributeName = "intervalStart", converter = OfYearMonth.class)
@Convert(attributeName = "intervalEnd", converter = OfYearMonth.class)
@Entity
@Table(name = _YearMonthIntervalEntity.TABLE_NAME)
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public class _YearMonthIntervalEntity extends __MappedYearMonthInterval {

    static final String TABLE_NAME = "year_month_interval";

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
