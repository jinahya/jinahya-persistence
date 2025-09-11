package com.github.jinahya.persistence.more.entity_relationships.example.bidirectional_many_to_many;

import com.github.jinahya.persistence.more.entity_relationships.__BidirectionalManyToMany;
import com.github.jinahya.persistence.more.entity_relationships.___InverseSide;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import java.util.Collection;

@Entity
@Table(catalog = "entity_relationships", schema = "bidirectional_many_to_many", name = "PROJECT")
public class Project implements __BidirectionalManyToMany<Project, Employee> {

    private Collection<Employee> employees;

    @___InverseSide(value = Employee.class, owningSide = Project.class)
    @ManyToMany
    @JoinTable(
            name = "PROJECT_EMPLOYEE",
            joinColumns = {
                    @JoinColumn(name = "PROJECTS_<PK of PROJECT>")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "EMPLOYEES_<PK of EMPLOYEE")
            }
    )
    public Collection<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(Collection<Employee> employees) {
        this.employees = employees;
    }

    // ...
}
