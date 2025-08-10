# jinahya-persistence-mapped-test

A module for testing interfaces and classes extend what defined in the `jinahya-persistence-mapped` module.

---

**[WARNING] DO NOT USE THIS MODULE with a JDBC connection with DDL capabilities, PLEASE.**

**[WARNING] DO NOT USE THIS MODULE with a JDBC connection with DDL capabilities, PLEASE.**

**[WARNING] DO NOT USE THIS MODULE with a JDBC connection with DDL capabilities, PLEASE.**

**[WARNING] DO NOT USE THIS MODULE with a JDBC connection with DDL capabilities, PLEASE.**

**[WARNING] DO NOT USE THIS MODULE with a JDBC connection with DDL capabilities, PLEASE.**

**[WARNING] DO NOT USE THIS MODULE with a JDBC connection with DDL capabilities, PLEASE.**

**[WARNING] DO NOT USE THIS MODULE with a JDBC connection with DDL capabilities, PLEASE.**

**[WARNING] DO NOT USE THIS MODULE with a JDBC connection with DDL capabilities, PLEASE.**

**[WARNING] DO NOT USE THIS MODULE with a JDBC connection with DDL capabilities, PLEASE.**

---

## Preparing the `persistence.xml` file

We need to create the `/META-INF/persistence.xml` file in the test source directory.

