package com.github.jinahya.persistence.crypto;

import com.github.jinahya.persistence.mapped.__MappedEntity;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PostLoad;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.Year;
import java.util.Objects;
import java.util.UUID;

@EntityListeners({
        _EncryptionListener.class
})
@__EncryptedEntity(encryptionIdentifierAttribute = "__encryptionIdentifier")
@Entity
@Table(name = "entity01")
@Setter
@Getter
@ToString
@Slf4j
class Entity01 extends __MappedEntity<Long> {

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    protected Entity01() {
        super();
    }

    // -----------------------------------------------------------------------------------------------------------------
    @PostLoad
    private void onPostLoad() {
        log.debug("onPostLoad()");
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object
    @Override
    public final boolean equals(final Object obj) {
        if (!(obj instanceof Entity01 entity01)) {
            return false;
        }
        return Objects.equals(getId(), entity01.getId());
    }

    @Override
    public final int hashCode() {
        return Objects.hashCode(getId());
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, insertable = false, updatable = false)
    private Long id;

    // -----------------------------------------------------------------------------------------------------------------
    @Basic(optional = true)
    @Column(name = "encryption_identifie__r", nullable = true, insertable = true, updatable = true)
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    @EqualsAndHashCode.Exclude
    String encryptionIdentifier__;

    // -----------------------------------------------------------------------------------------------------------------
    @__EncryptedAttribute(encryptedAttribute = "boolean1Enc__")
    @Basic(optional = true)
    @Column(name = "boolean1", nullable = true, insertable = true, updatable = true)
    Boolean boolean1;

    @Basic(optional = true)
    @Column(name = "boolean1_enc__", nullable = true, insertable = true, updatable = true)
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    @EqualsAndHashCode.Exclude
    byte[] boolean1Enc__;

    // -----------------------------------------------------------------------------------------------------------------
    @__EncryptedAttribute(encryptedAttribute = "byte1Enc__")
    @Basic(optional = true)
    @Column(name = "byte1", nullable = true, insertable = true, updatable = true)
    Byte byte1;

    @Basic(optional = true)
    @Column(name = "byte1_enc__", nullable = true, insertable = true, updatable = true)
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    @EqualsAndHashCode.Exclude
    byte[] byte1Enc__;

    // -----------------------------------------------------------------------------------------------------------------
    @__EncryptedAttribute(encryptedAttribute = "")
    @Basic(optional = true)
    @Column(name = "short1", nullable = true, insertable = true, updatable = true)
    Short short1;

    @Basic(optional = true)
    @Column(name = "short1_enc__", nullable = true, insertable = true, updatable = true)
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    @EqualsAndHashCode.Exclude
    byte[] short1Enc__;

    // -----------------------------------------------------------------------------------------------------------------
    @__EncryptedAttribute(encryptedAttribute = "")
    @Basic(optional = true)
    @Column(name = "int1", nullable = true, insertable = true, updatable = true)
    Integer int1;

    @Basic(optional = true)
    @Column(name = "int1_enc__", nullable = true, insertable = true, updatable = true)
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    @EqualsAndHashCode.Exclude
    byte[] int1Enc__;

    // -----------------------------------------------------------------------------------------------------------------
    @__EncryptedAttribute(encryptedAttribute = "")
    @Basic(optional = true)
    @Column(name = "long1", nullable = true, insertable = true, updatable = true)
    Long long1;

    @Basic(optional = true)
    @Column(name = "long1_enc__", nullable = true, insertable = true, updatable = true)
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    @EqualsAndHashCode.Exclude
    byte[] long1Enc__;

    // -----------------------------------------------------------------------------------------------------------------
    @__EncryptedAttribute(encryptedAttribute = "")
    @Basic(optional = true)
    @Column(name = "char1", nullable = true, insertable = true, updatable = true)
    Character char1;

    @Basic(optional = true)
    @Column(name = "char1_enc__", nullable = true, insertable = true, updatable = true)
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    @EqualsAndHashCode.Exclude
    byte[] char1Enc__;

    // -----------------------------------------------------------------------------------------------------------------
    @__EncryptedAttribute(encryptedAttribute = "")
    @Basic(optional = true)
    @Column(name = "float1", nullable = true, insertable = true, updatable = true)
    Float float1;

    @Basic(optional = true)
    @Column(name = "float1_enc__", nullable = true, insertable = true, updatable = true)
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    @EqualsAndHashCode.Exclude
    byte[] float1Enc__;

    // -----------------------------------------------------------------------------------------------------------------
    @__EncryptedAttribute(encryptedAttribute = "")
    @Basic(optional = true)
    @Column(name = "double1", nullable = true, insertable = true, updatable = true)
    Double double1;

    @Basic(optional = true)
    @Column(name = "double1_enc__", nullable = true, insertable = true, updatable = true)
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    @EqualsAndHashCode.Exclude
    byte[] double1Enc__;

    // -----------------------------------------------------------------------------------------------------------------
    @__EncryptedAttribute(encryptedAttribute = "")
    @Basic(optional = true)
    @Column(name = "string1", nullable = true, insertable = true, updatable = true)
    String string1;

    @Size(min = 0, max = 1024)
    @Basic(optional = true)
    @Column(name = "string1_enc__", nullable = true, insertable = true, updatable = true)
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    @EqualsAndHashCode.Exclude
    byte[] string1Enc__;

    // -----------------------------------------------------------------------------------------------------------------
    @__EncryptedAttribute(encryptedAttribute = "")
    @Basic(optional = true)
    @Column(name = "uuid1", nullable = true, insertable = true, updatable = true)
    UUID uuid1;

    @Size(min = 0, max = 1024)
    @Basic(optional = true)
    @Column(name = "uuid1_enc__", nullable = true, insertable = true, updatable = true)
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    @EqualsAndHashCode.Exclude
    byte[] uuid1Enc__;

    // -----------------------------------------------------------------------------------------------------------------
    @DecimalMax("+9999")
    @DecimalMin("-9999")
    @__EncryptedAttribute(encryptedAttribute = "")
    @Basic(optional = true)
    @Column(name = "bigInteger1", nullable = true, insertable = true, updatable = true)
    BigInteger bigInteger1;

    @Size(min = 0, max = 1024)
    @Basic(optional = true)
    @Column(name = "bigInteger1_enc__", nullable = true, insertable = true, updatable = true)
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    @EqualsAndHashCode.Exclude
    byte[] bigInteger1Enc__;

    // -----------------------------------------------------------------------------------------------------------------
    @DecimalMax("+9999.9999")
    @DecimalMin("-9999.9999")
    @__EncryptedAttribute(encryptedAttribute = "")
    @Basic(optional = true)
    @Column(name = "big_decimal1", nullable = true, insertable = true, updatable = true)
    BigDecimal bigDecimal1;

    @Basic(optional = true)
    @Column(name = "big_decimal1_enc__", nullable = true, insertable = true, updatable = true)
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    @EqualsAndHashCode.Exclude
    byte[] bigDecimal1Enc__;

    // -----------------------------------------------------------------------------------------------------------------
    @__EncryptedAttribute(encryptedAttribute = "")
    @Basic(optional = true)
    @Column(name = "local_date1", nullable = true, insertable = true, updatable = true)
    LocalDate localDate1;

    @Basic(optional = true)
    @Column(name = "local_date1_enc__", nullable = true, insertable = true, updatable = true)
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    @EqualsAndHashCode.Exclude
    byte[] localDate1Enc__;

    // -----------------------------------------------------------------------------------------------------------------
    @__EncryptedAttribute(encryptedAttribute = "")
    @Basic(optional = true)
    @Column(name = "local_time1", nullable = true, insertable = true, updatable = true)
    LocalTime localTime1;

    @Basic(optional = true)
    @Column(name = "local_time1_enc__", nullable = true, insertable = true, updatable = true)
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    @EqualsAndHashCode.Exclude
    byte[] localTime1Enc__;

    // -----------------------------------------------------------------------------------------------------------------
    @__EncryptedAttribute(encryptedAttribute = "")
    @Basic(optional = true)
    @Column(name = "local_date_time1", nullable = true, insertable = true, updatable = true)
    LocalDateTime localDateTime1;

    @Basic(optional = true)
    @Column(name = "local_date_time1_enc__", nullable = true, insertable = true, updatable = true)
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    @EqualsAndHashCode.Exclude
    byte[] localDateTime1Enc__;

    // -----------------------------------------------------------------------------------------------------------------
    @__EncryptedAttribute(encryptedAttribute = "")
    @Basic(optional = true)
    @Column(name = "offset_time1", nullable = true, insertable = true, updatable = true)
    OffsetDateTime offsetTime1;

    @Basic(optional = true)
    @Column(name = "offset_time1_enc__", nullable = true, insertable = true, updatable = true)
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    @EqualsAndHashCode.Exclude
    byte[] offsetTime1Enc__;

    // -----------------------------------------------------------------------------------------------------------------
    @__EncryptedAttribute(encryptedAttribute = "")
    @Basic(optional = true)
    @Column(name = "offset_date_time1", nullable = true, insertable = true, updatable = true)
    OffsetDateTime offsetDateTime1;

    @Basic(optional = true)
    @Column(name = "offset_date_time1_enc__", nullable = true, insertable = true, updatable = true)
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    @EqualsAndHashCode.Exclude
    byte[] offsetDateTime1Enc__;

    // -----------------------------------------------------------------------------------------------------------------
    @__EncryptedAttribute(encryptedAttribute = "")
    @Basic(optional = true)
    @Column(name = "instant1", nullable = true, insertable = true, updatable = true)
    Instant instant1;

    @Basic(optional = true)
    @Column(name = "instant1_enc__", nullable = true, insertable = true, updatable = true)
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    @EqualsAndHashCode.Exclude
    byte[] instant1Enc__;

    // -----------------------------------------------------------------------------------------------------------------
    @__EncryptedAttribute(encryptedAttribute = "")
    @Basic(optional = true)
    @Column(name = "year1", nullable = true, insertable = true, updatable = true)
    Year year1;

    @Basic(optional = true)
    @Column(name = "year1_enc__", nullable = true, insertable = true, updatable = true)
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    @EqualsAndHashCode.Exclude
    byte[] year1Enc__;

    // -----------------------------------------------------------------------------------------------------------------
//    @__EncryptedAttribute(encryptedAttribute = "")
//    @Basic(optional = true)
//    @Column(name = "java_util_date1", nullable = true, insertable = true, updatable = true)
//    java.util.Date javaUtilDate1;
//
//    @Basic(optional = true)
//    @Column(name = "java_util_date1_enc__", nullable = true, insertable = true, updatable = true)
//    @Setter(AccessLevel.NONE)
//    @Getter(AccessLevel.NONE)
//    @EqualsAndHashCode.Exclude
//    byte[] javaUtilDate1Enc__;

    // -----------------------------------------------------------------------------------------------------------------
//    @__EncryptedAttribute(encryptedAttribute = "")
//    @Basic(optional = true)
//    @Column(name = "calendar1", nullable = true, insertable = true, updatable = true)
//    java.util.Calendar calendar1;
//
//    @Basic(optional = true)
//    @Column(name = "calendar1_enc__", nullable = true, insertable = true, updatable = true)
//    @Setter(AccessLevel.NONE)
//    @Getter(AccessLevel.NONE)
//    @EqualsAndHashCode.Exclude
//    byte[] calendar1Enc__;

    // -----------------------------------------------------------------------------------------------------------------
//    @__EncryptedAttribute(encryptedAttribute = "")
//    @Temporal(TemporalType.DATE)
//    @Basic(optional = true)
//    @Column(name = "java_sql_date1", nullable = true, insertable = true, updatable = true)
//    java.sql.Date javaSqlDate1;
//
//    @Basic(optional = true)
//    @Column(name = "java_sql_date1_enc__", nullable = true, insertable = true, updatable = true)
//    @Setter(AccessLevel.NONE)
//    @Getter(AccessLevel.NONE)
//    @EqualsAndHashCode.Exclude
//    byte[] javaSqlDate1Enc__;

    // -----------------------------------------------------------------------------------------------------------------
//    @__EncryptedAttribute(encryptedAttribute = "")
//    @Temporal(TemporalType.TIME)
//    @Basic(optional = true)
//    @Column(name = "java_sql_time1", nullable = true, insertable = true, updatable = true)
//    java.sql.Time javaSqlTime1;
//
//    @Basic(optional = true)
//    @Column(name = "java_sql_time1_enc__", nullable = true, insertable = true, updatable = true)
//    @Setter(AccessLevel.NONE)
//    @Getter(AccessLevel.NONE)
//    @EqualsAndHashCode.Exclude
//    byte[] javaSqlTime1Enc__;

//    // -----------------------------------------------------------------------------------------------------------------
//    @__EncryptedAttribute(encryptedAttribute = "")
//    @Temporal(TemporalType.TIMESTAMP)
//    @Basic(optional = true)
//    @Column(name = "java_sql_timestamp1", nullable = true, insertable = true, updatable = true)
//    java.sql.Timestamp javaSqlTimestamp1;
//
//    @Basic(optional = true)
//    @Column(name = "java_sql_timestamp1_enc__", nullable = true, insertable = true, updatable = true)
//    @Setter(AccessLevel.NONE)
//    @Getter(AccessLevel.NONE)
//    @EqualsAndHashCode.Exclude
//    byte[] javaSqlTimestamp1Enc__;
}
