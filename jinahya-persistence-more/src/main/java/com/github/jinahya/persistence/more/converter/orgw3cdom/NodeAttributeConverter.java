package com.github.jinahya.persistence.more.converter.orgw3cdom;

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

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.regex.Pattern;

// a mutable Node with the identity equals: replace the attribute, don't modify it in place
// whatever the column stores, a document or content, with or without the XML declaration, is read; a DOCTYPE is not
public abstract class NodeAttributeConverter<X extends Node, Y>
        implements AttributeConverter<X, Y> {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    // stored as the XML text, without the XML declaration
    public abstract static class OfString<X extends Node>
            extends NodeAttributeConverter<X, String> {

        // a Document if the text is a document, a DocumentFragment otherwise
        @Converter(autoApply = false)
        public static class OfNode
                extends OfString<Node> {

            public OfNode() {
                super(Node.class);
            }
        }

        @Converter(autoApply = false)
        public static class OfDocument
                extends OfString<Document> {

            public OfDocument() {
                super(Document.class);
            }
        }

        @Converter(autoApply = false)
        public static class OfDocumentFragment
                extends OfString<DocumentFragment> {

            public OfDocumentFragment() {
                super(DocumentFragment.class);
            }
        }

        // the root element of a document; whatever is around it is not kept
        @Converter(autoApply = false)
        public static class OfElement
                extends OfString<Element> {

            public OfElement() {
                super(Element.class);
            }
        }

        private OfString(final Class<X> attributeClass) {
            super(attributeClass);
        }

        @Override
        public String convertToDatabaseColumn(final X attribute) {
            if (attribute == null) {
                return null;
            }
            return write(attribute);
        }

        @Override
        public X convertToEntityAttribute(final String dbData) {
            if (dbData == null) {
                return null;
            }
            return super.read(dbData);
        }
    }

    // stored as the XML text in UTF-8, without the XML declaration; read in whichever encoding the bytes are in
    public abstract static class OfBytes<X extends Node>
            extends NodeAttributeConverter<X, byte[]> {

        // a Document if the text is a document, a DocumentFragment otherwise
        @Converter(autoApply = false)
        public static class OfNode
                extends OfBytes<Node> {

            public OfNode() {
                super(Node.class);
            }
        }

        @Converter(autoApply = false)
        public static class OfDocument
                extends OfBytes<Document> {

            public OfDocument() {
                super(Document.class);
            }
        }

        @Converter(autoApply = false)
        public static class OfDocumentFragment
                extends OfBytes<DocumentFragment> {

            public OfDocumentFragment() {
                super(DocumentFragment.class);
            }
        }

        // the root element of a document; whatever is around it is not kept
        @Converter(autoApply = false)
        public static class OfElement
                extends OfBytes<Element> {

            public OfElement() {
                super(Element.class);
            }
        }

        private OfBytes(final Class<X> attributeClass) {
            super(attributeClass);
        }

        @Override
        public byte[] convertToDatabaseColumn(final X attribute) {
            if (attribute == null) {
                return null;
            }
            return write(attribute).getBytes(StandardCharsets.UTF_8);
        }

        @Override
        public X convertToEntityAttribute(final byte[] dbData) {
            if (dbData == null) {
                return null;
            }
            return super.read(decode(dbData));
        }
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    private NodeAttributeConverter(final Class<X> attributeClass) {
        super();
        this.attributeClass = Objects.requireNonNull(attributeClass, "attributeClass is null");
    }

    // -----------------------------------------------------------------------------------------------------------------

    // a leading BOM and the XML declaration; "\s" keeps <?xml-stylesheet ...?>, a processing instruction, out
    private static final Pattern DECLARATION = Pattern.compile("^﻿?(?:<\\?xml\\s[^>]*\\?>)?");

    // the name of the element the text is wrapped in, so that content, which a parser does not take, is a document
    private static final String WRAPPER = "w";

    // the text -> a Document or a DocumentFragment, whichever it is; then fitted to the attributeClass
    private X read(final String text) {
        final Document document;
        try {
            document = builder().parse(new InputSource(new StringReader(
                    '<' + WRAPPER + '>' + DECLARATION.matcher(text).replaceFirst("") + "</" + WRAPPER + '>')));
        } catch (final SAXException | IOException e) {
            throw new IllegalArgumentException("not an XML document or content: " + text, e);
        }
        final var wrapper = document.getDocumentElement();
        if (attributeClass == DocumentFragment.class || (attributeClass == Node.class && !isDocument(wrapper))) {
            final var fragment = document.createDocumentFragment();
            for (Node child; (child = wrapper.getFirstChild()) != null; ) {
                fragment.appendChild(child);
            }
            return attributeClass.cast(fragment);
        }
        if (!isDocument(wrapper)) {
            throw new IllegalArgumentException("not an XML document: " + text);
        }
        // the wrapper's children become the document's own; the whitespace around the root, which a document does
        // not hold as a node, is dropped
        document.removeChild(wrapper);
        for (Node child; (child = wrapper.getFirstChild()) != null; ) {
            wrapper.removeChild(child);
            if (child.getNodeType() != Node.TEXT_NODE) {
                document.appendChild(child);
            }
        }
        if (attributeClass == Element.class) {
            return attributeClass.cast(document.getDocumentElement());
        }
        return attributeClass.cast(document);
    }

    // whether the wrapped text is a document: exactly one element, and only comments, processing instructions and
    // whitespace around it
    private static boolean isDocument(final Element wrapper) {
        var elements = 0;
        for (var child = wrapper.getFirstChild(); child != null; child = child.getNextSibling()) {
            switch (child.getNodeType()) {
                case Node.ELEMENT_NODE -> elements++;
                case Node.COMMENT_NODE, Node.PROCESSING_INSTRUCTION_NODE -> {
                }
                case Node.TEXT_NODE -> {
                    if (!child.getNodeValue().chars().allMatch(c -> c == ' ' || c == '\t' || c == '\r' || c == '\n')) {
                        return false;
                    }
                }
                default -> {
                    return false;
                }
            }
        }
        return elements == 1;
    }

    // a new builder for each read: neither a factory nor a builder is thread-safe
    private static DocumentBuilder builder() {
        final var factory = DocumentBuilderFactory.newDefaultInstance();
        factory.setNamespaceAware(true);
        factory.setXIncludeAware(false);
        factory.setExpandEntityReferences(false);
        try {
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            // a DOCTYPE is rejected, and with it every entity but the predefined ones
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            final var builder = factory.newDocumentBuilder();
            builder.setErrorHandler(THROWING);
            return builder;
        } catch (final ParserConfigurationException e) {
            throw new IllegalStateException("failed to configure a document builder", e);
        }
    }

    // the default one prints every error to System.err before it is thrown
    private static final ErrorHandler THROWING = new ErrorHandler() {

        @Override
        public void warning(final SAXParseException exception) {
        }

        @Override
        public void error(final SAXParseException exception) throws SAXException {
            throw exception;
        }

        @Override
        public void fatalError(final SAXParseException exception) throws SAXException {
            throw exception;
        }
    };

    // the node -> the XML text, without the XML declaration
    private static String write(final Node node) {
        final var factory = TransformerFactory.newDefaultInstance();
        try {
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            final var transformer = factory.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            final var writer = new StringWriter();
            transformer.transform(new DOMSource(node), new StreamResult(writer));
            return writer.toString();
        } catch (final TransformerConfigurationException e) {
            throw new IllegalStateException("failed to configure a transformer", e);
        } catch (final TransformerException e) {
            throw new IllegalArgumentException("failed to write " + node, e);
        }
    }

    // the bytes -> the text, in the encoding the XML parser detects from the BOM and the XML declaration; read up to the
    // start of the document only, so that content, with more than one root, is decoded, too
    private static String decode(final byte[] dbData) {
        final var factory = XMLInputFactory.newDefaultFactory();
        factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
        final String encoding;
        try {
            final var reader = factory.createXMLStreamReader(new ByteArrayInputStream(dbData));
            try {
                encoding = reader.getEncoding();
            } finally {
                reader.close();
            }
        } catch (final XMLStreamException e) {
            throw new IllegalArgumentException("not an XML document or content: " + dbData.length + " byte(s)", e);
        }
        try {
            return new String(dbData, encoding == null ? StandardCharsets.UTF_8.name() : encoding);
        } catch (final UnsupportedEncodingException e) {
            throw new IllegalArgumentException("unsupported encoding: " + encoding, e);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    private final Class<X> attributeClass;
}
