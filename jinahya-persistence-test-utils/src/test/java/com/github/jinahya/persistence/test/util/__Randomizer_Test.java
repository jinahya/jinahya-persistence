package com.github.jinahya.persistence.test.util;

import com.navercorp.fixturemonkey.ArbitraryBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
class __Randomizer_Test {

    /**
     * A target class which can only be stamped by a constructor, and, hence, only through its instantiator.
     *
     * @implNote Accessors, and {@code public} visibility, are required by {@link __Randomizer.___OfPodam},
     *         which writes a property through its setter and never assigns a field; a field-only class would be left
     *         entirely unpopulated, making any assertion made here vacuous.
     */
    public static class Stamped {

        static final String STAMP = "stamped";

        private Stamped(final String stamp) {
            super();
            this.stamp = stamp;
        }

        public String getStamp() {
            return stamp;
        }

        public void setStamp(final String stamp) {
            this.stamp = stamp;
        }

        public String getValue() {
            return value;
        }

        public void setValue(final String value) {
            this.value = value;
        }

        String stamp;

        /**
         * A property which no randomizer excludes; a control, for evidence that the randomization actually happened.
         */
        String value;
    }

    static class StampedInstantiator extends __Instantiator<Stamped> {

        StampedInstantiator() {
            super(Stamped.class);
        }

        @Override
        public Stamped get() {
            return new Stamped(Stamped.STAMP);
        }
    }

    static class StampedPodamRandomizer extends __Randomizer.___OfPodam<Stamped> {

        StampedPodamRandomizer() {
            super(Stamped.class, List.of("stamp"));
        }
    }

    static class StampedEasyRandomRandomizer extends __Randomizer.___OfEasyRandom<Stamped> {

        StampedEasyRandomRandomizer() {
            super(Stamped.class, List.of("stamp"));
        }
    }

    /**
     * A randomizer whose seed is a constant, for a reproducible sequence.
     */
    static class StampedSeededRandomizer extends __Randomizer.___OfEasyRandom<Stamped> {

        StampedSeededRandomizer() {
            super(Stamped.class, List.of("stamp"));
        }

        @Override
        protected long getSeed() {
            return 42L;
        }
    }

    static class StampedInstancioRandomizer extends __Randomizer.___OfInstancio<Stamped> {

        StampedInstancioRandomizer() {
            super(Stamped.class, List.of("stamp"));
        }
    }

    /**
     * A randomizer which excludes nothing at all, for evidence that this flavor fills only what is unset.
     */
    static class StampedUnexcludedInstancioRandomizer extends __Randomizer.___OfInstancio<Stamped> {

        StampedUnexcludedInstancioRandomizer() {
            super(Stamped.class, List.of());
        }
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * A target class stamped by a no-argument constructor, which is the shape {@link __Randomizer.___OfFixtureMonkey}
     * requires; {@link Stamped}, whose only constructor takes an argument, that flavor could not construct at all.
     */
    public static class Defaulted {

        static final String STAMP = "stamped";

        public Defaulted() {
            super();
            this.stamp = STAMP;
        }

        String stamp;

        /**
         * A field which no randomizer excludes; a control, for evidence that the randomization actually happened.
         */
        String value;
    }

    static class DefaultedFixtureMonkeyRandomizer extends __Randomizer.___OfFixtureMonkey<Defaulted> {

        DefaultedFixtureMonkeyRandomizer() {
            super(Defaulted.class, List.of("stamp"));
        }

        @Override
        protected Defaulted newTargetInstance() {
            throw new AssertionError("this flavor is not to call newTargetInstance()");
        }
    }

    /**
     * A randomizer which sets a property on the builder, the customization this flavor is picked for.
     */
    static class DefaultedCustomizedRandomizer extends __Randomizer.___OfFixtureMonkey<Defaulted> {

        static final String CUSTOMIZED = "customized";

        DefaultedCustomizedRandomizer() {
            super(Defaulted.class, List.of("stamp"));
        }

