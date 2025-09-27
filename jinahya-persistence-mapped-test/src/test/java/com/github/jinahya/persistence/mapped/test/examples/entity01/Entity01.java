package com.github.jinahya.persistence.mapped.test.examples.entity01;

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
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = Entity01.TABLE_NAME)
public class Entity01 implements  __MappedEntity<Long> {

    public static final String TABLE_NAME = "entity01";

    // -----------------------------------------------------------------------------------------------------------------
    protected Entity01() {
        super();
    }

    Entity01(final Entity01Builder builder) {
//        super(builder);
        super();
        id = builder.id();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object

    @Override
    public String toString() {
        return super.toString() + '{' +
               "id=" + id +
               '}';
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof Entity01 that)) {
            return false;
        }
        final var thisId = this.getId();
        final var thatId = that.getId();
        return thisId != null && Objects.equals(thisId, thatId);
    }

    @Override
    public int hashCode() {
//        return getClass().hashCode();
        return Objects.hashCode(getId());
    }

    // -----------------------------------------------------------------------------------------------------------------
    public Long getId() {
        return id;
    }

    protected void setId(final Long id) {
        this.id = id;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
}
