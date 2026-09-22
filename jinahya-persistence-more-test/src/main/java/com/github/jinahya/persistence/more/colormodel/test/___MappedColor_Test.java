package com.github.jinahya.persistence.more.colormodel.test;

/*-
 * #%L
 * jinahya-persistence-more-test
 * %%
 * Copyright (C) 2024 - 2025 Jinahya, Inc.
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

import com.github.jinahya.persistence.more.colormodel.___MappedColor;
import com.github.jinahya.persistence.more.test.___Utils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * An abstract base class for testing {@link ___MappedColor} implementations.
 * <p>
 * Everything checked here is checked through the component index and the sRGB conversion, which is the whole point of
 * that root class: the columns a model declares are its own, but the way they are addressed and converted is common,
 * so a test of it need not know which model it is looking at. A subclass names a concrete class and gets the lot.
 *
 * @param <COLOR> the color class under test
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S100", // Method names should comply with a naming convention
        "java:S101"  // Class names should comply with a naming convention
})
public abstract class ___MappedColor_Test<COLOR extends ___MappedColor> {

    /**
     * The tolerance, {@value}, within which a component is expected to survive a conversion to sRGB and back.
     */
    protected static final double TOLERANCE = 1.0e-9d;

    /**
     * Normalized component values which are exact in binary, and which every model round-trips.
     *
     * @implNote {@value ___MappedColor#MAX_COMPONENT} is not among them, and deliberately. A hue is an angle on a
     *         half-open range: a normalized hue of {@code 1.0} is {@code 360} degrees, which reduces onto {@code 0},
     *         so the one model-independent assertion which can be made about it is that it is accepted — see
     *         {@link #_Accepted_MaxComponent()}.
     */
    private static final double[] ROUND_TRIPPING_COMPONENTS = {0.0d, 0.25d, 0.5d, 0.75d};

    /**
     * sRGB triples which every model in the package converts in both directions.
     */
    private static final double[][] SRGB_SAMPLES = {
            {0.0d, 0.0d, 0.0d},
            {1.0d, 1.0d, 1.0d},
            {1.0d, 0.0d, 0.0d},
            {0.2d, 0.7d, 0.4d},
    };

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance for testing the specified color class.
     *
     * @param colorClass the color class to test.
     */
    protected ___MappedColor_Test(final Class<COLOR> colorClass) {
        super();
        this.colorClass = Objects.requireNonNull(colorClass, "colorClass is null");
    }

    // ----------------------------------------------------------------------------------------------------- components

    /**
     * Verifies that a color consists of a positive number of components, and that the array reports all of them.
     */
    @DisplayName("the component array holds every component, and only them")
    @Test
    protected void _Consistent_ComponentCount() {
        final var instance = newColorInstance();
        final var count = instance.getComponentCount();
        assertTrue(count > 0, () -> "non-positive component count of " + colorClass + ": " + count);
        final var components = instance.toComponentArray();
        assertEquals(count, components.length, "the component array does not hold every component");
        for (var i = 0; i < count; i++) {
            final var index = i;
            assertEquals(instance.getComponent(index), components[index],
                         () -> "the component array disagrees at " + index);
        }
    }

    /**
     * Verifies that every component round-trips through its index.
     */
    @DisplayName("every component round-trips through its index")
    @Test
    protected void _RoundTrip_SetThenGetComponent() {
        final var instance = newColorInstance();
        for (var i = 0; i < instance.getComponentCount(); i++) {
            for (final double component : ROUND_TRIPPING_COMPONENTS) {
                final var index = i;
                instance.setComponent(index, component);
                assertEquals(
                        component,
                        instance.getComponent(index),
                        TOLERANCE,
                        () -> "the component at " + index + " did not survive being written as " + component
                );
            }
        }
    }

    /**
     * Verifies that the maximum component is accepted at every index.
     *
     * @implNote Accepted, and not read back: a hue is circular, so a normalized {@code 1.0} is the same angle as
     *         {@code 0.0} and is reported as the latter. Which index carries a hue is a model's own business, so what
     *         can be said here of every index is that the value is in range.
     */
    @DisplayName("the maximum component is accepted at every index")
    @Test
    protected void _Accepted_MaxComponent() {
        final var instance = newColorInstance();
        for (var i = 0; i < instance.getComponentCount(); i++) {
            final var index = i;
            assertDoesNotThrow(
                    () -> instance.setComponent(index, ___MappedColor.MAX_COMPONENT),
                    () -> "the maximum component was rejected at " + index
            );
        }
    }

    /**
     * Verifies that an index past the last component is rejected, reading and writing alike.
     */
    @DisplayName("an index outside the components is rejected, reading and writing alike")
    @Test
    protected void _IndexOutOfBounds_ComponentIndex() {
        final var instance = newColorInstance();
        final var count = instance.getComponentCount();
        assertThrows(IndexOutOfBoundsException.class, () -> instance.getComponent(count));
        assertThrows(IndexOutOfBoundsException.class, () -> instance.getComponent(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> instance.setComponent(count, 0.0d));
        assertThrows(IndexOutOfBoundsException.class, () -> instance.setComponent(-1, 0.0d));
    }

    /**
     * Verifies that a component outside its range, or not a number at all, is rejected.
     */
    @DisplayName("a component out of range, or NaN, is rejected")
    @Test
    protected void _IllegalArgument_ComponentOutOfRange() {
        final var instance = newColorInstance();
        for (var i = 0; i < instance.getComponentCount(); i++) {
            final var index = i;
            assertThrows(IllegalArgumentException.class, () -> instance.setComponent(index, -0.1d),
                         "a component below the minimum was accepted at " + index);
            assertThrows(IllegalArgumentException.class, () -> instance.setComponent(index, 1.1d),
                         "a component above the maximum was accepted at " + index);
            assertThrows(IllegalArgumentException.class, () -> instance.setComponent(index, Double.NaN),
                         "NaN was accepted as a component at " + index);
        }
    }

    // ---------------------------------------------------------------------------------------------------------- alpha

    /**
     * Verifies that the alpha is in range, and that a fully opaque alpha is reported as opaque.
     */
    @DisplayName("the alpha is in range, and a maximum alpha is opaque")
    @Test
    protected void _InRange_Alpha() {
        final var instance = newColorInstance();
        final var alpha = instance.getAlpha();
        assertTrue(
                alpha >= ___MappedColor.MIN_COMPONENT && alpha <= ___MappedColor.MAX_COMPONENT,
                () -> "the alpha of an instance of " + colorClass + " is out of range: " + alpha
        );
        if (alpha == ___MappedColor.ALPHA_OPAQUE) {
            assertTrue(instance.isOpaque(), "a fully opaque color does not report itself opaque");
        }
    }

    // ----------------------------------------------------------------------------------------------------------- sRGB

    /**
     * Verifies that reading a color back in sRGB gives what was written in sRGB.
     *
     * @implNote Only this direction. A model need not be a bijection with sRGB — CMYK is not, and its
     *         model-to-sRGB-to-model trip projects onto maximum black rather than returning what it started as — but
     *         sRGB into the model and back out is required to be exact, and it is what a caller converting between
     *         two models depends on.
     */
    @DisplayName("sRGB into the model and back out is the identity")
    @Test
    protected void _RoundTrip_Srgb() {
        for (final double[] srgb : SRGB_SAMPLES) {
            final var instance = newColorInstance();
            instance.setSrgb(srgb[0], srgb[1], srgb[2]);
            final var actual = instance.toComponentArrayInSrgb();
            assertEquals(3, actual.length, "an sRGB reading is not three components");
            for (var i = 0; i < 3; i++) {
                final var index = i;
                assertEquals(
                        srgb[index],
                        actual[index],
                        TOLERANCE,
                        () -> "the sRGB component at " + index + " did not survive the conversion into " + colorClass
                );
            }
        }
    }

    /**
     * Verifies that writing sRGB components leaves the alpha alone.
     *
     * @implNote The alpha is not an sRGB component, so a model which carries one keeps it across a conversion. A
     *         model which lost it here would silently turn every converted color opaque.
     */
    @DisplayName("writing sRGB leaves the alpha alone")
    @Test
    protected void _KeepsAlpha_SetSrgb() {
        final var instance = newColorInstance();
        final var alpha = instance.getAlpha();
        instance.setSrgb(0.2d, 0.7d, 0.4d);
        assertEquals(alpha, instance.getAlpha(), TOLERANCE, "writing sRGB changed the alpha");
    }

    /**
     * Verifies that an sRGB component outside its range, or not a number at all, is rejected.
     */
    @DisplayName("an sRGB component out of range, or NaN, is rejected")
    @Test
    protected void _IllegalArgument_SetSrgbOutOfRange() {
        final var instance = newColorInstance();
        assertThrows(IllegalArgumentException.class, () -> instance.setSrgb(1.5d, 0.0d, 0.0d));
        assertThrows(IllegalArgumentException.class, () -> instance.setSrgb(0.0d, -0.1d, 0.0d));
        assertThrows(IllegalArgumentException.class, () -> instance.setSrgb(0.0d, 0.0d, Double.NaN));
    }

    // ------------------------------------------------------------------------------------------- hasSameComponentsAs

    /**
     * Verifies that a color compares by its components, and against nothing else.
     */
    @DisplayName("a color has the same components as one written alike, and not as null")
    @Test
    protected void _ByComponents_HasSameComponentsAs() {
        final var one = newColorInstance();
        one.setSrgb(0.2d, 0.7d, 0.4d);
        final var same = newColorInstance();
        same.setSrgb(0.2d, 0.7d, 0.4d);
        final var other = newColorInstance();
        other.setSrgb(1.0d, 1.0d, 1.0d);
        assertFalse(one.hasSameComponentsAs(null), "a color has the same components as null");
        assertTrue(one.hasSameComponentsAs(one), "a color does not have its own components");
        assertTrue(one.hasSameComponentsAs(same), "two colors written alike do not have the same components");
        assertFalse(one.hasSameComponentsAs(other), "two colors written differently have the same components");
    }

    // ------------------------------------------------------------------------------------------------------ notation

    /**
     * Verifies that each notation is well formed, and that all three agree with {@link ___MappedColor#isOpaque()}
     * about whether the alpha is carried.
     *
     * @implNote The three notations each decide, separately, whether to write the alpha, and each asks
     *         {@link ___MappedColor#isOpaque() isOpaque()} to find out. Asserting the agreement is what catches a
     *         model whose alpha is opaque to one of them and not to the others.
     */
    @DisplayName("the three notations are well formed, and agree about the alpha")
    @Test
    protected void _WellFormed_Notations() {
        final var instance = newColorInstance();
        instance.setSrgb(0.2d, 0.7d, 0.4d);
        final var opaque = instance.isOpaque();

        final var hex = instance.toHexNotation();
        assertNotNull(hex);
        assertTrue(hex.matches(opaque ? "^#[0-9a-f]{6}$" : "^#[0-9a-f]{8}$"),
                   () -> "the hex notation is malformed, or disagrees about the alpha: " + hex);

        final var legacy = instance.toLegacyRgbNotation();
        assertNotNull(legacy);
        assertTrue(
                legacy.matches(opaque
                                       ? "^rgb\\(\\d{1,3}, \\d{1,3}, \\d{1,3}\\)$"
                                       : "^rgba\\(\\d{1,3}, \\d{1,3}, \\d{1,3}, [0-9]*\\.?[0-9]+\\)$"),
                () -> "the legacy notation is malformed, or disagrees about the alpha: " + legacy
        );

        final var modern = instance.toModernRgbNotation();
        assertNotNull(modern);
        assertTrue(
                modern.matches(opaque
                                       ? "^rgb\\(\\d{1,3} \\d{1,3} \\d{1,3}\\)$"
                                       : "^rgb\\(\\d{1,3} \\d{1,3} \\d{1,3} / [0-9]*\\.?[0-9]+\\)$"),
                () -> "the modern notation is malformed, or disagrees about the alpha: " + modern
        );
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance of {@link #colorClass}.
     *
     * @return a new instance of {@link #colorClass}.
     * @implSpec The default implementation invokes the no-argument constructor which Jakarta Persistence requires
     *           every entity to declare.
     */
    protected COLOR newColorInstance() {
        return ___Utils.newInstance(colorClass);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The color class to test.
     */
    protected final Class<COLOR> colorClass;
}
