package com.github.jinahya.persistence.more.test;

import com.github.jinahya.persistence.more.__SelfReferencing;
import com.github.jinahya.persistence.more.__SelfReferencingParent;
import com.github.jinahya.persistence.more.__SelfReferencingUtils;

/**
 * A self-referencing sample which answers through the marked member, which is the path
 * {@link __SelfReferencing_Test} exists to check.
 */
class _SampleNode implements __SelfReferencing<_SampleNode> {

    @Override
    public _SampleNode getHierarchyParent() {
        return __SelfReferencingUtils.parentOf(this);
    }

    @Override
    public int getHierarchyDepth() {
        return parent == null ? 0 : parent.getHierarchyDepth() + 1;
    }

    @__SelfReferencingParent
    private _SampleNode parent;
}
