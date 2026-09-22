package com.github.jinahya.persistence.more.colormodel;

/*-
 * #%L
 * jinahya-persistence-more
 * %%
 * Copyright (C) 2025 - 2026 Jinahya, Inc.
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

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

import java.io.Serial;

/**
 * An abstract mapped superclass for colors in the
 * <a href="https://www.w3.org/TR/css-color-4/#numeric-srgb">sRGB</a> color model, with an alpha.
 * <p>
 * One column, {@value #COLUMN_NAME_ALPHA}, is added to those of {@link __MappedRgb}. It holds the alpha normalized
 * between {@value ___MappedColor#MIN_COMPONENT} and {@value ___MappedColor#MAX_COMPONENT}, both inclusive, and defaults
 * to {@value ___MappedColor#ALPHA_OPAQUE} — the value
 * <a href="https://www.w3.org/TR/css-color-4/#rgb-functions">CSS Color 4</a> gives an omitted alpha.
 * <p>
 * The alpha is not a component: {@link #getComponentCount()} still reports {@value __MappedRgb#COMPONENT_COUNT}, as it
 * does for {@link __MappedRgb}, and the alpha is reached through {@link #getAlpha()} alone. See {@link ___MappedColor}
 * for why.
 * <h2>{@code rgb()} and {@code rgba()}</h2>
 * The spec makes {@code rgb()} and {@code rgba()} exact aliases, each accepting both the legacy comma-separated syntax
 * and the modern whitespace-separated one; the {@code a} in the name carries no meaning of its own. That is why this
 * class exists only to add a column, and not to model a distinct notation — see {@link #toLegacyRgbNotation()} and
 * {@link #toModernRgbNotation()} for the two serializations.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="https://www.w3.org/TR/css-color-4/">CSS Color Module Level 4</a>
 * @see <a href="https://www.w3.org/TR/css-color-4/#typedef-legacy-rgba-syntax">CSS Color 4,
 *         &lt;legacy-rgba-syntax&gt;</a>
 */
// forced, so that an entity which puts its @Id on a getter cannot flip this hierarchy to
// property access and, with it, silently unmap every component below.
// the cost: EclipseLink then requires the entity to declare @Access(AccessType.FIELD) too.
@Access(AccessType.FIELD)
@MappedSuperclass
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public abstract class __MappedRgba extends __MappedRgb {

    @Serial
    private static final long serialVersionUID = -3055377264126889216L;

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The name of the column, {@value}, of the {@value #ATTRIBUTE_NAME_ALPHA} attribute.
     */
    public static final String COLUMN_NAME_ALPHA = "alpha";

    /**
     * The name of the attribute, {@value}, mapped to the {@value #COLUMN_NAME_ALPHA} column.
     */
    public static final String ATTRIBUTE_NAME_ALPHA = "alpha";

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance, fully opaque.
     */
    protected __MappedRgba() {
        super();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object

    @Override
    public String toString() {
        return super.toString() + '{' +
               "alpha=" + alpha +
               '}';
    }

    // ---------------------------------------------------------------------------------------------------------- alpha

    /**
     * {@inheritDoc}
     *
     * @return the current value of the {@value #ATTRIBUTE_NAME_ALPHA} attribute.
     */
    @Transient
    @Override
    public double getAlpha() {
        return alpha;
    }

    /**
     * Replaces the current value of the alpha with specified value.
     *
     * @param alpha new value for the alpha, between {@value ___MappedColor#MIN_COMPONENT} and
     *              {@value ___MappedColor#MAX_COMPONENT}, both inclusive.
     * @see <a href="https://www.w3.org/TR/css-color-4/#typedef-alpha-value">CSS Color 4, &lt;alpha-value&gt;</a>
     */
    public void setAlpha(final double alpha) {
        this.alpha = requireValidComponent(alpha, ATTRIBUTE_NAME_ALPHA);
    }

    /**
     * Returns the current value of the alpha, denormalized onto eight bits.
     *
     * @return a value between {@code 0} and {@value ___MappedColor#MAX_COMPONENT_8_BIT}, both inclusive.
     */
    @Transient
    public int getAlphaAsEightBits() {
        return toEightBits(getAlpha());
    }

    /**
     * Replaces the current value of the alpha with specified eight-bit value.
     *
     * @param alpha new value for the alpha, between {@code 0} and {@value ___MappedColor#MAX_COMPONENT_8_BIT}, both
     *              inclusive.
     */
    public void setAlphaAsEightBits(final int alpha) {
        setAlpha(fromEightBits(alpha, ATTRIBUTE_NAME_ALPHA));
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The alpha of this color, normalized, mapped to the {@value #COLUMN_NAME_ALPHA} column. Not a component; it
     * defaults to fully opaque.
     */
    @DecimalMax(DECIMAL_MAX_COMPONENT)
    @DecimalMin(DECIMAL_MIN_COMPONENT)
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_ALPHA, nullable = false, insertable = true, updatable = true)
    private double alpha = ALPHA_OPAQUE;
}
