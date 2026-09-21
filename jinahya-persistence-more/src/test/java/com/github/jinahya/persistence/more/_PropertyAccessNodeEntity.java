package com.github.jinahya.persistence.more;

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

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.jspecify.annotations.Nullable;

/**
 * An entity which orders its siblings under {@link jakarta.persistence.AccessType#PROPERTY PROPERTY} access, declaring
 * its identity on a getter and marking its accessors.
 * <p>
 * This is the adversarial placement. Every accessor it declares is a mapping candidate, so the two it overrides from
 * {@link __SelfReferencing} have to be annotated {@link Transient @Transient} <em>here</em> — nothing declared on the
 * interface reaches them. What it does <em>not</em> declare is {@link #getSiblingOrdinal()}: the default method is
 * inherited from {@link __SelfReferencingOrdered}, and an inherited interface method is not a persistent property, so
 * no column comes of it.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Entity
@Table(name = _PropertyAccessNodeEntity.TABLE_NAME)
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public class _PropertyAccessNodeEntity implements __SelfReferencingOrdered<_PropertyAccessNodeEntity> {

    static final String TABLE_NAME = "property_access_node_entity";

    static final String COLUMN_NAME_PARENT = "hierarchy_parent_id";

    static final String COLUMN_NAME_DISPLAY_ORDER = "display_order";

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance, which the no-arg constructor a provider requires.
     */
    public _PropertyAccessNodeEntity() {
        super();
    }

    _PropertyAccessNodeEntity(final @Nullable _PropertyAccessNodeEntity parent, final Integer displayOrder) {
        this.parent = parent;
        this.displayOrder = displayOrder;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Nullable
    @Transient
    @Override
    public _PropertyAccessNodeEntity getHierarchyParent() {
        return __SelfReferencingUtils.parentOf(this);
    }

    @Transient
    @Override
    public int getHierarchyDepth() {
        var depth = 0;
        for (var p = getHierarchyParent(); p != null; p = p.getHierarchyParent()) {
            depth++;
        }
        return depth;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    @__SelfReferencingParent
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = COLUMN_NAME_PARENT, nullable = true, insertable = true, updatable = true)
    public @Nullable _PropertyAccessNodeEntity getParent() {
        return parent;
    }

    public void setParent(final @Nullable _PropertyAccessNodeEntity parent) {
        this.parent = parent;
    }

    @__SelfReferencingOrdinal
    @NotNull
    @PositiveOrZero
    @Column(name = COLUMN_NAME_DISPLAY_ORDER, nullable = false, insertable = true, updatable = true)
    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(final Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    // -----------------------------------------------------------------------------------------------------------------

    private Long id;

    private _PropertyAccessNodeEntity parent;

    private Integer displayOrder;
}
