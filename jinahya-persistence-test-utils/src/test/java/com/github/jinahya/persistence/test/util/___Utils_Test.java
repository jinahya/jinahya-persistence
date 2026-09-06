package com.github.jinahya.persistence.test.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link ___Utils}, covering the whitespace handling of the postfix probe.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S3577" // Test classes should comply with a naming convention
})
class ___Utils_Test {

    static class Target {

    }

    static class TargetRandomizer extends __Randomizer.___OfEasyRandomBean<Target> {

        TargetRandomizer() {
            super(Target.class, java.util.List.of());
        }
    }

    @DisplayName("a blank postfix is skipped, whatever kind of whitespace it is")
    @ParameterizedTest
    // ordinary space, tab, newline, and U+2003 EM SPACE, which Character.isWhitespace accepts
    @ValueSource(strings = {"", " ", "\t", "\n", " ", "  \t  "})
    void __blankPostfixIsSkipped(final String postfix) {
        assertThat(___Utils.siblingClassForPostfix(Target.class, null, postfix)).isNull();
    }

    @DisplayName("a non-blank postfix is stripped before the name is built")
    @Test
    void __postfixIsStrippedForTheName() {
        // "  Randomizer  " has to locate TargetRandomizer, i.e. the surrounding whitespace is
        // normalized away rather than becoming part of the class name
        assertThat(___Utils.siblingClassForPostfix(Target.class, __Randomizer.class, "  Randomizer  "))
                .isEqualTo(TargetRandomizer.class);
    }

    @DisplayName("an all-blank postfix list finds nothing rather than probing an empty name")
    @Test
    void __allBlank() {
        assertThat(___Utils.siblingClassForPostfix(Target.class, null, " ", "\t")).isNull();
    }

    @DisplayName("no postfixes at all is rejected")
    @Test
    void _IllegalArgumentException_NoPostfixes() {
        assertThatThrownBy(() -> ___Utils.siblingClassForPostfix(Target.class, null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
