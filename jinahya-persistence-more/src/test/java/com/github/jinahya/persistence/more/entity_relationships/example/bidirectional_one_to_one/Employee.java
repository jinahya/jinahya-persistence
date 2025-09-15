package com.github.jinahya.persistence.more.entity_relationships.example.bidirectional_one_to_one;

import com.github.jinahya.persistence.more.entity_relationships.__BidirectionalOneToOne;
import com.github.jinahya.persistence.more.entity_relationships.___InverseSide;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class Employee implements __BidirectionalOneToOne<Employee, Cubicle> {

    private Cubicle assignedCubicle;

    @___InverseSide(value = Cubicle.class, owningSide = Employee.class)
    @OneToOne
    @JoinColumn(name = "ASSIGNEDCUBICLE_<PK of CUBICLE>")
    public Cubicle getAssignedCubicle() {
        return assignedCubicle;
    }

    public void setAssignedCubicle(Cubicle cubicle) {
        this.assignedCubicle = cubicle;
    }

    // ...
}
