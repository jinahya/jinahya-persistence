package com.github.jinahya.persistence.encryption.hibernate;

import org.hibernate.annotations.Type;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks an attribute as encrypted, on Hibernate ORM: the attribute holds the plaintext, its column holds the
 * ciphertext, and the codec is chosen from the attribute's declared java type.
 * <pre>{@code
 * @Encrypted
 * @Column(name = "name_enc", length = 2048)   // a binary column, as long as the service makes it
 * private String name;
 * }</pre>
 * One annotation, for every supported type: it composes Hibernate ORM's {@link Type @Type} with
 * {@link EncryptedUserType}, which Hibernate ORM creates for each annotated attribute, with the attribute's declared
 * type and the session factory's settings in hand. The standard mapping,
 * {@code @Convert(converter = Encrypted<Type>Converter.class)}, keeps working next to it.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see EncryptedUserType
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@Type(EncryptedUserType.class)
public @interface Encrypted {

}
