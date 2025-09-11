package com.github.jinahya.persistence.more.entity_relationships.example.__unidirectional_many_to_one;

import com.github.jinahya.persistence.more.entity_relationships.__UnidirectionalManyToOne;
import com.github.jinahya.persistence.more.entity_relationships.___Owned;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(catalog = "entity_relationships", schema = "unidirectional_many_to_one", name = "EMPLOYEE")
public class Employee implements __UnidirectionalManyToOne<Address> {

    private Address address;

    @___Owned(value = Address.class, owner = Employee.class)
    @ManyToOne
    @JoinColumn(name = "ADDRESS_<PK of ADDRESS>", referencedColumnName = "<PK of ADDRESS>")
    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    // ...
}
