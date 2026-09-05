package com.github.jinahya.persistence.more;

import jakarta.annotation.Nullable;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.PositiveOrZero;

public interface __SelfReferencing<T extends __SelfReferencing<T>> {

    @Nullable
    @Transient
    T getHierarchyParent();

    @PositiveOrZero
    @Transient
    int getHierarchyDepth();
}
