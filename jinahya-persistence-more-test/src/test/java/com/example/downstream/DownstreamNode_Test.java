package com.example.downstream;

import com.github.jinahya.persistence.more.test.__SelfReferencingOrdered_Test;

/** Extends the published self-referencing base from outside its package, as a consumer does. */
class DownstreamNode_Test extends __SelfReferencingOrdered_Test<DownstreamNode> {

    DownstreamNode_Test() {
        super(DownstreamNode.class);
    }
}
