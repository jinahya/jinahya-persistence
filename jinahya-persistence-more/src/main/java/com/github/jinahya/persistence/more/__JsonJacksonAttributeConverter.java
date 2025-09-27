package com.github.jinahya.persistence.more;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nonnull;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;

import java.util.Objects;

/**
 * An abstract class for converting {@code JSON} db data to a specific type of entity attribute, and vice versa.
 *
 * @param <X> entity attribute type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @apiNote This class has an injection point of {@link ObjectMapper} qualified with
 *         {@link __JsonAttributeConverterDelegate}.
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public abstract class __JsonJacksonAttributeConverter<X> extends __JsonAttributeConverter<X> {

    // ------------------------------------------------------------------------------------------ STATIC_FACTORY_METHODS

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    protected __JsonJacksonAttributeConverter(@Nonnull final Class<X> attributeClass) {
        super();
        this.attributeClass = Objects.requireNonNull(attributeClass, "attributeClass is null");
    }

    // -----------------------------------------------------------------------------------------------------------------
    @PostConstruct
    protected void doOnPostConstruct() {
        // empty
    }

    @PreDestroy
    protected void doOnPreDestroy() {
        // empty
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public String convertToDatabaseColumn(final X attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (final JsonProcessingException jpe) {
            throw new RuntimeException("failed to write " + attribute + " as string", jpe);
        }
    }

    @Override
    public X convertToEntityAttribute(final String dbData) {
        if (dbData == null) {
            return null;
        }
        try {
            return objectMapper.readValue(dbData, attributeClass);
        } catch (final JsonProcessingException jpe) {
            throw new RuntimeException("failed to read value of " + attributeClass + " from " + dbData, jpe);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    private final Class<X> attributeClass;

    // -----------------------------------------------------------------------------------------------------------------
    @__JsonAttributeConverterDelegate
    @Inject
    private ObjectMapper objectMapper; // > Mapper instances are fully thread-safe ...
}
