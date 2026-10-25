package com.github.jinahya.persistence.encryption.hibernate;

import com.github.jinahya.persistence.encryption.converter.EncryptedConverters;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import org.hibernate.MappingException;
import org.hibernate.annotations.NaturalId;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.usertype.UserType;
import org.hibernate.usertype.UserTypeCreationContext;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.invoke.MethodType;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * The Hibernate ORM type behind {@link Encrypted @Encrypted}: the attribute's value in the entity, its ciphertext in a
 * {@code VARBINARY} column, and the conversion between them the generic module's converter for the attribute's
 * declared type.
 * <p>
 * Hibernate ORM creates one instance for each annotated attribute, through the constructor taking the annotation and a
 * {@link UserTypeCreationContext}, from which the declared type and the service come. The service is the one the
 * persistence unit property {@value EncryptedConverters#PROPERTY_SERVICE} names: an instance, its class, or its class
 * name; without the property, the one {@code META-INF/services/} names.
 * <p>
 * What this type adds to the standard converter:
 * <ul>
 *   <li><strong>the second-level cache holds the ciphertext.</strong> The form an entity is cached in is what
 *       {@link #disassemble(Object)} returns, which here is the encrypted bytes;
 *       {@link #assemble(Serializable, Object)} decrypts on a cache hit. A cache put costs an encryption, so a cached
 *       entry's bytes need not equal the row's; both decrypt to the value;</li>
 *   <li>the column type is decided here, {@link Types#VARBINARY}, whatever the attribute's type, and the length the
 *       column declares is applied to it, a temporal attribute's included, where the standard mapping loses a
 *       temporal attribute's. A column which declares no length gets Hibernate ORM's default of {@code 255}, as any
 *       column does: declare one, as a ciphertext is as long as the service's frame makes it;</li>
 *   <li>a mutable value, an array or a {@code java.util.Date}, is copied and compared by content;</li>
 *   <li>an identifier, a version or a natural id is refused: a randomized ciphertext can be none of them, as the
 *       column never compares equal to what is bound;</li>
 *   <li>a literal in a query goes through the converter too. The type exposes the conversion as its
 *       {@linkplain #getValueConverter() value converter}, so Hibernate ORM binds, extracts and renders through it:
 *       a bulk {@code UPDATE ... SET e.name = 'literal'} stores the literal's ciphertext, where a type without the
 *       converter would have rendered the literal's own bytes into the column.</li>
 * </ul>
 * The rows, and the bytes, are those of the generic module's converters: a value stored through one reads through the
 * other.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see Encrypted
 */
public class EncryptedUserType implements UserType<Object> {

    /**
     * Creates a new instance for the attribute the specified annotation sits on.
     *
     * @param annotation the annotation.
     * @param context    the context: the member annotated, and the service registry the settings come from.
     * @throws MappingException when the attribute is an identifier, a version or a natural id, or its declared type
     *                          has no converter, or the service cannot be had.
     */
    @SuppressWarnings({"unchecked"})
    public EncryptedUserType(final Encrypted annotation, final UserTypeCreationContext context) {
        super();
        Objects.requireNonNull(annotation, "annotation is null");
        Objects.requireNonNull(context, "context is null");
        final var member = context.getMemberDetails().toJavaMember();
        final Class<?> declared;
        if (member instanceof Field field) {
            declared = field.getType();
        } else if (member instanceof Method method) {
            declared = method.getReturnType();
        } else {
            throw new MappingException("@Encrypted on neither a field nor a getter: " + member);
        }
        final var details = context.getMemberDetails();
        for (final var refused : REFUSED) {
            if (details.hasDirectAnnotationUsage(refused)) {
                throw new MappingException(
                        "@Encrypted on a @" + refused.getSimpleName() + " attribute, which a randomized ciphertext"
                        + " cannot be: " + member.getDeclaringClass().getName() + '.' + member.getName());
            }
        }
        if (!EncryptedConverters.supports(declared)) {
            throw new MappingException(
                    "@Encrypted on an attribute of " + declared.getName() + ", which has no encrypted converter: "
                    + member.getDeclaringClass().getName() + '.' + member.getName());
        }
        this.returnedClass = (Class<Object>) (declared.isPrimitive()
                                              ? MethodType.methodType(declared).wrap().returnType() : declared);
        final var configuration = context.getServiceRegistry().getService(ConfigurationService.class);
        final var value = configuration == null ? null
                : configuration.getSettings().get(EncryptedConverters.PROPERTY_SERVICE);
        try {
            this.converter = EncryptedConverters.forType(returnedClass, EncryptedConverters.serviceOf(value));
        } catch (final IllegalArgumentException | IllegalStateException e) {
            throw new MappingException("no encryption service for @Encrypted " + member.getDeclaringClass().getName()
                                       + '.' + member.getName() + ": " + e.getMessage(), e);
        }
        this.mutable = returnedClass.isArray() || java.util.Date.class.isAssignableFrom(returnedClass);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public int getSqlType() {
        return Types.VARBINARY;
    }

    @Override
    public AttributeConverter<Object, byte[]> getValueConverter() {
        // with a converter, Hibernate ORM binds, extracts and renders literals through it, not through nullSafeSet
        return converter;
    }

    @Override
    public Class<Object> returnedClass() {
        return returnedClass;
    }

    @Override
    public boolean equals(final Object x, final Object y) {
        return Objects.deepEquals(x, y);
    }

    @Override
    public int hashCode(final Object x) {
        return x.getClass().isArray() ? Arrays.deepHashCode(new Object[] {x}) : x.hashCode();
    }

    @Override
    public Object nullSafeGet(final ResultSet rs, final int position,
                                                            final WrapperOptions options) throws SQLException {
        final var bytes = rs.getBytes(position);
        return bytes == null || rs.wasNull() ? null : converter.convertToEntityAttribute(bytes);
    }

    @Override
    public void nullSafeSet(final PreparedStatement st, final Object value,
                            final int index, final WrapperOptions options) throws SQLException {
        if (value == null) {
            st.setNull(index, Types.VARBINARY);
            return;
        }
        st.setBytes(index, converter.convertToDatabaseColumn(value));
    }

    @Override
    public Object deepCopy(final Object value) {
        if (value == null || !mutable) {
            return value;
        }
        if (value.getClass().isArray()) {
            final var length = Array.getLength(value);
            final var copy = Array.newInstance(value.getClass().getComponentType(), length);
            System.arraycopy(value, 0, copy, 0, length);
            return copy;
        }
        // java.util.Date, and its java.sql subclasses, which clone() keeps, nanos included
        return ((java.util.Date) value).clone();
    }

    @Override
    public boolean isMutable() {
        return mutable;
    }

    /**
     * {@inheritDoc}
     *
     * @return the ciphertext of the {@code value}: what the second-level cache holds.
     */
    @Override
    public Serializable disassemble(
            final Object value) {
        return value == null ? null : converter.convertToDatabaseColumn(value);
    }

    /**
     * {@inheritDoc}
     *
     * @return the value decrypted from the {@code cached} ciphertext.
     */
    @Override
    public Object assemble(
            final Serializable cached, final Object owner) {
        return cached == null ? null : converter.convertToEntityAttribute((byte[]) cached);
    }

    @Override
    public Object replace(final Object detached,
                                                        final Object managed,
                                                        final Object owner) {
        return deepCopy(detached);
    }

    // -----------------------------------------------------------------------------------------------------------------
    // what an attribute may not be: its column is compared with a fresh ciphertext, and never matches
    private static final List<Class<? extends Annotation>> REFUSED =
            List.of(Id.class, EmbeddedId.class, Version.class, NaturalId.class);

    private final Class<Object> returnedClass;

    private final AttributeConverter<Object, byte[]> converter;

    private final boolean mutable;
}
