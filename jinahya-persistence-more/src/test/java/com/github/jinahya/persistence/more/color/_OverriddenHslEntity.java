package com.github.jinahya.persistence.more.color;

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
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * An entity which renames inherited columns, to verify that {@link jakarta.persistence.Access @Access}({@code FIELD})
 * on the mapped superclasses does not take {@link AttributeOverride @AttributeOverride} away from a downstream user.
 * <p>
 * Both depths are covered: {@code saturation} is declared one level up, in {@link __MappedHsl}, and {@code hue} two
 * levels up, in {@link ___MappedHueColor}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@AttributeOverride(
        name = ___MappedHueColor.ATTRIBUTE_NAME_HUE,
        column = @Column(name = _OverriddenHslEntity.COLUMN_NAME_HUE, nullable = false)
)
@AttributeOverride(
        name = __MappedHsl.ATTRIBUTE_NAME_SATURATION,
        column = @Column(name = _OverriddenHslEntity.COLUMN_NAME_SATURATION, nullable = false)
)
@Access(AccessType.FIELD)
@Entity
@Table(name = _OverriddenHslEntity.TABLE_NAME)
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public class _OverriddenHslEntity extends __MappedHsl {

    static final String TABLE_NAME = "overridden_hsl_entity";

    static final String COLUMN_NAME_HUE = "hue_degrees";

    static final String COLUMN_NAME_SATURATION = "chroma";

    public Long getId() {
        return id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
