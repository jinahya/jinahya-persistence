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

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

/**
 * Verifies that the color mapped superclasses map as declared against a real persistence provider.
 * <p>
 * Everything else about this package is checked by compiling and by reading the generated metamodel. This is the only
 * place where a provider actually creates the tables, writes the columns and reads them back.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
class ___MappedColor_PersistenceTest {

    private static final double TOLERANCE = 1.0e-12d;

    private static EntityManagerFactory ENTITY_MANAGER_FACTORY;

    @BeforeAll
    static void openEntityManagerFactory() {
        ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory("__morePU");
    }

    @AfterAll
    static void closeEntityManagerFactory() {
        if (ENTITY_MANAGER_FACTORY != null) {
            ENTITY_MANAGER_FACTORY.close();
        }
    }

    private static <R> R applyEntityManager(final Function<? super EntityManager, ? extends R> function) {
        try (final var entityManager = ENTITY_MANAGER_FACTORY.createEntityManager()) {
            final var transaction = entityManager.getTransaction();
            transaction.begin();
            try {
                final var result = function.apply(entityManager);
                transaction.commit();
                return result;
            } catch (final Exception e) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
                throw e;
            }
        }
    }

    private static void acceptEntityManager(final Consumer<? super EntityManager> consumer) {
        applyEntityManager(em -> {
            consumer.accept(em);
            return null;
        });
    }

    /**
     * Persists specified entity, detaches it, reads it back, and applies the managed copy to specified function.
     */
    private static <T, R> R persistAndFind(final T entity, final Class<T> entityClass,
                                           final Function<? super T, ? extends R> function) {
        final var id = applyEntityManager(em -> {
            em.persist(entity);
            em.flush();
            return em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(entity);
        });
        return applyEntityManager(em -> function.apply(em.find(entityClass, id)));
    }

    /**
     * Runs specified action, and returns the {@link ConstraintViolationException} it failed with.
     * <p>
     * The exception is searched for down the cause chain, and the violations are read from the exception itself rather
     * than from its message: EclipseLink names only the entity in the message, where Hibernate names the property too.
     */
    private static ConstraintViolationException constraintViolationOf(final Runnable runnable) {
        try {
            runnable.run();
        } catch (final Exception e) {
            for (Throwable c = e; c != null; c = c.getCause()) {
                if (c instanceof ConstraintViolationException cve) {
                    return cve;
                }
            }
            throw new AssertionError("not a constraint violation", e);
        }
        throw new AssertionError("no constraint violation was raised");
    }

    private static List<String> columnNamesOf(final String tableName) {
        // untyped: EclipseLink reads a result class as an entity class, and has no descriptor for String
        final List<?> rows = applyEntityManager(em -> em
                .createNativeQuery(
                        // uppercase: database_to_upper=false keeps identifiers verbatim, and H2's own
                        // schema is upper case
                        "select lower(COLUMN_NAME) from INFORMATION_SCHEMA.COLUMNS"
                        + " where lower(TABLE_NAME) = lower(?1)")
                .setParameter(1, tableName)
                .getResultList()
        );
        return rows.stream().map(String::valueOf).toList();
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Nested
    class ColumnsTest {

        @DisplayName("__MappedRgba maps exactly red, green, blue and alpha")
        @Test
        void rgbaColumns__() {
            assertThat(columnNamesOf(_RgbaEntity.TABLE_NAME))
                    .contains(__MappedRgb.COLUMN_NAME_RED, __MappedRgb.COLUMN_NAME_GREEN,
                              __MappedRgb.COLUMN_NAME_BLUE, __MappedRgba.COLUMN_NAME_ALPHA);
        }

        @DisplayName("__MappedHsl maps the inherited hue, plus saturation and lightness")
        @Test
        void hslColumns__() {
            assertThat(columnNamesOf(_HslEntity.TABLE_NAME))
                    .contains(___MappedHueColor.COLUMN_NAME_HUE, __MappedHsl.COLUMN_NAME_SATURATION,
                              __MappedHsl.COLUMN_NAME_LIGHTNESS);
        }

        @DisplayName("__MappedHwb maps the inherited hue, plus whiteness and blackness")
        @Test
        void hwbColumns__() {
            assertThat(columnNamesOf(_HwbEntity.TABLE_NAME))
                    .contains(___MappedHueColor.COLUMN_NAME_HUE, __MappedHwb.COLUMN_NAME_WHITENESS,
                              __MappedHwb.COLUMN_NAME_BLACKNESS);
        }

        @DisplayName("__MappedCmyk maps all four ink columns")
        @Test
        void cmykColumns__() {
            assertThat(columnNamesOf(_CmykEntity.TABLE_NAME))
                    .contains(__MappedCmyk.COLUMN_NAME_CYAN, __MappedCmyk.COLUMN_NAME_MAGENTA,
                              __MappedCmyk.COLUMN_NAME_YELLOW, __MappedCmyk.COLUMN_NAME_BLACK);
        }

        @DisplayName("no @Transient accessor leaks into a column")
        @Test
        void transientAccessorsAreNotMapped__() {
            assertThat(columnNamesOf(_RgbaEntity.TABLE_NAME))
                    .doesNotContain("opaque", "componentcount", "component", "redaseightbits",
                                    "componentarray", "hexnotation");
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Nested
    class RoundTripTest {

        @DisplayName("an rgba color survives a write and a read, alpha included")
        @Test
        void rgba__() {
            final var entity = new _RgbaEntity();
            entity.setSrgb(.25d, .5d, .75d);
            entity.setAlpha(.125d);
            persistAndFind(entity, _RgbaEntity.class, found -> {
                assertThat(found).isNotNull();
                assertThat(found.getRed()).isCloseTo(.25d, within(TOLERANCE));
                assertThat(found.getGreen()).isCloseTo(.5d, within(TOLERANCE));
                assertThat(found.getBlue()).isCloseTo(.75d, within(TOLERANCE));
                assertThat(found.getAlpha()).isCloseTo(.125d, within(TOLERANCE));
                assertThat(found.toHexNotation()).isEqualTo(entity.toHexNotation());
                return null;
            });
        }

        @DisplayName("an alpha left alone is persisted opaque, not zero")
        @Test
        void alphaDefaultsToOpaque__() {
            final var entity = new _RgbaEntity();
            entity.setSrgb(1.0d, .0d, .0d);
            persistAndFind(entity, _RgbaEntity.class, found -> {
                assertThat(found.getAlpha()).isEqualTo(___MappedColor.ALPHA_OPAQUE);
                assertThat(found.isOpaque()).isTrue();
                return null;
            });
        }

        @DisplayName("a hue is stored in degrees, not normalized")
        @Test
        void hslHueIsDegrees__() {
            final var entity = new _HslEntity();
            entity.setHue(240.0d);
            entity.setSaturation(1.0d);
            entity.setLightness(.5d);
            persistAndFind(entity, _HslEntity.class, found -> {
                assertThat(found.getHue()).isCloseTo(240.0d, within(TOLERANCE));
                assertThat(found.getNormalizedHue()).isCloseTo(2.0d / 3.0d, within(TOLERANCE));
                assertThat(found.toModernHslNotation()).isEqualTo("hsl(240 100% 50%)");
                return null;
            });
            // and the column really holds the degrees
            final List<?> hues = applyEntityManager(em -> em
                    .createNativeQuery("select " + ___MappedHueColor.COLUMN_NAME_HUE
                                       + " from " + _HslEntity.TABLE_NAME)
                    .getResultList());
            assertThat(hues).isNotEmpty().allSatisfy(
                    v -> assertThat(((Number) v).doubleValue()).isCloseTo(240.0d, within(TOLERANCE))
            );
        }

        @DisplayName("an hwb color survives a write and a read")
        @Test
        void hwb__() {
            final var entity = new _HwbEntity();
            entity.setHue(90.0d);
            entity.setWhiteness(.2d);
            entity.setBlackness(.3d);
            persistAndFind(entity, _HwbEntity.class, found -> {
                assertThat(found.getHue()).isCloseTo(90.0d, within(TOLERANCE));
                assertThat(found.getWhiteness()).isCloseTo(.2d, within(TOLERANCE));
                assertThat(found.getBlackness()).isCloseTo(.3d, within(TOLERANCE));
                return null;
            });
        }

        @DisplayName("a cmyk color survives a write and a read")
        @Test
        void cmyk__() {
            final var entity = new _CmykEntity();
            entity.setSrgb(.2d, .7d, .4d);
            final var expected = entity.toComponentArray();
            persistAndFind(entity, _CmykEntity.class, found -> {
                assertThat(found.toComponentArray())
                        .usingComparatorWithPrecision(TOLERANCE)
                        .containsExactly(expected);
                return null;
            });
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Nested
    class AccessTypeTest {

        @DisplayName("an entity with @Id on a getter still persists every component")
        @Test
        void idOnGetterDoesNotUnmapComponents__() {
            assertThat(columnNamesOf(_PropertyAccessRgbaEntity.TABLE_NAME))
                    .as("without @Access(FIELD) on the superclasses these columns would not exist")
                    .contains(__MappedRgb.COLUMN_NAME_RED, __MappedRgb.COLUMN_NAME_GREEN,
                              __MappedRgb.COLUMN_NAME_BLUE, __MappedRgba.COLUMN_NAME_ALPHA);
            final var entity = new _PropertyAccessRgbaEntity();
            entity.setSrgb(.1d, .2d, .3d);
            entity.setAlpha(.4d);
            persistAndFind(entity, _PropertyAccessRgbaEntity.class, found -> {
                assertThat(found.getRed()).isCloseTo(.1d, within(TOLERANCE));
                assertThat(found.getGreen()).isCloseTo(.2d, within(TOLERANCE));
                assertThat(found.getBlue()).isCloseTo(.3d, within(TOLERANCE));
                assertThat(found.getAlpha()).isCloseTo(.4d, within(TOLERANCE));
                return null;
            });
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Nested
    class EmbeddableTest {

        @DisplayName("the embeddable form: two colors in one table, each overriding its three column names")
        @Test
        void twoColorsInOneTable__() {
            final var foreground = new _RgbEmbeddable();
            foreground.setSrgb(1.0d, .5d, .25d);
            final var background = new _RgbEmbeddable();
            background.setSrgb(.0d, .125d, 1.0d);
            final var entity = new _ThemeEntity();
            entity.setForeground(foreground);
            entity.setBackground(background);
            persistAndFind(entity, _ThemeEntity.class, found -> {
                assertThat(found.getForeground()).isNotNull();
                assertThat(found.getForeground().getRed()).isCloseTo(1.0d, within(TOLERANCE));
                assertThat(found.getForeground().getGreen()).isCloseTo(.5d, within(TOLERANCE));
                assertThat(found.getForeground().getBlue()).isCloseTo(.25d, within(TOLERANCE));
                assertThat(found.getBackground()).isNotNull();
                assertThat(found.getBackground().getRed()).isCloseTo(.0d, within(TOLERANCE));
                assertThat(found.getBackground().getGreen()).isCloseTo(.125d, within(TOLERANCE));
                assertThat(found.getBackground().getBlue()).isCloseTo(1.0d, within(TOLERANCE));
                return null;
            });
            // the overridden names are the ones in the schema, and the inherited ones are not there at all
            assertThat(columnNamesOf(_ThemeEntity.TABLE_NAME))
                    .contains(_ThemeEntity.COLUMN_NAME_FOREGROUND_RED, _ThemeEntity.COLUMN_NAME_FOREGROUND_GREEN,
                              _ThemeEntity.COLUMN_NAME_FOREGROUND_BLUE, _ThemeEntity.COLUMN_NAME_BACKGROUND_RED,
                              _ThemeEntity.COLUMN_NAME_BACKGROUND_GREEN, _ThemeEntity.COLUMN_NAME_BACKGROUND_BLUE)
                    .doesNotContain(__MappedRgb.COLUMN_NAME_RED, __MappedRgb.COLUMN_NAME_GREEN,
                                    __MappedRgb.COLUMN_NAME_BLUE);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Nested
    class AttributeOverrideTest {

        @DisplayName("@Access(FIELD) on the superclasses leaves @AttributeOverride working, at any depth")
        @Test
        void inheritedColumnsCanBeRenamed__() {
            final var columns = columnNamesOf(_OverriddenHslEntity.TABLE_NAME);
            assertThat(columns)
                    .as("hue is declared two levels up, in ___MappedHueColor")
                    .contains(_OverriddenHslEntity.COLUMN_NAME_HUE)
                    .doesNotContain(___MappedHueColor.COLUMN_NAME_HUE);
            assertThat(columns)
                    .as("saturation is declared one level up, in __MappedHsl")
                    .contains(_OverriddenHslEntity.COLUMN_NAME_SATURATION)
                    .doesNotContain(__MappedHsl.COLUMN_NAME_SATURATION);
            assertThat(columns)
                    .as("lightness was not overridden, and keeps its declared name")
                    .contains(__MappedHsl.COLUMN_NAME_LIGHTNESS);
        }

        @DisplayName("a renamed column still round-trips")
        @Test
        void renamedColumnsRoundTrip__() {
            final var entity = new _OverriddenHslEntity();
            entity.setHue(300.0d);
            entity.setSaturation(.6d);
            entity.setLightness(.4d);
            persistAndFind(entity, _OverriddenHslEntity.class, found -> {
                assertThat(found.getHue()).isCloseTo(300.0d, within(TOLERANCE));
                assertThat(found.getSaturation()).isCloseTo(.6d, within(TOLERANCE));
                assertThat(found.getLightness()).isCloseTo(.4d, within(TOLERANCE));
                return null;
            });
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Nested
    class ValidationTest {

        @DisplayName("@DecimalMin/@DecimalMax on a component are evaluated on persist")
        @Test
        void outOfRangeComponent__ConstraintViolationException() throws ReflectiveOperationException {
            final var entity = new _RgbaEntity();
            entity.setSrgb(.5d, .5d, .5d);
            // the setters cannot produce this; a value arriving straight into the field can
            final var field = __MappedRgb.class.getDeclaredField(__MappedRgb.ATTRIBUTE_NAME_RED);
            field.setAccessible(true);
            field.setDouble(entity, 1.5d);
            final var violations = constraintViolationOf(() -> acceptEntityManager(em -> em.persist(entity)))
                    .getConstraintViolations();
            assertThat(violations)
                    .extracting(v -> v.getPropertyPath().toString())
                    .contains(__MappedRgb.ATTRIBUTE_NAME_RED);
        }

        @DisplayName("a hue of exactly 360 is rejected: the range is half-open")
        @Test
        void hueOf360__ConstraintViolationException() throws ReflectiveOperationException {
            final var entity = new _HslEntity();
            final var field = ___MappedHueColor.class.getDeclaredField(___MappedHueColor.ATTRIBUTE_NAME_HUE);
            field.setAccessible(true);
            field.setDouble(entity, ___MappedHueColor.MAX_HUE);
            final var violations = constraintViolationOf(() -> acceptEntityManager(em -> em.persist(entity)))
                    .getConstraintViolations();
            assertThat(violations)
                    .extracting(v -> v.getPropertyPath().toString())
                    .contains(___MappedHueColor.ATTRIBUTE_NAME_HUE);
        }
    }
}
