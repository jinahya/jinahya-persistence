package com.github.jinahya.persistence.test.util.randomid;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * An entity with an {@link EmbeddedId @EmbeddedId}.
 */
@Entity
@Table(name = "random_id_embedded")
public class _Embedded {

    public static _Embedded of(final _Embeddable id) {
        final var instance = new _Embedded();
        instance.id = id;
        return instance;
    }

    @EmbeddedId
    private _Embeddable id;
}
