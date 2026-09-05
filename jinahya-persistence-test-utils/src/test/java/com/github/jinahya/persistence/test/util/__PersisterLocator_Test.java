package com.github.jinahya.persistence.test.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
class __PersisterLocator_Test {

//SEP:a sibling persister, of either postfix

    static class Ent {

    }

    static class EntPersister extends __Persister<Ent> {

        EntPersister() {
            super(Ent.class);
        }
    }

    static class Underscored {

    }

    static class Underscored_Persister extends __Persister<Underscored> {

        Underscored_Persister() {
            super(Underscored.class);
        }
    }

//SEP:a persister nested in its own entity class

    static class Nesting {

        static class NestingPersister extends __Persister<Nesting> {

            NestingPersister() {
                super(Nesting.class);
            }
        }
    }

//SEP:a persister nested in the persister of the enclosing class

    static class Outer {

        static class Inner {

        }
    }

    static class OuterPersister extends __Persister<Outer> {

        OuterPersister() {
            super(Outer.class);
        }

        static class InnerPersister extends __Persister<Outer.Inner> {

            InnerPersister() {
                super(Outer.Inner.class);
            }
        }
    }

//SEP:a class following no convention

    static class Bare {

    }

// ---------------------------------------------------------------------------------------------------------------------
    @DisplayName("STANDARD.apply(Ent.class) -> EntPersister")
    @Test
    void standard_EntPersister_Ent() {
        assertThat(__PersisterLocator.STANDARD.apply(Ent.class)).isSameAs(EntPersister.class);
    }

    @DisplayName("STANDARD.apply(Underscored.class) -> Underscored_Persister")
    @Test
    void standard_UnderscoredPersister_Underscored() {
        assertThat(__PersisterLocator.STANDARD.apply(Underscored.class)).isSameAs(Underscored_Persister.class);
    }

    @DisplayName("STANDARD.apply(Nesting.class) -> Nesting$NestingPersister")
    @Test
    void standard_NestedPersister_Nesting() {
        assertThat(__PersisterLocator.STANDARD.apply(Nesting.class)).isSameAs(Nesting.NestingPersister.class);
    }

    @DisplayName("STANDARD.apply(Outer.Inner.class) -> null;"
                 + " unlike the other locators, the enclosing chain is not consulted")
    @Test
    void standard_Null_EnclosingChainNotConsulted() {
        assertThat(__PersisterLocator.STANDARD.apply(Outer.Inner.class)).isNull();
    }

    @DisplayName("STANDARD.apply(Bare.class) -> null")
    @Test
    void standard_Null_Bare() {
        assertThat(__PersisterLocator.STANDARD.apply(Bare.class)).isNull();
    }
}
