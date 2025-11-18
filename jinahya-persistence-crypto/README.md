# jinahya-persistence-crypto

![Maven Central Version](https://img.shields.io/maven-central/v/io.github.jinahya/jinahya-persistence-crypto)
[![javadoc](https://javadoc.io/badge2/io.github.jinahya/jinahya-persistence-crypto/javadoc.svg)](https://javadoc.io/doc/io.github.jinahya/jinahya-persistence-mapped)

A module for encrypting/decrypting attributes in application level.

```java

@Entity
class MyEntity {

    @__Encrypted("socialSecurityNumberEnc") // the target attribute name, not column name
    // should be nullable/optional
    @Nullable
    @Basic(optional = true)
    @Column(name = "social_security_number", nullable = true, insertable = true, updatable = false)
    private String socialSecurityNumber;

    // should be nullable/optional
    @Nullable
    @Basic(optional = true)
    @Column(name = "social_security_number_enc", nullable = true, insertable = true, updatable = false)
    private String socialSecurityNumberEnc;
}
```

## Constraints

* Only `@Basic` mappings are supported.
* Source(decrypted|plain) attributes should be `optional`(`nullable`).
* Target(encrypted) attributes should be `optional`(`nullable`).

## Basics

| attribute            | decrypted                                     | notes |
|----------------------|-----------------------------------------------|-------|
| `_int_(int v)`       | `4` bytes of the `v` in big endian byte order |       |
| `_octets_(byte[] v)` | `_int_(v.length)` + octets                    |       |

## Types

* No primitive types are supported.

| attribute                  | decrypted                                           | notes                    |
|----------------------------|-----------------------------------------------------|--------------------------|
| `java.lang.Byte`           | `byte[1]` of the value                              |                          |
| `java.lang.Short`          | `byte[2]` bytes in big endian                       |                          |
| `java.lang.Integer`        | `byte[4]` bytes in big endian                       |                          |
| `java.lang.Long`           | `byte[8]` bytes in big endian                       |                          |
| `java.lang.Character`      | `byte[2]` bytes in big endian                       |                          |
| `java.lang.Float`          | `byte[4]` of the raw `32`-bit int                   |                          |
| `java.lang.Double`         | `byte[8]` of the raw `64`-bit long                  |                          |
| `java.lang.Boolean`        | `byte[1]`, of `0` for `false`, `1` for `true`       |                          |
| `java.lang.String`         | `_octet_(v)`, of the utf-8` encoded bytes           |                          |
| `java.uti.UUID`            | two `Long`s of msb and lsb                          |                          |
| `java.math.BigIntger`      | `_octet_` of the two's complement, big-endian bytes |                          |
| `java.math.BigDecimal`     | An encrypted field.                                 |                          |
| `java.time.LocalDate`      | An encrypted field.                                 |                          |
| `java.time.LocalTime`      | An encrypted field.                                 |                          |
| `java.time.LocalDateTime`  | An encrypted field.                                 |                          |
| `java.time.OffsetTime`     | An encrypted field.                                 |                          |
| `java.time.OffsetDateTime` | An encrypted field.                                 |                          |
| `java.time.Instant`        | An encrypted field.                                 |                          |
| `java.time.Year`           | An encrypted field.                                 |                          |
| `java.util.Date`           | An encrypted field.                                 |                          |
| `java.util.Calendar`       | An encrypted field.                                 |                          |
| `java.sql.Date`            | An encrypted field.                                 |                          |
| `java.sql.Time`            | An encrypted field.                                 |                          |
| `java.sql.TimeStamp`       | An encrypted field.                                 |                          |
| `byte[]`                   | An encrypted field.                                 |                          |
| `Byte[]`                   | An encrypted field.                                 |                          |
| `char[]`                   | An encrypted field.                                 |                          |
| `Character[]`              | An encrypted field.                                 |                          |
| `enum`                     | the `name` of the constant using `java.lang.String` |                          |
| `java.io.Serializable`     |                                                     | not platform-independent |
