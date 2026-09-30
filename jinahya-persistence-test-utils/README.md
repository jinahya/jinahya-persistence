# jinahya-persistence-test-utils

![Maven Central Version](https://img.shields.io/maven-central/v/io.github.jinahya/jinahya-persistence-test-utils)
[![javadoc](https://javadoc.io/badge2/io.github.jinahya/jinahya-persistence-test-utils/javadoc.svg)](https://javadoc.io/doc/io.github.jinahya/jinahya-persistence-test-utils)


A module for testing classes/interfaces created with `jinahya-persistence`.

Package: `com.github.jinahya.persistence.test.util`

## Roles

A target class is randomized, and then persisted, by two collaborating roles, each located for the target class by
convention.

| role              | interface          | skeletal implementation     | comes from                                                                          | when none is located                    |
|-------------------|--------------------|-----------------------------|-------------------------------------------------------------------------------------|-----------------------------------------|
| fill it with data | `ObjectRandomizer` | `AbstractObjectRandomizer`  | [`jinahya-object-randomizer`](https://github.com/jinahya/jinahya-object-randomizer) | `newRandomizedInstanceOf` returns empty |
| persist it        | `EntityPersister`  | `AbstractEntityPersister`   | this module                                                                         | `newPersisterInstanceOf` returns empty  |

Each role is an interface: `ObjectRandomizer` is a `Supplier<T>`, `EntityPersister` a
`BiFunction<EntityManager, T, T>`. The skeletal class beside it is a convenience, not the role — it remembers the
target class, and brings the behavior most implementations want (the exclusions and the engine, for the randomizer; a
plain `persist`, for the persister). A helper which owes nothing to that machinery implements the interface directly,
and the convention locates it just the same.

Both halves are needed to persist: `newPersistedInstanceOf` throws `IllegalArgumentException` when either is missing.

The randomizer role, and the three engine flavors behind it, live in `io.github.jinahya:jinahya-object-randomizer`,
which this module depends on at compile scope; it is on the classpath of anything that depends on this module, and
brings nothing of its own along, every dependency it declares being `provided` or `test`.

Each role has a `*Utils` class — `ObjectRandomizerUtils` and `EntityPersisterUtils` — which locates it by the convention
and applies it. The convention is the only mechanism: a helper is found because of how it is named, and a class the
convention cannot reach — a local or an anonymous class — has no helper.

The convention spans source sets: an entity in `src/main/java/com/foo/Foo.java` and its `FooRandomizer` in
`src/test/java/com/foo/` are the same package, and both are on the test classpath.

## Convention

For a target class `Foo`, `randomizerClassOf` probes, in order:

1. `FooRandomizer` — declared beside `Foo`
2. `Foo_Randomizer` — declared beside `Foo`

and `persisterClassOf` likewise for `Persister`. That is the whole rule, and it is identical for both roles.

A candidate is passed over, rather than failed on, when it does not exist, when it cannot be loaded, and when it does
not implement the role its name claims — the probe continues with the next postfix, and the empty `Optional` it ends
with carries no reason. Each of the last two is logged at `WARNING`, being a misconfiguration all the same.

A counterpart is always a *sibling* of its target class. One nested inside the target is not probed — it would have to
be declared in the target's own source, which the ordinary `main`/`test` split cannot do — and the enclosing class of a
nested target is not consulted either. A class nested inside another, such as an `@Embeddable` identifier declared
inside its entity, therefore has to be declared as a top-level class to have a counterpart of its own.

Every located class is instantiated reflectively and must declare an accessible no-argument constructor.

## Usage

```java
class FooRandomizer extends PodamObjectRandomizer<Foo> {
    FooRandomizer() {
        super(Foo.class, List.of("id"));  // leave the generated identifier alone
    }
}

class FooPersister extends AbstractEntityPersister<Foo> {
    FooPersister() {
        super(Foo.class);
    }
}
```

```java
final var foo = EntityPersisterUtils.newPersistedInstanceOf(entityManager, Foo.class);
```

The instance is persisted but not flushed: a test which persists several instances flushes once, when it is done.

Override `apply(entityManager, entityInstance)` for an entity whose required associations have to be persisted first,
calling `super.apply(...)` for the instance itself — the `_Employee_Persister` under `src/test` does exactly that for
the `_Department` its entity may not be written without.

A persister is a *consumer*, and so is contravariant in the class it is declared for: one declared for a superclass of
the target accepts instances of the target, which is how an entity hierarchy shares one implementation. That is what
`newPersisterInstanceOf` checks, and it can only check an `AbstractEntityPersister`, which remembers its target class;
one implementing `EntityPersister` directly leaves nothing but an erased type parameter, and is taken as it is. A
persister which *is* located and accepts no instance of the class it was located for is a fault, and throws.

## Randomizer flavors

The three flavors are declared by `jinahya-object-randomizer`, beside `AbstractObjectRandomizer`, and its README
documents them in full; what matters here is which one an entity wants.

| flavor                           | engine                                                   | instantiation                                                  | needs accessors | Jakarta constraints |
|----------------------------------|----------------------------------------------------------|----------------------------------------------------------------|-----------------|---------------------|
| `PodamObjectRandomizer`          | [PODAM](https://mtedone.github.io/podam/)                | populates the instance from `newTargetInstance()`              | yes             | honored             |
| `InstancioObjectRandomizer`      | [Instancio](https://www.instancio.org)                   | fills the instance from `newTargetInstance()`                  | no              | honored             |
| `FixtureMonkeyObjectRandomizer`  | [Fixture Monkey](https://naver.github.io/fixture-monkey) | constructs the instance itself, through its no-arg constructor | no              | honored, via plugin |

`PodamObjectRandomizer` is the only flavor which needs the target class to expose getters and setters — a JPA entity
mapped with field access it leaves entirely unpopulated, silently.

`InstancioObjectRandomizer` is the flavor to pick for such an entity when an override of `newTargetInstance()` still
has to decide how the instance is constructed: it fills the instance it is handed, through Instancio's existing-object
API. Only a `null` field, and a primitive still at its default, is filled, so a value assigned there survives even when
it is not excluded.

`FixtureMonkeyObjectRandomizer` constructs the instance itself, and so never calls `newTargetInstance()`; an entity
which needs state assigned before it is randomized — a non-nullable column no engine should fill, such as the
`hireDate` of the `_Employee` under `src/test` — belongs to one of the other two.

All three honor `jakarta.validation.constraints` with nothing overridden. PODAM and Instancio read them out of their
own artifact; Fixture Monkey keeps its support in a second one, `fixture-monkey-jakarta-validation`, which
`getFixtureMonkey()` looks up reflectively — so it is declared here, and leaving it out would mean the engine filling
instances while ignoring every constraint on them, with one `DEBUG` line to say so. That artifact declares a complete
Jakarta EE 9 stack at `compile` scope, which the root pom excludes and replaces with the EE 11 one this build pins;
see the `<exclusions>` there.

Every engine is declared `provided`, both there and here, so a consumer brings only the one it uses:
`uk.co.jemos.podam:podam`, `org.instancio:instancio-core`, or `com.navercorp.fixturemonkey:fixture-monkey` — the last
with `fixture-monkey-jakarta-validation` beside it. No flavor promises that a randomized instance satisfies every
persistence constraint; assert on what matters.
