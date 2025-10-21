package com.github.jinahya.persistence.mapped;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class SampleMappedBuilder_Test {

    @Test
    void __() {
        final var builder = new SampleMappedBuilder()
                .age(ThreadLocalRandom.current().nextBoolean() ? null : ThreadLocalRandom.current().nextInt())
                .name(ThreadLocalRandom.current().nextBoolean() ? null : Long.toString(System.nanoTime()))
//                .age(ThreadLocalRandom.current().nextInt())
//                .name(Long.toString(System.nanoTime()))
                ;
        log.debug("builder: {}", builder);
        final var built = builder.build();
        log.debug("built: {}", built);
        assertThat(built.getAge())
                .as("age")
                .isEqualTo(builder.age());
        assertThat(built.getName())
                .as("name")
                .isEqualTo(builder.name());
    }
}
