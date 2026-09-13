package com.github.jinahya.persistence.test.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
class __InstantiatorUtils_Convention_Test {

//SEP:a sibling instantiator, of either postfix

    static class Sibling {

    }

    static class SiblingInstantiator extends __Instantiator<Sibling> {

        SiblingInstantiator() {
            super(Sibling.class);
        }
    }

    static class Underscored {

    }

    static class Underscored_Instantiator extends __Instantiator<Underscored> {

        Underscored_Instantiator() {
            super(Underscored.class);
        }
    }

//SEP:a nested target class, whose counterpart is nested in the counterpart of its enclosing class:
//SEP:an arrangement the convention deliberately does not consult

    static class Outer {

        static class Inner {

        }
    }

    static class OuterInstantiator extends __Instantiator<Outer> {

        OuterInstantiator() {
            super(Outer.class);
        }

        static class InnerInstantiator extends __Instantiator<Outer.Inner> {

            InnerInstantiator() {
                super(Outer.Inner.class);
            }
        }
    }

//SEP:a class following no convention

    static class Bare {

    }

    // ---------------------------------------------------------------------------------------------------------------------
    @DisplayName("locateStandard(Sibling.class) -> SiblingInstantiator")
    @Test
    void standard_SiblingInstantiator_Sibling() {
        assertThat(__InstantiatorUtils.locateStandard(Sibling.class)).isSameAs(SiblingInstantiator.class);
    }

    @DisplayName("locateStandard(Underscored.class) -> Underscored_Instantiator")
    @Test
    void standard_UnderscoredInstantiator_Underscored() {
        assertThat(__InstantiatorUtils.locateStandard(Underscored.class)).isSameAs(Underscored_Instantiator.class);
    }

    @DisplayName("locateStandard(Outer.Inner.class) -> null;"
                 + " the enclosing chain is not consulted, so a nested target class has no instantiator")
    @Test
    void standard_Null_EnclosingChainNotConsulted() {
        assertThat(__InstantiatorUtils.locateStandard(Outer.Inner.class)).isNull();
    }

    @DisplayName("locateStandard(Bare.class) -> null")
    @Test
    void standard_Null_Bare() {
        assertThat(__InstantiatorUtils.locateStandard(Bare.class)).isNull();
    }
}
