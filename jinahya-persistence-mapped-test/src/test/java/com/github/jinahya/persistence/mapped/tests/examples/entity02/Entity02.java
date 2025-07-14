package com.github.jinahya.persistence.mapped.tests.examples.entity02;

/*-
 * #%L
 * jinahya-persistence-mapped-test
 * %%
 * Copyright (C) 2024 - 2025 Jinahya, Inc.
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

import com.github.jinahya.persistence.mapped.__MappedEntity;
import jakarta.annotation.Nullable;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = Entity02.TABLE_NAME)
public class Entity02 extends __MappedEntity<Entity02, Long> {

    public static final String TABLE_NAME = "entity02";

    // -----------------------------------------------------------------------------------------------------------------
    protected Entity02() {
        super();
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return super.toString() + '{' +
               "id=" + id +
               '}';
    }

    @Override
    public final boolean equals(final Object obj) {
        return super.equals(obj);
    }

    @Override
    public final int hashCode() {
        return super.hashCode();
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    protected final Long getId__() {
        return getId();
    }

    @Override
    protected final void setId__(final Long id__) {
        setId(id__);
    }

    // -----------------------------------------------------------------------------------------------------------------
    public Long getId() {
        return id;
    }

    protected void setId(final Long id) {
        this.id = id;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Nullable
    public String getName() {
        return name;
    }

    protected void setName(@Nullable final String name) {
        this.name = name;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // Caused by: Exception [EclipseLink-6023] (Eclipse Persistence Services - 4.0.7.v202506240759-923a428d4722d199eff24e730890e156ce7ea9de): org.eclipse.persistence.exceptions.QueryException
    // Exception Description: The list of fields to insert into the table [DatabaseTable(entity1)] is empty.  You must define at least one mapping for this table.
    @Nullable
    @Basic(optional = true)
    @Column(name = "name", nullable = true, insertable = true, updatable = true)
    private String name;
}
