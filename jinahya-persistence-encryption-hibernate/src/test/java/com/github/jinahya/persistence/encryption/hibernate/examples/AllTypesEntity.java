package com.github.jinahya.persistence.encryption.hibernate.examples;

import com.github.jinahya.persistence.encryption.hibernate.Encrypted;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.util.UUID;

/**
 * One {@code @Encrypted} attribute of every supported type, an enum, and a primitive.
 */
@Access(AccessType.FIELD)
@Entity
@Table(name = AllTypesEntity.TABLE_NAME)
public class AllTypesEntity {

    public static final String TABLE_NAME = "all_types_entity";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long id;

    @Encrypted
    @Basic(optional = true)
    @Column(name = "a_boolean", nullable = true, length = 2048)
    public Boolean aBoolean;

    @Encrypted
    @Basic(optional = true)
    @Column(name = "a_byte", nullable = true, length = 2048)
    public Byte aByte;

    @Encrypted
    @Basic(optional = true)
    @Column(name = "a_short", nullable = true, length = 2048)
    public Short aShort;

    @Encrypted
    @Basic(optional = true)
    @Column(name = "a_character", nullable = true, length = 2048)
    public Character aCharacter;

    @Encrypted
    @Basic(optional = true)
    @Column(name = "an_integer", nullable = true, length = 2048)
    public Integer anInteger;

    @Encrypted
    @Basic(optional = true)
    @Column(name = "a_float", nullable = true, length = 2048)
    public Float aFloat;

    @Encrypted
    @Basic(optional = true)
    @Column(name = "a_long", nullable = true, length = 2048)
    public Long aLong;

    @Encrypted
    @Basic(optional = true)
    @Column(name = "a_double", nullable = true, length = 2048)
    public Double aDouble;

    @Encrypted
    @Basic(optional = true)
    @Column(name = "a_string", nullable = true, length = 2048)
    public String aString;

    @Encrypted
    @Basic(optional = true)
    @Column(name = "a_uuid", nullable = true, length = 2048)
    public UUID aUuid;

    @Encrypted
    @Basic(optional = true)
    @Column(name = "a_big_integer", nullable = true, length = 2048)
    public BigInteger aBigInteger;

    @Encrypted
    @Basic(optional = true)
    @Column(name = "a_big_decimal", nullable = true, length = 2048)
    public BigDecimal aBigDecimal;

    @Encrypted
    @Basic(optional = true)
    @Column(name = "a_local_date", nullable = true, length = 2048)
    public LocalDate aLocalDate;

    @Encrypted
    @Basic(optional = true)
    @Column(name = "a_local_time", nullable = true, length = 2048)
    public LocalTime aLocalTime;

    @Encrypted
    @Basic(optional = true)
    @Column(name = "a_local_date_time", nullable = true, length = 2048)
    public LocalDateTime aLocalDateTime;

    @Encrypted
    @Basic(optional = true)
    @Column(name = "an_offset_time", nullable = true, length = 2048)
    public OffsetTime anOffsetTime;

    @Encrypted
    @Basic(optional = true)
    @Column(name = "an_offset_date_time", nullable = true, length = 2048)
    public OffsetDateTime anOffsetDateTime;

    @Encrypted
    @Basic(optional = true)
    @Column(name = "an_instant", nullable = true, length = 2048)
    public Instant anInstant;

    @Encrypted
    @Basic(optional = true)
    @Column(name = "a_year", nullable = true, length = 2048)
    public Year aYear;

    @Encrypted
    @Basic(optional = true)
    @Column(name = "a_util_date", nullable = true, length = 2048)
    public java.util.Date aUtilDate;

    @Encrypted
    @Basic(optional = true)
    @Column(name = "a_sql_date", nullable = true, length = 2048)
    public java.sql.Date aSqlDate;

    @Encrypted
    @Basic(optional = true)
    @Column(name = "a_sql_time", nullable = true, length = 2048)
    public java.sql.Time aSqlTime;

    @Encrypted
    @Basic(optional = true)
    @Column(name = "a_sql_timestamp", nullable = true, length = 2048)
    public java.sql.Timestamp aSqlTimestamp;

    @Encrypted
    @Basic(optional = true)
    @Column(name = "bytes", nullable = true, length = 2048)
    public byte[] bytes;

    @Encrypted
    @Basic(optional = true)
    @Column(name = "bytes_wrapped", nullable = true, length = 2048)
    public Byte[] bytesWrapped;

    @Encrypted
    @Basic(optional = true)
    @Column(name = "chars", nullable = true, length = 2048)
    public char[] chars;

    @Encrypted
    @Basic(optional = true)
    @Column(name = "chars_wrapped", nullable = true, length = 2048)
    public Character[] charsWrapped;

    @Encrypted
    @Basic(optional = true)
    @Column(name = "a_color", nullable = true, length = 2048)
    public Color aColor;

    @Encrypted
    @Column(name = "an_int", nullable = true, length = 2048)
    public int anInt;
}
