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
import nl.jqno.equalsverifier.api.SingleTypeEqualsVerifierApi;
import org.junit.jupiter.api.Test;

@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119"  // Type parameter names should comply with a naming convention
})
public abstract class __MappedEntityWithGeneratedIdentityTest<ENTITY extends __MappedEntityWithGeneratedIdentity>
        extends __MappedEntityWithGeneratedIdTest<ENTITY, Long> {

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    /**
     * Creates a new instance for testing specified entity class.
     *
     * @param entityClass the entity class to test.
     */
    protected __MappedEntityWithGeneratedIdentityTest(final Class<ENTITY> entityClass) {
        super(entityClass, Long.class);
    }

    // ----------------------------------------------------------------------------------------------- equals / hashCode

    /**
     * {@inheritDoc}
     */
    @Test
    @Override
    protected void equals_Verify_() {
        super.equals_Verify_();
    }

    /**
     * {@inheritDoc}}
     *
     * @return {@inheritDoc}
     */
    @Nonnull
    @Override
    protected SingleTypeEqualsVerifierApi<ENTITY> createEqualsVerifier() {
        return super.createEqualsVerifier();
    }

    /**
     * {@inheritDoc}}}
     *
     * @param equalsVerifier {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Nonnull
    @Override
    protected SingleTypeEqualsVerifierApi<ENTITY> configureEqualsVerifier(
            @Nonnull SingleTypeEqualsVerifierApi<ENTITY> equalsVerifier) {
        return super.configureEqualsVerifier(equalsVerifier)
                ;
    }
}
