package com.github.jinahya.persistence.more.entity_relationships.example.bidirectional_one_to_one;

import com.github.jinahya.persistence.more.entity_relationships._BidirectionalOneToOne;
import com.github.jinahya.persistence.more.entity_relationships.___OwningSide;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@___OwningSide
@Entity
public class Employee implements _BidirectionalOneToOne<Employee, Cubicle> {

    @JoinColumn(name = "ASSIGNEDCUBICLE_<PK of CUBICLE>")
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
