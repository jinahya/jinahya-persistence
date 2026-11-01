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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings({
        "java:S3577" // Test classes should comply with a naming convention
})
class NodeAttributeConverter_Test {

    @DisplayName("null <-> null")
    @Test
    void __null() {
        final var converter = new NodeAttributeConverter.OfString.OfNode();
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
        assertThat(converter.convertToEntityAttribute(null)).isNull();
    }

    @DisplayName("OfNode: a document -> a Document")
    @ParameterizedTest
    @ValueSource(strings = {"<a/>", "<a>b<c/></a>", "<!--x--><a/><?p d?>", "<a xmlns=\"urn:x\"><b/></a>"})
    void __node_document(final String xml) {
        final var converter = new NodeAttributeConverter.OfString.OfNode();
        final var node = converter.convertToEntityAttribute(xml);
        assertThat(node).isInstanceOf(Document.class);
        assertThat(converter.convertToDatabaseColumn(node)).isEqualTo(xml);
    }

    @DisplayName("OfNode: content which is not a document -> a DocumentFragment")
    @ParameterizedTest
    @ValueSource(strings = {"", "a", "<a/><b/>", "a<b/>c", "<!--x-->", "<![CDATA[<a/>]]>"})
    void __node_fragment(final String xml) {
        final var converter = new NodeAttributeConverter.OfString.OfNode();
        final var node = converter.convertToEntityAttribute(xml);
        assertThat(node).isInstanceOf(DocumentFragment.class);
        assertThat(node.isEqualNode(converter.convertToEntityAttribute(converter.convertToDatabaseColumn(node))))
                .isTrue();
    }

