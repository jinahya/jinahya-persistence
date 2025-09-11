package com.github.jinahya.persistence.more.entity_relationships.example.__unidirectional_one_to_many;

import com.github.jinahya.persistence.more.entity_relationships.__UnidirectionalOneToMany;
import com.github.jinahya.persistence.more.entity_relationships.___OwningSide;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.Collection;

@___OwningSide
@Entity
@Table(catalog = "entity_relationships", schema = "unidirectional_one_to_many", name = "EMPLOYEE")
public class Employee implements __UnidirectionalOneToMany<AnnualReview> {

    private Collection<AnnualReview> annualReviews;

    @OneToMany
    @JoinTable(
            name = "EMPLOYEE_ANNUALREVIEWS",
            joinColumns = {
                    @JoinColumn(name = "EMPLOYEE_<PK of EMPLOYEE>")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "ANNUALREVIEW_<PK of ANNUALREVIEW>", unique = true)
            }
    )
    public Collection<AnnualReview> getAnnualReviews() {
        return annualReviews;
    }

    public void setAnnualReviews(Collection<AnnualReview> annualReviews) {
        this.annualReviews = annualReviews;
    }

    // ...
}
