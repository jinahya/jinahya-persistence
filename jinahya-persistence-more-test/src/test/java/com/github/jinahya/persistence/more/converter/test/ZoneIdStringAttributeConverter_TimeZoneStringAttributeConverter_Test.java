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

import com.github.jinahya.persistence.more.converter.time.ZoneIdStringAttributeConverter;

import java.util.List;
import java.util.TimeZone;

class ZoneIdStringAttributeConverter_TimeZoneStringAttributeConverter_Test
        extends __AttributeConverter_Test<
        ZoneIdStringAttributeConverter.TimeZoneStringAttributeConverter, TimeZone, String> {

    ZoneIdStringAttributeConverter_TimeZoneStringAttributeConverter_Test() {
        super(ZoneIdStringAttributeConverter.TimeZoneStringAttributeConverter.class);
    }

    @Override
    protected List<__AttributeConverterTestCase<TimeZone, String>> testCases() {
        return List.of(
                __AttributeConverterTestCase.of(TimeZone.getTimeZone("Asia/Seoul"), "Asia/Seoul"),
                __AttributeConverterTestCase.of(TimeZone.getTimeZone("GMT"), "GMT"),
                __AttributeConverterTestCase.of(TimeZone.getTimeZone("GMT+09:00"), "GMT+09:00")
        );
    }
}
