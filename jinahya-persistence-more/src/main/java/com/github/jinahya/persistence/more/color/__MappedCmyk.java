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
import java.util.function.DoubleFunction;

/**
 * An abstract mapped superclass for colors in the CMYK color model.
 * <p>
 * Four columns are mapped — {@value #COLUMN_NAME_CYAN}, {@value #COLUMN_NAME_MAGENTA}, {@value #COLUMN_NAME_YELLOW} and
 * {@value #COLUMN_NAME_BLACK} — each holding a component normalized between {@value ___MappedColor#MIN_COMPONENT} and
 * {@value ___MappedColor#MAX_COMPONENT}, both inclusive.
 * <p>
 * A CMYK color is device-dependent. {@link #applySrgb(DoubleFunction)} therefore uses the naive, uncalibrated
 * conversion CSS Color 5 specifies for
 * <a href="https://www.w3.org/TR/css-color-5/#device-cmyk">{@code device-cmyk()}</a> when no color profile is known;
 * it is a rendering of convenience, not a colorimetric one.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="https://www.w3.org/TR/css-color-4/">CSS Color Module Level 4</a>
 * @see <a href="https://www.w3.org/TR/css-color-5/">CSS Color Module Level 5</a>
 * @see <a href="https://www.w3.org/TR/css-color-5/#device-cmyk">CSS Color 5, &sect;6 Uncalibrated CMYK Colors:
 *         the device-cmyk() Function</a>
 * @see ___MappedColorUtils#cmykToRgb(double, double, double, double, DoubleFunction)
 */
