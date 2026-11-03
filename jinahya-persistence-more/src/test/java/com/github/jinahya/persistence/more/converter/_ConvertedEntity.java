package com.github.jinahya.persistence.more.converter;

import com.github.jinahya.persistence.more.converter.javanet.UriStringAttributeConverter;
import com.github.jinahya.persistence.more.converter.javaniocharset.CharsetStringAttributeConverter;
import com.github.jinahya.persistence.more.converter.javatime.ZoneOffsetStringAttributeConverter;
import com.github.jinahya.persistence.more.converter.javautil.BitSetBytesAttributeConverter;
import com.github.jinahya.persistence.more.converter.javautil.CurrencyStringAttributeConverter;
import com.github.jinahya.persistence.more.converter.javautil.UuidAttributeConverter;
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
import java.net.URI;
import java.nio.charset.Charset;
import java.time.ZoneOffset;
import java.util.BitSet;
import java.util.Currency;
import java.util.UUID;

/**
 * An entity which registers {@link __NumberStringAttributeConverters.OfBigDecimal},
 * {@link UuidAttributeConverter.OfString}, {@link UuidAttributeConverter.OfBytes} and
 * {@link UuidAttributeConverter.OfHexString}, and the other converters of {@code java} types, on real attributes, so
 * that the provider — not a unit test — is what instantiates the converters.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Entity
@Table(name = "converted_entity")
public class _ConvertedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    @Convert(converter = __NumberStringAttributeConverters.OfBigDecimal.class)
    @Basic(optional = true)
    @Column(name = "amount", nullable = true, length = 64, columnDefinition = "VARCHAR(64)")
    public BigDecimal amount;

    @Convert(converter = UuidAttributeConverter.OfString.class)
    @Basic(optional = true)
    @Column(name = "uuid_text", nullable = true, length = 36, columnDefinition = "VARCHAR(36)")
    public UUID uuidText;

    @Convert(converter = UuidAttributeConverter.OfBytes.class)
    @Basic(optional = true)
    @Column(name = "uuid_bytes", nullable = true, length = 16, columnDefinition = "BINARY(16)")
    public UUID uuidBytes;

    @Convert(converter = UuidAttributeConverter.OfHexString.class)
    @Basic(optional = true)
    @Column(name = "uuid_hex", nullable = true, length = 32, columnDefinition = "VARCHAR(32)")
    public UUID uuidHex;

    @Convert(converter = __NumberStringAttributeConverters.OfBigInteger.class)
    @Basic(optional = true)
    @Column(name = "big_integer", nullable = true, length = 64, columnDefinition = "VARCHAR(64)")
    public BigInteger bigInteger;

    @Convert(converter = __NumberStringAttributeConverters.OfByte.class)
    @Basic(optional = true)
    @Column(name = "byte_text", nullable = true, length = 4, columnDefinition = "VARCHAR(4)")
    public Byte byteText;

    @Convert(converter = __NumberStringAttributeConverters.OfShort.class)
    @Basic(optional = true)
    @Column(name = "short_text", nullable = true, length = 6, columnDefinition = "VARCHAR(6)")
    public Short shortText;

    @Convert(converter = CurrencyStringAttributeConverter.class)
    @Basic(optional = true)
    @Column(name = "currency", nullable = true, length = 3, columnDefinition = "CHAR(3)")
    public Currency currency;

    @Convert(converter = UriStringAttributeConverter.class)
    @Basic(optional = true)
    @Column(name = "uri", nullable = true, length = 255, columnDefinition = "VARCHAR(255)")
    public URI uri;

    @Convert(converter = CharsetStringAttributeConverter.class)
    @Basic(optional = true)
    @Column(name = "charset", nullable = true, length = 64, columnDefinition = "VARCHAR(64)")
    public Charset charset;

    @Convert(converter = ZoneOffsetStringAttributeConverter.class)
    @Basic(optional = true)
    @Column(name = "zone_offset", nullable = true, length = 9, columnDefinition = "VARCHAR(9)")
    public ZoneOffset zoneOffset;

    @Convert(converter = BitSetBytesAttributeConverter.class)
    @Basic(optional = true)
    @Column(name = "bit_set", nullable = true, length = 128, columnDefinition = "VARBINARY(128)")
    public BitSet bitSet;
}
