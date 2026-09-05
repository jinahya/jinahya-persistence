package com.github.jinahya.persistence.test.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
class __InstantiatorLocator_Test {

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

//SEP:an instantiator nested in its own target class

    static class Nesting {

        static class NestingInstantiator extends __Instantiator<Nesting> {

            NestingInstantiator() {
                super(Nesting.class);
            }
        }
    }

//SEP:an instantiator nested in the instantiator of the enclosing class

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
    @DisplayName("STANDARD.apply(Sibling.class) -> SiblingInstantiator")
    @Test
    void standard_SiblingInstantiator_Sibling() {
        assertThat(__InstantiatorLocator.STANDARD.apply(Sibling.class)).isSameAs(SiblingInstantiator.class);
    }

    @DisplayName("STANDARD.apply(Underscored.class) -> Underscored_Instantiator")
    @Test
    void standard_UnderscoredInstantiator_Underscored() {
        assertThat(__InstantiatorLocator.STANDARD.apply(Underscored.class)).isSameAs(Underscored_Instantiator.class);
    }

    @DisplayName("STANDARD.apply(Nesting.class) -> Nesting$NestingInstantiator")
    @Test
    void standard_NestedInstantiator_Nesting() {
        assertThat(__InstantiatorLocator.STANDARD.apply(Nesting.class))
                .isSameAs(Nesting.NestingInstantiator.class);
    }

    @DisplayName("STANDARD.apply(Outer.Inner.class) -> OuterInstantiator$InnerInstantiator,"
                 + " through the instantiator of the enclosing class")
    @Test
    void standard_InstantiatorEnclosedByTheEnclosingOne_OuterInner() {
        assertThat(__InstantiatorLocator.STANDARD.apply(Outer.Inner.class))
                .isSameAs(OuterInstantiator.InnerInstantiator.class);
    }

    @DisplayName("STANDARD.apply(Bare.class) -> null")
    @Test
    void standard_Null_Bare() {
        assertThat(__InstantiatorLocator.STANDARD.apply(Bare.class)).isNull();
    }
}
