package com.github.jinahya.persistence.more.entity_relationships.example.__unidirectional_many_to_many;

import com.github.jinahya.persistence.more.entity_relationships.__UnidirectionalManyToMany;
import com.github.jinahya.persistence.more.entity_relationships.___Owned;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;

import java.util.Collection;

@Entity
public class Employee implements __UnidirectionalManyToMany<Parent> {

    private Collection<Parent> patents;

    @___Owned(value = Parent.class, owner = Employee.class)
    @ManyToMany
    public Collection<Parent> getPatents() {
        return patents;
    }

    public void setPatents(Collection<Parent> patents) {
        this.patents = patents;
    }

    // ...
}
