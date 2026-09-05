package com.github.jinahya.persistence.test.util;

import jakarta.annotation.Nonnull;
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

        @Nonnull
        @Override
        public Bean get() {
            return new Bean();
        }

        Set<String> excludedFields() {
            return excludedFields;
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
}
