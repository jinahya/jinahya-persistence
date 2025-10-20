package com.github.jinahya.persistence.more;

import jakarta.annotation.Nullable;
import org.junit.jupiter.api.Test;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings({
        "java:S3577" // Test classes should comply with a naming convention
})
class __CollectionStringAttributeConverter_Test {

    private static final __CollectionStringAttributeConverter<List<String>, String> converter =
            new __CollectionStringAttributeConverter.__OfStrings<>(",", ArrayList::new) { // @formatter:off
                @Override protected boolean filterDatabaseColumnElement(@Nullable final String element) {
                    return super.filterEntityAttributeElement(element) && Objects.nonNull(element);
                }
                @Override protected boolean filterEntityAttributeElement(@Nullable final String element) {
                    return super.filterEntityAttributeElement(element) && Objects.nonNull(element);
                } // @formatter:on
            };

    // -----------------------------------------------------------------------------------------------------------------
    @Test
    void __() {
        final var attribute = new ArrayList<String>() { // @formatter:off
            @Serial private static final long serialVersionUID = -8978674045998471226L;
            {
                add("a");
                add(null);
            } // @formatter:on
        };
        final var converted = converter.convertToDatabaseColumn(attribute);
        final var convertedBack = converter.convertToEntityAttribute(converted);
        assertThat(convertedBack)
                .hasSameElementsAs(attribute.stream().filter(Objects::nonNull).toList());
    }
}
