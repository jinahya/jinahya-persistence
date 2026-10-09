# jinahya-persistence-crypto

![Maven Central Version](https://img.shields.io/maven-central/v/io.github.jinahya/jinahya-persistence-crypto)
[![javadoc](https://javadoc.io/badge2/io.github.jinahya/jinahya-persistence-crypto/javadoc.svg)](https://javadoc.io/doc/io.github.jinahya/jinahya-persistence-crypto)

Application-level encryption (ALE) for Jakarta Persistence entities: selected attributes are encrypted inside the
application, so the database only ever stores their ciphertext. The cryptography and the keys are not this module's
concern; they live behind `EntityEncryptionManager`, which an application implements (with a KMS, Vault, Tink, …).

Each encrypted value has two attributes in the entity: the annotated one holds the plaintext, and a second,
`byte[]`-typed one — a column of the same table — holds the ciphertext.

## Choose your path

Start from one question: **does the column you are encrypting already hold data?**

```
plain entity, column holds data  ──►  Mode A  ──►  Mode B
plain entity, no data yet        ──────────────►  Mode B
```

| mode | the plaintext attribute is | use it when | in one line |
|------|-----------------------------|-------------|-------------|
| **[B](#mode-b-the-steady-state)** | `@Transient` | there is no plaintext data to migrate | behaves like an ordinary field |
| **[A](#mode-a-encrypting-existing-data)** | mapped, to the old column | the column already holds plaintext | migrates rows as they are written, at a cost |

**A is how you get in; B is where you stay.** Both use the same annotation and the same listener; the module tells them
apart by whether the plaintext is `@Transient` ([#82](https://github.com/jinahya/jinahya-persistence/issues/82)). The design behind this, and the schemes considered, are in
[`SCHEMES.adoc`](SCHEMES.adoc).

## Mode B: the steady state

```java
@EncryptedEntity
@EntityListeners(EntityEncryptionListener.class) // registered directly; see "Wiring"
@Entity
class User {

    @EncryptedAttribute // pairs with nameEnc__, by name
    @Transient          // no column: the plaintext lives only in the object
    private String name;

    @Column(name = "name_enc")
    private byte[] nameEnc__;  // the ciphertext; no accessors needed

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
        this.nameEnc__ = null; // required: see below
    }
}
```

It behaves like an ordinary field: `getName()` always returns the value, reading an entity does not write it, the
version does not move, a value can be cleared, and Bean Validation constraints on `name` see the real value.

**The setter has to null the ciphertext.** The persistence provider dirty-checks mapped attributes only; it never
sees a change to a `@Transient` field. Nulling `nameEnc__` is what makes the change visible, so that `@PreUpdate`
runs and re-encrypts. Write this setter by hand — a generated one (Lombok's `@Setter`, an IDE's) is not enough. The
module proves it at startup: it calls the setter on a throwaway instance, and rejects the mapping if the setter is
missing or does not null the ciphertext.

**Change the value through the setter, never the field.** A direct write to `name` is kept only if the instance is
flushed for another reason anyway (another attribute changed); otherwise it is lost. An unchanged value is never
re-encrypted.

**`merge()` does not copy a transient field.** Merging a detached instance whose plaintext changed keeps the managed
instance's old value — Jakarta Persistence merges persistent state only. Set the value on the instance `merge()`
returns:

```java
em.merge(detached).setName(detached.getName());
```

## Mode A: encrypting existing data

```java
@EncryptedEntity
@EntityListeners(EntityEncryptionListener.class)
@Entity
class User {

    @EncryptedAttribute
    @Column(name = "name", insertable = false) // the existing column, kept; nullable and updatable
    private String name;

    @Column(name = "name_enc")
    private byte[] nameEnc__;                  // added
}
```

The existing column doubles as the legacy column, so encryption can be introduced on a table which already holds
plaintext, with no separate tool and no write freeze. Measured on both providers
(`__EncryptionLifecycle_Test.observeLegacyRowMigration`):

| row                                   | on read (`@PostLoad`)                    | on the next write                                          |
|---------------------------------------|------------------------------------------|------------------------------------------------------------|
| legacy: plaintext, no ciphertext      | left alone; the application reads it     | encrypted; the `UPDATE` stores the ciphertext and nulls the plaintext column |
| migrated: no plaintext, ciphertext    | decrypted                                | re-encrypted                                               |

A row which is never written again stays plaintext; migrate the rest with a batch, a chunk at a time:

```java
var entity = em.find(User.class, id); // a legacy row: left as plaintext
service.encrypt(entity);              // now dirty; @PreUpdate then sees it already encrypted
// commit, em.clear(), next chunk
```

This relies on the plaintext column being updatable, which is why that is required ([#73](https://github.com/jinahya/jinahya-persistence/issues/73)). Nulling the column
does not erase the plaintext from backups, logs, replicas or indexes; scrubbing those is the application's.

### What Mode A costs

The plaintext is a *mapped* attribute, and a JPA listener can only keep its value out of the row by nulling the field.
These follow, and cannot be fixed inside the standard callbacks; Mode B has none of them.

* **Reading an entity writes it.** `@PostLoad` runs after the provider has taken its loaded-state snapshot, so moving
  the value between the two mapped attributes leaves the instance dirty from the moment it is read. Measured on both
  providers: a transaction which only reads still issues an `UPDATE`, increments `@Version`, and — encryption being
  randomized — rewrites the ciphertext with a fresh IV. Optimistic locking is unusable, every read shows up in
  replication, CDC and audit, and a read-only connection fails.
* **The instance is emptied by `persist()` and by every flush.** Right after `em.persist(entity)` returns, and after
  every flush, the application's own object reads back `null` for every encrypted attribute.
* **A value cannot be cleared through the plaintext.** With the plaintext `null` and the ciphertext present, encrypting
  reads that as "already encrypted"; clear the ciphertext attribute as well.
* **No Bean Validation constraint** may sit on the plaintext: Jakarta Persistence validates after encrypting has nulled
  it ([#9](https://github.com/jinahya/jinahya-persistence/issues/9)).
* **The insert window.** `@PrePersist` fires when `persist()` is called, not when the `INSERT` is built. The plaintext
  column has to be `insertable = false` (enforced), which guarantees *INSERT confidentiality only*:

  | after `persist()`, the application sets the plaintext again | Hibernate 7.4                            | EclipseLink 5.0                     |
  |-------------------------------------------------------------|------------------------------------------|-------------------------------------|
  | plain `@Column`                                              | not written (an `UPDATE` re-encrypts it) | **written, and left, in the clear** |
  | `@Column(insertable = false)`                                | not written                              | not written                         |

  On EclipseLink the late assignment is *dropped* rather than stored. **Do not modify an encrypted attribute between
  `persist()` and the flush.**

## Switching from A to B

Per entity, when its team is ready — nothing forces it:

1. Let rows migrate as they are written, and run the batch above for the rest.
2. Check that none is left: `service.countUnmigrated(User.class)` counts the rows still holding plaintext and no
   ciphertext. Run it while the entity is still in Mode A.
3. When it returns `0`: replace `@Column(insertable = false)` on the plaintext with `@Transient`, and add the
   invalidating setter. Nothing else changes; no data moves, and the ciphertext column is already full.
4. The old plaintext column is left over: it has to stay nullable (unmapped, it is never written) and lose any `UNIQUE`
   constraint. Drop it whenever convenient.

Mixing the two during a rolling deploy works, and so does rolling back: a row written in either mode reads in the
other (measured on both providers, `__ModeB_Test`). Mode B cannot see a row which still holds plaintext only — which
is why step 2 comes first.

## Wiring

Register `EntityEncryptionListener` itself, the way Spring Data JPA's `AuditingEntityListener` is registered; no
subclass is needed. It encrypts on `@PrePersist` and `@PreUpdate`, before the statement is built, and decrypts on
`@PostLoad` ([#5](https://github.com/jinahya/jinahya-persistence/issues/5)). Its `AbstractEntityEncryptionService` comes from CDI: injected when the persistence provider
creates listeners through a `BeanManager`, looked up from `CDI.current()` otherwise. Provide the service as a CDI bean
(a subclass of `AbstractEntityEncryptionService`, with an `EntityEncryptionManager`).

**Never encrypt from `@PostPersist` or `@PostUpdate`** — the row is already written by the time the post-callbacks
run.

**A subclass has to re-declare every callback it wants.** Neither Hibernate ORM nor EclipseLink invokes a callback
annotation inherited from a listener's superclass (measured on both). A subclass which only overrides
`getEncryptionService()` — to supply the service without CDI, say — encrypts nothing unless it also re-declares
`@PrePersist`, `@PreUpdate` and `@PostLoad` methods which call `super`.

## Constraints

Every mapping is validated in full **before any instance is touched**; under CDI, every entity of the persistence unit
is validated at startup (`AbstractEntityEncryptionService.validateMappings()`, called from `onStartup`), and all
failures are reported at once ([#74](https://github.com/jinahya/jinahya-persistence/issues/74)). Outside a container, call `validateMappings()` yourself. Each encrypt and
decrypt is **all-or-nothing** per instance: every value is computed before any is assigned ([#83](https://github.com/jinahya/jinahya-persistence/issues/83)). A mapping which
breaks any of these is rejected:

| rule | A | B |
|------|:-:|:-:|
| the entity class is annotated `@EncryptedEntity`, directly or by inheritance; a forgotten annotation is rejected, not skipped; an entity with no encrypted attribute passes through untouched ([#72](https://github.com/jinahya/jinahya-persistence/issues/72)) | ✓ | ✓ |
| `@EncryptedAttribute` sits on a persistent attribute (A) or on a `@Transient` field (B); anywhere else — unmapped, a getter the access type does not read — is rejected ([#70](https://github.com/jinahya/jinahya-persistence/issues/70)) | ✓ | ✓ |
| the plaintext is `@Basic`, optional, not of a primitive type, neither an identifier nor a version | ✓ | ✓ |
| the plaintext's declared java type has a codec (see [Encoding](#encoding)); for a member of a generic `@MappedSuperclass`, the type the entity binds it to ([#67](https://github.com/jinahya/jinahya-persistence/issues/67)) | ✓ | ✓ |
| the plaintext column is **non-insertable**, nullable and **updatable** ([#73](https://github.com/jinahya/jinahya-persistence/issues/73)) | ✓ | — (no column) |
| no Bean Validation constraint on the plaintext ([#9](https://github.com/jinahya/jinahya-persistence/issues/9)) | ✓ | — (allowed) |
| a setter which also nulls the ciphertext, proven at startup | — | ✓ |
| the ciphertext attribute exists, is `@Basic`, optional, typed `byte[]`, not an identifier or a version, not itself annotated, paired only once, and its column is insertable, updatable and nullable | ✓ | ✓ |
| an `@Embedded` attribute is not itself annotated; annotate the attributes inside the embeddable, which are descended into | ✓ | ✓ |
| an embeddable reached through an `@ElementCollection` — element or map key — holds no `@EncryptedAttribute` at any depth: collections are not walked ([#71](https://github.com/jinahya/jinahya-persistence/issues/71)) | ✓ | ✓ |

### How the column rules are established (Mode A)

Jakarta Persistence exposes no portable accessor for effective per-column metadata: neither the metamodel,
`PersistenceUnitUtil`, `SchemaManager` nor `EntityManagerFactory.getProperties()` reports whether a column is
insertable. So the module resolves what it can from annotations, and lets an application supply the rest:

```java
protected ColumnRules resolveColumnRules(ManagedType<?> rootType,
                                         List<Attribute<?, ?>> embeddingPath,
                                         Attribute<?, ?> attribute);
```

Each of `insertable`, `updatable` and `nullable` is `YES`, `NO` or `UNKNOWN`.

The default implementation describes the **annotation** mapping. It resolves the applicable
`@AttributeOverride` — searching the root class by the whole dotted path, then each embedding attribute by its own
suffix, outermost winning — and otherwise reads the attribute's own `@Column`, honouring the annotation defaults when
neither is present. Note that an override carries its own `@Column`, with its own defaults, so **an override which
only renames a column restores `insertable = true`** even when the embeddable's own member declares otherwise. The
same embeddable can therefore be safe on one embedding path and unsafe on another, and mappings are validated and
cached per path, not per type.

**If you map in `orm.xml`**, override this method for the paths your configuration covers, state the facts it
establishes, and delegate the rest to `super`. A future provider adapter can implement the same hook against
provider metadata without this module depending on either provider.

`UNKNOWN` is rejected, never assumed safe: a fact that cannot be established is treated exactly like a fact that is
known to be wrong. What the default implementation cannot see is an `orm.xml` mapping that makes an
annotation-safe attribute unsafe — an application using such overrides has to supply a resolver.

## The application's responsibility

This module moves values between the plaintext and the ciphertext; it imposes no policy of its own.

* **The encryption identifier.** `EntityEncryptionManager.getEncryptionIdentifier(Object)` is never stored; it is
  derived again on every encrypt and decrypt, so it has to be the same for the whole lifetime of the row — from an
  unencrypted, never-updated attribute such as a tenant column; never the version, a *current* key id, or an
  `IDENTITY` id, which is still `null` at `@PrePersist` ([#61](https://github.com/jinahya/jinahya-persistence/issues/61)).
* **The cipher suite.** `EntityEncryptionManager` decides the algorithm and the keys. An *authenticated* mode (AES-GCM,
  for example) is recommended: tampering with the stored ciphertext is then detected by the manager, before any byte
  is decoded.
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

* **Querying an encrypted attribute.** The database holds only ciphertext: predicates and orderings against a Mode A
  plaintext column match what is *stored*, which is `NULL`; a Mode B plaintext is not mapped at all, so JPQL naming it
  is rejected.
* **Bulk JPQL `UPDATE`/`DELETE`** bypass entity callbacks entirely and leave managed state unsynchronized.
* **Scalar projections** do not load entities, so nothing decrypts them.
* **Collections and associations.** Only `@Basic` and `@Embedded` are walked; an encrypted embeddable inside an
  `@ElementCollection` is rejected ([#71](https://github.com/jinahya/jinahya-persistence/issues/71)); cascaded entities need their own listener registration.
* **The shared (second-level) cache.** The test persistence units set `shared-cache-mode` to `NONE`; nothing here is
  proven against a cache hit.

## Encoding

The `EntityEncryptionManager` receives, and returns, an array of bytes. Values are turned into those bytes by the
attribute's *declared* java type — for a member inherited from a generic `@MappedSuperclass`, the type the concrete
entity binds it to ([#67](https://github.com/jinahya/jinahya-persistence/issues/67)) — big-endian, and self-contained:
a value is reconstructed from its bytes without consulting the database. The sizes below are of the plaintext
encoding, after the header and before the manager is called. Every encoding is pinned byte for byte by
`EncryptionServiceUtils_Test`.

### Payload header

Every encoded value is prefixed with a two-byte header before it reaches the manager
([#75](https://github.com/jinahya/jinahya-persistence/issues/75)):

| byte | content                                                                                   |
|------|-------------------------------------------------------------------------------------------|
| `0`  | the format version, currently `1`; any other version is rejected                          |
| `1`  | the id of the codec which encoded the value (`EntityEncryptionServiceUtils.Codec`); stored data, never renumbered |

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

## Deprecated for removal

* `@__EncryptionIdentifier`, `EncryptedEntity.encryptionIdentifierAttribute()` and
  `EncryptedEntity.DEFAULT_ENCRYPTION_IDENTIFIER` — never read; the identifier comes only from
  `EntityEncryptionManager.getEncryptionIdentifier(Object)`.
* `EntityEncryptionServiceQualifier` — nothing in this module selects by it.
* `EntityEncryptionListener.onStartup(Startup)` / `onShutdown(Shutdown)` — an entity listener is not a CDI bean, so these
  observers never fire on the instance the persistence provider uses.
