package com.github.jinahya.persistence.more.converter.javautil;

/*-
 * #%L
 * jinahya-persistence-more
 * %%
 * Copyright (C) 2026 Jinahya, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.lang.invoke.MethodHandles;
import java.util.BitSet;

/**
 * A converter for an entity attribute of {@link BitSet}, stored as its {@link BitSet#toByteArray() bytes}.
 * <p>
 * A value is stored as {@link BitSet#toByteArray()} and read back with {@link BitSet#valueOf(byte[])}, which is its
 * inverse: bit {@code n} is bit {@code n % 8} of byte {@code n / 8}, in little-endian order, and the bytes end at the
 * last set bit, so an empty set is stored as no bytes, and reads back as an empty set.
 * <p>
 * It implements {@link AttributeConverter AttributeConverter&lt;BitSet, byte[]&gt;} itself, so that EclipseLink, which
 * takes a converter's column type from the type arguments of the interface the class names, finds both of them.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @apiNote A {@link BitSet} is mutable, and providers do not agree on a set changed in place: Hibernate ORM
 *         stores a bit set on a managed entity, and EclipseLink does not. For the change to be stored by every
 *         provider, copy the set, change the copy, and assign the copy to the attribute; the set the attribute held
 *         must stay as it was, for it is what the change is found against.
 */
@Converter(autoApply = false)
public class BitSetBytesAttributeConverter implements AttributeConverter<BitSet, byte[]> {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     *
     * @implNote {@code public}, not {@code protected}: a {@link Converter @Converter} class is instantiated by
     *         the persistence provider, and is documented as needing a public no-argument constructor.
     */
    public BitSetBytesAttributeConverter() {
        super();
    }

    // ---------------------------------------------------------------------------------------------- AttributeConverter

    /**
     * {@inheritDoc}
     *
     * @param attribute {@inheritDoc}
     * @return the bytes of the {@code attribute}; {@code null} when the {@code attribute} is {@code null}.
     */
    @Override
    public byte[] convertToDatabaseColumn(final BitSet attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.toByteArray();
    }

    /**
     * {@inheritDoc}
     *
     * @param dbData {@inheritDoc}
     * @return the bit set of the {@code dbData}; {@code null} when the {@code dbData} is {@code null}.
     */
    @Override
    public BitSet convertToEntityAttribute(final byte[] dbData) {
        if (dbData == null) {
            return null;
        }
        return BitSet.valueOf(dbData);
    }
}
