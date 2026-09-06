package com.github.jinahya.persistence.more;

import jakarta.persistence.Transient;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link __SelfReferencing}, pinning what its interface-level annotations do and do not achieve.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S3577" // Test classes should comply with a naming convention
})
class __SelfReferencing_Test {

    private static jakarta.validation.ValidatorFactory FACTORY;

    @BeforeAll
    static void openFactory() {
        FACTORY = Validation.buildDefaultValidatorFactory();
    }

    @AfterAll
    static void closeFactory() {
        if (FACTORY != null) {
            FACTORY.close();
        }
    }

    private static Validator validator() {
        return FACTORY.getValidator();
    }

    /**
     * An implementation which declares its own accessors and annotates neither of them.
     */
    static class Node implements __SelfReferencing<Node> {

        private final int depth;

        Node(final int depth) {
            this.depth = depth;
        }

        @Nullable
        @Override
        public Node getHierarchyParent() {
            return null;
        }

        @Override
        public int getHierarchyDepth() {
            return depth;
        }
    }

    @DisplayName("a constraint declared on the interface IS inherited by an implementation")
    @Test
    void __constraintIsInherited() {
        // Jakarta Validation inherits constraint declarations from implemented interfaces, so
        // @PositiveOrZero on the interface getter really does constrain an implementation
        assertThat(validator().validate(new Node(-1)))
                .as("a negative depth violates @PositiveOrZero declared on the interface")
                .isNotEmpty();
        assertThat(validator().validate(new Node(0))).isEmpty();
    }

    @DisplayName("@Transient on the interface is NOT visible on an implementation's own accessor")
    @Test
    void __transientIsNotInherited() throws Exception {
        // Jakarta Persistence reads mapping annotations from the entity class and its mapped
        // superclasses; an implemented interface is not part of the mapping. So an entity which
        // declares its own getter gets no @Transient from here, whatever the interface says.
        final var declared = Node.class.getDeclaredMethod("getHierarchyDepth");

        assertThat(declared.getAnnotation(Transient.class))
                .as("the implementation's own accessor carries no @Transient")
                .isNull();
    }
}
