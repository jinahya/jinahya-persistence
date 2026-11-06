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

import com.github.jinahya.persistence.shared.__PersistenceProviders;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.util.BitSet;
import java.util.Currency;
import java.util.HexFormat;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

/**
 * Persists the attributes the converters of {@code java} types convert, so that the provider binds the column type each
 * converter names.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("the converters of java types, persisted")
class JavaTypeAttributeConverters_PersistenceTest {

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

    private static Long persist(final Consumer<_ConvertedEntity> setter) {
        try (var em = ENTITY_MANAGER_FACTORY.createEntityManager()) {
            final var tx = em.getTransaction();
            tx.begin();
            final var entity = new _ConvertedEntity();
            setter.accept(entity);
            em.persist(entity);
            tx.commit();
            return entity.id;
        }
    }

    private static Object column(final String name, final Long id) {
        try (var em = ENTITY_MANAGER_FACTORY.createEntityManager()) {
            return em.createNativeQuery("SELECT " + name + " FROM converted_entity WHERE id = " + id)
                    .getSingleResult();
        }
    }

    private static _ConvertedEntity find(final Long id) {
        try (var em = ENTITY_MANAGER_FACTORY.createEntityManager()) {
            return em.find(_ConvertedEntity.class, id);
        }
    }

    @DisplayName("each value is stored as its converter writes it, and read back")
    @Test
    void __roundTrip() {
        final var bits = new BitSet();
        bits.set(0);
        bits.set(9);
        final var id = persist(e -> {
            e.bigInteger = new BigInteger("-123456789012345678901234567890");
            e.byteText = Byte.MIN_VALUE;
            e.shortText = Short.MAX_VALUE;
            e.currency = Currency.getInstance("KRW");
            e.uri = URI.create("https://example.com/a%20b?q=1#f");
            e.charset = StandardCharsets.UTF_8;
            e.zoneOffset = ZoneOffset.ofHoursMinutes(-3, -30);
            e.bitSet = bits;
        });
        final var found = find(id);
        assertThat(found.bigInteger).isEqualTo(new BigInteger("-123456789012345678901234567890"));
        assertThat(found.byteText).isEqualTo(Byte.MIN_VALUE);
        assertThat(found.shortText).isEqualTo(Short.MAX_VALUE);
        assertThat(found.currency).isSameAs(Currency.getInstance("KRW"));
        assertThat(found.uri).isEqualTo(URI.create("https://example.com/a%20b?q=1#f"));
        assertThat(found.charset).isEqualTo(StandardCharsets.UTF_8);
        assertThat(found.zoneOffset).isEqualTo(ZoneOffset.ofHoursMinutes(-3, -30));
        assertThat(found.bitSet).isEqualTo(bits);
        assertThat(column("big_integer", id)).isEqualTo("-123456789012345678901234567890");
        assertThat(column("byte_text", id)).isEqualTo("-128");
        assertThat(column("short_text", id)).isEqualTo("32767");
        assertThat(column("currency", id)).isEqualTo("KRW");
        assertThat(column("uri", id)).isEqualTo("https://example.com/a%20b?q=1#f");
        assertThat(column("charset", id)).isEqualTo("UTF-8");
        assertThat(column("zone_offset", id)).isEqualTo("-03:30");
        assertThat(HexFormat.of().formatHex((byte[]) column("bit_set", id))).isEqualTo("0102");
    }

    // EclipseLink does not store a set changed in place, Hibernate ORM does; a changed copy is stored by both
    @DisplayName("a changed copy of a bit set, assigned to a managed entity, is stored")
    @Test
    void __bitSetChangedCopy() {
        assumeFalse(__PersistenceProviders.isWovenByEclipseLink(_ConvertedEntity.class),
                    "woven, a field assigned from outside the entity is not tracked");
        final var id = persist(e -> e.bitSet = new BitSet());
        try (var em = ENTITY_MANAGER_FACTORY.createEntityManager()) {
            final var tx = em.getTransaction();
            tx.begin();
            final var entity = em.find(_ConvertedEntity.class, id);
            final var bits = (BitSet) entity.bitSet.clone();
            bits.set(3);
            entity.bitSet = bits;
            tx.commit();
        }
        assertThat(find(id).bitSet.get(3)).isTrue();
        assertThat(HexFormat.of().formatHex((byte[]) column("bit_set", id))).isEqualTo("08");
    }
}
