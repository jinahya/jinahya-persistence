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
    @Column(name = "name_enc")      // a binary column; holds the ciphertext only
    private String name;            // holds the plaintext only
}
```

## What it adds

| | the generic module | this module |
|---|---|---|
| the mapping | `@Convert(converter = Encrypted<Type>Converter.class)`, one converter class per type | `@Encrypted`; the codec follows the attribute's declared type, enums and primitives included |
| the service | injected, or `META-INF/services/` | the persistence unit property `jinahya.persistence.encryption.service`: an instance, its class, or its class name; without it, `META-INF/services/` |
| a bad service | fails on the first conversion | fails the session factory |
| the second-level cache | holds the attribute's value | holds the ciphertext: a cache hit decrypts |
| the column | binary, from the converter's type | binary, from the type's `VARBINARY` |
| an in-place change of an array or a `Date` | detected | detected |

Everything else is the generic module's: the rows are the same bytes, so an attribute mapped one way reads what the
other wrote (`EncryptedUserType_Test`, "mixed"), and the encoding table, the service contract and the constraints are
those of [its README](../jinahya-persistence-encryption/README.md).

## How it works

`@Encrypted` composes Hibernate ORM's `@Type` with `EncryptedUserType`. Hibernate ORM creates one instance of that
type per annotated attribute, handing it the attribute and the session factory's settings, from which it takes the
declared java type and the service, and asks the generic module for the converter of that type with that service
(`EncryptedConverters.forType`). The type then binds the ciphertext to a `VARBINARY` column, reads it back, compares
and copies values by content, and gives the second-level cache the ciphertext as the attribute's disassembled form.

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
which names no service fails the session factory.

## Measured

On H2, by the module's tests: every supported type, an enum and a primitive round-trip through `@Encrypted` and every
column is binary; a read never writes; a change, a null, a merge and a bulk JPQL `UPDATE` with a parameter are stored
as ciphertext; a `StatelessSession` stores ciphertext and reads it decrypted; `@DynamicUpdate` keeps an unchanged
ciphertext; Bean Validation sees the plaintext; an array or a `Date` changed in place is stored; the service comes
from the property as an instance or a class name; and with the second-level cache on, a cache hit issues no statement
and decrypts the cached form.

## Not covered

* Bytecode enhancement, versionless optimistic locking (unusable with a randomized ciphertext, as on the generic
  path), and the query cache: not measured.
* A key selected from the entity instance: the service sees bytes only, as everywhere in this project.
