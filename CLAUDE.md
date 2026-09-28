# CLAUDE.md

Guidance for Claude Code when working in this repository.

## Dependency convergence

Dependency convergence is **very important** in this project. A multi-module Maven
build (`jinahya-persistence-*`) that lets the same artifact resolve to different
versions on different classpaths produces failures that only show up at runtime, so
treat any divergence as a build defect, not a warning.

### Origin: the Jakarta EE umbrella platform

Versions in this project originate from the **Jakarta EE platform umbrella BOM**, not
from individual artifacts:

- `jakarta.platform:jakarta.jakartaee-bom` is imported (`<type>pom</type>`,
  `<scope>import</scope>`) in the root `pom.xml` `<dependencyManagement>`, pinned by
  the `version.jakarta.jakartaee-bom` property (currently `11.0.0`).
- **Jakarta EE 11 is the only supported generation.** The platform values live in the
  root `<properties>` — `version.jakarta.jakartaee-bom`, `persistence.version` (3.2),
  `persistence.schemaLocation` — deliberately *not* in a profile, so no profile
  combination can move the platform out from under the implementations. EE 10 support
  was removed; do not reintroduce a platform profile.
- **There is exactly one profile axis: the persistence provider.**
  `jakarta-ee-11-hibernate-orm` (active by default) and `jakarta-ee-11-eclipselink` are
  the only implementation profiles, and they choose a provider, never a version. Every
  implementation version — both providers', Hibernate Validator's, Expressly's, Weld's —
  is a single Jakarta EE 11 aligned value in the root `<properties>`.
- Do not add a profile to test an implementation at a second version. Hibernate ORM 7.2,
  Hibernate Validator 9.0 and the single-choice validator/Weld profiles were removed for
  that reason: varying them tests those projects' release history, not this one. A new
  profile is justified only by a second *implementation* of a spec, and must be named
  `jakarta-ee-NN-<implementation>` for the generation it is certified against.

Consequences for convergence work:

- Never pin a `jakarta.*` API artifact (`jakarta.persistence-api`,
  `jakarta.validation-api`, `jakarta.enterprise.cdi-api`, `jakarta.el-api`, …) to an
  explicit version. Let the umbrella BOM decide, and change the platform version in
  one place instead.
- When a provider (Hibernate ORM/Validator, EclipseLink, Weld, Expressly) drags in a
  `jakarta.*` API newer or older than the umbrella BOM, the fix is to align the
  provider version with the platform generation — not to override the API version.
- Check convergence against both providers, not just the default:
  `./_mvn_jakarta_ee_11.sh -q enforcer:enforce -Drules=dependencyConvergence` runs each one.
- Naming a profile on the command line deactivates every `activeByDefault` profile. With
  the provider as the only axis that is harmless — `-Pjakarta-ee-11-eclipselink` names the
  one thing the defaults were supplying — but it is also why no other configuration may
  be parked in an `activeByDefault` profile.

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

Rules:

- The persistence provider is never hard-coded: `persistence-unit.provider` and
  `metamodel.generator.groupId/artifactId/version` default to Hibernate in
  `<properties>` and are overridden by the `jakarta-ee-11-eclipselink` profile;
  `persistence.xml` and the annotation processor path read them. Change the profile, not
  the literal provider class.
- Both persistence providers must stay buildable. A change made for Hibernate has to
  be checked against EclipseLink and vice versa (`./mvnw -Pjakarta-ee-11-eclipselink test`).
- Hibernate Validator and Expressly move together: validator 8 ↔ expressly 5,
  validator 9 ↔ expressly 6 (the pom records this pairing in comments). Never bump one
  without the other.
- Weld's version is tied to the CDI API generation the umbrella supplies; if the
  platform ever moves to EE 12, the Weld line must move with it.
- Keep each implementation on the newest release of a series certified for EE 11, and
  refresh the README table when bumping.

One naming trap: `version.org.jboss.weld` holds the **weld-junit5 (weld-testing)**
version, not the Weld version — weld-junit5 5.0.x is the Weld 6 / CDI 4.1 line, which
is why it holds a `5.0.x` value. Verify a resolved version with `dependency:tree` /
`help:evaluate` rather than reading the property name.

The current spec/implementation matrix — what each version in this build is, what the
latest released counterpart is, and where the EE 12 line stands — is maintained in
`README.md` under "Jakarta EE alignment". Update that table whenever the umbrella BOM
or any implementation version changes.

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
   platform level (see above). Otherwise pin it in the root `pom.xml`
   `<dependencyManagement>` section — that is the single place versions are decided
   here (see the `<dependencyManagement>` block and the `version.*` properties).
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
- Otherwise declare the version in the root `pom.xml` (`<dependencyManagement>` /
  `version.*` property); child modules declare `groupId`/`artifactId` and scope only.
- After the change, re-check convergence across the reactor before committing.
