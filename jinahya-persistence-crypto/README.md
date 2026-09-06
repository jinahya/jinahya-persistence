# jinahya-persistence-crypto

![Maven Central Version](https://img.shields.io/maven-central/v/io.github.jinahya/jinahya-persistence-crypto)
[![javadoc](https://javadoc.io/badge2/io.github.jinahya/jinahya-persistence-crypto/javadoc.svg)](https://javadoc.io/doc/io.github.jinahya/jinahya-persistence-crypto)

A module for encrypting/decrypting attributes in application level.

An entity carries the value twice: the annotated attribute holds the plaintext and is nulled before the row is
written, and a second, `byte[]`-typed attribute of the same entity holds the ciphertext.

```java

@__EncryptedEntity
@Entity
@EntityListeners(MyEncryptionListener.class)
class MyEntity {

    // the plaintext; nulled by encrypt(), restored by decrypt().
    // insertable = false is required - see "Declare the plaintext column insertable = false" below
    @__EncryptedAttribute // defaults to the attribute below, by name
    @Nullable
    @Basic(optional = true)
    @Column(name = "social_security_number", nullable = true, insertable = false)
    private String socialSecurityNumber;

    // the ciphertext; the attribute name, not the column name, is what the annotation refers to
    @Nullable
    @Basic(optional = true)
    @Column(name = "social_security_number_enc", nullable = true)
    private byte[] socialSecurityNumberEnc__;
}
```

`@__EncryptedAttribute` pairs the two by attribute name: an empty `encryptedAttribute()`, the default, means the
annotated attribute's name suffixed with `Enc__`. Name the counterpart explicitly to use anything else.

## Constraints

Every annotated attribute of a managed type is validated once, in full, **before any instance is touched**, so an
inconsistent mapping cannot leave an instance half-encrypted. A mapping which breaks any of these is rejected:

* Only `@Basic` mappings are supported; `@Embedded` attributes are descended into. An annotated attribute which is
  neither is an error, not something skipped.
* Source (decrypted, plain) attributes must be `optional`, must not be of a primitive type, and their column must be
  **non-insertable** and nullable.
* Target (encrypted) attributes must be `optional`, `@Basic`, typed `byte[]`, paired only once, and their column must
  be insertable, updatable and nullable.
* An `@Embedded` attribute may not itself be annotated; annotate the attributes inside the embeddable.
* Neither side may be an identifier or a version attribute, and the ciphertext attribute may not itself be annotated.
* An entity class must be annotated with `@__EncryptedEntity`; anything else passes through untouched.

### How the column rules are established

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

## Not supported

* **Bulk JPQL `UPDATE`/`DELETE`** bypass entity callbacks entirely and leave managed state unsynchronized. Do not
  assign an encrypted attribute in bulk DML; predicates and orderings against a plaintext column match what is
  *stored*, which is `NULL`.
* **Scalar projections** do not load entities, so nothing decrypts them.
* **Collections and associations.** Only `@Basic` and `@Embedded` are walked; an encrypted embeddable inside an
  `@ElementCollection` is not covered, and cascaded entities need their own listener registration.
* **The shared (second-level) cache.** The test persistence units set `shared-cache-mode` to `NONE`; nothing here is
  proven against a cache hit.
* **Setting an already-encrypted attribute to `null`.** With the plaintext `null` and the ciphertext present,
  `encrypt()` reads that as "already encrypted" and leaves it alone, so an erasure cannot be expressed that way.
  Clear the ciphertext attribute as well.

## Wiring

`__EncryptionListener` is an abstract entity listener whose callbacks only log. A subclass supplies an
`__EncryptionService` and calls `encrypt(Object)` / `decrypt(Object)` from the callbacks it needs.

**Encrypt from `@PrePersist` and `@PreUpdate`, never from `@PostPersist` or `@PostUpdate`** — the row is already
written by the time the post-callbacks run, so encrypting there stores the plaintext. Decrypt from `@PostLoad`.

That is necessary but not sufficient. `__EncryptionLifecycle_Test` measures what the providers actually do, and two
things do not follow from the callbacks alone.

### Declare the plaintext column `insertable = false` (enforced)

`@PrePersist` fires when `persist()` is called, **not** when the `INSERT` is built, and Jakarta Persistence has no
callback in between. Anything assigned to an encrypted attribute after `persist()` is therefore unencrypted when the
statement is built:

| after `persist()`, the application sets the plaintext again | Hibernate 7.4                            | EclipseLink 5.0                     |
|-------------------------------------------------------------|------------------------------------------|-------------------------------------|
| plain `@Column`                                              | not written (an `UPDATE` re-encrypts it) | **written, and left, in the clear** |
| `@Column(insertable = false)`                                | not written                              | not written                         |

Declaring the plaintext column `insertable = false` closes it on every provider, and the mapping is now rejected
without it. That guarantees **INSERT confidentiality only.** On EclipseLink the late assignment is then *dropped*
rather than stored — only one `INSERT` is issued, so no `@PreUpdate` runs to re-encrypt it, and the row keeps the
ciphertext of the earlier value. **Do not modify an encrypted attribute between `persist()` and the flush.**

### Reading an entity writes it

`@PostLoad` runs after the provider has taken its loaded-state snapshot, so moving the value between the two *mapped*
attributes leaves the instance dirty from the moment it is read. Measured on both providers: a transaction which only
reads still issues an `UPDATE`, increments `@Version`, and — encryption being randomized — rewrites the ciphertext
with a fresh IV.

Consequences: optimistic locking is unusable, every read shows up in replication, CDC and audit, and a read-only
connection fails. There is no fix inside the standard callbacks; the providers' own pre-statement hooks
(`org.hibernate.event.spi.PreLoadEventListener`, EclipseLink `DescriptorEventListener.postBuild`) operate on the row
before the snapshot and do not have this problem.

### The instance is emptied by the flush

Encrypting nulls the plaintext attribute, so after any flush the application's own object reads back `null` for every
encrypted attribute. Decrypting again from `@PostPersist`/`@PostUpdate` restores it, at the cost of dirtying the
instance once more.

## Encoding

The `__EncryptionManager` receives, and returns, an array of bytes. Values are turned into those bytes by java type,
big-endian, and are self-contained — a value is reconstructed from its bytes without consulting the database. The
sizes below are of the plaintext encoding, before the manager is called.

| attribute              | bytes      | encoding                                                    |
|------------------------|------------|-------------------------------------------------------------|
| `Boolean`              | `1`        | `0` for `false`, `1` for `true`                             |
| `Byte`                 | `1`        | the value                                                   |
| `Short`                | `2`        | big endian                                                  |
| `Character`            | `2`        | big endian                                                  |
| `Integer`              | `4`        | big endian                                                  |
| `Float`                | `4`        | the raw `32`-bit int                                        |
| `Long`                 | `8`        | big endian                                                  |
| `Double`               | `8`        | the raw `64`-bit long                                       |
| `UUID`                 | `16`       | `getMostSignificantBits()` + `getLeastSignificantBits()`    |
| `String`               | variable   | the `utf-8` encoded bytes                                   |
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
| `java.util.Calendar`   | `8`        | `getTime().getTime()`(`Long`)                               |
| `java.sql.Date`        | `8`        | `getTime()`(`Long`)                                         |
| `java.sql.Time`        | `8`        | `getTime()`(`Long`)                                         |
| `java.sql.Timestamp`   | `16`       | `toInstant()`(`Instant`, `12`) + `4` unused                  |
| `byte[]`               | `length`   | as is                                                       |
| `Byte[]`               | `length`   | unboxed; use `byte[]` instead                               |
| `char[]`               | `2×length` | each `char` big endian                                      |
| `Character[]`          | `2×length` | unboxed; use `char[]` instead                               |
| `enum`                 | variable   | `name()`(`String`)                                          |
| `java.io.Serializable` | variable   | java serialization; not platform-independent                |

`java.sql.Date`, `java.sql.Time` and `java.sql.Timestamp` are matched before `java.util.Date`, so a `Timestamp`
keeps its nanos rather than being truncated to milliseconds.

`java.util.Date`, `java.util.Calendar`, `java.sql.Date`, `java.sql.Time` and `java.sql.Timestamp` are supported but
[deprecated by Jakarta Persistence 3.2](https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#deprecations);
prefer the `java.time` types.

## Not implemented

* `__SecureAttributeConveter` — the single-attribute, single-column alternative. Its conversion methods throw
  `UnsupportedOperationException`.
* `@__EncryptionIdentifier` and `__EncryptedEntity.encryptionIdentifierAttribute()` — the identifier comes only from
  `__EncryptionManager.getEncryptionIdentifier(Object)`.
