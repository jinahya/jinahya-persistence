# `jinahya-persistence-crypto` — completeness assessment

A fresh audit of what the module **covers**, not of what it does. Read against the working
tree on branch `sketch`. Items marked **[verified]** were reproduced by executing code;
everything else is read from source.

The module's own `README.md` is accurate and already states most of the limits below. This
document is a second opinion on *completeness* — where the coverage envelope ends, and
which of those edges are loud and which are silent.

---

## 1. What `@__EncryptedAttribute` actually reaches

This is the question that matters most, because the annotation looks transitive and is not.

| Attribute kind of the annotated member | Annotated **directly** | The kind **contains** an annotated attribute |
| --- | --- | --- |
| `BASIC` | encrypted | n/a |
| `EMBEDDED` | **rejected** — *"an @Embedded attribute cannot itself be encrypted"* | **descended into**, recursively, at any depth, cycle-guarded |
| `ELEMENT_COLLECTION` | **rejected** — *"only a BASIC attribute can be encrypted"* | **silently ignored** ⚠ |
| `ONE_TO_ONE`, `MANY_TO_ONE`, `ONE_TO_MANY`, `MANY_TO_MANY` | **rejected** — same message | not traversed (by design — see below) |

The whole traversal is `__EncryptionService.validate(...)`. Its loop has exactly three
outcomes per attribute: `EMBEDDED` descends, an annotated non-`BASIC` throws, and
**everything else falls through `if (annotation == null) continue;`**. That single
`continue` is the entire boundary of the module.

### The one dangerous cell

An embeddable used by an `@ElementCollection` is never visited, so an
`@__EncryptedAttribute` *inside* it is never even read. No validation, no exception, no
log line — and the plaintext is persisted to the collection table.

**[verified]** A probe entity holding `@ElementCollection List<_SecretEmbeddable>` was run
through `service.encrypt(entity)`. After the call:

```
note=TOP-SECRET   noteEnc=null
```

The plaintext survived untouched and no ciphertext was produced. Compare the same
embeddable reached through a plain `@Embedded`, where `note` is nulled and `noteEnc__` is
populated. The failure mode is the worst available one: the developer wrote the
annotation, so they believe the value is encrypted.

This is listed in `README.md` under **Not supported**, so it is known — but nothing in the
build pins it, and the module rejects a dozen far less dangerous mistakes loudly. A
validation-time rejection of *any* `ELEMENT_COLLECTION` whose element type carries
`@__EncryptedAttribute` would close it with the machinery already present.

### Relations are correctly *not* traversed

A `@ManyToOne Child` is not descended into, and that is right: `Child` is an entity with
its own lifecycle callbacks, so it encrypts itself when it is persisted. The module does
not need to reach across the association. The completeness caveat is only that this is
silent — a `Child` that carries `@__EncryptedAttribute` but is missing
`@__EncryptedEntity`, or whose listener is not registered, is skipped without a word.

---

## 2. Three tiers of enforcement

Sorting the module's guarantees by how loudly they fail is more useful than listing them.

**Enforced — rejected before any value is moved.** The plaintext attribute must be
`BASIC`, non-primitive, optional, neither `@Id` nor `@Version`, and its column must be
provably `insertable = false` and nullable. The ciphertext attribute must exist, be
`BASIC`, be `byte[]`, be optional, be insertable + updatable + nullable, not be the
plaintext attribute, not be an id/version, not itself be annotated, and not be paired
twice. `MappingFlag.UNKNOWN` — which is what an XML-mapped column yields, since only
annotations are read — is rejected alongside an explicit `NO`, so the unknown case fails
safe. This tier is genuinely thorough.

**Silent but correct.** Unannotated attributes of any kind; related entities; a null
embeddable; an attribute already encrypted (plaintext `null`, ciphertext present).

**Silent and load-bearing on the developer.** The `@ElementCollection` hole above; an
entity missing `@__EncryptedEntity`; a listener not registered; validation never running
at all (next section).

---

## 3. Validation is lazy, so the enforced tier is only as complete as your test coverage

`getMapping(...)` is reached only from `encrypt(Object)` and `decrypt(Object)`. There is no
eager pass — `onStartup(@Observes Startup)` and `@PostConstruct` only log. An entity class
that is never persisted or loaded is never validated, so every rejection in tier one fires
on first use in production rather than at deployment.

