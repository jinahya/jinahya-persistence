package com.github.jinahya.persistence.encryption.examples;

import com.github.jinahya.persistence.encryption.EncryptedBigDecimalConverter;
import com.github.jinahya.persistence.encryption.EncryptedBigIntegerConverter;
import com.github.jinahya.persistence.encryption.EncryptedBooleanConverter;
import com.github.jinahya.persistence.encryption.EncryptedByteConverter;
import com.github.jinahya.persistence.encryption.EncryptedBytesConverter;
import com.github.jinahya.persistence.encryption.EncryptedBytesWrappedConverter;
import com.github.jinahya.persistence.encryption.EncryptedCharacterConverter;
import com.github.jinahya.persistence.encryption.EncryptedCharsConverter;
import com.github.jinahya.persistence.encryption.EncryptedCharsWrappedConverter;
import com.github.jinahya.persistence.encryption.EncryptedDoubleConverter;
import com.github.jinahya.persistence.encryption.EncryptedFloatConverter;
import com.github.jinahya.persistence.encryption.EncryptedInstantConverter;
import com.github.jinahya.persistence.encryption.EncryptedIntegerConverter;
import com.github.jinahya.persistence.encryption.EncryptedLocalDateConverter;
import com.github.jinahya.persistence.encryption.EncryptedLocalDateTimeConverter;
import com.github.jinahya.persistence.encryption.EncryptedLocalTimeConverter;
import com.github.jinahya.persistence.encryption.EncryptedLongConverter;
import com.github.jinahya.persistence.encryption.EncryptedOffsetDateTimeConverter;
import com.github.jinahya.persistence.encryption.EncryptedOffsetTimeConverter;
import com.github.jinahya.persistence.encryption.EncryptedShortConverter;
import com.github.jinahya.persistence.encryption.EncryptedSqlDateConverter;
import com.github.jinahya.persistence.encryption.EncryptedSqlTimeConverter;
import com.github.jinahya.persistence.encryption.EncryptedSqlTimestampConverter;
import com.github.jinahya.persistence.encryption.EncryptedStringConverter;
import com.github.jinahya.persistence.encryption.EncryptedUtilDateConverter;
import com.github.jinahya.persistence.encryption.EncryptedUuidConverter;
import com.github.jinahya.persistence.encryption.EncryptedYearConverter;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
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
 * An entity with one encrypted attribute of every supported type.
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

    @Convert(converter = EncryptedBooleanConverter.class)
    @Basic(optional = true)
    @Column(name = "a_boolean", nullable = true, length = 2048)
    public Boolean aBoolean;

    @Convert(converter = EncryptedByteConverter.class)
    @Basic(optional = true)
    @Column(name = "a_byte", nullable = true, length = 2048)
    public Byte aByte;

    @Convert(converter = EncryptedShortConverter.class)
    @Basic(optional = true)
    @Column(name = "a_short", nullable = true, length = 2048)
    public Short aShort;

    @Convert(converter = EncryptedCharacterConverter.class)
    @Basic(optional = true)
    @Column(name = "a_character", nullable = true, length = 2048)
    public Character aCharacter;

    @Convert(converter = EncryptedIntegerConverter.class)
    @Basic(optional = true)
    @Column(name = "an_integer", nullable = true, length = 2048)
    public Integer anInteger;

    @Convert(converter = EncryptedFloatConverter.class)
    @Basic(optional = true)
    @Column(name = "a_float", nullable = true, length = 2048)
    public Float aFloat;

    @Convert(converter = EncryptedLongConverter.class)
    @Basic(optional = true)
    @Column(name = "a_long", nullable = true, length = 2048)
    public Long aLong;

    @Convert(converter = EncryptedDoubleConverter.class)
    @Basic(optional = true)
    @Column(name = "a_double", nullable = true, length = 2048)
    public Double aDouble;

    @Convert(converter = EncryptedStringConverter.class)
    @Basic(optional = true)
    @Column(name = "a_string", nullable = true, length = 2048)
    public String aString;

    @Convert(converter = EncryptedUuidConverter.class)
    @Basic(optional = true)
    @Column(name = "a_uuid", nullable = true, length = 2048)
    public UUID aUuid;

    @Convert(converter = EncryptedBigIntegerConverter.class)
    @Basic(optional = true)
    @Column(name = "a_big_integer", nullable = true, length = 2048)
    public BigInteger aBigInteger;

    @Convert(converter = EncryptedBigDecimalConverter.class)
    @Basic(optional = true)
    @Column(name = "a_big_decimal", nullable = true, length = 2048)
    public BigDecimal aBigDecimal;

    @Convert(converter = EncryptedLocalDateConverter.class)
    @Basic(optional = true)
    @Column(name = "a_local_date", nullable = true, length = 2048)
    public LocalDate aLocalDate;

    @Convert(converter = EncryptedLocalTimeConverter.class)
    @Basic(optional = true)
    @Column(name = "a_local_time", nullable = true, length = 2048)
    public LocalTime aLocalTime;

    @Convert(converter = EncryptedLocalDateTimeConverter.class)
    @Basic(optional = true)
    @Column(name = "a_local_date_time", nullable = true, length = 2048)
    public LocalDateTime aLocalDateTime;

    @Convert(converter = EncryptedOffsetTimeConverter.class)
    @Basic(optional = true)
    @Column(name = "an_offset_time", nullable = true, length = 2048)
    public OffsetTime anOffsetTime;

    @Convert(converter = EncryptedOffsetDateTimeConverter.class)
    @Basic(optional = true)
    @Column(name = "an_offset_date_time", nullable = true, length = 2048)
    public OffsetDateTime anOffsetDateTime;

    @Convert(converter = EncryptedInstantConverter.class)
    @Basic(optional = true)
    @Column(name = "an_instant", nullable = true, length = 2048)
    public Instant anInstant;

    @Convert(converter = EncryptedYearConverter.class)
    @Basic(optional = true)
    @Column(name = "a_year", nullable = true, length = 2048)
    public Year aYear;

    @Convert(converter = EncryptedUtilDateConverter.class)
    @Basic(optional = true)
    @Column(name = "a_util_date", nullable = true, length = 2048)
    public java.util.Date aUtilDate;

    @Convert(converter = EncryptedSqlDateConverter.class)
    @Basic(optional = true)
    @Column(name = "a_sql_date", nullable = true, length = 2048)
    public java.sql.Date aSqlDate;

    @Convert(converter = EncryptedSqlTimeConverter.class)
    @Basic(optional = true)
    @Column(name = "a_sql_time", nullable = true, length = 2048)
    public java.sql.Time aSqlTime;

    @Convert(converter = EncryptedSqlTimestampConverter.class)
    @Basic(optional = true)
    @Column(name = "a_sql_timestamp", nullable = true, length = 2048)
    public java.sql.Timestamp aSqlTimestamp;

    @Convert(converter = EncryptedBytesConverter.class)
    @Basic(optional = true)
    @Column(name = "bytes", nullable = true, length = 2048)
    public byte[] bytes;

    @Convert(converter = EncryptedBytesWrappedConverter.class)
    @Basic(optional = true)
    @Column(name = "bytes_wrapped", nullable = true, length = 2048)
    public Byte[] bytesWrapped;

    @Convert(converter = EncryptedCharsConverter.class)
    @Basic(optional = true)
    @Column(name = "chars", nullable = true, length = 2048)
    public char[] chars;

    @Convert(converter = EncryptedCharsWrappedConverter.class)
    @Basic(optional = true)
    @Column(name = "chars_wrapped", nullable = true, length = 2048)
    public Character[] charsWrapped;

    @Convert(converter = EncryptedColorConverter.class)
    @Basic(optional = true)
    @Column(name = "a_color", nullable = true, length = 2048)
    public Color aColor;
}
