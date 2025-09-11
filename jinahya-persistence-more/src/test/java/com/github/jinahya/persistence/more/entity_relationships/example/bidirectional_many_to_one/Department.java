package com.github.jinahya.persistence.more.entity_relationships.example.bidirectional_many_to_one;

import com.github.jinahya.persistence.more.entity_relationships.__UnidirectionalOneToMany;
import com.github.jinahya.persistence.more.entity_relationships.___OwningSide;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.Collection;
import java.util.HashSet;

@Entity
@Table(catalog = "entity_relationships", schema = "bidirectional_many_to_one", name = "DEPARTMENT")
public class Department implements __UnidirectionalOneToMany<Employee> {

    private Collection<Employee> employees = new HashSet();

    @___OwningSide(inverseSide = Department.class)
    @OneToMany(mappedBy = "department")
    public Collection<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(Collection<Employee> employees) {
        this.employees = employees;
    }

    // ...
}
