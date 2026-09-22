/**
 * Interfaces and classes which complement Jakarta Persistence when creating a persistence unit.
 *
 * <h2>Enums with stable persisted values</h2>
 * {@link com.github.jinahya.persistence.more.__AttributeEnum} lets an enum constant declare the value actually written
 * to the database, so the persisted form survives renaming and reordering of constants, and
 * {@link com.github.jinahya.persistence.more.__AttributeEnumUtils} looks a constant up by its attribute value. The
 * converter which carries a value in either direction,
 * {@link com.github.jinahya.persistence.more.converter.__AttributeEnumConverter}, lives with the other converters.
 *
 * <h2>Entities which reference their own type</h2>
 * {@link com.github.jinahya.persistence.more.__SelfReferencing} is the view of an entity's position within a hierarchy
 * of its own type — its parent, and its depth from the root. The children of an instance are a set there;
 * {@link com.github.jinahya.persistence.more.__SelfReferencingOrdered} is the interface for hierarchies where they are
 * a sequence instead, and adds the ordinal among siblings. An entity which names its members on its own terms marks
 * them {@link com.github.jinahya.persistence.more.__SelfReferencingParent @__SelfReferencingParent} and
 * {@link com.github.jinahya.persistence.more.__SelfReferencingOrdinal @__SelfReferencingOrdinal}, and
 * {@link com.github.jinahya.persistence.more.__SelfReferencingUtils} reads them.
 *
 * <h2>Subpackages</h2>
 * {@link com.github.jinahya.persistence.more.converter} holds the attribute converters — the base for storing a value
 * as a {@code String}, a whole list in one delimited column, and the two ways of composing a converter rather than
 * writing one. {@link com.github.jinahya.persistence.more.colormodel} holds a mapped superclass per color model —
 * RGB, CMYK, HSL, HWB — over one normalized way of addressing whatever components a model has.
 * {@link com.github.jinahya.persistence.more.temporalinterval} holds the intervals on a temporal axis, and
 * {@link com.github.jinahya.persistence.more.orderedrange} the ranges over anything merely {@code Comparable}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@org.jspecify.annotations.NullMarked
package com.github.jinahya.persistence.more;

/*-
 * #%L
 * jinahya-persistence-more
 * %%
 * Copyright (C) 2025 - 2026 Jinahya, Inc.
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
