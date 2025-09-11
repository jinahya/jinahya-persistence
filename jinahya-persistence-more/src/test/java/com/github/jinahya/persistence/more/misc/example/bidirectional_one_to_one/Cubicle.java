package com.github.jinahya.persistence.more.misc.example.bidirectional_one_to_one;

import com.github.jinahya.persistence.more.misc.__UnidirectionalOneToOne;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;

@Entity
public class Cubicle implements __UnidirectionalOneToOne<Cubicle, Employee> {

    private Employee residentEmployee;

    @OneToOne(mappedBy = "assignedCubicle")
    public Employee getResidentEmployee() {
        return residentEmployee;
    }

    public void setResidentEmployee(Employee employee) {
        this.residentEmployee = employee;
    }

    // ...
}
