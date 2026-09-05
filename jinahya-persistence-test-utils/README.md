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

Each role has a `__XxxLocator`, whose `STANDARD` constant implements the naming convention, and a `__XxxUtils` class
which drives it. Pass a custom locator for classes the convention cannot reach — including entity classes in `main`
whose helpers live in `test`.

## Convention

For a target class `Foo`, each `STANDARD` locator probes, in order:

1. `FooRandomizer`, then `Foo_Randomizer` — declared beside `Foo`
2. `Foo$FooRandomizer`, then `Foo$Foo_Randomizer` — nested inside `Foo`

and likewise for `Instantiator` and `Persister`. The instantiator and randomizer locators additionally look inside the
helper of `Foo`'s *enclosing* class first, so a nested `Outer.Inner` resolves to `OuterRandomizer$InnerRandomizer`; the
persister locator does not. A randomizer may instead be named outright with `@__RandomizerClass`, which wins over the
convention and is deliberately not `@Inherited`.

Every located class is instantiated reflectively and must declare an accessible no-argument constructor.

## Usage

```java
class FooRandomizer extends __Randomizer.___OfEasyRandomBean<Foo> {
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

| flavor                 | engine                                                  | instantiation                                     |
|------------------------|---------------------------------------------------------|---------------------------------------------------|
| `___OfPodam`           | [PODAM](https://mtedone.github.io/podam/)               | populates an instance from the located instantiator |
| `___OfEasyRandomBean`  | [Easy Random](https://github.com/j-easy/easy-random)    | constructs the instance itself, bypassing constructors |

`___OfEasyRandomBean` honors Bean Validation constraints only when `easy-random-bean-validation` is on the runtime
classpath, from which Easy Random picks up its `BeanValidationRandomizerRegistry` through the `ServiceLoader`. Both
engines, and both Jakarta APIs, are declared `provided`, so a consumer brings only what it uses.
