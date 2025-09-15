package com.github.jinahya.persistence.mapped.test.examples.inheritance.joined;

import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "PROJ_TYPE")
@Table(name = "PROJECT")
public abstract class Project {

    @Id
    private long id;

    private String name;
}
