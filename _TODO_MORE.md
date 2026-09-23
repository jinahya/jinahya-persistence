# TODO — `jinahya-persistence-more`, what to build next

Staging for **new value families** in `jinahya-persistence-more`, as opposed to `_TODO.md`, which
stages defects. Nothing here is filed yet. Same rule as that file: an item lives here while it is
being worked out, and is deleted once it becomes a
[GitHub issue](https://github.com/jinahya/jinahya-persistence/issues).

The module has three families today — `colormodel`, `temporalinterval`, `orderedrange` — plus
`converter` and the self-referencing types. The question this file answers is what a fourth is
worth being.

## The bar

Everything here was ranked by two filters, and the second one did most of the work:

1. **Is the naive mapping wrong?** A value worth a package is one a schema gets wrong by hand — a
   composite whose columns constrain each other, or a single column whose encoding has to preserve
   an order. A value that is one column and one type belongs in `converter`, or nowhere.
2. **Does a mature library already own it?** If Hibernate, Moneta, geolatte or Hibernate Validator
   already covers the ground, this module adding a second answer helps nobody. See
   [Considered and set aside](#considered-and-set-aside) — the list is longer than the list of
   candidates, which is the point.

What survives both filters clusters around what this module actually has that others do not: an
**order-preserving encoding discipline**, and a **provider-portable, pure-Jakarta-Persistence
surface** — `@MappedSuperclass` and `AttributeConverter`, working identically on Hibernate and
EclipseLink.

## Candidates, ranked

### 1. Ship the encoders `__MappedOrderedRange` demands

The package names this gap itself. `__MappedOrderedRange.encode` requires an encoding that is
**order-preserving** and **prefix-free**, and `orderedrange/package-info.java` admits what that
costs: *"free for a fixed-width ISO form and real work for a number, which needs a fixed width and
a sign scheme both"*, and then *"Nothing checks it … Verify an encoder in a test of the encoder."*

So today every consumer hand-rolls the hard half, unverified, and a mistake does not fail — it
returns the wrong rows.

- Ship a tested encoder per domain: `Integer`/`Long` (offset-binary, or a sign flag plus fixed
  width), `BigDecimal` (scale *and* sign both bite), `Instant`, `Duration`, and the trivial ISO
  ones for the `java.time` point types.
- Ship the contract test with them, in `-more-test`, so a downstream encoder can be checked too:
  it has to hammer the encoder across the domain — including the sign boundary, the width boundary
  and the prefix case — not sample one pair per row, and it has to be explicit that it compares by
  UTF-16 code unit where the column compares by its collation.
- Highest leverage of anything in this file: no new concept, no new package, and it turns
  `orderedrange` from a frame into something usable out of the box.

### 2. Materialized path, alongside the adjacency list

`__SelfReferencing` is a parent pointer, which is the one shape that cannot answer *all
descendants* without recursion or a provider-specific recursive CTE. A path column answers it as a
prefix scan on an ordinary index.

- Same competence as item 1: the separator must be outside the segment alphabet, segments are
  fixed-width or escaped, depth is derived from the path rather than stored twice, and a move
  rewrites a subtree.
- Extends a package the module already owns rather than opening a domain, and the two shapes are
  complementary — adjacency for writes and integrity, path for reads.
- No portable-Jakarta-Persistence library covers it.

### 3. A lexical order key

`__SelfReferencingOrdered` orders siblings by an ordinal, so a single reorder rewrites the tail of
the sequence.

- A fractional rank — `between(a, b)` yielding a key strictly between two others, rebalancing only
  when keys grow long — makes a reorder one `UPDATE`.
- Item 1's competence once more, which is why these three belong together.
- Not shipped by any JPA library as far as this survey found.

### 4. Portable converters for the JDK value types

Not novelty — **provider parity**. Hibernate has native basic types for `UUID`, `Locale`,
`Currency`, `ZoneId`, `URL`, `InetAddress`; EclipseLink largely does not, and `@Convert` is the
standard surface that works on both.

Worth having in `…more.converter`:

- `UUID`, in both the 36-character and the 16-byte forms — with a word on which versions sort in
  binary and which do not.
- `Locale`, via `toLanguageTag` (BCP 47), never `toString`.
- `Currency`, `ZoneId`/`ZoneOffset`, `URI`, `InetAddress`.
- `byte[]` as hex or Base64.
- An `EnumSet` converter over `__AttributeEnum`, joining the **declared** values — which is what
  that interface exists for — composing with `__JoinedStringAttributeConverter`.

Cheapest item here, and it lands in an existing package.

### 5. A zoned future point — `LocalDateTime` + `ZoneId`

An appointment in November is not an instant. An offset is a *result* of a zone and a date, and the
tz database moves; storing `OffsetDateTime` freezes an offset that may be wrong on arrival, and
storing `Instant` throws away the zone and cannot re-derive it.

- Two columns, the instant derived on read.
- The near-neighbour is Hibernate's `@TimeZoneStorage`, and it stores the **offset**, on one
  provider. A zone id is what survives a tz update, and portability is the gap.
- Sits naturally next to `temporalinterval`. Note that bitemporal — an application-time period plus
  a system-time period — already falls out of that package through the embeddable form, and needs a
  worked example rather than a package.

### 6. IP address and CIDR as a range

An address is an ordered domain whose order-preserving encoding is item 1 again — fixed width,
IPv4-mapped v6 so both families sort together — and a network *is* a range, so containment becomes
a range predicate on an ordinary index.

PostgreSQL has `inet`/`cidr` natively; portable Jakarta Persistence has nothing.

### 7. Recurrence — RFC 5545, with a materialized next occurrence

ical4j owns the rule arithmetic. Nobody owns the persistence problem: rule text alone cannot be
queried, so *what is due* needs a maintained next-occurrence column and a stated rule for
refreshing it.

Composes with `temporalinterval` — a recurrence is an interval that repeats. Considerably more work
than the six above; last of the real candidates.

## Considered and set aside

Kept here so the same ground is not resurveyed.

### Money — amount + currency

**Set aside: Moneta (JSR-354) owns it.** The modelling content was real — the scale is not free but
the currency's minor-unit exponent (JPY 0, USD 2, BHD 3, and the non-decimal leftovers), the
minor-unit `long` is derived rather than a second truth, and single-currency-column versus per-row
are two different classes — but it is content JavaMoney already carries, and a second answer helps
nobody.

### A geographic point, and a bounding box

**Set aside: hibernate-spatial and geolatte own it.** The parts that were interesting anyway, for
the record: latitude is bounded and must be rejected outside ±90 while longitude *wraps* and must be
normalized — two constraints that look alike and are not; ISO 6709 orders lat-then-lon and GeoJSON
orders lon-then-lat; and a bounding box is two `orderedrange` ranges, the longitude one being the
case where `lower > upper` is legitimate rather than a bug.

### Audit columns — created/modified at/by

**Set aside: Hibernate Envers, and every framework's own.** The `by` half additionally needs a
principal source, which drags CDI in for what is otherwise four columns.

### Soft delete

**Set aside: too small to be a package.** Hibernate 6.4 has `@SoftDelete` with no EclipseLink
counterpart, so the portable answer is a state model plus a `Predicate` helper and an honest note
that applying the filter stays the caller's job. Not nothing, but not a family.

### Check-digit identifiers — IBAN, ISBN, EAN/GTIN, Luhn

**Set aside: Hibernate Validator already ships the constraints** (`@CreditCardNumber`, `@EAN`,
`@ISBN`, `@LuhnCheck`, `@Mod10Check`, `@Mod11Check`). Normalization on the way into the column is
the only remaining sliver, and it is a converter, not a package.

### JSON columns

**Set aside: native in Hibernate 6**, and hibernate-types before it.

### Quantity — value + unit

**Set aside for now, not permanently.** Structurally it is `colormodel` again: a unit enum per
dimension, one canonical base unit, every conversion through it — exactly as every colour goes
through sRGB. Indriya (JSR-385) owns the *values*, and no JPA mapping for them was found, so the
gap may be real. But the dimension scope is a rabbit hole; nail the scope — length, mass, data size,
and nothing else — before any code, and not before the seven candidates above.

### Postal address, and person name

**Set aside: no anchor.** Both recur constantly and neither has a universal model to follow, so the
result would be bikeshed all the way down.

## Adjacent, surfaced while surveying

### The colour package said embeddables were impossible — resolved, not filed

`colormodel/package-info.java` claimed that Jakarta Persistence *"does not portably let an
`@Embeddable` extend a `@MappedSuperclass`"*, while `__MappedOrderedRange` and
`___MappedTemporalInterval` documented exactly that as the supported second form and pinned it with
tests. The claim was written in the commit that created the package (`9170963`, "wip") and never
re-checked. It was wrong: the obstacle was not the spec but a missing annotation on one class —
`___MappedColor` was the only mapped superclass in the module without an `@Access` of its own, and
EclipseLink NPEs in `EmbeddableAccessor.preProcessMappedSuperclassMetadata` on exactly that.

Fixed rather than filed, since the fix was two annotations and the evidence was a test:
`@Access(AccessType.FIELD)` on `___MappedColor`, `@Access(AccessType.PROPERTY)` on
`_PropertyAccessRgbaEntity` (the cost, on ORM 7.2 only — that series propagates the root's access
type onto the entity), an `_RgbEmbeddable` and a `_ThemeEntity` carrying two colour triples, and
`EmbeddableTest.twoColorsInOneTable__` in the package's persistence test. All six EE 11
combinations pass across the reactor. The package-info, `___MappedColor`, both property-access
entities' javadoc and the README's 0.5.5 upgrade note were rewritten to match.

---

*Surveyed 2026-09-23 against the module's own sources and the open issue list; neither #62 nor any
other open issue stakes out a new value family.*
