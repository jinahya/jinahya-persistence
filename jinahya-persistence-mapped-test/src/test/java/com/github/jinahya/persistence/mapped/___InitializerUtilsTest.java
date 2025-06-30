package com.github.jinahya.persistence.mapped;

import jakarta.annotation.Nonnull;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ___InitializerUtilsTest {

    static class Pojo {

        private Pojo(final String name) {
            super();
            this.name = name;
        }

        private Pojo() {
            this(null);
        }

        private String name;
    }

    static class PojoInitializer extends ___Initializer<Pojo> {

        PojoInitializer() {
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
        final var randomized = ___InitializerUtils.newInitializedInstanceOf(Pojo.class);
        assertThat(randomized).hasValueSatisfying(v -> {
            assertThat(v.name).isEqualTo("name");
        });
    }
}