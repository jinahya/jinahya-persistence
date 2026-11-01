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
 * Attribute converters which store a DOM {@link org.w3c.dom.Node} as XML text.
 *
 * <h2>Documents and content</h2>
 * {@link com.github.jinahya.persistence.more.converter.orgw3cdom.NodeAttributeConverter} stores a node as the XML
 * text, without the XML declaration, in a {@code String} column through its
 * {@link com.github.jinahya.persistence.more.converter.orgw3cdom.NodeAttributeConverter.OfString OfString}, or in
 * UTF-8 in a {@code byte[]} column through its
 * {@link com.github.jinahya.persistence.more.converter.orgw3cdom.NodeAttributeConverter.OfBytes OfBytes}. An XML
 * column may hold a whole document or content, several roots and text among them; the converter does not know which
 * the column takes, and reads either: {@code OfNode} returns a {@link org.w3c.dom.Document} or a
 * {@link org.w3c.dom.DocumentFragment}, whichever the text is, {@code OfDocumentFragment} reads a document as content,
 * too, and {@code OfDocument} and {@code OfElement} take a document only. A DOCTYPE is not read.
 *
 * <h2>Dependencies</h2>
 * None: DOM, and the parser and the transformer the converters use, are the JDK's {@code java.xml}.
 *
 * <h2>Registration</h2>
 * As in the parent package: only a concrete converter carries {@link jakarta.persistence.Converter @Converter}, every
 * one of them declares {@code autoApply = false}, and naming it in {@link jakarta.persistence.Convert @Convert}, or in
 * {@code persistence.xml}, is what puts it to work.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
package com.github.jinahya.persistence.more.converter.orgw3cdom;
