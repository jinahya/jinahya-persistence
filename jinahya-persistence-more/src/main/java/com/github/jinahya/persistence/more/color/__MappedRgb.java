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
 * An abstract mapped superclass for colors in the
 * <a href="https://www.w3.org/TR/css-color-4/#numeric-srgb">sRGB</a> color model, without an alpha.
 * <p>
 * Three columns are mapped — {@value #COLUMN_NAME_RED}, {@value #COLUMN_NAME_GREEN} and {@value #COLUMN_NAME_BLUE} —
 * each holding a component normalized between {@value ___MappedColor#MIN_COMPONENT} and
 * {@value ___MappedColor#MAX_COMPONENT}, both inclusive. The eight-bit values familiar from CSS, between {@code 0} and
 * {@value ___MappedColor#MAX_COMPONENT_8_BIT}, are available through {@link #getRedAsEightBits()} and its siblings,
 * which are {@link Transient @Transient}.
 * <p>
 * This model is sRGB itself, so {@link #applySrgb(java.util.function.DoubleFunction)} and
 * {@link #setSrgb(double, double, double)} are pass-throughs; every other model in this package converts.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="https://www.w3.org/TR/css-color-4/">CSS Color Module Level 4</a>
 * @see <a href="https://www.w3.org/TR/css-color-4/#rgb-functions">CSS Color 4, &sect;5 sRGB Colors</a>
 * @see __MappedRgba
 */
// forced, so that an entity which puts its @Id on a getter cannot flip this hierarchy to
// property access and, with it, silently unmap every component below.
// the cost: EclipseLink then requires the entity to declare @Access(AccessType.FIELD) too.
@Access(AccessType.FIELD)
@MappedSuperclass
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public abstract class __MappedRgb extends ___MappedColor {

    @Serial
    private static final long serialVersionUID = 8261125405927873932L;

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The number of components, {@value}, a color in this model consists of.
     */
    public static final int COMPONENT_COUNT = 3;

    /**
     * The index, {@value}, of the <span style="color:red;">red</span> component.
     */
    public static final int COMPONENT_INDEX_RED = 0;

    /**
     * The index, {@value}, of the <span style="color:green;">green</span> component.
     */
    public static final int COMPONENT_INDEX_GREEN = 1;

    /**
     * The index, {@value}, of the <span style="color:blue;">blue</span> component.
     */
    public static final int COMPONENT_INDEX_BLUE = 2;

    // ------------------------------------------------------------------------------------------------------------ red

    /**
     * The name of the column, {@value}, of the {@value #ATTRIBUTE_NAME_RED} attribute.
     */
    public static final String COLUMN_NAME_RED = "red";

    /**
     * The name of the attribute, {@value}, mapped to the {@value #COLUMN_NAME_RED} column.
     */
    public static final String ATTRIBUTE_NAME_RED = "red";

    // ---------------------------------------------------------------------------------------------------------- green

    /**
     * The name of the column, {@value}, of the {@value #ATTRIBUTE_NAME_GREEN} attribute.
     */
    public static final String COLUMN_NAME_GREEN = "green";

    /**
     * The name of the attribute, {@value}, mapped to the {@value #COLUMN_NAME_GREEN} column.
     */
    public static final String ATTRIBUTE_NAME_GREEN = "green";

    // ----------------------------------------------------------------------------------------------------------- blue

    /**
     * The name of the column, {@value}, of the {@value #ATTRIBUTE_NAME_BLUE} attribute.
     */
    public static final String COLUMN_NAME_BLUE = "blue";

    /**
     * The name of the attribute, {@value}, mapped to the {@value #COLUMN_NAME_BLUE} column.
     */
    public static final String ATTRIBUTE_NAME_BLUE = "blue";

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance.
     */
    protected __MappedRgb() {
        super();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object

    @Override
    public String toString() {
        return super.toString() + '{' +
               "red=" + red +
               ",green=" + green +
               ",blue=" + blue +
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
            case COMPONENT_INDEX_RED -> getRed();
            case COMPONENT_INDEX_GREEN -> getGreen();
            case COMPONENT_INDEX_BLUE -> getBlue();
            default -> throw new IndexOutOfBoundsException("index: " + index);
        };
    }

    @Override
    public void setComponent(final int index, final double component) {
        switch (requireValidComponentIndex(index)) {
            case COMPONENT_INDEX_RED -> setRed(component);
            case COMPONENT_INDEX_GREEN -> setGreen(component);
            case COMPONENT_INDEX_BLUE -> setBlue(component);
            default -> throw new IndexOutOfBoundsException("index: " + index);
        }
    }

    @Override
    public <R> R applySrgb(
            final DoubleFunction<? extends DoubleFunction<? extends DoubleFunction<? extends R>>> function) {
        return function.apply(getRed()).apply(getGreen()).apply(getBlue());
    }

    /**
     * {@inheritDoc}
     * <p>
     * This model <em>is</em> sRGB, so the components are stored as given.
     *
     * @param r {@inheritDoc}
     * @param g {@inheritDoc}
     * @param b {@inheritDoc}
     * @see <a href="https://www.w3.org/TR/css-color-4/#numeric-srgb">CSS Color 4, &sect;5 sRGB Colors</a>
     */
    @Override
    public void setSrgb(final double r, final double g, final double b) {
        setRed(r);
        setGreen(g);
        setBlue(b);
    }

    // ------------------------------------------------------------------------------------------------------------ red

    /**
     * Returns the current value of the <span style="color:red;">red</span> component.
     *
     * @return a value between {@value ___MappedColor#MIN_COMPONENT} and {@value ___MappedColor#MAX_COMPONENT}, both
     *         inclusive.
     */
    public double getRed() {
        return red;
    }

    /**
     * Replaces the current value of the <span style="color:red;">red</span> component with specified value.
     *
     * @param red new value for the <span style="color:red;">red</span> component, between
     *            {@value ___MappedColor#MIN_COMPONENT} and {@value ___MappedColor#MAX_COMPONENT}, both inclusive.
     */
    public void setRed(final double red) {
        this.red = requireValidComponent(red, ATTRIBUTE_NAME_RED);
    }

    /**
     * Returns the current value of the <span style="color:red;">red</span> component, denormalized onto eight bits.
     *
     * @return a value between {@code 0} and {@value ___MappedColor#MAX_COMPONENT_8_BIT}, both inclusive.
     * @see <a href="https://www.w3.org/TR/css-color-4/#rgb-functions">CSS Color 4, &sect;5.1 The RGB functions:
     *         rgb() and rgba()</a>
     */
    @Transient
    public int getRedAsEightBits() {
        return toEightBits(getRed());
    }

    /**
     * Replaces the current value of the <span style="color:red;">red</span> component with specified eight-bit value.
     *
     * @param red new value for the <span style="color:red;">red</span> component, between {@code 0} and
     *            {@value ___MappedColor#MAX_COMPONENT_8_BIT}, both inclusive.
     */
    public void setRedAsEightBits(final int red) {
        setRed(fromEightBits(red, ATTRIBUTE_NAME_RED));
    }

    // ---------------------------------------------------------------------------------------------------------- green

    /**
     * Returns the current value of the <span style="color:green;">green</span> component.
     *
     * @return a value between {@value ___MappedColor#MIN_COMPONENT} and {@value ___MappedColor#MAX_COMPONENT}, both
     *         inclusive.
     */
    public double getGreen() {
        return green;
    }

    /**
     * Replaces the current value of the <span style="color:green;">green</span> component with specified value.
     *
     * @param green new value for the <span style="color:green;">green</span> component, between
     *              {@value ___MappedColor#MIN_COMPONENT} and {@value ___MappedColor#MAX_COMPONENT}, both inclusive.
     */
    public void setGreen(final double green) {
        this.green = requireValidComponent(green, ATTRIBUTE_NAME_GREEN);
    }

    /**
     * Returns the current value of the <span style="color:green;">green</span> component, denormalized onto eight
     * bits.
     *
     * @return a value between {@code 0} and {@value ___MappedColor#MAX_COMPONENT_8_BIT}, both inclusive.
     */
    @Transient
    public int getGreenAsEightBits() {
        return toEightBits(getGreen());
    }

    /**
     * Replaces the current value of the <span style="color:green;">green</span> component with specified eight-bit
     * value.
     *
     * @param green new value for the <span style="color:green;">green</span> component, between {@code 0} and
     *              {@value ___MappedColor#MAX_COMPONENT_8_BIT}, both inclusive.
     */
    public void setGreenAsEightBits(final int green) {
        setGreen(fromEightBits(green, ATTRIBUTE_NAME_GREEN));
    }

    // ----------------------------------------------------------------------------------------------------------- blue

    /**
     * Returns the current value of the <span style="color:blue;">blue</span> component.
     *
     * @return a value between {@value ___MappedColor#MIN_COMPONENT} and {@value ___MappedColor#MAX_COMPONENT}, both
     *         inclusive.
     */
    public double getBlue() {
        return blue;
    }

    /**
     * Replaces the current value of the <span style="color:blue;">blue</span> component with specified value.
     *
     * @param blue new value for the <span style="color:blue;">blue</span> component, between
     *             {@value ___MappedColor#MIN_COMPONENT} and {@value ___MappedColor#MAX_COMPONENT}, both inclusive.
     */
    public void setBlue(final double blue) {
        this.blue = requireValidComponent(blue, ATTRIBUTE_NAME_BLUE);
    }

    /**
     * Returns the current value of the <span style="color:blue;">blue</span> component, denormalized onto eight bits.
     *
     * @return a value between {@code 0} and {@value ___MappedColor#MAX_COMPONENT_8_BIT}, both inclusive.
     */
    @Transient
    public int getBlueAsEightBits() {
        return toEightBits(getBlue());
    }

    /**
     * Replaces the current value of the <span style="color:blue;">blue</span> component with specified eight-bit
     * value.
     *
     * @param blue new value for the <span style="color:blue;">blue</span> component, between {@code 0} and
     *             {@value ___MappedColor#MAX_COMPONENT_8_BIT}, both inclusive.
     */
    public void setBlueAsEightBits(final int blue) {
        setBlue(fromEightBits(blue, ATTRIBUTE_NAME_BLUE));
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DecimalMax(DECIMAL_MAX_COMPONENT)
    @DecimalMin(DECIMAL_MIN_COMPONENT)
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_RED, nullable = false, insertable = true, updatable = true)
    private double red;

    @DecimalMax(DECIMAL_MAX_COMPONENT)
    @DecimalMin(DECIMAL_MIN_COMPONENT)
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_GREEN, nullable = false, insertable = true, updatable = true)
    private double green;

    @DecimalMax(DECIMAL_MAX_COMPONENT)
    @DecimalMin(DECIMAL_MIN_COMPONENT)
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_BLUE, nullable = false, insertable = true, updatable = true)
    private double blue;
}
