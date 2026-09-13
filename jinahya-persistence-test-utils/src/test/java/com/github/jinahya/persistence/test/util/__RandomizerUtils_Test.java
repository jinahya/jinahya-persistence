package com.github.jinahya.persistence.test.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
class __RandomizerUtils_Test {

    static class Bean {

    }

    /**
     * A randomizer whose exclusions are merged, and, hence, carry blank, duplicate, and {@code null} elements.
     */
    static class BeanRandomizer extends __Randomizer<Bean> {

        BeanRandomizer() {
            super(
                    Bean.class,
                    __RandomizerUtils.moreExcludedFields(
                            List.of(" a ", "  "),
                            Arrays.asList("a", null, "b")
                    )
            );
        }

        @Override
        public Bean get() {
            return new Bean();
        }

        Set<String> excludedFields() {
            return excludedFields;
        }
    }

//SEP:producer covariance -- a randomizer is located by name, then checked against the class it is declared for

    /**
     * A superclass whose conventionally named randomizer is declared for a subclass of it, which a randomizer of the
     * superclass may be: every instance it produces is a {@code Sup}.
     */
    static class Sup {

    }

    static class Sub extends Sup {

    }

    static class SupRandomizer extends __Randomizer<Sub> {

        SupRandomizer() {
            super(Sub.class, List.of());
        }

        @Override
        public Sub get() {
            return new Sub();
        }
    }

    /**
     * A class whose conventionally named randomizer is declared for its superclass, which can not produce instances of
     * it.
     */
    static class Narrowed extends Sup {

    }

    static class NarrowedRandomizer extends __Randomizer<Sup> {

        NarrowedRandomizer() {
            super(Sup.class, List.of());
        }

        @Override
        public Sup get() {
            return new Sup();
        }
    }

    /**
     * A class whose conventionally named randomizer is declared for an unrelated class.
     */
    static class Unrelated {

    }

    static class Foreign {

    }

    static class UnrelatedRandomizer extends __Randomizer<Foreign> {

        UnrelatedRandomizer() {
            super(Foreign.class, List.of());
        }

        @Override
        public Foreign get() {
            return new Foreign();
        }
    }

    // ---------------------------------------------------------------------------------------------------------------------
    @DisplayName("moreExcludedFields(a, b) -> a, then b, as they are")
    @Test
    void moreExcludedFields_Concatenated_() {
        assertThat(__RandomizerUtils.moreExcludedFields(List.of("a", "b"), List.of("b", "c")))
                .containsExactly("a", "b", "b", "c");
    }

    @DisplayName("moreExcludedFields(empty, empty) -> empty")
    @Test
    void moreExcludedFields_Empty_Empty() {
        assertThat(__RandomizerUtils.moreExcludedFields(List.of(), List.of())).isEmpty();
    }

    @DisplayName("moreExcludedFields(null, _) / moreExcludedFields(_, null) -> NullPointerException")
    @Test
    void moreExcludedFields_NullPointerException_Null() {
        assertThatThrownBy(() -> __RandomizerUtils.moreExcludedFields(null, List.of()))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> __RandomizerUtils.moreExcludedFields(List.of(), null))
                .isInstanceOf(NullPointerException.class);
    }

    @DisplayName("the randomizer constructor strips, drops blank/null, and deduplicates the merged exclusions")
    @Test
    void excludedFields_StrippedDedupedWithoutBlanks_Merged() {
        assertThat(new BeanRandomizer().excludedFields()).containsExactlyInAnyOrder("a", "b");
    }

    @DisplayName("newRandomizedInstanceOf(Bean.class) -> present, from the sibling BeanRandomizer")
    @Test
    void newRandomizedInstanceOf_Present_Bean() {
        assertThat(__RandomizerUtils.newRandomizedInstanceOf(Bean.class))
                .isPresent()
                .containsInstanceOf(Bean.class);
    }

    @DisplayName("newRandomizedInstanceOf(Sup.class) -> present; a randomizer declared for a subclass produces a Sup")
    @Test
    void newRandomizedInstanceOf_Present_RandomizerOfSubclass() {
        assertThat(__RandomizerUtils.newRandomizedInstanceOf(Sup.class))
                .isPresent()
                .containsInstanceOf(Sub.class);
    }

    @DisplayName("newRandomizedInstanceOf(Narrowed.class) -> RuntimeException;"
                 + " NarrowedRandomizer was provided, so producing a Sup is a fault, not an absence")
    @Test
    void newRandomizedInstanceOf_RuntimeException_RandomizerProducesASuperclass() {
        assertThatThrownBy(() -> __RandomizerUtils.newRandomizedInstanceOf(Narrowed.class))
                .isExactlyInstanceOf(RuntimeException.class)
                .hasMessageContaining("produced a");
    }

    @DisplayName("newRandomizedInstanceOf(Unrelated.class) -> RuntimeException;"
                 + " UnrelatedRandomizer was provided, so producing a Foreign is a fault, not an absence")
    @Test
    void newRandomizedInstanceOf_RuntimeException_RandomizerProducesAnUnrelatedClass() {
        assertThatThrownBy(() -> __RandomizerUtils.newRandomizedInstanceOf(Unrelated.class))
                .isExactlyInstanceOf(RuntimeException.class)
                .hasMessageContaining("produced a");
    }
}
