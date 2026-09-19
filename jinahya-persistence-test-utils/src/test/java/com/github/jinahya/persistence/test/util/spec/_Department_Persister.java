package com.github.jinahya.persistence.test.util.spec;

import com.github.jinahya.persistence.test.util.__Persister;

/**
 * The persister located, by the naming convention, for {@link _Department}.
 * <p>
 * A department requires nothing to be persisted first, so this class adds nothing to
 * {@link __Persister#apply(jakarta.persistence.EntityManager, Object)}. It exists because the convention is what makes
 * a class persistable at all: without it, {@link _Employee_Persister} could not ask for a persisted department.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public class _Department_Persister extends __Persister<_Department> {

    /**
     * Creates a new instance.
     */
    public _Department_Persister() {
        super(_Department.class);
    }
}
