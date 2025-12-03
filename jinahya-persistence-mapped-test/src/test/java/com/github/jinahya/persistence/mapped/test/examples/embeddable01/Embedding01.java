package com.github.jinahya.persistence.mapped.test.examples.embeddable01;

import com.github.jinahya.persistence.mapped.__MappedEntity;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "embedding_01")
@Setter
@Getter
public class Embedding01 extends __MappedEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // -----------------------------------------------------------------------------------------------------------------
    @Embedded
    private Embeddable01 embeddable01;
}
