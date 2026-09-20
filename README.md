# jinahya-persistence

[![Java CI with Maven](https://github.com/jinahya/jinahya-persistence/actions/workflows/maven.yml/badge.svg)](https://github.com/jinahya/jinahya-persistence/actions/workflows/maven.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=jinahya_jinahya-persistence&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=jinahya_jinahya-persistence)

[![Maven Central Version](https://img.shields.io/maven-central/v/io.github.jinahya/jinahya-persistence)](https://central.sonatype.com/artifact/io.github.jinahya/jinahya-persistence/versions)
[![javadoc](https://javadoc.io/badge2/io.github.jinahya/jinahya-persistence/javadoc.svg)](https://javadoc.io/doc/io.github.jinahya/jinahya-persistence)


## Jakarta EE alignment

Every spec API in this build comes from a single **Jakarta EE platform umbrella BOM**
(`jakarta.platform:jakarta.jakartaee-bom`, imported in the root `pom.xml`); only the
implementations carry their own versions, and each is kept on a series certified for
that platform generation. Build baseline: Java 21 (tests compiled at 25).

Subsections below are ordered by Jakarta EE version.

### Jakarta EE 10 — dropped

Support for Jakarta EE 10 was removed. It had only ever been half-wired: the
`_jakarta-ee-10.0.0` profile switched the BOM to 10.0.0 (persistence-api 3.1.0,
validation-api 3.0.2, cdi-api 4.0.1, el-api 5.0.1), but EclipseLink 4.0.8 was the only
implementation with a matching profile — every other implementation fell back to its
EE 11 version, and `-P_jakarta-ee-10.0.0` on its own resolved no persistence provider at
all. It also declared `persistence.version` 3.0 while EE 10 ships Jakarta Persistence 3.1.

Removed with it: `_jakarta-ee-10.0.0`, `__eclipselink-4.0.8-jakarta-ee-10`, the
`_jakarta-ee-11.0.0` profile (its values moved into `<properties>`), and the six empty
`jakarta-persistence-3.x-{hibernate,eclipselink}` marker profiles, which covered the
Jakarta EE 9/9.1 and 10 generations and carried no configuration.

### Jakarta EE 11 — current

This is the only supported generation. Its platform values live in the root
`<properties>` — `version.jakarta.jakartaee-bom` (11.0.0), `persistence.version` (3.2),
`persistence.schemaLocation` — not in a profile, so no profile combination can move the
platform out from under the implementations.

| Spec | Version from the EE 11 BOM | Implementation | In this build | Latest released | Status |
| --- | --- | --- | --- | --- | --- |
| Jakarta Persistence 3.2 | `jakarta.persistence-api` 3.2.0 | Hibernate ORM (`hibernate-core`) | 7.4.9.Final | 7.4.9.Final | current — latest stable series for JPA 3.2 / EE 11 |
| Jakarta Persistence 3.2 | `jakarta.persistence-api` 3.2.0 | EclipseLink (`org.eclipse.persistence.jpa`) | 5.0.1 | 5.0.1 | current — 5.0 is certified for JPA 3.2 / EE 11 |
| Jakarta Validation 3.1 | `jakarta.validation-api` 3.1.1 | Hibernate Validator | 9.1.3.Final | 9.1.3.Final | current |
| Jakarta Expression Language 6.0 | `jakarta.el-api` 6.0.1 | Expressly (required by Hibernate Validator) | 6.0.0 | 6.0.0 | current |
| Jakarta CDI 4.1 | `jakarta.enterprise.cdi-api` 4.1.0 | Weld, via `weld-junit5` | weld-junit5 5.0.3.Final → weld-se-core 6.0.3.Final | weld-junit5 5.0.3.Final (Weld 6.0.4.Final) | current — `weld-junit5` 5.0.3 pins Weld 6.0.3 |

`version.org.jboss.weld` is the **weld-junit5 (weld-testing)** version, not the Weld
version: weld-junit5 5.0.x is the Weld 6 / CDI 4.1 line.

#### Profiles

The platform is fixed, so the profiles only choose implementations. They form
independent axes — provider and validator each offer a choice, CDI currently has a
single profile:

| Axis | Profile | Implementation |
| --- | --- | --- |
| provider (`__`) | `__hibernate-orm-7.4-jakarta-ee-11` | Hibernate ORM 7.4.9.Final — **active by default** |
| provider (`__`) | `__hibernate-orm-7.2-jakarta-ee-11` | Hibernate ORM 7.2.25.Final (limited-support series) |
| provider (`__`) | `__eclipselink-5.0-jakarta-ee-11` | EclipseLink 5.0.1 |
| validator (`___`) | `___hibernate-validator-9.1-jakarta-ee-11` | Hibernate Validator 9.1.3.Final — active by default |
| validator (`___`) | `___hibernate-validator-9.0-jakarta-ee-11` | Hibernate Validator 9.0.1.Final (limited-support series) |
| CDI (`___`) | `___weld-6-jakarta-ee-11` | `weld-junit5` 5.0.3.Final — active by default |

That is 3 × 2 = **six supported combinations**, all against the same Jakarta EE 11
platform.

**Name every axis you use.** The defaults are `activeByDefault`, and Maven deactivates
all `activeByDefault` profiles as soon as any profile is named on the command line — so
`-P___hibernate-validator-9.0-jakarta-ee-11` on its own resolves *no persistence
provider at all*. Version properties survive (the root `<properties>` repeat the default
profiles' values), but the provider *dependency* comes only from a provider profile.

`./_mvn_jakarta_ee_11.sh [maven args...]` runs the given build once per combination and
reports which ones failed — e.g. `./_mvn_jakarta_ee_11.sh test` or
`./_mvn_jakarta_ee_11.sh -q enforcer:enforce -Drules=dependencyConvergence`.

### Jakarta EE 12 — next (not adoptable yet)

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
| Jakarta Persistence 4.0 | `jakarta.persistence-api` 4.0.0-M7 | Hibernate ORM 8.0 (JPA 4.0 / EE 12 / Java 17+) | 8.0.0.Beta1 |
| Jakarta Persistence 4.0 | — | EclipseLink | no EE 12 line published |
| Jakarta Validation 4.0 | `jakarta.validation-api` 4.0.0-M1 | Hibernate Validator | no EE 12 line published (9.1 is Validation 3.1) |
| Jakarta CDI 5.0 | `jakarta.enterprise.cdi-api` 5.0.0 (final) | Weld 7 (CDI 5.0) | 7.0.0.CR1 |
| Jakarta Expression Language 6.1 | `jakarta.el-api` 6.1.0-M2 | Expressly | no 6.1 published |

Moving to EE 12 means moving the umbrella BOM and *all* implementations at once, and
today that is only possible on milestone builds.

*Series excluded as end-of-life or superseded: Hibernate ORM 6.x, 7.0, 7.1 and 7.3;
Hibernate Validator 8.0 and older; EclipseLink 4.0 and older; Weld 5 and older; and the
Jakarta EE 9/9.1 and 10 platform generations.*

*Surveyed 2026-09-19 against Maven Central metadata and the upstream release pages
([Hibernate ORM](https://hibernate.org/orm/releases/),
[Hibernate Validator](https://hibernate.org/validator/releases/),
[EclipseLink](https://eclipse.dev/eclipselink/releases/index.html),
[Weld](https://weld.cdi-spec.org/get-started/),
[Jakarta EE Platform 12](https://projects.eclipse.org/projects/ee4j.jakartaee-platform/releases/12)).*

## Modules

| Module | What it holds | Depends on (within the reactor) | Maven Central |
| --- | --- | --- | --- |
| [`jinahya-persistence-utils`](jinahya-persistence-utils) | Static helpers over the core Jakarta Persistence types: resource-level transaction wrappers, JDBC `Connection` unwrapping, and reflective read/write of a metamodel `Attribute`. | — | [![v](https://img.shields.io/maven-central/v/io.github.jinahya/jinahya-persistence-utils)](https://central.sonatype.com/artifact/io.github.jinahya/jinahya-persistence-utils) [![javadoc](https://javadoc.io/badge2/io.github.jinahya/jinahya-persistence-utils/javadoc.svg)](https://javadoc.io/doc/io.github.jinahya/jinahya-persistence-utils) |
| [`jinahya-persistence-more`](jinahya-persistence-more) | Extended mapping building blocks, in three packages: attribute enums and self-referencing entities; the attribute converters (see [below](#inside-jinahya-persistence-more)); and the mapped colour types. | — | [![v](https://img.shields.io/maven-central/v/io.github.jinahya/jinahya-persistence-more)](https://central.sonatype.com/artifact/io.github.jinahya/jinahya-persistence-more) [![javadoc](https://javadoc.io/badge2/io.github.jinahya/jinahya-persistence-more/javadoc.svg)](https://javadoc.io/doc/io.github.jinahya/jinahya-persistence-more) |
| [`jinahya-persistence-crypto`](jinahya-persistence-crypto) | Transparent attribute encryption for entities: the encryption service, its CDI qualifier, and the lifecycle listener which moves values between plaintext and ciphertext attributes. | `-utils` (`compile`) | [![v](https://img.shields.io/maven-central/v/io.github.jinahya/jinahya-persistence-crypto)](https://central.sonatype.com/artifact/io.github.jinahya/jinahya-persistence-crypto) [![javadoc](https://javadoc.io/badge2/io.github.jinahya/jinahya-persistence-crypto/javadoc.svg)](https://javadoc.io/doc/io.github.jinahya/jinahya-persistence-crypto) |
| [`jinahya-persistence-more-test`](jinahya-persistence-more-test) | Abstract JUnit base classes for testing what `-more` defines — attribute converters and attribute enums. Lives in `src/main` so other projects' tests can extend it. | `-more` (`provided`) | [![v](https://img.shields.io/maven-central/v/io.github.jinahya/jinahya-persistence-more-test)](https://central.sonatype.com/artifact/io.github.jinahya/jinahya-persistence-more-test) [![javadoc](https://javadoc.io/badge2/io.github.jinahya/jinahya-persistence-more-test/javadoc.svg)](https://javadoc.io/doc/io.github.jinahya/jinahya-persistence-more-test) |
| [`jinahya-persistence-test-utils`](jinahya-persistence-test-utils) | Randomizer / instantiator / persister SPIs, with locators, for building entity instances in a test suite. Also `src/main`, for the same reason. | — | not published yet |
| [`coverage-report-aggregated`](coverage-report-aggregated) | Build-only: aggregates every module's JaCoCo execution data into one report. Not an artifact anyone depends on. | all five | — |

### Inside `jinahya-persistence-more`

Three packages, by what they are for:

| Package | What it holds |
| --- | --- |
| `…persistence.more` | `__AttributeEnum`, whose constants declare the value actually written to the database so the persisted form survives renaming and reordering; and `__SelfReferencing`, the view of an entity's position within a hierarchy of its own type. |
| `…persistence.more.converter` | Everything which converts an attribute. See below. |
| `…persistence.more.color` | Mapped superclasses for colours in several models — RGB, RGBA, HSL, HWB, CMYK — sharing one way of addressing their components, and following CSS Color 4 for every conversion. |

#### The converter package

A converter is pinned by two independent things, and the package keeps them apart. The **column axis** says what the
column is and leaves the attribute open; the **attribute axis** says what the attribute is and leaves the column open.
Both are interfaces carrying nothing, so a converter pinned on both implements one of each and still has its single
inheritance to spend elsewhere:

```java
public abstract class __NumberStringAttributeConverter<X extends Number>
        implements __StringAttributeConverter<X>, __NumberAttributeConverter<X, String> { … }
```

Every abstract contract sits alone in its file, and the concrete converters — the ones carrying `@Converter` and
registrable in a persistence unit — live in a `…Converters` holder per family:

| Holder | Converters |
| --- | --- |
| `__NumberStringAttributeConverters` | `BigDecimal`, `Integer`, `Long`, `Float`, `Double`, each stored as its exact decimal text |
| `__TemporalAccessorStringAttributeConverters` | the ten `java.time` types with a `parse(CharSequence)`, in ISO-8601 or through a `DateTimeFormatter` |
| `__TemporalAmountStringAttributeConverters` | `Duration` and `Period` |
| `__BooleanYnAttributeConverters` | a legacy `'Y'`/`'N'` flag, as a `Character` or a `String`, strict or lenient |

`__AttributeEnumConverter` is the exception with a reason: its four nested types are abstract, not registrable, because
a converter has to name its enum class and that class belongs to the consumer.

#### Upgrading to 0.5.5

Two breaking changes land together, so that a call site is edited once rather than twice:

- every converter moved from `com.github.jinahya.persistence.more` to `…more.converter`;
- `__StringAttributeConverter` became an **interface**, so a class which `extends` it now `implements` it, and the
  numeric converters it used to nest moved to `__NumberStringAttributeConverters`.

A converter named in `persistence.xml` or in `@Convert` has to be renamed accordingly —
`…more.__StringAttributeConverter$OfBigDecimal` is now `…more.converter.__NumberStringAttributeConverters$OfBigDecimal`.

### Dependencies

Per module, at the scopes a consumer sees. `test`-scoped dependencies are omitted; they are a private matter of each
module's own build.

Two properties hold across the whole reactor, and are worth keeping that way:

- **`compile` appears exactly once** — `-crypto` on `-utils`. Every other dependency of every module is `provided`, so
  adding one of these artifacts to a project pulls in nothing the project did not ask for.
- **Nothing is `runtime`-scoped.** Where a runtime implementation is needed — a persistence provider, a validation
  provider, a JDBC driver — the module leaves the choice to the consumer, and only the build picks one (see the
  profiles above).

Every `jakarta.*` version comes from the `jakarta.jakartaee-bom` umbrella and is therefore absent from the poms; the
versions below are what the current platform (`11.0.0`) resolves to.

| Module | compile | runtime | provided |
| --- | --- | --- | --- |
| `jinahya-persistence-utils` | — | — | `jakarta.persistence:jakarta.persistence-api` 3.2.0<br>`org.jspecify:jspecify` 1.0.1 |
| `jinahya-persistence-more` | — | — | `jakarta.persistence:jakarta.persistence-api` 3.2.0<br>`jakarta.validation:jakarta.validation-api` 3.1.1<br>`org.jspecify:jspecify` 1.0.1 |
| `jinahya-persistence-crypto` | `io.github.jinahya:jinahya-persistence-utils` | — | `jakarta.annotation:jakarta.annotation-api` 3.0.0<br>`jakarta.enterprise:jakarta.enterprise.cdi-api` 4.1.0<br>`jakarta.inject:jakarta.inject-api` 2.0.1<br>`jakarta.persistence:jakarta.persistence-api` 3.2.0<br>`jakarta.validation:jakarta.validation-api` 3.1.1<br>`org.jspecify:jspecify` 1.0.1 |
| `jinahya-persistence-more-test` | — | — | `io.github.jinahya:jinahya-persistence-more`<br>`jakarta.persistence:jakarta.persistence-api` 3.2.0<br>`jakarta.validation:jakarta.validation-api` 3.1.1<br>`org.jspecify:jspecify` 1.0.1<br>`org.junit.jupiter:junit-jupiter-api` 5.14.4 |
| `jinahya-persistence-test-utils` | — | — | `com.navercorp.fixturemonkey:fixture-monkey` 1.2.3<br>`jakarta.persistence:jakarta.persistence-api` 3.2.0<br>`org.instancio:instancio-core` 6.0.1<br>`org.jeasy:easy-random` 6.0.1<br>`org.jspecify:jspecify` 1.0.1<br>`uk.co.jemos.podam:podam` 8.0.2.RELEASE |

`jakarta.persistence-api` and `jspecify` are declared once, in the root pom, and inherited by every module; the rest are
declared by the module which uses them.

#### Notes on the two test-support modules

Their helper types sit in `src/main`, not `src/test`, so that another project's tests can extend them. That makes the
choice of scope for a testing library a published-API decision rather than a private one:

- `-more-test` needs `junit-jupiter-api` at compile time and cannot avoid it — its base classes carry the `@Test`
  methods a subclass inherits, and implement JUnit extension interfaces in their own signatures. It is `provided`, so a
  consumer's JUnit version wins rather than this module's.
- Nothing else is needed. AssertJ, Mockito and `junit-platform-commons` were each removed in favour of what
  `org.junit.jupiter.api.Assertions` and plain reflection already provide, so a consumer needs only JUnit on the test
  classpath.
- `-test-utils` needs no testing library at all in `src/main`; Easy Random and PoDAM are its subject matter, and both
  are `provided` for the same reason.
