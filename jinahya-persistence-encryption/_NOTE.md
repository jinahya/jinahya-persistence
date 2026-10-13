# Notes

## `Serializable` as a plaintext type

> **Decided (#99):** `Serializable` and `java.util.Calendar` are no longer supported. What follows is the analysis
> behind it.

Jakarta Persistence 3.2 §2.6 lists "any other type that implements `Serializable`" as a basic type; without
encryption, a provider writes its Java serialization to a binary column. Supporting it here mirrors what a plain
mapping allows. From the encryption side, though, it has more problems than any other type.

| problem | why it is worse with encryption |
|---|---|
| **Security** (deserialization) | the same risk a plain mapping has; authenticated encryption narrows it, but the module adds no filter of its own |
| **Class evolution breaks stored rows** | renaming or moving the class, or a changed `serialVersionUID`, makes every row unreadable; a plain column could at least be inspected or repaired in SQL, here the bytes are encrypted and recovery needs application code to decrypt and re-encrypt |
| **Unstable bytes** (#91) | the same value can serialize to different bytes, so "unchanged" can't be decided from the encoding |
| **The `T[]` fallback** (#92) | an unresolved generic array silently becomes `Serializable`, which affects the stored format |
| **Late failure** | validation only checks `implements Serializable`; a non-serializable field in the graph fails at the first write, not at startup |
| **Size** | serialized objects are large (about 1.4 KB for a `GregorianCalendar`), plus the encryption overhead |
| **Opaque** | the one codec whose format isn't ours to define or document; it is Java's |

Every other codec has a fixed, documented encoding which decoding validates. `Serializable` brings Java's format,
Java's security model and Java's class-evolution rules into stored, encrypted data. #98 proposes dropping it; the
trade-off is spec coverage against everything above.

### Security

Decoding calls `ObjectInputStream.readObject()` on the decrypted bytes, which instantiates whatever classes the stream
names and runs their `readObject` / `readResolve`:

- **code execution through gadget chains**: a crafted stream combines classes already on the classpath whose
  deserialization does something harmful;
- **denial of service**: huge arrays, deep nesting or many references exhaust memory or CPU.

Exposure: crafted bytes have to get through decryption first. With authenticated encryption (AES-GCM, say) in the
`EntityEncryptionService`, only a key holder can produce a ciphertext which decrypts; that is the main protection, and
it lives in the service. What remains is an untrustworthy key holder (another service, a leaked key) or a service
which doesn't authenticate. Today the module relies only on a JVM-wide `jdk.serialFilter`, if the application set one.

Options:

| option | what it does | cost |
|---|---|---|
| a) resource limits | a per-stream `ObjectInputFilter` capping depth, references, array length and total bytes | stops denial of service, not gadget chains; needs sensible defaults |
| b) filter from the service | a hook such as `default ObjectInputFilter serialFilter(Class<?> expectedType)` on `EntityEncryptionService`, applied to each stream | the application decides which classes are allowed, per attribute type; the default can keep today's behavior |
| c) require a filter | refuse to decode `Serializable` unless a JVM-wide filter, or the hook of b), is configured | safe by default; breaks setups without one (fine pre-1.0) |
| d) drop `Serializable` (#98) | no deserialization at all | removes the problem, and the type |

An allowlist derived from the declared type alone (allow only `com.example.Payload`) doesn't work: the graph also holds
field types, collection classes and the `[B` / `[I` arrays, so it would reject legitimate values. The class decision
has to come from the application, as in b).

Leaning: b) with a), the application supplying the class filter and the module always adding resource limits; or c)
on top of them to be safe by default; or d) if `Serializable` isn't worth keeping.

### `Calendar` through `Serializable`

An idea for #92's `Calendar` half: drop the dedicated `Calendar` codec and let `Calendar` fall through to
`Serializable`. Measured on this JDK, each round-trips completely, `equals` included:

| calendar | serialized | class and zone after |
|---|---|---|
| `GregorianCalendar` | 1,437 bytes | `GregorianCalendar`, `Asia/Seoul` |
| `sun.util.BuddhistCalendar` | 1,477 bytes | `BuddhistCalendar`, `Asia/Seoul` |
| `JapaneseImperialCalendar` | 1,417 bytes | `JapaneseImperialCalendar`, `Asia/Seoul` |

A `jdk.serialFilter` then has to allow `java.util.Calendar`, `GregorianCalendar`, `TimeZone`, `SimpleTimeZone`,
`sun.util.calendar.ZoneInfo` and `[B [I [J [Z`. The idea stands or falls with `Serializable` itself, so it waits on the
security decision above.

## A payload header (undecided)

The payload used to start with a two-byte header (#75): a format version, then the id of the codec which wrote the
value. It was removed: a value is now encoded and decoded by the attribute's current declared type, and nothing about
the type is stored. Whether to bring a header back is open.

What a header gives:

- **a type change fails loudly.** An attribute whose declared type changed after rows were written reads "encoded by
  `LONG_8`, decoded by `DOUBLE_8`" instead of a wrong value. Without it, a change between encodings of the same width
  (`Long` → `Double`, `Integer` → `Float` / `Year`, `LocalDate` → `LocalTime` / `Long`) is misread silently, and the
  next write stores the wrong value; one of different widths fails only on length, with a vaguer message;
- **the encoding can evolve.** A version byte tells old and new payloads apart, so an encoding can change without
  misreading what is stored;
- **the reason is in the payload.** With encryption, the database can't help: the column is ciphertext, untyped, and
  can't be inspected or migrated in SQL. The payload is the only place which can say what wrote it.

What it costs:

- two bytes per value, before encryption;
- codec ids become stored data: never renumbered, never reused, retired ones remembered;
- the type-to-codec mapping (`Codec`, `codecOf`) next to the encoders and decoders.

Without one, the rule is documented instead (README, "No type information is stored"): changing an encrypted
attribute's declared type, or an encoding, is a breaking change which needs the rows migrated first.

