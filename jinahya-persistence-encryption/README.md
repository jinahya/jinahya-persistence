# jinahya-persistence-encryption

[![Maven Central Version](https://img.shields.io/maven-central/v/io.github.jinahya/jinahya-persistence-encryption)](https://central.sonatype.com/artifact/io.github.jinahya/jinahya-persistence-encryption/versions)
[![javadoc](https://javadoc.io/badge2/io.github.jinahya/jinahya-persistence-encryption/javadoc.svg)](https://javadoc.io/doc/io.github.jinahya/jinahya-persistence-encryption)

Application-level encryption (ALE) for Jakarta Persistence entities: the application holds the plaintext, the
database holds the ciphertext, and the two meet in a standard `AttributeConverter`. The cryptography and the keys are
not this module's concern; they live behind `EntityEncryptionService`, which an application implements once (with a
KMS, Vault, Tink, …).

## How it works

### The pieces

| type | what it is | who writes it |
|------|------------|---------------|
| `Encrypted<Type>Converter` | an attribute converter per supported java type; named on each attribute to encrypt | the module |
| `EntityEncryptionService` | the cryptography: bytes in, bytes out | the application, once |

That is the whole surface. There is no listener, no callback, and no provider-specific code. The encoding of each
type into bytes is fixed and pinned by tests, but it is not an API: nothing outside the converters calls it.

### A complete example

```java
@Entity
class User {

    @Convert(converter = EncryptedStringConverter.class)
    @Column(name = "name_enc", length = 2048)   // a binary column; holds the ciphertext only, as long as the
    private String name;                        // service makes it: declare a length (see the constraints)
}

class KmsEncryptionService implements EntityEncryptionService {
    public byte[] encrypt(byte[] decryptedBytes) { ... }
    public byte[] decrypt(byte[] encryptedBytes) { ... }
}
```

```
META-INF/services/com.github.jinahya.persistence.encryption.EntityEncryptionService
    com.example.KmsEncryptionService
```

### What happens

```
INSERT / UPDATE   user.name ──codec──► bytes ──service.encrypt──► name_enc
SELECT            name_enc ──service.decrypt──► bytes ──codec──► user.name
```

Going to the column, the converter encodes the value to bytes by its java type ([Encoding](#encoding)), hands them
to the service, and what the service returns is what the column stores. Coming back, the stored bytes go to the
service, and the result is decoded by the same codec. `null` is `null` in both directions, and the service is not
called for it.

The persistence provider does everything else, as for any converted attribute:

* **The attribute is ordinary.** It is dirty-checked, merged, validated and cached as its plaintext value. A read
  never writes, nothing is ever nulled or restored in the entity, and Bean Validation sees the real value.
* **The column is binary**, whatever the java type. The database knows nothing about what it holds.
* **Nothing in the lifecycle is involved.** No callback, no ordering rule, no insert window: the conversion happens
  where the provider binds the column, so it also covers a `StatelessSession`, a bulk JPQL `UPDATE` with a parameter,
  and a read-only query.

A projection of the attribute, and a `refresh`, read the plaintext; a bulk JPQL `UPDATE` with a parameter or a
literal stores ciphertext; an attribute of an embeddable, or one mapped on its getter, is converted like any other;
and persistence contexts on several threads share the one converter. Measured on both providers by
`EncryptionLifecycle_Test`, `AllTypes_Test` and `Injection_Test`.

## The service

```java
public interface EntityEncryptionService {
    byte[] encrypt(byte[] decryptedBytes);
    byte[] decrypt(byte[] encryptedBytes);
}
```

* **It sees bytes only.** It is not told which entity, attribute or row a value belongs to. Which key a value was
  encrypted with is the service's to record, inside the bytes it returns (a key id in its own frame); which key to
  use for a new value, a tenant's for instance, comes from the application's own context.
* **An authenticated mode** (AES-GCM, for example) is recommended: tampering with the stored bytes is then detected
  by the service, before any byte is decoded.
* **It has to be thread-safe.** One instance serves every conversion of every persistence context. A `Cipher` is not
  thread-safe: one per call, or a pool, never a field.
* **Every array crossing the boundary is a copy.** The service may keep what it is given; what it returns is copied
  before it is stored or decoded. An empty value is encrypted like any other: the service is given an empty array.
* **What it throws passes through.** A service which fails on encrypt fails the flush, and nothing is written; one
  which fails on decrypt, on tampered bytes for instance, fails the read instead of yielding a value. A service which
  returns `null`, and a value which cannot be encoded or decoded, are a `PersistenceException` whose message never
  holds the value.
* **It is not told where the bytes belong**, so it cannot bind them there. See the constraints: a ciphertext moved to
  another row or column decrypts as well as where it was written.

### How the converters find it

Provide the service once, in one of two ways:

| way | when |
|---|---|
| **as a bean**, injected into the converters | when the persistence provider creates its converters through a bean container: a Jakarta EE container, or a CDI `BeanManager` passed as `jakarta.persistence.bean.manager` (measured on both providers: `Injection_Test`), or Spring Boot with Hibernate ORM (not tested here) |
| **through `ServiceLoader`**, from `META-INF/services/` | otherwise; the implementation needs a public no-arg constructor, and exactly one has to be named |

A converter looks for an injected service first, and loads one on its first conversion otherwise. With neither, the
first conversion fails with an `IllegalStateException`; so does more than one implementation named
(`EntityEncryptionServices_Test`). The loaded service is **one per JVM**: every converter without an injected service
shares it, found through the context class loader of the first conversion. Two persistence units in one JVM which
need two services inject them, or use a provider module, whose persistence unit property names the service per unit.

### Listing the converters

A converter class is a managed class of the persistence unit. In a unit which sets `exclude-unlisted-classes`, list
each converter the entities use, as the specification asks; EclipseLink fails the unit otherwise, where Hibernate ORM
finds the class on its own:

```xml
<class>com.github.jinahya.persistence.encryption.converter.EncryptedStringConverter</class>
```

## Existing data

A table which already holds plaintext is migrated row by row, with no separate tool. Keep the old column mapped for
the duration, read-only on insert, and move its value across when a row is loaded:

```java
@Convert(converter = EncryptedStringConverter.class)
@Column(name = "name_enc")
private String name;

@Column(name = "name", insertable = false)     // the old column, during migration only
private String legacyName;

@PostLoad
void migrate() {
    if (name == null && legacyName != null) {
        name = legacyName;                      // the entity is now dirty: the next flush stores the
        legacyName = null;                      // ciphertext, and nulls the old column
    }
}
```

A legacy row is migrated by the first transaction which loads it, including one which only reads, so during the
migration phase a read of a legacy row writes once. Rows never read are migrated by a chunked loop which loads them
and commits; no module internals are needed. Once every row has its ciphertext, delete the attribute, the callback,
and the column. Measured on both providers (`EncryptionLifecycle_Test`, "legacy rows").

Nulling the column does not erase the plaintext from backups, logs, replicas or indexes; scrubbing those is the
application's.

## Constraints and costs

* **Querying an encrypted attribute matches nothing.** The database holds ciphertext, and a randomized cipher never
  produces the same bytes twice, so a predicate or an ordering on the attribute compares against what is stored and
  finds nothing, silently. Ordering, ranges and date arithmetic are gone with it. The one predicate which works is a
  null check: `IS NULL` and `IS NOT NULL` see the column, which is `NULL` exactly when the attribute is.
* **The bytes are not bound to their row.** The service sees bytes only, with no associated data naming the entity,
  attribute or row, so a ciphertext copied into another row, or another column of the same type, decrypts there as
  well as where it was written; an authenticated mode detects a changed ciphertext, not a moved one. Where that
  matters, the service frames each value with what it must stay bound to, from the application's own context.
* **The length of the ciphertext follows the length of the plaintext**, by whatever the cipher adds; a `String`'s
  byte length is readable from its column. Padding, where the length is a secret, is the service's.
* **The column needs a length, and gets what the provider makes of it.** A ciphertext is as long as the service's
  frame makes it, a KMS envelope for one, so declare `@Column(length = ...)` on every encrypted attribute. Without
  one, Hibernate ORM gives the column its default length of `255` bytes, and a longer ciphertext fails the write. On
  Hibernate ORM the declared length reaches a converted column of every type **but a temporal one**: an attribute
  of a `java.time`, `java.util.Date` or `java.sql` type keeps the default `255` whatever its `length` says, so widen
  such a column with `columnDefinition`, or map it through the Hibernate module, whose type applies the length to
  every type. EclipseLink types the column by the converter's `byte[]`, `LONGVARBINARY` on H2, and applies no length
  at all. A value which does not fit fails the write, on neither provider is anything truncated
  (`AllTypes_Test.columnLengths`, `tooLong`).
* **No type information is stored.** A value is decoded by the converter the attribute names *now*. Changing an
  attribute's declared type, or its converter, is a breaking change for the rows already written: decrypt them with
  the old converter and re-encrypt with the new.
* **An unrelated change may encrypt the value again.** EclipseLink writes changed columns only, and keeps the stored
  ciphertext. Hibernate ORM writes every column of an `UPDATE` unless the entity is `@DynamicUpdate`, so it encrypts
  the unchanged plaintext again, with a fresh IV (`HibernateDynamic_Test`, under the Hibernate profile). Harmless,
  but a write.
* **Versionless optimistic locking is unusable** on an encrypted attribute, for the same reason as querying: the
  `WHERE` would compare a fresh ciphertext with the stored one. Use a `@Version` attribute. Not measured here.
* **The provider's shared cache holds the attribute's value**, the plaintext, as for any converted attribute (what
  Hibernate ORM caches depends on the type's mutability, but is never the ciphertext). The test persistence units set
  `shared-cache-mode` to `NONE`; leave encrypted entities uncached where that matters, or use the Hibernate module,
  whose type caches the ciphertext.
* **Bytecode enhancement and weaving are not measured.** The converter sits where the provider binds the column, not
  in the entity, so there is no reason it would differ; it has not been run.
* **A primitive attribute** (`int`, `boolean`, …) is converted as its wrapper, as the specification requires, and
  reads `NULL` as whatever the provider does for a primitive; prefer the wrapper.

## Encoding

The service receives, and returns, an array of bytes. A value is turned into those bytes by the converter's java
type, big-endian, and self-contained: a value is reconstructed from its bytes without consulting the database. The
sizes below are of the encoding, before the service is called. Every encoding is pinned byte for byte by the
`ValueCodecs_Of*_Test` classes, and never changes once released. A fixed-width codec decodes an array of exactly its
width and rejects any other: a value stored under one type is never read as a prefix of another, silently. The codecs
themselves are package-private: the converters are the API. A type the module has no converter for is brought by
extending `EncryptedAttributeConverter` with an encoder and a decoder of one's own, whose bytes are then one's own to
keep stable.

| converter | attribute | bytes | encoding |
|---|---|---|---|
| `EncryptedBooleanConverter` | `Boolean` | `1` | `0` for `false`, `1` for `true`; read as `false` for `0`, `true` for any other byte |
| `EncryptedByteConverter` | `Byte` | `1` | the value |
| `EncryptedShortConverter` | `Short` | `2` | big endian |
| `EncryptedCharacterConverter` | `Character` | `2` | big endian |
| `EncryptedIntegerConverter` | `Integer` | `4` | big endian |
| `EncryptedFloatConverter` | `Float` | `4` | the raw `32`-bit int |
| `EncryptedLongConverter` | `Long` | `8` | big endian |
| `EncryptedDoubleConverter` | `Double` | `8` | the raw `64`-bit long |
| `EncryptedUuidConverter` | `UUID` | `16` | `getMostSignificantBits()` + `getLeastSignificantBits()` |
| `EncryptedStringConverter` | `String` | variable | strict `utf-8`: an unpaired surrogate is rejected on encrypt, malformed `utf-8` on decrypt |
| `EncryptedBigIntegerConverter` | `BigInteger` | variable | `toByteArray()` |
| `EncryptedBigDecimalConverter` | `BigDecimal` | variable | `scale()`(`4`) + `unscaledValue()`(`BigInteger`) |
| `EncryptedLocalDateConverter` | `LocalDate` | `8` | `toEpochDay()`(`Long`) |
| `EncryptedLocalTimeConverter` | `LocalTime` | `8` | `toNanoOfDay()`(`Long`) |
| `EncryptedLocalDateTimeConverter` | `LocalDateTime` | `16` | `toLocalDate()` + `toLocalTime()` |
| `EncryptedOffsetTimeConverter` | `OffsetTime` | `12` | `toLocalTime()`(`8`) + `getOffset().getTotalSeconds()`(`4`) |
| `EncryptedOffsetDateTimeConverter` | `OffsetDateTime` | `20` | `toLocalDateTime()`(`16`) + offset seconds(`4`) |
| `EncryptedInstantConverter` | `Instant` | `12` | `getEpochSecond()`(`8`) + `getNano()`(`4`) |
| `EncryptedYearConverter` | `Year` | `4` | `getValue()`(`Integer`) |
| `EncryptedUtilDateConverter` | `java.util.Date` | `8` | `getTime()`(`Long`); for an attribute declared exactly `java.util.Date`, not a subclass |
| `EncryptedSqlDateConverter` | `java.sql.Date` | `8` | `getTime()`(`Long`) |
| `EncryptedSqlTimeConverter` | `java.sql.Time` | `8` | `getTime()`(`Long`) |
| `EncryptedSqlTimestampConverter` | `java.sql.Timestamp` | `12` | `toInstant()`(`Instant`); the nanos are kept |
| `EncryptedBytesConverter` | `byte[]` | `length` | as is |
| `EncryptedBytesWrappedConverter` | `Byte[]` | `length` | unboxed; a `null` element is rejected |
| `EncryptedCharsConverter` | `char[]` | `2×length` | each `char` big endian |
| `EncryptedCharsWrappedConverter` | `Character[]` | `2×length` | unboxed; a `null` element is rejected |
| a subclass of `EncryptedEnumConverter<E>` | `enum` | variable | `name()`(`String`), never the ordinal: reordering constants is safe, renaming one is a breaking change |

An `enum` attribute needs a converter of its own, as a converter has to name its type. Declare the interface again,
with the enum type: EclipseLink reads a converter's type arguments from the class's own interface clause only, and
otherwise derives the column from the attribute's type, a `VARCHAR` for an enum, and fails to convert the ciphertext
to it ([#58](https://github.com/jinahya/jinahya-persistence/issues/58) is the same behaviour in `-more`):

```java
@Converter
public class EncryptedColorConverter extends EncryptedEnumConverter<Color>
        implements AttributeConverter<Color, byte[]> {
    public EncryptedColorConverter() { super(Color.class); }
}
```

`java.util.Date`, the `java.sql` temporal types, `Byte[]` and `Character[]` are supported but
[deprecated by Jakarta Persistence 3.2](https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#deprecations);
prefer the `java.time` types, `byte[]` and `char[]`. Hibernate ORM rejects a `Byte[]` or `Character[]` attribute
unless `hibernate.type.wrapper_array_handling` is set (`ALLOW`, or `LEGACY`).

`java.util.Calendar`, `java.io.Serializable` and arrays of other types have no converter: none has a clean, secure
encoding ([#99](https://github.com/jinahya/jinahya-persistence/issues/99)). Map the value's parts to supported types.

## Provider modules

None is required: the converter is standard Jakarta Persistence, and both providers are measured. Each provider has an
optional module on top, built on this one through `EncryptedConverters.forType(type, service)`, the one hook exposed
for them, so the bytes are the same and an attribute mapped either way reads what the other wrote:

| provider | module | adds |
|---|---|---|
| Hibernate ORM | [`jinahya-persistence-encryption-hibernate`](../jinahya-persistence-encryption-hibernate/README.md) | `@Encrypted`, one annotation for every type; the service from a persistence unit property; the ciphertext in the second-level cache |
| EclipseLink | [`jinahya-persistence-encryption-eclipselink`](../jinahya-persistence-encryption-eclipselink/README.md) | `@Convert("encrypted")`, one named converter for every type, nothing listed; binary DDL; the service from a persistence unit property |

What they keep, what was decided against and what stays open is in [`VENDORS.adoc`](VENDORS.adoc).

The scenarios all three modules are tested on, which test covers each in which module, and the gaps, are in
[`docs/TESTCASES.adoc`](docs/TESTCASES.adoc).

## Design notes

The module reached this shape after a listener-based design, which moved the value between a plaintext attribute and
a ciphertext attribute inside entity callbacks. Its working notes are kept as history: [`SCHEMES.adoc`](SCHEMES.adoc),
[`WIRING.adoc`](WIRING.adoc), [`_SURVEY.adoc`](_SURVEY.adoc), [`_NOTE.md`](_NOTE.md) and [`_CRYPTO.md`](_CRYPTO.md).
