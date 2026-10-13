package com.github.jinahya.persistence.encryption;

import com.github.jinahya.persistence.encryption.examples.CollectionSecretEntity;
import com.github.jinahya.persistence.encryption.examples.FrozenPlainEntity;
import com.github.jinahya.persistence.encryption.examples.GenericDeepEntity;
import com.github.jinahya.persistence.encryption.examples.GenericIntegerEntity;
import com.github.jinahya.persistence.encryption.examples.GenericStringEntity;
import com.github.jinahya.persistence.encryption.examples.GraphEntity;
import com.github.jinahya.persistence.encryption.examples.InheritedOverrideEntity;
import com.github.jinahya.persistence.encryption.examples.MisnamedPlaintextEntity;
import com.github.jinahya.persistence.encryption.examples.MisplacedAnnotationEntity;
import com.github.jinahya.persistence.encryption.examples.OverriddenEntity;
import com.github.jinahya.persistence.encryption.examples.PlainEntity;
import com.github.jinahya.persistence.encryption.examples.PlaintextAnnotatedEntity;
import com.github.jinahya.persistence.encryption.examples.RescuedEntity;
import com.github.jinahya.persistence.encryption.examples.SecretEmbeddable;
import com.github.jinahya.persistence.encryption.examples.SecretEntity;
import com.github.jinahya.persistence.encryption.examples.TwoPathEntity;
import com.github.jinahya.persistence.encryption.examples.UnguardedEmbeddable;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Verifies that {@link EntityEncryptor} moves every supported java type between the decrypted attribute and its paired
 * encrypted attribute, and back, against a real metamodel.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
@NoArgsConstructor(access = AccessLevel.PACKAGE)
class EntityEncryptor_Test {

    private static SecretEntity populated() {
        final var entity = new SecretEntity();
        entity.name = "Jane Roe";
        entity.age = 37;
        entity.reference = UUID.randomUUID();
        entity.bornOn = LocalDate.of(1988, 11, 3);
        entity.seenAt = java.sql.Timestamp.valueOf("2026-01-02 03:04:05.123456789");
        entity.joinedAt = new java.util.Date(1_700_000_000_000L);
        entity.grade = SecretEntity.Grade.HIGH;
        entity.photo = new byte[32];
        ThreadLocalRandom.current().nextBytes(entity.photo);
        entity.secretNumber = 1_234_567_890_123L;
        entity.secret = new SecretEmbeddable("nested");
        return entity;
    }

    // -----------------------------------------------------------------------------------------------------------------
    private static EntityManagerFactory ENTITY_MANAGER_FACTORY;

    // -----------------------------------------------------------------------------------------------------------------
    @BeforeAll
    static void openEntityManagerFactory() {
        ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory("__encryptionPU");
    }

