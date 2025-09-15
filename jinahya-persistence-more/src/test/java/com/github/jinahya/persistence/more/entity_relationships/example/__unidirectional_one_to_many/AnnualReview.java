package com.github.jinahya.persistence.more.entity_relationships.example.__unidirectional_one_to_many;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(catalog = "entity_relationships", schema = "unidirectional_one_to_many", name = "ANNUALREVIEW")
public class AnnualReview {
    // ...
}
