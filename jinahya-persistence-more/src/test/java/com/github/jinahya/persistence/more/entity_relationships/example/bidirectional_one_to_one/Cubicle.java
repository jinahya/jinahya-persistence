package com.github.jinahya.persistence.more.entity_relationships.example.bidirectional_one_to_one;

import com.github.jinahya.persistence.more.entity_relationships.__UnidirectionalOneToOne;
import com.github.jinahya.persistence.more.entity_relationships.___OwningSide;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;

@Entity
public class Cubicle implements __UnidirectionalOneToOne<Employee> {

    private Employee residentEmployee;

    @___OwningSide(value = Employee.class, inverseSide = Cubicle.class)
    @OneToOne(mappedBy = "assignedCubicle")
    public Employee getResidentEmployee() {
        return residentEmployee;
    }

    public void setResidentEmployee(Employee employee) {
        this.residentEmployee = employee;
    }

    // ...
}
