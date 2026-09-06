package com.github.jinahya.persistence.test.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests that {@link __Randomizer#excludedFields} are honored by both flavors, for the shapes a JPA entity actually
 * takes: a field inherited from a mapped superclass, and an instance whose runtime class is a subclass of the
 * {@link __Randomizer#targetClass targetClass}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
class __Randomizer_Exclusion_Test {

    /**
     * A base which declares the excluded property, standing in for a {@code @MappedSuperclass}.
     */
    public static class Base {

        private Long id;

        public Long getId() {
            return id;
        }

        public void setId(final Long id) {
            this.id = id;
        }
    }

    /**
     * A target which inherits the excluded property, and declares an unexcluded one as a control.
     */
    public static class Derived extends Base {

        private String name;

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }
    }

    /**
     * A separate target, whose located instantiator yields a subclass of it. Kept apart from {@link Derived} so that
     * the inherited-field case and the subclass-instance case are exercised independently; the standard locator
     * resolves an instantiator by name, and would otherwise apply it to both.
     */
    public static class Instantiated extends Base {

        private String name;

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }
    }

    public static class InstantiatedSub extends Instantiated {

    }

    /**
     * Located, by the standard locator, for {@link Instantiated}; yields a subclass of it, which
     * {@link ___Utils#canProduce(Class, Class, Object) producer covariance} explicitly permits.
     */
    static class InstantiatedInstantiator extends __Instantiator<Instantiated> {

        InstantiatedInstantiator() {
            super(Instantiated.class);
        }

        @Override
        public Instantiated get() {
            return new InstantiatedSub();
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("___OfPodam")
    @Nested
    class OfPodamTest {

        class DerivedRandomizer extends __Randomizer.___OfPodam<Derived> {

            DerivedRandomizer() {
                super(Derived.class, List.of("id"));
            }
        }

        @DisplayName("an inherited field named in excludedFields is not randomized")
        @Test
        void excluded_Inherited() {
            final var instance = new DerivedRandomizer().get();
            assertThat(instance.getName()).as("the control property is randomized").isNotNull();
            assertThat(instance.getId()).as("the inherited, excluded property is left alone").isNull();
        }

        class InstantiatedRandomizer extends __Randomizer.___OfPodam<Instantiated> {

            InstantiatedRandomizer() {
                super(Instantiated.class, List.of("id"));
            }
        }

        @DisplayName("an excluded field is not randomized when the instance is a subclass of the target")
        @Test
        void excluded_SubclassInstance() {
            // the instantiator located for Instantiated yields an InstantiatedSub; PODAM introspects the runtime
            // class, so the exclusions have to apply to the subclass as well
            final var instance = new InstantiatedRandomizer().get();
            assertThat(instance).isInstanceOf(InstantiatedSub.class);
            assertThat(instance.getName()).as("the control property is randomized").isNotNull();
            assertThat(instance.getId()).as("the excluded property is left alone").isNull();
        }
    }

    @DisplayName("___OfEasyRandomBean")
    @Nested
    class OfEasyRandomBeanTest {

        class DerivedRandomizer extends __Randomizer.___OfEasyRandomBean<Derived> {

            DerivedRandomizer() {
                super(Derived.class, List.of("id"));
            }
        }

        @DisplayName("an inherited field named in excludedFields is not randomized")
        @Test
        void excluded_Inherited() {
            final var instance = new DerivedRandomizer().get();
            assertThat(instance.getName()).as("the control property is randomized").isNotNull();
            assertThat(instance.getId()).as("the inherited, excluded property is left alone").isNull();
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * A field-only class, the ordinary shape of an entity mapped with field access.
     */
    public static class FieldOnly {

        Long id;

        String name;
    }

    static class FieldOnlyPodamRandomizer extends __Randomizer.___OfPodam<FieldOnly> {

        FieldOnlyPodamRandomizer() {
            super(FieldOnly.class, List.of("id"));
        }
    }

    static class FieldOnlyEasyRandomizer extends __Randomizer.___OfEasyRandomBean<FieldOnly> {

        FieldOnlyEasyRandomizer() {
            super(FieldOnly.class, List.of("id"));
        }
    }

    @DisplayName("___OfPodam leaves a field-only class entirely unpopulated; a documented limitation")
    @Test
    void fieldOnly_NotPopulated_OfPodam() {
        final var instance = new FieldOnlyPodamRandomizer().get();
        // PODAM writes through setters only, so neither the excluded nor the control field is touched
        assertThat(instance.name).isNull();
        assertThat(instance.id).isNull();
    }

    @DisplayName("___OfEasyRandomBean populates a field-only class, honoring the exclusion")
    @Test
    void fieldOnly_Populated_OfEasyRandomBean() {
        final var instance = new FieldOnlyEasyRandomizer().get();
        assertThat(instance.name).as("the control field is randomized").isNotNull();
        assertThat(instance.id).as("the excluded field is left alone").isNull();
    }
}
