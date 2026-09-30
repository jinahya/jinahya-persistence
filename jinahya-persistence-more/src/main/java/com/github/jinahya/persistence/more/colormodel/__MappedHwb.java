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
 * <a href="https://www.w3.org/TR/css-color-4/#the-hwb-notation">HWB</a> color model.
 * <p>
 * Three columns are mapped: {@value ___MappedHueColor#COLUMN_NAME_HUE}, inherited and held in degrees, plus
 * {@value #COLUMN_NAME_WHITENESS} and {@value #COLUMN_NAME_BLACKNESS}, each normalized between
 * {@value ___MappedColor#MIN_COMPONENT} and {@value ___MappedColor#MAX_COMPONENT}, both inclusive.
 * <p>
 * A whiteness and a blackness summing to {@value ___MappedColor#MAX_COMPONENT} or more denote a gray, and the hue is
 * then immaterial; the conversion to sRGB handles that case as the spec prescribes, rather than rejecting it.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="https://www.w3.org/TR/css-color-4/">CSS Color Module Level 4</a>
 * @see <a href="https://www.w3.org/TR/css-color-4/#the-hwb-notation">CSS Color 4, &sect;8 HWB Colors</a>
 * @see ___MappedColorUtils#hwbToRgb(double, double, double, DoubleFunction)
 */
// forced, so that an entity which puts its @Id on a getter cannot flip this hierarchy to
// property access and, with it, silently unmap every component below.
// the cost: EclipseLink then requires the entity to declare @Access(AccessType.FIELD) too.
@Access(AccessType.FIELD)
@MappedSuperclass
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public abstract class __MappedHwb extends ___MappedHueColor {

    @Serial
    private static final long serialVersionUID = 6284069818616861963L;

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The number of components, {@value}, a color in this model consists of.
     */
    public static final int COMPONENT_COUNT = 3;

    /**
     * The index, {@value}, of the whiteness component.
     */
    public static final int COMPONENT_INDEX_WHITENESS = 1;

    /**
     * The index, {@value}, of the blackness component.
     */
    public static final int COMPONENT_INDEX_BLACKNESS = 2;

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The name of the column, {@value}, of the {@value #ATTRIBUTE_NAME_WHITENESS} attribute.
     */
    public static final String COLUMN_NAME_WHITENESS = "whiteness";

    /**
     * The name of the attribute, {@value}, mapped to the {@value #COLUMN_NAME_WHITENESS} column.
     */
    public static final String ATTRIBUTE_NAME_WHITENESS = "whiteness";

    /**
     * The name of the column, {@value}, of the {@value #ATTRIBUTE_NAME_BLACKNESS} attribute.
     */
    public static final String COLUMN_NAME_BLACKNESS = "blackness";

    /**
     * The name of the attribute, {@value}, mapped to the {@value #COLUMN_NAME_BLACKNESS} column.
     */
    public static final String ATTRIBUTE_NAME_BLACKNESS = "blackness";

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     *
     * @see <a href="https://www.w3.org/TR/css-color-4/#hwb-to-rgb">CSS Color 4, &sect;8.1 Converting HWB Colors to
     *         sRGB</a>
     */
    protected __MappedHwb() {
        super();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object

    /**
     * Returns a string representation of this color.
     *
     * @return a string representation of this color; that of the superclass, which carries the hue, followed by the
     *         {@value #ATTRIBUTE_NAME_WHITENESS} and {@value #ATTRIBUTE_NAME_BLACKNESS} attributes.
     * @implNote The format is unspecified, and is meant for diagnostics rather than for parsing; see
     *         {@link #toHwbNotation()} for the CSS notation.
     */
    @Override
    public String toString() {
        return super.toString() + '{' +
               "whiteness=" + whiteness +
               ",blackness=" + blackness +
               '}';
    }

    // ----------------------------------------------------------------------------------------------------- COMPONENTS

    /**
     * {@inheritDoc}
     *
     * @return {@value #COMPONENT_COUNT}; the hue, the whiteness and the blackness. The hue is declared by
     *         {@link ___MappedHueColor}, the other two here.
     */
    @Transient
    @Override
    public int getComponentCount() {
        return COMPONENT_COUNT;
    }

    /**
     * {@inheritDoc}
     *
     * @param index {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @implSpec {@value ___MappedHueColor#COMPONENT_INDEX_HUE} reads {@link #getNormalizedHue()} &mdash; the
     *         <em>normalized</em> hue, not the one in degrees {@link #getHue()} returns, because this method speaks
     *         only in normalized values &mdash; {@value #COMPONENT_INDEX_WHITENESS} reads {@link #getWhiteness()},
     *         and {@value #COMPONENT_INDEX_BLACKNESS} reads {@link #getBlackness()}.
     */
    @Transient
    @Override
    public double getComponent(final int index) {
        return switch (requireValidComponentIndex(index)) {
            case COMPONENT_INDEX_HUE -> getNormalizedHue();
            case COMPONENT_INDEX_WHITENESS -> getWhiteness();
            case COMPONENT_INDEX_BLACKNESS -> getBlackness();
            default -> throw new IndexOutOfBoundsException("index: " + index);
        };
    }

    /**
     * {@inheritDoc}
     *
     * @param index     {@inheritDoc}
     * @param component {@inheritDoc}
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @throws IllegalArgumentException  {@inheritDoc}
     * @implSpec {@value ___MappedHueColor#COMPONENT_INDEX_HUE} writes {@link #setNormalizedHue(double)},
     *         {@value #COMPONENT_INDEX_WHITENESS} writes {@link #setWhiteness(double)}, and
     *         {@value #COMPONENT_INDEX_BLACKNESS} writes {@link #setBlackness(double)}.
     */
    @Override
    public void setComponent(final int index, final double component) {
        switch (requireValidComponentIndex(index)) {
            case COMPONENT_INDEX_HUE -> setNormalizedHue(component);
            case COMPONENT_INDEX_WHITENESS -> setWhiteness(component);
            case COMPONENT_INDEX_BLACKNESS -> setBlackness(component);
            default -> throw new IndexOutOfBoundsException("index: " + index);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * The conversion is the one CSS Color 4 specifies. A whiteness and a blackness which sum to {@code 1.0} or more
     * make a gray, whatever the hue, and the hue is then simply not consulted.
     *
     * @param function {@inheritDoc}
     * @param <R>      {@inheritDoc}
     * @return {@inheritDoc}
     * @see ___MappedColorUtils#hwbToRgb(double, double, double, DoubleFunction)
     * @see <a href="https://www.w3.org/TR/css-color-4/#hwb-to-rgb">CSS Color 4, &sect;8.1 Converting HWB Colors to
     *         sRGB</a>
     */
    @Override
    public <R> R applySrgb(
            final DoubleFunction<? extends DoubleFunction<? extends DoubleFunction<? extends R>>> function) {
        return ___MappedColorUtils.hwbToRgb(getHue(), getWhiteness(), getBlackness(), function);
    }

    /**
     * {@inheritDoc}
     * <p>
     * The whiteness and the blackness are clamped, so that a value landing a hair outside the range through rounding is
     * stored rather than rejected.
     *
     * @param r {@inheritDoc}
     * @param g {@inheritDoc}
     * @param b {@inheritDoc}
     * @see <a href="https://www.w3.org/TR/css-color-4/#rgb-to-hwb">CSS Color 4, &sect;8.2 Converting sRGB Colors to
     *         HWB</a>
     */
    @Override
    public void setSrgb(final double r, final double g, final double b) {
        ___MappedColorUtils.rgbToHwb(
                requireValidComponent(r, "r"),
                requireValidComponent(g, "g"),
                requireValidComponent(b, "b"),
                h -> w -> k -> {
                    setHue(h);
                    setWhiteness(clampComponent(w));
                    setBlackness(clampComponent(k));
                    return null;
                }
        );
    }

    // ------------------------------------------------------------------------------------------------------- NOTATION

    /**
     * Returns this color in the <a href="https://www.w3.org/TR/css-color-4/#the-hwb-notation">{@code hwb()} syntax</a>
     * of CSS Color 4.
     * <p>
     * There is only one, and no {@code hwba()}: {@code hwb()} postdates the legacy comma-separated forms, so its
     * grammar admits only whitespace-separated components and a solidus before the alpha. That is why this method has
     * no {@code Legacy}/{@code Modern} pair, unlike {@link #toLegacyRgbNotation()} and {@link #toModernRgbNotation()}.
     * <p>
     * This notation is lossless, where the {@code rgb()} ones carry only an eight-bit sRGB rendering.
     * <p>
     * {@snippet lang = "none":
     * hwb(120 0% 0%)
     * hwb(120 0% 0% / 0.5)
     *}
     *
     * @return this color in the {@code hwb()} syntax.
     */
    @Transient
    public String toHwbNotation() {
        final var components = formatNumber(getHue()) + ' ' + formatPercentage(getWhiteness()) + ' ' +
                               formatPercentage(getBlackness());
        return isOpaque()
                ? "hwb(" + components + ')'
                : "hwb(" + components + " / " + formatAlpha(getAlpha()) + ')';
    }

    // ------------------------------------------------------------------------------------------------------ whiteness

    /**
     * Returns the current value of the whiteness.
     *
     * @return a value between {@value ___MappedColor#MIN_COMPONENT} and {@value ___MappedColor#MAX_COMPONENT}, both
     *         inclusive.
     */
    public double getWhiteness() {
        return whiteness;
    }

    /**
     * Replaces the current value of the whiteness with specified value.
     *
     * @param whiteness new value for the whiteness.
     */
    public void setWhiteness(final double whiteness) {
        this.whiteness = requireValidComponent(whiteness, ATTRIBUTE_NAME_WHITENESS);
    }

    // ------------------------------------------------------------------------------------------------------ blackness

    /**
     * Returns the current value of the blackness.
     *
     * @return a value between {@value ___MappedColor#MIN_COMPONENT} and {@value ___MappedColor#MAX_COMPONENT}, both
     *         inclusive.
     */
    public double getBlackness() {
        return blackness;
    }

    /**
     * Replaces the current value of the blackness with specified value.
     *
     * @param blackness new value for the blackness.
     */
    public void setBlackness(final double blackness) {
        this.blackness = requireValidComponent(blackness, ATTRIBUTE_NAME_BLACKNESS);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The whiteness component of this color, normalized, mapped to the {@value #COLUMN_NAME_WHITENESS} column.
     */
    @DecimalMax(DECIMAL_MAX_COMPONENT)
    @DecimalMin(DECIMAL_MIN_COMPONENT)
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_WHITENESS, nullable = false, insertable = true, updatable = true)
    private double whiteness;

    /**
     * The blackness component of this color, normalized, mapped to the {@value #COLUMN_NAME_BLACKNESS} column.
     */
    @DecimalMax(DECIMAL_MAX_COMPONENT)
    @DecimalMin(DECIMAL_MIN_COMPONENT)
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_BLACKNESS, nullable = false, insertable = true, updatable = true)
    private double blackness;
}
