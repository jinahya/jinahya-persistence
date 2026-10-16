package com.github.jinahya.persistence.encryption.eclipselink.examples;

import com.github.jinahya.persistence.encryption.converter.EncryptedStringConverter;
import com.github.jinahya.persistence.encryption.eclipselink.EncryptedConverter;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.eclipse.persistence.annotations.Converter;

/**
 * The native mapping and the standard mapping side by side, on two columns of one row: the bytes are the same.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Converter(name = MixedEntity.CONVERTER, converterClass = EncryptedConverter.class)
@Access(AccessType.FIELD)
@Entity
@Table(name = MixedEntity.TABLE_NAME)
public class MixedEntity {

    public static final String TABLE_NAME = "mixed_entity";

    public static final String CONVERTER = "encrypted";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long id;

    @org.eclipse.persistence.annotations.Convert(CONVERTER)
    @Basic(optional = true)
    @Column(name = "native_enc", nullable = true, length = 2048)
    public String nativeName;

    @jakarta.persistence.Convert(converter = EncryptedStringConverter.class)
    @Basic(optional = true)
    @Column(name = "standard_enc", nullable = true, length = 2048)
    public String standardName;
}
