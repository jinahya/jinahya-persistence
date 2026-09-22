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
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Entity
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
