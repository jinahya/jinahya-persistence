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

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import org.jspecify.annotations.Nullable;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.DoubleFunction;

/**
 * An abstract mapped superclass for colors, of any color model, whose components are addressed uniformly.
 * <p>
 * This class maps no column of its own. It fixes only the vocabulary every color model in this package shares — how
 * many components a color has, how each of them is read and written on a common {@code [0.0, 1.0]} scale, what its
 * alpha is, and how it converts to and from
 * <a href="https://www.w3.org/TR/css-color-4/#numeric-srgb">sRGB</a> — and leaves each subclass free to declare the
 * columns natural to its own model: {@link __MappedRgb red/green/blue}, {@link __MappedCmyk cyan/magenta/yellow/black},
 * {@link __MappedHsl hue/saturation/lightness}, {@link __MappedHwb hue/whiteness/blackness}.
 * <p>
 * Keeping the components in real columns, rather than packed into one opaque column on this class, is deliberate: a
 * persisted color stays queryable, indexable and readable in SQL.
 *
 * <h2>Components, and the alpha</h2>
 * A component is a <em>coordinate</em> of the color model, and the alpha is not one of them —
 * <a href="https://www.w3.org/TR/css-color-4/">CSS Color 4</a> models every color as coordinates in a color space
 * <em>plus</em> an alpha, and this class follows it. {@link #getComponentCount()} therefore reports {@code 3} for
 * every model here but {@link __MappedCmyk}, which has four, and never counts the alpha; the alpha is reached only
 * through {@link #getAlpha()}.
 * <p>
 * {@link #getComponent(int)} and {@link #setComponent(int, double)} always speak in <em>normalized</em> values between
 * {@value #MIN_COMPONENT} and {@value #MAX_COMPONENT}, both inclusive, whatever unit the model itself prefers. A
 * subclass whose component has a natural unit of its own — a hue in degrees, say — keeps a typed accessor for that unit
 * alongside, and normalizes only here.
 *
 * <h2>Alpha</h2>
 * {@link #getAlpha()} defaults to {@value #ALPHA_OPAQUE}, matching CSS Color 4, where an omitted alpha is {@code 100%}.
 * A model which actually stores an alpha — {@link __MappedRgba}, for one — overrides it.
 *
 * <h2>Access type</h2>
 * The subclasses which map columns are annotated {@link jakarta.persistence.Access @Access}({@code FIELD}), so that an
 * entity declaring its {@link jakarta.persistence.Id @Id} on a getter cannot flip the hierarchy to property access and
 * unmap every component. An entity extending them should declare {@code @Access(AccessType.FIELD)} too: EclipseLink
 * requires it, Hibernate does not.
 *
 * <h2>Out-of-range values</h2>
 * CSS Color 4 clamps a component outside its range rather than rejecting it. This class does not: a component is
 * persisted, and silently storing something other than what a caller asked for is worse, in a database, than saying no.
 * {@link #requireValidComponent(double, String)} throws, and {@link #clampComponent(double)} is available where
 * clamping <em>is</em> wanted — notably where a conversion's own rounding could land a hair outside the range.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="https://www.w3.org/TR/css-color-4/">CSS Color Module Level 4</a>
 */
