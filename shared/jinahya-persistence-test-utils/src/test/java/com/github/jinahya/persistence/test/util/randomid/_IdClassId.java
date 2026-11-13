package com.github.jinahya.persistence.test.util.randomid;

import java.util.Objects;

/**
 * The {@link jakarta.persistence.IdClass @IdClass} of {@link _IdClass}.
 */
public class _IdClassId {

    public static _IdClassId of(final int a, final String b) {
        final var instance = new _IdClassId();
        instance.a = a;
        instance.b = b;
        return instance;
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof _IdClassId that)) {
            return false;
        }
        return a == that.a && Objects.equals(b, that.b);
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, b);
    }

    private int a;

    private String b;
}
