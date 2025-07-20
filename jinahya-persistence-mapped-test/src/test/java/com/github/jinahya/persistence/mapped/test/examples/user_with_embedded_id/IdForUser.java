package com.github.jinahya.persistence.mapped.test.examples.user_with_embedded_id;

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

import lombok.ToString;

import java.io.Serial;

@ToString(callSuper = true)
class IdForUser extends _MappedIdForUser {

    @Serial
    private static final long serialVersionUID = 7137484531059857534L;

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    protected IdForUser() {
        super();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object
}
