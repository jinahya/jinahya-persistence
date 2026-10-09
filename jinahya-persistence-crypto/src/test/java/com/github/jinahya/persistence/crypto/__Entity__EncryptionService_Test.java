package com.github.jinahya.persistence.crypto;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowable;

/**
 * Verifies that {@link AbstractEntityEncryptionService} moves every supported java type between the decrypted attribute and its
 * paired encrypted attribute, and back, against a real metamodel.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
class __Entity__EncryptionService_Test {

    private static EntityManagerFactory ENTITY_MANAGER_FACTORY;

    @BeforeAll
    static void openEntityManagerFactory() {
        ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory("__cryptoPU");
    }

    @AfterAll
    static void closeEntityManagerFactory() {
        if (ENTITY_MANAGER_FACTORY != null) {
            ENTITY_MANAGER_FACTORY.close();
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    private AbstractEntityEncryptionService service;

    @BeforeEach
    void createService() {
        service = new EntityEncryptionService(ENTITY_MANAGER_FACTORY, new _Entity_EncryptionManager());
    }

    private static _SecretEntity populated() {
        final var entity = new _SecretEntity();
        entity.name = "Jane Roe";
        entity.age = 37;
        entity.reference = UUID.randomUUID();
        entity.bornOn = LocalDate.of(1988, 11, 3);
        entity.seenAt = java.sql.Timestamp.valueOf("2026-01-02 03:04:05.123456789");
        entity.joinedAt = new java.util.Date(1_700_000_000_000L);
        entity.checkedAt = Calendar.getInstance();
        entity.checkedAt.setTimeInMillis(1_700_000_000_000L);
        entity.grade = _SecretEntity.Grade.HIGH;
        entity.photo = new byte[32];
        ThreadLocalRandom.current().nextBytes(entity.photo);
        entity.secretNumber = 1_234_567_890_123L;
        entity.secret = new _SecretEmbeddable("nested");
        entity.opaque = "opaque-default";
        return entity;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("a provider proxy is resolved to its entity class")
    @Nested
    class ProxyTest {

        @DisplayName("encrypt/decrypt accept a getReference() proxy")
        @Test
        void __proxy() {
            final var manager = new _Entity_EncryptionManager();
            final var svc = new EntityEncryptionService(ENTITY_MANAGER_FACTORY, manager);

            final Long id;
            try (var em = ENTITY_MANAGER_FACTORY.createEntityManager()) {
                final var tx = em.getTransaction();
                tx.begin();
                final var entity = populated();
                svc.encrypt(entity);
                em.persist(entity);
                tx.commit();
                id = entity.getId();
            }
            assertThat(id).isNotNull();

            try (var em = ENTITY_MANAGER_FACTORY.createEntityManager()) {
                // an uninitialized reference: getClass() is the generated subclass, not the entity class
                final var reference = em.getReference(_SecretEntity.class, id);
                assertThat(reference).isNotNull();

                assertThatCode(() -> svc.decrypt(reference))
                        .as("the metamodel knows _SecretEntity, not the proxy subclass")
                        .doesNotThrowAnyException();
            }
        }
    }

    @DisplayName("a payload the codec cannot read is reported with context")
    @Nested
    class UnreadablePayloadTest {

        @DisplayName("an unknown enum constant names the attribute, not just the constant")
        @Test
        void __unknownEnumConstant() {
            // the ciphertext is well formed and decrypts cleanly; it is the DECODED bytes that the
            // codec cannot turn back into a value -- Enum.valueOf throws IllegalArgumentException,
            // which the guard around the decode ladder did not catch
            final var manager = new _Entity_EncryptionManager();
            final var service = new EntityEncryptionService(ENTITY_MANAGER_FACTORY, manager);
            final var entity = populated();
            final var identifier = manager.getEncryptionIdentifier(entity);

            entity.grade = null;
            // a well-formed header, so the bytes reach the enum codec, which cannot find the constant
            entity.gradeEnc__ = manager.encrypt(identifier, EntityEncryptionServiceUtils.frame(
                    EntityEncryptionServiceUtils.Codec.ENUM_, "NOPE".getBytes(java.nio.charset.StandardCharsets.UTF_8)));

            assertThatThrownBy(() -> service.decrypt(entity))
                    .as("the failure has to say which attribute and which java type")
                    .hasMessageContaining("grade")
                    .hasMessageContaining("Grade");
        }

        @DisplayName("a payload of an unknown format version is rejected, naming the attribute (#75)")
        @Test
        void __unknownFormatVersion() {
            final var manager = new _Entity_EncryptionManager();
            final var service = new EntityEncryptionService(ENTITY_MANAGER_FACTORY, manager);
            final var entity = populated();
            final var framed = EntityEncryptionServiceUtils.frame(
                    EntityEncryptionServiceUtils.Codec.INT_4, EntityEncryptionServiceUtils.int_4(37));
            framed[0] = (byte) (EntityEncryptionServiceUtils.FORMAT_VERSION + 1);
            entity.age = null;
            entity.ageEnc__ = manager.encrypt(manager.getEncryptionIdentifier(entity), framed);

            assertThatThrownBy(() -> service.decrypt(entity))
                    .hasMessageContaining("unknown payload format version")
                    .hasMessageContaining("age");
        }

        @DisplayName("a payload written by another codec is rejected rather than misread, e.g. after Long -> Integer (#75)")
        @Test
        void __codecMismatch() {
            final var manager = new _Entity_EncryptionManager();
            final var service = new EntityEncryptionService(ENTITY_MANAGER_FACTORY, manager);
            final var entity = populated();
            // what a row holds when `age` was declared Long when it was written, and is Integer now
            entity.age = null;
            entity.ageEnc__ = manager.encrypt(manager.getEncryptionIdentifier(entity), EntityEncryptionServiceUtils.frame(
                    EntityEncryptionServiceUtils.Codec.LONG_8, EntityEncryptionServiceUtils.long_8(37L)));

            assertThatThrownBy(() -> service.decrypt(entity))
                    .hasMessageContaining("encoded by LONG_8")
                    .hasMessageContaining("decoded by INT_4")
                    .hasMessageContaining("age");
            assertThat(entity.age).as("nothing was misread").isNull();
        }
    }

    @DisplayName("encrypt()")
    @Nested
    class EncryptTest {

        @DisplayName("moves each value out of the decrypted attribute and into the encrypted one")
        @Test
        void __movesValues() {
            final var entity = populated();

            service.encrypt(entity);

            assertThat(entity.name).isNull();
            assertThat(entity.age).isNull();
            assertThat(entity.reference).isNull();
            assertThat(entity.photo).isNull();
            assertThat(entity.secretNumber).isNull();
            assertThat(entity.nameEnc__).isNotNull().isNotEmpty();
            assertThat(entity.ageEnc__).isNotNull().isNotEmpty();
            assertThat(entity.referenceEnc__).isNotNull().isNotEmpty();
            assertThat(entity.photoEnc__).isNotNull().isNotEmpty();
            assertThat(entity.secretNumberCipher).isNotNull().isNotEmpty();
        }

        @DisplayName("no ciphertext holds the plaintext")
        @Test
        void __ciphertextDiffersFromPlaintext() {
            final var entity = populated();
            final var photo = entity.photo.clone();

            service.encrypt(entity);

            assertThat(entity.photoEnc__).isNotEqualTo(photo);
            assertThat(new String(entity.nameEnc__, java.nio.charset.StandardCharsets.ISO_8859_1))
                    .doesNotContain("Jane Roe");
        }

        @DisplayName("a null embedded attribute is skipped, not dereferenced")
        @Test
        void __nullEmbedded() {
            final var entity = populated();
            entity.secret = null;

            assertThatCode(() -> service.encrypt(entity)).doesNotThrowAnyException();
            assertThatCode(() -> service.decrypt(entity)).doesNotThrowAnyException();

            assertThat(entity.name).isEqualTo("Jane Roe");
        }

        @DisplayName("a value which would not be read back as is, is rejected against its attribute, without the value (#77)")
        @Test
        void __unencodableValueRejected() {
            final var entity = new _SecretEntity();
            entity.name = "a\uD800b"; // an unpaired surrogate: not representable in UTF-8

            assertThatThrownBy(() -> service.encrypt(entity))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("cannot encode the value")
                    .hasMessageContaining("decrypted attribute: name")
                    .hasMessageNotContaining("a\uD800b");
            assertThat(entity.name).as("the caller still holds the value").isEqualTo("a\uD800b");
        }

        @DisplayName("an attribute which is null on both sides stays null")
        @Test
        void __bothNull() {
            final var entity = new _SecretEntity();
            entity.secret = new _SecretEmbeddable(null);

            assertThatCode(() -> service.encrypt(entity)).doesNotThrowAnyException();

            assertThat(entity.name).isNull();
            assertThat(entity.nameEnc__).isNull();
        }
    }

    @DisplayName("rejected mappings")
    @Nested
    class RejectionTest {

        @DisplayName("an invalid embeddable is rejected before a valid sibling has been transformed")
        @Test
        void __graphIsValidatedFirst() {
            final var entity = new _GraphEntity();
            entity.valid = new _SecretEmbeddable("keep-me");
            entity.invalid = new _UnguardedEmbeddable();
            entity.invalid.leak = "leak-me";

            assertThatThrownBy(() -> service.encrypt(entity))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("non-insertable");

            assertThat(entity.valid.getNote())
                    .as("the valid embeddable must not have been touched")
                    .isEqualTo("keep-me");
            assertThat(entity.valid.getNoteEnc__())
                    .as("and must not have been given a ciphertext")
                    .isNull();
        }

        @DisplayName("the same embeddable is judged per embedding path, not once per type")
        @Test
        void __judgedPerEmbeddingPath() {
            // _SecretEmbeddable.note declares insertable = false and is safe through _SecretEntity.secret, which
            // adds no override. Through _OverriddenEntity the override only renames, so its own @Column defaults
            // restore insertable = true. One service instance must reach both verdicts for the same embeddable.
            final var accepted = populated();
            service.encrypt(accepted); // caches _SecretEmbeddable at path [secret]
            assertThat(accepted.secret.getNoteEnc__()).as("accepted through this path").isNotNull();

            final var rejected = new _OverriddenEntity();
            rejected.overridden = new _SecretEmbeddable("leak-me");

            assertThatThrownBy(() -> service.encrypt(rejected))
                    .as("a type-keyed cache would have reused the accepted mapping")
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("non-insertable")
                    .hasMessageContaining("@AttributeOverride");
            assertThat(rejected.overridden.getNote()).as("and nothing was touched").isEqualTo("leak-me");
        }

        @DisplayName("an application outside this package can assert facts its XML mapping establishes")
        @Test
        void __resolverCanAssertXmlFacts() {
            // deliberately an external-package subclass: an in-package one cannot exercise the ColumnRules
            // constructor's accessibility, which is exactly what an application would hit first
            final var relaxed = new com.github.jinahya.persistence.cryptoext._ExternalResolverService(
                    ENTITY_MANAGER_FACTORY, new _Entity_EncryptionManager(), java.util.Set.of("leak"));
            final var entity = new _GraphEntity();
            entity.valid = new _SecretEmbeddable("keep-me");
            entity.second = new _SecretEmbeddable("also-keep-me");
            entity.invalid = new _UnguardedEmbeddable();
            entity.invalid.leak = "now-encrypted";

            relaxed.encrypt(entity);

            assertThat(entity.invalid.leak).as("the asserted mapping is accepted and encrypted").isNull();
            assertThat(entity.invalid.leakEnc__).isNotNull().isNotEmpty();
        }

        @DisplayName("a class-level override of an inherited attribute is honoured")
        @Test
        void __inheritedAttributeClassLevelOverride() {
            // the attribute is held directly, so the embedding path is empty; the override lives on the entity class
            final var entity = new _InheritedOverrideEntity();
            entity.inherited = "inherited-value";

            service.encrypt(entity);

            assertThat(entity.inherited).as("the class-level override makes the inherited member safe").isNull();
            assertThat(entity.inheritedEnc__).isNotNull().isNotEmpty();

            service.decrypt(entity);
            assertThat(entity.inherited).isEqualTo("inherited-value");
        }

        @DisplayName("an override which makes an unsafe member safe is accepted")
        @Test
        void __overrideRescuingAnUnsafeMemberIsAccepted() {
            // _UnguardedEmbeddable.leak declares an insertable column; the override makes it non-insertable, and the
            // override is what the provider actually uses, so the mapping has to be accepted
            final var entity = new _RescuedEntity();
            entity.rescued = new _UnguardedEmbeddable();
            entity.rescued.leak = "rescued-value";

            service.encrypt(entity);

            assertThat(entity.rescued.leak).isNull();
            assertThat(entity.rescued.leakEnc__).isNotNull().isNotEmpty();
        }

        @DisplayName("two paths to one embeddable under the same root do not share a mapping")
        @Test
        void __twoPathsUnderOneRoot() {
            // _TwoPathEntity reaches _SecretEmbeddable twice, under one root, with different overrides. A cache
            // keyed by (root, type) rather than (root, path) would conflate the two.
            final var entity = new _TwoPathEntity();
            entity.first = new _SecretEmbeddable("one");
            entity.second = new _SecretEmbeddable("two");

            service.encrypt(entity);

            assertThat(entity.first.getNote()).isNull();
            assertThat(entity.second.getNote()).isNull();
            assertThat(entity.first.getNoteEnc__()).as("each path is encrypted on its own").isNotNull();
            assertThat(entity.second.getNoteEnc__()).isNotNull();

            service.decrypt(entity);
            assertThat(entity.first.getNote()).as("and each decrypts back to its own value").isEqualTo("one");
            assertThat(entity.second.getNote()).isEqualTo("two");
        }

        @DisplayName("a fact which cannot be established is rejected, not assumed safe")
        @Test
        void __unknownIsRejected() {
            final var unsure = new EntityEncryptionService(ENTITY_MANAGER_FACTORY, new _Entity_EncryptionManager()) {
                @Override
                protected ColumnRules resolveColumnRules(final jakarta.persistence.metamodel.ManagedType<?> rootType,
                                                         final java.util.List<jakarta.persistence.metamodel.Attribute<?, ?>> embeddingPath,
                                                         final jakarta.persistence.metamodel.Attribute<?, ?> attribute) {
                    return new ColumnRules(MappingFlag.UNKNOWN, MappingFlag.UNKNOWN, MappingFlag.UNKNOWN, "unsure");
                }
            };

            assertThatThrownBy(() -> unsure.encrypt(populated()))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("cannot establish");
        }

        @DisplayName("a null embeddable does not hide an invalid mapping")
        @Test
        void __nullEmbeddableStillValidated() {
            final var entity = new _GraphEntity();
            entity.valid = new _SecretEmbeddable("keep-me");
            entity.invalid = null; // never reached by the transformation, still validated

            assertThatThrownBy(() -> service.encrypt(entity))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("non-insertable");
        }

        @DisplayName("an annotated member the metamodel does not know is rejected, not silently ignored (#70)")
        @Test
        void __transientMemberRejected() {
            final var entity = new _TransientSecretEntity();
            entity.name = "never-stored";

            assertThatThrownBy(() -> service.encrypt(entity))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("not a persistent attribute")
                    .hasMessageContaining("_TransientSecretEntity.name");
            assertThatThrownBy(() -> service.decrypt(entity))
                    .as("the read path is guarded by the same validation")
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("not a persistent attribute");
            assertThat(entity.nameEnc__).as("and nothing was touched").isNull();
        }

        @DisplayName("an encrypted embeddable reached through an @ElementCollection is rejected, not persisted in the clear (#71)")
        @Test
        void __elementCollectionRejected() {
            final var entity = new _CollectionSecretEntity();
            entity.secrets.add(new _SecretEmbeddable("TOP-SECRET"));

            assertThatThrownBy(() -> service.encrypt(entity))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("@ElementCollection")
                    .hasMessageContaining("_SecretEmbeddable.note");
            assertThat(entity.secrets.getFirst().getNote()).as("nothing was touched").isEqualTo("TOP-SECRET");
            assertThat(entity.secrets.getFirst().getNoteEnc__()).isNull();
        }

        @DisplayName("an entity with encrypted attributes but no @EncryptedEntity is rejected, not skipped (#72)")
        @Test
        void __unmarkedEntityRejected() {
            final var entity = new _UnmarkedSecretEntity();
            entity.name = "would-be-dropped";

            assertThatThrownBy(() -> service.encrypt(entity))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("not annotated with @EncryptedEntity")
                    .hasMessageContaining("_UnmarkedSecretEntity");
            assertThatThrownBy(() -> service.decrypt(entity))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("not annotated with @EncryptedEntity");
            assertThat(entity.name).as("nothing was touched").isEqualTo("would-be-dropped");
            assertThat(entity.nameEnc__).isNull();
        }

        @DisplayName("an entity with no encrypted attribute, and no @EncryptedEntity, passes through untouched (#72)")
        @Test
        void __plainEntityPassesThrough() {
            // a manager which must never be reached: not even for the encryption identifier
            final var untouchable = new EntityEncryptionService(ENTITY_MANAGER_FACTORY, new EntityEncryptionManager() {
                @Override
                public String getEncryptionIdentifier(final Object entityInstance) {
                    throw new AssertionError("must not be called for a plain entity");
                }

                @Override
                public byte[] encrypt(final String encryptionIdentifier, final byte[] decryptedBytes) {
                    throw new AssertionError("must not be called for a plain entity");
                }

                @Override
                public byte[] decrypt(final String encryptionIdentifier, final byte[] encryptedBytes) {
                    throw new AssertionError("must not be called for a plain entity");
                }
            });
            final var entity = new _PlainEntity();
            entity.name = "plain";

            assertThatCode(() -> untouchable.encrypt(entity)).doesNotThrowAnyException();
            assertThatCode(() -> untouchable.decrypt(entity)).doesNotThrowAnyException();
            assertThat(entity.name).isEqualTo("plain");
        }

        @DisplayName("a plaintext column which is not updatable is rejected (#73)")
        @Test
        void __nonUpdatablePlaintextRejected() {
            final var entity = new _FrozenPlainEntity();
            entity.name = "frozen";

            assertThatThrownBy(() -> service.encrypt(entity))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("has to be updatable");
            assertThat(entity.name).as("nothing was touched").isEqualTo("frozen");
        }
    }

    @DisplayName("validateMappings() (#74)")
    @Nested
    class ValidateMappingsTest {

        @DisplayName("reports every invalid entity of the persistence unit at once, and none of the valid ones")
        @Test
        void __reportsEveryInvalidEntity() {
            final var thrown = catchThrowable(() -> service.validateMappings());

            assertThat(thrown).isInstanceOf(RuntimeException.class);
            final var message = thrown.getMessage();
            // this test unit holds deliberately invalid entities, each covered by its own rejection test
            assertThat(message).contains(
                    "crypto._GraphEntity",              // an invalid embeddable
                    "crypto._OverriddenEntity",         // an override restoring insertable = true
                    "crypto._LifecycleEntity",          // a plaintext column left insertable
                    "crypto._TransientSecretEntity",    // #70
                    "crypto._CollectionSecretEntity",   // #71
                    "crypto._UnmarkedSecretEntity",     // #72
                    "crypto._FrozenPlainEntity"         // #73
            );
            assertThat(thrown.getSuppressed()).as("one suppressed failure per invalid entity").hasSizeGreaterThanOrEqualTo(7);
            assertThat(message).doesNotContain(
                    "crypto._SecretEntity;", "crypto._SecretEntity.", "crypto._GuardedEntity", "crypto._PlainEntity");
        }

        @DisplayName("the valid mappings are usable afterwards")
        @Test
        void __validMappingsStillUsable() {
            catchThrowable(() -> service.validateMappings());
            final var entity = populated();
            assertThatCode(() -> service.encrypt(entity)).doesNotThrowAnyException();
            assertThat(entity.nameEnc__).isNotNull();
        }
    }

    @DisplayName("decrypt()")
    @Nested
    class DecryptTest {

        @DisplayName("a truncated ciphertext is reported against the attribute, not as an index error")
        @Test
        void __truncated() {
            final var entity = new _SecretEntity();
            entity.ageEnc__ = new byte[
                    _Entity_EncryptionManager.IV_BYTES + _Entity_EncryptionManager.KEY_BYTES + _Entity_EncryptionManager.AAD_BYTES];
            // a well-formed header with an empty payload; Integer needs four bytes after it
            final var manager = new _Entity_EncryptionManager();
            entity.ageEnc__ = manager.encrypt("irrelevant", EntityEncryptionServiceUtils.frame(
                    EntityEncryptionServiceUtils.Codec.INT_4, new byte[0]));

            assertThatThrownBy(() -> service.decrypt(entity))
                    .isInstanceOf(RuntimeException.class)
                    // the wording generalised when the guard widened to cover odd-length char[] payloads
                    // and unknown enum constants, which are not "too short" but are equally unreadable
                    .hasMessageContaining("cannot reconstruct the value")
                    .hasMessageContaining("age")
                    .hasMessageContaining("decrypted bytes: " + EntityEncryptionServiceUtils.HEADER_BYTES);
        }

        @DisplayName("an over-long payload is rejected, not read as its first bytes (#76)")
        @Test
        void __overLong() {
            final var entity = new _SecretEntity();
            // a correct header, but eight bytes where Integer reads four
            final var manager = new _Entity_EncryptionManager();
            entity.ageEnc__ = manager.encrypt("irrelevant", EntityEncryptionServiceUtils.frame(
                    EntityEncryptionServiceUtils.Codec.INT_4, EntityEncryptionServiceUtils.long_8(37L)));

            assertThatThrownBy(() -> service.decrypt(entity))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("reads exactly 4")
                    .hasMessageContaining("age");
            assertThat(entity.age).as("nothing was misread").isNull();
        }
    }
}
