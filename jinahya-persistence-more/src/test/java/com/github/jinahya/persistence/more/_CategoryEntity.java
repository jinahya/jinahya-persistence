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

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.jspecify.annotations.Nullable;

/**
 * A concrete entity which orders its siblings, using {@link AccessType#FIELD FIELD} access and marking its fields.
 * <p>
 * It names its members on its own terms and never declares {@link #getSiblingOrdinal()}, so the ordinal is read the way
 * the marks are meant to be read: through {@link __SelfReferencingUtils}, by a provider-managed instance, against a
 * real database.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Access(AccessType.FIELD)
@Entity
@Table(name = _CategoryEntity.TABLE_NAME)
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public class _CategoryEntity implements __SelfReferencingOrdered<_CategoryEntity> {

    static final String TABLE_NAME = "category_entity";

    static final String COLUMN_NAME_PARENT = "hierarchy_parent_id";

    static final String COLUMN_NAME_SIBLING_ORDINAL = "sibling_ordinal";

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance with no name, which the no-arg constructor a provider requires.
     */
    public _CategoryEntity() {
        super();
    }

    _CategoryEntity(final String name, final @Nullable _CategoryEntity parent, final Integer siblingOrdinal) {
        this.name = name;
        this.parent = parent;
        this.siblingOrdinal = siblingOrdinal;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Nullable
    @Override
    public _CategoryEntity getHierarchyParent() {
        return __SelfReferencingUtils.parentOf(this);
    }

    @Override
    public int getHierarchyDepth() {
        var depth = 0;
        for (var p = getHierarchyParent(); p != null; p = p.getHierarchyParent()) {
            depth++;
        }
        return depth;
    }

    // -----------------------------------------------------------------------------------------------------------------

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setSiblingOrdinal(final Integer siblingOrdinal) {
        this.siblingOrdinal = siblingOrdinal;
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @__SelfReferencingParent
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = COLUMN_NAME_PARENT, nullable = true, insertable = true, updatable = true)
    private _CategoryEntity parent;

    @__SelfReferencingOrdinal
    @NotNull
    @PositiveOrZero
    @Column(name = COLUMN_NAME_SIBLING_ORDINAL, nullable = false, insertable = true, updatable = true)
    private Integer siblingOrdinal;
}
