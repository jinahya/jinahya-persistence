package com.github.jinahya.persistence.mapped.tests.test;

import com.github.jinahya.persistence.mapped.tests.___Instantiator;
import com.github.jinahya.persistence.mapped.tests.___InstantiatorUtils;
import jakarta.annotation.Nonnull;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ___InstantiatorUtilsTest {

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

    private static class PojoInstantiator extends ___Instantiator<Pojo> {

        PojoInstantiator() {
            super(Pojo.class);
        }

        @Nonnull
        @Override
        public Pojo get() {
            return new Pojo("name");
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Test
    void __() {
        final var instance = ___InstantiatorUtils.newInstantiatedInstanceOf(Pojo.class);
        assertThat(instance).isPresent();
    }
}