See
the [persistnece.xml](https://github.com/jinahya/jinahya-persistence/blob/develop/jinahya-persistence-mapped-test/src/test/resources/META-INF/persistence.xml)
defined for testing the [jinahya-persistence-mapped] module itself.

There are two predefined persistence units, `__testPU` and `__itPU`, for unit tests and integration tests,
respectively.`

### Properties for the `jinahya-persistence-mapped-test` module.

Three custom properties are defined for the `jinahya-persistence-mapped-test` module.

| Property                              | Description                                                                                                                                                                                                                                             | Notes                     |
|---------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------------------|
| `jinahya.persistence.default-catalog` | the name of the [table catalog](https://docs.oracle.com/en/java/javase/21/docs/api/java.sql/java/sql/DatabaseMetaData.html?source=%3Aex%3Apw%3A%3A%3A%3A%3ATNS_PWA_B%2C%3Aex%3Apw%3A%3A%3A%3A%3ATNS_PWA_B#getCatalogs())                                | e.g. `UNNAMED` with H2    |
| `jinahya.persistence.default-schema`  | the name of the [table schema](https://docs.oracle.com/en/java/javase/21/docs/api/java.sql/java/sql/DatabaseMetaData.html?source=%3Aex%3Apw%3A%3A%3A%3A%3ATNS_PWA_B%2C%3Aex%3Apw%3A%3A%3A%3A%3ATNS_PWA_B#getSchemas(java.lang.String,java.lang.String)) | e.g. `PUBLIC` with H2     |
| `jinahya.persistence.default-types`   | a comma-separated names of the [table types](https://docs.oracle.com/en/java/javase/21/docs/api/java.sql/java/sql/DatabaseMetaData.html?source=%3Aex%3Apw%3A%3A%3A%3A%3ATNS_PWA_B%2C%3Aex%3Apw%3A%3A%3A%3A%3ATNS_PWA_B#getTableTypes())                 | e.g. `BASE TABLE` with H2 |

### `__testPU`

This is the persistence unit for unit tests.

```xml
  <persistence-unit name="__testPU">
  <description>Persistence Unit for an In-Memory DB</description>
  <!--    <provider>org.eclipse.persistence.jpa.PersistenceProvider</provider>-->
  <!--    <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>-->
  <provider>${persistence-unit.provider}</provider>
  <!-- your entity classes -->
  <class>...</class>
  <shared-cache-mode>NONE</shared-cache-mode>
  <validation-mode>CALLBACK</validation-mode>
  <properties>
    <!-- standard properties -->
    <property name="javax.persistence.jdbc.driver" value="org.h2.Driver"/>
    <property name="javax.persistence.jdbc.url" value="jdbc:h2:mem:test;DB_CLOSE_DELAY=-1"/>
    <property name="javax.persistence.jdbc.user" value="sa"/>
    <property name="javax.persistence.jdbc.password" value=""/>
    <!-- provider specific properties -->
    <!-- customer properties jinahya.persistence -->
    <property name="jinahya.persistence.default_catalog" value="UNNAMED"/>  <!-- H2 -->
    <property name="jinahya.persistence.default_schema" value="PUBLIC"/>    <!-- H2 -->
    <property name="jinahya.persistence.default_types" value="BASE TABLE"/> <!-- H2 -->
  </properties>
</persistence-unit>
```

### `__itPU`

This is the persistence unit for integration tests.

```xml
<persistence-unit name="__itPU">
  <description>Persistence Unit for a Physical Database</description>
  <!--    <provider>org.eclipse.persistence.jpa.PersistenceProvider</provider>-->
  <!--    <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>-->
  <provider>${persistence-unit.provider}</provider>
  <!-- your entity classes -->
  <class>...</class>
  <shared-cache-mode>NONE</shared-cache-mode>
  <validation-mode>CALLBACK</validation-mode>
  <properties>
    <!-- standard properties -->
    <property name="jakarta.persistence.jdbc.driver" value="org.h2.Driver"/>
    <!-- https://github.com/eclipse-ee4j/eclipselink/issues/1393 -->
    <property name="jakarta.persistence.jdbc.url" value="jdbc:h2:mem:;database_to_upper=false;MODE=LEGACY"/>
    <property name="jakarta.persistence.jdbc.user" value="sa"/>
    <property name="jakarta.persistence.jdbc.password" value="password"/>
    <!-- provider specific properties -->
    <property name="eclipselink.ddl-generation" value="none"/> <!-- DO NOT USE ANY OTHER VALUE!!! -->
    <property name="hibernate.hbm2ddl.auto" value="none"/>     <!-- DO NOT USE ANY OTHER VALUE!!! -->
    <!-- custom properties for jinahya.persistence -->
    <property name="jinahya.persistence.default_catalog" value="..."/>
    <property name="jinahya.persistence.default_schema" value="..."/>
    <property name="jinahya.persistence.default_types" value="...,...,..."/>
  </properties>
</persistence-unit>
```

## Testing individual entity classes

### Preparing an instantiator class

> The entity class must have a public or protected constructor with no parameters, ...
>
> [Jakarta Persistence]

A class extends the `__MappedEntity` class should have a default constructor, anyway. You don't have to define an
instantiator class for it.

### Preparing a randomizer class

A randomizer class is required for testing persistence operations such as persisting a new (randomized) entity.

```java
// Place this class next to the MyEntity.java
class MyEntityRandomizer extends __MappedEntityRandomizer<MyEntity, Long> {

    MyEntityRandomizer() {
        super(MyEntity.class, Long.class, "exclude-this-field", 'exclude-that-field');
    }
}
```

### Testing a class extends `__MappedEntity` as a normal Java class.

Extends the `__MappedEntityTest` class with type parameters.

```java
class MyEntityTest extends __MappedEntity<MyEntity, Long> {

    MyEntityTest() {
        super(MyEntity.class, Long.class);
    }

    @Override
    public ENTITY get() {
        final var instance = super.get();
        // customize, when required, the randomized entity instance
        return instance;
    }
}
```

That's it. The class should test `toString()`, `equals/hashCode`, and standard accessors.

### Testing a class extends `__MappedEntity` as a JPA entity class within the `__testPU`

```java
class MyEntityPersistenceTest extends __MappedEntityPersistenceTest<MyEntity, Long> {

    MyEntityTest() {
        super(MyEntity.class, Long.class);
    }
}
```

### Testing a class extends `__MappedEntity` as a JPA entity class within the `__itPU`

```java
class MyEntityPersistenceIT extends __MappedEntityPersistenceIT<MyEntity, Long> {

    MyEntityTest() {
        super(MyEntity.class, Long.class);
    }
}
```

## Testing the whole table family

---

[Jakarta Persistence]: https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2