@MappedSuperclass
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public abstract class ___MappedColor implements Serializable {

    @Serial
    private static final long serialVersionUID = -3671525436570428941L;

    // ----------------------------------------------------------------------------------------------------- COMPONENTS

    /**
     * The minimum value, {@value}, of a normalized component.
     */
    public static final double MIN_COMPONENT = 0.0d;

    /**
     * The maximum value, {@value}, of a normalized component.
     */
    public static final double MAX_COMPONENT = 1.0d;

    /**
     * The value, {@value}, of {@link #MIN_COMPONENT} for
     * {@link jakarta.validation.constraints.DecimalMin @DecimalMin}.
     */
    public static final String DECIMAL_MIN_COMPONENT = "0.0";

    /**
     * The value, {@value}, of {@link #MAX_COMPONENT} for
     * {@link jakarta.validation.constraints.DecimalMax @DecimalMax}.
     */
    public static final String DECIMAL_MAX_COMPONENT = "1.0";

    /**
     * The value, {@value}, of a fully opaque alpha.
     */
    public static final double ALPHA_OPAQUE = MAX_COMPONENT;

    /**
     * The maximum value, {@value}, of a component denormalized to eight bits.
     */
    public static final int MAX_COMPONENT_8_BIT = 255;

    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100L);

    // -------------------------------------------------------------------------------------------------------- HELPERS

    /**
     * Checks whether specified value is a valid normalized component, and returns it.
     * <p>
     * {@link Double#NaN} is not a valid component, and is rejected along with any value out of range.
     *
     * @param component the value to check.
     * @param name      the name of the component, for the message of the exception thrown.
     * @return the {@code component}.
     * @throws IllegalArgumentException when the {@code component} is not between {@value #MIN_COMPONENT} and
     *                                  {@value #MAX_COMPONENT}, both inclusive.
     * @see #clampComponent(double)
     */
    protected static double requireValidComponent(final double component, final String name) {
        if (!(component >= MIN_COMPONENT && component <= MAX_COMPONENT)) {
            throw new IllegalArgumentException("invalid " + name + ": " + component);
        }
        return component;
    }

    /**
     * Clamps specified value onto the range of a normalized component.
     * <p>
     * Use this, rather than {@link #requireValidComponent(double, String)}, where a value arrives from a conversion
     * whose own rounding may put it a hair outside the range, and where the caller never named a component to begin
     * with.
     *
     * @param component the value to clamp.
     * @return a value between {@value #MIN_COMPONENT} and {@value #MAX_COMPONENT}, both inclusive.
     * @throws IllegalArgumentException when the {@code component} is {@link Double#NaN}.
     */
    protected static double clampComponent(final double component) {
        if (Double.isNaN(component)) {
            throw new IllegalArgumentException("component is NaN");
        }
        return Math.clamp(component, MIN_COMPONENT, MAX_COMPONENT);
    }

    /**
     * Denormalizes specified component onto eight bits.
     *
     * @param component a normalized component.
     * @return a value between {@code 0} and {@value #MAX_COMPONENT_8_BIT}, both inclusive.
     * @see #fromEightBits(int, String)
     */
    protected static int toEightBits(final double component) {
        return (int) Math.round(clampComponent(component) * MAX_COMPONENT_8_BIT);
    }

    /**
     * Normalizes specified eight-bit value onto the range of a component.
     *
     * @param component a value between {@code 0} and {@value #MAX_COMPONENT_8_BIT}, both inclusive.
     * @param name      the name of the component, for the message of the exception thrown.
     * @return a value between {@value #MIN_COMPONENT} and {@value #MAX_COMPONENT}, both inclusive.
     * @throws IllegalArgumentException when the {@code component} is out of range.
     * @see #toEightBits(double)
     */
    protected static double fromEightBits(final int component, final String name) {
        if (component < 0 || component > MAX_COMPONENT_8_BIT) {
            throw new IllegalArgumentException("invalid " + name + ": " + component);
        }
        return component / (double) MAX_COMPONENT_8_BIT;
    }

    /**
     * Formats specified value as a CSS {@code <number>}, without a trailing {@code .0}.
     *
     * @param value the value to format.
     * @return the {@code value} as a number.
     * @see <a href="https://www.w3.org/TR/css-values-4/#number-value">CSS Values 4, &sect;6.2 Real Numbers: the
     *         &lt;number&gt; type</a>
     */
    protected static String formatNumber(final double value) {
        return value == Math.rint(value) && !Double.isInfinite(value)
                ? Long.toString((long) value)
                : Double.toString(value);
    }

    /**
     * Formats specified normalized component as a CSS {@code <percentage>}.
     * <p>
     * The scaling is decimal, not binary: {@code 0.333 * 100} is {@code 33.300000000000004} in a {@code double}, and
     * that noise has no business reaching a stylesheet.
     *
     * @param component a normalized component.
     * @return the {@code component} as a percentage, {@code 50%} for {@code 0.5}.
     * @see <a href="https://www.w3.org/TR/css-values-4/#percentage-value">CSS Values 4, &sect;7.1 Percentages: the
     *         &lt;percentage&gt; type</a>
     */
    protected static String formatPercentage(final double component) {
        return BigDecimal.valueOf(component)
                       .multiply(ONE_HUNDRED)
                       .stripTrailingZeros()
                       .toPlainString() + '%';
    }

    /**
     * Formats specified alpha as the shortest decimal which still denormalizes onto the same eight bits.
     * <p>
     * The three color components of a serialized color are already eight-bit values; carrying the alpha at the full
     * precision of a {@code double} alongside them would be noise. An alpha of {@code 128/255} serializes as
     * {@code 0.5}, not as {@code 0.5019607843137255}.
     *
     * @param alpha the alpha to format.
     * @return the {@code alpha} as a number.
     * @see <a href="https://www.w3.org/TR/css-color-4/#typedef-alpha-value">CSS Color 4, &lt;alpha-value&gt;</a>
     */
    protected static String formatAlpha(final double alpha) {
        final var eightBits = toEightBits(alpha);
        var scale = 1L;
        for (var i = 0; i < 4; i++) {
            final var rounded = Math.round(alpha * scale) / (double) scale;
            if (toEightBits(rounded) == eightBits) {
                return formatNumber(rounded);
            }
            scale *= 10L;
        }
        return Double.toString(alpha);
    }

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected ___MappedColor() {
        super();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object

    // Note: neither equals(Object)/hashCode() nor toString() is overridden here.
    //
    // This class maps no column, so it has no state of its own to print; each model prints the columns it declares.
    //
    // And a mapped superclass has no business defining equality for the entities which extend it: components are
    // mutable, so a component-based hashCode would lose an entity in a HashSet the moment a setter ran, and
    // getClass() does not survive a lazily-loaded proxy. An entity's identity is its own business — see
    // hasSameComponentsAs(___MappedColor) for the value comparison this class can honestly offer.

    /**
     * Checks whether specified color is of the very same class as this one and carries the same components, alpha
     * included.
     * <p>
     * This is deliberately <em>not</em> {@link #equals(Object)}: these classes are extended by entities, whose identity
     * is their primary key, and whose components change over their lifetime.
     *
     * @param other the color to compare with; may be {@code null}.
     * @return {@code true} when the {@code other} color is of this class and matches component for component;
     *         {@code false} otherwise.
     */
    public boolean hasSameComponentsAs(final @Nullable ___MappedColor other) {
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        return Arrays.equals(toComponentArray(), other.toComponentArray()) &&
               Double.compare(getAlpha(), other.getAlpha()) == 0;
    }

    // ----------------------------------------------------------------------------------------------------- COMPONENTS

    /**
     * Returns the number of components this color consists of.
     * <p>
     * The alpha is not one of them; see {@link #getAlpha()}.
     *
     * @return the number of components of this color; positive.
     */
    @Transient
    public abstract int getComponentCount();

    /**
     * Returns the normalized value of the component at specified index.
     *
     * @param index the index of the component, between {@code 0}, inclusive, and {@link #getComponentCount()},
     *              exclusive.
     * @return a value between {@value #MIN_COMPONENT} and {@value #MAX_COMPONENT}, both inclusive.
     * @throws IndexOutOfBoundsException when the {@code index} is out of range.
     */
    @Transient
    public abstract double getComponent(int index);

    /**
     * Replaces the value of the component at specified index with specified normalized value.
     *
     * @param index     the index of the component, between {@code 0}, inclusive, and {@link #getComponentCount()},
     *                  exclusive.
     * @param component a value between {@value #MIN_COMPONENT} and {@value #MAX_COMPONENT}, both inclusive.
     * @throws IndexOutOfBoundsException when the {@code index} is out of range.
     * @throws IllegalArgumentException  when the {@code component} is out of range.
     */
    public abstract void setComponent(int index, double component);

    /**
     * Returns all normalized components of this color, in the order this model declares them.
     *
     * @return an array of {@link #getComponentCount()} values, each between {@value #MIN_COMPONENT} and
     *         {@value #MAX_COMPONENT}, both inclusive; the alpha is not among them.
     */
    @Transient
    public double[] toComponentArray() {
        final var components = new double[getComponentCount()];
        for (var i = 0; i < components.length; i++) {
            components[i] = getComponent(i);
        }
        return components;
    }

    /**
     * Checks whether specified index addresses a component of this color, and returns it.
     *
     * @param index the index to check.
     * @return the {@code index}.
     * @throws IndexOutOfBoundsException when the {@code index} is out of range.
     */
    protected int requireValidComponentIndex(final int index) {
        return Objects.checkIndex(index, getComponentCount());
    }

    // ---------------------------------------------------------------------------------------------------------- alpha

    /**
     * Returns the alpha of this color.
     * <p>
     * The default implementation returns {@value #ALPHA_OPAQUE}, as
     * <a href="https://www.w3.org/TR/css-color-4/#rgb-functions">CSS Color 4</a> does for an omitted alpha. A model
     * which stores an alpha of its own overrides this method.
     *
     * @return a value between {@value #MIN_COMPONENT} and {@value #MAX_COMPONENT}, both inclusive.
     * @see <a href="https://www.w3.org/TR/css-color-4/#transparency">CSS Color 4, &sect;15 Transparency: the
     *         opacity property</a>
     */
    @Transient
    public double getAlpha() {
        return ALPHA_OPAQUE;
    }

    /**
     * Checks whether this color is fully opaque <em>as rendered</em>.
     * <p>
     * The test is made on the eight-bit alpha, not on the stored one: an alpha of {@code 0.999} denormalizes onto
     * {@value #MAX_COMPONENT_8_BIT} and renders indistinguishably from an opaque color, so it serializes as one.
     *
     * @return {@code true} when this color is opaque; {@code false} otherwise.
     * @see <a href="https://www.w3.org/TR/css-color-4/#transparency">CSS Color 4, &sect;15 Transparency: the
     *         opacity property</a>
     */
    @Transient
    public boolean isOpaque() {
        return toEightBits(getAlpha()) == MAX_COMPONENT_8_BIT;
    }

    // ----------------------------------------------------------------------------------------------------------- sRGB

    /**
     * Applies the <span style="color:red;">r</span><span style="color:green;">g</span><span
     * style="color:blue;">b</span> components of this color, converted to sRGB, to specified function, and returns the
     * result.
     * <p>
     * {@snippet lang = "java":
     * final var luminance = color.applySrgb(r -> g -> b -> .2126d * r + .7152d * g + .0722d * b);
     *}
     *
     * @param function the function to be applied with, in currying, the normalized
     *                 <span style="color:red;">red</span>, <span style="color:green;">green</span> and
     *                 <span style="color:blue;">blue</span> components.
     * @param <R>      result type parameter
     * @return the result of the {@code function}.
     * @see #setSrgb(double, double, double)
     * @see <a href="https://www.w3.org/TR/css-color-4/#numeric-srgb">CSS Color 4, &sect;5 sRGB Colors</a>
     */
    public abstract <R> R applySrgb(
            DoubleFunction<? extends DoubleFunction<? extends DoubleFunction<? extends R>>> function);

    /**
     * Replaces the components of this color with those of specified sRGB color, converted into this model.
     * <p>
     * This is the inverse of {@link #applySrgb(DoubleFunction)}, and the two together convert between any two models in
     * this package:
     * {@snippet lang = "java":
     * hsl.applySrgb(r -> g -> b -> { cmyk.setSrgb(r, g, b); return null; });
     *} The alpha is not touched: it is not an sRGB component, and a model which carries one keeps it.
     *
     * @param r a normalized <span style="color:red;">red</span> component.
     * @param g a normalized <span style="color:green;">green</span> component.
     * @param b a normalized <span style="color:blue;">blue</span> component.
     * @throws IllegalArgumentException when any argument is out of range.
     * @see <a href="https://www.w3.org/TR/css-color-4/#numeric-srgb">CSS Color 4, &sect;5 sRGB Colors</a>
     */
    public abstract void setSrgb(double r, double g, double b);

    /**
     * Returns the components of this color, converted to sRGB.
     *
     * @return an array of three values, each between {@value #MIN_COMPONENT} and {@value #MAX_COMPONENT}, both
     *         inclusive, holding the <span style="color:red;">red</span>, <span style="color:green;">green</span> and
     *         <span style="color:blue;">blue</span> components, in that order.
     * @see #applySrgb(DoubleFunction)
     */
    @Transient
    public double[] toComponentArrayInSrgb() {
        return applySrgb(r -> g -> b -> new double[]{r, g, b});
    }

    // ------------------------------------------------------------------------------------------------------- NOTATION

    /**
     * Returns this color in the <a href="https://www.w3.org/TR/css-color-4/#hex-notation">hex notation</a> of CSS Color
     * 4.
     * <p>
     * The alpha is appended, as {@code aa}, only when this color is not {@linkplain #isOpaque() opaque}.
     * <p>
     * {@snippet lang = "none":
     * #ff0000
     * #ff000080
     *}
     *
     * @return {@code #rrggbb}, or {@code #rrggbbaa} when this color is not opaque.
     * @see ___MappedColorUtils#applyHexNotation(String, DoubleFunction)
     * @see <a href="https://www.w3.org/TR/css-color-4/#hex-notation">CSS Color 4, &sect;4.2 The RGB Hexadecimal
     *         Notations</a>
     */
    @Transient
    public String toHexNotation() {
        final var alpha = getAlpha();
        final var opaque = isOpaque();
        return applySrgb(r -> g -> b -> {
            final var hex = String.format("#%02x%02x%02x", toEightBits(r), toEightBits(g), toEightBits(b));
            return opaque ? hex : hex + String.format("%02x", toEightBits(alpha));
        });
    }

    /**
     * Returns this color in the
     * <a href="https://www.w3.org/TR/css-color-4/#typedef-legacy-rgb-syntax">legacy {@code rgb()}/{@code rgba()}
     * syntax</a> of CSS Color 4 — components separated by commas, and a distinct function name when the alpha is
     * carried.
     * <p>
     * {@snippet lang = "none":
     * rgb(255, 0, 0)
     * rgba(255, 0, 0, 0.5)
     *}
     *
     * @return this color in the legacy syntax.
     * @see #toModernRgbNotation()
     * @see <a href="https://www.w3.org/TR/css-color-4/#typedef-legacy-rgb-syntax">CSS Color 4,
     *         &lt;legacy-rgb-syntax&gt;</a>
     */
    @Transient
    public String toLegacyRgbNotation() {
        final var alpha = getAlpha();
        final var opaque = isOpaque();
        return applySrgb(r -> g -> b -> {
            final var components = toEightBits(r) + ", " + toEightBits(g) + ", " + toEightBits(b);
            return opaque
                    ? "rgb(" + components + ')'
                    : "rgba(" + components + ", " + formatAlpha(alpha) + ')';
        });
    }

    /**
     * Returns this color in the
     * <a href="https://www.w3.org/TR/css-color-4/#typedef-modern-rgb-syntax">modern {@code rgb()} syntax</a> of CSS
     * Color 4 — components separated by whitespace, and the alpha, when carried, separated by a solidus.
     * <p>
     * {@snippet lang = "none":
     * rgb(255 0 0)
     * rgb(255 0 0 / 0.5)
     *}
     *
     * @return this color in the modern syntax.
     * @see #toLegacyRgbNotation()
     * @see <a href="https://www.w3.org/TR/css-color-4/#typedef-modern-rgb-syntax">CSS Color 4,
     *         &lt;modern-rgb-syntax&gt;</a>
     */
    @Transient
    public String toModernRgbNotation() {
        final var alpha = getAlpha();
        final var opaque = isOpaque();
        return applySrgb(r -> g -> b -> {
            final var components = toEightBits(r) + " " + toEightBits(g) + " " + toEightBits(b);
            return opaque
                    ? "rgb(" + components + ')'
                    : "rgb(" + components + " / " + formatAlpha(alpha) + ')';
        });
    }
}
