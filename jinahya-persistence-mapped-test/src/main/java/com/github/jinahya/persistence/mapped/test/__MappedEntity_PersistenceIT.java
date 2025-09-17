package com.github.jinahya.persistence.mapped.test;

/*-
 * #%L
 * jinahya-persistence-mapped-test
 * %%
 * Copyright (C) 2024 - 2025 Jinahya, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.github.jinahya.persistence.mapped.__MappedEntity;
import jakarta.annotation.Nonnull;
import jakarta.inject.Inject;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.WeldJunit5AutoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.AnnotationUtils;
import org.junit.platform.commons.util.ReflectionUtils;

import java.lang.System.Logger.Level;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;

@AddBeanClasses({
        __PersistenceProducer.class
})
@ExtendWith(WeldJunit5AutoExtension.class)
@SuppressWarnings({
        "java:S100", // Method names should comply with a naming convention
        "java:S101", // Class names should comply with a naming convention
        "java:S119", // Type parameter names should comply with a naming convention
        "java:S6813" // Field dependency injection should be avoided
})
public abstract class __MappedEntity_PersistenceIT<ENTITY extends __MappedEntity<ID>, ID>
        extends __MappedEntity_Persistence_<ENTITY, ID> {

    private static final System.Logger logger = System.getLogger(MethodHandles.lookup().lookupClass().getName());

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Creates a new instance for testing specified entity class with
     * {@value __PersistenceProducer_TestConstants#PERSISTENCE_UNIT_NAME_IT_PU} persistence unit.
     *
     * @param entityClass the entity class to test.
     * @param idClass     an id class of the {@code entityClass}.
     */
    protected __MappedEntity_PersistenceIT(final Class<ENTITY> entityClass, final Class<ID> idClass) {
        super(entityClass, idClass);
    }

