package com.github.jinahya.persistence.more.entity_relationships.example.bidirectional_many_to_one;

import com.github.jinahya.persistence.more.entity_relationships.__UnidirectionalOneToMany;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

import java.util.Collection;
import java.util.HashSet;

@Entity
public class Department implements __UnidirectionalOneToMany<Employee> {

    private Collection<Employee> employees = new HashSet();

    @OneToMany(mappedBy = "department")
    public Collection<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(Collection<Employee> employees) {
        this.employees = employees;
    }

    // ...
}
