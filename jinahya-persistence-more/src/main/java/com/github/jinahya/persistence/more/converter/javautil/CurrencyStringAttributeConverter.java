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
import java.util.Currency;

/**
 * A converter for an entity attribute of {@link Currency}, stored as its ISO 4217
 * {@link Currency#getCurrencyCode() code}.
 * <p>
 * A value is stored as {@link Currency#getCurrencyCode()} &mdash; {@code "KRW"}, {@code "USD"} &mdash; and read back
 * with {@link Currency#getInstance(String)}, which is its inverse: a currency has one instance for its code.
 * <p>
 * It implements {@link AttributeConverter AttributeConverter&lt;Currency, String&gt;} itself, not the parent's column
 * axis, {@link com.github.jinahya.persistence.more.converter.StringAttributeConverter}: EclipseLink takes a
 * converter's column type from the type arguments of the interface the class names, as
 * {@link com.github.jinahya.persistence.more.converter.javatime.ZoneIdStringAttributeConverter} explains.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Converter(autoApply = false)
public class CurrencyStringAttributeConverter implements AttributeConverter<Currency, String> {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     *
     * @implNote {@code public}, not {@code protected}: a {@link Converter @Converter} class is instantiated by
     *         the persistence provider, and is documented as needing a public no-argument constructor.
     */
    public CurrencyStringAttributeConverter() {
        super();
    }

    // ---------------------------------------------------------------------------------------------- AttributeConverter

    /**
     * {@inheritDoc}
     *
     * @param attribute {@inheritDoc}
     * @return the ISO 4217 code of the {@code attribute}; {@code null} when the {@code attribute} is {@code null}.
     */
    @Override
    public String convertToDatabaseColumn(final Currency attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getCurrencyCode();
    }

    /**
     * {@inheritDoc}
     *
     * @param dbData {@inheritDoc}
     * @return the currency of the {@code dbData}; {@code null} when the {@code dbData} is {@code null}.
     * @throws IllegalArgumentException when the {@code dbData} is not an ISO 4217 code the running JDK supports, in
     *                                  upper case.
     */
    @Override
    public Currency convertToEntityAttribute(final String dbData) {
        if (dbData == null) {
            return null;
        }
        return Currency.getInstance(dbData);
    }
}