This matters more than it looks: the module's central safety property (*a plaintext column
can never be written*) is checked per entity, lazily. An entity added later, or exercised
only on a rare code path, carries no guarantee until that path runs. A startup pass over
`metamodel.getEntities()` filtered by `@__EncryptedEntity` would turn the whole tier-one
list into deployment-time errors using code that already exists.

---

## 4. The listener is a scaffold, not a policy

`__EncryptionListener`'s seven callbacks all only log; a subclass must override and call
`encrypt`/`decrypt`. No ready-made subclass ships, so **every downstream re-derives the
wiring**, and the correct wiring is not obvious — `README.md` has to spend three
subsections explaining that you encrypt from `@PrePersist`/`@PreUpdate` and never from the
post-callbacks, that reading an entity dirties it, and that a flush empties your own
instance. That knowledge currently lives only in prose and in
`_LifecycleListener` (a test class).

Note also that the two entry points are gated differently:

- `__EncryptionListener.encrypt/decrypt` check `@__EncryptedEntity` and skip silently
  when it is absent.
- `__EncryptionService.encrypt(Object)` / `decrypt(Object)` — both `public` — do **not**
  check it and will process any managed type.

So the annotation is a listener-level filter, not a module-level invariant.

---

## 5. Type coverage is complete

Checked against the Jakarta Persistence 3.2 §2.6 enumeration the class javadoc links to:
all 8 wrappers, `String`, `UUID`, `BigInteger`/`BigDecimal`, all 7 `java.time` types,
`java.util.Date`/`Calendar`, all 3 `java.sql` types, `byte[]`/`Byte[]`/`char[]`/
`Character[]`, enums, and the `Serializable` fallback. **No gap.** Primitives are
deliberately excluded (encrypting nulls the attribute), and rejected explicitly.

