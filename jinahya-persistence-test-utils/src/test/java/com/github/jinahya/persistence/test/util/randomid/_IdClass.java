package com.github.jinahya.persistence.test.util.randomid;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

/**
 * An entity with an {@link IdClass @IdClass}.
 */
@Entity
@IdClass(_IdClassId.class)
@Table(name = "random_id_id_class")
public class _IdClass {

    public static _IdClass of(final int a, final String b) {
        final var instance = new _IdClass();
        instance.a = a;
        instance.b = b;
        return instance;
    }

    @Id
    private int a;

    @Id
    private String b;
}
