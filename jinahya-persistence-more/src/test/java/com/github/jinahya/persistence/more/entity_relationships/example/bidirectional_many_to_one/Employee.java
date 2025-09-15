package com.github.jinahya.persistence.more.entity_relationships.example.bidirectional_many_to_one;

import com.github.jinahya.persistence.more.entity_relationships.__BidirectionalManyToOne;
import com.github.jinahya.persistence.more.entity_relationships.___InverseSide;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(catalog = "entity_relationships", schema = "bidirectional_many_to_one", name = "EMPLOYEE")
public class Employee implements __BidirectionalManyToOne<Employee, Department> {

    private Department department;

    @___InverseSide(owningSide = Employee.class)
    @ManyToOne
    @JoinColumn(name = "DEPARTMENT_<PK of DEPARTMENT>", referencedColumnName = "<PK of DEPARTMENT>")
    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    // ...
}
