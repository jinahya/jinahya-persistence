package com.github.jinahya.persistence.test.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
class __InstantiatorUtils_Test {

    private static class Pojo {

        private Pojo(final String name) {
            super();
            this.name = name;
        }

        private Pojo() {
            this(null);
        }

        public String getName() {
            return name;
        }

        private final String name;
    }

    private static class PojoInstantiator extends __Instantiator<Pojo> {

        PojoInstantiator() {
            super(Pojo.class);
        }

        @Override
        public Pojo get() {
            return new Pojo("name");
        }
    }

    private static class Unnamed {

    }

//SEP:producer covariance -- an instantiator is located by name, then checked against the class it is declared for

    private static class Sup {

    }

    private static class Sub extends Sup {

    }

    /**
     * An instantiator, of {@code Sup}, declared for a subclass of it; every instance it produces is still a
     * {@code Sup}.
     */
    private static class SupInstantiator extends __Instantiator<Sub> {

        SupInstantiator() {
            super(Sub.class);
        }
    }

    /**
     * A class whose conventionally named instantiator is declared for its superclass, which can not produce instances
     * of it.
     */
    private static class Narrowed extends Sup {

    }

    private static class NarrowedInstantiator extends __Instantiator<Sup> {

        NarrowedInstantiator() {
            super(Sup.class);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("newInstantiatedInstanceOf(Pojo.class) -> instantiated by PojoInstantiator")
    @Test
    void __() {
        final var instance = __InstantiatorUtils.newInstantiatedInstanceOf(Pojo.class);
        assertThat(instance).isNotNull();
        assertThat(instance.getName()).isEqualTo("name");
    }

    @DisplayName("newInstantiatedInstanceOf(Unnamed.class) -> instantiated directly")
    @Test
    void newInstantiatedInstanceOf_Instantiated_NoSiblingInstantiator() {
        assertThat(__InstantiatorUtils.newInstantiatedInstanceOf(Unnamed.class)).isNotNull();
    }

    @DisplayName("newInstantiatedInstanceOf(Sup.class) -> a Sub; an instantiator declared for a subclass is used")
    @Test
    void newInstantiatedInstanceOf_Sub_InstantiatorOfSubclass() {
        assertThat(__InstantiatorUtils.newInstantiatedInstanceOf(Sup.class)).isInstanceOf(Sub.class);
    }

    @DisplayName("newInstantiatedInstanceOf(Narrowed.class) -> RuntimeException;"
                 + " NarrowedInstantiator was provided, so producing a Sup is a fault, not a fallback")
    @Test
    void newInstantiatedInstanceOf_RuntimeException_InstantiatorProducesASuperclass() {
        assertThatThrownBy(() -> __InstantiatorUtils.newInstantiatedInstanceOf(Narrowed.class))
                .isExactlyInstanceOf(RuntimeException.class)
                .hasMessageContaining("produced a");
    }
}
