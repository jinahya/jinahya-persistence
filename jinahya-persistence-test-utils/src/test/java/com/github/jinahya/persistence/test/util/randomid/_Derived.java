package com.github.jinahya.persistence.test.util.randomid;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * An entity with an {@link IdClass @IdClass}, one of whose identifier attributes is an association; a derived
 * identity.
 */
@Entity
@IdClass(_DerivedId.class)
@Table(name = "random_id_derived")
public class _Derived {

    public static _Derived of(final _Basic basic, final int seq) {
        final var instance = new _Derived();
        instance.basic = basic;
        instance.seq = seq;
        return instance;
    }

    @Id
    @ManyToOne(optional = false)
    @JoinColumn(name = "basic_id", nullable = false)
    private _Basic basic;

    @Id
    private int seq;
}
