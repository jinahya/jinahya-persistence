package com.github.jinahya.persistence.encryption.examples;

import com.github.jinahya.persistence.encryption.EncryptedAttribute;
import com.github.jinahya.persistence.encryption.LifecycleListenerEntity;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

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
 * An entity holding one encrypted attribute of each supported java type, primitives included, for the round trip
 * through a persistence provider and its database.
 * <p>
 * A primitive plaintext is never nulled, so its column is mapped {@code updatable = false}, as recommended; as every
 * {@code INSERT} leaves it out, the column needs a {@code DEFAULT}: Hibernate ORM fails to load a {@code NULL} into a
 * primitive (EclipseLink loads it as zero).
 * <p>
 * Hibernate ORM rejects a {@code Byte[]} or {@code Character[]} attribute unless
 * {@code hibernate.type.wrapper_array_handling} is set; the test persistence unit sets it to {@code ALLOW}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@EntityListeners(LifecycleListenerEntity.class)
@Access(AccessType.FIELD)
@Entity
@Table(name = AllTypesEntity.TABLE_NAME)
@SuppressWarnings({
        "deprecation", // java.util.Date, java.sql.*, Byte[] and Character[] are deprecated by Jakarta Persistence 3.2
        "java:S1104"   // public fields: this is a test fixture
})
public class AllTypesEntity {

    public static final String TABLE_NAME = "all_types_entity";

    /**
     * A constant enum, for the {@code enum} attribute.
     */
    public enum Grade {
        LOW,
        HIGH
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long id;

    // --------------------------------------------------------------------------------------------------------- boolean
    @Basic
    @Column(name = "primitive_boolean", insertable = false, updatable = false,
            columnDefinition = "BOOLEAN DEFAULT FALSE")
    public boolean primitiveBoolean;

    @EncryptedAttribute("primitiveBoolean")
    @Basic(optional = true)
    @Column(name = "primitive_boolean_enc", nullable = true, length = 2048)
    public byte[] primitiveBooleanEnc;

    // --------------------------------------------------------------------------------------------------------- Boolean
    @Basic(optional = true)
    @Column(name = "wrapper_boolean", nullable = true, insertable = false)
    public Boolean wrapperBoolean;

    @EncryptedAttribute("wrapperBoolean")
    @Basic(optional = true)
    @Column(name = "wrapper_boolean_enc", nullable = true, length = 2048)
    public byte[] wrapperBooleanEnc;

    // ------------------------------------------------------------------------------------------------------------ byte
    @Basic
    @Column(name = "primitive_byte", insertable = false, updatable = false,
            columnDefinition = "TINYINT DEFAULT 0")
    public byte primitiveByte;

    @EncryptedAttribute("primitiveByte")
    @Basic(optional = true)
    @Column(name = "primitive_byte_enc", nullable = true, length = 2048)
    public byte[] primitiveByteEnc;

    // ------------------------------------------------------------------------------------------------------------ Byte
    @Basic(optional = true)
    @Column(name = "wrapper_byte", nullable = true, insertable = false)
    public Byte wrapperByte;

    @EncryptedAttribute("wrapperByte")
    @Basic(optional = true)
    @Column(name = "wrapper_byte_enc", nullable = true, length = 2048)
    public byte[] wrapperByteEnc;

    // ----------------------------------------------------------------------------------------------------------- short
    @Basic
    @Column(name = "primitive_short", insertable = false, updatable = false,
            columnDefinition = "SMALLINT DEFAULT 0")
    public short primitiveShort;

    @EncryptedAttribute("primitiveShort")
    @Basic(optional = true)
    @Column(name = "primitive_short_enc", nullable = true, length = 2048)
    public byte[] primitiveShortEnc;

    // ----------------------------------------------------------------------------------------------------------- Short
    @Basic(optional = true)
    @Column(name = "wrapper_short", nullable = true, insertable = false)
    public Short wrapperShort;

    @EncryptedAttribute("wrapperShort")
    @Basic(optional = true)
    @Column(name = "wrapper_short_enc", nullable = true, length = 2048)
    public byte[] wrapperShortEnc;

