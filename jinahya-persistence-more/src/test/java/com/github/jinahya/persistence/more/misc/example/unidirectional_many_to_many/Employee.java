package com.github.jinahya.persistence.more.misc.example.unidirectional_many_to_many;

import com.github.jinahya.persistence.more.misc.__UnidirectionalManyToMany;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;

import java.util.Collection;

@Entity
public class Employee implements __UnidirectionalManyToMany<Employee, Parent> {

    private Collection<Parent> patents;

    @ManyToMany
    public Collection<Parent> getPatents() {
        return patents;
    }

    public void setPatents(Collection<Parent> patents) {
        this.patents = patents;
    }

    // ...
}
