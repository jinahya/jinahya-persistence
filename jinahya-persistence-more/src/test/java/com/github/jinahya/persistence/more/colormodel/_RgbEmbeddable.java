package com.github.jinahya.persistence.more.colormodel;

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

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Embeddable;

/**
 * The embeddable form — what a table carrying <em>two</em> colors needs, since a {@code @MappedSuperclass} is
 * inherited once.
 * <p>
 * Concrete, public and with a no-arg constructor, which is what an {@link Embeddable @Embeddable} has to be; it adds
 * nothing of its own. The {@link Access @Access}({@code FIELD}) matches the hierarchy above it.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Embeddable
@Access(AccessType.FIELD)
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public class _RgbEmbeddable extends __MappedRgb {

    /**
     * Creates a new instance.
     */
    public _RgbEmbeddable() {
        super();
    }
}
