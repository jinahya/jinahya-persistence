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
| Jakarta Persistence 3.2 | `jakarta.persistence-api` 3.2.0 | Hibernate ORM (`hibernate-core`) | 7.4.7.Final | 7.4.7.Final | current — latest stable series for JPA 3.2 / EE 11 |
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
| provider (`__`) | `__hibernate-orm-7.4-jakarta-ee-11` | Hibernate ORM 7.4.7.Final — **active by default** |
| provider (`__`) | `__hibernate-orm-7.2-jakarta-ee-11` | Hibernate ORM 7.2.24.Final (limited-support series) |
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

Jakarta EE Platform 12 is **not released**. The Eclipse project page still shows a
planned date of 2026-05-31, but no `jakarta.jakartaee-bom` 12.x exists on Maven Central
(the BOM's metadata was last updated 2025-04-08), and community reporting in April 2026
pointed at the Core Profile landing in Q4 2026 with the Web Profile and Platform after
that. Everything below is milestone/pre-release only.

| Spec (EE 12 target) | Latest artifact on Central | Implementation | Latest artifact on Central |
| --- | --- | --- | --- |
| Jakarta Persistence 4.0 | `jakarta.persistence-api` 4.0.0-M6 | Hibernate ORM 8.0 (JPA 4.0 / EE 12 / Java 17+) | 8.0.0.Beta1 |
| Jakarta Persistence 4.0 | — | EclipseLink | no EE 12 line published |
| Jakarta Validation 4.0 | `jakarta.validation-api` 4.0.0-M1 | Hibernate Validator | no EE 12 line published (9.1 is Validation 3.1) |
| Jakarta CDI 5.0 | `jakarta.enterprise.cdi-api` 5.0.0.CR1 | Weld 7 (CDI 5.0) | 7.0.0.CR1 |
| Jakarta Expression Language 6.1 | `jakarta.el-api` 6.1.0-M2 | Expressly | no 6.1 published |

Moving to EE 12 means moving the umbrella BOM and *all* implementations at once, and
today that is only possible on milestone builds.

*Series excluded as end-of-life or superseded: Hibernate ORM 6.x, 7.0, 7.1 and 7.3;
Hibernate Validator 8.0 and older; EclipseLink 4.0 and older; Weld 5 and older; and the
Jakarta EE 9/9.1 and 10 platform generations.*

*Surveyed 2026-09-05 against Maven Central metadata and the upstream release pages
([Hibernate ORM](https://hibernate.org/orm/releases/),
[Hibernate Validator](https://hibernate.org/validator/releases/),
[EclipseLink](https://eclipse.dev/eclipselink/releases/index.html),
[Weld](https://weld.cdi-spec.org/get-started/),
[Jakarta EE Platform 12](https://projects.eclipse.org/projects/ee4j.jakartaee-platform/releases/12)).*

## Modules

### [jinahya-persistence-more](https://github.com/jinahya/jinahya-persistence/tree/develop/jinahya-persistence-more)

### [jinahya-persistence-more-test](https://github.com/jinahya/jinahya-persistence/tree/develop/jinahya-persistence-more-test)

### [jinahya-persistence-test-utils](https://github.com/jinahya/jinahya-persistence/tree/develop/jinahya-persistence-test-utils)

![Maven Central Version](https://img.shields.io/maven-central/v/io.github.jinahya/jinahya-persistence-test-utils)
[![javadoc](https://javadoc.io/badge2/io.github.jinahya/jinahya-persistence-test-utils/javadoc.svg)](https://javadoc.io/doc/io.github.jinahya/jinahya-persistence-test-utils)
