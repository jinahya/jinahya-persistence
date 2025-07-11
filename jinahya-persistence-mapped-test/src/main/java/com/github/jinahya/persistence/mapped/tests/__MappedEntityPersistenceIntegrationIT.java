package com.github.jinahya.persistence.mapped.tests;

import com.github.jinahya.persistence.mapped.__MappedEntity;
import com.github.jinahya.persistence.mapped.tests.util.__JavaSqlUtils;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119", // Type parameter names should comply with a naming convention
        "java:S6813" // Field dependency injection should be avoided
})
public abstract class __MappedEntityPersistenceIntegrationIT<ENTITY extends __MappedEntity<ENTITY, ID>, ID>
        extends ___MappedEntityPersistenceTest<ENTITY, ID> {

    // -----------------------------------------------------------------------------------------------------------------
    protected __MappedEntityPersistenceIntegrationIT(final Class<ENTITY> entityClass, final Class<ID> idClass) {
        super(entityClass, idClass);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @DisplayName("all table columns are mapped")
    @Test
    protected void _Mapped_AllTableColumnNames() {
        final var table = __MappedEntityTestUtils.getTableAnnotation(entityClass);
        final var catalog = applyEntityManagerFactory(___MappedEntityPersistenceTestUtils::getDefaultCatalog)
                .orElseGet(table::catalog);
        final var schema = applyEntityManagerFactory(___MappedEntityPersistenceTestUtils::getDefaultSchema)
                .orElseGet(table::schema);
        final var tableColumnNames = applyConnectionInTransactionAndRollback(c -> {
            try {
                return __JavaSqlUtils.addAllColumnNames(c, catalog, schema, table.name(), new ArrayList<>());
            } catch (final SQLException sqle) {
                throw new RuntimeException("failed to get table column names: entity class: " + entityClass, sqle);
            }
        });
        assertThat(tableColumnNames)
                .as("table column names for " + entityClass)
                .isNotEmpty();
        final var attributeColumnNames = applyEntityManagerFactory(
                emf -> __MappedEntityPersistenceTestUtils.addAllAttributeColumNames(emf, entityClass, new ArrayList<>())
        );
        assertThat(attributeColumnNames)
                .as("attribute column names for " + entityClass)
                .isNotEmpty();
        // -------------------------------------------------------------------------------------------------------------
        tableColumnNames.removeAll(attributeColumnNames);
        // -------------------------------------------------------------------------------------------------------------
        assertThat(tableColumnNames)
                .as("remaining table column names for " + entityClass)
                .isEmpty();
    }

    @DisplayName("all entity columns are mapped")
    @Test
    protected void _Mapped_AllEntityColumnColumnNames() {
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    final EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    @Override
    final EntityManager getEntityManager() {
        return entityManager;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @__PersistenceProducer.Integration__
    @Inject
    private EntityManagerFactory entityManagerFactory;

    @__PersistenceProducer.Integration__
    @Inject
    private EntityManager entityManager;
}
