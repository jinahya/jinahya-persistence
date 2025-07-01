package com.github.jinahya.persistence.mapped.tests;

import com.github.jinahya.persistence.mapped.__MappedEntity;
import com.github.jinahya.persistence.mapped.tests.util.__JavaLangReflectUtils;
import org.mockito.Mockito;

import java.util.Objects;
import java.util.Optional;

/**
 * A class for testing subclass of {@link __MappedEntity}.
 *
 * @param <ENTITY> entity type parameter
 * @param <ID>     id type parameter
 */
@SuppressWarnings({
        "java:S100", // Method names should comply with a naming convention
        "java:S101", // Class names should comply with a naming convention
        "java:S119", // Type parameter names should comply with a naming convention
        "java:S5960" // Assertions should not be used in production code
})
abstract class ___MappedEntityTest<ENTITY extends __MappedEntity<ENTITY, ID>, ID> {

    /**
     * Creates a new instance to test the specified entity class.
     *
     * @param entityClass the entity class to test.
     * @param idClass     the id class of the {@code entityClass}.
     * @see #entityClass
     * @see #idClass
     */
    ___MappedEntityTest(final Class<ENTITY> entityClass, final Class<ID> idClass) {
        super();
        this.entityClass = Objects.requireNonNull(entityClass, "entityClass is null");
        this.idClass = Objects.requireNonNull(idClass, "idClass is null");
    }

    // ----------------------------------------------------------------------------------------------------- entityClass

    /**
     * Returns a new instance of the {@link #entityClass}.
     *
     * @return a new instance of the {@link #entityClass}.
     */
    protected ENTITY newEntityInstance() {
        return __JavaLangReflectUtils.newInstanceOf(entityClass);
    }

    /**
     * Returns a apy of a new instance of {@link #entityClass}.
     *
     * @return a apy of a new spied instance of {@link #entityClass}.
     * @see #newEntityInstance()
     * @see Mockito#spy(Object)
     */
    protected ENTITY newEntityInstanceSpy() {
        return Mockito.spy(newEntityInstance());
    }

    /**
     * Returns a new randomized instance of the {@link #entityClass}.
     *
     * @return a new randomized instance of the {@link #entityClass}.
     * @see __MappedEntityRandomizerUtils#newRandomizedInstanceOf(Class)
     */
    protected Optional<ENTITY> newRandomizedEntityInstance() {
        return __MappedEntityRandomizerUtils.newRandomizedInstanceOf(entityClass);
    }

    /**
     * Returns a spy of anew randomized instance of the {@link #entityClass}.
     *
     * @return a apy of a new randomized instance of the {@link #entityClass}.
     * @see #newRandomizedEntityInstance()
     * @see Mockito#spy(Object)
     */
    protected Optional<ENTITY> newRandomizedEntityInstanceSpy() {
        return Mockito.spy(newRandomizedEntityInstance());
    }

    // --------------------------------------------------------------------------------------------------------- idClass

    /**
     * Returns a new instance of the {@link #idClass}.
     *
     * @return a new instance of the {@link #idClass}.
     */
    protected ID newIdInstance() {
        return ___InitializerUtils.newInitializedInstanceOf(idClass)
                .orElseGet(() -> __JavaLangReflectUtils.newInstanceOf(idClass));
    }

    /**
     * Returns a apy of a new instance of {@link #idClass}.
     *
     * @return a apy of a new spied instance of {@link #idClass}.
     * @see #newIdInstance()
     * @see Mockito#spy(Object)
     */
    protected ID newIdInstanceSpy() {
        return Mockito.spy(newIdInstance());
    }

    /**
     * Returns a new randomized instance of the {@link #idClass}.
     *
     * @return a new randomized instance of the {@link #idClass}.
     * @see ___RandomizerUtils#newRandomizedInstanceOf(Class)
     */
    protected Optional<ID> newRandomizedIdInstance() {
        return ___RandomizerUtils.newRandomizedInstanceOf(idClass);
    }

    /**
     * Returns a spy of anew randomized instance of the {@link #idClass}.
     *
     * @return a apy of a new randomized instance of the {@link #idClass}.
     * @see #newRandomizedIdInstance()
     * @see Mockito#spy(Object)
     */
    protected Optional<ID> newRandomizedIdInstanceSpy() {
        return Mockito.spy(newRandomizedIdInstance());
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The entity class to test.
     */
    protected final Class<ENTITY> entityClass;

    /**
     * The type of the id of the {@link #entityClass}.
     */
    protected final Class<ID> idClass;
}
