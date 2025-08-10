package com.github.jinahya.persistence.mapped.test;

/*-
 * #%L
 * jinahya-persistence-mapped-test
 * %%
 * Copyright (C) 2025 Jinahya, Inc.
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

import com.github.jinahya.persistence.mapped.__MappedEntityWithGeneratedIdentity;
import jakarta.annotation.Nonnull;
import nl.jqno.equalsverifier.Warning;
import nl.jqno.equalsverifier.api.SingleTypeEqualsVerifierApi;

@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119"  // Type parameter names should comply with a naming convention
})
public abstract class __MappedEntityWithGeneratedIdentityTest<ENITTY extends __MappedEntityWithGeneratedIdentity>
        extends __MappedEntityWithGeneratedIdTest<ENITTY, Long> {

    protected __MappedEntityWithGeneratedIdentityTest(final Class<ENITTY> entityClass) {
        super(entityClass, Long.class);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Nonnull
    @Override
    protected SingleTypeEqualsVerifierApi<ENITTY> equals_Verify_(
            @Nonnull SingleTypeEqualsVerifierApi<ENITTY> equalsVerifier) {
        return super.equals_Verify_(equalsVerifier)
                .suppress(Warning.SURROGATE_KEY)
                ;
    }
}
