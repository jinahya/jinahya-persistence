# jinahya-persistence-encryption-eclipselink

[![Maven Central Version](https://img.shields.io/maven-central/v/io.github.jinahya/jinahya-persistence-encryption-eclipselink)](https://central.sonatype.com/artifact/io.github.jinahya/jinahya-persistence-encryption-eclipselink/versions)
[![javadoc](https://javadoc.io/badge2/io.github.jinahya/jinahya-persistence-encryption-eclipselink/javadoc.svg)](https://javadoc.io/doc/io.github.jinahya/jinahya-persistence-encryption-eclipselink)

EclipseLink's own way into [`jinahya-persistence-encryption`](../jinahya-persistence-encryption/README.md): one named
converter for every supported type, the column made binary by the converter, and the service from a persistence unit
property. Optional: the generic module is complete on its own, and this one changes nothing about what is stored.

```java
@org.eclipse.persistence.annotations.Converter(name = "encrypted", converterClass = EncryptedConverter.class)
@Entity
class User {

    @org.eclipse.persistence.annotations.Convert("encrypted")
    @Column(name = "name_enc")      // a binary column; holds the ciphertext only
    private String name;            // holds the plaintext only
}
```

The name is registered once, on any entity of the persistence unit, and used on each attribute to encrypt.

## What it adds

| | the generic module | this module |
|---|---|---|
| the mapping | `@Convert(converter = Encrypted<Type>Converter.class)`, one converter class per type, each listed in a unit with `exclude-unlisted-classes` | `@Convert("encrypted")`; the codec follows the attribute's declared type, enums and primitives included; nothing to list |
| the column | binary only because each converter declares `byte[]` in its own `implements` clause | binary, set by the converter when the mapping is initialized, so the DDL EclipseLink generates is right |
| the service | injected, or `META-INF/services/` | the persistence unit property `jinahya.persistence.encryption.service`: an instance, its class, or its class name; without it, `META-INF/services/` |
| a bad service | fails on the first conversion | fails on the first use of the unit, before any write |
| an in-place change of an array or a `Date` | not detected | not detected unless the mapping is `@Mutable`; see the rules |

Everything else is the generic module's: the rows are the same bytes, and the encoding table, the service contract
and the constraints are those of [its README](../jinahya-persistence-encryption/README.md).

## How it works

`EncryptedConverter` is an EclipseLink `Converter`, which EclipseLink initializes with the mapping it converts and
the session. From the mapping it takes the attribute's declared java type, from the session the service property,
and from the generic module the converter of that type with that service (`EncryptedConverters.forType`). It sets the
mapping's field type to `byte[]`, so the column is binary, and from then on converts through that converter.

## Configuration

```xml
<dependency>
  <groupId>io.github.jinahya</groupId>
  <artifactId>jinahya-persistence-encryption-eclipselink</artifactId>
  <version>${version.jinahya-persistence}</version>
</dependency>
```

EclipseLink is `provided`: the application brings it. Then, optionally, name the service:

```java
var factory = Persistence.createEntityManagerFactory(
        "unit", Map.of("jinahya.persistence.encryption.service", service));   // or its Class, or class name
```

Without the property the service is loaded from `META-INF/services/`, as for the generic converters.

## Rules

* **Mutable values follow EclipseLink's `@Mutable`.** The converter reports itself immutable, so an array or a
  `java.util.Date` attribute is, unless its mapping is `@Mutable`, treated as a value: an in-place change is not
  detected, and one instance is shared between the shared cache and every persistence context. Declare `@Mutable`
  on a `Date` attribute to get copies and in-place change detection (measured). On an array attribute, `@Mutable`
  detects the in-place change too, but also makes every flush of the entity write it again, a plain read included
  (measured: the version moves on every transaction which loads the entity), because EclipseLink compares a mutable
  attribute with its backup copy by `equals`, identity for an array, and then by converting both sides, and a
  randomized ciphertext never compares equal. For an array, assign a new array instead, and leave the entity out of
  the shared cache where sharing an instance matters.
* **The shared cache holds the plaintext**, as it holds any attribute's value. Leave encrypted entities uncached where
  that matters (`@Cacheable(false)`, or `shared-cache-mode` `NONE`).

## Measured

On H2, by the module's tests: every supported type, an enum and a primitive round-trip through the one named
converter, with no converter listed in the unit, and every column EclipseLink generates is binary; a read never
writes; a change, a null, a merge and a bulk JPQL `UPDATE` with a parameter are stored as ciphertext; an unrelated
change keeps the stored ciphertext; a read-only query is decrypted; Bean Validation sees the plaintext; an array
changed in place is not written, one assigned anew is; a `@Mutable` `Date` changed in place is written, and a read of
it still never writes; and the service comes from the property as an instance or a
class name.

## Not covered

* Weaving with change tracking, and field-based optimistic locking (unusable with a randomized ciphertext, as on the
  generic path): not measured.
* A key selected from the entity instance: the service sees bytes only, as everywhere in this project.
