package com.github.jinahya.persistence.encryption.eclipselink.examples;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.eclipse.persistence.annotations.Convert;

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
 * One attribute of every supported type, an enum, and a primitive, all through the one named converter, which
 * {@link SecretEntity} registers.
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

    @Convert(SecretEntity.CONVERTER)
    @Basic(optional = true)
    @Column(name = "a_boolean", nullable = true, length = 2048)
    public Boolean aBoolean;

    @Convert(SecretEntity.CONVERTER)
    @Basic(optional = true)
    @Column(name = "a_byte", nullable = true, length = 2048)
    public Byte aByte;

    @Convert(SecretEntity.CONVERTER)
    @Basic(optional = true)
    @Column(name = "a_short", nullable = true, length = 2048)
    public Short aShort;

    @Convert(SecretEntity.CONVERTER)
    @Basic(optional = true)
    @Column(name = "a_character", nullable = true, length = 2048)
    public Character aCharacter;

    @Convert(SecretEntity.CONVERTER)
    @Basic(optional = true)
    @Column(name = "an_integer", nullable = true, length = 2048)
    public Integer anInteger;

    @Convert(SecretEntity.CONVERTER)
    @Basic(optional = true)
    @Column(name = "a_float", nullable = true, length = 2048)
    public Float aFloat;

    @Convert(SecretEntity.CONVERTER)
    @Basic(optional = true)
    @Column(name = "a_long", nullable = true, length = 2048)
    public Long aLong;

    @Convert(SecretEntity.CONVERTER)
    @Basic(optional = true)
    @Column(name = "a_double", nullable = true, length = 2048)
    public Double aDouble;

    @Convert(SecretEntity.CONVERTER)
    @Basic(optional = true)
    @Column(name = "a_string", nullable = true, length = 2048)
    public String aString;

    @Convert(SecretEntity.CONVERTER)
    @Basic(optional = true)
    @Column(name = "a_uuid", nullable = true, length = 2048)
    public UUID aUuid;

    @Convert(SecretEntity.CONVERTER)
    @Basic(optional = true)
    @Column(name = "a_big_integer", nullable = true, length = 2048)
    public BigInteger aBigInteger;

    @Convert(SecretEntity.CONVERTER)
    @Basic(optional = true)
    @Column(name = "a_big_decimal", nullable = true, length = 2048)
    public BigDecimal aBigDecimal;

    @Convert(SecretEntity.CONVERTER)
    @Basic(optional = true)
    @Column(name = "a_local_date", nullable = true, length = 2048)
    public LocalDate aLocalDate;

    @Convert(SecretEntity.CONVERTER)
    @Basic(optional = true)
    @Column(name = "a_local_time", nullable = true, length = 2048)
    public LocalTime aLocalTime;

    @Convert(SecretEntity.CONVERTER)
    @Basic(optional = true)
    @Column(name = "a_local_date_time", nullable = true, length = 2048)
    public LocalDateTime aLocalDateTime;

    @Convert(SecretEntity.CONVERTER)
    @Basic(optional = true)
    @Column(name = "an_offset_time", nullable = true, length = 2048)
    public OffsetTime anOffsetTime;

    @Convert(SecretEntity.CONVERTER)
    @Basic(optional = true)
    @Column(name = "an_offset_date_time", nullable = true, length = 2048)
    public OffsetDateTime anOffsetDateTime;

    @Convert(SecretEntity.CONVERTER)
    @Basic(optional = true)
    @Column(name = "an_instant", nullable = true, length = 2048)
    public Instant anInstant;

    @Convert(SecretEntity.CONVERTER)
    @Basic(optional = true)
    @Column(name = "a_year", nullable = true, length = 2048)
    public Year aYear;

    @Convert(SecretEntity.CONVERTER)
    @Basic(optional = true)
    @Column(name = "a_util_date", nullable = true, length = 2048)
    public java.util.Date aUtilDate;

    @Convert(SecretEntity.CONVERTER)
    @Basic(optional = true)
    @Column(name = "a_sql_date", nullable = true, length = 2048)
    public java.sql.Date aSqlDate;

    @Convert(SecretEntity.CONVERTER)
    @Basic(optional = true)
    @Column(name = "a_sql_time", nullable = true, length = 2048)
    public java.sql.Time aSqlTime;

    @Convert(SecretEntity.CONVERTER)
    @Basic(optional = true)
    @Column(name = "a_sql_timestamp", nullable = true, length = 2048)
    public java.sql.Timestamp aSqlTimestamp;

    @Convert(SecretEntity.CONVERTER)
    @Basic(optional = true)
    @Column(name = "bytes", nullable = true, length = 2048)
    public byte[] bytes;

    @Convert(SecretEntity.CONVERTER)
    @Basic(optional = true)
    @Column(name = "bytes_wrapped", nullable = true, length = 2048)
    public Byte[] bytesWrapped;

    @Convert(SecretEntity.CONVERTER)
    @Basic(optional = true)
    @Column(name = "chars", nullable = true, length = 2048)
    public char[] chars;

    @Convert(SecretEntity.CONVERTER)
    @Basic(optional = true)
    @Column(name = "chars_wrapped", nullable = true, length = 2048)
    public Character[] charsWrapped;

    @Convert(SecretEntity.CONVERTER)
    @Basic(optional = true)
    @Column(name = "a_color", nullable = true, length = 2048)
    public Color aColor;

    @Convert(SecretEntity.CONVERTER)
    @Column(name = "an_int", nullable = true, length = 2048)
    public int anInt;
}
