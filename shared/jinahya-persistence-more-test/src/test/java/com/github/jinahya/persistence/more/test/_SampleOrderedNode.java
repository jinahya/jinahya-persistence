package com.github.jinahya.persistence.more.test;

import com.github.jinahya.persistence.more.__SelfReferencingOrdered;
import com.github.jinahya.persistence.more.__SelfReferencingOrdinal;
import com.github.jinahya.persistence.more.__SelfReferencingParent;
import com.github.jinahya.persistence.more.__SelfReferencingUtils;

/**
 * An ordered self-referencing sample which keeps the default {@code getSiblingOrdinal()}, so that the marked ordinal
 * member is what answers it.
 */
class _SampleOrderedNode implements __SelfReferencingOrdered<_SampleOrderedNode> {

    @Override
    public _SampleOrderedNode getHierarchyParent() {
        return __SelfReferencingUtils.parentOf(this);
    }

    @Override
    public int getHierarchyDepth() {
        return parent == null ? 0 : parent.getHierarchyDepth() + 1;
    }

    @__SelfReferencingParent
    private _SampleOrderedNode parent;

    // an Integer, so that an unassigned ordinal reads null and is reported by @NotNull rather than read as zero
    @__SelfReferencingOrdinal
    private Integer ordinal;
}
