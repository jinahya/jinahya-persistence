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

import java.lang.invoke.MethodHandles;

/**
 * An abstract class for testing persistence unit of {@link __PersistenceProducerConstants#PERSISTENCE_UNIT_NAME_TEST_PU}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see __PersistenceUnitIT
 */
//@AddBeanClasses({
//        __PersistenceProducer.class
//})
//@ExtendWith(WeldJunit5AutoExtension.class)
@SuppressWarnings({
        "java:S100", // Method names should comply with a naming convention
        "java:S101", // Class names should comply with a naming convention
        "java:S6813" // Field dependency injection should be avoided
})
public abstract class __PersistenceUnitTest extends __PersistenceUnit_ {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected __PersistenceUnitTest() {
        super();
    }
}
