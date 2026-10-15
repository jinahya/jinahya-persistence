package com.github.jinahya.persistence.encryption.examples;

import com.github.jinahya.persistence.encryption.EncryptedAttribute;
import com.github.jinahya.persistence.encryption.LifecycleListenerEntity;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;

/**
 * An encrypted entity whose {@code @PreUpdate} callback, which assigns the plaintext, is declared in
 * {@code META-INF/callbacks-orm.xml}, where the callback validation can't see it; caught after the {@code UPDATE}
 * (#101).
 * <p>
 * The entity and its table are declared there too, and not annotated: EclipseLink's metamodel generator would
 * otherwise see the entity twice.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Access(AccessType.FIELD)
@EntityListeners(LifecycleListenerEntity.class)
public class XmlCallbackSecretEntity {

    public static final String TABLE_NAME = "xml_callback_secret_entity";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long id;

    @Basic(optional = true)
    @Column(name = "note", nullable = true)
    public String note;

    @Basic(optional = true)
    @Column(name = "name", nullable = true, insertable = false)
    public String name;

    @EncryptedAttribute("name")
    @Basic(optional = true)
    @Column(name = "name_enc", nullable = true, length = 2048)
    public byte[] nameEnc;

    @Transient
    public String nameOnPreUpdate;

    // the pre-update callback, declared in META-INF/callbacks-orm.xml
    void assignOnPreUpdate() {
        if (nameOnPreUpdate != null) {
            name = nameOnPreUpdate;
        }
    }
}
