package com.github.jinahya.persistence.encryption.eclipselink;

import com.github.jinahya.persistence.encryption.converter.EncryptedConverters;
import jakarta.persistence.AttributeConverter;
import org.eclipse.persistence.descriptors.VersionLockingPolicy;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.mappings.foundation.AbstractDirectMapping;
import org.eclipse.persistence.sessions.Session;

/**
 * EclipseLink's own way into the generic module: one named converter for every supported type, which picks the codec
 * from the mapped attribute's declared java type when the mapping is initialized, and makes the column binary.
 * <pre>{@code
 * @org.eclipse.persistence.annotations.Converter(name = "encrypted", converterClass = EncryptedConverter.class)
 * @Entity
 * class User {
 *
 *     @org.eclipse.persistence.annotations.Convert("encrypted")
 *     @Column(name = "name_enc")       // a binary column, whatever the attribute's type
 *     private String name;
 * }
 * }</pre>
 * The name is registered once, on any entity of the persistence unit, and used on each attribute to encrypt. Nothing
 * has to be listed in the persistence unit, and the DDL EclipseLink generates is binary, as the field's type is set
 * here. The service is the one the persistence unit property {@value EncryptedConverters#PROPERTY_SERVICE} names: an
 * instance, its class, or its class name; without the property, the one {@code META-INF/services/} names.
 * <p>
 * The rows, and the bytes, are those of the generic module's converters: a value stored through one reads through the
 * other. EclipseLink's shared cache holds the attribute's value, the plaintext, as for any attribute; leave encrypted
 * entities uncached where that matters.
 * <p>
 * The column is the platform's type for {@code byte[]}, {@code LONGVARBINARY} on H2 for one, and a length declared on
 * the column is not applied to it. An identifier or a version attribute is refused when the mapping is initialized: a
 * randomized ciphertext can be neither, as the column never compares equal to what is bound.
 * <p>
 * <strong>Mutable values.</strong> This converter reports itself immutable, so an attribute of an array or a
 * {@code java.util.Date} type is, unless the mapping says otherwise with EclipseLink's
 * {@link org.eclipse.persistence.annotations.Mutable @Mutable}, treated as a value: an in-place change is not
 * detected, and one instance is shared between the shared cache and every persistence context. Declare
 * {@code @Mutable} on a {@code Date} attribute to get copies and in-place change detection. On an array attribute,
 * {@code @Mutable} also makes every flush of the entity write it again: EclipseLink compares a mutable attribute with
 * its backup copy by {@code equals}, which for an array is identity, and then by converting both sides, and a
 * randomized ciphertext never compares equal. So for an array, assign a new array instead, and leave the entity out of
 * the shared cache where sharing an instance matters.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public class EncryptedConverter implements Converter {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance; EclipseLink instantiates a converter named by class through this constructor.
     */
    public EncryptedConverter() {
        super();
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * @throws IllegalStateException    when the mapping is not a direct mapping of a supported type, or is a primary
     *                                  key or the version, or no service can be loaded.
     * @throws IllegalArgumentException when the service property names no service.
     */
    @Override
    @SuppressWarnings({"unchecked"})
    public void initialize(final DatabaseMapping mapping, final Session session) {
        if (!(mapping instanceof AbstractDirectMapping direct)) {
            throw new IllegalStateException(
                    "the encrypted converter applies to a direct mapping only; found " + mapping.getClass().getName()
                    + " for " + mapping.getDescriptor().getJavaClassName() + '.' + mapping.getAttributeName());
        }
        final var descriptor = mapping.getDescriptor();
        final var field = direct.getField();
        if (descriptor.getPrimaryKeyFields().stream().anyMatch(pk -> sameField(pk, field))) {
            throw new IllegalStateException(
                    "the encrypted converter on a primary key, which a randomized ciphertext cannot be: "
                    + descriptor.getJavaClassName() + '.' + mapping.getAttributeName());
        }
        if (descriptor.getOptimisticLockingPolicy() instanceof VersionLockingPolicy locking
            && sameField(locking.getWriteLockField(), field)) {
            throw new IllegalStateException(
                    "the encrypted converter on the version, which a randomized ciphertext cannot be: "
                    + descriptor.getJavaClassName() + '.' + mapping.getAttributeName());
        }
        var type = direct.getAttributeClassification();
        if (type == null) {
            type = direct.getAttributeAccessor().getAttributeClass();
        }
        if (type == null || !EncryptedConverters.supports(type)) {
            throw new IllegalStateException(
                    "the encrypted converter supports no attribute of " + type + ": "
                    + mapping.getDescriptor().getJavaClassName() + '.' + mapping.getAttributeName());
        }
        final var service = EncryptedConverters.serviceOf(session.getProperty(EncryptedConverters.PROPERTY_SERVICE));
        this.delegate = (AttributeConverter<Object, byte[]>) EncryptedConverters.forType(type, service);
        // the column holds the ciphertext, whatever the attribute's type: the DDL, and the JDBC binding, follow this
        direct.setFieldClassification(byte[].class);
        // the mapping's mutability is left as the application declared it (@Mutable), see the class note
    }

    @Override
    public Object convertObjectValueToDataValue(
            final Object objectValue, final Session session) {
        return objectValue == null ? null : delegate().convertToDatabaseColumn(objectValue);
    }

    @Override
    public Object convertDataValueToObjectValue(
            final Object dataValue, final Session session) {
        if (dataValue == null) {
            return null;
        }
        if (!(dataValue instanceof byte[] bytes)) {
            throw new IllegalStateException(
                    "the column holds " + dataValue.getClass().getName()
                    + ", not bytes; an encrypted column is binary");
        }
        return delegate().convertToEntityAttribute(bytes);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@code false}, always: the mapping's own {@code @Mutable} decides, see the class note.
     */
    @Override
    public boolean isMutable() {
        return false;
    }

    // the fields may not be qualified with their table yet, when the mapping is initialized
    private static boolean sameField(final DatabaseField a,
                                     final DatabaseField b) {
        if (a == null || b == null) {
            return false;
        }
        return a.equals(b) || a.getName().equalsIgnoreCase(b.getName());
    }

    private AttributeConverter<Object, byte[]> delegate() {
        final var result = delegate;
        if (result == null) {
            throw new IllegalStateException("not initialized: the converter is used before its mapping is");
        }
        return result;
    }

    // -----------------------------------------------------------------------------------------------------------------
    // set when the mapping is initialized, by one thread; read by every conversion, on any thread
    private transient volatile AttributeConverter<Object, byte[]> delegate;
}
