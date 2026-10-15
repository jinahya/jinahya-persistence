# jinahya-persistence-encryption-hibernate

[![Maven Central Version](https://img.shields.io/maven-central/v/io.github.jinahya/jinahya-persistence-encryption-hibernate)](https://central.sonatype.com/artifact/io.github.jinahya/jinahya-persistence-encryption-hibernate/versions)
[![javadoc](https://javadoc.io/badge2/io.github.jinahya/jinahya-persistence-encryption-hibernate/javadoc.svg)](https://javadoc.io/doc/io.github.jinahya/jinahya-persistence-encryption-hibernate)

The [`jinahya-persistence-encryption`](../jinahya-persistence-encryption/README.md) engine on Hibernate ORM's own
event listeners, in place of the portable entity listener.

## The encryption module, in short

[`jinahya-persistence-encryption`](../jinahya-persistence-encryption/README.md) is application-level encryption for
Jakarta Persistence entities. An application pairs each secret attribute (the *plaintext*) with a `byte[]` attribute
annotated `@EncryptedAttribute` (the *ciphertext*). The database stores the ciphertext only. Keys and cryptography stay
in the application's one `EntityEncryptionService`.

```java
@Entity
class User {

    @Column(name = "name", insertable = false) // the plaintext: never carried by an INSERT
    private String name;

    @EncryptedAttribute("name")                // its ciphertext: the column which is stored
    @Column(name = "name_enc")
    private byte[] nameEnc;
}
```

What it costs, whichever way it is wired:

* **Querying stops working.** The database holds ciphertext only, so a predicate or an ordering on an encrypted
  attribute matches what is stored, which is `NULL`.
* **The service is on every load and write.** Each loaded entity is decrypted attribute by attribute, and each written
  one encrypted, after one `init(context)` per instance to select its keys.
* **No type information is stored.** Changing an attribute's java type or encoding breaks the rows already written.
* **The key selection is the application's.** Whatever `init(context)` selects keys by has to stay the same for the
  whole lifetime of the row.

The portable wiring, `@EntityListeners(EntityEncryptionListener.class)`, uses standard callbacks only, and so pays
more ([What it costs](../jinahya-persistence-encryption/README.md#what-it-costs)): a read leaves the entity dirty, so
every read issues an `UPDATE` and bumps `@Version`; every further flush writes again; and Bean Validation sees a nulled
plaintext. This module removes those three, at the price of depending on Hibernate ORM's internals.

## What this module changes

| | `EntityEncryptionListener` | this module |
|---|---|---|
| a read | writes the entity back (`UPDATE`, `@Version` + 1, fresh IV) | never writes |
| an unrelated change | re-encrypts every attribute | keeps the stored ciphertext; re-encrypts only a changed plaintext |
| the entity's plaintext before an `UPDATE` | nulled, then restored on `@PostUpdate` | never touched; cleared only in the row's state |
| a failed flush | leaves the plaintext nulled; reload before retrying | leaves the entity as it was |
| Bean Validation | sees the nulled plaintext | sees the plaintext |
| `EntityEncryptionService.getEntityManagerFactory()` | called on the first callback | never called |

Everything else is unchanged: the mapping and its [constraints](../jinahya-persistence-encryption/README.md#constraints),
the [encoding](../jinahya-persistence-encryption/README.md#encoding), the service, and the migration of
[existing data](../jinahya-persistence-encryption/README.md#existing-data) on write. Here a legacy row is migrated by
**any** write of its entity, not only by a change to the plaintext.

## How it is implemented

`EntityEncryptionIntegrator` is a Hibernate ORM `Integrator`, found through `ServiceLoader`
(`META-INF/services/org.hibernate.integrator.spi.Integrator`). When the session factory is built, it reads the service
from the persistence unit properties and registers one engine, `HibernateEntityEncryptor`, on four events:

| event | what happens |
|---|---|
| `PRE_INSERT` | the ciphertexts are computed into the entity, then copied into the state the `INSERT` is built from |
| `FLUSH_ENTITY` | Hibernate's `DefaultFlushEntityEventListener` is **replaced** by a subclass. Once an entity is found dirty, and after any interceptor and `@PreUpdate` callback, the plaintexts which changed against the snapshot are encrypted; the `UPDATE` state gets the new ciphertexts and the cleared plaintexts, and those indices are marked dirty. `@DynamicUpdate` therefore writes the right columns |
| `POST_UPDATE` | the snapshot, the entry's loaded state, is reset to what the entity holds, so the cleared plaintext does not make it dirty again |
| `POST_LOAD` | the entity is decrypted, and its snapshot reset to the decrypted values, so a read is not a change |

The entity's own plaintext fields are never written to by the engine, so nothing has to be restored after an
`UPDATE`. Only POJO-mapped entities are handled: a dynamic-map entity is skipped. `@DynamicInsert` and `@DynamicUpdate`
are covered by `EntityEncryptionIntegrator_Test`.

## Configuration

Add the module next to Hibernate ORM, which it expects the application to bring (`provided`):

```xml
<dependency>
  <groupId>io.github.jinahya</groupId>
  <artifactId>jinahya-persistence-encryption-hibernate</artifactId>
  <version>${version.jinahya-persistence}</version>
</dependency>
```

Then give the persistence unit the service, under `jinahya.persistence.encryption.service`
(`EntityEncryptionEngine.PROPERTY_ENCRYPTION_SERVICE`). The value is one of:

* an `EntityEncryptionService` instance, passed in the properties of the entity manager factory. Use this one for a
  service which is a container bean.
* its `Class`, or
* its class name. The class is loaded through the thread's context class loader and created through its public no-arg
  constructor, so it gets no injection.

```java
var factory = Persistence.createEntityManagerFactory(
        "unit", Map.of(EntityEncryptionEngine.PROPERTY_ENCRYPTION_SERVICE, service));
```

```xml
<!-- persistence.xml: by class name -->
<property name="jinahya.persistence.encryption.service" value="com.example.KmsEncryptionService"/>
```

**Without the property, the integrator does nothing**: nothing is encrypted or decrypted, so an entity mapped with
`@EncryptedAttribute` stores neither its plaintext, which is non-insertable, nor a ciphertext. It is logged at `DEBUG` only. A property which names no service fails
the session factory, not its first write.

Under Spring Boot, put the bean into the JPA properties, for example from a `HibernatePropertiesCustomizer`. This is not
tested here:

```java
@Bean
HibernatePropertiesCustomizer entityEncryption(EntityEncryptionService service) {
    return properties -> properties.put(EntityEncryptionEngine.PROPERTY_ENCRYPTION_SERVICE, service);
}
```

As `getEntityManagerFactory()` is never called, the service needs no lazy reference to the factory, and there is no
cycle between the service bean and the factory bean.

### Rules

* **Do not register `EntityEncryptionListener` as well.** The two are alternatives, not layers.
* **No other `FLUSH_ENTITY` listener.** The integrator replaces Hibernate's default one, and fails the session factory
  if another listener has been registered there.
* **The second-level cache holds the plaintext.** The cache entry written by an `INSERT` carries it, although the row
  never does. Where that matters, leave the encrypted entities uncached.
* **Bytecode enhancement with dirty tracking is not verified.**
