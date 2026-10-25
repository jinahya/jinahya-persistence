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
/**
 * Attribute converters which store an attribute as JSON text in a {@code String} column.
 *
 * <h2>Two APIs</h2>
 * {@link com.github.jinahya.persistence.more.converter.json.JsonpStringAttributeConverter} stores a Jakarta JSON
 * Processing {@link jakarta.json.JsonValue} as it is, and its concrete converters, one per value type, are nested in
 * it.
 * {@link com.github.jinahya.persistence.more.converter.json.JsonbStringAttributeConverter} stores any type Jakarta
 * JSON Binding maps, and is subclassed once per attribute type.
 *
 * <h2>Dependencies</h2>
 * Neither API is a dependency of this module at run time: both are declared {@code provided}, and the application
 * which uses a converter here brings the API it uses and an implementation of it &mdash; Eclipse Parsson for JSON
 * Processing, Eclipse Yasson for JSON Binding, or whatever its container already ships.
 *
 * <h2>Registration</h2>
 * As in the parent package: only a concrete converter carries {@link jakarta.persistence.Converter @Converter}, every
 * one of them declares {@code autoApply = false}, and naming it in {@link jakarta.persistence.Convert @Convert}, or in
 * {@code persistence.xml}, is what puts it to work.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@org.jspecify.annotations.NullMarked
package com.github.jinahya.persistence.more.converter.json;
