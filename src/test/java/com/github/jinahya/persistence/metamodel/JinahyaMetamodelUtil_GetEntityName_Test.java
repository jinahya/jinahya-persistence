package com.github.jinahya.persistence.metamodel;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JinahyaMetamodelUtil_GetEntityName_Test {

    @DisplayName("(null, )NullPointerException")
    @Test
    void _ThrowNullPointerException_EntityManagerIsNull() {
        final var entityManager = (EntityManager) null;
        final var entityClass = Object.class;
        assertThatThrownBy(
                () -> JinahyaMetamodelUtil.getEntityName(entityManager, entityClass)
        ).isInstanceOf(NullPointerException.class);
    }

    @DisplayName("(, null)NullPointerException")
    @Test
    void _ThrowNullPointerException_EntityClassIsNull() {
        final var entityManager = Mockito.mock(EntityManager.class);
        final var entityClass = (Class<?>) null;
        assertThatThrownBy(
                () -> JinahyaMetamodelUtil.getEntityName(entityManager, entityClass)
        ).isInstanceOf(NullPointerException.class);
    }
}