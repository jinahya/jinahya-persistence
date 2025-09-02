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

@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119"  // Type parameter names should comply with a naming convention
})
public abstract class __MappedEntityWithGeneratedIdentity_Test<ENTITY extends __MappedEntityWithGeneratedIdentity>
        extends __MappedEntityWithGeneratedId_Test<ENTITY, Long> {

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance for testing specified entity class.
     *
     * @param entityClass the entity class to test.
     */
    protected __MappedEntityWithGeneratedIdentity_Test(final Class<ENTITY> entityClass) {
        super(entityClass, Long.class);
    }

    // -------------------------------------------------------------------------------------------------------- toString
    @Override
    protected void toString_NotBlank_(@Nonnull final ENTITY entityInstance) {
        super.toString_NotBlank_(entityInstance);
    }

    //    @Test
    @Override
    protected void toString_NotBlank_newEntityInstance() {
        super.toString_NotBlank_newEntityInstance();
    }

    //    @Test
    @Override
    protected void toString_NotBlank_newRandomizedEntityInstance() {
        super.toString_NotBlank_newRandomizedEntityInstance();
    }

    // ------------------------------------------------------------------------------------------------- equals/hashCode
//    @Test
    @Override
    protected void equals_Verify_() {
        super.equals_Verify_();
    }

    @Nonnull
    @Override
    protected SingleTypeEqualsVerifierApi<ENTITY> createEqualsVerifier() {
        return super.createEqualsVerifier();
    }

    @Nonnull
    @Override
    protected SingleTypeEqualsVerifierApi<ENTITY> configureEqualsVerifier(
            @Nonnull SingleTypeEqualsVerifierApi<ENTITY> equalsVerifier) {
        return super.configureEqualsVerifier(equalsVerifier)
                ;
    }

    // ------------------------------------------------------------------------------------------------------- accessors
    @Override
    protected void accessors_DoesNotThrow_(@Nonnull ENTITY entityInstance) {
        super.accessors_DoesNotThrow_(entityInstance);
    }

    //    @Test
    @Override
    protected void accessors_DoesNotThrow_newEntityInstance() {
        super.accessors_DoesNotThrow_newEntityInstance();
    }

    //    @Test
    @Override
    protected void accessors_DoesNotThrow_newRandomizedEntityInstance() {
        super.accessors_DoesNotThrow_newRandomizedEntityInstance();
    }
}
