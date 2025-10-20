package com.github.jinahya.persistence.mapped;

/*-
 * #%L
 * jinahya-persistence-mapped
 * %%
 * Copyright (C) 2025 Jinahya, Inc.
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

import lombok.Getter;
import lombok.Setter;

import java.beans.IntrospectionException;

@Setter
@Getter
class SampleMapped extends _Mapped {

    private SampleMapped(final SampleMappedBuilder builder) {
        super();
        try {
            __MappedUtils.setPropertiesFrom(this, builder);
        } catch (final IntrospectionException e) {
            throw new RuntimeException(e);
        }
    }

    private Integer age;

    private String name;
}
