package com.github.jinahya.persistence.mapped.test.examples.embeddable01;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Setter
@Getter
public class Embeddable01 {

    private String name;

    private Integer age;
}
