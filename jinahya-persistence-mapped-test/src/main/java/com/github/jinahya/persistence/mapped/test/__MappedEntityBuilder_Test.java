package com.github.jinahya.persistence.mapped.test;

import com.github.jinahya.persistence.mapped.__MappedEntity;
import com.github.jinahya.persistence.mapped.__MappedEntityBuilder;

import java.util.Objects;

/**
 * An abstract class for testing subclasses of {@link __MappedEntityBuilder}.
 *
 * @param <BUILDER> builder type parameter
 * @param <ENTITY>  entity type parameter
 * @param <ID>      id type parameter
 */
@SuppressWarnings({
        "java:S101", // Class names should comply with a naming convention
        "java:S119", // Type parameter names should comply with a naming convention
})
public abstract class __MappedEntityBuilder_Test<
        BUILDER extends __MappedEntityBuilder<BUILDER, ENTITY>,
        ENTITY extends __MappedEntity<ID>,
        ID
        > extends __MappedBuilder_Test<BUILDER, ENTITY> {

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance for testing specified builder class.
     *
     * @param builderClass the builder class to test.
     * @param entityClass  a target entity class of the {@code builderClass}.
     * @param idClass      an id class of the {@code entityClass}.
     */
    protected __MappedEntityBuilder_Test(final Class<BUILDER> builderClass, final Class<ENTITY> entityClass,
                                         final Class<ID> idClass) {
        super(builderClass, entityClass);
        this.idClass = Objects.requireNonNull(idClass, "idClass is null");
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The id class of the {@link #targetClass}.
     */
    protected final Class<ID> idClass;
}
