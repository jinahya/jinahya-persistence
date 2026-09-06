/**
 * Interfaces and classes which complement Jakarta Persistence when creating a persistence unit.
 * <p>
 * Two themes live here.
 *
 * <h2>Attribute converters</h2>
 * {@link com.github.jinahya.persistence.more.__StringAttributeConverter} is the base for storing a value as a
 * {@code String}, and carries ready-made converters for the {@link java.lang.Number} types.
 * {@link com.github.jinahya.persistence.more.__JoinedStringAttributeConverter} stores a whole list in one delimited
 * column. Converters can be composed rather than written from scratch:
 * {@link com.github.jinahya.persistence.more.__ChainingAttributeConverter} builds one converter out of two, through an
 * intermediate type, and {@link com.github.jinahya.persistence.more.__AttributeConverterUtils} builds one out of a pair
 * of functions.
 *
 * <h2>Enums with stable persisted values</h2>
 * {@link com.github.jinahya.persistence.more.__AttributeEnum} lets an enum constant declare the value actually written
 * to the database, so the persisted form survives renaming and reordering of constants;
 * {@link com.github.jinahya.persistence.more.__AttributeEnumConverter} is the converter for it, and
 * {@link com.github.jinahya.persistence.more.__AttributeEnumUtils} looks a constant up by its attribute value.
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