    @AfterAll
    static void closeEntityManagerFactory() {
        if (ENTITY_MANAGER_FACTORY != null) {
            ENTITY_MANAGER_FACTORY.close();
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    @BeforeEach
    void createService() {
        service = new EntityEncryptor(new EntityEncryptionServiceImpl(ENTITY_MANAGER_FACTORY));
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("a provider proxy is resolved to its entity class")
    @Nested
    class ProxyTest {

        @DisplayName("encrypt/decrypt accept a getReference() proxy")
        @Test
        void __proxy() {
            final var manager = new EntityEncryptionServiceImpl(ENTITY_MANAGER_FACTORY);
            final var svc = new EntityEncryptor(manager);

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
                final var reference = em.getReference(SecretEntity.class, id);
                assertThat(reference).isNotNull();

                assertThatCode(() -> svc.decrypt(reference))
                        .as("the metamodel knows SecretEntity, not the proxy subclass")
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
            final var manager = new EntityEncryptionServiceImpl(ENTITY_MANAGER_FACTORY);
            final var service = new EntityEncryptor(manager);
            final var entity = populated();
            final var identifier = manager.getEncryptionIdentifier(entity);

            entity.grade = null;
            // a well-formed header, so the bytes reach the enum codec, which cannot find the constant
            entity.gradeEnc__ = manager.encrypt(identifier, EntityEncryptorUtils.frame(
                    EntityEncryptorUtils.Codec.ENUM_, "NOPE".getBytes(java.nio.charset.StandardCharsets.UTF_8)));

            assertThatThrownBy(() -> service.decrypt(entity))
                    .as("the failure has to say which attribute and which java type")
                    .hasMessageContaining("grade")
                    .hasMessageContaining("Grade");
        }

        @DisplayName("a payload of an unknown format version is rejected, naming the attribute (#75)")
        @Test
        void __unknownFormatVersion() {
            final var manager = new EntityEncryptionServiceImpl(ENTITY_MANAGER_FACTORY);
            final var service = new EntityEncryptor(manager);
            final var entity = populated();
            final var framed = EntityEncryptorUtils.frame(
                    EntityEncryptorUtils.Codec.INT_4, ValueCodecs.OfPrimitive.encodeInt(37));
            framed[0] = (byte) (EntityEncryptorUtils.FORMAT_VERSION + 1);
            entity.age = null;
            entity.ageEnc__ = manager.encrypt(manager.getEncryptionIdentifier(entity), framed);

            assertThatThrownBy(() -> service.decrypt(entity))
                    .hasMessageContaining("age")
                    .cause()
                    .hasMessageContaining("unknown payload format version");
        }

        @DisplayName(
                "a payload written by another codec is rejected rather than misread, e.g. after Long -> Integer (#75)")
        @Test
        void __codecMismatch() {
            final var manager = new EntityEncryptionServiceImpl(ENTITY_MANAGER_FACTORY);
            final var service = new EntityEncryptor(manager);
            final var entity = populated();
            // what a row holds when `age` was declared Long when it was written, and is Integer now
            entity.age = null;
            entity.ageEnc__ = manager.encrypt(manager.getEncryptionIdentifier(entity), EntityEncryptorUtils.frame(
                    EntityEncryptorUtils.Codec.LONG_8, ValueCodecs.OfPrimitive.encodeLong(37L)));

            assertThatThrownBy(() -> service.decrypt(entity))
                    .hasMessageContaining("age")
                    .cause()
                    .hasMessageContaining("encoded by LONG_8")
                    .hasMessageContaining("decoded by INT_4");
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

        @DisplayName(
                "a value which would not be read back as is, is rejected against its attribute, without the value (#77)")
        @Test
        void __unencodableValueRejected() {
            final var entity = new SecretEntity();
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
            final var entity = new SecretEntity();
            entity.secret = new SecretEmbeddable(null);

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
            final var entity = new GraphEntity();
            entity.valid = new SecretEmbeddable("keep-me");
            entity.invalid = new UnguardedEmbeddable();
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
            // SecretEmbeddable.note declares insertable = false and is safe through SecretEntity.secret, which
            // adds no override. Through OverriddenEntity the override only renames, so its own @Column defaults
            // restore insertable = true. One service instance must reach both verdicts for the same embeddable.
            final var accepted = populated();
            service.encrypt(accepted); // caches SecretEmbeddable at path [secret]
            assertThat(accepted.secret.getNoteEnc__()).as("accepted through this path").isNotNull();

            final var rejected = new OverriddenEntity();
            rejected.overridden = new SecretEmbeddable("leak-me");

            assertThatThrownBy(() -> service.encrypt(rejected))
                    .as("a type-keyed cache would have reused the accepted mapping")
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("non-insertable")
                    .hasMessageContaining("@AttributeOverride");
            assertThat(rejected.overridden.getNote()).as("and nothing was touched").isEqualTo("leak-me");
        }

        @DisplayName("a class-level override of an inherited attribute is honoured")
        @Test
        void __inheritedAttributeClassLevelOverride() {
            // the attribute is held directly, so the embedding path is empty; the override lives on the entity class
            final var entity = new InheritedOverrideEntity();
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
            // UnguardedEmbeddable.leak declares an insertable column; the override makes it non-insertable, and the
            // override is what the provider actually uses, so the mapping has to be accepted
            final var entity = new RescuedEntity();
            entity.rescued = new UnguardedEmbeddable();
            entity.rescued.leak = "rescued-value";

            service.encrypt(entity);

            assertThat(entity.rescued.leak).isNull();
            assertThat(entity.rescued.leakEnc__).isNotNull().isNotEmpty();
        }

        @DisplayName("two paths to one embeddable under the same root do not share a mapping")
        @Test
        void __twoPathsUnderOneRoot() {
            // TwoPathEntity reaches SecretEmbeddable twice, under one root, with different overrides. A cache
            // keyed by (root, type) rather than (root, path) would conflate the two.
            final var entity = new TwoPathEntity();
            entity.first = new SecretEmbeddable("one");
            entity.second = new SecretEmbeddable("two");

            service.encrypt(entity);

            assertThat(entity.first.getNote()).isNull();
            assertThat(entity.second.getNote()).isNull();
            assertThat(entity.first.getNoteEnc__()).as("each path is encrypted on its own").isNotNull();
            assertThat(entity.second.getNoteEnc__()).isNotNull();

            service.decrypt(entity);
            assertThat(entity.first.getNote()).as("and each decrypts back to its own value").isEqualTo("one");
            assertThat(entity.second.getNote()).isEqualTo("two");
        }

        @DisplayName("a null embeddable does not hide an invalid mapping")
        @Test
        void __nullEmbeddableStillValidated() {
            final var entity = new GraphEntity();
            entity.valid = new SecretEmbeddable("keep-me");
            entity.invalid = null; // never reached by the transformation, still validated

            assertThatThrownBy(() -> service.encrypt(entity))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("non-insertable");
        }

        @DisplayName(
                "an annotated member which is neither persistent nor a transient field is rejected, not silently ignored (#70)")
        @Test
        void __misplacedAnnotationRejected() {
            final var entity = new MisplacedAnnotationEntity();
            entity.name = "never-encrypted";

            assertThatThrownBy(() -> service.encrypt(entity))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("is not a persistent attribute")
                    .hasMessageContaining("MisplacedAnnotationEntity.getNameEnc__");
            assertThat(entity.nameEnc__).as("and nothing was touched").isNull();
        }

        @DisplayName("an annotation left on the plaintext, naming the ciphertext, is rejected with a hint")
        @Test
        void __plaintextAnnotatedRejected() {
            final var entity = new PlaintextAnnotatedEntity();
            entity.name = "never-encrypted";

            assertThatThrownBy(() -> service.encrypt(entity))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("has to be typed byte[]")
                    .hasMessageContaining("holding the ciphertext, and names the plaintext");
            assertThat(entity.nameEnc__).as("and nothing was touched").isNull();
        }

        @DisplayName("an annotation naming a plaintext which does not exist is rejected")
        @Test
        void __misnamedPlaintextRejected() {
            final var entity = new MisnamedPlaintextEntity();
            entity.name = "never-encrypted";

            assertThatThrownBy(() -> service.encrypt(entity))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("no plaintext named 'nmae'");
            assertThat(entity.nameEnc__).as("and nothing was touched").isNull();
        }

        @DisplayName(
                "an encrypted embeddable reached through an @ElementCollection is rejected, not persisted in the clear (#71)")
        @Test
        void __elementCollectionRejected() {
            final var entity = new CollectionSecretEntity();
            entity.secrets.add(new SecretEmbeddable("TOP-SECRET"));

            assertThatThrownBy(() -> service.encrypt(entity))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("@ElementCollection")
                    .hasMessageContaining("SecretEmbeddable.note");
            assertThat(entity.secrets.getFirst().getNote()).as("nothing was touched").isEqualTo("TOP-SECRET");
            assertThat(entity.secrets.getFirst().getNoteEnc__()).isNull();
        }

        @DisplayName("an entity with no encrypted attribute passes through untouched (#72)")
        @Test
        void __plainEntityPassesThrough() {
            // a service whose cryptography must never be reached: not even for the encryption identifier
            final var untouchable = new EntityEncryptor(new EntityEncryptionService() {
                @Override
                public jakarta.persistence.EntityManagerFactory getEntityManagerFactory() {
                    return ENTITY_MANAGER_FACTORY;
                }

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
            final var entity = new PlainEntity();
            entity.name = "plain";

            assertThatCode(() -> untouchable.encrypt(entity)).doesNotThrowAnyException();
            assertThatCode(() -> untouchable.decrypt(entity)).doesNotThrowAnyException();
            assertThat(entity.name).isEqualTo("plain");
        }

        @DisplayName("a plaintext column which is not updatable is rejected (#73)")
        @Test
        void __nonUpdatablePlaintextRejected() {
            final var entity = new FrozenPlainEntity();
            entity.name = "frozen";

            assertThatThrownBy(() -> service.encrypt(entity))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("has to be updatable");
            assertThat(entity.name).as("nothing was touched").isEqualTo("frozen");
        }
    }

    @DisplayName("an attribute declared as a type variable of a generic mapped superclass (#67)")
    @Nested
    class GenericSuperclassTest {

        @DisplayName("is encoded by the type each entity binds it to, not by its erasure")
        @Test
        void __twoBindingsOfOneSuperclass() {
            final var string = new GenericStringEntity();
            string.secret = "generic secret";
            final var integer = new GenericIntegerEntity();
            integer.secret = 37;

            service.encrypt(string);
            service.encrypt(integer);
            assertThat(string.secretEnc__).isNotNull();
            assertThat(integer.secretEnc__).isNotNull();

            service.decrypt(string);
            service.decrypt(integer);
            assertThat(string.secret).isEqualTo("generic secret");
            assertThat(integer.secret).as("an Integer, decoded by int_4").isEqualTo(37);
        }

        @DisplayName("is resolved through more than one level of binding")
        @Test
        void __multiLevelBinding() {
            final var deep = new GenericDeepEntity();
            deep.secret = LocalDate.of(2026, 10, 9);

            service.encrypt(deep);
            assertThat(deep.secret).isNull();
            service.decrypt(deep);
            assertThat(deep.secret).isEqualTo(LocalDate.of(2026, 10, 9));
        }
    }

    @DisplayName("a failure part-way leaves the instance as it was (#83)")
    @Nested
    class AllOrNothingTest {

        @DisplayName("encrypt(): one unencodable attribute, and no other attribute, nor the embeddable, is touched")
        @Test
        void __encrypt() {
            final var entity = populated();
            entity.name = "a\uD800b"; // not encodable; every other attribute is

            assertThatThrownBy(() -> service.encrypt(entity)).isInstanceOf(RuntimeException.class);

            assertThat(entity.age).isEqualTo(37);
            assertThat(entity.bornOn).isEqualTo(LocalDate.of(1988, 11, 3));
            assertThat(entity.secretNumber).isEqualTo(1_234_567_890_123L);
            assertThat(entity.secret.getNote()).isEqualTo("nested");
            assertThat(entity.nameEnc__).isNull();
            assertThat(entity.ageEnc__).isNull();
            assertThat(entity.bornOnEnc__).isNull();
            assertThat(entity.secretNumberCipher).isNull();
            assertThat(entity.secret.getNoteEnc__()).isNull();
        }

        @DisplayName("decrypt(): one unreadable ciphertext, and no other attribute, nor the embeddable, is touched")
        @Test
        void __decrypt() {
            final var manager = new EntityEncryptionServiceImpl(ENTITY_MANAGER_FACTORY);
            final var service = new EntityEncryptor(manager);
            final var entity = populated();
            service.encrypt(entity);
            final var nameEnc = entity.nameEnc__.clone();
            final var noteEnc = entity.secret.getNoteEnc__().clone();
            // a payload of another codec where an Integer is expected
            entity.ageEnc__ = manager.encrypt(manager.getEncryptionIdentifier(entity), EntityEncryptorUtils.frame(
                    EntityEncryptorUtils.Codec.LONG_8, ValueCodecs.OfPrimitive.encodeLong(37L)));

            assertThatThrownBy(() -> service.decrypt(entity)).isInstanceOf(RuntimeException.class);

            assertThat(entity.name).as("nothing was decrypted").isNull();
            assertThat(entity.age).isNull();
            assertThat(entity.secret.getNote()).isNull();
            assertThat(entity.nameEnc__).as("and no ciphertext was cleared").isEqualTo(nameEnc);
            assertThat(entity.secret.getNoteEnc__()).isEqualTo(noteEnc);
        }
    }

    @DisplayName("no array is shared with the encryption service")
    @Nested
    class ArrayIsolationTest {

        @DisplayName("what the service is given, and what it returns, are never the entity's own arrays")
        @Test
        void __neverShared() {
            final var given = java.util.Collections.synchronizedList(new java.util.ArrayList<byte[]>());
            final var returned = java.util.Collections.synchronizedList(new java.util.ArrayList<byte[]>());
            final var cryptography = new EntityEncryptionServiceImpl(ENTITY_MANAGER_FACTORY) {
                @Override
                public byte[] encrypt(final String encryptionIdentifier, final byte[] decryptedBytes) {
                    given.add(decryptedBytes);
                    final var result = super.encrypt(encryptionIdentifier, decryptedBytes);
                    returned.add(result);
                    return result;
                }

                @Override
                public byte[] decrypt(final String encryptionIdentifier, final byte[] encryptedBytes) {
                    given.add(encryptedBytes);
                    final var result = super.decrypt(encryptionIdentifier, encryptedBytes);
                    returned.add(result);
                    return result;
                }
            };
            final var encryptor = new EntityEncryptor(cryptography);
            final var entity = populated();
            final var photo = entity.photo.clone();

            encryptor.encrypt(entity);
            final var stored = entity.photoEnc__;
            assertThat(given).as("the plaintext array is not handed over").noneMatch(a -> a == entity.photo);
            assertThat(returned).as("the stored ciphertext is not the service's array").noneMatch(a -> a == stored);

            // a service which reuses its buffers: scribbling over everything it touched changes nothing of the entity
            given.forEach(a -> java.util.Arrays.fill(a, (byte) 0));
            returned.forEach(a -> java.util.Arrays.fill(a, (byte) 0));
            given.clear();
            returned.clear();
            encryptor.decrypt(entity);
            assertThat(entity.photo).isEqualTo(photo);

            assertThat(given).as("the stored ciphertext is not handed over").noneMatch(a -> a == stored);
            assertThat(returned).as("the decrypted value is not the service's array").noneMatch(a -> a == entity.photo);
        }
    }

    @DisplayName("the encryption identifier is opaque")
    @Nested
    class EncryptionIdentifierTest {

        @DisplayName("whatever the service returns, null and blank included, reaches encrypt and decrypt as it is")
        @Test
        void __identifierPassedAsItIs() {
            for (final var identifier : new String[]{"", "  ", null, "tenant-1"}) {
                final var seen = java.util.Collections.synchronizedList(new java.util.ArrayList<String>());
                final var cryptography = new EntityEncryptionServiceImpl(ENTITY_MANAGER_FACTORY) {
                    @Override
                    public String getEncryptionIdentifier(final Object entityInstance) {
                        return identifier;
                    }

                    @Override
                    public byte[] encrypt(final String encryptionIdentifier, final byte[] decryptedBytes) {
                        seen.add(encryptionIdentifier);
                        return super.encrypt(encryptionIdentifier, decryptedBytes);
                    }

                    @Override
                    public byte[] decrypt(final String encryptionIdentifier, final byte[] encryptedBytes) {
                        seen.add(encryptionIdentifier);
                        return super.decrypt(encryptionIdentifier, encryptedBytes);
                    }
                };
                final var encryptor = new EntityEncryptor(cryptography);
                final var entity = populated();

                encryptor.encrypt(entity);
                assertThat(entity.nameEnc__).isNotNull();
                encryptor.decrypt(entity);

                assertThat(entity.name).as("round trip with identifier %s", identifier).isEqualTo("Jane Roe");
                assertThat(seen).as("identifiers passed to the service").isNotEmpty().containsOnly(identifier);
            }
        }
    }

    @DisplayName("decrypt()")
    @Nested
    class DecryptTest {

        @DisplayName("a truncated ciphertext is reported against the attribute, not as an index error")
        @Test
        void __truncated() {
            final var entity = new SecretEntity();
            entity.ageEnc__ = new byte[
                    EntityEncryptionServiceImpl.IV_BYTES + EntityEncryptionServiceImpl.KEY_BYTES +
                    EntityEncryptionServiceImpl.AAD_BYTES];
            // a well-formed header with an empty payload; Integer needs four bytes after it
            final var manager = new EntityEncryptionServiceImpl(ENTITY_MANAGER_FACTORY);
            entity.ageEnc__ = manager.encrypt("irrelevant", EntityEncryptorUtils.frame(
                    EntityEncryptorUtils.Codec.INT_4, new byte[0]));

            assertThatThrownBy(() -> service.decrypt(entity))
                    .isInstanceOf(RuntimeException.class)
                    // the wording generalised when the guard widened to cover odd-length char[] payloads
                    // and unknown enum constants, which are not "too short" but are equally unreadable
                    .hasMessageContaining("cannot reconstruct the value")
                    .hasMessageContaining("age")
                    .hasMessageContaining("decrypted bytes: " + EntityEncryptorUtils.HEADER_BYTES);
        }

        @DisplayName("an over-long payload is rejected, not read as its first bytes (#76)")
        @Test
        void __overLong() {
            final var entity = new SecretEntity();
            // a correct header, but eight bytes where Integer reads four
            final var manager = new EntityEncryptionServiceImpl(ENTITY_MANAGER_FACTORY);
            entity.ageEnc__ = manager.encrypt("irrelevant", EntityEncryptorUtils.frame(
                    EntityEncryptorUtils.Codec.INT_4, ValueCodecs.OfPrimitive.encodeLong(37L)));

            assertThatThrownBy(() -> service.decrypt(entity))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("age")
                    .cause()
                    .hasMessageContaining("reads exactly 4");
            assertThat(entity.age).as("nothing was misread").isNull();
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    private EntityEncryptor service;
}
