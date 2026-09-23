package com.github.jinahya.persistence.more.colormodel;

/*-
 * #%L
 * jinahya-persistence-more
 * %%
 * Copyright (C) 2025 - 2026 Jinahya, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * An entity which declares its identity on a <em>getter</em>, making its own access type
 * {@link jakarta.persistence.AccessType#PROPERTY PROPERTY}.
 * <p>
 * This is the adversarial case for the colors: were the mapped superclasses not annotated
 * {@link jakarta.persistence.Access @Access}({@code FIELD}), this entity would drag the whole hierarchy to property
 * access, and the {@link jakarta.persistence.Transient @Transient} accessors sitting alongside each component —
 * {@code getRedAsEightBits()}, {@code isOpaque()}, {@code getComponentCount()} — would unmap the components themselves.
 * Every component would silently stop being persisted.
 * <p>
 * The {@link Access @Access}({@code PROPERTY}) is stated rather than inferred. Jakarta Persistence 3.2 &sect;2.3.1
 * decides a hierarchy's default access from the classes which do <em>not</em> declare one, so the {@code @Id} on the
 * getter below settles it on its own and Hibernate ORM 7.4 and EclipseLink both read it that way. ORM 7.2 instead
 * propagates the access type of the <em>root</em> mapped superclass — {@link ___MappedColor}, which declares
 * {@code @Access(FIELD)} so that an {@link jakarta.persistence.Embeddable @Embeddable} can extend this hierarchy on
 * EclipseLink — down onto the entity, looks for an {@code @Id} among its fields, finds none, and rejects the class as
 * having no identifier. Saying it here costs one annotation and keeps the profile buildable; the implicit path stays
 * covered by {@code _PropertyAccessNodeEntity}, whose roots are interfaces and declare no access type at all.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Entity
@Access(AccessType.PROPERTY)
@Table(name = _PropertyAccessRgbaEntity.TABLE_NAME)
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public class _PropertyAccessRgbaEntity extends __MappedRgba {

    static final String TABLE_NAME = "property_access_rgba_entity";

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
