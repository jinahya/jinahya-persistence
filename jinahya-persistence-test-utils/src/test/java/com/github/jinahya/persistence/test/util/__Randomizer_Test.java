package com.github.jinahya.persistence.test.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

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

    static class StampedEasyRandomBeanRandomizer extends __Randomizer.___OfEasyRandomBean<Stamped> {

        StampedEasyRandomBeanRandomizer() {
            super(Stamped.class, List.of("stamp"));
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

    @DisplayName("___OfEasyRandomBean.get() -> an instance which the located instantiator did not create")
    @Test
    void get_NotInstantiatedByTheInstantiator_OfEasyRandomBean() {
        final var instance = new StampedEasyRandomBeanRandomizer().get();
        assertThat(instance).isNotNull();
        assertThat(instance.value).as("the control property is randomized").isNotNull();
        // Easy Random creates the instance itself, bypassing every constructor, so the stamp is never applied
        assertThat(instance.stamp).isNull();
    }
}
