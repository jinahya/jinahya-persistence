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

| attribute              | size  | component1                                    | component2                                 | notes                    |
|------------------------|-------|-----------------------------------------------|--------------------------------------------|--------------------------|
| `Byte`                 | `8`   | `byte[1]` of the value                        |                                            |                          |
| `Short`                | `16`  | `byte[2]` bytes in big endian                 |                                            |                          |
| `Integer`              | `16`  | `byte[4]` bytes in big endian                 |                                            |                          |
| `Long`                 | `64`  | `byte[8]` bytes in big endian                 |                                            |                          |
| `Character`            | `16`  | `byte[2]` bytes in big endian                 |                                            |                          |
| `Float`                | `32`  | `byte[4]` of the raw `32`-bit int             |                                            |                          |
| `Double`               | `64`  | `byte[8]` of the raw `64`-bit long            |                                            |                          |
| `Boolean`              | `8`   | `byte[1]`, of `0` for `false`, `1` for `true` |                                            |                          |
| `String`               |       | `byte[]`, of the `utf-8` encoded bytes        |                                            |                          |
| `UUID`                 | `128` | `getMostSignificantBits()`(`Long`)            | `getLeastSignificantBits()`(`Long`)        |                          |
| `BigIntger`            |       | `toByteArray()`(`byte[]`)                     |                                            |                          |
| `BigDecimal`           |       | `toPlainString()(`String`)                    |                                            |                          |
| `LocalDate`            | `64`  | `toEpochDay()`(`Long`)                        |                                            |                          |
| `LocalTime`            | `64`  | `toNanoOfDay()`(`Long`)                       |                                            |                          |
| `LocalDateTime`        | `128` | `toLocalDate()`(`LocalDate`)                  | `toLocalTime()`(`Long`)                    |                          |
| `OffsetTime`           | `96`  | `toLocalDateTime()`(`LocalDateTime`)          | `getOffset().getTotalSeconds()`(`Integer`) |                          |
| `OffsetDateTime`       | `160` | `toLocalDateTime()`(`LocalDateTime`)          | `getOffset().getTotalSeconds()`(`Integer`) |                          |
| `Instant`              | `128` | `toEpochSecond()`(`Long`)                     | `getNanos()`(`Long`)                       |                          |
| `Year`                 | `32`  | `getValue()`(`Integer`)                       |                                            |                          |
| `Date`                 | `64`  | `getTime()`(`Long`)                           |                                            |                          |
| `Calendar`             | `64`  | `getTime()`(`Date`)                           |                                            |                          |
| `java.sql.Date`        |       | `Date`                                        |                                            |                          |
| `java.sql.Time`        |       | `Date`                                        |                                            |                          |
| `java.sql.TimeStamp`   |       | `Date`                                        |                                            |                          |
| `byte[]`               |       | `length`                                      |                                            |                          |
| `Byte[]`               |       | `byte[]`                                      |                                            |                          |
| `char[]`               |       | `length << 1`                                 |                                            |                          |
| `Character[]`          |       | `char[]`                                      |                                            |                          |
| `enum`                 |       | `name()`(`java.lang.String`)                  |                                            |                          |
| `java.io.Serializable` |       |                                               | _                                          | not platform-independent |