//    // -----------------------------------------------------------------------------------------------------------------
//    @Override
//    @PostConstruct
//    protected void doOnPostConstruct() {
//        super.doOnPostConstruct();
//    }
//
//    // https://stackoverflow.com/a/72628439/330457
//    protected void onStartup(@Observes final Startup startup) {
//        super.onStartup(startup);
//    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Asserts no modification to the schema.
     */
    @DisplayName("no modification to the schema")
    @BeforeEach
    protected final void assertNoSchemaModification() {
        acceptEntityManagerFactory(__PersistenceProducer_TestUtils::assertSchemagenDatabaseActionNone);
        acceptEntityManagerFactory(__PersistenceProducer_TestUtils::assertEclipselinkDdlGenerationNone);
        acceptEntityManagerFactory(__PersistenceProducer_TestUtils::assertHibernateHbm2ddlAutoNone);
    }

    // -----------------------------------------------------------------------------------------------------------------
    protected Collection<String> getTableColumnNames() {
        final var table = __MappedEntity_TestUtils.getTableAnnotation(entityClass);
        final var catalog = applyEntityManagerFactory(__PersistenceUnit_TestUtils::getJinahyaTableCatalog)
                .orElseGet(table::catalog);
        final var schema = applyEntityManagerFactory(__PersistenceUnit_TestUtils::getJinahyaTableSchema)
                .orElseGet(table::schema);
        final var tableColumnNames = applyEntityManager(
                em -> ___JakartaPersistence_TestUtils.applyConnectionAndRollback(
                        em,
                        c -> ___JavaSql_TestUtils.addAllColumnNames(
                                c,
                                catalog,
                                schema,
                                table.name(),
                                new ArrayList<>()
                        )
                ));
        logger.log(Level.DEBUG, "tableColumnNames: {0}", tableColumnNames);
        assertThat(tableColumnNames)
                .as("table column names for " + entityClass +
                    "; catalog: " + catalog +
                    "; schema: " + schema +
                    "; table: " + table.name()
                )
                .isNotEmpty();
        return tableColumnNames;
    }

    protected Collection<String> getEntityColumnNames() {
        final var entityColumnNames = applyEntityManagerFactory(
                emf -> ___JakartaPersistence_TestUtils.addAllEntityColumNames(
                        emf,
                        entityClass,
                        new ArrayList<>()
                )
        );
        logger.log(Level.DEBUG, "entity column names: {0}", entityColumnNames);
        assertThat(entityColumnNames)
                .as("entity column names of " + entityClass)
                .isNotEmpty();
        return entityColumnNames;
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Checks that all table column names are mapped, and reports to the
     * {@link #_Mapped_AllTableColumnNames(Collection)} method with remaining table column names.
     *
     * @see #_Mapped_AllTableColumnNames(Collection)
     */
    @DisplayName("check that all table columns are mapped")
    @Test
    final void _Mapped_AllTableColumnNames() {
        {
            final var clazz = getClass();
            final var disabled = AnnotationUtils.findAnnotation(
                    clazz, __Disable_TableColumnNames_Test.class
            );
            assumeThat(disabled)
                    .as("%s on %s", __Disable_TableColumnNames_Test.class, clazz)
                    .isEmpty();
        }
        // ------------------------------------------------------------------------------------------------------- given
        final var tableColumnNames = getTableColumnNames();
        final var attributeColumnNames = Collections.unmodifiableCollection(getEntityColumnNames());
        // -------------------------------------------------------------------------------------------------------- when
        tableColumnNames.removeAll(attributeColumnNames);
        _Mapped_AllTableColumnNames(tableColumnNames);
        // -------------------------------------------------------------------------------------------------------- then
        assertThat(tableColumnNames)
                .as("remaining table column names")
                .isEmpty();
    }

    /**
     * Notifies specified remaining table column names, from which all entity column names are removed.
     *
     * @param tableColumnNames the remaining table column names that all entity column names are removed.
     * @see #_Mapped_AllTableColumnNames()
     */
    protected void _Mapped_AllTableColumnNames(@Nonnull final Collection<String> tableColumnNames) {
        Objects.requireNonNull(tableColumnNames, "tableColumnNames is null");
        tableColumnNames.forEach(cn -> {
            logger.log(Level.WARNING, "remaining table column name: " + cn);
        });
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Checks that all entity column names are known.
     *
     * @see #_Empty_AllEntityColumnNames(Collection)
     */
    @DisplayName("check that all entity columns are known")
    @Test
    void _Known_AllEntityColumnNames() {
        {
            final var clazz = getClass();
            final var disabled = AnnotationUtils.findAnnotation(
                    clazz, __Disable_EnittyColumnNames_Test.class
            );
            assumeThat(disabled)
                    .as("%s on %s", __Disable_EnittyColumnNames_Test.class, clazz)
                    .isEmpty();
        }
        // ------------------------------------------------------------------------------------------------------- given
        final var tableColumnNames = Collections.unmodifiableCollection(getTableColumnNames());
        final var entityColumnNames = getEntityColumnNames();
        // -------------------------------------------------------------------------------------------------------- when
        entityColumnNames.removeAll(tableColumnNames);
        _Empty_AllEntityColumnNames(entityColumnNames);
        // -------------------------------------------------------------------------------------------------------- then
        assertThat(entityColumnNames)
                .as("remaining entity column names")
                .isEmpty();
    }

    /**
     * Notifies specified remaining entity column names, from which all table column names are removed.
     *
     * @param remainingEntityColumnNames the remaining entity column names that all table column names are removed.
     */
    protected void _Empty_AllEntityColumnNames(@Nonnull final Collection<String> remainingEntityColumnNames) {
        Objects.requireNonNull(remainingEntityColumnNames, "remainingEntityColumnNames is null");
        remainingEntityColumnNames.forEach(cn -> {
            logger.log(Level.WARNING, "remaining entity column name: " + cn);
        });
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * All values of {@code @Column(nullable)} should match corresponding table column's nullability.
     */
    @Test
    void _MatchTableColumnNullable_EntityColumnNullable() {
        {
            final var clazz = getClass();
            final var disabled = AnnotationUtils.findAnnotation(
                    clazz, __Disable_ColumnNullable_Test.class
            );
            assumeThat(disabled)
                    .as("%s on %s", __Disable_ColumnNullable_Test.class, clazz)
                    .isEmpty();
        }
        final var tableName = tableName();
        final var tableColumnNamesAndIsNullables =
                applyEntityManager(em -> ___JakartaPersistence_TestUtils.applyConnectionAndRollback(
                        em,
                        c -> ___JavaSql_TestUtils.getColumnNameAndIsNullable(
                                c,
                                tableCatalog(),
                                tableSchema(),
                                tableName
                        )
                ));
        final var managedType = applyEntityManagerFactory(em -> {
            return em.getMetamodel().managedType(entityClass);
        });
        managedType.getAttributes().forEach(a -> {
            final var entityMember = a.getJavaMember();
            final Column entityColumn;
            if (entityMember instanceof Field field) {
                entityColumn = field.getAnnotation(Column.class);
            } else if (entityMember instanceof Method method) {
                entityColumn = method.getAnnotation(Column.class);
            } else {
                throw new RuntimeException("unknown entity member type: " + entityMember);
            }
            if (entityColumn == null) {
                logger.log(Level.WARNING, "no @Column annotation on {0}", entityMember);
                return;
            }
            final var entityColumnName = entityColumn.name();
            final var entityColumnNullable = entityColumn.nullable();
            final var tableColumnIsNullable = tableColumnNamesAndIsNullables.get(entityColumnName);
            if (!_MatchTableColumnNullable_EntityColumnNullable(
                    entityMember, entityColumn, tableColumnIsNullable)) {
                logger.log(
                        Level.INFO,
                        "skipping nullability check; member {0}, column: {1}",
                        entityMember,
                        entityColumn
                );
                return;
            }
            assertThat(entityColumnNullable)
                    .as("@Column#nullable of %s supposed to match %s.%s(%s) ", entityMember, tableName,
                        entityColumnName, tableColumnIsNullable)
                    .isEqualTo(tableColumnIsNullable);
        });
    }

    /**
     * Notifies that an attribute's {@link Column#nullable() @Column#nullable} element is going to be verified to match
     * the table column's nullability.
     *
     * @param member   a java member of the attribute.
     * @param column   a {@link Column @Column} annotation of the attribute.
     * @param nullable the nullability of the table column.
     * @return {@code true} if the annotation should be tested; {@code false} for skipping the attribute.
     */
    protected boolean _MatchTableColumnNullable_EntityColumnNullable(@Nonnull final Member member,
                                                                     @Nonnull final Column column,
                                                                     final boolean nullable) {
        return true;
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * All values of either {@code @Basic(optional)}, {@code @OneToOne(optional)}, or {@code @ManyToOne(optional)}
     * should match {@code @Column(nullable)}.
     */
    @DisplayName("@Basic, @OneToOne, and @ManyToOne's optional should match @Column(nullable)")
    @Test
    void _MatchColumnNullable_EntityMappingOptional() {
        {
            final var clazz = getClass();
            final var disabled = AnnotationUtils.findAnnotation(
                    clazz, __Disable_MappingOptional_Test.class
            );
            assumeThat(disabled)
                    .as("%s on %s", __Disable_MappingOptional_Test.class, clazz)
                    .isEmpty();
        }
        final var managedType = applyEntityManagerFactory(em -> {
            return em.getMetamodel().managedType(entityClass);
        });
        managedType.getAttributes().forEach(a -> {
            final var entityMember = a.getJavaMember();
            final Column entityColumn;
            if (entityMember instanceof Field field) {
                entityColumn = field.getAnnotation(Column.class);
            } else if (entityMember instanceof Method method) {
                entityColumn = method.getAnnotation(Column.class);
            } else {
                throw new RuntimeException("unknown entity member type: " + entityMember);
            }
            if (entityColumn == null) {
                logger.log(Level.WARNING, "no @Column annotation on {0}", entityMember);
                return;
            }
            final var entityColumnNullable = entityColumn.nullable();
            {
                final var entityMapping = ((AccessibleObject) entityMember).getAnnotation(Basic.class);
                if (entityMapping == null) {
                    return;
                }
                if (!_MatchColumnNullable_EntityMappingOptional(entityMember, entityColumn, entityMapping)) {
                    logger.log(
                            Level.INFO,
                            "skipping mapping#optional check; member {0}, column: {1}, mapping: {2}",
                            entityMember,
                            entityColumn,
                            entityMapping
                    );
                    return;
                }
                assertThat(entityMapping.optional())
                        .as("@Basic#optional of %s against %s", entityMember, entityColumn)
                        .isEqualTo(entityColumnNullable);
            }
            {
                final var entityMapping = ((AccessibleObject) entityMember).getAnnotation(OneToOne.class);
                if (entityMapping == null) {
                    return;
                }
                if (!_MatchColumnNullable_EntityMappingOptional(entityMember, entityColumn, entityMapping)) {
                    // TODO: log
                    return;
                }
                assertThat(entityMapping.optional())
                        .as("@OneToOne#optional of %s against %s", entityMember, entityColumn)
                        .isEqualTo(entityColumnNullable);
            }
            {
                final var entityMapping = ((AccessibleObject) entityMember).getAnnotation(ManyToOne.class);
                if (entityMapping == null) {
                    return;
                }
                if (!_MatchColumnNullable_EntityMappingOptional(entityMember, entityColumn, entityMapping)) {
                    // TODO: log
                    return;
                }
                assertThat(entityMapping.optional())
                        .as("@ManyToOne#optional of %s against %s", entityMember, entityColumn)
                        .isEqualTo(entityColumnNullable);
            }
        });
    }

    /**
     * Notifies that an attribute's {@link Basic#optional() @Basic#optional} element is going to be verified to match
     * the attribute's {@link Column#nullable() @Column#nullable}.
     *
     * @param member  a java member of the attribute.
     * @param column  a {@link Column @Column} annotation of the attribute.
     * @param mapping the mapping.
     * @return {@code true} if the mapping should be verified; {@code false} for skipping the mapping.
     */
    protected boolean _MatchColumnNullable_EntityMappingOptional(@Nonnull final Member member,
                                                                 @Nonnull final Column column,
                                                                 @Nonnull final Basic mapping) {
        return true;
    }

    protected boolean _MatchColumnNullable_EntityMappingOptional(@Nonnull final Member member,
                                                                 @Nonnull final Column column,
                                                                 @Nonnull final OneToOne mapping) {
        return true;
    }

    protected boolean _MatchColumnNullable_EntityMappingOptional(@Nonnull final Member member,
                                                                 @Nonnull final Column column,
                                                                 @Nonnull final ManyToOne mapping) {
        return true;
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * All attributes of {@code @Column(nullable)} should not be annotated with {@link NotNull}.
     */
    @DisplayName("@Column(nullable = true) should not have @NotNull")
    @Test
    void _ShouldNotBeAnnotatedWithNotNull_AttributeWithColumnNullable() {
        {
            final var clazz = getClass();
            final var disabled = AnnotationUtils.findAnnotation(
                    clazz, __Disable_NotNull_Test.class
            );
            assumeThat(disabled)
                    .as("%s on %s", __Disable_NotNull_Test.class, clazz)
                    .isEmpty();
        }
        final var managedType = applyEntityManagerFactory(em -> {
            return em.getMetamodel().managedType(entityClass);
        });
        managedType.getAttributes().forEach(a -> {
            final var entityMember = a.getJavaMember();
            final Column entityColumn;
            if (entityMember instanceof Field field) {
                entityColumn = field.getAnnotation(Column.class);
            } else if (entityMember instanceof Method method) {
                entityColumn = method.getAnnotation(Column.class);
            } else {
                throw new RuntimeException("unknown entity member type: " + entityMember);
            }
            if (entityColumn == null) {
                logger.log(Level.WARNING, "no @Column annotation on {0}", entityMember);
                return;
            }
            if (!entityColumn.nullable()) {
                return;
            }
            final var notNull = ((AccessibleObject) entityMember).getAnnotation(NotNull.class);
            if (notNull == null) {
                return;
            }
            if (!_ShouldNotBeAnnotatedWithNotNull_AttributeWithColumnNullable(entityMember, entityColumn,
                                                                              notNull)) {
                return;
            }
            throw new AssertionError(
                    "entity member " + entityMember + " with + " + entityColumn + " is annotated with @NotNull"
            );
        });
    }

    protected boolean _ShouldNotBeAnnotatedWithNotNull_AttributeWithColumnNullable(
            final Member entityMember, final Column entityColum, final NotNull notNull) {
        return true;
    }

    /**
     * All attributes of {@code @Column(nullable)} should not be annotated with any {@code NonNull} annotation.
     */
    @DisplayName("@Column(nullable = true) should not have any @NonNull")
    @Test
    void _ShouldNotBeAnnotatedWithAnyNonNull_AttributeWithColumnNullable() {
        {
            final var clazz = getClass();
            final var disabled = AnnotationUtils.findAnnotation(
                    clazz, __Disable_NonNull_Test.class
            );
            assumeThat(disabled)
                    .as("%s on %s", __Disable_NonNull_Test.class, clazz)
                    .isEmpty();
        }
        final var managedType = applyEntityManagerFactory(em -> {
            return em.getMetamodel().managedType(entityClass);
        });
        managedType.getAttributes().forEach(a -> {
            final var entityMember = a.getJavaMember();
            final Column entityColumn;
            final java.lang.annotation.Annotation[] annotations;
            if (entityMember instanceof Field field) {
                final var actualField =
                        ReflectionUtils.findFields(entityClass, f -> f.getName().equals(field.getName()),
                                                   ReflectionUtils.HierarchyTraversalMode.BOTTOM_UP).get(0);
                entityColumn = field.getAnnotation(Column.class);
//                annotations = field.getDeclaredAnnotations();
                annotations = actualField.getDeclaredAnnotations();
            } else if (entityMember instanceof Method method) {
                final var actualMethod = ReflectionUtils.findMethod(entityClass, method.getName()).orElseThrow();
                entityColumn = method.getAnnotation(Column.class);
//                annotations = method.getDeclaredAnnotations();
                annotations = actualMethod.getDeclaredAnnotations();
            } else {
                throw new RuntimeException("unknown entity member type: " + entityMember);
            }
            if (entityColumn == null) {
                logger.log(Level.WARNING, "no @Column annotation for " + entityMember);
                return;
            }
            if (!entityColumn.nullable()) {
                return;
            }
            final var nonnull = Arrays.stream(annotations)
                    .filter(nn -> {
                        final var c = nn.getClass();
                        final var name = c.getName();
                        final var simpleName = c.getSimpleName();
                        return nn.annotationType().getSimpleName().equalsIgnoreCase("nonnull");
                    })
                    .findAny()
                    .orElse(null);
            if (nonnull == null) {
                return;
            }
            if (!_ShouldNotBeAnnotatedWithAnyNonNull_AttributeWithColumnNullable(entityMember, entityColumn, nonnull)) {
                // TODO: log
                return;
            }
            throw new AssertionError(
                    "entity member " + entityMember + " with + " + entityColumn + " is annotated with " + nonnull
            );
        });
    }

    protected boolean _ShouldNotBeAnnotatedWithAnyNonNull_AttributeWithColumnNullable(
            @Nonnull final Member entityMember, @Nonnull final Column entityColum, @Nonnull final Object nonNull) {
        return true;
    }

    // ----------------------------------------------------------------------------------------------------- entityClass

    /**
     * Verifies that a randomly selected entity instance is valid.
     *
     * @see ___JakartaPersistence_TestUtils#selectRandom(EntityManager, Class)
     */
    @DisplayName("select an entity instance of a random index")
    @Test
    void _Valid_RandomlySelectedEntityInstance() {
        {
            final var clazz = getClass();
            final var disabled = AnnotationUtils.findAnnotation(clazz, __Disable_RandomSelection_Test.class);
            assumeThat(disabled)
                    .as("%s on %s", __Disable_RandomSelection_Test.class, clazz)
                    .isEmpty();
        }
        applyEntityManager(em -> {
            // ---------------------------------------------------------------------------------------------------- when
            final var selected = ___JakartaPersistence_TestUtils.selectRandom(em, entityClass);
            if (selected.isEmpty()) {
                logger.log(Level.INFO, "no random entity selected; maybe the table is empty?");
                return null;
            }
            final var value = selected.get();
            _Valid_RandomlySelectedEntityInstance(value);
            // ---------------------------------------------------------------------------------------------------- then
            ___JakartaValidation_TestUtils.requireValid(value);
            return null;
        });
    }

    /**
     * Notifies, by the {@link #_Valid_RandomlySelectedEntityInstance()} method, that the specified entity instance has
     * been selected.
     *
     * @param entityInstance the entity instance.
     * @see #_Valid_RandomlySelectedEntityInstance()
     */
    protected void _Valid_RandomlySelectedEntityInstance(@Nonnull final ENTITY entityInstance) {
        Objects.requireNonNull(entityInstance, "entityInstance is null");
        logger.log(Level.DEBUG, "randomly selected entity instance: {0}", entityInstance);
    }

    // -------------------------------------------------------------------------------------- super.entityManagerFactory
    @Override
    final EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @__PersistenceProducer.__itPU
    @Inject
    private EntityManagerFactory entityManagerFactory;
}
