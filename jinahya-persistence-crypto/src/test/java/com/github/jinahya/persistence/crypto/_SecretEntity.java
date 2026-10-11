package com.github.jinahya.persistence.crypto;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.UUID;

/**
 * An entity holding one {@link EncryptedAttribute encrypted attribute} of each java type whose encoding
 * {@link AbstractEntityEncryptionService} dispatches on, for verifying the encrypt/decrypt round trip against a real metamodel.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@EncryptedEntity
@Access(AccessType.FIELD)
@Entity
@Table(name = "secret_entity")
public class _SecretEntity {

    /**
     * A constant enum, for the {@code enum} attribute.
     */
    public enum Grade {

        LOW,

        HIGH
    }

    // -----------------------------------------------------------------------------------------------------------------
    public Long getId() {
        return id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ---------------------------------------------------------------------------------------------------------- String
    @Basic(optional = true)
    @Column(name = "name", nullable = true, insertable = false)
    public String name;

    @EncryptedAttribute("name")
    @Basic(optional = true)
    @Column(name = "name_enc", nullable = true, length = 2048)
    public byte[] nameEnc__;

    // --------------------------------------------------------------------------------------------------------- Integer
    @Basic(optional = true)
    @Column(name = "age", nullable = true, insertable = false)
    public Integer age;

    @EncryptedAttribute("age")
    @Basic(optional = true)
    @Column(name = "age_enc", nullable = true, length = 2048)
    public byte[] ageEnc__;

    // ------------------------------------------------------------------------------------------------------------ UUID
    @Basic(optional = true)
    @Column(name = "reference", nullable = true, insertable = false)
    public UUID reference;

    @EncryptedAttribute("reference")
    @Basic(optional = true)
    @Column(name = "reference_enc", nullable = true, length = 2048)
    public byte[] referenceEnc__;

    // ------------------------------------------------------------------------------------------------------- LocalDate
    @Basic(optional = true)
    @Column(name = "born_on", nullable = true, insertable = false)
    public LocalDate bornOn;

    @EncryptedAttribute("bornOn")
    @Basic(optional = true)
    @Column(name = "born_on_enc", nullable = true, length = 2048)
    public byte[] bornOnEnc__;

    // ------------------------------------------------------------------------------------------- java.sql.Timestamp
    // the branch which used to fall through to java.util.Date, losing the nanos
    @Basic(optional = true)
    @Column(name = "seen_at", nullable = true, insertable = false)
    public java.sql.Timestamp seenAt;

    @EncryptedAttribute("seenAt")
    @Basic(optional = true)
    @Column(name = "seen_at_enc", nullable = true, length = 2048)
    public byte[] seenAtEnc__;

    // ------------------------------------------------------------------------------------------------- java.util.Date
    @Temporal(TemporalType.TIMESTAMP)
    @Basic(optional = true)
    @Column(name = "joined_at", nullable = true, insertable = false)
    public java.util.Date joinedAt;

    @EncryptedAttribute("joinedAt")
    @Basic(optional = true)
    @Column(name = "joined_at_enc", nullable = true, length = 2048)
    public byte[] joinedAtEnc__;

    // -------------------------------------------------------------------------------------------------------- Calendar
    @Temporal(TemporalType.TIMESTAMP)
    @Basic(optional = true)
    @Column(name = "checked_at", nullable = true, insertable = false)
    public Calendar checkedAt;

    @EncryptedAttribute("checkedAt")
    @Basic(optional = true)
    @Column(name = "checked_at_enc", nullable = true, length = 2048)
    public byte[] checkedAtEnc__;

    // ------------------------------------------------------------------------------------------------------------ enum
    @Enumerated(EnumType.STRING)
    @Basic(optional = true)
    @Column(name = "grade", nullable = true, insertable = false)
    public Grade grade;

    @EncryptedAttribute("grade")
    @Basic(optional = true)
    @Column(name = "grade_enc", nullable = true, length = 2048)
    public byte[] gradeEnc__;

    // ---------------------------------------------------------------------------------------------------------- byte[]
    @Basic(optional = true)
    @Column(name = "photo", nullable = true, length = 2048, insertable = false)
    public byte[] photo;

    @EncryptedAttribute("photo")
    @Basic(optional = true)
    @Column(name = "photo_enc", nullable = true, length = 2048)
    public byte[] photoEnc__;

    // -------------------------------------------------------- a ciphertext whose name does not follow the plaintext
    @Basic(optional = true)
    @Column(name = "secret_number", nullable = true, insertable = false)
    public Long secretNumber;

    @EncryptedAttribute("secretNumber")
    @Basic(optional = true)
    @Column(name = "secret_number_cipher", nullable = true, length = 2048)
    public byte[] secretNumberCipher;

    // ------------------------------------------------ a declared type broader than the value it holds at run time
    // Jakarta Persistence 3.2 §2.6 admits any Serializable as a basic type; the value here is an ordinary String,
    // so the declared type and the runtime type deliberately disagree.
    @Basic(optional = true)
    @Column(name = "opaque", nullable = true, insertable = false)
    public java.io.Serializable opaque;

    @EncryptedAttribute("opaque")
    @Basic(optional = true)
    @Column(name = "opaque_enc", nullable = true, length = 2048)
    public byte[] opaqueEnc__;

    // -------------------------------------------------------------------------------------------------------- EMBEDDED
    @Embedded
    public _SecretEmbeddable secret;
}
