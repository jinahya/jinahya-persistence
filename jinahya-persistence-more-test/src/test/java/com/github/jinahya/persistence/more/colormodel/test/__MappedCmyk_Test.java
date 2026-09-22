package com.github.jinahya.persistence.more.colormodel.test;

import com.github.jinahya.persistence.more.colormodel.__MappedCmyk;

/**
 * Tests {@link __MappedCmyk} against what every colour model is expected to do.
 */
class __MappedCmyk_Test extends ___MappedColor_Test<__MappedCmyk_Test.Color> {

    static final class Color extends __MappedCmyk {

    }

    __MappedCmyk_Test() {
        super(Color.class);
    }
}
