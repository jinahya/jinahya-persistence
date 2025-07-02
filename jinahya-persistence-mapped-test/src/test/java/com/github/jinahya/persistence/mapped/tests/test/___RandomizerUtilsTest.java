package com.github.jinahya.persistence.mapped.tests.test;

import com.github.jinahya.persistence.mapped.tests.___Randomizer;
import com.github.jinahya.persistence.mapped.tests.___RandomizerUtils;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Test;
import uk.co.jemos.podam.api.DataProviderStrategy;
import uk.co.jemos.podam.api.PodamFactory;

import static org.assertj.core.api.Assertions.assertThat;

class ___RandomizerUtilsTest {

    private static class Pojo {

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        @NotBlank
        private String name;
    }

    private static class PojoRandomizer extends ___Randomizer<Pojo> {

        PojoRandomizer() {
            super(Pojo.class);
        }

        @Nonnull
        @Override
        protected DataProviderStrategy getDataProviderStrategy() {
            return super.getDataProviderStrategy();
        }

        @Nonnull
        @Override
        protected PodamFactory getPodamFactory() {
            return super.getPodamFactory();
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