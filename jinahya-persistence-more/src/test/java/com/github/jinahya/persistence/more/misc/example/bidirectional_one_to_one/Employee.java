package com.github.jinahya.persistence.more.misc.example.bidirectional_one_to_one;

import com.github.jinahya.persistence.more.misc.__BidirectionalOneToOne;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;

@Entity
public class Employee implements __BidirectionalOneToOne<Employee, Cubicle> {

    private Cubicle assignedCubicle;

    @OneToOne
    public Cubicle getAssignedCubicle() {
        return assignedCubicle;
    }

    public void setAssignedCubicle(Cubicle cubicle) {
        this.assignedCubicle = cubicle;
    }

    // ...
}
