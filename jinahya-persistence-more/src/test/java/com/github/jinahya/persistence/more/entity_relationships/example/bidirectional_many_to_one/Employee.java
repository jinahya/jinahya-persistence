package com.github.jinahya.persistence.more.entity_relationships.example.bidirectional_many_to_one;

import com.github.jinahya.persistence.more.entity_relationships._BidirectionalManyToOne;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

// OWNER
@Entity
public class Employee implements _BidirectionalManyToOne<Employee, Department> {

    @JoinColumn(name = "DEPARTMENT_<PK of DEPARTMENT>")
    private Department department;

    @ManyToOne
    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    // ...
}
