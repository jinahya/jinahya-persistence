package com.github.jinahya.persistence.mapped;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class __ConcreteMappedEntityTest {

    @Test
    void toString__() {
        final var instance = new __ConcreteMappedEntity();
        final var string = instance.toString();
        assertThat(string).isNotBlank();
    }

    @Test
    void equals__() {
        final var instance1 = new __ConcreteMappedEntity();
        {
            assertThat(instance1).isEqualTo(instance1);
            assertThat(instance1).isNotEqualTo(new Object());
        }
        final var instance2 = new __ConcreteMappedEntity();
        assertThat(instance1).isNotEqualTo(instance2);
        assertThat(instance2).isNotEqualTo(instance1);
        instance1.setId__(0L);
        instance2.setId__(0L);
        assertThat(instance1).isEqualTo(instance2);
        assertThat(instance2).isEqualTo(instance1);
    }

    @Test
    void getId____() {
        final var instance = new __ConcreteMappedEntity();
        final var id__ = instance.getId__();
    }

    @Test
    void id____() {
        final var instance = new __ConcreteMappedEntity();
        final var result = instance.id__(0L);
        assertThat(result).isSameAs(instance);
    }
}
