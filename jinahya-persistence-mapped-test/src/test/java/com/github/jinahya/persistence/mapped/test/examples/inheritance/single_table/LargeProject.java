package com.github.jinahya.persistence.mapped.test.examples.inheritance.single_table;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.math.BigDecimal;

@Entity
@DiscriminatorValue("L")
public class LargeProject extends Project {

    private BigDecimal budget;
}
