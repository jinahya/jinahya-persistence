package com.github.jinahya.persistence.test.util.spec;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * The specification's {@code Department} example; the owning side of the association an {@link _Employee} requires.
 * <p>
 * The {@code name} is unique, deliberately: a randomizer which produced a constant -- which is what
 * {@link com.github.jinahya.persistence.test.util.__Randomizer.___OfEasyRandom ___OfEasyRandom} would do with a
 * fixed seed -- would persist the first instance and fail on the second, and that failure is worth having.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see _Department_Randomizer
 * @see _Department_Persister
 */
@Access(AccessType.FIELD)
@Entity
@Table(name = _Department.TABLE_NAME)
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public class _Department {

    /**
     * The name of the table to which this entity maps.
     */
    public static final String TABLE_NAME = "spec_department";

    /**
     * The name of the column to which the {@code id} attribute maps.
     */
    public static final String COLUMN_NAME_ID = "department_id";

    /**
     * The name of the column to which the {@code name} attribute maps.
     */
    public static final String COLUMN_NAME_NAME = "name";

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public String toString() {
        return super.toString() + '{'
               + "id=" + id
               + ",name=" + name
               + '}';
    }

    // -----------------------------------------------------------------------------------------------------------------
    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Set<_Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(final Set<_Employee> employees) {
        this.employees = employees;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = COLUMN_NAME_ID, nullable = false)
    private Long id;

    @Column(name = COLUMN_NAME_NAME, nullable = false, unique = true)
    private String name;

    /**
     * The inverse side of {@link _Employee#getDepartment()}.
     *
     * @implNote Excluded from randomization. It is not the owning side, so nothing here is written on persist,
     *         and a randomizer which filled it would have to invent {@link _Employee} instances -- each of which
     *         requires a {@code _Department}, which is where this started.
     */
    @OneToMany(mappedBy = _Employee.ATTRIBUTE_NAME_DEPARTMENT)
    private Set<_Employee> employees = new LinkedHashSet<>();
}
