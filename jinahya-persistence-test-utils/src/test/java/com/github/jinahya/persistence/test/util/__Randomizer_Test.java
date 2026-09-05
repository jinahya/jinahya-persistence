package com.github.jinahya.persistence.test.util;

import jakarta.annotation.Nonnull;
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
     */
    static class Stamped {

        static final String STAMP = "stamped";

        private Stamped(final String stamp) {
            super();
            this.stamp = stamp;
        }

        String stamp;

        int value;
    }

    static class StampedInstantiator extends __Instantiator<Stamped> {

        StampedInstantiator() {
            super(Stamped.class);
        }

        @Nonnull
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
        assertThat(instance.stamp).isEqualTo(Stamped.STAMP);
    }

    @DisplayName("___OfEasyRandomBean.get() -> an instance which the located instantiator did not create")
    @Test
    void get_NotInstantiatedByTheInstantiator_OfEasyRandomBean() {
        final var instance = new StampedEasyRandomBeanRandomizer().get();
        assertThat(instance).isNotNull();
        // Easy Random creates the instance itself, bypassing every constructor, so the stamp is never applied
        assertThat(instance.stamp).isNull();
    }
}
