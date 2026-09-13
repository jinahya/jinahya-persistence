package com.github.jinahya.persistence.test.util.spec;

import com.github.jinahya.persistence.test.util.__Instantiator;

import java.time.LocalDate;

/**
 * The instantiator located, by the naming convention, for {@link _Employee}.
 * <p>
 * It assigns the hire date, which is the one attribute of an {@code _Employee} that neither the provider nor a
 * randomizer is responsible for. {@link _Employee_Randomizer} excludes the same attribute, so the value assigned here
 * is the value which reaches the database -- which is what makes this the visible half of the instantiator/randomizer
 * handover.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see com.github.jinahya.persistence.test.util.__Randomizer#newTargetInstance()
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public class _Employee_Instantiator extends __Instantiator<_Employee> {

    /**
     * The hire date this instantiator assigns; a constant, so that a test can assert it.
     */
    public static final LocalDate HIRE_DATE = LocalDate.of(2026, 1, 1);

    /**
     * Creates a new instance.
     *
     * @apiNote The no-argument constructor is what {@code __InstantiatorUtils} instantiates a located
     *         instantiator by.
     */
    public _Employee_Instantiator() {
        super(_Employee.class);
    }

    /**
     * {@inheritDoc}
     *
     * @return a new {@link _Employee} whose hire date is {@link #HIRE_DATE}.
     * @implSpec Takes the instance from {@code super.get()}, which uses the no-argument constructor, and
     *         assigns the hire date to it.
     */
    @Override
    public _Employee get() {
        final var instance = super.get();
        instance.setHireDate(HIRE_DATE);
        return instance;
    }
}
