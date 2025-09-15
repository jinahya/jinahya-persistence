package com.github.jinahya.persistence.more.entity_relationships.example.__unidirectional_many_to_one;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(catalog = "entity_relationships", schema = "unidirectional_many_to_one", name = "ADDRESS")
public class Address {
    // ...
}
