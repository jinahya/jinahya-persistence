package com.github.jinahya.persistence.more.converter.test;

/*-
 * #%L
 * jinahya-persistence-more-test
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

import com.github.jinahya.persistence.more.converter.postgresql.__ArrayAttributeConverters;

import java.util.Arrays;
import java.util.List;

class __ArrayAttributeConverters_OfStrings_Test
        extends __AttributeConverter_Test<__ArrayAttributeConverters.OfStrings, List<String>, Object> {

    __ArrayAttributeConverters_OfStrings_Test() {
        super(__ArrayAttributeConverters.OfStrings.class);
    }

    @Override
    protected List<__AttributeConverterTestCase<List<String>, Object>> testCases() {
        return List.of(
                __AttributeConverterTestCase.of(List.of(), "{}"),
                __AttributeConverterTestCase.of(List.of("a", "b"), "{a,b}"),
                __AttributeConverterTestCase.of(Arrays.asList("a", null), "{a,NULL}"),
                __AttributeConverterTestCase.of(List.of("", "NULL", "a b", "a,b"), "{\"\",\"NULL\",\"a b\",\"a,b\"}"),
                __AttributeConverterTestCase.of(List.of("say \"hi\"", "a\\b"), "{\"say \\\"hi\\\"\",\"a\\\\b\"}")
        );
    }
}
