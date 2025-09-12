package com.github.jinahya.persistence.mapped.test.examples.inheritance.table_per_class;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "SMALLPROJECT")
public class SmallProject extends Project {

}
