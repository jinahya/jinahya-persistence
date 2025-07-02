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

        private String name;
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

    @Test
    void __() {
        final var randomized = ___InstantiatorUtils.newInitializedInstanceOf(Pojo.class);
        assertThat(randomized).hasValueSatisfying(v -> {
            assertThat(v.name).isEqualTo("name");
        });
    }
}