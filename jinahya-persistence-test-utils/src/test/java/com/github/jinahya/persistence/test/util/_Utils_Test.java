package com.github.jinahya.persistence.test.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link _Utils}, covering the shapes which can, and can not, be instantiated reflectively.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S3577" // Test classes should comply with a naming convention
})
class _Utils_Test {

    /**
     * A record which declares no component, and whose canonical constructor therefore takes no argument.
     */
    record Empty() {

    }

    /**
     * A record which declares components, and a no-argument constructor explicitly.
     *
     * @param x a component.
     * @param y another component.
     */
    record Defaulted(int x, int y) {

        Defaulted() {
            this(0, 0);
        }
    }

    /**
     * A record whose only constructor is a canonical one which takes arguments.
     *
     * @param x a component.
     */
    record Componentized(int x) {

    }

    // ---------------------------------------------------------------------------------------------------------------------
    @DisplayName("a record which has a no-argument constructor is instantiated")
    @ParameterizedTest
    @ValueSource(classes = {Empty.class, Defaulted.class})
    void newInstance__RecordWithANoArgConstructor(final Class<?> clazz) {
        assertThat(_Utils.newInstance(clazz)).isNotNull();
    }

    @DisplayName("a record which has no no-argument constructor is reported as such")
    @Test
    void newInstance_RuntimeException_RecordWithoutANoArgConstructor() {
        assertThatThrownBy(() -> _Utils.newInstance(Componentized.class))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("no no-arg constructor");
    }
}
