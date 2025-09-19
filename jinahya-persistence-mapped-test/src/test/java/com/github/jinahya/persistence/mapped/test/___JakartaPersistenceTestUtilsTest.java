package com.github.jinahya.persistence.mapped.test;

/*-
 * #%L
 * jinahya-persistence-mapped-test
 * %%
 * Copyright (C) 2025 Jinahya, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.ManagedType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * A class for testing {@link ___JakartaPersistence_TestUtils}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class ___JakartaPersistenceTestUtilsTest {

    @DisplayName("acceptEachAttributeName(managedType, consumer)")
    @Nested
    class AcceptEachAttributeNameTest {

        @Test
        @SuppressWarnings({"unchecked"})
        void __() {
            final var managedType = mock(ManagedType.class);
            final var attributeNames = List.of("x", "a", "b", "c");
            final var attributes = attributeNames.stream()
                    .map(name -> {
                        final var attribute = mock(Attribute.class);
                        when(attribute.getName()).thenReturn(name);
                        return attribute;
                    }).collect(Collectors.toCollection(LinkedHashSet::new));
            when(managedType.getAttributes()).thenReturn(attributes);
            @SuppressWarnings("Convert2Lambda")
            final Consumer<String> consumer = spy(
                    new Consumer<String>() { // DO NOT CONVERT INTO A LAMBDA EXPRESSION
                        @Override
                        public void accept(final String s) {
                            // empty
                        }
                    }
            );
            // ---------------------------------------------------------------------------------------------------- when
            ___JakartaPersistence_TestUtils.acceptEachAttributeName(managedType, consumer);
            // ---------------------------------------------------------------------------------------------------- then
            final var captor = ArgumentCaptor.forClass(String.class);
            verify(consumer, times(attributes.size())).accept(captor.capture());
            final var captured = captor.getAllValues();
            assertThat(captured).hasSameElementsAs(attributeNames);
        }
    }

    @DisplayName("allAllAttributeNames(managedType, collection)")
    @Nested
    class AddAllAttributeNamesTest {

        @Test
        @SuppressWarnings({"unchecked"})
        void __() {
            final var managedType = mock(ManagedType.class);
            final var attributeNames = List.of("x", "a", "b", "c");
            final var attributes = attributeNames.stream()
                    .map(name -> {
                        final var attribute = mock(Attribute.class);
                        when(attribute.getName()).thenReturn(name);
                        return attribute;
                    })
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            when(managedType.getAttributes()).thenReturn(attributes);
            final var collection = mock(Collection.class);
            // ---------------------------------------------------------------------------------------------------- when
            final var result = ___JakartaPersistence_TestUtils.addAllAttributeNames(managedType, collection);
            // ---------------------------------------------------------------------------------------------------- then
            final var captor = ArgumentCaptor.forClass(String.class);
            verify(collection, times(attributes.size())).add(captor.capture());
            final var captured = captor.getAllValues();
            assertThat(captured).hasSameElementsAs(attributeNames);
            assertThat(result).isSameAs(collection);
        }
    }

    @DisplayName("applyEntityManagerInTransaction(entityManager, function, rollback)")
    @Nested
    class ApplyEntityManagerInTransactionTest {

        @Test
        void _IllegalStateException_EntityManagerIsAlreadyInTransaction() {
            // --------------------------------------------------------------------------------------------------- given
            final var entityManager = mock(EntityManager.class);
            when(entityManager.isJoinedToTransaction()).thenReturn(true);
            // ----------------------------------------------------------------------------------------------- when/then
            assertThatThrownBy(() -> {
                ___JakartaPersistence_TestUtils.getInTransaction(
                        entityManager,
                        () -> null,
                        false
                );
            }).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void __() {
            // --------------------------------------------------------------------------------------------------- given
            final var entityManager = mock(EntityManager.class);
            final var transaction = mock(EntityTransaction.class);
            when(entityManager.getTransaction()).thenReturn(transaction);
            final var expected = new Object();
            //noinspection Convert2Lambda
            final var function = (Function<EntityManager, Object>) spy(
                    new Function<EntityManager, Object>() { // DO NOT CONVERT INTO A LAMBDA EXPRESSION
                        @Override
                        public Object apply(final EntityManager entityManager) {
                            return expected;
                        }
                    }
            );
            final var rollback = ThreadLocalRandom.current().nextBoolean();
            // ---------------------------------------------------------------------------------------------------- when
            final var actual = ___JakartaPersistence_TestUtils.getInTransaction(
                    entityManager,
                    () -> function.apply(entityManager),
                    rollback
            );
            // ---------------------------------------------------------------------------------------------------- then
            verify(entityManager, times(1)).getTransaction();
            verify(transaction, times(1)).begin();
            if (rollback) {
                verify(transaction, times(1)).rollback();
            } else {
                verify(transaction, times(1)).commit();
            }
            assertThat(actual).isSameAs(expected);
        }

        @Test
        void _Rollback_FunctionThrows() {
            // --------------------------------------------------------------------------------------------------- given
            final var entityManager = mock(EntityManager.class);
            final var transaction = mock(EntityTransaction.class);
            when(entityManager.getTransaction()).thenReturn(transaction);
            //noinspection Convert2Lambda
            final var function = (Function<EntityManager, Object>) spy(
                    new Function<EntityManager, Object>() { // DO NOT CONVERT INTO A LAMBDA EXPRESSION
                        @Override
                        public Object apply(final EntityManager entityManager) {
                            throw new RuntimeException();
                        }
                    }
            );
            final var rollback = ThreadLocalRandom.current().nextBoolean();
            // ---------------------------------------------------------------------------------------------------- when
            assertThatThrownBy(() -> {
                ___JakartaPersistence_TestUtils.getInTransaction(
                        entityManager,
                        () -> function.apply(entityManager),
                        rollback
                );
            }).isInstanceOf(RuntimeException.class);
            // ---------------------------------------------------------------------------------------------------- then
            verify(entityManager, times(1)).getTransaction();
            verify(transaction, times(1)).begin();
            verify(transaction, times(1)).rollback(); // regardless of the rollback parameter
        }
    }
}
