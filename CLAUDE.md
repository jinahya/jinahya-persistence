# CLAUDE.md

Guidance for Claude Code when working in this repository.

This project is a member of [jinahyaee-bom](https://github.com/jinahya/jinahyaee-bom) (checked out as the sibling
`../jinahyaee-bom`). Its `README.md` sets the rules every member follows: read its
[Jakarta EE and Java SE](https://github.com/jinahya/jinahyaee-bom#jakarta-ee-and-java-se) and
[Structuring a member](https://github.com/jinahya/jinahyaee-bom#structuring-a-member) sections before changing this
project's layout, its generations, its Java versions, its implementations, or its release. The reference
implementation of those rules is the sibling `../jinahya-validation`; follow it where this file is silent.

See this project's own [README.md](README.md) for its artifacts, layout, and how to build it.

**`.idea/codeStyles` is a git submodule (`onacit/dot-idea-codestyles`), and we don't touch it.** Unlike the other
projects of jakarta-persistence-applied-book-projects, which track the same plain `.idea/codeStyles` files, this one
keeps the submodule with its own content: never replace it with plain files, align it with the others, or bump it.
The `.gitignore` is the one all the projects share.

## Dependency convergence

Dependency convergence is **very important** in this project. A multi-module Maven
build (`jinahya-persistence-*`) that lets the same artifact resolve to different
versions on different classpaths produces failures that only show up at runtime, so
treat any divergence as a build defect, not a warning.

### Origin: the Jakarta EE umbrella platform

Versions in this project originate from the **Jakarta EE platform umbrella BOM**, not
from individual artifacts.

### Layout: one module per Jakarta EE generation

The build follows jinahyaee-bom's "Structuring a member", as the sibling `jinahya-validation` does:

- The root `pom.xml` holds only what is the same for every generation: plugins, the
  enforcer, test libraries, and non-Jakarta dependencies (H2, the randomizer engines, ...).
  It never names a platform or implementation version.
- `jinahya-persistence-NN/pom.xml` (now only `jinahya-persistence-11`) is the parent of
  one Jakarta EE generation. It imports `jakarta.platform:jakarta.jakartaee-bom`
  (`<type>pom</type>`, `<scope>import</scope>`), pinned by `version.jakarta.jakartaee-bom`
  (currently `11.0.0`), and holds `jakartaee`, `maven.compiler.source` (the generation's
  minimum Java SE, 17), `persistence.version` (3.2), `persistence.schemaLocation`, every
  implementation version, and the provider profiles.
- `jinahya-persistence-NN/jinahya-persistence-<module>-NN/` are the published artifacts;
  each carries the generation in its artifactId and holds only a `pom.xml` naming its
  `shared.module`, plus a `src/` for what only that generation needs, if anything.
- `shared/<module>/src/...` holds the sources (and resources, and module docs) every
  generation shares; each generation compiles them against its own API.
  `shared/<module>/src/test/java-<persistence-provider>` holds tests of one provider only.
  `src/test-shared/java` holds the test sources every module shares.
- **Jakarta EE 11 is the only supported generation.** EE 10 was dropped for lack of a
  complete, maintained implementation set; do not reintroduce it, and never as a profile.
  A new generation is a new `jinahya-persistence-NN` module (see README "Adding a
  generation"), never a profile.
- **There is exactly one profile axis: the persistence provider.** `hibernate-orm`
  (active by default) and `eclipselink` are the only implementation profiles, declared by
  each `jinahya-persistence-NN` parent; they set `persistence-provider` and choose a
  provider, never a version. `eclipselink-weaving` is an add-on to `eclipselink`, not a
  provider. Every implementation version — both providers', Hibernate Validator's,
  Expressly's, Weld's — is a single value in the generation parent's `<properties>`.
- Do not add a profile to test an implementation at a second version. Hibernate ORM 7.2,
  Hibernate Validator 9.0 and the single-choice validator/Weld profiles were removed for
  that reason: varying them tests those projects' release history, not this one. A new
  profile is justified only by a second *implementation* of a spec.

Consequences for convergence work:

- Never pin a `jakarta.*` API artifact (`jakarta.persistence-api`,
  `jakarta.validation-api`, `jakarta.enterprise.cdi-api`, `jakarta.el-api`, …) to an
  explicit version. Let the umbrella BOM decide, and change the platform version in
  one place instead.
- When a provider (Hibernate ORM/Validator, EclipseLink, Weld, Expressly) drags in a
  `jakarta.*` API newer or older than the umbrella BOM, the fix is to align the
  provider version with the platform generation — not to override the API version.
- Check convergence against both providers, not just the default:
  `./_mvn.sh -q enforcer:enforce -Drules=dependencyConvergence` runs each one
  (`dependencyConvergence` is also bound by the inherited `jinahya-parent`).
- Naming a profile of a POM on the command line deactivates that POM's `activeByDefault`
  profiles. With the provider as the only axis that is harmless — `-Peclipselink` names
  the one thing the defaults were supplying — but it is also why no other configuration
  may be parked in an `activeByDefault` profile. `enforce-require-provider-profile` fails
  the build when no provider is active.

### Keeping specs and implementations aligned

Everything below hangs off the platform generation chosen above. When the umbrella
moves, **every row must move with it** — a spec API from one generation running against
an implementation built for another is the most common source of convergence and
runtime failures here.

| Spec (API, from the umbrella BOM) | Implementations used in this build | Version property |
| --- | --- | --- |
| Jakarta Persistence (`jakarta.persistence-api`) | Hibernate ORM (`org.hibernate.orm:hibernate-core`) | `version.org.hibernate.orm` |
| Jakarta Persistence (`jakarta.persistence-api`) | EclipseLink (`org.eclipse.persistence:org.eclipse.persistence.jpa`) | `version.org.eclipse.persistence` |
| Jakarta Validation (`jakarta.validation-api`) | Hibernate Validator (`org.hibernate.validator:hibernate-validator`) | `version.org.hibernate.validator` |
| Jakarta Expression Language (`jakarta.el-api`) | Expressly (`org.glassfish.expressly:expressly`) — required by Hibernate Validator | `version.org.glassfish.expressly` |
| Jakarta CDI (`jakarta.enterprise.cdi-api`) | Weld (`org.jboss.weld*`, `weld-junit5`) | `version.org.jboss.weld` |
| Jakarta JSON Processing (`jakarta.json-api`) | Eclipse Parsson (`org.eclipse.parsson:parsson`) — test scope | `version.org.eclipse.parsson` |
| Jakarta JSON Binding (`jakarta.json.bind-api`) | Eclipse Yasson (`org.eclipse:yasson`) — test scope | `version.org.eclipse.yasson` |
| Jakarta XML Binding (`jakarta.xml.bind-api`, **not** in the EE 11 BOM: `version.jakarta.xml.bind-api` in the generation parent) | GlassFish JAXB runtime (`org.glassfish.jaxb:jaxb-runtime`) — test scope | `version.org.glassfish.jaxb` |

Rules:

- The persistence provider is never hard-coded: `persistence-unit.provider` and
  `metamodel.generator.groupId/artifactId/version` default to Hibernate in the
  generation parent's `<properties>` and are overridden by the `eclipselink` profile;
  `persistence.xml` and the annotation processor path read them. Change the profile, not
  the literal provider class.
- Both persistence providers must stay buildable. A change made for Hibernate has to
  be checked against EclipseLink and vice versa (`./mvnw -Peclipselink test`). A test which
  needs one provider's own API goes under `src/test/java-<persistence-provider>`, not
  behind `testExcludes`.
- Hibernate Validator and Expressly move together: validator 8 ↔ expressly 5,
  validator 9 ↔ expressly 6 (the pom records this pairing in comments). Never bump one
  without the other.
- Weld's version is tied to the CDI API generation the umbrella supplies; if the
  platform ever moves to EE 12, the Weld line must move with it.
- Keep each implementation on the newest release of a series certified for EE 11, and
  refresh `jinahya-persistence-11/README.md` when bumping.
- What the main classes need outside the `test` scope must run on the generation's
  minimum Java SE (`enforceBytecodeVersion`), and the main sources must compile with
  `--release` of that minimum: no Java 18+ API (e.g., `Locale.of`) in `shared/*/src/main`.

One naming trap: `version.org.jboss.weld` holds the **weld-junit5 (weld-testing)**
version, not the Weld version — weld-junit5 5.0.x is the Weld 6 / CDI 4.1 line, which
is why it holds a `5.0.x` value. Verify a resolved version with `dependency:tree` /
`help:evaluate` rather than reading the property name.

The current spec/implementation matrix of a generation is maintained in its own
`jinahya-persistence-NN/README.md`; the generation table, and where the EE 12 line
stands, in `README.md` under "Jakarta EE alignment". Update them whenever the umbrella
BOM or any implementation version changes.

### Detecting

- `./mvnw -q dependency:tree -Dverbose` — `(version managed from ...)` / `omitted for
  conflict with ...` lines mark divergent transitive versions.
- `./mvnw enforcer:enforce -Drules=dependencyConvergence` — fails the build and prints
  every convergence violation with the paths that introduce each version.
- Run it against the whole reactor (from the root), not just the module being edited;
  a version conflict is a property of the aggregated graph.

### When a convergence problem is detected

1. **Do not** silently accept the Maven "nearest wins" resolution.
2. If the artifact comes from the Jakarta EE umbrella (`jakarta.*`), resolve it at the
   platform level (see above). Otherwise pin it in `<dependencyManagement>`: the
   generation parent's, if the artifact implements a Jakarta spec or is otherwise tied
   to a generation, the root `pom.xml`'s if it is the same for every generation.
   Prefer bumping to the highest version required by any path, unless that version
   breaks a consumer.
3. Use `<exclusions>` only when a dependency genuinely must not be on the classpath —
   not as a way to make a version conflict disappear.
4. Re-run the detection commands afterwards and confirm the graph is clean before
   reporting the work as done.
5. If a conflict cannot be resolved without a behavioural change (an API break, a
   downgrade of a transitive requirement), stop and report it rather than choosing a
   version unilaterally.

### When adding or upgrading a dependency

- If the artifact is part of Jakarta EE, take it from the imported
  `jakarta.jakartaee-bom` with no `<version>` at all.
- Otherwise declare the version in the generation parent (an implementation of a Jakarta
  spec) or the root `pom.xml` (anything else), as a `version.*` property and a
  `<dependencyManagement>` entry; leaf modules declare `groupId`/`artifactId` and scope
  only.
- After the change, re-check convergence across the reactor before committing.
