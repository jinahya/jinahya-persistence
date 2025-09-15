package com.github.jinahya.persistence.more.entity_relationships.example.self;

import com.github.jinahya.persistence.more.entity_relationships.self.__UnidirectionalManyToOneSelf;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class UnidirectionalManyToOneSelf
        implements __UnidirectionalManyToOneSelf<UnidirectionalManyToOneSelf> {

    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "PARENT_<PK of UNIDIRECTIONALMANYTOONESELF>")
    private UnidirectionalManyToOneSelf parent;
}
