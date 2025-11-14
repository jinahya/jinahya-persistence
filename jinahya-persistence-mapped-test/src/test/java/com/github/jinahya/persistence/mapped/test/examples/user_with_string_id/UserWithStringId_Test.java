package com.github.jinahya.persistence.mapped.test.examples.user_with_string_id;

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

import com.github.jinahya.persistence.mapped.test.__Configure_EqualsVerifier;
import com.github.jinahya.persistence.mapped.test.__MappedEntity_Test;
import jakarta.annotation.Nonnull;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.api.SingleTypeEqualsVerifierApi;

class UserWithStringId_Test extends __MappedEntity_Test<UserWithStringId, String> {

    UserWithStringId_Test() {
        super(UserWithStringId.class, String.class);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @__Configure_EqualsVerifier
    @Nonnull
    @Override
    protected SingleTypeEqualsVerifierApi<UserWithStringId> equals_Verify_(
            final @Nonnull SingleTypeEqualsVerifierApi<UserWithStringId> equalsVerifier) {
        return super.equals_Verify_(equalsVerifier)
                .suppress(Warning.SURROGATE_KEY);
    }
}
