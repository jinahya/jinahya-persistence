package com.github.jinahya.persistence.more.location;

import com.github.jinahya.persistence.mapped.__Mapped;
import jakarta.annotation.Nonnull;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

@MappedSuperclass
@SuppressWarnings({
        "java:S101" // Class names should comply with a naming convention
})
public abstract class __MappedLocation2d extends __Mapped {

    // -----------------------------------------------------------------------------------------------------------------
    public static final String COLUMN_NAME_LATITUDE = "latitude";

    public static final String ATTRIBUTE_NAME_LATITUDE = "latitude";

    public static final String DECIMAL_MIN_LATITUDE = "-90.0";

    public static final String DECIMAL_MAX_LATITUDE = "+90.0";

    public static final double MIN_LATITUDE = -90.0d;

    public static final double MAX_LATITUDE = +90.0d;

    // -----------------------------------------------------------------------------------------------------------------
    public static final String COLUMN_NAME_LONGITUDE = "longitude";

    public static final String ATTRIBUTE_NAME_LONGITUDE = "longitude";

    public static final String DECIMAL_MIN_LONGITUDE = "-180.0";

    public static final String DECIMAL_MAX_LONGITUDE = "+180.0";

    public static final double MIN_LONGITUDE = -180.0d;

    public static final double MAX_LONGITUDE = +180.0d;

    // -----------------------------------------------------------------------------------------------------------------

    // -----------------------------------------------------------------------------------------------------------------
    protected __MappedLocation2d() {
        super();
    }

    protected __MappedLocation2d(final __MappedLocation2dBuilder<?, ?> builder) {
        super();
        latitude = builder.latitude();
        longitude = builder.longitude();
    }

    // ------------------------------------------------------------------------------------------------ java.lang.Object
    @Override
    public String toString() {
        return super.toString() + '{' +
               "latitude=" + latitude +
               ",longitude=" + longitude +
               '}';
    }

    @Override
    public final boolean equals(final Object obj) {
        if (!(obj instanceof __MappedLocation2d that)) {
            return false;
        }
//        return Objects.equals(latitude, that.latitude)
//               && Objects.equals(longitude, that.longitude);
        return (latitude != null && latitude.compareTo(that.latitude) == 0)
               && (longitude != null && longitude.compareTo(that.longitude) == 0);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(latitude, longitude);
    }

    // -----------------------------------------------------------------------------------------------------------------
    public double[] toArrayOfDoubles() {
        if (latitude == null) {
            throw new IllegalStateException("latitude is currently null");
        }
        if (longitude == null) {
            throw new IllegalStateException("longitude is currently null");
        }
        return DoubleBuffer.allocate(2).put(latitude.doubleValue()).put(longitude.doubleValue()).array();
    }

    public void fromArrayOfDoubles(@Nonnull final double[] array) {
        if (Objects.requireNonNull(array, "array is null").length < 2) {
            throw new IllegalArgumentException("array.length(" + array.length + ") is less than 2");
        }
        final var buffer = DoubleBuffer.wrap(array);
        setLatitudeFromDouble(buffer.get());
        setLongitudeFromDouble(buffer.get());
    }

    public byte[] toArrayOfBytes() {
        final var array = toArrayOfDoubles();
        final var buffer = ByteBuffer.allocate(Double.BYTES << 1);
        buffer.asDoubleBuffer().put(array);
        return buffer.array();
    }

    public void fromArrayOfDoubles(@Nonnull final byte[] array) {
        if (Objects.requireNonNull(array, "array is null").length < (Double.BYTES << 1)) {
            throw new IllegalArgumentException("array.length(" + array.length + ") is less than 16");
        }
        fromArrayOfDoubles(
                ByteBuffer.wrap(array).asDoubleBuffer().array()
        );
    }

    // -------------------------------------------------------------------------------------------------------- latitude
    @Nonnull
    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(@Nonnull final BigDecimal latitude) {
        this.latitude = latitude;
    }

    public <N extends Number> N getLatitudeAsMapped(final Function<? super BigDecimal, ? extends N> mapper) {
        return Optional.ofNullable(getLatitude())
                .map(v -> Objects.requireNonNull(mapper, "mapper is null").apply(v))
                .orElse(null);
    }

    public <N extends Number> void setLatitudeFromMapped(final N latitude,
                                                         final Function<? super N, ? extends BigDecimal> mapper) {
        setLatitude(
                Optional.ofNullable(latitude)
                        .map(v -> Objects.requireNonNull(mapper, "mapper is null").apply(v))
                        .orElse(null)
        );
    }

    @Transient
    public Double getLatitudeAsDouble() {
        return getLatitudeAsMapped(BigDecimal::doubleValue);
    }

    @Transient
    public void setLatitudeFromDouble(final Double latitude) {
        setLatitudeFromMapped(latitude, BigDecimal::valueOf);
    }

    @Transient
    public Float getLatitudeAsFloat() {
        return getLatitudeAsMapped(BigDecimal::floatValue);
    }

    @Transient
    public void setLatitudeFromFloat(final Float latitude) {
        setLatitudeFromMapped(latitude, BigDecimal::valueOf);
    }

    // ------------------------------------------------------------------------------------------------------- longitude
    @Nonnull
    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(@Nonnull final BigDecimal longitude) {
        this.longitude = longitude;
    }

    public <N extends Number> N getLongitudeAsMapped(final Function<? super BigDecimal, ? extends N> mapper) {
        return Optional.ofNullable(getLongitude())
                .map(v -> Objects.requireNonNull(mapper, "mapper is null").apply(v))
                .orElse(null);
    }

    public <N extends Number> void setLongitudeFromMapped(final N longitude,
                                                          final Function<? super N, ? extends BigDecimal> mapper) {
        setLongitude(
                Optional.ofNullable(longitude)
                        .map(v -> Objects.requireNonNull(mapper, "mapper is null").apply(v))
                        .orElse(null)
        );
    }

    @Transient
    public Double getLongitudeAsDouble() {
        return getLongitudeAsMapped(BigDecimal::doubleValue);
    }

    @Transient
    public void setLongitudeFromDouble(final Double longitude) {
        setLongitudeFromMapped(longitude, BigDecimal::valueOf);
    }

    @Transient
    public Float getLongitudeAsFloat() {
        return getLongitudeAsMapped(BigDecimal::floatValue);
    }

    @Transient
    public void setLongitudeFromFloat(final Float longitude) {
        setLongitudeFromMapped(longitude, BigDecimal::valueOf);
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Nonnull
    @DecimalMax(DECIMAL_MAX_LATITUDE)
    @DecimalMin(DECIMAL_MIN_LATITUDE)
    @NotNull
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_LATITUDE, nullable = false, insertable = true, updatable = true)
    private BigDecimal latitude;

    @Nonnull
    @DecimalMax(DECIMAL_MAX_LONGITUDE)
    @DecimalMin(DECIMAL_MIN_LONGITUDE)
    @NotNull
    @Basic(optional = false)
    @Column(name = COLUMN_NAME_LONGITUDE, nullable = false, insertable = true, updatable = true)
    private BigDecimal longitude;
}
