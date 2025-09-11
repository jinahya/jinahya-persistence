package com.github.jinahya.persistence.more.misc.example.unidirectional_one_to_many;

import com.github.jinahya.persistence.more.misc.__UnidirectionalOneToMany;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

import java.util.Collection;

@Entity
public class Employee implements __UnidirectionalOneToMany<Employee, AnnualReview> {

    private Collection<AnnualReview> annualReviews;

    @OneToMany
    public Collection<AnnualReview> getAnnualReviews() {
        return annualReviews;
    }

    public void setAnnualReviews(Collection<AnnualReview> annualReviews) {
        this.annualReviews = annualReviews;
    }

    // ...
}
