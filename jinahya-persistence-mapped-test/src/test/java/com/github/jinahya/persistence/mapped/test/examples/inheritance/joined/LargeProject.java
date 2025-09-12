package com.github.jinahya.persistence.mapped.test.examples.inheritance.joined;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@DiscriminatorValue("L")
@Table(name = "LARGEPROJECT")
public class LargeProject extends Project {

    private BigDecimal budget;
}