    @DisplayName("the XML declaration is read, and not written")
    @Test
    void __declaration() {
        final var converter = new NodeAttributeConverter.OfString.OfDocument();
        final var document = converter.convertToEntityAttribute(
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n<a/>");
        assertThat(converter.convertToDatabaseColumn(document)).isEqualTo("<a/>");
    }

    @DisplayName("<?xml-stylesheet ...?> is a processing instruction, not the XML declaration")
    @Test
    void __stylesheet() {
        final var converter = new NodeAttributeConverter.OfString.OfDocument();
        final var xml = "<?xml-stylesheet href=\"s.xsl\"?><a/>";
        assertThat(converter.convertToDatabaseColumn(converter.convertToEntityAttribute(xml))).isEqualTo(xml);
    }

    @DisplayName("OfDocument: the whitespace around the root is dropped")
    @Test
    void __document_whitespace() {
        final var converter = new NodeAttributeConverter.OfString.OfDocument();
        final var document = converter.convertToEntityAttribute("\n <a/>\n");
        assertThat(document.getDocumentElement().getTagName()).isEqualTo("a");
        assertThat(converter.convertToDatabaseColumn(document)).isEqualTo("<a/>");
    }

    @DisplayName("OfDocumentFragment: a document is content, too")
    @Test
    void __fragment_document() {
        final var converter = new NodeAttributeConverter.OfString.OfDocumentFragment();
        final var fragment = converter.convertToEntityAttribute("<a/>");
        assertThat(fragment.getChildNodes().getLength()).isOne();
        assertThat(converter.convertToDatabaseColumn(fragment)).isEqualTo("<a/>");
    }

    @DisplayName("OfDocumentFragment: several roots and text")
    @Test
    void __fragment() {
        final var converter = new NodeAttributeConverter.OfString.OfDocumentFragment();
        final var xml = "a<b/>c<d>e</d>";
        final var fragment = converter.convertToEntityAttribute(xml);
        assertThat(fragment.getChildNodes().getLength()).isEqualTo(4);
        assertThat(converter.convertToDatabaseColumn(fragment)).isEqualTo(xml);
    }

    @DisplayName("OfElement: the root element, without what is around it")
    @Test
    void __element() {
        final var converter = new NodeAttributeConverter.OfString.OfElement();
        final var element = converter.convertToEntityAttribute("<!--x--><a><b/></a>");
        assertThat(element.getTagName()).isEqualTo("a");
        assertThat(element.getOwnerDocument().getDocumentElement()).isSameAs(element);
        assertThat(converter.convertToDatabaseColumn(element)).isEqualTo("<a><b/></a>");
    }

    @DisplayName("not a document -> IllegalArgumentException, from OfDocument and OfElement")
    @ParameterizedTest
    @ValueSource(strings = {"", "a", "<a/><b/>", "a<b/>", "<!--x-->"})
    void _IllegalArgumentException_NotDocument(final String xml) {
        assertThatThrownBy(() -> new NodeAttributeConverter.OfString.OfDocument().convertToEntityAttribute(xml))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new NodeAttributeConverter.OfString.OfElement().convertToEntityAttribute(xml))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("not XML -> IllegalArgumentException")
    @ParameterizedTest
    @ValueSource(strings = {"<a>", "</a>", "<a></b>", "a</w><w>b", "<x:a/>", "&nope;"})
    void _IllegalArgumentException_NotXml(final String xml) {
        assertThatThrownBy(() -> new NodeAttributeConverter.OfString.OfNode().convertToEntityAttribute(xml))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("a DOCTYPE -> IllegalArgumentException")
    @Test
    void _IllegalArgumentException_Doctype() {
        final var xml = "<!DOCTYPE a [<!ENTITY e SYSTEM \"file:///etc/passwd\">]><a>&e;</a>";
        assertThatThrownBy(() -> new NodeAttributeConverter.OfString.OfDocument().convertToEntityAttribute(xml))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // -------------------------------------------------------------------------------------------------------- OfBytes

    private static byte[] bytes(final String string, final Charset charset) {
        return string.getBytes(charset);
    }

    @DisplayName("OfBytes: null <-> null")
    @Test
    void __bytes_null() {
        final var converter = new NodeAttributeConverter.OfBytes.OfNode();
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
        assertThat(converter.convertToEntityAttribute(null)).isNull();
    }

    @DisplayName("OfBytes: round-trips, in UTF-8")
    @ParameterizedTest
    @ValueSource(strings = {"<a>한</a>", "a<b/>c", ""})
    void __bytes_roundTrip(final String xml) {
        final var converter = new NodeAttributeConverter.OfBytes.OfNode();
        final var utf8 = bytes(xml, StandardCharsets.UTF_8);
        assertThat(converter.convertToDatabaseColumn(converter.convertToEntityAttribute(utf8))).isEqualTo(utf8);
    }

    @DisplayName("OfBytes: content in the encoding its XML declaration names is read, too")
    @Test
    void __bytes_declaredEncoding() throws Exception {
        final var converter = new NodeAttributeConverter.OfBytes.OfDocumentFragment();
        final var fragment = converter.convertToEntityAttribute(
                "<?xml version=\"1.0\" encoding=\"EUC-KR\"?><a>한</a><b/>".getBytes("EUC-KR"));
        assertThat(converter.convertToDatabaseColumn(fragment))
                .isEqualTo(bytes("<a>한</a><b/>", StandardCharsets.UTF_8));
    }

    @DisplayName("OfBytes: a document in UTF-16, with the BOM, is read, too")
    @Test
    void __bytes_utf16() {
        final var converter = new NodeAttributeConverter.OfBytes.OfDocument();
        final var document = converter.convertToEntityAttribute(bytes("<a>한</a>", StandardCharsets.UTF_16));
        assertThat(document.getDocumentElement().getTextContent()).isEqualTo("한");
    }

    @DisplayName("OfBytes: a UTF-8 BOM is read, and not written")
    @Test
    void __bytes_utf8Bom() {
        final var converter = new NodeAttributeConverter.OfBytes.OfDocument();
        final var document = converter.convertToEntityAttribute(bytes("﻿<a/>", StandardCharsets.UTF_8));
        assertThat(converter.convertToDatabaseColumn(document)).isEqualTo(bytes("<a/>", StandardCharsets.UTF_8));
    }

    @DisplayName("OfBytes: not a document -> IllegalArgumentException")
    @ParameterizedTest
    @ValueSource(strings = {"", "<a/><b/>", "<a>"})
    void _IllegalArgumentException_bytes_NotDocument(final String xml) {
        final var converter = new NodeAttributeConverter.OfBytes.OfDocument();
        assertThatThrownBy(() -> converter.convertToEntityAttribute(bytes(xml, StandardCharsets.UTF_8)))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
