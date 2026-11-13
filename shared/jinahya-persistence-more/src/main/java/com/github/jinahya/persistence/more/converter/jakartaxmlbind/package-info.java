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
 * Attribute converters which store any type Jakarta XML Binding maps as XML text.
 *
 * <h2>Columns</h2>
 * {@link com.github.jinahya.persistence.more.converter.jakartaxmlbind.JaxbAttributeConverter} stores a value as the XML
 * text in a {@code String} column through its
 * {@link com.github.jinahya.persistence.more.converter.jakartaxmlbind.JaxbAttributeConverter.OfString OfString}, or as
 * the XML bytes in a {@code byte[]} column through its
 * {@link com.github.jinahya.persistence.more.converter.jakartaxmlbind.JaxbAttributeConverter.OfBytes OfBytes}; either
 * is subclassed once per attribute type, with the attribute class passed to its constructor.
 *
 * <h2>Binding</h2>
 * Everything Jakarta XML Binding decides is left to the subclass, through three methods, each with a default:
 * {@link com.github.jinahya.persistence.more.converter.jakartaxmlbind.JaxbAttributeConverter#context() context}, for
 * the classes the context knows, such as an {@code ObjectFactory};
 * {@link
 * com.github.jinahya.persistence.more.converter.jakartaxmlbind.JaxbAttributeConverter#write(jakarta.xml.bind.Marshaller,
 * java.lang.Object, javax.xml.transform.Result) write}, for the element written and the marshaller's properties, such
 * as the XML declaration and the encoding; and
 * {@link
 * com.github.jinahya.persistence.more.converter.jakartaxmlbind.JaxbAttributeConverter#read(jakarta.xml.bind.Unmarshaller,
 * javax.xml.transform.Source) read}, for how a value is read. By default a value is written as an
 * {@code @XmlRootElement} without the XML declaration, and read as the attribute class whatever its root element. The
 * converter keeps the rest: a new marshaller and unmarshaller for each conversion, the column as the result written to,
 * {@code null} for {@code null}, and a parser which rejects a DOCTYPE, so no entity but the predefined ones is
 * resolved.
 *
 * <h2>Dependencies</h2>
 * The API is not a dependency of this module at run time: it is declared {@code provided}, and the application which
 * uses a converter here brings it and an implementation of it &mdash; the GlassFish JAXB runtime
 * ({@code org.glassfish.jaxb:jaxb-runtime}), or whatever its container already ships. Jakarta XML Binding is not part
 * of the Jakarta EE 11 platform.
 *
 * <h2>Registration</h2>
 * As in the parent package: only a concrete converter carries {@link jakarta.persistence.Converter @Converter}, every
 * one of them declares {@code autoApply = false}, and naming it in {@link jakarta.persistence.Convert @Convert}, or in
 * {@code persistence.xml}, is what puts it to work.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
package com.github.jinahya.persistence.more.converter.jakartaxmlbind;
