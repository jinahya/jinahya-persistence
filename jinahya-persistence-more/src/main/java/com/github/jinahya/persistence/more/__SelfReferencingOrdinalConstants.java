package com.github.jinahya.persistence.more;

/*-
 * #%L
 * jinahya-persistence-more
 * %%
 * Copyright (C) 2025 - 2026 Jinahya, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

/**
 * Constants for the sibling ordinal of {@link __SelfReferencingOrdered}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({
        "java:S114" // Interface names should comply with a naming convention
})
public final class __SelfReferencingOrdinalConstants {

    /**
     * The ordinal of the first sibling, which is the smallest ordinal any instance may hold. The value is {@value}.
     *
     * @see __SelfReferencingOrdered
     */
    public static final int MIN_ORDINAL = 0;

    /**
     * Creates a new instance, which is not allowed.
     */
    private __SelfReferencingOrdinalConstants() {
        throw new AssertionError("instantiation is not allowed");
    }
}