    // ------------------------------------------------------------------------------------------------------------- int
    @Basic
    @Column(name = "primitive_int", insertable = false, updatable = false,
            columnDefinition = "INTEGER DEFAULT 0")
    public int primitiveInt;

    @EncryptedAttribute("primitiveInt")
    @Basic(optional = true)
    @Column(name = "primitive_int_enc", nullable = true, length = 2048)
    public byte[] primitiveIntEnc;

    // --------------------------------------------------------------------------------------------------------- Integer
    @Basic(optional = true)
    @Column(name = "wrapper_int", nullable = true, insertable = false)
    public Integer wrapperInt;

    @EncryptedAttribute("wrapperInt")
    @Basic(optional = true)
    @Column(name = "wrapper_int_enc", nullable = true, length = 2048)
    public byte[] wrapperIntEnc;

    // ------------------------------------------------------------------------------------------------------------ long
    @Basic
    @Column(name = "primitive_long", insertable = false, updatable = false,
            columnDefinition = "BIGINT DEFAULT 0")
    public long primitiveLong;

    @EncryptedAttribute("primitiveLong")
    @Basic(optional = true)
    @Column(name = "primitive_long_enc", nullable = true, length = 2048)
    public byte[] primitiveLongEnc;

    // ------------------------------------------------------------------------------------------------------------ Long
    @Basic(optional = true)
    @Column(name = "wrapper_long", nullable = true, insertable = false)
    public Long wrapperLong;

    @EncryptedAttribute("wrapperLong")
    @Basic(optional = true)
    @Column(name = "wrapper_long_enc", nullable = true, length = 2048)
    public byte[] wrapperLongEnc;

    // ------------------------------------------------------------------------------------------------------------ char
    @Basic
    @Column(name = "primitive_char", insertable = false, updatable = false,
            columnDefinition = "CHAR(1) DEFAULT ''")
    public char primitiveChar;

    @EncryptedAttribute("primitiveChar")
    @Basic(optional = true)
    @Column(name = "primitive_char_enc", nullable = true, length = 2048)
    public byte[] primitiveCharEnc;

    // ------------------------------------------------------------------------------------------------------- Character
    @Basic(optional = true)
    @Column(name = "wrapper_char", nullable = true, insertable = false)
    public Character wrapperChar;

    @EncryptedAttribute("wrapperChar")
    @Basic(optional = true)
    @Column(name = "wrapper_char_enc", nullable = true, length = 2048)
    public byte[] wrapperCharEnc;

    // ----------------------------------------------------------------------------------------------------------- float
    @Basic
    @Column(name = "primitive_float", insertable = false, updatable = false,
            columnDefinition = "REAL DEFAULT 0")
    public float primitiveFloat;

    @EncryptedAttribute("primitiveFloat")
    @Basic(optional = true)
    @Column(name = "primitive_float_enc", nullable = true, length = 2048)
    public byte[] primitiveFloatEnc;

    // ----------------------------------------------------------------------------------------------------------- Float
    @Basic(optional = true)
    @Column(name = "wrapper_float", nullable = true, insertable = false)
    public Float wrapperFloat;

    @EncryptedAttribute("wrapperFloat")
    @Basic(optional = true)
    @Column(name = "wrapper_float_enc", nullable = true, length = 2048)
    public byte[] wrapperFloatEnc;

    // ---------------------------------------------------------------------------------------------------------- double
    @Basic
    @Column(name = "primitive_double", insertable = false, updatable = false,
            columnDefinition = "DOUBLE PRECISION DEFAULT 0")
    public double primitiveDouble;

    @EncryptedAttribute("primitiveDouble")
    @Basic(optional = true)
    @Column(name = "primitive_double_enc", nullable = true, length = 2048)
    public byte[] primitiveDoubleEnc;

    // ---------------------------------------------------------------------------------------------------------- Double
    @Basic(optional = true)
    @Column(name = "wrapper_double", nullable = true, insertable = false)
    public Double wrapperDouble;

