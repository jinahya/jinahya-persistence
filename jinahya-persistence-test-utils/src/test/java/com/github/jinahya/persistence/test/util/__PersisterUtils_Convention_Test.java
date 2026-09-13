package com.github.jinahya.persistence.test.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
class __PersisterUtils_Convention_Test {

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

//SEP:a nested entity class, whose counterpart is nested in the counterpart of its enclosing class:
//SEP:an arrangement the convention deliberately does not consult

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
    @DisplayName("locateStandard(Ent.class) -> EntPersister")
    @Test
    void standard_EntPersister_Ent() {
        assertThat(__PersisterUtils.locateStandard(Ent.class)).isSameAs(EntPersister.class);
    }

    @DisplayName("locateStandard(Underscored.class) -> Underscored_Persister")
    @Test
    void standard_UnderscoredPersister_Underscored() {
        assertThat(__PersisterUtils.locateStandard(Underscored.class)).isSameAs(Underscored_Persister.class);
    }

    @DisplayName("locateStandard(Outer.Inner.class) -> null;"
                 + " the enclosing chain is not consulted, so a nested entity class has no persister")
    @Test
    void standard_Null_EnclosingChainNotConsulted() {
        assertThat(__PersisterUtils.locateStandard(Outer.Inner.class)).isNull();
    }

    @DisplayName("locateStandard(Bare.class) -> null")
    @Test
    void standard_Null_Bare() {
        assertThat(__PersisterUtils.locateStandard(Bare.class)).isNull();
    }
}
