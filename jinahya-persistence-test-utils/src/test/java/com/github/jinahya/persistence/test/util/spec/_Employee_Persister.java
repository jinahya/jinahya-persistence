package com.github.jinahya.persistence.test.util.spec;

import com.github.jinahya.persistence.test.util.AbstractEntityPersister;
import com.github.jinahya.persistence.test.util.EntityPersister;
import com.github.jinahya.persistence.test.util.EntityPersisterUtils;
import jakarta.persistence.EntityManager;

import java.util.Objects;

/**
 * The persister located, by the naming convention, for {@link _Employee}.
 * <p>
 * An {@code _Employee} may not be written without a {@link _Department}, and the randomizer deliberately leaves that
 * association alone; supplying it is this class's job, and is the reason {@link EntityPersister} is a role of its
 * own rather than a call to {@link EntityManager#persist(Object)}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public class _Employee_Persister
        extends AbstractEntityPersister<_Employee> {

    /**
     * Creates a new instance.
     *
     * @apiNote The no-argument constructor is what {@link EntityPersisterUtils} instantiates a located persister
     *         by.
     */
    public _Employee_Persister() {
        super(_Employee.class);
    }

    /**
     * {@inheritDoc}
     *
     * @param entityManager  {@inheritDoc}
     * @param entityInstance {@inheritDoc}
     * @return {@inheritDoc}
     * @implSpec Persists a new {@link _Department}, through
     *         {@link EntityPersisterUtils#newPersistedInstanceOf(EntityManager, Class)}, and assigns it, when the
     *         {@code entityInstance} carries none; then persists the {@code entityInstance} itself, through
     *         {@code super.apply(...)}. A department already assigned -- by a caller which wants two employees in one
     *         department -- is left as it is.
     */
    @Override
    public _Employee apply(final EntityManager entityManager, final _Employee entityInstance) {
        Objects.requireNonNull(entityManager, "entityManager is null");
        Objects.requireNonNull(entityInstance, "entityInstance is null");
        if (entityInstance.getDepartment() == null) {
            entityInstance.setDepartment(
                    EntityPersisterUtils.newPersistedInstanceOf(entityManager, _Department.class)
            );
        }
        return super.apply(entityManager, entityInstance);
    }
}
