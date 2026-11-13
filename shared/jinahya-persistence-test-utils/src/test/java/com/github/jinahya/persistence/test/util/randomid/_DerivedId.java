package com.github.jinahya.persistence.test.util.randomid;

import java.util.Objects;

/**
 * The {@link jakarta.persistence.IdClass @IdClass} of {@link _Derived}; {@code basic} holds the identifier of the
 * associated {@link _Basic}.
 */
public class _DerivedId {

    public static _DerivedId of(final long basic, final int seq) {
        final var instance = new _DerivedId();
        instance.basic = basic;
        instance.seq = seq;
        return instance;
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof _DerivedId that)) {
            return false;
        }
        return basic == that.basic && seq == that.seq;
    }

    @Override
    public int hashCode() {
        return Objects.hash(basic, seq);
    }

    private long basic;

    private int seq;
}