        @Override
        protected ArbitraryBuilder<Defaulted> getArbitraryBuilder() {
            return super.getArbitraryBuilder().set("value", CUSTOMIZED);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("___OfPodam.get() -> an instance from the located instantiator")
    @Test
    void get_InstantiatedByTheInstantiator_OfPodam() {
        final var instance = new StampedPodamRandomizer().get();
        assertThat(instance).isNotNull();
        assertThat(instance.value).as("the control property is randomized").isNotNull();
        assertThat(instance.stamp).isEqualTo(Stamped.STAMP);
    }

    @DisplayName("___OfEasyRandom.get() -> an instance which the located instantiator did not create")
    @Test
    void get_NotInstantiatedByTheInstantiator_OfEasyRandom() {
        final var instance = new StampedEasyRandomRandomizer().get();
        assertThat(instance).isNotNull();
        assertThat(instance.value).as("the control property is randomized").isNotNull();
        // Easy Random creates the instance itself, bypassing every constructor, so the stamp is never applied
        assertThat(instance.stamp).isNull();
    }

    /**
     * Each located randomizer is instantiated afresh, so this is the shape in which
     * {@link __RandomizerUtils#newRandomizedInstanceOf(java.lang.Class)} actually generates: the values have to differ
     * between one instance and the next, or a second entity could not be persisted alongside the first under a unique
     * constraint.
     */
    @DisplayName("___OfEasyRandom.get() -> values which differ across randomizer instances")
    @Test
    void get_DistinctValues_OfEasyRandom() {
        final var values = Stream.generate(() -> new StampedEasyRandomRandomizer().get().value)
                .limit(8)
                .collect(Collectors.toSet());
        assertThat(values).as("the control property varies across randomizer instances").hasSize(8);
    }

    @DisplayName("___OfEasyRandom.getSeed() overridden with a constant -> a reproducible sequence")
    @Test
    void get_ReproducibleValues_OfEasyRandomSeededWithAConstant() {
        assertThat(new StampedSeededRandomizer().get().value)
                .isEqualTo(new StampedSeededRandomizer().get().value);
    }

    @DisplayName("___OfInstancio.get() -> an instance from the located instantiator")
    @Test
    void get_InstantiatedByTheInstantiator_OfInstancio() {
        final var instance = new StampedInstancioRandomizer().get();
        assertThat(instance).isNotNull();
        assertThat(instance.value).as("the control property is randomized").isNotNull();
        assertThat(instance.stamp).isEqualTo(Stamped.STAMP);
    }

    /**
     * This flavor fills a {@code null} field, and a primitive still at its default, and nothing else; a value the
     * instantiator has already assigned survives without being named in
     * {@link __Randomizer#excludedFields excludedFields}.
     */
    @DisplayName("___OfInstancio.get() -> an already assigned value, though nothing is excluded")
    @Test
    void get_AssignedValueKept_OfInstancioExcludingNothing() {
        final var instance = new StampedUnexcludedInstancioRandomizer().get();
        assertThat(instance.value).as("the unset control property is randomized").isNotNull();
        assertThat(instance.stamp).as("the property the instantiator assigned is left alone").isEqualTo(Stamped.STAMP);
    }

    @DisplayName("___OfFixtureMonkey.get() -> an instance the engine constructed itself, keeping its stamp")
    @Test
    void get_NotInstantiatedByTheInstantiator_OfFixtureMonkey() {
        // newTargetInstance() throws, so this passes only while the flavor never calls it; the engine goes through the
        // no-argument constructor instead, which is why the excluded field carries the value that constructor assigned
        final var instance = new DefaultedFixtureMonkeyRandomizer().get();
        assertThat(instance).isNotNull();
        assertThat(instance.value).as("the control field is randomized").isNotNull();
        assertThat(instance.stamp).isEqualTo(Defaulted.STAMP);
    }

    @DisplayName("___OfFixtureMonkey.getArbitraryBuilder() overridden -> the property the builder sets")
    @Test
    void get_CustomizedValue_OfFixtureMonkeyWithAnOverriddenBuilder() {
        final var instance = new DefaultedCustomizedRandomizer().get();
        assertThat(instance.value).isEqualTo(DefaultedCustomizedRandomizer.CUSTOMIZED);
        assertThat(instance.stamp).isEqualTo(Defaulted.STAMP);
    }
}
