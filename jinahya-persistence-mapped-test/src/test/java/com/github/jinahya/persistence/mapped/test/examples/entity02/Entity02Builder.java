package com.github.jinahya.persistence.mapped.test.examples.entity02;

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

import com.github.jinahya.persistence.mapped.__MappedEntityBuilder;

class Entity02Builder extends __MappedEntityBuilder<Entity02Builder, Entity02> {

    Entity02Builder() {
        super(Entity02.class);
    }

    // -----------------------------------------------------------------------------------------------------------------
    Long id() {
        return id;
    }

    public Entity02Builder id(final Long id) {
        this.id = id;
        return this;
    }

    // -----------------------------------------------------------------------------------------------------------------
    String name() {
        return name;
    }

    Entity02Builder name(final String name) {
        this.name = name;
        return this;
    }

    // -----------------------------------------------------------------------------------------------------------------
    private Long id;

    private String name;
}
