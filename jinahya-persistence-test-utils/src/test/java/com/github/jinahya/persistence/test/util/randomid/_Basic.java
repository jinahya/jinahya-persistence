package com.github.jinahya.persistence.test.util.randomid;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * An entity with a single {@link Id @Id} of a primitive type.
 */
@Entity
@Table(name = "random_id_basic")
public class _Basic {

    public static _Basic of(final long id) {
        final var instance = new _Basic();
        instance.id = id;
        return instance;
    }

    @Id
    private long id;
}
