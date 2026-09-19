/*-
 * #%L
 * jinahya-persistence-utils
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

/**
 * Utilities for working with the Jakarta Persistence metamodel.
 * <p>
 * These classes bridge the gap between the metamodel and plain reflection:
 * {@link com.github.jinahya.persistence.metamodel.__ManagedTypeUtils} and
 * {@link com.github.jinahya.persistence.metamodel.__EntityTypeUtils} resolve a
 * {@link jakarta.persistence.metamodel.ManagedType managedType} or an
 * {@link jakarta.persistence.metamodel.EntityType entityType} for a class, from a single
 * {@link jakarta.persistence.EntityManagerFactory entityManagerFactory} or from the first of several which knows it,
 * while {@link com.github.jinahya.persistence.metamodel.__AttributeUtils} reads and writes the value of an
 * {@link jakarta.persistence.metamodel.Attribute attribute}, whether it is mapped to a field or to a property.
 * <p>
 * The {@code find...} methods answer with an {@link java.util.Optional optional}; the {@code get...} methods throw.
 * Both exist because {@link jakarta.persistence.metamodel.Metamodel} itself only throws.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@org.jspecify.annotations.NullMarked
package com.github.jinahya.persistence.metamodel;
