package com.github.jinahya.persistence.mapped.test.examples.inheritance.single_table;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("S")
public class SmallProject extends Project {

}
