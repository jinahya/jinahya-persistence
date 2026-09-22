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
import java.util.function.DoubleFunction;

/**
 * An abstract mapped superclass for colors in the
 * <a href="https://www.w3.org/TR/css-color-4/#the-hsl-notation">HSL</a> color model.
 * <p>
 * Three columns are mapped: {@value ___MappedHueColor#COLUMN_NAME_HUE}, inherited and held in degrees, plus
 * {@value #COLUMN_NAME_SATURATION} and {@value #COLUMN_NAME_LIGHTNESS}, each normalized between
 * {@value ___MappedColor#MIN_COMPONENT} and {@value ___MappedColor#MAX_COMPONENT}, both inclusive.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="https://www.w3.org/TR/css-color-4/">CSS Color Module Level 4</a>
 * @see <a href="https://www.w3.org/TR/css-color-4/#the-hsl-notation">CSS Color 4, &sect;7 HSL Colors</a>
 * @see ___MappedColorUtils#hslToRgb(double, double, double, DoubleFunction)
 */
// forced, so that an entity which puts its @Id on a getter cannot flip this hierarchy to
// property access and, with it, silently unmap every component below.
// the cost: EclipseLink then requires the entity to declare @Access(AccessType.FIELD) too.
@Access(AccessType.FIELD)
@MappedSuperclass
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public abstract class __MappedHsl extends ___MappedHueColor {

    @Serial
    private static final long serialVersionUID = -8420570677180334911L;

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The number of components, {@value}, a color in this model consists of.
     */
    public static final int COMPONENT_COUNT = 3;

    /**
     * The index, {@value}, of the saturation component.
     */
    public static final int COMPONENT_INDEX_SATURATION = 1;

    /**
     * The index, {@value}, of the lightness component.
     */
    public static final int COMPONENT_INDEX_LIGHTNESS = 2;

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The name of the column, {@value}, of the {@value #ATTRIBUTE_NAME_SATURATION} attribute.
     */
    public static final String COLUMN_NAME_SATURATION = "saturation";

    /**
     * The name of the attribute, {@value}, mapped to the {@value #COLUMN_NAME_SATURATION} column.
     */
    public static final String ATTRIBUTE_NAME_SATURATION = "saturation";

    /**
     * The name of the column, {@value}, of the {@value #ATTRIBUTE_NAME_LIGHTNESS} attribute.
     */
    public static final String COLUMN_NAME_LIGHTNESS = "lightness";

    /**
     * The name of the attribute, {@value}, mapped to the {@value #COLUMN_NAME_LIGHTNESS} column.
     */
    public static final String ATTRIBUTE_NAME_LIGHTNESS = "lightness";

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     *
     * @see <a href="https://www.w3.org/TR/css-color-4/#hsl-to-rgb">CSS Color 4, &sect;7.1 Converting HSL Colors to
     *         sRGB</a>
     */
    protected __MappedHsl() {
        super();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object

    @Override
    public String toString() {
        return super.toString() + '{' +
               "saturation=" + saturation +
               ",lightness=" + lightness +
               '}';
    }

    // ----------------------------------------------------------------------------------------------------- COMPONENTS

    @Transient
    @Override
    public int getComponentCount() {
        return COMPONENT_COUNT;
    }

    @Transient
    @Override
    public double getComponent(final int index) {
        return switch (requireValidComponentIndex(index)) {
            case COMPONENT_INDEX_HUE -> getNormalizedHue();
            case COMPONENT_INDEX_SATURATION -> getSaturation();
            case COMPONENT_INDEX_LIGHTNESS -> getLightness();
            default -> throw new IndexOutOfBoundsException("index: " + index);
        };
    }

    @Override
    public void setComponent(final int index, final double component) {
        switch (requireValidComponentIndex(index)) {
            case COMPONENT_INDEX_HUE -> setNormalizedHue(component);
            case COMPONENT_INDEX_SATURATION -> setSaturation(component);
            case COMPONENT_INDEX_LIGHTNESS -> setLightness(component);
            default -> throw new IndexOutOfBoundsException("index: " + index);
        }
    }

    @Override
    public <R> R applySrgb(
            final DoubleFunction<? extends DoubleFunction<? extends DoubleFunction<? extends R>>> function) {
        return ___MappedColorUtils.hslToRgb(getHue(), getSaturation(), getLightness(), function);
    }

    /**
     * {@inheritDoc}
     * <p>
     * A gray has no hue to recover. CSS Color 4 marks the hue <a href="https://www.w3.org/TR/css-color-4/#missing">
     * missing</a> there; this class stores {@value ___MappedHueColor#MIN_HUE}, which is how a missing hue behaves when
     * it is used. The saturation and the lightness are clamped, so that a value landing a hair outside the range
     * through rounding is stored rather than rejected.
     *
     * @param r {@inheritDoc}
     * @param g {@inheritDoc}
     * @param b {@inheritDoc}
     * @see <a href="https://www.w3.org/TR/css-color-4/#rgb-to-hsl">CSS Color 4, &sect;7.2 Converting sRGB Colors to
     *         HSL</a>
     */
    @Override
    public void setSrgb(final double r, final double g, final double b) {
        ___MappedColorUtils.rgbToHsl(
                requireValidComponent(r, "r"),
                requireValidComponent(g, "g"),
                requireValidComponent(b, "b"),
                h -> s -> l -> {
                    setHue(h);
                    setSaturation(clampComponent(s));
                    setLightness(clampComponent(l));
                    return null;
                }
        );
    }

    // ------------------------------------------------------------------------------------------------------- NOTATION

    /**
     * Returns this color in the
     * <a href="https://www.w3.org/TR/css-color-4/#typedef-legacy-hsl-syntax">legacy {@code hsl()}/{@code hsla()}
     * syntax</a> of CSS Color 4 — components separated by commas, and a distinct function name when the alpha is
     * carried.
     * <p>
     * Unlike {@code rgb()}, the legacy {@code hsl()} syntax has no all-number form: the saturation and the lightness
     * are always percentages, and only the hue is a number.
     * <p>
     * {@snippet lang = "none":
     * hsl(120, 100%, 50%)
     * hsla(120, 100%, 50%, 0.5)
     *}
     *
     * @return this color in the legacy syntax.
     * @see #toModernHslNotation()
     */
    @Transient
    public String toLegacyHslNotation() {
        final var components = formatNumber(getHue()) + ", " + formatPercentage(getSaturation()) + ", " +
                               formatPercentage(getLightness());
        return isOpaque()
                ? "hsl(" + components + ')'
                : "hsla(" + components + ", " + formatAlpha(getAlpha()) + ')';
    }

    /**
     * Returns this color in the
     * <a href="https://www.w3.org/TR/css-color-4/#typedef-modern-hsl-syntax">modern {@code hsl()} syntax</a> of CSS
     * Color 4 — components separated by whitespace, and the alpha, when carried, separated by a solidus.
     * <p>
     * Unlike {@link #toModernRgbNotation()}, this notation is lossless: it carries the hue, saturation and lightness
     * this color actually stores, rather than their eight-bit sRGB rendering.
     * <p>
     * {@snippet lang = "none":
     * hsl(120 100% 50%)
     * hsl(120 100% 50% / 0.5)
     *}
     *
     * @return this color in the modern syntax.
     * @see #toLegacyHslNotation()
     */
    @Transient
    public String toModernHslNotation() {
        final var components = formatNumber(getHue()) + ' ' + formatPercentage(getSaturation()) + ' ' +
                               formatPercentage(getLightness());
        return isOpaque()
                ? "hsl(" + components + ')'
                : "hsl(" + components + " / " + formatAlpha(getAlpha()) + ')';
    }

    // ----------------------------------------------------------------------------------------------------- saturation

    /**
     * Returns the current value of the saturation.
     *
     * @return a value between {@value ___MappedColor#MIN_COMPONENT} and {@value ___MappedColor#MAX_COMPONENT}, both
     *         inclusive.
     */
    public double getSaturation() {
        return saturation;
    }

    /**
     * Replaces the current value of the saturation with specified value.
     *
     * @param saturation new value for the saturation.
     */
    public void setSaturation(final double saturation) {
        this.saturation = requireValidComponent(saturation, ATTRIBUTE_NAME_SATURATION);
    }

    // ------------------------------------------------------------------------------------------------------ lightness

    /**
     * Returns the current value of the lightness.
     *
     * @return a value between {@value ___MappedColor#MIN_COMPONENT} and {@value ___MappedColor#MAX_COMPONENT}, both
     *         inclusive.
     */
    public double getLightness() {
        return lightness;
    }

    /**
     * Replaces the current value of the lightness with specified value.
     *
     * @param lightness new value for the lightness.
     */
    public void setLightness(final double lightness) {
        this.lightness = requireValidComponent(lightness, ATTRIBUTE_NAME_LIGHTNESS);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The saturation component of this color, normalized, mapped to the {@value #COLUMN_NAME_SATURATION} column.
     */
    @DecimalMax(DECIMAL_MAX_COMPONENT)
    @DecimalMin(DECIMAL_MIN_COMPONENT)
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_SATURATION, nullable = false, insertable = true, updatable = true)
    private double saturation;

    /**
     * The lightness component of this color, normalized, mapped to the {@value #COLUMN_NAME_LIGHTNESS} column.
     */
    @DecimalMax(DECIMAL_MAX_COMPONENT)
    @DecimalMin(DECIMAL_MIN_COMPONENT)
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_LIGHTNESS, nullable = false, insertable = true, updatable = true)
    private double lightness;
}
