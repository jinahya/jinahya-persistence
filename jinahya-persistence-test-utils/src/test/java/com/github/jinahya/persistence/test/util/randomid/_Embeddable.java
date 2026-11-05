package com.github.jinahya.persistence.test.util.randomid;

import jakarta.persistence.Embeddable;

import java.util.Objects;

/**
 * The {@link jakarta.persistence.EmbeddedId @EmbeddedId} of {@link _Embedded}.
 */
@Embeddable
public class _Embeddable {

    public static _Embeddable of(final int a, final String b) {
        final var instance = new _Embeddable();
        instance.a = a;
        instance.b = b;
        return instance;
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof _Embeddable that)) {
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
