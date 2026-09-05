package com.github.jinahya.persistence.test.util;

import jakarta.annotation.Nonnull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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

        @Nonnull
        @Override
        public Pojo get() {
            return new Pojo("name");
        }
    }

    private static class Unnamed {

    }

    private static class UnnamedInstantiatorOfAnyName extends __Instantiator<Unnamed> {

        UnnamedInstantiatorOfAnyName() {
            super(Unnamed.class);
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

    @DisplayName("newInstantiatedInstanceOf(Unnamed.class, locator) -> instantiated by the located instantiator")
    @Test
    void newInstantiatedInstanceOf_Instantiated_Locator() {
        assertThat(__InstantiatorUtils.newInstantiatedInstanceOf(
                Unnamed.class,
                c -> c == Unnamed.class ? UnnamedInstantiatorOfAnyName.class : null
        )).isNotNull();
    }

    @DisplayName("newInstantiatedInstanceOf(Pojo.class, locator -> null) -> instantiated directly")
    @Test
    void newInstantiatedInstanceOf_Instantiated_LocatorLocatesNothing() {
        final var instance = __InstantiatorUtils.newInstantiatedInstanceOf(Pojo.class, c -> null);
        assertThat(instance).isNotNull();
        assertThat(instance.getName()).isNull();
    }
}
