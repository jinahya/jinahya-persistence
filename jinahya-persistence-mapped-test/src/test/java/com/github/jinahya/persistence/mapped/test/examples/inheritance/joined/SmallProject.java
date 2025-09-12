package com.github.jinahya.persistence.mapped.test.examples.inheritance.joined;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@DiscriminatorValue("S")
@Table(name = "SMALLPROJECT")
public class SmallProject extends Project {

}
