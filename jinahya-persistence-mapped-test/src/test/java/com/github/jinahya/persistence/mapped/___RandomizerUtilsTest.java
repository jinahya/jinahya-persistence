package com.github.jinahya.persistence.mapped;

import jakarta.annotation.Nonnull;
import lombok.Getter;
import lombok.Setter;

class ___RandomizerUtilsTest {

    @Setter
    @Getter
    static class Randomizable {

        private String name;
    }

    static class RandomizableRandomizer implements ___Randomizer<Randomizable> {

        @Nonnull
        @Override
        public Randomizable get() {
            return null;
        }
    }
}