    @EncryptedAttribute("wrapperDouble")
    @Basic(optional = true)
    @Column(name = "wrapper_double_enc", nullable = true, length = 2048)
    public byte[] wrapperDoubleEnc;

    // ---------------------------------------------------------------------------------------------------------- String
    @Basic(optional = true)
    @Column(name = "string_", nullable = true, insertable = false, length = 2048)
    public String string;

    @EncryptedAttribute("string")
    @Basic(optional = true)
    @Column(name = "string__enc", nullable = true, length = 2048)
    public byte[] stringEnc;

    // ------------------------------------------------------------------------------------------------------------ UUID
    @Basic(optional = true)
    @Column(name = "uuid_", nullable = true, insertable = false)
    public UUID uuid;

    @EncryptedAttribute("uuid")
    @Basic(optional = true)
    @Column(name = "uuid__enc", nullable = true, length = 2048)
    public byte[] uuidEnc;

    // ------------------------------------------------------------------------------------------------------ BigInteger
    @Basic(optional = true)
    @Column(name = "big_integer", nullable = true, insertable = false)
    public BigInteger bigInteger;

    @EncryptedAttribute("bigInteger")
    @Basic(optional = true)
    @Column(name = "big_integer_enc", nullable = true, length = 2048)
    public byte[] bigIntegerEnc;

    // ------------------------------------------------------------------------------------------------------ BigDecimal
    @Basic(optional = true)
    @Column(name = "big_decimal", nullable = true, insertable = false)
    public BigDecimal bigDecimal;

    @EncryptedAttribute("bigDecimal")
    @Basic(optional = true)
    @Column(name = "big_decimal_enc", nullable = true, length = 2048)
    public byte[] bigDecimalEnc;

    // ------------------------------------------------------------------------------------------------------- LocalDate
    @Basic(optional = true)
    @Column(name = "local_date", nullable = true, insertable = false)
    public LocalDate localDate;

    @EncryptedAttribute("localDate")
    @Basic(optional = true)
    @Column(name = "local_date_enc", nullable = true, length = 2048)
    public byte[] localDateEnc;

    // ------------------------------------------------------------------------------------------------------- LocalTime
    @Basic(optional = true)
    @Column(name = "local_time", nullable = true, insertable = false)
    public LocalTime localTime;

    @EncryptedAttribute("localTime")
    @Basic(optional = true)
    @Column(name = "local_time_enc", nullable = true, length = 2048)
    public byte[] localTimeEnc;

    // --------------------------------------------------------------------------------------------------- LocalDateTime
    @Basic(optional = true)
    @Column(name = "local_date_time", nullable = true, insertable = false)
    public LocalDateTime localDateTime;

    @EncryptedAttribute("localDateTime")
    @Basic(optional = true)
    @Column(name = "local_date_time_enc", nullable = true, length = 2048)
    public byte[] localDateTimeEnc;

    // ------------------------------------------------------------------------------------------------------ OffsetTime
    @Basic(optional = true)
    @Column(name = "offset_time", nullable = true, insertable = false)
    public OffsetTime offsetTime;

    @EncryptedAttribute("offsetTime")
    @Basic(optional = true)
    @Column(name = "offset_time_enc", nullable = true, length = 2048)
    public byte[] offsetTimeEnc;

    // -------------------------------------------------------------------------------------------------- OffsetDateTime
    @Basic(optional = true)
    @Column(name = "offset_date_time", nullable = true, insertable = false)
    public OffsetDateTime offsetDateTime;

    @EncryptedAttribute("offsetDateTime")
    @Basic(optional = true)
    @Column(name = "offset_date_time_enc", nullable = true, length = 2048)
    public byte[] offsetDateTimeEnc;

    // --------------------------------------------------------------------------------------------------------- Instant
    @Basic(optional = true)
    @Column(name = "instant_", nullable = true, insertable = false)
    public Instant instant;

    @EncryptedAttribute("instant")
    @Basic(optional = true)
    @Column(name = "instant__enc", nullable = true, length = 2048)
    public byte[] instantEnc;

