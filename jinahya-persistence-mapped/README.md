# jinahya-persistence-mapped

A module for defining mapped superclasses (and may be entities).

## Motivation

## Defining mapped superclasses for extension

```java

@jakarta.persistence.MappedSuperclass
abstract class _MappedEntityWithIdentity<SELF extends _MappedEntity<SELF>>
        extends com.github.jinahya.persistence.mapped.__MappedEntity<SELF, Long> {

    @jakarta.persistence.Id
    @jakarta.persistence.GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;
}
```

```java
@jakarta.persistence.Entity
@jakarta.persistence.Table(name = "MY_ENTITY")
class MyEntity extends _MappedEntity<MyEntity> {

}
```
