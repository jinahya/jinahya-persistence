# jinahya-persistence-encryption-eclipselink

[![Maven Central Version](https://img.shields.io/maven-central/v/io.github.jinahya/jinahya-persistence-encryption-eclipselink)](https://central.sonatype.com/artifact/io.github.jinahya/jinahya-persistence-encryption-eclipselink/versions)
[![javadoc](https://javadoc.io/badge2/io.github.jinahya/jinahya-persistence-encryption-eclipselink/javadoc.svg)](https://javadoc.io/doc/io.github.jinahya/jinahya-persistence-encryption-eclipselink)

The [`jinahya-persistence-encryption`](../jinahya-persistence-encryption/README.md) engine on EclipseLink's own
descriptor events, in place of the portable entity listener.

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
plaintext. On EclipseLink the listener also needs CDI to be injected; it cannot be injected under Spring. This module
removes all of that, at the price of depending on EclipseLink's internals.

## What this module changes

| | `EntityEncryptionListener` | this module |
|---|---|---|
| a read | writes the entity back (`UPDATE`, `@Version` + 1, fresh IV) | never writes |
| an unrelated change | re-encrypts every attribute | keeps the stored ciphertext; re-encrypts only a changed plaintext |
| the entity's plaintext before an `UPDATE` | nulled, then restored on `@PostUpdate` | never touched; nulled only in the row |
| Bean Validation | sees the nulled plaintext | sees the plaintext |
| how the service is found | injected into the listener by CDI | a persistence unit property; no container needed |

Everything else is unchanged: the mapping and its [constraints](../jinahya-persistence-encryption/README.md#constraints),
the [encoding](../jinahya-persistence-encryption/README.md#encoding), the service, and the migration of
[existing data](../jinahya-persistence-encryption/README.md#existing-data) on write. Here a legacy row is migrated by
**any** `UPDATE` of its entity, not only by a change to the plaintext.

## How it is implemented

`EntityEncryptionSessionCustomizer` is an EclipseLink `SessionCustomizer`. When the session is built, it reads the
service from the session properties and adds one `DescriptorEventListener`, `EclipseLinkEntityEncryptor`, to every
entity descriptor (aggregate descriptors, the embeddables, are reached through their owners). The engine works on the
unit of work:

| descriptor event | what happens |
|---|---|
| `preInsert` | the ciphertexts are computed into the entity, while the unit of work calculates its changes; the plaintext columns are non-insertable, so the row never carries them |
| `preUpdateWithChanges` (an internal listener) | the plaintexts which changed against the **backup clone** are encrypted into the entity; EclipseLink then calculates the changes again, so the new ciphertexts are written. Registered with `addInternalListener()`, which EclipseLink notifies after every JPA `@PreUpdate` callback of the same event, so a plaintext a callback assigns is encrypted too ([#107](https://github.com/jinahya/jinahya-persistence/issues/107)). That method is marked INTERNAL in EclipseLink; the tests pin its behaviour |
| `aboutToUpdate` | each plaintext column the mapping nulls is set to `NULL` in the row being written. The entity keeps its value |
| `postClone`, `postRefresh` | the working clone is decrypted, and its backup clone, if already taken, given the decrypted plaintexts, so a read is not a change |
| `postMerge` | the same, for a refresh which merged the shared cache's object into the working clone. A merge of a detached instance, or of the clone's changes into the shared cache, is left alone |

An object outside a unit of work, such as the one in the shared cache, is never decrypted: it keeps what the row holds.
So a session event listener, `UndecryptedReadGuard`, also added by the customizer, refuses any query which would
return such objects ([Rules](#rules)).
Both cache modes are covered by `EntityEncryptionSessionCustomizer_Isolated_Test` (`shared-cache-mode` `NONE`) and
`EntityEncryptionSessionCustomizer_Shared_Test` (`ALL`).

## Configuration

Add the module next to EclipseLink, which it expects the application to bring (`provided`):

```xml
<dependency>
  <groupId>io.github.jinahya</groupId>
  <artifactId>jinahya-persistence-encryption-eclipselink</artifactId>
  <version>${version.jinahya-persistence}</version>
</dependency>
```

Then set two persistence unit properties:

| property | value |
|---|---|
| `eclipselink.session.customizer` | `com.github.jinahya.persistence.encryption.eclipselink.EntityEncryptionSessionCustomizer` |
| `jinahya.persistence.encryption.service` (`EntityEncryptionEngine.PROPERTY_ENCRYPTION_SERVICE`) | an `EntityEncryptionService` instance, passed in the properties of the entity manager factory; or its `Class`; or its class name. A class is loaded through the thread's context class loader and created through its public no-arg constructor, so it gets no injection |

```java
var factory = Persistence.createEntityManagerFactory("unit", Map.of(
        "eclipselink.session.customizer", EntityEncryptionSessionCustomizer.class.getName(),
        EntityEncryptionEngine.PROPERTY_ENCRYPTION_SERVICE, service));
```

```xml
<!-- persistence.xml -->
<property name="eclipselink.session.customizer"
          value="com.github.jinahya.persistence.encryption.eclipselink.EntityEncryptionSessionCustomizer"/>
<property name="jinahya.persistence.encryption.service" value="com.example.KmsEncryptionService"/>
```

Once the customizer is named, the service property is required: when it is missing, or names no service, the customizer
throws an `IllegalArgumentException`, and the entity manager factory is not built.

**The service's `getEntityManagerFactory()` is called on the first event**, as the engine finds the encrypted attributes
in that factory's metamodel. Resolve it lazily: the service is handed over while the factory is still being built.

**An application with a customizer of its own** can name only one, and calls this one from it:

```java
public class MySessionCustomizer implements SessionCustomizer {

    @Override
    public void customize(Session session) throws Exception {
        new EntityEncryptionSessionCustomizer().customize(session);
        // ... the application's own customization
    }
}
```

### Rules

* **Do not register `EntityEncryptionListener` as well.** The two are alternatives, not layers.
* **No attribute change tracking.** Weaving may enable it, and the engine fails on the first event of such an entity.
  Disable it with `eclipselink.weaving.changetracking=false`.
* **No reads outside the unit of work.** In a persistence unit with any encrypted entity, a query is refused, with an
  `IllegalStateException`, when it would return objects which are never decrypted. That covers a query (or a `find`)
  which is read-only (`eclipselink.read-only`), doesn't maintain the cache (`eclipselink.maintain-cache=false`) or
  doesn't register its results in the unit of work, and any query in a unit of work with read-only classes, or in a
  unit with a read-only (`@ReadOnly`) or non-registering descriptor. The rule is **unit-wide**: a read-only query of an
  unrelated entity, or of a scalar result, is refused too, as an unencrypted entity may reach an encrypted one through
  a relationship, and a report's result can't be told safe before it is prepared
  ([#103](https://github.com/jinahya/jinahya-persistence/issues/103)). Direct access to the shared cache, and to its
  objects, is not covered.
* **The shared cache holds the plaintext.** It receives the plaintext of a new or changed entity when the transaction
  commits, although the row never does. Where that matters, leave the encrypted entities uncached
  (`@Cacheable(false)`, or `shared-cache-mode` `NONE`).
* **A missing secondary row hides the entity.** EclipseLink does not find an entity whose `@SecondaryTable` row is
  missing, so a ciphertext mapped to a new secondary table needs those rows inserted first.
