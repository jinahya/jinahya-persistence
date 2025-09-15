package com.github.jinahya.persistence.more.entity_relationships.example.self;

import com.github.jinahya.persistence.more.entity_relationships.self.__UnidirectionalOneToManySelf;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class UnidirectionalOneToManySelf
        implements __UnidirectionalOneToManySelf<UnidirectionalOneToManySelf> {

    @Id
    private Long id;
}
