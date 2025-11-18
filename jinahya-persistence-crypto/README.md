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

## Types

| Type                             | Description                                         | notes                    |
|----------------------------------|-----------------------------------------------------|--------------------------|
| (`byte`, )`java.lang.Byte`       | `1` byte                                            |                          |
| (`short`, )`java.lang.Short`     | `2` bytes in big endian                             |                          |
| (`int`, )`java.lang.Integer`     | `4` bytes in big endian                             |                          |
| (`long`, )`java.lang.Long`       | `8` bytes in big endian                             |                          |
| (`char`, )`java.lang.Character`  | `2` bytes in big endian                             |                          |
| (`float`, )`java.lang.Float`     | `32` bits                                           |                          |
| (`double`, )`java.lang.Double`   | `64` bits                                           |                          |
| (`boolean`, )`java.lang.Boolean` | `1` bytes; `0` for `false`, `1` for `true`          |                          |
| `java.lang.String`               | `utf-8` encoded bytes                               |                          |
| `java.uti.UUID`                  | two `long` bytes of msb and lsb                     |                          |
| `java.math.BigIntger`            | `byte[]` of the value                               |                          |
| `java.math.BigDecimal`           | An encrypted field.                                 |                          |
| `java.time.LocalDate`            | An encrypted field.                                 |                          |
| `java.time.LocalTime`            | An encrypted field.                                 |                          |
| `java.time.LocalDateTime`        | An encrypted field.                                 |                          |
| `java.time.OffsetTime`           | An encrypted field.                                 |                          |
| `java.time.OffsetDateTime`       | An encrypted field.                                 |                          |
| `java.time.Instant`              | An encrypted field.                                 |                          |
| `java.time.Year`                 | An encrypted field.                                 |                          |
| `java.util.Date`                 | An encrypted field.                                 |                          |
| `java.util.Calendar`             | An encrypted field.                                 |                          |
| `java.sql.Date`                  | An encrypted field.                                 |                          |
| `java.sql.Time`                  | An encrypted field.                                 |                          |
| `java.sql.TimeStamp`             | An encrypted field.                                 |                          |
| `byte[]`                         | An encrypted field.                                 |                          |
| `Byte[]`                         | An encrypted field.                                 |                          |
| `char[]`                         | An encrypted field.                                 |                          |
| `Character[]`                    | An encrypted field.                                 |                          |
| `enum`                           | the `name` of the constant using `java.lang.String` |                          |
| `java.io.Serializable`           |                                                     | not platform-independent |
