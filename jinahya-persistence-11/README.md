# jinahya-persistence-11

The Jakarta EE 11 generation of [jinahya-persistence](../README.md): Jakarta Persistence 3.2, on Java SE 17 or
higher.
The current generation.

| Artifact | |
| --- | --- |
| `io.github.jinahya:jinahya-persistence-utils-11` | Utilities for Jakarta Persistence 3.2 |
| `io.github.jinahya:jinahya-persistence-more-11` | Extended mapping building blocks |
| `io.github.jinahya:jinahya-persistence-more-test-11` | Test support for what `-more-11` defines |
| `io.github.jinahya:jinahya-persistence-test-utils-11` | Test utilities: randomizer / instantiator / persister SPIs |
| `io.github.jinahya:jinahya-persistence-encryption-11` | Application-level encryption of entity attributes |
| `io.github.jinahya:jinahya-persistence-encryption-hibernate-11` | `-encryption-11`, Hibernate ORM's own way |
| `io.github.jinahya:jinahya-persistence-encryption-eclipselink-11` | `-encryption-11`, EclipseLink's own way |

Every version below is set in this directory's `pom.xml`, and nowhere else; keep the tables in step with it.
What is the same for every generation (the build JDK, the test dependencies, the plugins) is the root's; see
[Java versions](../README.md#java-versions).

## Environment

| | Version | Set by | Enforced by |
| --- | --- | --- | --- |
| Java SE, minimum | 17 | `maven.compiler.source` | `enforce-require-jakartaee-java-version` |
| Main classes | `--release 17` | `maven.compiler.source` | |
| Runtime dependencies | run on Java SE 17 | | `enforce-jakartaee-bytecode-version` (`enforceBytecodeVersion`) |
| Build JDK | 25 or newer | the root's `maven.compiler.testSource` | the root's `enforce-java` |
| Test classes | `--release 25` | the root's `maven.compiler.testSource` | |

## Jakarta EE 11 alignment

The platform values are `version.jakarta.jakartaee-bom` (11.0.0), `persistence.version` (3.2), and
`persistence.schemaLocation`, all in `<properties>`, not in a profile, so no profile combination can move the
platform out from under the implementations.

| | Version from the EE 11 BOM | Implementation | In this build | Latest of its series | Status |
| --- | --- | --- | --- | --- | --- |
| Jakarta EE Platform | `jakarta.jakartaee-bom` 11.0.0 | | | 11.0.0 | current |
| Jakarta Persistence 3.2 | `jakarta.persistence-api` 3.2.0 (`provided`) | Hibernate ORM 7.4 (`hibernate-core`) | 7.4.15.Final | 7.4.15.Final | current; 8.0, Jakarta Persistence 4.0, is at 8.0.0.CR1 |
| Jakarta Persistence 3.2 | `jakarta.persistence-api` 3.2.0 (`provided`) | EclipseLink 5.0 (`org.eclipse.persistence.jpa`) | 5.0.2 | 5.0.2 | current; 5.0.3 is at 5.0.3-RC1 |
| Jakarta Validation 3.1 | `jakarta.validation-api` 3.1.1 (`provided`) | Hibernate Validator 9.1 | 9.1.6.Final | 9.1.6.Final | current; 9.2, also Jakarta Validation 3.1, is at 9.2.0.CR2 |
| Jakarta Expression Language 6.0 | `jakarta.el-api` 6.0.1 | Expressly 6.0 (for Hibernate Validator) | 6.0.0 | 6.0.0 | current |
| Jakarta CDI 4.1 | `jakarta.enterprise.cdi-api` 4.1.0 | Weld 6.0, via `weld-junit5` 5.0 | weld-junit5 5.0.3.Final → weld-se-core 6.0.3.Final | weld-junit5 5.0.3.Final; weld-se-core 6.0.4.Final | current; `weld-junit5` 5.0.3 pins Weld 6.0.3 |
| Jakarta JSON Processing 2.1 | `jakarta.json-api` 2.1.3 (`provided`) | Eclipse Parsson 1.1 | 1.1.9 | 1.1.9 | current |
| Jakarta JSON Binding 3.0 | `jakarta.json.bind-api` 3.0.1 (`provided`) | Eclipse Yasson 3.0 | 3.0.5 | 3.0.5 | current |
| Jakarta XML Binding 4.0 | not in the EE 11 BOM; `jakarta.xml.bind-api` 4.0.5 (`provided`), versioned in this `pom.xml` | GlassFish JAXB runtime 4.0 (`jaxb-runtime`) | 4.0.9 | 4.0.9 | current; not part of the EE 11 platform |

Every implementation is `test`-scoped: the tests run against one persistence provider at a time, chosen with this
generation's `hibernate-orm` (default) or `eclipselink` profile; Parsson, Yasson, and the JAXB runtime serve the
`-more-11` tests only.

Hibernate Validator and Expressly move together: validator 9 ↔ expressly 6.

`version.org.jboss.weld` is the **weld-junit5 (weld-testing)** version, not the Weld version: weld-junit5 5.0.x is the
Weld 6 / CDI 4.1 line.

## Profiles

The one profile axis: the provider which implements Jakarta Persistence 3.2, at the version above. The profiles choose
no versions; a profile decides only which provider is on the test classpath, which `persistence-unit.provider` goes
into `persistence.xml`, which metamodel generator the annotation processor path uses, and which
`src/test/java-<persistence-provider>` directories are compiled.

| Profile | `persistence-provider` | Provider |
| --- | --- | --- |
| `hibernate-orm` | `hibernate-orm` | Hibernate ORM — **active by default** |
| `eclipselink` | `eclipselink` | EclipseLink |
| `eclipselink-weaving` | | an add-on to `eclipselink`: the EclipseLink agent weaves the entities as they are loaded |

`enforce-require-provider-profile` fails the build when no provider profile is active, e.g. under `-P!hibernate-orm`
alone.

Validation (Hibernate Validator + Expressly) and CDI (Weld) have no profiles: each has a single implementation, at its
Jakarta EE 11 aligned release.

## Runtime dependencies of the artifacts

Every dependency outside the `test` scope; each runs on Java SE 17, which `enforce-jakartaee-bytecode-version` checks.
No artifact has a `runtime` dependency.

| Artifact | Dependency | Scope | Version |
| --- | --- | --- | --- |
| `jinahya-persistence-utils-11` | `jakarta.persistence:jakarta.persistence-api` | `provided` | 3.2.0, from the BOM |
| `jinahya-persistence-more-11` | `jakarta.persistence:jakarta.persistence-api` | `provided` | 3.2.0, from the BOM |
| `jinahya-persistence-more-11` | `jakarta.validation:jakarta.validation-api` | `provided` | 3.1.1, from the BOM |
| `jinahya-persistence-more-11` | `jakarta.annotation:jakarta.annotation-api` | `provided` | 3.0.0, from the BOM |
| `jinahya-persistence-more-11` | `jakarta.json:jakarta.json-api` | `provided` | 2.1.3, from the BOM |
| `jinahya-persistence-more-11` | `jakarta.json.bind:jakarta.json.bind-api` | `provided` | 3.0.1, from the BOM |
| `jinahya-persistence-more-11` | `jakarta.xml.bind:jakarta.xml.bind-api` | `provided` | 4.0.5 |
| `jinahya-persistence-more-test-11` | `io.github.jinahya:jinahya-persistence-more-11` | `provided` | this project's |
| `jinahya-persistence-more-test-11` | `jakarta.persistence:jakarta.persistence-api` | `provided` | 3.2.0, from the BOM |
| `jinahya-persistence-more-test-11` | `org.junit.jupiter:junit-jupiter-api` | `provided` | 6.1.3 |
| `jinahya-persistence-test-utils-11` | `io.github.jinahya:jinahya-persistence-utils-11` | `compile` | this project's |
| `jinahya-persistence-test-utils-11` | `io.github.jinahya:jinahya-object-randomizer` | `compile` | 0.0.7 |
| `jinahya-persistence-test-utils-11` | `jakarta.persistence:jakarta.persistence-api` | `provided` | 3.2.0, from the BOM |
| `jinahya-persistence-test-utils-11` | `org.junit.jupiter:junit-jupiter-api` | `provided` | 6.1.3 |
| `jinahya-persistence-test-utils-11` | `com.navercorp.fixturemonkey:fixture-monkey` | `provided` | 1.2.3 |
| `jinahya-persistence-test-utils-11` | `com.navercorp.fixturemonkey:fixture-monkey-jakarta-validation` | `provided` | 1.2.3 |
| `jinahya-persistence-test-utils-11` | `org.instancio:instancio-core` | `provided` | 6.2.1 |
| `jinahya-persistence-test-utils-11` | `uk.co.jemos.podam:podam` | `provided` | 8.0.2.RELEASE |
| `jinahya-persistence-encryption-11` | `jakarta.persistence:jakarta.persistence-api` | `provided` | 3.2.0, from the BOM |
| `jinahya-persistence-encryption-11` | `jakarta.inject:jakarta.inject-api` | `provided` | 2.0.1, from the BOM |
| `jinahya-persistence-encryption-hibernate-11` | `io.github.jinahya:jinahya-persistence-encryption-11` | `compile` | this project's |
| `jinahya-persistence-encryption-hibernate-11` | `org.hibernate.orm:hibernate-core` | `provided` | 7.4.15.Final |
| `jinahya-persistence-encryption-hibernate-11` | `jakarta.persistence:jakarta.persistence-api` | `provided` | 3.2.0, from the BOM |
| `jinahya-persistence-encryption-eclipselink-11` | `io.github.jinahya:jinahya-persistence-encryption-11` | `compile` | this project's |
| `jinahya-persistence-encryption-eclipselink-11` | `org.eclipse.persistence:org.eclipse.persistence.core` | `provided` | 5.0.2 |
| `jinahya-persistence-encryption-eclipselink-11` | `jakarta.persistence:jakarta.persistence-api` | `provided` | 3.2.0, from the BOM |

*Surveyed 2026-11-14 against Maven Central metadata and the upstream release pages
([Jakarta EE Platform 11](https://jakarta.ee/specifications/platform/11/),
[Hibernate ORM](https://hibernate.org/orm/releases/),
[EclipseLink](https://eclipse.dev/eclipselink/releases/index.html),
[Hibernate Validator](https://hibernate.org/validator/releases/),
[Weld](https://weld.cdi-spec.org/download/)).*

## Building

From the root, which builds every module on one JDK:

```sh
./_mvn.sh -pl jinahya-persistence-11 -amd verify                # against each provider
./_mvn_hibernate_orm.sh -pl jinahya-persistence-11 -amd verify  # against Hibernate ORM only
./mvnw -Peclipselink,eclipselink-weaving -pl jinahya-persistence-11 -amd test   # EclipseLink, woven
```
