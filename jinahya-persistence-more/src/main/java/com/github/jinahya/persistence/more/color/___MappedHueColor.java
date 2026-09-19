package com.github.jinahya.persistence.more.color;

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
 * An abstract mapped superclass for colors whose first component is a hue.
 * <p>
 * One column, {@value #COLUMN_NAME_HUE}, is mapped, holding the hue in <em>degrees</em> — the unit
 * <a href="https://www.w3.org/TR/css-color-4/#typedef-hue">CSS Color 4</a> uses — between {@value #MIN_HUE},
 * inclusive, and {@value #MAX_HUE}, exclusive. It is the one component in this package whose column is not normalized;
 * {@link #getComponent(int)} still reports it normalized, as the contract of {@link ___MappedColor} requires.
 * <p>
 * The hue bounds live here, next to the column they constrain, in the same four shapes {@link ___MappedColor} gives the
 * component bounds: a {@code double} pair for callers, and a {@code String} pair for the constraints. Both subclasses
 * use them, and {@link ___MappedColorUtils} reads them rather than keeping its own.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="https://www.w3.org/TR/css-color-4/">CSS Color Module Level 4</a>
 * @see <a href="https://www.w3.org/TR/css-color-4/#typedef-hue">CSS Color 4, &lt;hue&gt;</a>
 * @see __MappedHsl
 * @see __MappedHwb
 */
// forced, so that an entity which puts its @Id on a getter cannot flip this hierarchy to
// property access and, with it, silently unmap every component below.
// the cost: EclipseLink then requires the entity to declare @Access(AccessType.FIELD) too.
@Access(AccessType.FIELD)
@MappedSuperclass
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public abstract class ___MappedHueColor extends ___MappedColor {

    @Serial
    private static final long serialVersionUID = 5148340843748231186L;

    /**
     * The index, {@value}, of the hue component.
     */
    public static final int COMPONENT_INDEX_HUE = 0;

    /**
     * The minimum value, {@value}, of a hue, in degrees; inclusive.
     */
    public static final double MIN_HUE = 0.0d;

    /**
     * The maximum value, {@value}, of a hue, in degrees; <em>exclusive</em> — a hue of {@value} is the same angle as a
     * hue of {@value #MIN_HUE}, and {@link #setHue(double)} reduces it accordingly.
     */
    public static final double MAX_HUE = 360.0d;

    /**
     * The value, {@value}, of {@link #MIN_HUE} for {@link DecimalMin @DecimalMin}.
     */
    public static final String DECIMAL_MIN_HUE = "0.0";

    /**
     * The value, {@value}, of {@link #MAX_HUE} for {@link DecimalMax @DecimalMax}; applied as exclusive.
     */
    public static final String DECIMAL_MAX_HUE = "360.0";

    /**
     * The name of the column, {@value}, of the {@value #ATTRIBUTE_NAME_HUE} attribute.
     */
    public static final String COLUMN_NAME_HUE = "hue";

    /**
     * The name of the attribute, {@value}, mapped to the {@value #COLUMN_NAME_HUE} column.
     */
    public static final String ATTRIBUTE_NAME_HUE = "hue";

    // -------------------------------------------------------------------------------------------------------- HELPERS

    /**
     * Reduces specified hue onto {@code [}{@value #MIN_HUE}{@code , }{@value #MAX_HUE}{@code )}.
     * <p>
     * A hue is an angle, so no value is out of range; {@code -90} and {@code 630} are both {@code 270}.
     *
     * @param hue a hue, in degrees.
     * @return the reduced hue.
     * @throws IllegalArgumentException when the {@code hue} is {@link Double#NaN} or infinite.
     * @see <a href="https://www.w3.org/TR/css-color-4/#typedef-hue">CSS Color 4, &lt;hue&gt;</a>
     */
    protected static double reduceHue(final double hue) {
        if (!Double.isFinite(hue)) {
            throw new IllegalArgumentException("invalid hue: " + hue);
        }
        final var reduced = ((hue % MAX_HUE) + MAX_HUE) % MAX_HUE;
        // a tiny negative hue reduces, in binary, onto MAX_HUE itself; the range is half-open
        return reduced < MAX_HUE ? reduced : MIN_HUE;
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected ___MappedHueColor() {
        super();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object

    @Override
    public String toString() {
        return super.toString() + '{' +
               "hue=" + hue +
               '}';
    }

    // ------------------------------------------------------------------------------------------------------------ hue

    /**
     * Returns the current value of the hue, in degrees.
     *
     * @return a value between {@value #MIN_HUE}, inclusive, and {@value #MAX_HUE}, exclusive.
     */
    public double getHue() {
        return hue;
    }

    /**
     * Replaces the current value of the hue with specified value, in degrees.
     * <p>
     * The value is reduced onto {@code [}{@value #MIN_HUE}{@code , }{@value #MAX_HUE}{@code )} rather than rejected: a
     * hue is an angle, and no angle is out of range.
     *
     * @param hue new value for the hue, in degrees.
     * @throws IllegalArgumentException when the {@code hue} is {@link Double#NaN} or infinite.
     * @see #reduceHue(double)
     * @see <a href="https://www.w3.org/TR/css-color-4/#typedef-hue">CSS Color 4, &lt;hue&gt;</a>
     */
    public void setHue(final double hue) {
        this.hue = reduceHue(hue);
    }

    /**
     * Returns the current value of the hue, normalized.
     *
     * @return a value between {@value ___MappedColor#MIN_COMPONENT}, inclusive, and
     *         {@value ___MappedColor#MAX_COMPONENT}, exclusive.
     */
    @Transient
    public double getNormalizedHue() {
        return getHue() / MAX_HUE;
    }

    /**
     * Replaces the current value of the hue with specified normalized value.
     *
     * @param normalizedHue new value for the hue, between {@value ___MappedColor#MIN_COMPONENT} and
     *                      {@value ___MappedColor#MAX_COMPONENT}, both inclusive.
     * @throws IllegalArgumentException when the {@code normalizedHue} is out of range.
     */
    public void setNormalizedHue(final double normalizedHue) {
        setHue(requireValidComponent(normalizedHue, ATTRIBUTE_NAME_HUE) * MAX_HUE);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The hue of this color, in degrees rather than normalized &mdash; the unit CSS Color 4 gives it &mdash; mapped to
     * the {@value #COLUMN_NAME_HUE} column.
     */
    @DecimalMax(value = DECIMAL_MAX_HUE, inclusive = false)
    @DecimalMin(DECIMAL_MIN_HUE)
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_HUE, nullable = false, insertable = true, updatable = true)
    private double hue;
}
