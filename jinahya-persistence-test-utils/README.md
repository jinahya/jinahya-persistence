# jinahya-persistence-test-utils

![Maven Central Version](https://img.shields.io/maven-central/v/io.github.jinahya/jinahya-persistence-test-utils)
[![javadoc](https://javadoc.io/badge2/io.github.jinahya/jinahya-persistence-test-utils/javadoc.svg)](https://javadoc.io/doc/io.github.jinahya/jinahya-persistence-test-utils)


A module for testing classes/interfaces created with `jinahya-persistence`.

Package: `com.github.jinahya.persistence.test.util`

## Roles

A target class is instantiated, randomized, and persisted by three collaborating abstractions, each located for the
target class by convention.

| role             | abstraction     | required                    | when none is located                          |
|------------------|-----------------|-----------------------------|-----------------------------------------------|
| create an instance | `__Instantiator` | no                        | the target's no-argument constructor is used  |
| fill it with data  | `__Randomizer`   | yes, to persist           | `newRandomizedInstanceOf` returns empty       |
| persist it         | `__Persister`    | yes, to persist           | `newPersistedInstanceOf` throws               |

Each role has a `__XxxUtils` class which locates it by the convention and applies it. The convention is the only
mechanism: a helper is found because of how it is named, and a class the convention cannot reach — a local or an
anonymous class — has no helper.

The convention spans source sets: an entity in `src/main/java/com/foo/Foo.java` and its `FooRandomizer` in
`src/test/java/com/foo/` are the same package, and both are on the test classpath.

## Convention

For a target class `Foo`, `__XxxUtils.locateStandard` probes, in order:

1. `FooRandomizer` — declared beside `Foo`
2. `Foo_Randomizer` — declared beside `Foo`

and likewise for `Instantiator` and `Persister`. That is the whole rule, and it is identical for all three roles.

A counterpart is always a *sibling* of its target class. One nested inside the target is not probed — it would have to
be declared in the target's own source, which the ordinary `main`/`test` split cannot do — and the enclosing class of a
nested target is not consulted either. A class nested inside another, such as an `@Embeddable` identifier declared
inside its entity, therefore has to be declared as a top-level class to have a counterpart of its own.

Every located class is instantiated reflectively and must declare an accessible no-argument constructor.

## Usage

```java
class FooRandomizer extends __Randomizer.___OfEasyRandom<Foo> {
    FooRandomizer() {
        super(Foo.class, List.of("id"));  // leave the generated identifier alone
    }
}

class FooPersister extends __Persister<Foo> {
    FooPersister() {
        super(Foo.class);
    }
}
```

```java
final var foo = __PersisterUtils.newPersistedInstanceOf(entityManager, Foo.class);
```

The instance is persisted but not flushed.

## Randomizer flavors

| flavor                | engine                                                    | instantiation                                                  | needs accessors | Jakarta constraints |
|-----------------------|-----------------------------------------------------------|----------------------------------------------------------------|-----------------|---------------------|
| `___OfPodam`          | [PODAM](https://mtedone.github.io/podam/)                 | populates an instance from the located instantiator            | yes             | honored             |
| `___OfInstancio`      | [Instancio](https://www.instancio.org)                    | fills an instance from the located instantiator                | no              | opt-in              |
| `___OfEasyRandom` | [Easy Random](https://github.com/j-easy/easy-random)      | constructs the instance itself, bypassing constructors         | no              | unsupported         |
| `___OfFixtureMonkey`  | [Fixture Monkey](https://naver.github.io/fixture-monkey)  | constructs the instance itself, through its no-arg constructor | no              | opt-in, via plugin  |

`___OfPodam` reads `jakarta.validation.constraints` with no further setup, and is the only flavor which needs the target
class to expose getters and setters — a JPA entity mapped with field access it leaves entirely unpopulated, silently.

`___OfInstancio` is the flavor to pick for such an entity when the located `__Instantiator` still has to decide how the
instance is constructed: it fills the instance it is handed, through Instancio's existing-object API, which is still
marked experimental. Only a `null` field, and a primitive still at its default, is filled, so a value a constructor
assigned survives even when it is not excluded. Override `getInstancioSettings()` to turn constraint support on with
`Keys.BEAN_VALIDATION_ENABLED`, and `Keys.JPA_ENABLED` for `@Column(length = …)`; override `getInstancio(instance)` for
selectors or a replay seed.

`___OfEasyRandom` bypasses constructors entirely, and honors no constraint at all: Easy Random 6 removed its
constraint support outright — the `easy-random-bean-validation` artifact, which bound the `javax` annotations and so
honored nothing on this platform anyway, is gone, and 6.x ships as the single `org.jeasy:easy-random` artifact.

`___OfFixtureMonkey` writes fields reflectively too, but goes through a no-argument constructor, which it requires, so a
value that constructor assigns is in place before the fields are written and an excluded field keeps it. It is the
flavor whose builder a subclass can drive property by property, by overriding `getArbitraryBuilder()`; override
`getFixtureMonkey()` to configure the engine — keeping the exclusion configuration if you replace it — or to register
the `JakartaValidationPlugin` from `com.navercorp.fixturemonkey:fixture-monkey-jakarta-validation`.

Every engine is declared `provided`, so a consumer brings only the one it uses: `uk.co.jemos.podam:podam`,
`org.instancio:instancio-core`, `org.jeasy:easy-random`, or `com.navercorp.fixturemonkey:fixture-monkey`. No flavor
promises that a randomized instance satisfies every persistence constraint; assert on what matters.
