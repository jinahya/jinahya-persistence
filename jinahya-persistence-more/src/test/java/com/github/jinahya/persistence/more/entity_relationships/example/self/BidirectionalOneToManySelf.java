package com.github.jinahya.persistence.more.entity_relationships.example.self;

import com.github.jinahya.persistence.more.entity_relationships.___InverseSide;
import com.github.jinahya.persistence.more.entity_relationships.___Owned;
import com.github.jinahya.persistence.more.entity_relationships.self.__BidirectionalManyToOneSelf;
import com.github.jinahya.persistence.more.entity_relationships.self.__BidirectionalOneToManySelf;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

import java.util.Collection;

@Entity
public class BidirectionalOneToManySelf
        implements __BidirectionalOneToManySelf<BidirectionalOneToManySelf>,
                   __BidirectionalManyToOneSelf<BidirectionalOneToManySelf> {

    @Id
    private Long id;

    @___Owned(value = UnidirectionalManyToOneSelf.class, owner = BidirectionalOneToManySelf.class)
    @ManyToOne
    @JoinColumn(name = "PARENT_<PK of UNIDIRECTIONALMANYTOONESELF>")
    private UnidirectionalManyToOneSelf parent;

    @___InverseSide(value = BidirectionalOneToManySelf.class, owningSide = UnidirectionalManyToOneSelf.class)
    @OneToMany(mappedBy = "parent")
    private Collection<UnidirectionalManyToOneSelf> children;
}
