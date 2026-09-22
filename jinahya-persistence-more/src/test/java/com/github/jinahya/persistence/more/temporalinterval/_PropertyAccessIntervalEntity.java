package com.github.jinahya.persistence.more.temporalinterval;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * An entity which puts its {@code @Id} on a <em>getter</em>, making its own access type {@code PROPERTY}.
 * <p>
 * The colour and self-referencing packages each have one of these; this package had none, so the one arrangement most
 * likely to go wrong here was the one arrangement nothing exercised. Without the forced
 * {@code @Access(AccessType.FIELD)} on {@link ___MappedTemporalInterval}, this entity would drag the hierarchy to
 * property access with it, and the two interval columns would be looked for on accessors — picking up the
 * {@code @Transient} ones shaped like properties, and unmapping the real columns.
 * <p>
 * The {@link Access @Access}({@code PROPERTY}) is stated rather than inferred, exactly as
 * {@code _PropertyAccessRangeEntity} states it and for the same reason: {@link ___MappedTemporalInterval} is the root
 * of this mapped-superclass chain, and Hibernate ORM 7.2 pushes a root's access type onto the entity instead of
 * reading it from where the entity puts its {@code @Id}. 7.4 and EclipseLink infer it correctly. The library's own
 * javadoc now tells a downstream to state it either way, and this is that advice taken.
 */
@Entity
@Access(AccessType.PROPERTY)
@Table(name = _PropertyAccessIntervalEntity.TABLE_NAME)
@SuppressWarnings({"java:S101"})
public class _PropertyAccessIntervalEntity extends __MappedLocalDateInterval {

    static final String TABLE_NAME = "property_access_interval";

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
