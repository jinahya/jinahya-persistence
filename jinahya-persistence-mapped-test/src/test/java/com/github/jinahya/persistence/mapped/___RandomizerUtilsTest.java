package com.github.jinahya.persistence.mapped;

import jakarta.annotation.Nonnull;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import uk.co.jemos.podam.api.DataProviderStrategy;
import uk.co.jemos.podam.api.PodamFactory;

import static org.assertj.core.api.Assertions.assertThat;

class ___RandomizerUtilsTest {

    @Getter
    @Setter
    static class Pojo {

        private String name;
    }

    static class PojoRandomizer extends ___Randomizer<Pojo> {

        PojoRandomizer() {
            super(Pojo.class);
        }

        @Override
        protected DataProviderStrategy strategy() {
            return super.strategy();
        }

        @Override
        protected PodamFactory factory() {
            return super.factory();
        }

        @Nonnull
        @Override
        public Pojo get() {
            return super.get();
        }
    }

    @Test
    void __() {
        final var instance = ___RandomizerUtils.newRandomizedInstanceOf(Pojo.class);
        assertThat(instance).hasValueSatisfying(v -> {
            assertThat(v.name).isNotBlank();
        });
    }
}