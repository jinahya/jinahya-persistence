package com.github.jinahya.persistence.more.orderedrange;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

/**
 * An entity which puts its {@code @Id} on a <em>getter</em>, making its own access type {@code PROPERTY}.
 * <p>
 * Without the forced {@code @Access(AccessType.FIELD)} on {@link __MappedOrderedRange}, the hierarchy would flip with it
 * and the two cut columns would be looked for on accessors instead of fields — unmapping them, and picking up the
 * {@code @Transient} ones that are shaped like properties.
 * <p>
 * The {@link Access @Access}({@code PROPERTY}) is stated rather than inferred, which its sibling
 * {@code _PropertyAccessNodeEntity} does not have to do. Jakarta Persistence 3.2 &sect;2.3.1 decides a hierarchy's
 * default access from the classes which do <em>not</em> declare one, so the {@code @Id} on the getter below settles
 * it and {@link __MappedOrderedRange}'s own {@code @Access(FIELD)} is excluded from the question. Hibernate ORM 7.4
 * and EclipseLink read it that way; ORM 7.2 instead propagates the access type of the <em>root</em> mapped
 * superclass down onto the entity, looks for an {@code @Id} among the fields, finds none, and rejects the class as
 * having no identifier. That sibling escapes it only because the roots of its chain -- the {@code __SelfReferencing}
 * interfaces -- declare no access type at all, where this hierarchy's root is {@link __MappedOrderedRange} itself.
 * {@code _PropertyAccessRgbaEntity} used to escape it for the same reason and no longer does: {@code ___MappedColor}
 * now declares {@code @Access(FIELD)} too, so that an {@code @Embeddable} can extend the color hierarchy on
 * EclipseLink, and that entity states {@code @Access(PROPERTY)} exactly as this one does. Saying it here costs one
 * annotation and keeps the profile buildable; the implicit path stays covered by the one sibling left on it.
 */
@Entity
@Access(AccessType.PROPERTY)
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
