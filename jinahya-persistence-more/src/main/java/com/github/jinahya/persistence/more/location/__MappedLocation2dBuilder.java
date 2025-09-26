package com.github.jinahya.persistence.more.location;

import com.github.jinahya.persistence.mapped.__MappedBuilder;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings({
        "unchecked",
        "java:S101", // Class names should comply with a naming convention
        "java:S119"  // Type parameter names should comply with a naming convention
})
public abstract class __MappedLocation2dBuilder<
        SELF extends __MappedLocation2dBuilder<SELF, TARGET>,
        TARGET extends __MappedLocation2d
        >
        extends __MappedBuilder<SELF, TARGET> {

    // -----------------------------------------------------------------------------------------------------------------

    // -----------------------------------------------------------------------------------------------------------------
    protected __MappedLocation2dBuilder(final Class<TARGET> targetClass) {
        super(targetClass);
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object

    // -------------------------------------------------------------------------------------------------------- latitude
    public BigDecimal latitude() {
        return latitude;
    }

    public SELF latitude(final BigDecimal latitude) {
        this.latitude = latitude;
        return (SELF) this;
    }

    public <N extends Number> SELF latitude(final N latitude, final Function<? super N, ? extends BigDecimal> mapper) {
        return latitude(
                Optional.ofNullable(latitude)
                        .map(v -> Objects.requireNonNull(mapper, "mapper is null").apply(v))
                        .orElse(null)
        );
    }

    public SELF latitude(final Double latitude) {
        return latitude(latitude, BigDecimal::valueOf);
    }

    public SELF latitude(final Float latitude) {
        return latitude(latitude, BigDecimal::valueOf);
    }

    // ------------------------------------------------------------------------------------------------------- longitude
    public BigDecimal longitude() {
        return longitude;
    }

    public SELF longitude(final BigDecimal longitude) {
        this.longitude = longitude;
        return (SELF) this;
    }

    public <N extends Number> SELF longitude(final N longitude,
                                             final Function<? super N, ? extends BigDecimal> mapper) {
        return longitude(
                Optional.ofNullable(longitude)
                        .map(v -> Objects.requireNonNull(mapper, "mapper is null").apply(v))
                        .orElse(null)
        );
    }

    public SELF longitude(final Double longitude) {
        return longitude(longitude, BigDecimal::valueOf);
    }

    public SELF longitude(final Float longitude) {
        return longitude(longitude, BigDecimal::valueOf);
    }

    // -----------------------------------------------------------------------------------------------------------------
    private BigDecimal latitude;

    private BigDecimal longitude;
}
