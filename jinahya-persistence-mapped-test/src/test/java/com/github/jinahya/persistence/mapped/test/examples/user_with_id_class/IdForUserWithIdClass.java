package com.github.jinahya.persistence.mapped.test.examples.user_with_id_class;

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

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.ToString;

import java.io.Serial;

@ToString(callSuper = true)
class IdForUserWithIdClass extends _MappedIdForUserWithIdClass {

    @Serial
    private static final long serialVersionUID = -1152447558449093039L;

    // -----------------------------------------------------------------------------------------------------------------
    static IdForUserWithIdClass of(@NotBlank final String name, @NotNull final Integer age) {
        final var instance = new IdForUserWithIdClass();
        instance.setName(name);
        instance.setAge(age);
        return instance;
    }

    // -----------------------------------------------------------------------------------------------------------------
    protected IdForUserWithIdClass() {
        super();
    }
}