// forced, so that an entity which puts its @Id on a getter cannot flip this hierarchy to
// property access and, with it, silently unmap every component below.
// the cost: EclipseLink then requires the entity to declare @Access(AccessType.FIELD) too.
@Access(AccessType.FIELD)
@MappedSuperclass
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public abstract class __MappedCmyk extends ___MappedColor {

    @Serial
    private static final long serialVersionUID = 1256034697884069713L;

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The number of components, {@value}, a color in this model consists of.
     */
    public static final int COMPONENT_COUNT = 4;

    /**
     * The index, {@value}, of the <span style="color:cyan;">cyan</span> component.
     */
    public static final int COMPONENT_INDEX_CYAN = 0;

    /**
     * The index, {@value}, of the <span style="color:magenta;">magenta</span> component.
     */
    public static final int COMPONENT_INDEX_MAGENTA = 1;

    /**
     * The index, {@value}, of the <span style="color:yellow;">yellow</span> component.
     */
    public static final int COMPONENT_INDEX_YELLOW = 2;

    /**
     * The index, {@value}, of the black component.
     */
    public static final int COMPONENT_INDEX_BLACK = 3;

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The name of the column, {@value}, of the {@value #ATTRIBUTE_NAME_CYAN} attribute.
     */
    public static final String COLUMN_NAME_CYAN = "cyan";

    /**
     * The name of the attribute, {@value}, mapped to the {@value #COLUMN_NAME_CYAN} column.
     */
    public static final String ATTRIBUTE_NAME_CYAN = "cyan";

    /**
     * The name of the column, {@value}, of the {@value #ATTRIBUTE_NAME_MAGENTA} attribute.
     */
    public static final String COLUMN_NAME_MAGENTA = "magenta";

    /**
     * The name of the attribute, {@value}, mapped to the {@value #COLUMN_NAME_MAGENTA} column.
     */
    public static final String ATTRIBUTE_NAME_MAGENTA = "magenta";

    /**
     * The name of the column, {@value}, of the {@value #ATTRIBUTE_NAME_YELLOW} attribute.
     */
    public static final String COLUMN_NAME_YELLOW = "yellow";

    /**
     * The name of the attribute, {@value}, mapped to the {@value #COLUMN_NAME_YELLOW} column.
     */
    public static final String ATTRIBUTE_NAME_YELLOW = "yellow";

    /**
     * The name of the column, {@value}, of the {@value #ATTRIBUTE_NAME_BLACK} attribute.
     */
    public static final String COLUMN_NAME_BLACK = "black";

    /**
     * The name of the attribute, {@value}, mapped to the {@value #COLUMN_NAME_BLACK} column.
     */
    public static final String ATTRIBUTE_NAME_BLACK = "black";

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected __MappedCmyk() {
        super();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object

    @Override
    public String toString() {
        return super.toString() + '{' +
               "cyan=" + cyan +
               ",magenta=" + magenta +
               ",yellow=" + yellow +
               ",black=" + black +
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
            case COMPONENT_INDEX_CYAN -> getCyan();
            case COMPONENT_INDEX_MAGENTA -> getMagenta();
            case COMPONENT_INDEX_YELLOW -> getYellow();
            case COMPONENT_INDEX_BLACK -> getBlack();
            default -> throw new IndexOutOfBoundsException("index: " + index);
        };
    }

    @Override
    public void setComponent(final int index, final double component) {
        switch (requireValidComponentIndex(index)) {
            case COMPONENT_INDEX_CYAN -> setCyan(component);
            case COMPONENT_INDEX_MAGENTA -> setMagenta(component);
            case COMPONENT_INDEX_YELLOW -> setYellow(component);
            case COMPONENT_INDEX_BLACK -> setBlack(component);
            default -> throw new IndexOutOfBoundsException("index: " + index);
        }
    }

    @Override
    public <R> R applySrgb(
            final DoubleFunction<? extends DoubleFunction<? extends DoubleFunction<? extends R>>> function) {
        return ___MappedColorUtils.cmykToRgb(getCyan(), getMagenta(), getYellow(), getBlack(), function);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <strong>CSS defines no sRGB-to-CMYK conversion.</strong> This is the exact algebraic inverse of
     * {@link #applySrgb(DoubleFunction)} under maximum black generation — see
     * {@link ___MappedColorUtils#rgbToCmyk(double, double, double, DoubleFunction)} for precisely what that means and
     * does not mean. Results are clamped, so that a value landing a hair outside the range through rounding is stored
     * rather than rejected.
     *
     * @param r {@inheritDoc}
     * @param g {@inheritDoc}
     * @param b {@inheritDoc}
     */
    @Override
    public void setSrgb(final double r, final double g, final double b) {
        ___MappedColorUtils.rgbToCmyk(
                requireValidComponent(r, "r"),
                requireValidComponent(g, "g"),
                requireValidComponent(b, "b"),
                c -> m -> y -> k -> {
                    setCyan(clampComponent(c));
                    setMagenta(clampComponent(m));
                    setYellow(clampComponent(y));
                    setBlack(clampComponent(k));
                    return null;
                }
        );
    }

    // ------------------------------------------------------------------------------------------------------- NOTATION

    /**
     * Returns this color in the
     * <a href="https://www.w3.org/TR/css-color-5/#device-cmyk">legacy {@code device-cmyk()} syntax</a> of CSS
     * Color 5 — {@code device-cmyk( <number>#{4} )}, four comma-separated numbers.
     * <p>
     * The legacy grammar admits neither percentages nor an alpha; it has no solidus and no {@code device-cmyka()}
     * counterpart. A color which is not {@linkplain #isOpaque() opaque} therefore cannot be represented, and its alpha
     * is dropped — use {@link #toModernDeviceCmykNotation()} when the alpha matters.
     * <p>
     * {@snippet lang = "none":
     * device-cmyk(0, 1, 1, 0)
     *}
     *
     * @return this color in the legacy syntax.
     * @see #toModernDeviceCmykNotation()
     */
    @Transient
    public String toLegacyDeviceCmykNotation() {
        return "device-cmyk(" + formatNumber(getCyan()) + ", " + formatNumber(getMagenta()) + ", " +
               formatNumber(getYellow()) + ", " + formatNumber(getBlack()) + ')';
    }

    /**
     * Returns this color in the
     * <a href="https://www.w3.org/TR/css-color-5/#device-cmyk">modern {@code device-cmyk()} syntax</a> of CSS
     * Color 5 — {@code device-cmyk( <cmyk-component>{4} [ / <alpha-value> ]? )}, where a component may be a number or a
     * percentage.
     * <p>
     * This notation is lossless, where the {@code rgb()} ones carry only the naive, uncalibrated sRGB rendering.
     * <p>
     * {@snippet lang = "none":
     * device-cmyk(0% 100% 100% 0%)
     * device-cmyk(0% 100% 100% 0% / 0.5)
     *}
     *
     * @return this color in the modern syntax.
     * @see #toLegacyDeviceCmykNotation()
     */
    @Transient
    public String toModernDeviceCmykNotation() {
        final var components = formatPercentage(getCyan()) + ' ' + formatPercentage(getMagenta()) + ' ' +
                               formatPercentage(getYellow()) + ' ' + formatPercentage(getBlack());
        return isOpaque()
                ? "device-cmyk(" + components + ')'
                : "device-cmyk(" + components + " / " + formatAlpha(getAlpha()) + ')';
    }

    // ----------------------------------------------------------------------------------------------------------- cyan

    /**
     * Returns the current value of the <span style="color:cyan;">cyan</span> component.
     *
     * @return a value between {@value ___MappedColor#MIN_COMPONENT} and {@value ___MappedColor#MAX_COMPONENT}, both
     *         inclusive.
     */
    public double getCyan() {
        return cyan;
    }

    /**
     * Replaces the current value of the <span style="color:cyan;">cyan</span> component with specified value.
     *
     * @param cyan new value for the <span style="color:cyan;">cyan</span> component.
     */
    public void setCyan(final double cyan) {
        this.cyan = requireValidComponent(cyan, ATTRIBUTE_NAME_CYAN);
    }

    // -------------------------------------------------------------------------------------------------------- magenta

    /**
     * Returns the current value of the <span style="color:magenta;">magenta</span> component.
     *
     * @return a value between {@value ___MappedColor#MIN_COMPONENT} and {@value ___MappedColor#MAX_COMPONENT}, both
     *         inclusive.
     */
    public double getMagenta() {
        return magenta;
    }

    /**
     * Replaces the current value of the <span style="color:magenta;">magenta</span> component with specified value.
     *
     * @param magenta new value for the <span style="color:magenta;">magenta</span> component.
     */
    public void setMagenta(final double magenta) {
        this.magenta = requireValidComponent(magenta, ATTRIBUTE_NAME_MAGENTA);
    }

    // --------------------------------------------------------------------------------------------------------- yellow

    /**
     * Returns the current value of the <span style="color:yellow;">yellow</span> component.
     *
     * @return a value between {@value ___MappedColor#MIN_COMPONENT} and {@value ___MappedColor#MAX_COMPONENT}, both
     *         inclusive.
     */
    public double getYellow() {
        return yellow;
    }

    /**
     * Replaces the current value of the <span style="color:yellow;">yellow</span> component with specified value.
     *
     * @param yellow new value for the <span style="color:yellow;">yellow</span> component.
     */
    public void setYellow(final double yellow) {
        this.yellow = requireValidComponent(yellow, ATTRIBUTE_NAME_YELLOW);
    }

    // ---------------------------------------------------------------------------------------------------------- black

    /**
     * Returns the current value of the black component.
     *
     * @return a value between {@value ___MappedColor#MIN_COMPONENT} and {@value ___MappedColor#MAX_COMPONENT}, both
     *         inclusive.
     */
    public double getBlack() {
        return black;
    }

    /**
     * Replaces the current value of the black component with specified value.
     *
     * @param black new value for the black component.
     */
    public void setBlack(final double black) {
        this.black = requireValidComponent(black, ATTRIBUTE_NAME_BLACK);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The cyan component of this color, normalized, mapped to the {@value #COLUMN_NAME_CYAN} column.
     */
    @DecimalMax(DECIMAL_MAX_COMPONENT)
    @DecimalMin(DECIMAL_MIN_COMPONENT)
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_CYAN, nullable = false, insertable = true, updatable = true)
    private double cyan;

    /**
     * The magenta component of this color, normalized, mapped to the {@value #COLUMN_NAME_MAGENTA} column.
     */
    @DecimalMax(DECIMAL_MAX_COMPONENT)
    @DecimalMin(DECIMAL_MIN_COMPONENT)
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_MAGENTA, nullable = false, insertable = true, updatable = true)
    private double magenta;

    /**
     * The yellow component of this color, normalized, mapped to the {@value #COLUMN_NAME_YELLOW} column.
     */
    @DecimalMax(DECIMAL_MAX_COMPONENT)
    @DecimalMin(DECIMAL_MIN_COMPONENT)
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_YELLOW, nullable = false, insertable = true, updatable = true)
    private double yellow;

    /**
     * The black component of this color, normalized, mapped to the {@value #COLUMN_NAME_BLACK} column.
     */
    @DecimalMax(DECIMAL_MAX_COMPONENT)
    @DecimalMin(DECIMAL_MIN_COMPONENT)
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_BLACK, nullable = false, insertable = true, updatable = true)
    private double black;
}
