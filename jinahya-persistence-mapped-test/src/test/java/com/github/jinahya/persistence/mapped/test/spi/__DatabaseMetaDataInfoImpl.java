package com.github.jinahya.persistence.mapped.test.spi;

/*-
 * #%L
 * jinahya-persistence-mapped-test
 * %%
 * Copyright (C) 2024 - 2025 Jinahya, Inc.
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

import jakarta.annotation.Nullable;

public class __DatabaseMetaDataInfoImpl implements __DatabaseMetaDataInfo {

    static final String CATALOG = "UNNAMED";

    static final String SCHEMA = "PUBLIC";

    @Nullable
    @Override
    public String getCatalog() {
        return CATALOG;
    }

    @Nullable
    @Override
    public String getSchema() {
        return SCHEMA;
    }
}
