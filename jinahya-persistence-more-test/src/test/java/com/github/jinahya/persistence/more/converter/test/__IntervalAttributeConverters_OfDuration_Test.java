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

import com.github.jinahya.persistence.more.converter.postgresql.__IntervalAttributeConverters;

import java.time.Duration;
import java.util.List;

class __IntervalAttributeConverters_OfDuration_Test
        extends __AttributeConverter_Test<__IntervalAttributeConverters.OfDuration, Duration, Object> {

    __IntervalAttributeConverters_OfDuration_Test() {
        super(__IntervalAttributeConverters.OfDuration.class);
    }

    @Override
    protected List<__AttributeConverterTestCase<Duration, Object>> testCases() {
        return List.of(
                __AttributeConverterTestCase.of(Duration.ZERO, "PT0S"),
                __AttributeConverterTestCase.of(Duration.ofHours(26L).plusMinutes(5L), "PT26H5M"),
                __AttributeConverterTestCase.of(Duration.ofMillis(-1500L), "PT-1.5S"),
                __AttributeConverterTestCase.of(Duration.ofSeconds(1L, 123_456_000L), "PT1.123456S")
        );
    }
}
