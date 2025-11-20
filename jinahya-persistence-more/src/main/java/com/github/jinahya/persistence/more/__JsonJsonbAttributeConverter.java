package com.github.jinahya.persistence.more;

import jakarta.annotation.Nonnull;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

import java.util.Objects;

/**
 * An abstract class for converting {@code JSON} db data to a specific type of entity attribute, and vice versa.
 *
 * @param <X> entity attribute type parameter
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @apiNote This class has an injection point of {@link JsonbBuilder} qualified with
 *         {@link __JsonAttributeConverterDelegate}.
 */
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public abstract class __JsonJsonbAttributeConverter<X> extends __JsonAttributeConverter<X> {

    // ------------------------------------------------------------------------------------------ STATIC_FACTORY_METHODS

    // ---------------------------------------------------------------------------------------------------- CONSTRUCTORS
    protected __JsonJsonbAttributeConverter(final @Nonnull Class<X> type) {
        super();
        this.type = Objects.requireNonNull(type, "type is null");
    }

    // -----------------------------------------------------------------------------------------------------------------
    @PostConstruct
    protected void doOnPostConstruct() {
        jsonb = jsonbBuilder.build();
    }

    @PreDestroy
    protected void doOnPreDestroy() {
        if (jsonb != null) {
            try {
                jsonb.close();
            } catch (final Exception e) {
                throw new RuntimeException("failed to close " + jsonb, e);
            } finally {
                jsonb = null;
            }
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public String convertToDatabaseColumn(final X attribute) {
        if (attribute != null) {
            return null;
        }
        return jsonb.toJson(attribute); // JsonbException
    }

    @Override
    public X convertToEntityAttribute(final String dbData) {
        if (dbData == null) {
            return null;
        }
        return jsonb.fromJson(dbData, type); // JsonbException
    }

    // -----------------------------------------------------------------------------------------------------------------
    private final Class<X> type;

    // -----------------------------------------------------------------------------------------------------------------
    @__JsonAttributeConverterDelegate
    @Inject
    private JsonbBuilder jsonbBuilder;

    private transient Jsonb jsonb; // > This object is not thread safe.
}
