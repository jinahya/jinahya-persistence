package com.github.jinahya.persistence.more.converter;

/*-
 * #%L
 * jinahya-persistence-more
 * %%
 * Copyright (C) 2026 Jinahya, Inc.
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

import com.github.jinahya.persistence.more.converter.javautil.UuidAttributeConverter;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HexFormat;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Persists the attributes {@link UuidAttributeConverter.OfString}, {@link UuidAttributeConverter.OfBytes} and
 * {@link UuidAttributeConverter.OfHexString} convert, so that the provider binds the column type each converter names.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("UuidAttributeConverter, persisted")
class UuidAttributeConverter_PersistenceTest {

    private static EntityManagerFactory ENTITY_MANAGER_FACTORY;

    @BeforeAll
    static void openEntityManagerFactory() {
        ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory("__morePU");
    }

    @AfterAll
    static void closeEntityManagerFactory() {
        if (ENTITY_MANAGER_FACTORY != null) {
            ENTITY_MANAGER_FACTORY.close();
        }
    }

    @DisplayName("a UUID is stored as its text, as its bytes and as their digits, and read back")
    @Test
    void __roundTrip() {
        final var value = UUID.fromString("017f22e2-79b0-7cc3-98c4-dc0c0c07398f");
        final Long id;
        try (var em = ENTITY_MANAGER_FACTORY.createEntityManager()) {
            final var tx = em.getTransaction();
            tx.begin();
            final var entity = new _ConvertedEntity();
            entity.uuidText = value;
            entity.uuidBytes = value;
            entity.uuidHex = value;
            em.persist(entity);
            tx.commit();
            id = entity.id;
        }
        try (var em = ENTITY_MANAGER_FACTORY.createEntityManager()) {
            final var found = em.find(_ConvertedEntity.class, id);
            assertThat(found.uuidText).isEqualTo(value);
            assertThat(found.uuidBytes).isEqualTo(value);
            assertThat(found.uuidHex).isEqualTo(value);
            final var text = em.createNativeQuery("SELECT uuid_text FROM converted_entity WHERE id = " + id)
                    .getSingleResult();
            assertThat(text).isEqualTo("017f22e2-79b0-7cc3-98c4-dc0c0c07398f");
            final var bytes = em.createNativeQuery("SELECT uuid_bytes FROM converted_entity WHERE id = " + id)
                    .getSingleResult();
            assertThat(HexFormat.of().formatHex((byte[]) bytes)).isEqualTo("017f22e279b07cc398c4dc0c0c07398f");
            final var hex = em.createNativeQuery("SELECT uuid_hex FROM converted_entity WHERE id = " + id)
                    .getSingleResult();
            assertThat(hex).isEqualTo("017f22e279b07cc398c4dc0c0c07398f");
        }
    }
}
