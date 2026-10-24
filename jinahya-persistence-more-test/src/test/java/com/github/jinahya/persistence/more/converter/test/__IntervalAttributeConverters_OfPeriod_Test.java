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

import java.time.Period;
import java.util.List;

class __IntervalAttributeConverters_OfPeriod_Test
        extends __AttributeConverter_Test<__IntervalAttributeConverters.OfPeriod, Period, Object> {

    __IntervalAttributeConverters_OfPeriod_Test() {
        super(__IntervalAttributeConverters.OfPeriod.class);
    }

    @Override
    protected List<__AttributeConverterTestCase<Period, Object>> testCases() {
        return List.of(
                __AttributeConverterTestCase.of(Period.ZERO, "P0D"),
                __AttributeConverterTestCase.of(Period.of(1, 2, 3), "P1Y2M3D"),
                __AttributeConverterTestCase.of(Period.ofMonths(14), "P14M"),
                __AttributeConverterTestCase.of(Period.ofDays(-45), "P-45D")
        );
    }
}
