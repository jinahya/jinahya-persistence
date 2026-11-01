package com.github.jinahya.persistence.more.converter.jakartaxmlbind;

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

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.JAXBIntrospector;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlValue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings({
        "java:S3577" // Test classes should comply with a naming convention
})
class JaxbAttributeConverter_Test {

    @XmlRootElement(name = "point")
    public static class Point {

        @XmlAttribute
        public int x;

        @XmlAttribute
        public int y;
    }

    @XmlRootElement(name = "label")
    public static class Label {

        @XmlValue
        public String value;
    }

    // not an @XmlRootElement; its element is declared in the ObjectFactory
    public static class Name {

        @XmlValue
        public String value;
    }

    // as xjc generates one
    @XmlRegistry
    public static class ObjectFactory {

        private static final QName NAME = new QName("name");

        @XmlElementDecl(name = "name")
        public JAXBElement<Name> createName(final Name value) {
            return new JAXBElement<>(NAME, Name.class, null, value);
        }
    }

    private static JAXBContext newContext(final Class<?>... classes) {
        try {
            return JAXBContext.newInstance(classes);
        } catch (final JAXBException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    static class PointConverter extends JaxbAttributeConverter.OfString<Point> {

        PointConverter() {
            super(Point.class);
        }
    }

    static class NameConverter extends JaxbAttributeConverter.OfString<Name> {

        private static final JAXBContext CONTEXT = newContext(ObjectFactory.class);

        NameConverter() {
            super(Name.class);
        }

        @Override
        protected JAXBContext context() {
            return CONTEXT;
        }

        @Override
        protected void write(final Marshaller marshaller, final Name attribute, final Result result)
                throws JAXBException {
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
            marshaller.marshal(new ObjectFactory().createName(attribute), result);
        }
    }

    // the default write, for a value which is not an @XmlRootElement
    static class NameWithoutElementConverter extends JaxbAttributeConverter.OfString<Name> {

        NameWithoutElementConverter() {
            super(Name.class);
        }
    }

    // keeps the XML declaration
    static class PointWithDeclarationConverter extends JaxbAttributeConverter.OfString<Point> {

        PointWithDeclarationConverter() {
            super(Point.class);
        }

        @Override
        protected void write(final Marshaller marshaller, final Point attribute, final Result result)
                throws JAXBException {
            marshaller.marshal(attribute, result);
        }
    }

    // a document whose class depends on its root element
    static class PointOrLabelConverter extends JaxbAttributeConverter.OfString<Object> {

        private static final JAXBContext CONTEXT = newContext(Point.class, Label.class);

        PointOrLabelConverter() {
            super(Object.class);
        }

        @Override
        protected JAXBContext context() {
            return CONTEXT;
        }

        @Override
        protected Object read(final Unmarshaller unmarshaller, final Source source) throws JAXBException {
            return JAXBIntrospector.getValue(unmarshaller.unmarshal(source));
        }
    }

    private static Point point(final int x, final int y) {
        final var point = new Point();
        point.x = x;
        point.y = y;
        return point;
    }

    private static Label label(final String value) {
        final var label = new Label();
        label.value = value;
        return label;
    }

    private static Name name(final String value) {
        final var name = new Name();
        name.value = value;
        return name;
    }

    @DisplayName("null <-> null")
    @Test
    void __null() {
        final var converter = new PointConverter();
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
        assertThat(converter.convertToEntityAttribute(null)).isNull();
    }

    @DisplayName("an @XmlRootElement round-trips, without the XML declaration")
    @Test
    void __rootElement() {
        final var converter = new PointConverter();
        final var column = converter.convertToDatabaseColumn(point(1, 2));
        assertThat(column).isEqualTo("<point x=\"1\" y=\"2\"/>");
        final var read = converter.convertToEntityAttribute(column);
        assertThat(read).isNotNull();
        assertThat(read.x).isEqualTo(1);
        assertThat(read.y).isEqualTo(2);
    }

    @DisplayName("the XML declaration and the whitespace around the root are read")
    @Test
    void __declaration() {
        final var read = new PointConverter().convertToEntityAttribute(
                "<?xml version=\"1.0\" encoding=\"UTF-16\"?>\n<point x=\"3\" y=\"4\"/>\n");
        assertThat(read.x).isEqualTo(3);
        assertThat(read.y).isEqualTo(4);
    }

    @DisplayName("an overridden write keeps the XML declaration")
    @Test
    void __writeDeclaration() {
        final var converter = new PointWithDeclarationConverter();
        final var column = converter.convertToDatabaseColumn(point(1, 2));
        assertThat(column).startsWith("<?xml ").endsWith("<point x=\"1\" y=\"2\"/>");
        assertThat(converter.convertToEntityAttribute(column).x).isEqualTo(1);
    }

    @DisplayName("not an @XmlRootElement round-trips in the element its ObjectFactory creates")
    @Test
    void __objectFactory() {
        final var converter = new NameConverter();
        final var column = converter.convertToDatabaseColumn(name("a & b"));
        assertThat(column).isEqualTo("<name>a &amp; b</name>");
        assertThat(converter.convertToEntityAttribute(column).value).isEqualTo("a & b");
    }

    @DisplayName("not an @XmlRootElement, with the default write -> IllegalArgumentException; read is fine")
    @Test
    void _IllegalArgumentException_NotRootElement() {
        final var converter = new NameWithoutElementConverter();
        assertThatThrownBy(() -> converter.convertToDatabaseColumn(name("a")))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(converter.convertToEntityAttribute("<whatever>a</whatever>").value).isEqualTo("a");
    }

    @DisplayName("an overridden context and read pick the class by the root element")
    @Test
    void __byRootElement() {
        final var converter = new PointOrLabelConverter();
        assertThat(converter.convertToEntityAttribute("<point x=\"5\" y=\"6\"/>"))
                .isInstanceOfSatisfying(Point.class, p -> {
                    assertThat(p.x).isEqualTo(5);
                    assertThat(p.y).isEqualTo(6);
                });
        assertThat(converter.convertToEntityAttribute("<label>n</label>"))
                .isInstanceOfSatisfying(Label.class, l -> assertThat(l.value).isEqualTo("n"));
        assertThat(converter.convertToDatabaseColumn(point(5, 6))).isEqualTo("<point x=\"5\" y=\"6\"/>");
        assertThat(converter.convertToDatabaseColumn(label("n"))).isEqualTo("<label>n</label>");
    }

    @DisplayName("not an XML document -> IllegalArgumentException")
    @Test
    void _IllegalArgumentException_NotXml() {
        final var converter = new PointConverter();
        assertThatThrownBy(() -> converter.convertToEntityAttribute("<point"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> converter.convertToEntityAttribute("<point/><point/>"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("a DOCTYPE -> IllegalArgumentException; no entity is resolved")
    @Test
    void _IllegalArgumentException_Doctype() {
        final var converter = new NameConverter();
        assertThatThrownBy(() -> converter.convertToEntityAttribute(
                "<!DOCTYPE name [<!ENTITY e SYSTEM \"file:///etc/passwd\">]><name>&e;</name>"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // -------------------------------------------------------------------------------------------------------- OfBytes

    static class PointBytesConverter extends JaxbAttributeConverter.OfBytes<Point> {

        PointBytesConverter() {
            super(Point.class);
        }
    }

    static class LabelBytesConverter extends JaxbAttributeConverter.OfBytes<Label> {

        LabelBytesConverter() {
            super(Label.class);
        }
    }

    // keeps the XML declaration, in UTF-16
    static class LabelUtf16BytesConverter extends JaxbAttributeConverter.OfBytes<Label> {

        LabelUtf16BytesConverter() {
            super(Label.class);
        }

        @Override
        protected void write(final Marshaller marshaller, final Label attribute, final Result result)
                throws JAXBException {
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-16");
            marshaller.marshal(attribute, result);
        }
    }

    @DisplayName("OfBytes: null <-> null")
    @Test
    void __bytes_null() {
        final var converter = new PointBytesConverter();
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
        assertThat(converter.convertToEntityAttribute(null)).isNull();
    }

    @DisplayName("OfBytes: the XML text is stored in UTF-8, without the XML declaration")
    @Test
    void __bytes_utf8() {
        final var converter = new LabelBytesConverter();
        final var column = converter.convertToDatabaseColumn(label("한글 é"));
        assertThat(column).isEqualTo("<label>한글 é</label>".getBytes(StandardCharsets.UTF_8));
        assertThat(converter.convertToEntityAttribute(column).value).isEqualTo("한글 é");
    }

    @DisplayName("OfBytes: the bytes are in the encoding the marshaller declares")
    @Test
    void __bytes_marshallerEncoding() {
        final var converter = new LabelUtf16BytesConverter();
        final var column = converter.convertToDatabaseColumn(label("한글"));
        final var text = new String(column, StandardCharsets.UTF_16);
        assertThat(text).startsWith("<?xml ").contains("UTF-16").endsWith("<label>한글</label>");
        assertThat(converter.convertToEntityAttribute(column).value).isEqualTo("한글");
    }

    @DisplayName("OfBytes: read in the encoding the bytes declare")
    @Test
    void __bytes_declaredEncoding() {
        final var converter = new LabelBytesConverter();
        assertThat(converter.convertToEntityAttribute(
                "<?xml version=\"1.0\" encoding=\"UTF-16\"?><label>한글</label>".getBytes(StandardCharsets.UTF_16))
                           .value).isEqualTo("한글");
        assertThat(converter.convertToEntityAttribute(
                "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><label>é</label>".getBytes(StandardCharsets.ISO_8859_1))
                           .value).isEqualTo("é");
    }

    @DisplayName("OfBytes: an @XmlRootElement round-trips")
    @Test
    void __bytes_rootElement() {
        final var converter = new PointBytesConverter();
        final var column = converter.convertToDatabaseColumn(point(7, 8));
        assertThat(column).isEqualTo("<point x=\"7\" y=\"8\"/>".getBytes(StandardCharsets.UTF_8));
        final var read = converter.convertToEntityAttribute(column);
        assertThat(read.x).isEqualTo(7);
        assertThat(read.y).isEqualTo(8);
    }

    @DisplayName("OfBytes: not an XML document -> IllegalArgumentException")
    @Test
    void _IllegalArgumentException_bytes_NotXml() {
        final var converter = new PointBytesConverter();
        assertThatThrownBy(() -> converter.convertToEntityAttribute("<point".getBytes(StandardCharsets.UTF_8)))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
