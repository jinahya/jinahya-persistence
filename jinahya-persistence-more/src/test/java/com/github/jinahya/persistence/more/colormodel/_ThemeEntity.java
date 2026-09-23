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
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.jspecify.annotations.Nullable;

/**
 * An entity carrying <em>two</em> colors, each overriding the three column names it inherits.
 * <p>
 * The ordinary case a single mapped superclass cannot serve: a theme has a foreground and a background.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Access(AccessType.FIELD)
@Entity
@Table(name = _ThemeEntity.TABLE_NAME)
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public class _ThemeEntity {

    static final String TABLE_NAME = "theme";

    static final String COLUMN_NAME_FOREGROUND_RED = "fg_red";

    static final String COLUMN_NAME_FOREGROUND_GREEN = "fg_green";

    static final String COLUMN_NAME_FOREGROUND_BLUE = "fg_blue";

    static final String COLUMN_NAME_BACKGROUND_RED = "bg_red";

    static final String COLUMN_NAME_BACKGROUND_GREEN = "bg_green";

    static final String COLUMN_NAME_BACKGROUND_BLUE = "bg_blue";

    public Long getId() {
        return id;
    }

    public @Nullable _RgbEmbeddable getForeground() {
        return foreground;
    }

    public void setForeground(final @Nullable _RgbEmbeddable foreground) {
        this.foreground = foreground;
    }

    public @Nullable _RgbEmbeddable getBackground() {
        return background;
    }

    public void setBackground(final @Nullable _RgbEmbeddable background) {
        this.background = background;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @AttributeOverride(name = __MappedRgb.ATTRIBUTE_NAME_RED, column = @Column(name = COLUMN_NAME_FOREGROUND_RED))
    @AttributeOverride(name = __MappedRgb.ATTRIBUTE_NAME_GREEN, column = @Column(name = COLUMN_NAME_FOREGROUND_GREEN))
    @AttributeOverride(name = __MappedRgb.ATTRIBUTE_NAME_BLUE, column = @Column(name = COLUMN_NAME_FOREGROUND_BLUE))
    @Embedded
    private @Nullable _RgbEmbeddable foreground;

    @AttributeOverride(name = __MappedRgb.ATTRIBUTE_NAME_RED, column = @Column(name = COLUMN_NAME_BACKGROUND_RED))
    @AttributeOverride(name = __MappedRgb.ATTRIBUTE_NAME_GREEN, column = @Column(name = COLUMN_NAME_BACKGROUND_GREEN))
    @AttributeOverride(name = __MappedRgb.ATTRIBUTE_NAME_BLUE, column = @Column(name = COLUMN_NAME_BACKGROUND_BLUE))
    @Embedded
    private @Nullable _RgbEmbeddable background;
}
