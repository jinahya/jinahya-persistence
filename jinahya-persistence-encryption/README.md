# jinahya-persistence-encryption

![Maven Central Version](https://img.shields.io/maven-central/v/io.github.jinahya/jinahya-persistence-encryption)
[![javadoc](https://javadoc.io/badge2/io.github.jinahya/jinahya-persistence-encryption/javadoc.svg)](https://javadoc.io/doc/io.github.jinahya/jinahya-persistence-encryption)

Application-level encryption (ALE) for Jakarta Persistence entities: selected attributes are encrypted inside the
application, so the database only ever stores their ciphertext. The cryptography and the keys are not this module's
concern; they live behind `EntityEncryptionService`, which an application implements (with a KMS, Vault, Tink, …).

## How it works

### The pieces

| type | what it is | who writes it |
|------|------------|---------------|
| `@EncryptedAttribute` | marks the `byte[]` attribute holding a ciphertext, and names the attribute holding its plaintext | the application, on its entities |
| `EntityEncryptionListener` | the entity listener which does the work | the module; the application only registers it |
| `EntityEncryptionService` | which persistence unit, which key, and the cryptography itself | the application, as one bean |

That is the whole public surface. The mechanism in between — finding the encrypted attributes, turning values into
bytes, deciding what to write — is internal.

### A complete example

```java
@EntityListeners(EntityEncryptionListener.class)
@Entity
class User {

    @Column(name = "name", insertable = false) // the plaintext: mapped, but its column never gets a value
    private String name;

    @EncryptedAttribute("name")                // the ciphertext of name: the column which is stored
    @Column(name = "name_enc")
    private byte[] nameEnc;
}

@ApplicationScoped // or a Spring @Component
class KmsEncryptionService implements EntityEncryptionService {
    // getEntityManagerFactory(), getEncryptionIdentifier(entity), encrypt(id, bytes), decrypt(id, bytes)
}
```

Each encrypted value has two persistent attributes in the same entity: the `byte[]` one, annotated, which holds the
ciphertext, and the one it names, which holds the plaintext. The name is required. The plaintext stays mapped to its
own column, which is never written with a value: on a table which already holds plaintext, that is the existing
column, and its rows are migrated as they are written ([Existing data](#existing-data)).

### What happens

```
em.persist(user) / flush ──► @PrePersist, @PreUpdate ──► encrypt   user.name ──► user.nameEnc
                             @PostUpdate             ──► decrypt   user.nameEnc ──► user.name   (restored)
em.find(User.class, id)  ──► @PostLoad               ──► decrypt   user.nameEnc ──► user.name
```

For each encrypted attribute of the instance, and of its embeddables:

1. **encrypt** — the plaintext is encoded to bytes by its declared java type, behind a two-byte header naming the
   format and the codec ([Encoding](#encoding)); the service encrypts those bytes; the result is stored in the
   ciphertext attribute.
2. **decrypt** — the service decrypts the ciphertext; the header is checked, and the bytes decoded back to a value of
   the declared type; the value is set on the plaintext.

Each instance is changed **all or nothing**: every value is computed before any attribute is assigned. The mapping of
an entity class is validated in full on its first callback, before any instance is touched ([Constraints](#constraints)).

Two rules about what reaches the service:

* **`null` is `null`.** An absent value never reaches the service: a `null` plaintext is stored as a `null`
  ciphertext, and a `null` ciphertext read as a `null` plaintext, without a call. So the bytes the service is given,
  and the bytes it returns, are never `null`; every array crossing in either direction is a copy.
* **The encryption identifier is opaque.** The service derives it from the instance, and gets it back, unchanged, on
  every `encrypt` and `decrypt` of that instance — `null` included. What it means is entirely the service's; it has to
  be the same for the whole lifetime of the row ([The application's responsibility](#the-applications-responsibility)).

How the listener gets the service is under [Wiring](#wiring).

## Existing data

The plaintext's column doubles as the legacy column, so encryption can be introduced on a table which already holds
plaintext, with no separate tool and no write freeze. Measured on both providers
(`EncryptionLifecycle_Test`):

| row                                   | on read (`@PostLoad`)                    | on the next write                                          |
|---------------------------------------|------------------------------------------|------------------------------------------------------------|
| legacy: plaintext, no ciphertext      | left alone; the application reads it     | encrypted; the `UPDATE` stores the ciphertext and nulls the plaintext column |
| migrated: no plaintext, ciphertext    | decrypted                                | re-encrypted                                               |

A row which is never written again stays plaintext. A batch for those rows is not offered at present
([#87](https://github.com/jinahya/jinahya-persistence/issues/87)).

Migrating on write relies on the plaintext column being updatable, which is why that is required ([#73](https://github.com/jinahya/jinahya-persistence/issues/73)). Nulling the column
does not erase the plaintext from backups, logs, replicas or indexes; scrubbing those is the application's.

### The rules

**The plaintext attribute is the truth**: the application reads and writes it like any other attribute, and never sees
it cleared. The listener only keeps it out of the row, and keeps the ciphertext in step.

| callback | plaintext | ciphertext |
|---|---|---|
| `@PrePersist` | kept: the `INSERT` never carries the column, which is non-insertable | encrypt(plaintext), or `null` when there is no plaintext |
| `@PreUpdate` | cleared: the `UPDATE` carries the column, which is updatable, so it goes out as `NULL` | encrypt(plaintext), or `null` when there is no plaintext |
| `@PostUpdate` | restored: decrypt(ciphertext) | unchanged |
| `@PostLoad` | decrypt(ciphertext); left alone when there is no ciphertext — a legacy row, migrated on its next write | unchanged |

So setting the plaintext to `null` clears the value, and nothing else needs touching.

### What it costs

The plaintext is a *mapped* attribute, and a JPA listener can only keep its value out of the row by changing the
field. These follow, and cannot be fixed inside the standard callbacks: they are the price of using standard Jakarta
Persistence APIs only.

* **Reading an entity writes it.** `@PostLoad` runs after the provider has taken its loaded-state snapshot, so setting
  the decrypted value leaves the instance dirty from the moment it is read. Measured on both providers: a transaction
  which only reads still issues an `UPDATE`, increments `@Version`, and — encryption being randomized — rewrites the
  ciphertext with a fresh IV. Optimistic locking is unusable, every read shows up in replication, CDC and audit, and a
  read-only connection fails.
* **Writing keeps writing.** `@PostUpdate` restores the plaintext the `UPDATE` went out without, so the instance differs
  from the provider's snapshot again: every further flush in the same persistence context issues another `UPDATE`.
* **No Bean Validation constraint** may sit on the plaintext: Jakarta Persistence validates an update after
  `@PreUpdate` has cleared it ([#9](https://github.com/jinahya/jinahya-persistence/issues/9)).
* **The insert window.** `@PrePersist` fires when `persist()` is called, not when the `INSERT` is built. The plaintext
  column has to be `insertable = false` (enforced), which guarantees *INSERT confidentiality only*:

  | after `persist()`, the application sets the plaintext again | Hibernate 7.4                            | EclipseLink 5.0                     |
  |-------------------------------------------------------------|------------------------------------------|-------------------------------------|
  | plain `@Column`                                              | not written (an `UPDATE` re-encrypts it) | **written, and left, in the clear** |
  | `@Column(insertable = false)`                                | not written                              | not written                         |

  On EclipseLink the late assignment is *dropped* rather than stored. **Do not modify an encrypted attribute between
  `persist()` and the flush.**

## Wiring

Register `EntityEncryptionListener` itself, the way Spring Data JPA's `AuditingEntityListener` is registered; no
subclass is needed. It encrypts on `@PrePersist` and `@PreUpdate`, before the statement is built, decrypts on
`@PostLoad` ([#5](https://github.com/jinahya/jinahya-persistence/issues/5)), and restores the plaintext on `@PostUpdate`.

Then provide one `EntityEncryptionService` as a bean. It is the only type an application implements:

```java
@ApplicationScoped // or a Spring @Component
class KmsEncryptionService implements EntityEncryptionService {

    @Inject
    Provider<EntityManagerFactory> entityManagerFactory; // qualify it as you like; resolved lazily, see below

    @Override
    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory.get();
    }

    @Override
    public String getEncryptionIdentifier(Object entityInstance) { ... }

    @Override
    public byte[] encrypt(String encryptionIdentifier, byte[] decryptedBytes) { ... }

    @Override
    public byte[] decrypt(String encryptionIdentifier, byte[] encryptedBytes) { ... }
}
```

* **The entity manager factory** names the persistence unit whose entities are encrypted; its metamodel is where the
  encrypted attributes are found. Resolve it lazily — a `jakarta.inject.Provider`, or Spring's `ObjectFactory`: the
  persistence provider creates the listener while it is still building the factory, and the listener asks for it only
  on its first callback.
* **Qualification is yours.** The listener's one injection point is the module's own type, so whatever this service
  needs — which factory, which key client — it injects with its own qualifiers.

The listener is injected by the container, which happens only when the persistence provider creates its listeners
through one:

| platform | Hibernate ORM | EclipseLink |
|---|---|---|
| Jakarta EE container | yes | yes |
| CDI in Java SE, with the `BeanManager` passed as `jakarta.persistence.bean.manager` (below) | yes (tested) | yes (tested) |
| Spring Boot, through Hibernate's `SpringBeanContainer`; needs `jakarta.inject-api` on the class path | yes (not tested here) | **no** |

```java
var factory = Persistence.createEntityManagerFactory(
        "unit", Map.of("jakarta.persistence.bean.manager", container.getBeanManager()));
```

A listener created without one has no service, and its first callback fails with an `IllegalStateException`.

**Never encrypt from `@PostPersist` or `@PostUpdate`** — the row is already written by the time the post-callbacks
run.

## Constraints

The mapping of an entity class is validated in full **before any instance of it is touched**, on the first callback
for that class, and every failure of it is reported at once; there is no separate validation at startup. Each encrypt and decrypt is **all-or-nothing** per instance: every value is computed before any is assigned ([#83](https://github.com/jinahya/jinahya-persistence/issues/83)). An entity is encrypted
when it has an `@EncryptedAttribute`, directly or in an embeddable; one with none passes through untouched. A mapping
which breaks any of these is rejected:

| rule |
|------|
| `@EncryptedAttribute` sits on the persistent attribute holding the ciphertext; anywhere else — unmapped, transient, a getter the access type does not read — is rejected ([#70](https://github.com/jinahya/jinahya-persistence/issues/70)) |
| the plaintext it names is a persistent attribute of the same entity or embeddable; it is not itself annotated, and is named only once |
| the plaintext is `@Basic`, optional, not of a primitive type, neither an identifier nor a version |
| the plaintext's declared java type has a codec (see [Encoding](#encoding)); for a member of a generic `@MappedSuperclass`, the type the entity binds it to ([#67](https://github.com/jinahya/jinahya-persistence/issues/67)) |
| the plaintext column is **non-insertable**, nullable and **updatable** ([#73](https://github.com/jinahya/jinahya-persistence/issues/73)) |
| no Bean Validation constraint on the plaintext ([#9](https://github.com/jinahya/jinahya-persistence/issues/9)) |
| the ciphertext attribute is `@Basic`, optional, typed `byte[]`, not an identifier or a version, and its column is insertable, updatable and nullable |
| an `@Embedded` attribute is not itself annotated; annotate the ciphertext attributes inside the embeddable, which are descended into |
| an embeddable reached through an `@ElementCollection` — element or map key — holds no `@EncryptedAttribute` at any depth: collections are not walked ([#71](https://github.com/jinahya/jinahya-persistence/issues/71)) |

### How the column rules are established

Jakarta Persistence exposes no portable accessor for effective per-column metadata: neither the metamodel,
`PersistenceUnitUtil`, `SchemaManager` nor `EntityManagerFactory.getProperties()` reports whether a column is
insertable. So the module reads the **annotation** mapping. It resolves the applicable `@AttributeOverride` —
searching the root class by the whole dotted path, then each embedding attribute by its own suffix, outermost winning
— and otherwise reads the attribute's own `@Column`, honouring the annotation defaults when neither is present. Note
that an override carries its own `@Column`, with its own defaults, so **an override which only renames a column
restores `insertable = true`** even when the embeddable's own member declares otherwise. The same embeddable can
therefore be safe on one embedding path and unsafe on another, and an embeddable is validated for each path it is
reached by, not once per type.

**A mapping in `orm.xml` is not visible.** A plaintext mapped only there reads as the annotation defaults,
which are insertable, and is rejected. An `orm.xml` mapping which makes an annotation-safe attribute unsafe cannot be
seen either.

## The application's responsibility

This module moves values between the plaintext and the ciphertext; it imposes no policy of its own.

* **The encryption identifier.** `EntityEncryptionService.getEncryptionIdentifier(Object)` is never stored; it is
  derived again on every encrypt and decrypt, so it has to be the same for the whole lifetime of the row — from an
  unencrypted, never-updated attribute such as a tenant column; never the version, a *current* key id, or an
  `IDENTITY` id, which is still `null` at `@PrePersist` ([#61](https://github.com/jinahya/jinahya-persistence/issues/61)).
* **The cipher suite.** `EntityEncryptionService` decides the algorithm and the keys. An *authenticated* mode (AES-GCM,
  for example) is recommended: tampering with the stored ciphertext is then detected by the service, before any byte
  is decoded.
* **Secondary tables.** The ciphertext attribute may be mapped to a column of a `@SecondaryTable`
  (`@Column(table = "user_secret", …)`): attributes are found in the metamodel, never in tables, so it is an ordinary
  attribute of the same entity. The secondary rows are the schema's and the persistence provider's
  matter: EclipseLink does not find an entity whose secondary row is missing, so rows which exist before the mapping
  does need theirs inserted first; Hibernate ORM finds them.
* **Sizes and lengths.** Nothing is limited: the size of a value, of its ciphertext, and of the column holding it are
  the application's concern, as they are for a plain mapping.
* **Deserialization.** A `Serializable` attribute is deserialized without a filter of this module's own, so the
  JVM-wide `jdk.serialFilter` applies, exactly as for a plain `Serializable` mapping. Configure class allow-lists and
  graph limits there ([#8](https://github.com/jinahya/jinahya-persistence/issues/8),
  [#10](https://github.com/jinahya/jinahya-persistence/issues/10)):

  ```
  -Djdk.serialFilter=maxdepth=32;maxrefs=10000;maxbytes=1048576;java.base/*;com.example.model.*;!*
  ```

## Not supported

* **Querying an encrypted attribute.** The database holds only ciphertext: predicates and orderings against the
  plaintext column match what is *stored*, which is `NULL`.
* **Bulk JPQL `UPDATE`/`DELETE`** bypass entity callbacks entirely and leave managed state unsynchronized.
* **Scalar projections** do not load entities, so nothing decrypts them.
* **Collections and associations.** Only `@Basic` and `@Embedded` are walked; an encrypted embeddable inside an
  `@ElementCollection` is rejected ([#71](https://github.com/jinahya/jinahya-persistence/issues/71)); cascaded entities need their own listener registration.
* **Change-tracking class enhancement is not verified** — EclipseLink weaving with change tracking, and Hibernate
  ORM bytecode enhancement with dirty tracking. The listener writes entity fields by reflection, which such
  enhancement may not see; tracked this way, the provider could leave out of a write what the listener put into the
  entity. Hibernate ORM's `@DynamicInsert` and `@DynamicUpdate`, without enhancement, are verified
  (`HibernateDynamic_Test`).
* **The shared (second-level) cache.** The test persistence units set `shared-cache-mode` to `NONE`; nothing here is
  proven against a cache hit.

## Encoding

The `EntityEncryptionService` receives, and returns, an array of bytes. Values are turned into those bytes by the
attribute's *declared* java type — for a member inherited from a generic `@MappedSuperclass`, the type the concrete
entity binds it to ([#67](https://github.com/jinahya/jinahya-persistence/issues/67)) — big-endian, and self-contained:
a value is reconstructed from its bytes without consulting the database. The sizes below are of the plaintext
encoding, after the header and before the service is called. Every encoding is pinned byte for byte by
`EntityEncryptorUtils_Test`.

### Payload header

Every encoded value is prefixed with a two-byte header before it reaches the service
([#75](https://github.com/jinahya/jinahya-persistence/issues/75)):

| byte | content                                                                                   |
|------|-------------------------------------------------------------------------------------------|
| `0`  | the format version, currently `1`; any other version is rejected                          |
| `1`  | the id of the codec which encoded the value (`EntityEncryptorUtils.Codec`); stored data, never renumbered |

A reader checks both, so a payload written by another codec — an attribute whose declared type changed after the row
was written, say `Long` to `Integer` — is rejected, naming the attribute, rather than misread. Ciphertext written before
the header existed is not readable; the module is pre-1.0, and offers no migration for it
([#19](https://github.com/jinahya/jinahya-persistence/issues/19)).

Every fixed-width decoder reads a payload of **exactly** its width, and rejects anything shorter or longer
([#76](https://github.com/jinahya/jinahya-persistence/issues/76)); a variable-width one rejects what it cannot read.
The checks are unconditional, not `assert`s.

The codecs and the header are format version `1`. A change to any of them from now on is a format change: a new
version, which a reader of this version rejects.

| attribute              | bytes      | encoding                                                    |
|------------------------|------------|-------------------------------------------------------------|
| `Boolean`              | `1`        | `0` for `false`, `1` for `true`; any other byte is rejected ([#78](https://github.com/jinahya/jinahya-persistence/issues/78)) |
| `Byte`                 | `1`        | the value                                                   |
| `Short`                | `2`        | big endian                                                  |
| `Character`            | `2`        | big endian                                                  |
| `Integer`              | `4`        | big endian                                                  |
| `Float`                | `4`        | the raw `32`-bit int                                        |
| `Long`                 | `8`        | big endian                                                  |
| `Double`               | `8`        | the raw `64`-bit long                                       |
| `UUID`                 | `16`       | `getMostSignificantBits()` + `getLeastSignificantBits()`    |
| `String`               | variable   | the `utf-8` encoded bytes, strictly: an unpaired surrogate is rejected on encrypt, malformed `utf-8` on decrypt ([#77](https://github.com/jinahya/jinahya-persistence/issues/77)) |
| `BigInteger`           | variable   | `toByteArray()`                                             |
| `BigDecimal`           | variable   | `scale()`(`4`) + `unscaledValue()`(`BigInteger`)            |
| `LocalDate`            | `8`        | `toEpochDay()`(`Long`)                                      |
| `LocalTime`            | `8`        | `toNanoOfDay()`(`Long`)                                     |
| `LocalDateTime`        | `16`       | `toLocalDate()` + `toLocalTime()`                           |
| `OffsetTime`           | `12`       | `toLocalTime()`(`8`) + `getOffset().getTotalSeconds()`(`4`) |
| `OffsetDateTime`       | `20`       | `toLocalDateTime()`(`16`) + offset seconds(`4`)             |
| `Instant`              | `12`       | `getEpochSecond()`(`8`) + `getNano()`(`4`)                  |
| `Year`                 | `4`        | `getValue()`(`Integer`)                                     |
| `java.util.Date`       | `8`        | `getTime()`(`Long`)                                         |
| `java.util.Calendar`   | `8`        | `getTime().getTime()`(`Long`): the instant only, read back in the default time zone — the same value a plain `Calendar` mapping persists ([#11](https://github.com/jinahya/jinahya-persistence/issues/11)) |
| `java.sql.Date`        | `8`        | `getTime()`(`Long`)                                         |
| `java.sql.Time`        | `8`        | `getTime()`(`Long`)                                         |
| `java.sql.Timestamp`   | `12`       | `toInstant()`(`Instant`) ([#80](https://github.com/jinahya/jinahya-persistence/issues/80)) |
| `byte[]`               | `length`   | as is                                                       |
| `Byte[]`               | `length`   | unboxed; use `byte[]` instead                               |
| `char[]`               | `2×length` | each `char` big endian                                      |
| `Character[]`          | `2×length` | unboxed; use `char[]` instead                               |
| `enum`                 | variable   | `name()`(`String`), never the ordinal: reordering constants is safe, renaming one is a breaking change ([#13](https://github.com/jinahya/jinahya-persistence/issues/13)) |
| `java.io.Serializable` | variable   | java serialization, as a plain `Serializable` mapping; not platform-independent; deserialized under the JVM-wide `jdk.serialFilter` ([#8](https://github.com/jinahya/jinahya-persistence/issues/8)) |

`java.sql.Date`, `java.sql.Time` and `java.sql.Timestamp` are matched before `java.util.Date`, so a `Timestamp`
keeps its nanos rather than being truncated to milliseconds.

`java.util.Date`, `java.util.Calendar`, `java.sql.Date`, `java.sql.Time` and `java.sql.Timestamp` are supported but
[deprecated by Jakarta Persistence 3.2](https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#deprecations);
prefer the `java.time` types.
