# TODO — staging

Working list only. An item lives here while it is being worked out; once it is filed as a
GitHub issue it is **removed from this file**. The backlog itself lives in
[the issue tracker](https://github.com/jinahya/jinahya-persistence/issues).

An item resolved directly in the tree, rather than filed, is **struck through and kept here**
with a note saying what was done — so the record of a decision survives the change that
settled it. Struck items do not count towards the staged total.

**Currently staged: 13 open items** from a redundancy sweep (below), with four more resolved
directly and struck through, plus one closing section of observations that are deliberately
*not* work. All 53 items from the earlier audit were filed as issues #4-#56.

## Labels

| Label | Meaning |
| --- | --- |
| `jinahya-persistence-crypto` / `-more` / `-utils` / `-test-utils` / `-more-test-utils` | the module |
| `cross-cutting` | build, CI, POM, `module-info`; spans modules |
| `bug` / `enhancement` / `documentation` / `test-coverage` | what kind of work it is |
| `severity: high` | data loss, silent corruption, or a broken build |
| `severity: medium` | a real defect with a bounded blast radius |
| `severity: low` | cosmetic, documentation, or cleanup |
| `verified` | reproduced by executing it, not only by reading the source |

Note the two stale module labels, `jinahya-persistence-mapped` and
`jinahya-persistence-mapped-test`: they predate the current module names and are unused by
the issues above. Delete them, or map them onto the modules that replaced them.

## Where the backlog came from

Four independent passes over every module, reconciled against each other: one direct read of
all main and test sources plus the poms, root profiles and CI workflow; and three Codex
audits, each run without sight of this file or of the others.

Agreement across passes was the strongest signal, and the cross-check corrected the record
several times in both directions — including three claims that had already been written down
and turned out to be wrong (an empty-delimiter failure mode, an assumption that `@Size` was
honored by `easy-random-bean-validation`, and a Jakarta Persistence citation that could not be
substantiated). Two Codex passes also corrected their own coverage claims. Each issue carries
that provenance in its footer, and the eight `verified` issues were reproduced by execution or
by inspecting a binary rather than by reading.

## Staged

From a sweep of every `src/main` source in all five modules for redundant code and
unreachable branches, plus a dead-code scan over the test sources. Found by reading, not by
executing: nothing below is labelled `verified`, and each item says what still needs
checking. No source was changed.

### Delete dead declarations with no caller

Each of these has exactly one reference in the repository — its own declaration. Confirmed
with a repo-wide grep that includes javadoc, so nothing links to them either.

- `jinahya-persistence-crypto/src/main/java/com/github/jinahya/persistence/crypto/__EncryptionIdentifier.java`
  — the whole annotation type.
- `jinahya-persistence-more/src/main/java/com/github/jinahya/persistence/more/__SelfReferencingConstants.java:28`
  — `MIN_DEPTH`; the class exists only to hold it.
- `jinahya-persistence-more/src/main/java/com/github/jinahya/persistence/more/__SelfReferencingOrdinalConstants.java:28`
  — `MIN_ORDINAL`; same.
- ~~`jinahya-persistence-test-utils/src/main/java/com/github/jinahya/persistence/test/util/___PersistenceUtils.java`
  — the entire file: a `final class` holding nothing but a throwing private constructor.~~
  **Resolved** — file deleted.
- ~~`jinahya-persistence-more-test/src/main/java/com/github/jinahya/persistence/more/test/__TestTemplateInvocationContext_Utils.java`
  — package-private, and its only method `of(...)` has no callers, so there is no
  published-API reason to keep it.~~ **Resolved** — file deleted.
- `jinahya-persistence-more-test/src/main/java/com/github/jinahya/persistence/more/test/__AttributeConverter_Test.java:212,223`
  — `newAttributeInstance()` / `newDbDataInstance()`.
- ~~`jinahya-persistence-more/src/test/java/com/github/jinahya/persistence/more/temporalinterval/___MappedTemporalInterval_PersistenceTest.java:93`
  — unused private method `constraintViolationOf`.~~ **Resolved** — method and its orphaned
  `ConstraintViolationException` import deleted; the duplication it exposed is staged below.

Two of the seven entries did not in fact have a single reference: `___PersistenceUtils` and
`__TestTemplateInvocationContext_Utils` each had two — the declaration and the private
constructor bearing the same name. Both were dead regardless, and both are gone; the claim
holds for every entry still open.

What is left is exactly the published-API half: a `public` annotation type, two
`public static final` constants and two `protected` methods of a published test-support
module. Decide whether each counts as API before deleting; the package-private and private
entries did not, which is why they could go.

### Drop the unread `attributeClass` field from the two Long converters

`jinahya-persistence-more/src/main/java/com/github/jinahya/persistence/more/converter/__TemporalAccessorLongAttributeConverter.java:139`
and `.../__TemporalAmountLongAttributeConverter.java:124` both declare
`protected final Class<X> attributeClass`, null-check it in the constructor and never read it
— in main, in test, or in any subclass. The sibling *String* converters do read theirs (to
find a static factory), which is presumably where it was copied from.

Removing it changes the protected constructor signature, so it is a source-compatibility
break for anyone subclassing outside this repo.

### ~~`__SecureAttributeConveter` carries a dead copy of the listener's encrypt/decrypt~~

**Resolved** — `encrypt`, `decrypt` and `describe` deleted (47 lines), along with the `logger`
field and `MethodHandles` import they orphaned. `getEncryptionService()` and the injected field
stay. Note this removed two `protected` methods from a published abstract class; judged safe
because every conversion method of both leaves throws `UnsupportedOperationException`. The
misspelled class name is recorded in the class's own `@implNote`.

~~`jinahya-persistence-crypto/src/main/java/com/github/jinahya/persistence/crypto/__SecureAttributeConveter.java:117-156`
— `encrypt`, `decrypt` and `describe` are verbatim copies of
`__EncryptionListener.java:202-247`, and nothing calls them: all four conversion methods of
`OfBytes` and `OfString` throw `UnsupportedOperationException`. `_CRYPTO.md:142` and
`jinahya-persistence-crypto/README.md:189` already record the class as an unimplemented
alternative shape; the copied bodies are the part that can go now.~~

### Two writes of null over null in `__EncryptionService`

`jinahya-persistence-crypto/src/main/java/com/github/jinahya/persistence/crypto/__EncryptionService.java:638`
— `setAttributeValue(object, encryptedAttribute, null)` is reached only on the path where
`encryptedValue == null` was read two lines earlier. `:794` is the mirror image in
`decrypt(...)`: reached only when `decryptedValue == null`.

Harmless with the reflective setter as written, but each one costs a setter invocation per
attribute per call and reads as though it were doing something. Before deleting, confirm no
entity in scope has a setter with a side effect that the write is (accidentally) relying on.

### ~~Redundant null result checks in the Float and Double string converters~~

**Resolved** — but not by deletion. The build runs NullAway at `ERROR` over main sources in
JSpecify mode, so dropping the branch makes `value.floatValue()` a nullable dereference and
fails compilation. The config sets `AssertsEnabled=true`, so the dead branch became
`assert value != null;` — the idiom already used elsewhere in this package. **Any other
"redundant null check" item in this file faces the same constraint.**

~~`jinahya-persistence-more/src/main/java/com/github/jinahya/persistence/more/converter/__NumberStringAttributeConverters.java:205,255`
— `value == null ? null : value.floatValue()` / `.doubleValue()`.
`delegate.convertToEntityAttribute(dbData)` (`OfBigDecimal`, `:87`) returns null only for a
null `dbData`, and `dbData` was rejected as null at the top of the method.~~

### Duplicate `requireNonNull(factory)` in `__EntityManagerFactoryUtils`

`jinahya-persistence-utils/src/main/java/com/github/jinahya/persistence/__EntityManagerFactoryUtils.java:71,88`
null-check `factory` and then call `getPersistenceUnitUtil(factory)` (`:52`), which performs
the identical check. Keeping the outer one is defensible as fail-fast, but it is the only
place in the class that checks twice.

Same pattern, weaker case, in
`jinahya-persistence-more/src/main/java/com/github/jinahya/persistence/more/__SelfReferencingOrderedQueryUtils.java:121-123`:
`entityManager` and `entityClass` are checked, then re-checked by
`__SelfReferencingQueryUtils.selectRoots`.

### Eight unreachable `default` arms in the colour models

`__MappedRgb.java:159,169`, `__MappedHsl.java:137,147`, `__MappedHwb.java:140,150`,
`__MappedCmyk.java:173,184` (all under
`jinahya-persistence-more/src/main/java/com/github/jinahya/persistence/more/colormodel/`)
each read `default -> throw new IndexOutOfBoundsException("index: " + index)`. They switch on
`requireValidComponentIndex(index)`, which is
`Objects.checkIndex(index, getComponentCount())` (`___MappedColor.java:341`) — an
out-of-range index has already thrown.

The four in `setComponent` are switch *statements*: the arm can simply be deleted. The four
in `getComponent` are switch *expressions* over an `int`, where the compiler requires a
default even though it cannot be reached — those can only be annotated, not removed.

### The decrypt ladder tests for primitives the encrypt ladder does not

`jinahya-persistence-crypto/src/main/java/com/github/jinahya/persistence/crypto/__EncryptionService.java:807-822`
— eight `javaType == boolean.class || javaType == Boolean.class` style tests. The encrypt
ladder at `:659-674` has no primitive branches at all, and `validate(...)` at `:433` rejects
any decrypted attribute whose type is primitive.

Needs checking before deletion rather than assuming: the two sides read the type differently
— `validate` uses `Attribute.getJavaType()` (the provider's view) while both ladders use
`__AttributeUtils.getJavaMemberType(...)` (the declared member). A provider that reported
`Integer` for an `int` field would slip past `validate`, and then `encrypt` would throw
`unsupported java type: int` before `decrypt` ever ran. So the arms look unreachable for
anything `encrypt` can have written, but the asymmetry is the thing to resolve: either
`encrypt` should grow the same branches or `decrypt` should lose them.

### The reflective `parse` lookup in `__TemporalAmountStringAttributeConverter` is bypassed

`jinahya-persistence-more/src/main/java/com/github/jinahya/persistence/more/converter/__TemporalAmountStringAttributeConverter.java:72`
resolves `parse(CharSequence)` reflectively and `:108-117` invokes it — but both shipped
subclasses override `convertToEntityAttribute` to call the method directly:
`__TemporalAmountStringAttributeConverters.java:73` (`Duration.parse`) and `:104`
(`Period.parse`). The reflection resolves to exactly those two methods, so the base
implementation, the `parseMethod` field and the `noStaticFactory` throw are all dead for
everything in this repository.

Either drop the overrides and keep the reflective path, or drop the reflective path and make
the base abstract. The overrides exist for a reason worth recovering first — probably
`@Nullable` typing or native-image friendliness.

### `__TemporalAccessorLongAttributeConverter` and `__TemporalAmountLongAttributeConverter` are the same class twice

`jinahya-persistence-more/src/main/java/com/github/jinahya/persistence/more/converter/` — the
two files are identical line for line apart from the type bound (`TemporalAccessor` vs
`TemporalAmount`) and the javadoc. Both take `(attributeClass, encoder, decoder)`, both
null-check all three, both implement the two conversion methods the same way.

`TemporalAccessor` and `TemporalAmount` share no supertype but `Object`, so collapsing them
means an unbounded generic base with the two current classes as thin subclasses — worth it
only together with the dead-`attributeClass` item above, which shrinks both to almost nothing.

### `getTemporalAmount()` is copied four times across the interval models

`jinahya-persistence-more/src/main/java/com/github/jinahya/persistence/more/temporalinterval/`
— `__MappedInstantInterval.java:93`, `__MappedLocalDateTimeInterval.java:91`,
`__MappedLocalTimeInterval.java:143` and `__MappedOffsetDateTimeInterval.java:154` all hold
the same body: null-guard both ends, return `Duration.between(start, end)`.

A `default` method on a `___TemporalInterval` sub-interface for the `Duration`-valued models
would carry all four. Three things that look like blockers were checked and are not:
`___TemporalInterval<T extends Temporal & Comparable<? super T>>` is generic, so one body can
call `Duration.between(getIntervalStart(), getIntervalEnd())` for every point type; the
narrowed `Duration` return type is legal covariance over the interface's `TemporalAmount`; and
`___MappedTemporalInterval` forces `@Access(AccessType.FIELD)`, so a getter moved onto an
interface cannot be picked up as a persistent property — which is what the four `@Transient`
annotations are guarding against today. The `Period`-valued ones (`__MappedLocalDateInterval`,
`__MappedYearInterval`, `__MappedYearMonthInterval`) each differ and stay as they are.

### ~~`__JoinedStringAttributeConverter` accumulates the same strings twice~~

**Resolved** — overtaken by a rewrite of `convertToDatabaseColumn`, which now streams, maps
through the element converter, filters nulls and joins. `converteds`, the `StringJoiner`, the
per-element `contains` guard and the round-trip check are all gone, and with them the
`firstDifference` helper. Keeping a list round-trippable is now documented as the caller's
contract rather than enforced; the class is also `abstract` now.

~~builds `converteds` (an `ArrayList`) and a `StringJoiner` from the same values in the same
loop. `String.join(joiningDelimiter, converteds)` at the end produces the identical result,
so the joiner is pure duplication. Separately, the per-element
`converted.contains(joiningDelimiter)` guard is subsumed by the round-trip check — an element
carrying the delimiter can never split back equal to `converteds`. It survives only for its
more precise error message, which is a real reason to keep it.~~

### Twenty converters re-declare an interface their superclass already implements

`implements AttributeConverter<X, Y>` on every nested class in
`jinahya-persistence-more/src/main/java/com/github/jinahya/persistence/more/converter/`:
`__TemporalAccessorStringAttributeConverters` (10), `__NumberStringAttributeConverters` (5),
`__TemporalAmountStringAttributeConverters` (2), `__TemporalAccessorLongAttributeConverters`
(2), `__TemporalAmountLongAttributeConverters` (1). In each case the superclass already
implements it through `__StringAttributeConverter` / `__LongAttributeConverter`.

`__ChainingAttributeConverter` and `__AttributeEnumConverter` are the two legitimate direct
implementations and stay. Check first whether any persistence provider's `@Converter`
scanning cares about the directly-declared interface before stripping these — if it does,
that belongs in a comment instead.

### `constraintViolationOf` is declared three times across the persistence tests

The same private helper — run a `Runnable`, walk the cause chain, return the
`ConstraintViolationException`, throw an `AssertionError` otherwise — is declared in
`jinahya-persistence-more/src/test/java/com/github/jinahya/persistence/more/colormodel/___MappedColor_PersistenceTest.java:112`
and
`.../__SelfReferencingOrdered_PersistenceTest.java:99`. Both copies are used. A third copy in
`.../temporalinterval/___MappedTemporalInterval_PersistenceTest.java` had no caller and was
deleted; that is how the duplication surfaced.

The comment both carry — Hibernate throws it unwrapped where EclipseLink wraps it — is the
reason the helper exists at all, so it belongs in one place. `jinahya-persistence-more-test`
already publishes test support for this module.

### `__RangeBounds` duplicates the bound characters `__BoundType` already holds

`jinahya-persistence-more/src/main/java/com/github/jinahya/persistence/more/orderedrange/__RangeBounds.java:104-107`
stores its own `lowerBoundCharacter` / `upperBoundCharacter`, which are exactly the
characters `__BoundType` carries. `of(lowerBoundType, upperBoundType)` (`:90-100`) then
matches the two enums *by character* and falls through to an `AssertionError` that only a
character mismatch could reach.

Holding the two `__BoundType` values directly would make the factory a 2x2 lookup, delete
the duplicated character state and delete the unreachable `AssertionError` with it.

### ~~One codec in `__EncryptionServiceUtils` does not delegate like its siblings~~

**Resolved** — `local_time_8(byte[])` now calls `local_time_8(b, 0)`.

~~`jinahya-persistence-crypto/src/main/java/com/github/jinahya/persistence/crypto/__EncryptionServiceUtils.java:626`
— `local_time_8(byte[])` inlines `LocalTime.ofNanoOfDay(long_8(b, 0))` rather than calling
the private `local_time_8(b, 0)` twenty lines above it. Every other codec in the file
delegates. Same result today; it is the one place where the pair could drift apart.~~

### Redundant work in the colour conversions

- `jinahya-persistence-more/src/main/java/com/github/jinahya/persistence/more/colormodel/___MappedColorUtils.java:193`
  — `rgbToHwb` runs a full `rgbToHsl` and discards `s` and `l`; only `h` is used. The hue can
  be computed on its own for a third of the arithmetic.
- `.../___MappedColorUtils.java:344-349` — the first three `MIN_COMPONENT` entries of
  `values[]` are always overwritten by the loop below (`count` is 3 or 4 for every accepted
  notation length). Only `values[3]`'s `ALPHA_OPAQUE` default is load-bearing; the other
  three read as though a shorter notation could leave them in place.

### Two low-priority observations, probably deliberate

Recorded so the next sweep does not re-raise them, not as work:

- Around forty `@Column` / `@Basic` attributes are spelled out at their default value
  (`insertable = true`, `updatable = true`, `nullable = true`, `optional = true`) across the
  colormodel, orderedrange and temporalinterval mapped superclasses and the crypto test
  entities. Given how much `insertable = false` matters in the crypto module, the
  explicitness reads as intentional.
- Roughly thirty-five imports are referenced only from javadoc `{@link}` tags. Not removable
  without breaking the links.
- `__SecureAttributeConveter.getEncryptionService()` has no caller in the repository, now that
  the dead `encrypt`/`decrypt` copies are gone. It stays: it is the injection hook the class
  exists to hold, and the class is documented as an unimplemented alternative shape whose
  conversion methods throw. A caller appears when that shape is implemented.

One genuine defect surfaced alongside them and does **not** belong in this file's
redundancy list — file it separately:
`jinahya-persistence-more/src/main/java/com/github/jinahya/persistence/more/__SelfReferencingOrdered.java:112-114`
annotates `getSiblingOrdinal()` with both `@Nullable` and `@NotNull`.

## Adding to this file

Stage a new item here as `### <short title>` with enough detail to act on — the mechanism,
file:line anchors, a concrete failure scenario, and how it was verified. File it as an issue
when it is ready, then delete it from here.
