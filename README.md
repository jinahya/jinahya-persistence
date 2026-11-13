# jinahya-persistence

[![Java CI with Maven](https://github.com/jinahya/jinahya-persistence/actions/workflows/maven.yml/badge.svg)](https://github.com/jinahya/jinahya-persistence/actions/workflows/maven.yml)
[![Quality gate status](https://sonarcloud.io/api/project_badges/measure?project=jinahya_jinahya-persistence&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=jinahya_jinahya-persistence)

[![Maven Central Version](https://img.shields.io/maven-central/v/io.github.jinahya/jinahya-persistence)](https://central.sonatype.com/artifact/io.github.jinahya/jinahya-persistence/versions)
[![javadoc](https://javadoc.io/badge2/io.github.jinahya/jinahya-persistence/javadoc.svg)](https://javadoc.io/doc/io.github.jinahya/jinahya-persistence)


## Artifacts

Every artifact carries its Jakarta EE generation in its artifactId; use the ones of the generation you run on, and
never two generations together, since they share their packages. Up to 0.6.x the artifactIds had no suffix
(`jinahya-persistence-utils`, ...); see [Upgrading to 0.7.0](#upgrading-to-070).

| Artifact | Jakarta EE 11 |
| --- | --- |
| Utilities | `io.github.jinahya:jinahya-persistence-utils-11` |
| More | `io.github.jinahya:jinahya-persistence-more-11` |
| More, test support | `io.github.jinahya:jinahya-persistence-more-test-11` |
| Test utilities | `io.github.jinahya:jinahya-persistence-test-utils-11` |
| Encryption | `io.github.jinahya:jinahya-persistence-encryption-11` |
| Encryption, Hibernate ORM | `io.github.jinahya:jinahya-persistence-encryption-hibernate-11` |
| Encryption, EclipseLink | `io.github.jinahya:jinahya-persistence-encryption-eclipselink-11` |

The Jakarta APIs are `provided`: your platform, or your own dependency on a provider, supplies them.

## Layout

```
pom.xml                                   plugins, and the versions not tied to a generation
shared/<module>/src/...                   the sources every generation shares
  src/test/java-<persistence-provider>/   the tests of one provider only (hibernate-orm, eclipselink)
src/test-shared/java/                     the test sources every module shares (__PersistenceProviders)
jinahya-persistence-NN/pom.xml            Jakarta EE NN: the platform BOM, the implementations, Java SE, the profiles
  jinahya-persistence-<module>-NN/        the sources only Jakarta EE NN needs, if any
```

Each `jinahya-persistence-NN/*` module compiles `shared/<module>/src/...` against its own generation's API, together
with its own `src/`, which holds only what differs. Resources are taken from both places, too.

## Jakarta EE alignment

Every version tied to a generation is set in that generation's parent, `jinahya-persistence-NN/pom.xml`, and nowhere
else: the Jakarta EE BOM (and, through it, every `jakarta.*` API), `persistence.version`, the minimum Java SE, and
every implementation of a Jakarta specification. Each implementation is kept on the newest release of the series
aligned with its generation. The root `pom.xml` holds only what is the same for every generation (JUnit, Mockito,
AssertJ, Lombok, SLF4J, H2, the randomizer engines, the plugins).

Each generation documents its environment and its version alignment in its own `README.md`; update it whenever its
`pom.xml` changes.

| Jakarta EE | Module | Jakarta Persistence | Java SE, minimum | Status |
| --- | --- | --- | --- | --- |
| 10 | none | 3.1 | 11 | dropped; see [below](#jakarta-ee-10) |
| 11 | [`jinahya-persistence-11`](jinahya-persistence-11/README.md) | 3.2 | 17 | current |
| 12 | none yet | 4.0 | | not released; see [below](#jakarta-ee-12) |

### Profiles

The generation is a module, not a profile, so a build always builds every generation. The one profile axis is the
persistence provider, declared by each `jinahya-persistence-NN` parent:

| Profile | Provider |
| --- | --- |
| `hibernate-orm` | Hibernate ORM — **active by default** |
| `eclipselink` | EclipseLink |
| `eclipselink-weaving` | an add-on to `eclipselink`: the EclipseLink agent weaves the entities as they load |

`./_mvn.sh [maven args...]` runs the given build once per provider, through `_mvn_hibernate_orm.sh` and
`_mvn_eclipselink.sh`, and reports which ones failed — e.g. `./_mvn.sh test` or
`./_mvn.sh -q enforcer:enforce -Drules=dependencyConvergence`.

### Java versions

* **Main classes:** each generation compiles them for its Jakarta EE generation's minimum Java SE
  (`maven.compiler.source` in `jinahya-persistence-NN/pom.xml`), and the enforcer requires a JDK at least that new.
* **What the main classes need at runtime** (every dependency outside the `test` scope, `provided` included) must run
  on that minimum, too; each generation enforces it with `enforceBytecodeVersion`.
* **The build:** one JDK for the whole project, the latest LTS which can still compile for every generation's minimum
  (now JDK 25). The test classes are compiled for it (`maven.compiler.testSource`), and the enforcer requires it.

### Jakarta EE 10

Dropped: no implementation set that was both complete and maintained could be lined up for it. It had only ever been
half-wired, as a `_jakarta-ee-10.0.0` profile in which EclipseLink 4.0.8 was the only implementation with a matching
version. Its six `jakarta-persistence-3.x-{hibernate,eclipselink}` marker profiles went with it.

### Jakarta EE 12

Jakarta EE Platform 12 is **not released**. Its Eclipse project page still carries a
planned date of 2026-05-31, which has now passed with no release: no
`jakarta.jakartaee-bom` 12.x exists on Maven Central (the BOM's metadata was last
updated 2025-04-08), and community reporting in April 2026 pointed at the Core Profile
landing in Q4 2026 with the Web Profile and Platform after that. The individual specs
have started to go final on their own — `jakarta.enterprise.cdi-api` 5.0.0 was released
on 2026-09-16 — but everything else below is still milestone/pre-release, and there is
no platform BOM to move to.

| Spec (EE 12 target) | Latest artifact on Central | Implementation | Latest artifact on Central |
| --- | --- | --- | --- |
| Jakarta Persistence 4.0 | `jakarta.persistence-api` 4.0.0-M8 | Hibernate ORM 8.0 (JPA 4.0 / EE 12 / Java 17+) | 8.0.0.CR1 |
| Jakarta Persistence 4.0 | `jakarta.persistence-api` 4.0.0-M8 | EclipseLink 6.0 | 6.0.0-M1 (milestone) |
| Jakarta Validation 4.0 | `jakarta.validation-api` 4.0.0-M1 | Hibernate Validator | no EE 12 line published (9.1 is Validation 3.1) |
| Jakarta CDI 5.0 | `jakarta.enterprise.cdi-api` 5.0.0 (final) | Weld 7 (CDI 5.0) | 7.0.0.Final |
| Jakarta Expression Language 6.1 | `jakarta.el-api` 6.1.0-M2 | Expressly | no 6.1 published |

So there is no `jinahya-persistence-12` yet. Adding it means a new generation parent with the umbrella BOM and *all*
implementations moved at once, and today that is only possible on milestone builds.

*Series excluded as end-of-life or superseded: Hibernate ORM 6.x, 7.0, 7.1 and 7.3;
Hibernate Validator 8.0 and older; EclipseLink 4.0 and older; Weld 5 and older; and the
Jakarta EE 9/9.1 and 10 platform generations.*

*Surveyed 2026-11-14 against Maven Central metadata and the upstream release pages
([Hibernate ORM](https://hibernate.org/orm/releases/),
[Hibernate Validator](https://hibernate.org/validator/releases/),
[EclipseLink](https://eclipse.dev/eclipselink/releases/index.html),
[Weld](https://weld.cdi-spec.org/get-started/),
[Jakarta EE Platform 12](https://projects.eclipse.org/projects/ee4j.jakartaee-platform/releases/12)).*

### Adding a generation

1. Copy `jinahya-persistence-NN/` to the new generation, renaming the directories and the artifactIds.
2. In its `pom.xml`, set `jakartaee`, `maven.compiler.source` (the generation's minimum Java SE), the BOM,
   `persistence.version`, `persistence.schemaLocation`, and each implementation's version.
3. Put what differs for the generation in its modules' own `src/`; everything else stays in `shared/`.
4. Add it to the root `<modules>`, to the table above, and give it a `README.md` of its own.

## Modules

| Module | What it holds | Depends on (within the reactor) | Maven Central |
| --- | --- | --- | --- |
| [`jinahya-persistence-utils-11`](shared/jinahya-persistence-utils) | Static helpers over the core Jakarta Persistence types: resource-level transaction wrappers, JDBC `Connection` unwrapping, and reflective read/write of a metamodel `Attribute`. | — | [![v](https://img.shields.io/maven-central/v/io.github.jinahya/jinahya-persistence-utils-11)](https://central.sonatype.com/artifact/io.github.jinahya/jinahya-persistence-utils-11) [![javadoc](https://javadoc.io/badge2/io.github.jinahya/jinahya-persistence-utils-11/javadoc.svg)](https://javadoc.io/doc/io.github.jinahya/jinahya-persistence-utils-11) |
| [`jinahya-persistence-more-11`](shared/jinahya-persistence-more) | Extended mapping building blocks, in five packages: attribute enums and self-referencing entities; the attribute converters (see [below](#inside-jinahya-persistence-more)); the mapped colour types; and mapped superclasses for temporal intervals and for ordered ranges. | — | [![v](https://img.shields.io/maven-central/v/io.github.jinahya/jinahya-persistence-more-11)](https://central.sonatype.com/artifact/io.github.jinahya/jinahya-persistence-more-11) [![javadoc](https://javadoc.io/badge2/io.github.jinahya/jinahya-persistence-more-11/javadoc.svg)](https://javadoc.io/doc/io.github.jinahya/jinahya-persistence-more-11) |
| [`jinahya-persistence-encryption-11`](shared/jinahya-persistence-encryption) | Application-level encryption of entity attributes through standard attribute converters: one `Encrypted<Type>Converter` per supported type, the `EntityEncryptionService` SPI an application implements once, and the fixed encodings. The plaintext is the attribute, the ciphertext is its column. Formerly `jinahya-persistence-crypto`. | — | [![v](https://img.shields.io/maven-central/v/io.github.jinahya/jinahya-persistence-encryption-11)](https://central.sonatype.com/artifact/io.github.jinahya/jinahya-persistence-encryption-11) [![javadoc](https://javadoc.io/badge2/io.github.jinahya/jinahya-persistence-encryption-11/javadoc.svg)](https://javadoc.io/doc/io.github.jinahya/jinahya-persistence-encryption-11) |
| [`jinahya-persistence-encryption-hibernate-11`](shared/jinahya-persistence-encryption-hibernate) | Hibernate ORM's own way into `-encryption`: `@Encrypted`, one annotation for every supported type; the service from a persistence unit property; the ciphertext in the second-level cache. Optional. | `-encryption` (`compile`) | [![v](https://img.shields.io/maven-central/v/io.github.jinahya/jinahya-persistence-encryption-hibernate-11)](https://central.sonatype.com/artifact/io.github.jinahya/jinahya-persistence-encryption-hibernate-11) [![javadoc](https://javadoc.io/badge2/io.github.jinahya/jinahya-persistence-encryption-hibernate-11/javadoc.svg)](https://javadoc.io/doc/io.github.jinahya/jinahya-persistence-encryption-hibernate-11) |
| [`jinahya-persistence-encryption-eclipselink-11`](shared/jinahya-persistence-encryption-eclipselink) | EclipseLink's own way into `-encryption`: one named converter for every supported type, nothing listed; binary DDL; the service from a persistence unit property. Optional. | `-encryption` (`compile`) | [![v](https://img.shields.io/maven-central/v/io.github.jinahya/jinahya-persistence-encryption-eclipselink-11)](https://central.sonatype.com/artifact/io.github.jinahya/jinahya-persistence-encryption-eclipselink-11) [![javadoc](https://javadoc.io/badge2/io.github.jinahya/jinahya-persistence-encryption-eclipselink-11/javadoc.svg)](https://javadoc.io/doc/io.github.jinahya/jinahya-persistence-encryption-eclipselink-11) |
| [`jinahya-persistence-more-test-11`](shared/jinahya-persistence-more-test) | Abstract JUnit base classes for testing what `-more` defines — attribute converters and attribute enums. Lives in `src/main` so other projects' tests can extend it. | `-more` (`provided`) | [![v](https://img.shields.io/maven-central/v/io.github.jinahya/jinahya-persistence-more-test-11)](https://central.sonatype.com/artifact/io.github.jinahya/jinahya-persistence-more-test-11) [![javadoc](https://javadoc.io/badge2/io.github.jinahya/jinahya-persistence-more-test-11/javadoc.svg)](https://javadoc.io/doc/io.github.jinahya/jinahya-persistence-more-test-11) |
| [`jinahya-persistence-test-utils-11`](shared/jinahya-persistence-test-utils) | Randomizer / instantiator / persister SPIs, with locators, for building entity instances in a test suite. Also `src/main`, for the same reason. | — | not published yet |

### Inside `jinahya-persistence-more`

Five packages, by what they are for:

| Package | What it holds |
| --- | --- |
| `…persistence.more` | `AttributeEnum`, whose constants declare the value actually written to the database so the persisted form survives renaming and reordering; and `__SelfReferencing`, the view of an entity's position within a hierarchy of its own type — with `__SelfReferencingOrdered` for the hierarchies whose children are a sequence rather than a set. |
| `…persistence.more.converter` | Everything which converts an attribute. See below. |
| `…persistence.more.converter.javatime` | The `java.time` converters — ISO-8601 text, integral coordinates, day and month names — with the attribute axis those types call for. See below. |
| `…persistence.more.colormodel` | Mapped superclasses for a colour in each of four models — RGB, CMYK, HSL, HWB, and RGB once more with an alpha column — sharing one normalized way of addressing whatever components a model has. Four models but one space: every conversion goes through sRGB, following CSS Color 4. |
| `…persistence.more.temporalinterval` | Mapped superclasses for an interval on a temporal axis — `[start, end)`, either bound optional, one class per `java.time` point type, each measuring in the amount natural to it. Bound by `Temporal`, so the length of an interval is itself a value. |
| `…persistence.more.orderedrange` | Mapped superclasses for a range over anything merely `Comparable` — `Duration`, `BigDecimal`, `Integer`, `String` — carrying the full open/closed bound lattice, encoded into a column which still sorts by its endpoint. No axis, so no length. |

#### The converter package

A converter is pinned by two independent things, and the package keeps them apart. The **column axis** says what the
column is and leaves the attribute open; the **attribute axis** says what the attribute is and leaves the column open.
Both are interfaces carrying nothing, so a converter pinned on both implements one of each and still has its single
inheritance to spend elsewhere:

```java
public abstract class __NumberStringAttributeConverter<X extends Number>
        implements StringAttributeConverter<X>, __NumberAttributeConverter<X, String> { … }
```

The column axis is `StringAttributeConverter` and `__LongAttributeConverter`; the attribute axis is
`__NumberAttributeConverter`, `__BooleanAttributeConverter`, `__TemporalAccessorAttributeConverter` and
`__TemporalAmountAttributeConverter`, with `__TemporalAttributeConverter` and `__TemporalAdjusterAttributeConverter`
refining the third along `java.time.temporal`'s own lines. The four `java.time` axes, and every converter built on
them, live in the `…converter.javatime` sub-package; the column axes stay in `…converter`, shared with the Number and
Boolean families.

Every abstract contract sits alone in its file, and the concrete converters — the ones carrying `@Converter` and
registrable in a persistence unit — live in a `…Converters` holder per family
(the `__Temporal…` holders in `…converter.javatime`, the rest in `…converter`):

| Holder | Converters |
| --- | --- |
| `__NumberStringAttributeConverters` | `BigDecimal`, `BigInteger`, `Byte`, `Short`, `Integer`, `Long`, `Float`, `Double`, each stored as its exact decimal text |
| `InstantConverter`, `LocalDateConverter`, `LocalDateTimeConverter`, `LocalTimeConverter`, `OffsetDateTimeConverter`, `OffsetTimeConverter`, `ZonedDateTimeConverter`, `YearConverter`, `YearMonthConverter`, `MonthDayConverter` | one holder per `java.time` type with a `parse(CharSequence)`, whose `OfString` stores it in ISO-8601 or through a `DateTimeFormatter`; `YearConverter` also has `OfShort` and `OfInteger`, for a numeric column such as MySQL's `YEAR` |
| `DayOfWeekConverter`, `MonthConverter` | `DayOfWeek` and `Month`, each through its `OfString`, stored as their *name* — `Monday`, `March` — in a locale the converter names, English by default and never the server's, read back regardless of case |
| `__TemporalAmountStringAttributeConverters` | `Duration` and `Period` |
| `ZoneIdStringAttributeConverter` | a `ZoneId`, stored as its identifier — `Asia/Seoul`, `+09:00` — a concrete converter on its own, with no holder; and, nested in it and built on it, `TimeZoneStringAttributeConverter`, a `java.util.TimeZone` stored as the identifier of its zone; in `…converter.javatime` |
| `__BooleanYnAttributeConverters` | a legacy `'Y'`/`'N'` flag, as a `Character` or a `String`, strict or lenient |

`AttributeEnumConverter` is the exception with a reason: its four nested types are abstract, not registrable, because
a converter has to name its enum class and that class belongs to the consumer.

#### Upgrading to 0.7.0

Every artifact is now built once per Jakarta EE generation, and carries the generation in its artifactId: replace
`jinahya-persistence-<module>` with `jinahya-persistence-<module>-11`. The packages and the `Automatic-Module-Name`s are
unchanged. The main classes are now compiled for Java 17, the minimum of Jakarta EE 11, instead of 21.

#### Upgrading to 0.6.11

The converter sub-packages are named after the whole package of the API they convert, with the dots left out:
`…converter.time` is now `…converter.javatime`, `…converter.util` is now `…converter.javautil`, and `…converter.json`
is split into `…converter.jakartajson`, for `JsonpAttributeConverter`, and `…converter.jakartajsonbind`, for
`JsonbAttributeConverter`. A converter named in `persistence.xml` or in `@Convert` has to be renamed accordingly —
`…more.converter.time.ZoneIdStringAttributeConverter` is now `…more.converter.javatime.ZoneIdStringAttributeConverter`.
`__StringAttributeConverter` is now `StringAttributeConverter`, in the same package, and its test base in
`jinahya-persistence-more-test` is now `StringAttributeConverter_Test`; a class which implements or extends either has
to be renamed accordingly.
`__TemporalAccessorStringAttributeConverters` is gone: each of its converters is now the `OfString` of a top-level
holder named after its type, in `…converter.javatime` —
`…javatime.__TemporalAccessorStringAttributeConverters$OfYearMonth` is now `…javatime.YearMonthConverter$OfString`.
`__TemporalAccessorTextStringAttributeConverters` is gone the same way: `…javatime.__TemporalAccessorTextStringAttributeConverters$OfDayOfWeek`
is now `…javatime.DayOfWeekConverter$OfString`, and `$OfMonth` is now `…javatime.MonthConverter$OfString`.
`YearConverter` also has `OfShort` and `OfInteger`, which store a `Year` as its value in a numeric column.
New in `…converter.orgw3cdom`: `NodeAttributeConverter`, which stores a DOM `Document`, `DocumentFragment` or
`Element` as XML text, and reads whatever an XML column holds, a document or content.
New in `…converter.jakartaxmlbind`: `JaxbAttributeConverter`, which stores any type Jakarta XML Binding maps as XML
text, as a `String` or as a `byte[]`; a subclass passes the attribute class and overrides `context()`, `write(...)`
or `read(...)` for its own context, such as one with an `ObjectFactory`, marshaller properties and reading.
New in `…converter.javautil`: `Base64StringAttributeConverter`, which stores a `byte[]` as its `java.util.Base64`
text, one nested converter for each encoder — `OfBasic`, `OfUrl` and `OfMime`, each also `…WithoutPadding`.
Also new there: `HexFormatStringAttributeConverter`, which stores a `byte[]` as its `java.util.HexFormat` digits,
`OfLowerCase` or `OfUpperCase`, and reads either case; and `UuidAttributeConverter`, which stores a `UUID` as its
text, `OfString`, as its 16 big-endian bytes, `OfBytes`, or as their 32 hexadecimal digits, `OfHexString`, for a
text or a binary column; each keeps the order of version 6 and 7 UUIDs. A column of a native UUID type takes no
converter.
Also new: `OfBigInteger`, `OfByte` and `OfShort` in `__NumberStringAttributeConverters`; and a converter each, as
its text, for a `Currency` (its ISO 4217 code) in `…converter.javautil`, a `URI` in the new `…converter.javanet`, a
`Charset` (its canonical name) in the new `…converter.javaniocharset` and a `ZoneOffset` in `…converter.javatime`;
and `BitSetBytesAttributeConverter`, in `…converter.javautil`, which stores a `BitSet` as its `toByteArray()`. A
`BitSet` changed in place is stored by Hibernate ORM, not by EclipseLink; assign a changed copy instead.
New in `LocaleStringAttributeConverter`: `OfLenient`, which stores a locale as the arguments of `Locale.of` —
`ko`, `ko_KR`, `en_US_POSIX` — and reads any column, of one, two or three parts, a `Locale.toString()` one included,
dropping a script and extensions.
`JinahyaPersistenceTestUtils.selectCount` and `selectRandom`, deprecated for removal, are removed; use
`JinahyaEntityManagerUtils.selectEntityCount` and `JinahyaEntityManagerTestUtils.selectRandomEntity`, which they
merely called. New there: `applyCachedEntityManagerFactory(persistenceUnitName, function)`, which opens the factory of a
unit once and applies the same one to each later call, closing it when the virtual machine shuts down.

#### Upgrading to 0.6.8

The `java.time` converters moved to a sub-package of their own, `com.github.jinahya.persistence.more.converter.time`,
together with the attribute-axis interfaces added for them. A converter named in `persistence.xml` or in `@Convert`
has to be renamed accordingly — `…more.converter.__TemporalAccessorStringAttributeConverters$OfYearMonth` is now
`…more.converter.time.__TemporalAccessorStringAttributeConverters$OfYearMonth`. Nothing else in `…converter` moved.
New in the same package: `DayOfWeekConverter.OfString` and `.OfMonth`, which store a
day or a month as its name in a locale the converter names, English by default and never the server's.
Also new there: `ZoneIdStringAttributeConverter`, which stores a `ZoneId` as its identifier, and its nested
`TimeZoneStringAttributeConverter`, which stores a `java.util.TimeZone` as the identifier of its zone.
`…converter.util.TimeZoneStringAttributeConverter`, which stores a `TimeZone` as its ID and reads an unknown ID as
`GMT`, is deprecated from the start in favour of that nested converter.

#### Upgrading to 0.5.5

Two breaking changes land together, so that a call site is edited once rather than twice:

- every converter moved from `com.github.jinahya.persistence.more` to `…more.converter`;
- `__StringAttributeConverter` became an **interface**, so a class which `extends` it now `implements` it, and the
  numeric converters it used to nest moved to `__NumberStringAttributeConverters`.

A converter named in `persistence.xml` or in `@Convert` has to be renamed accordingly —
`…more.__StringAttributeConverter$OfBigDecimal` is now `…more.converter.__NumberStringAttributeConverters$OfBigDecimal`.

One smaller change, in the colour package, which gains something and costs something:

- `___MappedColor` — the root of the colour hierarchy, which maps no column of its own — now declares
  `@Access(AccessType.FIELD)`, as every other class in the package already did.

What it gains is the embeddable form the interval and range packages already had: an `@Embeddable` extending one of
the colour classes, embedded twice in one entity, so a table can carry a foreground **and** a background colour.
Without the annotation EclipseLink refuses to deploy such a unit, with a `NullPointerException` in
`EmbeddableAccessor.preProcessMappedSuperclassMetadata` — it walks the mapped-superclass chain of an `@Embeddable`
and fails where a link in that chain has no access type of its own.

What it costs falls on **Hibernate ORM 7.2 only**, and only on one shape of entity: that series propagates the *root*
mapped superclass's access type down onto the entity, so an entity which extends a colour class *and* declares its
`@Id` on a getter must now declare `@Access(AccessType.PROPERTY)` itself, or be rejected at deployment as having no
identifier. ORM 7.4 and EclipseLink read Jakarta Persistence 3.2 §2.3.1 as written and need no such declaration. The
rule the package documents either way: **an entity extending one of these should state its access type, whichever one
it is** — inference is unreliable in both directions, and in opposite providers.

### Dependencies

Per module, at the scopes a consumer sees. `test`-scoped dependencies are omitted; they are a private matter of each
module's own build.

Two properties hold across the whole reactor, and are worth keeping that way:

- **`compile` appears three times** — the two `-encryption-*` provider modules on `-encryption`, and `-test-utils` on
  `jinahya-object-randomizer`, which declares every dependency of its own as `provided` or `test`. Every other
  dependency of every module is `provided`, so adding one of these artifacts to a project pulls in nothing the project
  did not ask for.
- **Nothing is `runtime`-scoped.** Where a runtime implementation is needed — a persistence provider, a validation
  provider, a JDBC driver — the module leaves the choice to the consumer, and only the build picks one (see
  [Profiles](#profiles)).

Every `jakarta.*` version comes from the generation's `jakarta.jakartaee-bom` umbrella and is therefore absent from the
poms. The dependencies of each artifact, with the versions they resolve to, are tabled in its generation's README; see
[Runtime dependencies of the artifacts](jinahya-persistence-11/README.md#runtime-dependencies-of-the-artifacts) for
Jakarta EE 11. `jakarta.persistence-api` is declared once, in each `jinahya-persistence-NN` parent, and inherited by
its modules; the rest are declared by the module which uses them.

#### Notes on the two test-support modules

Their helper types sit in `src/main`, not `src/test`, so that another project's tests can extend them. That makes the
choice of scope for a testing library a published-API decision rather than a private one:

- `-more-test` needs `junit-jupiter-api` at compile time and cannot avoid it — its base classes carry the `@Test`
  methods a subclass inherits, and implement JUnit extension interfaces in their own signatures. It is `provided`, so a
  consumer's JUnit version wins rather than this module's.
- Nothing else is needed. AssertJ, Mockito and `junit-platform-commons` were each removed in favour of what
  `org.junit.jupiter.api.Assertions` and plain reflection already provide, so a consumer needs only JUnit on the test
  classpath.
- `-test-utils` needs no testing library at all in `src/main`. The randomizer role it applies comes from
  `jinahya-object-randomizer` at `compile` scope — a consumer extends its flavors, so it has to be on both classpaths —
  and the three engines behind them are `provided`, so a consumer brings only the one it uses.
  `fixture-monkey-jakarta-validation` is `provided` for the same reason: `FixtureMonkeyObjectRandomizer` looks the
  plugin up reflectively, so declaring it is what makes that flavor honor `jakarta.validation.constraints`. The root
  pom excludes the Jakarta EE 9 stack that artifact declares at `compile` scope, which would otherwise downgrade the
  platform; the exclusion is backed by `hibernate-validator` and `expressly`, at the EE 11 versions, on the module's
  test classpath.
