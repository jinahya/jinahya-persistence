package com.github.jinahya.persistence.test.util.spec;

import com.github.jinahya.object.randomizer.AbstractObjectRandomizer;
import com.github.jinahya.object.randomizer.InstancioObjectRandomizer;

/**
 * A Instancio randomizer of {@link _Department}.
 * <p>
 * This class is <strong>not</strong> named by the convention that
 * {@link com.github.jinahya.object.randomizer.ObjectRandomizerUtils ObjectRandomizerUtils} probes -- which is only
 * {@code _DepartmentRandomizer} and {@code _Department_Randomizer} -- so it is never located, and never competes with
 * {@link _Department_Randomizer}, the PODAM one the persistence tests actually use. It exists so that this flavor of
 * {@link AbstractObjectRandomizer} is exercised against a real entity rather than only against a shape invented in a
 * unit test.
 * <p>
 * The instance comes from {@link AbstractObjectRandomizer#newTargetInstance()}, and is filled in place; whatever the
 * constructor assigned is retained.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see _Department_Randomizer
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public class _Department_Randomizer_Instancio extends InstancioObjectRandomizer<_Department> {

    /**
     * Creates a new instance.
     */
    public _Department_Randomizer_Instancio() {
        super(_Department.class, _Department_Randomizer_Constants.EXCLUDED_FIELDS);
    }
}
