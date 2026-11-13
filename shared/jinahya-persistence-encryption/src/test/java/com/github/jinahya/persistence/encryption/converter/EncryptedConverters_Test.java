package com.github.jinahya.persistence.encryption.converter;

import com.github.jinahya.persistence.encryption.EntityEncryptionService;
import com.github.jinahya.persistence.encryption.EntityEncryptionServiceImpl;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings({"java:S101"})
class EncryptedConverters_Test {

    enum Color {RED, GREEN}

    /** Counts its calls, and delegates to the test service's cryptography. */
    static class CountingService implements EntityEncryptionService {

        final AtomicInteger calls = new AtomicInteger();

        @Override
        public byte[] encrypt(final byte[] decryptedBytes) {
            calls.incrementAndGet();
            return EntityEncryptionServiceImpl.encryptBytes(decryptedBytes);
        }

        @Override
        public byte[] decrypt(final byte[] encryptedBytes) {
            calls.incrementAndGet();
            return EntityEncryptionServiceImpl.decryptBytes(encryptedBytes);
        }
    }

    @DisplayName("a factory converter uses the given service, and its bytes read back through the named converter")
    @Test
    void sameBytesAsTheNamedConverter() {
        final var service = new CountingService();
        final Map<Class<?>, Object> values = Map.of(
                String.class, "sé", Integer.class, 7, int.class, -7, BigDecimal.class, new BigDecimal("1.50"),
                LocalDateTime.class, LocalDateTime.of(2020, 1, 2, 3, 4, 5), byte[].class, new byte[] {1, 2},
                Color.class, Color.GREEN);
        values.forEach((type, value) -> {
            @SuppressWarnings("unchecked")
            final var converter = (AttributeConverter<Object, byte[]>) EncryptedConverters.forType(type, service);
            final var stored = converter.convertToDatabaseColumn(value);
            assertThat(stored).isNotNull();
            assertThat(converter.convertToEntityAttribute(stored)).as(type.getName()).isEqualTo(value);
            if (type == String.class) {
                // the same cryptography, so the loaded converter of the same type reads it too
                assertThat(new EncryptedStringConverter().convertToEntityAttribute(stored)).isEqualTo(value);
            }
        });
        assertThat(service.calls.get()).isEqualTo(values.size() * 2);
    }

    @DisplayName("null passes through without a call; an unsupported type is rejected; a primitive is its wrapper")
    @Test
    void nullsAndRejections() {
        final var service = new CountingService();
        final var converter = EncryptedConverters.forType(Long.class, service);
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
        assertThat(converter.convertToEntityAttribute(null)).isNull();
        assertThat(service.calls.get()).isZero();
        assertThat(EncryptedConverters.supports(long.class)).isTrue();
        assertThat(EncryptedConverters.supports(Object.class)).isFalse();
        assertThatThrownBy(() -> EncryptedConverters.forType(Object.class, service))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("a service which returns null fails the conversion as a PersistenceException")
    @Test
    void serviceReturnsNull() {
        final var converter = EncryptedConverters.forType(String.class, new EntityEncryptionService() {
            @Override
            public byte[] encrypt(final byte[] decryptedBytes) {
                return null;
            }

            @Override
            public byte[] decrypt(final byte[] encryptedBytes) {
                return null;
            }
        });
        assertThatThrownBy(() -> converter.convertToDatabaseColumn("value"))
                .isInstanceOf(PersistenceException.class).hasMessageNotContaining("value");
        assertThatThrownBy(() -> converter.convertToEntityAttribute(new byte[] {1}))
                .isInstanceOf(PersistenceException.class);
    }

    @DisplayName("serviceOf: an instance, a class, a class name, or null for the loaded one")
    @Test
    void serviceOf() {
        final var service = new CountingService();
        assertThat(EncryptedConverters.serviceOf(service)).isSameAs(service);
        final var byClass = EncryptedConverters.serviceOf(EntityEncryptionServiceImpl.class);
        assertThat(byClass).isInstanceOf(EntityEncryptionServiceImpl.class);
        // one instance per class, however many attributes or units name it
        assertThat(EncryptedConverters.serviceOf(EntityEncryptionServiceImpl.class)).isSameAs(byClass);
        assertThat(EncryptedConverters.serviceOf(EntityEncryptionServiceImpl.class.getName())).isSameAs(byClass);
        assertThat(EncryptedConverters.serviceOf(" " + EntityEncryptionServiceImpl.class.getName() + " "))
                .isSameAs(byClass);
        assertThat(EncryptedConverters.serviceOf(null)).isInstanceOf(EntityEncryptionServiceImpl.class);
        assertThatThrownBy(() -> EncryptedConverters.serviceOf(42)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> EncryptedConverters.serviceOf("no.such.Class"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
