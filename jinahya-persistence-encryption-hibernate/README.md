# jinahya-persistence-encryption-hibernate

[![Maven Central Version](https://img.shields.io/maven-central/v/io.github.jinahya/jinahya-persistence-encryption-hibernate)](https://central.sonatype.com/artifact/io.github.jinahya/jinahya-persistence-encryption-hibernate/versions)
[![javadoc](https://javadoc.io/badge2/io.github.jinahya/jinahya-persistence-encryption-hibernate/javadoc.svg)](https://javadoc.io/doc/io.github.jinahya/jinahya-persistence-encryption-hibernate)

Hibernate ORM's own way into [`jinahya-persistence-encryption`](../jinahya-persistence-encryption/README.md): one
annotation for every supported type, the service from a persistence unit property, and the ciphertext in the
second-level cache. Optional: the generic module is complete on its own, and this one changes nothing about what is
stored.

```java
@Entity
class User {

    @Encrypted
    @Column(name = "name_enc", length = 2048)   // a binary column; holds the ciphertext only
    private String name;                        // holds the plaintext only
}
```

## What it adds

| | the generic module | this module |
|---|---|---|
| the mapping | `@Convert(converter = Encrypted<Type>Converter.class)`, one converter class per type | `@Encrypted`; the codec follows the attribute's declared type, enums and primitives included |
| the service | injected, or `META-INF/services/` | the persistence unit property `jinahya.persistence.encryption.service`: an instance, its class, or its class name; without it, `META-INF/services/`. A class, or a class name, is instantiated once per class in the JVM and shared by every attribute and factory naming it |
| a bad service | fails on the first conversion | fails the session factory |
| the second-level cache | holds the attribute's value | holds the ciphertext: a cache put encrypts, a cache hit decrypts |
| the column | binary, from the converter's type; the declared length is lost on a temporal attribute | binary, from the type's `VARBINARY`; the declared length is applied to every type |
| a column with no declared length | Hibernate ORM's default of `255` | the same: declare one |
| a query literal | converted | converted, through the type's value converter |
| an in-place change of an array or a `Date` | detected | detected |
| an `@Id`, `@Version` or `@NaturalId` attribute | refused by the provider, as the specification says | refused by the type, failing the session factory: a randomized ciphertext matches none of them |

Everything else is the generic module's: the rows are the same bytes, so an attribute mapped one way reads what the
other wrote (`EncryptedUserType_Test`, "mixed"), and the encoding table, the service contract and the constraints are
those of [its README](../jinahya-persistence-encryption/README.md).

## How it works

`@Encrypted` composes Hibernate ORM's `@Type` with `EncryptedUserType`. Hibernate ORM creates one instance of that
type per annotated attribute, handing it the attribute and the session factory's settings, from which it takes the
declared java type and the service, and asks the generic module for the converter of that type with that service
(`EncryptedConverters.forType`). The type exposes that converter as its value converter, so Hibernate ORM binds,
extracts and renders query literals through it, against a `VARBINARY` column; a bulk `UPDATE ... SET e.name =
'literal'` stores the literal's ciphertext, where a type without the converter would have written the literal's own
bytes. The type compares and copies values by content, and gives the second-level cache the ciphertext as the
attribute's disassembled form: a cache put costs an encryption, a cache hit a decryption, and never a statement.

The type refuses an `@Id`, `@EmbeddedId`, `@Version` or `@NaturalId` attribute when the session factory is built:
each is compared with a fresh ciphertext, which never matches (`Refusal_Test`).

## Configuration

```xml
<dependency>
  <groupId>io.github.jinahya</groupId>
  <artifactId>jinahya-persistence-encryption-hibernate</artifactId>
  <version>${version.jinahya-persistence}</version>
</dependency>
```

Hibernate ORM is `provided`: the application brings it. Then, optionally, name the service:

```java
var factory = Persistence.createEntityManagerFactory(
        "unit", Map.of("jinahya.persistence.encryption.service", service));   // or its Class, or class name
```

Without the property the service is loaded from `META-INF/services/`, as for the generic converters. A property
which names no service fails the session factory. A `Byte[]` or `Character[]` attribute needs
`hibernate.type.wrapper_array_handling` set, as on the standard path.

## Measured

On H2, by the module's tests: every supported type, an enum and a primitive round-trip through `@Encrypted`, every
column is binary and has the declared length, a temporal type's included; a read never writes; a change, a null, an
empty value, a merge, and a bulk JPQL `UPDATE` with a parameter or with a literal are stored as ciphertext; a
projection and a `refresh` read the plaintext; a null check is the predicate which works; an attribute of an
embeddable, and one mapped on its getter, are converted; a `StatelessSession` stores ciphertext and reads it
decrypted; `@DynamicUpdate` keeps an unchanged ciphertext; Bean Validation sees the plaintext; an array or a `Date`
changed in place is stored; a value too long for its column fails the write; tampered bytes fail the read; a service
which fails on encrypt fails the flush with nothing written; persistence contexts on several threads share the
converter; the service comes from the property as an instance or a class name, once; an `@Id`, a `@Version` and a
`@NaturalId` are refused; and with the second-level cache on, a cache put encrypts once, and a cache hit issues no
statement and decrypts the cached form.

## Not covered

* Bytecode enhancement, versionless optimistic locking (unusable with a randomized ciphertext, as on the generic
  path), and the query cache: not measured.
* A key selected from the entity instance: the service sees bytes only, as everywhere in this project.