    // ------------------------------------------------------------------------------------------------------------ Year
    @Basic(optional = true)
    @Column(name = "year_", nullable = true, insertable = false)
    public Year year;

    @EncryptedAttribute("year")
    @Basic(optional = true)
    @Column(name = "year__enc", nullable = true, length = 2048)
    public byte[] yearEnc;

    // -------------------------------------------------------------------------------------------------- java.util.Date
    @Temporal(TemporalType.TIMESTAMP)
    @Basic(optional = true)
    @Column(name = "util_date", nullable = true, insertable = false)
    public java.util.Date utilDate;

    @EncryptedAttribute("utilDate")
    @Basic(optional = true)
    @Column(name = "util_date_enc", nullable = true, length = 2048)
    public byte[] utilDateEnc;

    // --------------------------------------------------------------------------------------------------- java.sql.Date
    @Basic(optional = true)
    @Column(name = "sql_date", nullable = true, insertable = false)
    public java.sql.Date sqlDate;

    @EncryptedAttribute("sqlDate")
    @Basic(optional = true)
    @Column(name = "sql_date_enc", nullable = true, length = 2048)
    public byte[] sqlDateEnc;

    // --------------------------------------------------------------------------------------------------- java.sql.Time
    @Basic(optional = true)
    @Column(name = "sql_time", nullable = true, insertable = false)
    public java.sql.Time sqlTime;

    @EncryptedAttribute("sqlTime")
    @Basic(optional = true)
    @Column(name = "sql_time_enc", nullable = true, length = 2048)
    public byte[] sqlTimeEnc;

    // ---------------------------------------------------------------------------------------------- java.sql.Timestamp
    @Basic(optional = true)
    @Column(name = "sql_timestamp", nullable = true, insertable = false)
    public java.sql.Timestamp sqlTimestamp;

    @EncryptedAttribute("sqlTimestamp")
    @Basic(optional = true)
    @Column(name = "sql_timestamp_enc", nullable = true, length = 2048)
    public byte[] sqlTimestampEnc;

    // ---------------------------------------------------------------------------------------------------------- byte[]
    @Basic(optional = true)
    @Column(name = "bytes_", nullable = true, insertable = false, length = 2048)
    public byte[] bytes;

    @EncryptedAttribute("bytes")
    @Basic(optional = true)
    @Column(name = "bytes__enc", nullable = true, length = 2048)
    public byte[] bytesEnc;

    // ---------------------------------------------------------------------------------------------------------- Byte[]
    @Basic(optional = true)
    @Column(name = "wrapper_bytes", nullable = true, insertable = false, length = 2048)
    public Byte[] wrapperBytes;

    @EncryptedAttribute("wrapperBytes")
    @Basic(optional = true)
    @Column(name = "wrapper_bytes_enc", nullable = true, length = 2048)
    public byte[] wrapperBytesEnc;

    // ---------------------------------------------------------------------------------------------------------- char[]
    @Basic(optional = true)
    @Column(name = "chars_", nullable = true, insertable = false, length = 2048)
    public char[] chars;

    @EncryptedAttribute("chars")
    @Basic(optional = true)
    @Column(name = "chars__enc", nullable = true, length = 2048)
    public byte[] charsEnc;

    // ----------------------------------------------------------------------------------------------------- Character[]
    @Basic(optional = true)
    @Column(name = "wrapper_chars", nullable = true, insertable = false, length = 2048)
    public Character[] wrapperChars;

    @EncryptedAttribute("wrapperChars")
    @Basic(optional = true)
    @Column(name = "wrapper_chars_enc", nullable = true, length = 2048)
    public byte[] wrapperCharsEnc;

    // ----------------------------------------------------------------------------------------------------------- Grade
    @Enumerated(EnumType.STRING)
    @Basic(optional = true)
    @Column(name = "grade", nullable = true, insertable = false)
    public Grade grade;

    @EncryptedAttribute("grade")
    @Basic(optional = true)
    @Column(name = "grade_enc", nullable = true, length = 2048)
    public byte[] gradeEnc;
}
