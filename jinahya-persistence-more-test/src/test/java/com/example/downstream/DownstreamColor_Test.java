package com.example.downstream;

import com.github.jinahya.persistence.more.colormodel.test.___MappedColor_Test;

/** Extends the published colour base from outside its package, as a consumer does. */
class DownstreamColor_Test extends ___MappedColor_Test<DownstreamColor> {

    DownstreamColor_Test() {
        super(DownstreamColor.class);
    }
}
