package com.github.jinahya.persistence.mapped.test.examples.inheritance.table_per_class;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "LARGEPROJECT")
public class LargeProject extends Project {

    private BigDecimal budget;
}
