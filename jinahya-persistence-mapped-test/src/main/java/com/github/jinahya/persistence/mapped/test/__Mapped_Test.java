package com.github.jinahya.persistence.mapped.test;

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

import com.github.jinahya.persistence.mapped.__Mapped;
import jakarta.annotation.Nonnull;

/**
 * An abstract class for testing a specific subclass of {@link __Mapped} class.
 *
 * @param <MAPPED> target type parameter
 * @see __Mapped_Randomizer
 */
@SuppressWarnings({
        "java:S100", // Method names should comply with a naming convention
        "java:S101", // Class names should comply with a naming convention
        "java:S119", // Type parameter names should comply with a naming convention
        "java:S5960" // Assertions should not be used in production code
})
public abstract class __Mapped_Test<MAPPED extends __Mapped> extends ____Test<MAPPED> {

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance to test the specified mapped class.
     *
     * @param mappedClass the mapped class to test.
     * @see #targetClass
     */
    protected __Mapped_Test(final @Nonnull Class<MAPPED> mappedClass) {
        super(mappedClass);
    }
}
