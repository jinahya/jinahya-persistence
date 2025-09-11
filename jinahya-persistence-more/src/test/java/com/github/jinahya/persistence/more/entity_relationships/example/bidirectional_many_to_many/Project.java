package com.github.jinahya.persistence.more.entity_relationships.example.bidirectional_many_to_many;

import com.github.jinahya.persistence.more.entity_relationships.__UnidirectionalManyToMany;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;

import java.util.Collection;

@Entity
public class Project implements __UnidirectionalManyToMany<Employee> {

    private Collection<Project> employees;

    @ManyToMany
    public Collection<Project> getEmployees() {
        return employees;
    }

    public void setEmployees(Collection<Project> employees) {
        this.employees = employees;
    }

    // ...
}
