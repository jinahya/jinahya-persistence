package com.github.jinahya.persistence.more.entity_relationships.example.bidirectional_many_to_many;

import com.github.jinahya.persistence.more.entity_relationships.__UnidirectionalManyToMany;
import com.github.jinahya.persistence.more.entity_relationships.___OwningSide;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import java.util.Collection;

@Entity
@Table(catalog = "entity_relationships", schema = "bidirectional_many_to_many", name = "EMPLOYEE")
public class Employee implements __UnidirectionalManyToMany<Project> {

    private Collection<Project> projects;

    @___OwningSide(value = Project.class, inverseSide = Employee.class)
    @ManyToMany(mappedBy = "employees")
    public Collection<Project> getProjects() {
        return projects;
    }

    public void setProjects(Collection<Project> projects) {
        this.projects = projects;
    }

    // ...
}
