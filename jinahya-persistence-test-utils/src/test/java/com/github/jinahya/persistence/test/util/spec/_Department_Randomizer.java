package com.github.jinahya.persistence.test.util.spec;

import com.github.jinahya.persistence.test.util.__Randomizer;
import uk.co.jemos.podam.api.ClassInfoStrategy;
import uk.co.jemos.podam.api.DataProviderStrategy;
import uk.co.jemos.podam.api.PodamFactory;

/**
 * The randomizer located, by the naming convention, for {@link _Department}.
 * <p>
 * {@link _Department} declares no instantiator of its own, and so is built by the no-argument constructor -- which is
 * what keeps its {@code employees} set non-{@code null}. The PODAM flavor is used here for that very reason: it
 * populates the instance that {@code newTargetInstance()} returns, initializers and all.
 * <p>
 * The three strategy hooks below are overridden, and do nothing but delegate; they are here so that a reader can see at
 * a glance which hooks {@link __Randomizer.___OfPodam} offers, and where a subclass would step in. Only {@link #get()}
 * adds anything, and what it adds is an assertion.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public class _Department_Randomizer extends __Randomizer.___OfPodam<_Department> {

    /**
     * Creates a new instance.
     */
    public _Department_Randomizer() {
        super(_Department.class, _Department_Randomizer_Constants.EXCLUDED_FIELDS);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    protected DataProviderStrategy getDataProviderStrategy() {
        return super.getDataProviderStrategy();
    }

    @Override
    protected ClassInfoStrategy getClassInfoStrategy() {
        return super.getClassInfoStrategy();
    }

    @Override
    protected PodamFactory getPodamFactory() {
        return super.getPodamFactory();
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     * @implSpec Asserts the contract on the produced value: the excluded {@code id} is still {@code null}, the
     *         excluded {@code employees} is still the empty collection the constructor assigned -- neither {@code null}
     *         nor populated -- and the {@code name}, which is excluded from nothing, is filled. Assertions are enabled
     *         under Surefire, which is the only place this runs.
     */
    @Override
    public _Department get() {
        final var value = super.get();
        assert value != null : "null instance";
        assert value.getId() == null : "the excluded 'id' was randomized: " + value.getId();
        assert value.getName() != null : "'name' was not randomized";
        assert value.getEmployees() != null : "the excluded 'employees' was replaced with null";
        assert value.getEmployees().isEmpty() : "the excluded 'employees' was randomized: " + value.getEmployees();
        return value;
    }
}
