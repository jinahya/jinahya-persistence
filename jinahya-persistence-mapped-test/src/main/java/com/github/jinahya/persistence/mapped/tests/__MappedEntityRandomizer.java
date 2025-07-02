package com.github.jinahya.persistence.mapped.tests;

import com.github.jinahya.persistence.mapped.__MappedEntity;
import jakarta.annotation.Nonnull;
import uk.co.jemos.podam.api.DataProviderStrategy;
import uk.co.jemos.podam.api.PodamFactory;

import java.util.Objects;

/**
 * An abstract class for randomizing subclasses of {@link __MappedEntity}.
 *
 * @param <ENTITY> entity type parameter
 * @param <ID>     id type parameter
 */
@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119"  // Type parameter names should comply with a naming convention
})
public abstract class __MappedEntityRandomizer<ENTITY extends __MappedEntity<ENTITY, ID>, ID>
        extends ___Randomizer<ENTITY> {

    /**
     * Creates a new instance for randomizing the specified entity class.
     *
     * @param entityClass the entity class to be randomized.
     * @param idClass     the type of {@link ID} of the {@code entityClass}.
     * @see #entityClass
     * @see #idClass
     */
    protected __MappedEntityRandomizer(final Class<ENTITY> entityClass, final Class<ID> idClass) {
        super(entityClass);
        this.entityClass = Objects.requireNonNull(entityClass, "entityClass is null");
        this.idClass = Objects.requireNonNull(idClass, "idClass is null");
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Nonnull
    @Override
    @SuppressWarnings({
            "java:S1855" // Overriding methods should do more than simply call the same method in the super class
    })
    protected DataProviderStrategy getDataProviderStrategy() {
        return super.getDataProviderStrategy();
    }

    @Nonnull
    @Override
    @SuppressWarnings({
            "java:S1855" // Overriding methods should do more than simply call the same method in the super class
    })
    protected PodamFactory getPodamFactory() {
        return super.getPodamFactory();
    }

    @Nonnull
    @Override
    public ENTITY get() {
        return super.get();
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The entity class to be randomized.
     */
    protected final Class<ENTITY> entityClass;

    /**
     * The type of {@link ID} of the {@code #entityClass}.
     */
    protected final Class<ID> idClass;
}
