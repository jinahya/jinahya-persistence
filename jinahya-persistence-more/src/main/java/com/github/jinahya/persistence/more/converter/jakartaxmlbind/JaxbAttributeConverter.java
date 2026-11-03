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

import jakarta.persistence.AttributeConverter;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Base64;
import java.util.Objects;

/**
 * An attribute converter which stores a value as XML through Jakarta XML Binding.
 * <p>
 * A concrete converter extends {@link OfString} or {@link OfBytes}, by the column, passes the attribute class to the
 * constructor, and overrides whichever of {@link #context()}, {@link #write(Marshaller, Object, Result)} and
 * {@link #read(Unmarshaller, Source)} its binding needs. The converter itself creates a marshaller and an unmarshaller
 * for each conversion, parses the column with a DOCTYPE rejected, and converts {@code null} to {@code null}.
 *
 * @param <X> the attribute type
 * @param <Y> the column type
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public abstract class JaxbAttributeConverter<X, Y>
        implements AttributeConverter<X, Y> {

    /**
     * Stores a value as the XML text.
     *
     * @param <X> the attribute type
     */
    public abstract static class OfString<X>
            extends JaxbAttributeConverter<X, String> {

        /**
         * Creates a new instance for the specified attribute class.
         *
         * @param attributeClass the attribute class.
         */
        protected OfString(final Class<X> attributeClass) {
            super(attributeClass);
        }

        @Override
        public String convertToDatabaseColumn(final X attribute) {
            if (attribute == null) {
                return null;
            }
            final var writer = new StringWriter();
            super.marshal(attribute, new StreamResult(writer));
            return writer.toString();
        }

        @Override
        public X convertToEntityAttribute(final String dbData) {
            if (dbData == null) {
                return null;
            }
            return super.unmarshal(new InputSource(new StringReader(dbData)), dbData);
        }
    }

    /**
     * Stores a value as the XML bytes, in the encoding the marshaller writes them in; read in whichever encoding the
     * bytes are in.
     *
     * @param <X> the attribute type
     */
    public abstract static class OfBytes<X>
            extends JaxbAttributeConverter<X, byte[]> {

        /**
         * Creates a new instance for the specified attribute class.
         *
         * @param attributeClass the attribute class.
         */
        protected OfBytes(final Class<X> attributeClass) {
            super(attributeClass);
        }

        @Override
        public byte[] convertToDatabaseColumn(final X attribute) {
            if (attribute == null) {
                return null;
            }
            final var output = new ByteArrayOutputStream();
            super.marshal(attribute, new StreamResult(output));
            return output.toByteArray();
        }

        @Override
        public X convertToEntityAttribute(final byte[] dbData) {
            if (dbData == null) {
                return null;
            }
            return super.unmarshal(new InputSource(new ByteArrayInputStream(dbData)), dbData.length + " byte(s)");
        }
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    private JaxbAttributeConverter(final Class<X> attributeClass) {
        super();
        this.attributeClass = Objects.requireNonNull(attributeClass, "attributeClass is null");
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns the context which marshals and unmarshals values.
     * <p>
     * The default implementation returns a context for the attribute class alone, created once per class. Override it
     * for a context of more classes, such as one with an {@code ObjectFactory}; the context is called for on each
     * conversion, so an override should return a context it keeps.
     *
     * @return the context.
     * @throws JAXBException if failed to create the context.
     */
    protected JAXBContext context() throws JAXBException {
        return CONTEXTS.get(attributeClass);
    }

    /**
     * Writes the specified value to the specified result with the specified marshaller.
     * <p>
     * The default implementation sets {@link Marshaller#JAXB_FRAGMENT} to {@code true}, so that the XML declaration is
     * left out, and marshals the value as itself, which works for an {@code @XmlRootElement}. Override it to set other
     * properties, or to marshal an element of the value, such as one an {@code ObjectFactory} creates.
     *
     * @param marshaller a new marshaller of the {@link #context() context}.
     * @param attribute  the value; never {@code null}.
     * @param result     the result to write to.
     * @throws JAXBException if failed to marshal.
     */
    protected void write(final Marshaller marshaller, final X attribute, final Result result) throws JAXBException {
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
        marshaller.marshal(attribute, result);
    }

    /**
     * Reads a value from the specified source with the specified unmarshaller.
     * <p>
     * The default implementation unmarshals the source as the attribute class, whatever its root element's name.
     * Override it to set the unmarshaller up, or to unmarshal a value whose class depends on the document.
     *
     * @param unmarshaller a new unmarshaller of the {@link #context() context}.
     * @param source       the source to read from; its parser rejects a DOCTYPE.
     * @return the value.
     * @throws JAXBException if failed to unmarshal.
     */
    protected X read(final Unmarshaller unmarshaller, final Source source) throws JAXBException {
        return unmarshaller.unmarshal(source, attributeClass).getValue();
    }

    // -----------------------------------------------------------------------------------------------------------------
    private void marshal(final X attribute, final Result result) {
        try {
            write(context().createMarshaller(), attribute, result);
        } catch (final JAXBException e) {
            throw new IllegalArgumentException("failed to marshal " + attribute, e);
        }
    }

    // what is a description of the column, for a message
    private X unmarshal(final InputSource input, final String what) {
        try {
            return read(context().createUnmarshaller(), new SAXSource(reader(), input));
        } catch (final JAXBException e) {
            throw new IllegalArgumentException("failed to unmarshal " + what, e);
        }
    }

    // a new reader for each conversion: neither a factory nor a reader is thread-safe
    private static XMLReader reader() {
        final var factory = SAXParserFactory.newDefaultInstance();
        factory.setNamespaceAware(true);
        factory.setXIncludeAware(false);
        try {
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            // a DOCTYPE is rejected, and with it every entity but the predefined ones
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            return factory.newSAXParser().getXMLReader();
        } catch (final ParserConfigurationException | SAXException e) {
            throw new IllegalStateException("failed to configure a reader", e);
        }
    }

    // a context is thread-safe and costly to create; one per attribute class, created when the default context() is
    // first called, so that a converter which overrides it never creates one
    private static final ClassValue<JAXBContext> CONTEXTS = new ClassValue<>() {
        @Override
        protected JAXBContext computeValue(final Class<?> type) {
            try {
                return JAXBContext.newInstance(type);
            } catch (final JAXBException e) {
                throw new IllegalStateException("failed to create a context for " + type, e);
            }
        }
    };

    // -----------------------------------------------------------------------------------------------------------------
    private final Class<X> attributeClass;
}
