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

import com.github.jinahya.persistence.mapped.test._MappedEntityPersistenceTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class Entity02PersistenceTest extends _MappedEntityPersistenceTest<Entity02, Long> {

    Entity02PersistenceTest() {
        super(Entity02.class, Long.class);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Test
    @Override
    protected void persistEntityInstance() {
        super.persistEntityInstance();
    }

    @Override
    protected Entity02 persistingEntityInstance(final Entity02 entityInstance) {
        log.debug("persisting {}", entityInstance);
        return super.persistingEntityInstance(entityInstance);
    }

    @Override
    protected void persistedEntityInstance(final Entity02 entityInstance) {
        log.debug("persisted {}", entityInstance);
        super.persistedEntityInstance(entityInstance);
    }
}
