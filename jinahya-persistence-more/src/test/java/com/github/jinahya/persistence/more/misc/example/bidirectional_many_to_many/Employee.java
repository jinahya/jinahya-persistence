package com.github.jinahya.persistence.more.misc.example.bidirectional_many_to_many;

import com.github.jinahya.persistence.more.misc.__BidirectionalManyToMany;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;

import java.util.Collection;

@Entity
public class Employee implements __BidirectionalManyToMany<Employee, Project> {

    private Collection<Project> projects;

    @ManyToMany(mappedBy = "employees")
    public Collection<Project> getProjects() {
        return projects;
    }

    public void setProjects(Collection<Project> projects) {
        this.projects = projects;
    }

    // ...
}
