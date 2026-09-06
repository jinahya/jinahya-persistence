package com.github.jinahya.persistence.test.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
class __RandomizerLocator_Test {

    // ------------------------------------------------------------------------------------- an annotated target class
    @__RandomizerClass(SupRandomizer.class)
    static class Sup {

    }

    static class SupRandomizer extends __Randomizer<Sup> {

        SupRandomizer() {
            super(Sup.class, List.of());
        }

        @Override
        public Sup get() {
            return new Sup();
        }
    }

    /**
     * A subclass of an annotated class, which has its own, conventionally named, randomizer.
     */
    static class Sub extends Sup {

    }

    static class SubRandomizer extends __Randomizer<Sub> {

        SubRandomizer() {
            super(Sub.class, List.of());
        }

        @Override
        public Sub get() {
            return new Sub();
        }
    }

    // ---------------------------------------------------------------- a target class enclosing its own randomizer
    static class Enclosing {

        static class EnclosingRandomizer extends __Randomizer<Enclosing> {

            EnclosingRandomizer() {
                super(Enclosing.class, List.of());
            }

            @Override
            public Enclosing get() {
                return new Enclosing();
            }
        }
    }

    // ------------------------------------------------------------------------------------------- a misconfigured one
    @__RandomizerClass(String.class)
    static class Misannotated {

    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("STANDARD.apply(Sup.class) -> SupRandomizer, from the annotation")
    @Test
    void standard_SupRandomizer_Sup() {
        assertThat(__RandomizerLocator.STANDARD.apply(Sup.class)).isSameAs(SupRandomizer.class);
    }

    @DisplayName("STANDARD.apply(Sub.class) -> SubRandomizer; the annotation is not inherited")
    @Test
    void standard_SubRandomizer_Sub() {
        assertThat(__RandomizerLocator.STANDARD.apply(Sub.class)).isSameAs(SubRandomizer.class);
    }

    @DisplayName("newRandomizedInstanceOf(Sub.class) -> a Sub; the annotation is not inherited")
    @Test
    void newRandomizedInstanceOf_Sub_Sub() {
        assertThat(__RandomizerUtils.newRandomizedInstanceOf(Sub.class))
                .isPresent()
                .containsInstanceOf(Sub.class);
    }

    @DisplayName("STANDARD.apply(Enclosing.class) -> Enclosing$EnclosingRandomizer")
    @Test
    void standard_EnclosedRandomizer_Enclosing() {
        assertThat(__RandomizerLocator.STANDARD.apply(Enclosing.class))
                .isSameAs(Enclosing.EnclosingRandomizer.class);
    }

    @DisplayName("STANDARD.apply(Misannotated.class) -> null; the annotated value is not a randomizer")
    @Test
    void standard_Null_Misannotated() {
        assertThat(__RandomizerLocator.STANDARD.apply(Misannotated.class)).isNull();
    }
}