Two cosmetic inconsistencies remain in the codec layer, filed as
[#59](https://github.com/jinahya/jinahya-persistence/issues/59): `boolean_1` exists and is
tested but the service inlines it, and `byte_1` does not exist. Neither affects coverage.

Types outside §2.6 that providers accept natively — `ZonedDateTime`, `Duration` — fall
through to the `Serializable` branch and round-trip via the JDK serial form rather than a
compact codec. Correct, but opaque and bulkier than the neighbouring encodings.

---

## 6. Declared but unimplemented surface

| Element | State |
| --- | --- |
| `__SecureAttributeConveter` (`OfBytes`, `OfString`) | all four conversion methods throw `UnsupportedOperationException` |
| `@__EncryptionIdentifier` | declared, never read |
| `__EncryptedEntity.encryptionIdentifierAttribute()` | declared, never read; the identifier comes only from `__EncryptionManager.getEncryptionIdentifier(Object)` |

`@__EncryptedEmbedded` was in this list and was **removed** in this session: descent is
unconditional, so the annotation could only ever have subtracted coverage.

The identifier hooks are the one place where implementing something would *add* a
capability. Today the identifier is re-derived from the entity instance on both paths, and
because `encrypt()` nulls the plaintext, it can only key off state that both survives the
round trip and never changes for the life of the row —
[#61](https://github.com/jinahya/jinahya-persistence/issues/61). Downstream must otherwise
self-frame the scheme inside the ciphertext, as the reference `_EncryptionManager` does
with its `iv || key || aad || ciphertext` layout.

### The identifier stays a `String` — shape it downstream

Decided; no code change. A typed `__EncryptionManager<T>` was weighed and rejected. The
genericity itself is tractable — a wildcard field plus one capture point keeps `<T>` out of
`__EncryptionService`'s public API and out of `__EncryptionListener` entirely — but two
things argue against it. CDI resolution is the visible cost: a raw injection point like the
one at `_EncryptionService:14-16` does **not** satisfy a `__EncryptionManager<KeyRef>` bean
(CDI 4.1 §2.4.2.1), so every downstream raw `@Inject` would fail at deploy time. The
deciding cost is that the identifier's natural end state is a *column* — that is what
honoring `encryptionIdentifierAttribute()` means — and a `String` is what a column holds.

So the structure belongs inside implementations, rendered to `String` at the boundary.

**Two layers, not one.** This is the part that is easy to get wrong. The identifier answers
*whose key*; the ciphertext frame answers *which generation of it*. Rotation must never
change the identifier — only the ciphertext travels with the row, so only the ciphertext can
carry a version.

```java
/** What a key is selected by. Rendered to the String the manager exchanges. */
record KeyRef(String scope, String subject) {

    KeyRef {
        // a blank identifier silently resolving to a default key is the hazard @NotBlank exists to stop
        if (scope.isBlank() || subject.isBlank()) {
            throw new IllegalArgumentException("blank component");
        }
        // "a" + "b/c" and "a/b" + "c" must not render alike
        if (scope.indexOf('/') >= 0 || subject.indexOf('/') >= 0) {
            throw new IllegalArgumentException("component contains the delimiter");
        }
    }

    String render() {
        return scope + '/' + subject;
    }
}
```

- **`scope`** — the class of data protected: `"pii"`, `"payment"`, or the entity type. Lets
  one class be rotated or revoked without touching the others.
- **`subject`** — whose data: tenant id, owner id. This is what makes per-tenant keys and
  crypto-shredding possible — destroy the tenant's key and their rows become unreadable
  without touching a row.

**Deliberately absent**, and each for a reason:

| Not in the `KeyRef` | Why |
| --- | --- |
| key version / epoch | belongs in the ciphertext frame; putting it here is exactly the #61 failure |
| algorithm, mode, IV, tag | same — the frame carries them, as `_EncryptionManager` shows |
| anything mutable (status, plan, region) | changes under the row and orphans the ciphertext |
| the row's own encrypted values | `null` on the decrypt path |
| **the entity id** | see below |

**The entity-id trap.** The id looks like the ideal immutable key, and it is the one field
that is reliably wrong. With `GenerationType.IDENTITY` — which every entity in this
module's test tree uses — the id is assigned by the `INSERT`, and `@PrePersist` fires at
`persist()`, before it. An identifier derived from the id is therefore `null` when the row
is encrypted and populated when it is read back: a guaranteed mismatch. Worse, it is
invisible to the obvious test, because calling `service.encrypt(...)` then
`service.decrypt(...)` on one un-persisted instance sees `null` both times and round-trips
cleanly. It only fails through the real lifecycle. Per-row keys are usually the wrong shape
anyway — one key per row is a KMS problem, not a design.

---

## 7. What is not proven

- **The `@ElementCollection` gap is documented but untested.** A safety-relevant claim with
  nothing pinning it. It took ~20 lines to reproduce.
- **Second-level cache.** Both test persistence units set `shared-cache-mode` to `NONE`.
  Behaviour on a cache hit is unexamined.
- **Deep embeddable nesting.** Recursion supports arbitrary depth; no test nests more than
  one level — both test embeddables hold only basic attributes.
- **Property access.** Every crypto test entity is `@Access(AccessType.FIELD)`, inherited
  as a workaround from `jinahya-persistence-more` rather than chosen. **[verified]** all
  nine annotations can be removed with 66/66 tests still green on both Hibernate and
  EclipseLink, so nothing here depends on field access — but it also means
  `__AttributeUtils.getSetter` and its `SetterKey` caching, which exist solely for
  property-access entities, are exercised by no test in this module.
- **Concurrency.** `managedTypes`/`mappings` are `ConcurrentHashMap` and `resolve` uses
  `putIfAbsent`, so the caches look sound, but nothing tests concurrent first-use.

---

## 8. Structural limits the design cannot remove

These are properties of doing application-level encryption through JPA callbacks, not
defects, and `README.md` documents each. Listed here because they bound completeness more
than any missing feature does.

- **Reading an entity writes it.** `@PostLoad` runs after the loaded-state snapshot, so a
  read-only transaction still issues an `UPDATE`, bumps `@Version`, and rewrites the
  ciphertext with a fresh IV. Optimistic locking is unusable; every read reaches
  replication, CDC and audit; a read-only connection fails.
- **INSERT confidentiality only.** `insertable = false` closes the `persist()`-to-flush
  window on both providers, but on EclipseLink a late assignment is then *dropped* rather
  than stored.
- **The instance is emptied by the flush** unless the application re-decrypts, which
  dirties it again.
- **No querying.** Predicates, orderings and scalar projections against a plaintext column
  see `NULL`; bulk JPQL `UPDATE`/`DELETE` bypass callbacks entirely.

---

## Summary

Within its envelope — `BASIC` attributes on an entity, and on embeddables reached by
`@Embedded` — the module is complete and the validation is unusually thorough. The
envelope itself has one hole worth closing (`@ElementCollection`) and one structural
weakness (validation is lazy, so tier-one guarantees are contingent on runtime coverage).
Everything else described as missing is either correctly delegated to the owning entity, or
declared-and-documented as unimplemented.
