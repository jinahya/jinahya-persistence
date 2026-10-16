package com.github.jinahya.persistence.encryption.eclipselink.examples;

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
import jakarta.persistence.Version;
import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.Converter;
import org.eclipse.persistence.annotations.Mutable;

/**
 * An array marked {@code @Mutable}, on its own entity: the mapping compares the converted forms, which are
 * randomized, so every flush of this entity writes it. Measured by {@code EncryptedConverter_Test.mutableArray}, and
 * kept apart so that it does not touch the other fixtures' versions.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Converter(name = MutableArrayEntity.CONVERTER, converterClass = EncryptedConverter.class)
@Access(AccessType.FIELD)
@Entity
@Table(name = MutableArrayEntity.TABLE_NAME)
public class MutableArrayEntity {

    public static final String TABLE_NAME = "mutable_array_entity";

    public static final String CONVERTER = "encrypted";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long id;

    @Version
    @Column(name = "version")
    public Integer version;

    @Mutable
    @Convert(CONVERTER)
    @Basic(optional = true)
    @Column(name = "bytes_enc", nullable = true, length = 2048)
    public byte[] bytes;
}
