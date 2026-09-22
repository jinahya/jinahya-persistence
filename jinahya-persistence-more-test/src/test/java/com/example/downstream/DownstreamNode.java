package com.example.downstream;

import com.github.jinahya.persistence.more.__SelfReferencingOrdered;
import com.github.jinahya.persistence.more.__SelfReferencingOrdinal;
import com.github.jinahya.persistence.more.__SelfReferencingParent;
import com.github.jinahya.persistence.more.__SelfReferencingUtils;
import org.jspecify.annotations.Nullable;

/** A hierarchy a downstream maps, answering through the marked members. */
public class DownstreamNode implements __SelfReferencingOrdered<DownstreamNode> {

    @Nullable
    @Override
    public DownstreamNode getHierarchyParent() {
        return __SelfReferencingUtils.parentOf(this);
    }

    @Override
    public int getHierarchyDepth() {
        return parent == null ? 0 : parent.getHierarchyDepth() + 1;
    }

    @__SelfReferencingParent
    private @Nullable DownstreamNode parent;

    @__SelfReferencingOrdinal
    private @Nullable Integer ordinal;
}
